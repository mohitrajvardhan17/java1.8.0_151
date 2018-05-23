package sun.security.validator;

import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.UntrustedChecker;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.NetscapeCertTypeExtension;
import sun.security.x509.X509CertImpl;

public final class SimpleValidator
  extends Validator
{
  static final String OID_BASIC_CONSTRAINTS = "2.5.29.19";
  static final String OID_NETSCAPE_CERT_TYPE = "2.16.840.1.113730.1.1";
  static final String OID_KEY_USAGE = "2.5.29.15";
  static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
  static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
  static final ObjectIdentifier OBJID_NETSCAPE_CERT_TYPE = NetscapeCertTypeExtension.NetscapeCertType_Id;
  private static final String NSCT_SSL_CA = "ssl_ca";
  private static final String NSCT_CODE_SIGNING_CA = "object_signing_ca";
  private final Map<X500Principal, List<X509Certificate>> trustedX500Principals;
  private final Collection<X509Certificate> trustedCerts;
  
  SimpleValidator(String paramString, Collection<X509Certificate> paramCollection)
  {
    super("Simple", paramString);
    trustedCerts = paramCollection;
    trustedX500Principals = new HashMap();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
      X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
      Object localObject = (List)trustedX500Principals.get(localX500Principal);
      if (localObject == null)
      {
        localObject = new ArrayList(2);
        trustedX500Principals.put(localX500Principal, localObject);
      }
      ((List)localObject).add(localX509Certificate);
    }
  }
  
  public Collection<X509Certificate> getTrustedCertificates()
  {
    return trustedCerts;
  }
  
  X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject)
    throws CertificateException
  {
    if ((paramArrayOfX509Certificate == null) || (paramArrayOfX509Certificate.length == 0)) {
      throw new CertificateException("null or zero-length certificate chain");
    }
    paramArrayOfX509Certificate = buildTrustedChain(paramArrayOfX509Certificate);
    Date localDate = validationDate;
    if (localDate == null) {
      localDate = new Date();
    }
    UntrustedChecker localUntrustedChecker = new UntrustedChecker();
    X509Certificate localX509Certificate1 = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)];
    try
    {
      localUntrustedChecker.check(localX509Certificate1);
    }
    catch (CertPathValidatorException localCertPathValidatorException1)
    {
      throw new ValidatorException("Untrusted certificate: " + localX509Certificate1.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, localX509Certificate1, localCertPathValidatorException1);
    }
    TrustAnchor localTrustAnchor = new TrustAnchor(localX509Certificate1, null);
    AlgorithmChecker localAlgorithmChecker1 = new AlgorithmChecker(localTrustAnchor, variant);
    AlgorithmChecker localAlgorithmChecker2 = null;
    if (paramAlgorithmConstraints != null) {
      localAlgorithmChecker2 = new AlgorithmChecker(localTrustAnchor, paramAlgorithmConstraints, null, null, variant);
    }
    int i = paramArrayOfX509Certificate.length - 1;
    for (int j = paramArrayOfX509Certificate.length - 2; j >= 0; j--)
    {
      X509Certificate localX509Certificate2 = paramArrayOfX509Certificate[(j + 1)];
      X509Certificate localX509Certificate3 = paramArrayOfX509Certificate[j];
      try
      {
        localUntrustedChecker.check(localX509Certificate3, Collections.emptySet());
      }
      catch (CertPathValidatorException localCertPathValidatorException2)
      {
        throw new ValidatorException("Untrusted certificate: " + localX509Certificate3.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, localX509Certificate3, localCertPathValidatorException2);
      }
      try
      {
        localAlgorithmChecker1.check(localX509Certificate3, Collections.emptySet());
        if (localAlgorithmChecker2 != null) {
          localAlgorithmChecker2.check(localX509Certificate3, Collections.emptySet());
        }
      }
      catch (CertPathValidatorException localCertPathValidatorException3)
      {
        throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED, localX509Certificate3, localCertPathValidatorException3);
      }
      if ((!variant.equals("code signing")) && (!variant.equals("jce signing"))) {
        localX509Certificate3.checkValidity(localDate);
      }
      if (!localX509Certificate3.getIssuerX500Principal().equals(localX509Certificate2.getSubjectX500Principal())) {
        throw new ValidatorException(ValidatorException.T_NAME_CHAINING, localX509Certificate3);
      }
      try
      {
        localX509Certificate3.verify(localX509Certificate2.getPublicKey());
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR, localX509Certificate3, localGeneralSecurityException);
      }
      if (j != 0) {
        i = checkExtensions(localX509Certificate3, i);
      }
    }
    return paramArrayOfX509Certificate;
  }
  
  private int checkExtensions(X509Certificate paramX509Certificate, int paramInt)
    throws CertificateException
  {
    Set localSet = paramX509Certificate.getCriticalExtensionOIDs();
    if (localSet == null) {
      localSet = Collections.emptySet();
    }
    int i = checkBasicConstraints(paramX509Certificate, localSet, paramInt);
    checkKeyUsage(paramX509Certificate, localSet);
    checkNetscapeCertType(paramX509Certificate, localSet);
    if (!localSet.isEmpty()) {
      throw new ValidatorException("Certificate contains unknown critical extensions: " + localSet, ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
    }
    return i;
  }
  
  private void checkNetscapeCertType(X509Certificate paramX509Certificate, Set<String> paramSet)
    throws CertificateException
  {
    if (!variant.equals("generic")) {
      if ((variant.equals("tls client")) || (variant.equals("tls server")))
      {
        if (!getNetscapeCertTypeBit(paramX509Certificate, "ssl_ca")) {
          throw new ValidatorException("Invalid Netscape CertType extension for SSL CA certificate", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
        }
        paramSet.remove("2.16.840.1.113730.1.1");
      }
      else if ((variant.equals("code signing")) || (variant.equals("jce signing")))
      {
        if (!getNetscapeCertTypeBit(paramX509Certificate, "object_signing_ca")) {
          throw new ValidatorException("Invalid Netscape CertType extension for code signing CA certificate", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
        }
        paramSet.remove("2.16.840.1.113730.1.1");
      }
      else
      {
        throw new CertificateException("Unknown variant " + variant);
      }
    }
  }
  
  static boolean getNetscapeCertTypeBit(X509Certificate paramX509Certificate, String paramString)
  {
    try
    {
      Object localObject2;
      NetscapeCertTypeExtension localNetscapeCertTypeExtension;
      if ((paramX509Certificate instanceof X509CertImpl))
      {
        localObject1 = (X509CertImpl)paramX509Certificate;
        localObject2 = OBJID_NETSCAPE_CERT_TYPE;
        localNetscapeCertTypeExtension = (NetscapeCertTypeExtension)((X509CertImpl)localObject1).getExtension((ObjectIdentifier)localObject2);
        if (localNetscapeCertTypeExtension == null) {
          return true;
        }
      }
      else
      {
        localObject1 = paramX509Certificate.getExtensionValue("2.16.840.1.113730.1.1");
        if (localObject1 == null) {
          return true;
        }
        localObject2 = new DerInputStream((byte[])localObject1);
        byte[] arrayOfByte = ((DerInputStream)localObject2).getOctetString();
        arrayOfByte = new DerValue(arrayOfByte).getUnalignedBitString().toByteArray();
        localNetscapeCertTypeExtension = new NetscapeCertTypeExtension(arrayOfByte);
      }
      Object localObject1 = localNetscapeCertTypeExtension.get(paramString);
      return ((Boolean)localObject1).booleanValue();
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  private int checkBasicConstraints(X509Certificate paramX509Certificate, Set<String> paramSet, int paramInt)
    throws CertificateException
  {
    paramSet.remove("2.5.29.19");
    int i = paramX509Certificate.getBasicConstraints();
    if (i < 0) {
      throw new ValidatorException("End user tried to act as a CA", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
    }
    if (!X509CertImpl.isSelfIssued(paramX509Certificate))
    {
      if (paramInt <= 0) {
        throw new ValidatorException("Violated path length constraints", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
      }
      paramInt--;
    }
    if (paramInt > i) {
      paramInt = i;
    }
    return paramInt;
  }
  
  private void checkKeyUsage(X509Certificate paramX509Certificate, Set<String> paramSet)
    throws CertificateException
  {
    paramSet.remove("2.5.29.15");
    paramSet.remove("2.5.29.37");
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    if ((arrayOfBoolean != null) && ((arrayOfBoolean.length < 6) || (arrayOfBoolean[5] == 0))) {
      throw new ValidatorException("Wrong key usage: expected keyCertSign", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
    }
  }
  
  private X509Certificate[] buildTrustedChain(X509Certificate[] paramArrayOfX509Certificate)
    throws CertificateException
  {
    ArrayList localArrayList = new ArrayList(paramArrayOfX509Certificate.length);
    for (int i = 0; i < paramArrayOfX509Certificate.length; i++)
    {
      localObject1 = paramArrayOfX509Certificate[i];
      localObject2 = getTrustedCertificate((X509Certificate)localObject1);
      if (localObject2 != null)
      {
        localArrayList.add(localObject2);
        return (X509Certificate[])localArrayList.toArray(CHAIN0);
      }
      localArrayList.add(localObject1);
    }
    X509Certificate localX509Certificate1 = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)];
    Object localObject1 = localX509Certificate1.getSubjectX500Principal();
    Object localObject2 = localX509Certificate1.getIssuerX500Principal();
    List localList = (List)trustedX500Principals.get(localObject2);
    if (localList != null)
    {
      X509Certificate localX509Certificate2 = (X509Certificate)localList.iterator().next();
      localArrayList.add(localX509Certificate2);
      return (X509Certificate[])localArrayList.toArray(CHAIN0);
    }
    throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
  }
  
  private X509Certificate getTrustedCertificate(X509Certificate paramX509Certificate)
  {
    X500Principal localX500Principal1 = paramX509Certificate.getSubjectX500Principal();
    List localList = (List)trustedX500Principals.get(localX500Principal1);
    if (localList == null) {
      return null;
    }
    X500Principal localX500Principal2 = paramX509Certificate.getIssuerX500Principal();
    PublicKey localPublicKey = paramX509Certificate.getPublicKey();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
      if (localX509Certificate.equals(paramX509Certificate)) {
        return paramX509Certificate;
      }
      if ((localX509Certificate.getIssuerX500Principal().equals(localX500Principal2)) && (localX509Certificate.getPublicKey().equals(localPublicKey))) {
        return localX509Certificate;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\validator\SimpleValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */