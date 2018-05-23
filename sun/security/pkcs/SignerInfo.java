package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.CryptoPrimitive;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import sun.misc.HexDumpEncoder;
import sun.security.timestamp.TimestampToken;
import sun.security.util.ConstraintsParameters;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.X500Name;

public class SignerInfo
  implements DerEncoder
{
  private static final Set<CryptoPrimitive> DIGEST_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.MESSAGE_DIGEST));
  private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
  BigInteger version;
  X500Name issuerName;
  BigInteger certificateSerialNumber;
  AlgorithmId digestAlgorithmId;
  AlgorithmId digestEncryptionAlgorithmId;
  byte[] encryptedDigest;
  Timestamp timestamp;
  private boolean hasTimestamp = true;
  private static final Debug debug = Debug.getInstance("jar");
  PKCS9Attributes authenticatedAttributes;
  PKCS9Attributes unauthenticatedAttributes;
  
  public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte)
  {
    version = BigInteger.ONE;
    issuerName = paramX500Name;
    certificateSerialNumber = paramBigInteger;
    digestAlgorithmId = paramAlgorithmId1;
    digestEncryptionAlgorithmId = paramAlgorithmId2;
    encryptedDigest = paramArrayOfByte;
  }
  
  public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, PKCS9Attributes paramPKCS9Attributes1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte, PKCS9Attributes paramPKCS9Attributes2)
  {
    version = BigInteger.ONE;
    issuerName = paramX500Name;
    certificateSerialNumber = paramBigInteger;
    digestAlgorithmId = paramAlgorithmId1;
    authenticatedAttributes = paramPKCS9Attributes1;
    digestEncryptionAlgorithmId = paramAlgorithmId2;
    encryptedDigest = paramArrayOfByte;
    unauthenticatedAttributes = paramPKCS9Attributes2;
  }
  
  public SignerInfo(DerInputStream paramDerInputStream)
    throws IOException, ParsingException
  {
    this(paramDerInputStream, false);
  }
  
  public SignerInfo(DerInputStream paramDerInputStream, boolean paramBoolean)
    throws IOException, ParsingException
  {
    version = paramDerInputStream.getBigInteger();
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
    byte[] arrayOfByte = arrayOfDerValue[0].toByteArray();
    issuerName = new X500Name(new DerValue((byte)48, arrayOfByte));
    certificateSerialNumber = arrayOfDerValue[1].getBigInteger();
    DerValue localDerValue = paramDerInputStream.getDerValue();
    digestAlgorithmId = AlgorithmId.parse(localDerValue);
    if (paramBoolean) {
      paramDerInputStream.getSet(0);
    } else if ((byte)paramDerInputStream.peekByte() == -96) {
      authenticatedAttributes = new PKCS9Attributes(paramDerInputStream);
    }
    localDerValue = paramDerInputStream.getDerValue();
    digestEncryptionAlgorithmId = AlgorithmId.parse(localDerValue);
    encryptedDigest = paramDerInputStream.getOctetString();
    if (paramBoolean) {
      paramDerInputStream.getSet(0);
    } else if ((paramDerInputStream.available() != 0) && ((byte)paramDerInputStream.peekByte() == -95)) {
      unauthenticatedAttributes = new PKCS9Attributes(paramDerInputStream, true);
    }
    if (paramDerInputStream.available() != 0) {
      throw new ParsingException("extra data at the end");
    }
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    derEncode(paramDerOutputStream);
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(version);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    issuerName.encode(localDerOutputStream2);
    localDerOutputStream2.putInteger(certificateSerialNumber);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    digestAlgorithmId.encode(localDerOutputStream1);
    if (authenticatedAttributes != null) {
      authenticatedAttributes.encode((byte)-96, localDerOutputStream1);
    }
    digestEncryptionAlgorithmId.encode(localDerOutputStream1);
    localDerOutputStream1.putOctetString(encryptedDigest);
    if (unauthenticatedAttributes != null) {
      unauthenticatedAttributes.encode((byte)-95, localDerOutputStream1);
    }
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write((byte)48, localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream3.toByteArray());
  }
  
  public X509Certificate getCertificate(PKCS7 paramPKCS7)
    throws IOException
  {
    return paramPKCS7.getCertificate(certificateSerialNumber, issuerName);
  }
  
  public ArrayList<X509Certificate> getCertificateChain(PKCS7 paramPKCS7)
    throws IOException
  {
    X509Certificate localX509Certificate1 = paramPKCS7.getCertificate(certificateSerialNumber, issuerName);
    if (localX509Certificate1 == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(localX509Certificate1);
    X509Certificate[] arrayOfX509Certificate = paramPKCS7.getCertificates();
    if ((arrayOfX509Certificate == null) || (localX509Certificate1.getSubjectDN().equals(localX509Certificate1.getIssuerDN()))) {
      return localArrayList;
    }
    Principal localPrincipal = localX509Certificate1.getIssuerDN();
    int i = 0;
    for (;;)
    {
      int j = 0;
      for (int k = i; k < arrayOfX509Certificate.length; k++) {
        if (localPrincipal.equals(arrayOfX509Certificate[k].getSubjectDN()))
        {
          localArrayList.add(arrayOfX509Certificate[k]);
          if (arrayOfX509Certificate[k].getSubjectDN().equals(arrayOfX509Certificate[k].getIssuerDN()))
          {
            i = arrayOfX509Certificate.length;
          }
          else
          {
            localPrincipal = arrayOfX509Certificate[k].getIssuerDN();
            X509Certificate localX509Certificate2 = arrayOfX509Certificate[i];
            arrayOfX509Certificate[i] = arrayOfX509Certificate[k];
            arrayOfX509Certificate[k] = localX509Certificate2;
            i++;
          }
          j = 1;
          break;
        }
      }
      if (j == 0) {
        break;
      }
    }
    return localArrayList;
  }
  
  SignerInfo verify(PKCS7 paramPKCS7, byte[] paramArrayOfByte)
    throws NoSuchAlgorithmException, SignatureException
  {
    try
    {
      ContentInfo localContentInfo = paramPKCS7.getContentInfo();
      if (paramArrayOfByte == null) {
        paramArrayOfByte = localContentInfo.getContentBytes();
      }
      Timestamp localTimestamp = null;
      try
      {
        localTimestamp = getTimestamp();
      }
      catch (Exception localException) {}
      ConstraintsParameters localConstraintsParameters = new ConstraintsParameters(localTimestamp);
      String str = getDigestAlgorithmId().getName();
      byte[] arrayOfByte1;
      if (authenticatedAttributes == null)
      {
        arrayOfByte1 = paramArrayOfByte;
      }
      else
      {
        localObject1 = (ObjectIdentifier)authenticatedAttributes.getAttributeValue(PKCS9Attribute.CONTENT_TYPE_OID);
        if ((localObject1 == null) || (!((ObjectIdentifier)localObject1).equals(contentType))) {
          return null;
        }
        localObject2 = (byte[])authenticatedAttributes.getAttributeValue(PKCS9Attribute.MESSAGE_DIGEST_OID);
        if (localObject2 == null) {
          return null;
        }
        try
        {
          JAR_DISABLED_CHECK.permits(str, localConstraintsParameters);
        }
        catch (CertPathValidatorException localCertPathValidatorException1)
        {
          throw new SignatureException(localCertPathValidatorException1.getMessage(), localCertPathValidatorException1);
        }
        localObject3 = MessageDigest.getInstance(str);
        byte[] arrayOfByte2 = ((MessageDigest)localObject3).digest(paramArrayOfByte);
        if (localObject2.length != arrayOfByte2.length) {
          return null;
        }
        for (int i = 0; i < localObject2.length; i++) {
          if (localObject2[i] != arrayOfByte2[i]) {
            return null;
          }
        }
        arrayOfByte1 = authenticatedAttributes.getDerEncoding();
      }
      Object localObject1 = getDigestEncryptionAlgorithmId().getName();
      Object localObject2 = AlgorithmId.getEncAlgFromSigAlg((String)localObject1);
      if (localObject2 != null) {
        localObject1 = localObject2;
      }
      Object localObject3 = AlgorithmId.makeSigAlg(str, (String)localObject1);
      try
      {
        JAR_DISABLED_CHECK.permits((String)localObject3, localConstraintsParameters);
      }
      catch (CertPathValidatorException localCertPathValidatorException2)
      {
        throw new SignatureException(localCertPathValidatorException2.getMessage(), localCertPathValidatorException2);
      }
      X509Certificate localX509Certificate = getCertificate(paramPKCS7);
      if (localX509Certificate == null) {
        return null;
      }
      PublicKey localPublicKey = localX509Certificate.getPublicKey();
      if (!JAR_DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, localPublicKey)) {
        throw new SignatureException("Public key check failed. Disabled key used: " + KeyUtil.getKeySize(localPublicKey) + " bit " + localPublicKey.getAlgorithm());
      }
      if (localX509Certificate.hasUnsupportedCriticalExtension()) {
        throw new SignatureException("Certificate has unsupported critical extension(s)");
      }
      boolean[] arrayOfBoolean = localX509Certificate.getKeyUsage();
      if (arrayOfBoolean != null)
      {
        try
        {
          localObject4 = new KeyUsageExtension(arrayOfBoolean);
        }
        catch (IOException localIOException2)
        {
          throw new SignatureException("Failed to parse keyUsage extension");
        }
        boolean bool1 = ((KeyUsageExtension)localObject4).get("digital_signature").booleanValue();
        boolean bool2 = ((KeyUsageExtension)localObject4).get("non_repudiation").booleanValue();
        if ((!bool1) && (!bool2)) {
          throw new SignatureException("Key usage restricted: cannot be used for digital signatures");
        }
      }
      Object localObject4 = Signature.getInstance((String)localObject3);
      ((Signature)localObject4).initVerify(localPublicKey);
      ((Signature)localObject4).update(arrayOfByte1);
      if (((Signature)localObject4).verify(encryptedDigest)) {
        return this;
      }
    }
    catch (IOException localIOException1)
    {
      throw new SignatureException("IO error verifying signature:\n" + localIOException1.getMessage());
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new SignatureException("InvalidKey: " + localInvalidKeyException.getMessage());
    }
    return null;
  }
  
  SignerInfo verify(PKCS7 paramPKCS7)
    throws NoSuchAlgorithmException, SignatureException
  {
    return verify(paramPKCS7, null);
  }
  
  public BigInteger getVersion()
  {
    return version;
  }
  
  public X500Name getIssuerName()
  {
    return issuerName;
  }
  
  public BigInteger getCertificateSerialNumber()
  {
    return certificateSerialNumber;
  }
  
  public AlgorithmId getDigestAlgorithmId()
  {
    return digestAlgorithmId;
  }
  
  public PKCS9Attributes getAuthenticatedAttributes()
  {
    return authenticatedAttributes;
  }
  
  public AlgorithmId getDigestEncryptionAlgorithmId()
  {
    return digestEncryptionAlgorithmId;
  }
  
  public byte[] getEncryptedDigest()
  {
    return encryptedDigest;
  }
  
  public PKCS9Attributes getUnauthenticatedAttributes()
  {
    return unauthenticatedAttributes;
  }
  
  public PKCS7 getTsToken()
    throws IOException
  {
    if (unauthenticatedAttributes == null) {
      return null;
    }
    PKCS9Attribute localPKCS9Attribute = unauthenticatedAttributes.getAttribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID);
    if (localPKCS9Attribute == null) {
      return null;
    }
    return new PKCS7((byte[])localPKCS9Attribute.getValue());
  }
  
  public Timestamp getTimestamp()
    throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
  {
    if ((timestamp != null) || (!hasTimestamp)) {
      return timestamp;
    }
    PKCS7 localPKCS7 = getTsToken();
    if (localPKCS7 == null)
    {
      hasTimestamp = false;
      return null;
    }
    byte[] arrayOfByte = localPKCS7.getContentInfo().getData();
    SignerInfo[] arrayOfSignerInfo = localPKCS7.verify(arrayOfByte);
    ArrayList localArrayList = arrayOfSignerInfo[0].getCertificateChain(localPKCS7);
    CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
    CertPath localCertPath = localCertificateFactory.generateCertPath(localArrayList);
    TimestampToken localTimestampToken = new TimestampToken(arrayOfByte);
    verifyTimestamp(localTimestampToken);
    timestamp = new Timestamp(localTimestampToken.getDate(), localCertPath);
    return timestamp;
  }
  
  private void verifyTimestamp(TimestampToken paramTimestampToken)
    throws NoSuchAlgorithmException, SignatureException
  {
    String str = paramTimestampToken.getHashAlgorithm().getName();
    if (!JAR_DISABLED_CHECK.permits(DIGEST_PRIMITIVE_SET, str, null)) {
      throw new SignatureException("Timestamp token digest check failed. Disabled algorithm used: " + str);
    }
    MessageDigest localMessageDigest = MessageDigest.getInstance(str);
    if (!Arrays.equals(paramTimestampToken.getHashedMessage(), localMessageDigest.digest(encryptedDigest))) {
      throw new SignatureException("Signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate() + " is inapplicable");
    }
    if (debug != null)
    {
      debug.println();
      debug.println("Detected signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate());
      debug.println();
    }
  }
  
  public String toString()
  {
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    String str = "";
    str = str + "Signer Info for (issuer): " + issuerName + "\n";
    str = str + "\tversion: " + Debug.toHexString(version) + "\n";
    str = str + "\tcertificateSerialNumber: " + Debug.toHexString(certificateSerialNumber) + "\n";
    str = str + "\tdigestAlgorithmId: " + digestAlgorithmId + "\n";
    if (authenticatedAttributes != null) {
      str = str + "\tauthenticatedAttributes: " + authenticatedAttributes + "\n";
    }
    str = str + "\tdigestEncryptionAlgorithmId: " + digestEncryptionAlgorithmId + "\n";
    str = str + "\tencryptedDigest: \n" + localHexDumpEncoder.encodeBuffer(encryptedDigest) + "\n";
    if (unauthenticatedAttributes != null) {
      str = str + "\tunauthenticatedAttributes: " + unauthenticatedAttributes + "\n";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\SignerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */