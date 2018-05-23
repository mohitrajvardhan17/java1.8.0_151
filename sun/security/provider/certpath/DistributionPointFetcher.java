package sun.security.provider.certpath;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.DistributionPointName;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNames;
import sun.security.x509.IssuingDistributionPointExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.RDN;
import sun.security.x509.ReasonFlags;
import sun.security.x509.SerialNumber;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public class DistributionPointFetcher
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
  
  private DistributionPointFetcher() {}
  
  public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString1, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate, String paramString2)
    throws CertStoreException
  {
    return getCRLs(paramX509CRLSelector, paramBoolean, paramPublicKey, null, paramString1, paramList, paramArrayOfBoolean, paramSet, paramDate, paramString2);
  }
  
  public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate)
    throws CertStoreException
  {
    return getCRLs(paramX509CRLSelector, paramBoolean, paramPublicKey, null, paramString, paramList, paramArrayOfBoolean, paramSet, paramDate, "generic");
  }
  
  public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate, String paramString2)
    throws CertStoreException
  {
    X509Certificate localX509Certificate = paramX509CRLSelector.getCertificateChecking();
    if (localX509Certificate == null) {
      return Collections.emptySet();
    }
    try
    {
      X509CertImpl localX509CertImpl = X509CertImpl.toImpl(localX509Certificate);
      if (debug != null) {
        debug.println("DistributionPointFetcher.getCRLs: Checking CRLDPs for " + localX509CertImpl.getSubjectX500Principal());
      }
      CRLDistributionPointsExtension localCRLDistributionPointsExtension = localX509CertImpl.getCRLDistributionPointsExtension();
      if (localCRLDistributionPointsExtension == null)
      {
        if (debug != null) {
          debug.println("No CRLDP ext");
        }
        return Collections.emptySet();
      }
      List localList = localCRLDistributionPointsExtension.get("points");
      HashSet localHashSet = new HashSet();
      Iterator localIterator = localList.iterator();
      while ((localIterator.hasNext()) && (!Arrays.equals(paramArrayOfBoolean, ALL_REASONS)))
      {
        DistributionPoint localDistributionPoint = (DistributionPoint)localIterator.next();
        Collection localCollection = getCRLs(paramX509CRLSelector, localX509CertImpl, localDistributionPoint, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramX509Certificate, paramString1, paramList, paramSet, paramDate, paramString2);
        localHashSet.addAll(localCollection);
      }
      if (debug != null) {
        debug.println("Returning " + localHashSet.size() + " CRLs");
      }
      return localHashSet;
    }
    catch (CertificateException|IOException localCertificateException) {}
    return Collections.emptySet();
  }
  
  private static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, List<CertStore> paramList, Set<TrustAnchor> paramSet, Date paramDate, String paramString2)
    throws CertStoreException
  {
    GeneralNames localGeneralNames1 = paramDistributionPoint.getFullName();
    if (localGeneralNames1 == null)
    {
      localObject1 = paramDistributionPoint.getRelativeName();
      if (localObject1 == null) {
        return Collections.emptySet();
      }
      try
      {
        GeneralNames localGeneralNames2 = paramDistributionPoint.getCRLIssuer();
        if (localGeneralNames2 == null)
        {
          localGeneralNames1 = getFullNames((X500Name)paramX509CertImpl.getIssuerDN(), (RDN)localObject1);
        }
        else
        {
          if (localGeneralNames2.size() != 1) {
            return Collections.emptySet();
          }
          localGeneralNames1 = getFullNames((X500Name)localGeneralNames2.get(0).getName(), (RDN)localObject1);
        }
      }
      catch (IOException localIOException1)
      {
        return Collections.emptySet();
      }
    }
    Object localObject1 = new ArrayList();
    Object localObject2 = null;
    Object localObject3 = localGeneralNames1.iterator();
    Object localObject4;
    while (((Iterator)localObject3).hasNext()) {
      try
      {
        GeneralName localGeneralName = (GeneralName)((Iterator)localObject3).next();
        if (localGeneralName.getType() == 4)
        {
          localObject4 = (X500Name)localGeneralName.getName();
          ((Collection)localObject1).addAll(getCRLs((X500Name)localObject4, paramX509CertImpl.getIssuerX500Principal(), paramList));
        }
        else if (localGeneralName.getType() == 6)
        {
          localObject4 = (URIName)localGeneralName.getName();
          X509CRL localX509CRL = getCRL((URIName)localObject4);
          if (localX509CRL != null) {
            ((Collection)localObject1).add(localX509CRL);
          }
        }
      }
      catch (CertStoreException localCertStoreException)
      {
        localObject2 = localCertStoreException;
      }
    }
    if ((((Collection)localObject1).isEmpty()) && (localObject2 != null)) {
      throw ((Throwable)localObject2);
    }
    localObject3 = new ArrayList(2);
    Iterator localIterator = ((Collection)localObject1).iterator();
    while (localIterator.hasNext())
    {
      localObject4 = (X509CRL)localIterator.next();
      try
      {
        paramX509CRLSelector.setIssuerNames(null);
        if ((paramX509CRLSelector.match((CRL)localObject4)) && (verifyCRL(paramX509CertImpl, paramDistributionPoint, (X509CRL)localObject4, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramX509Certificate, paramString1, paramSet, paramList, paramDate, paramString2))) {
          ((Collection)localObject3).add(localObject4);
        }
      }
      catch (IOException|CRLException localIOException2)
      {
        if (debug != null)
        {
          debug.println("Exception verifying CRL: " + localIOException2.getMessage());
          localIOException2.printStackTrace();
        }
      }
    }
    return (Collection<X509CRL>)localObject3;
  }
  
  private static X509CRL getCRL(URIName paramURIName)
    throws CertStoreException
  {
    URI localURI = paramURIName.getURI();
    if (debug != null) {
      debug.println("Trying to fetch CRL from DP " + localURI);
    }
    CertStore localCertStore = null;
    try
    {
      localCertStore = URICertStore.getInstance(new URICertStore.URICertStoreParameters(localURI));
    }
    catch (InvalidAlgorithmParameterException|NoSuchAlgorithmException localInvalidAlgorithmParameterException)
    {
      if (debug != null) {
        debug.println("Can't create URICertStore: " + localInvalidAlgorithmParameterException.getMessage());
      }
      return null;
    }
    Collection localCollection = localCertStore.getCRLs(null);
    if (localCollection.isEmpty()) {
      return null;
    }
    return (X509CRL)localCollection.iterator().next();
  }
  
  private static Collection<X509CRL> getCRLs(X500Name paramX500Name, X500Principal paramX500Principal, List<CertStore> paramList)
    throws CertStoreException
  {
    if (debug != null) {
      debug.println("Trying to fetch CRL from DP " + paramX500Name);
    }
    X509CRLSelector localX509CRLSelector = new X509CRLSelector();
    localX509CRLSelector.addIssuer(paramX500Name.asX500Principal());
    localX509CRLSelector.addIssuer(paramX500Principal);
    ArrayList localArrayList = new ArrayList();
    PKIX.CertStoreTypeException localCertStoreTypeException = null;
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      CertStore localCertStore = (CertStore)localIterator1.next();
      try
      {
        Iterator localIterator2 = localCertStore.getCRLs(localX509CRLSelector).iterator();
        while (localIterator2.hasNext())
        {
          CRL localCRL = (CRL)localIterator2.next();
          localArrayList.add((X509CRL)localCRL);
        }
      }
      catch (CertStoreException localCertStoreException)
      {
        if (debug != null)
        {
          debug.println("Exception while retrieving CRLs: " + localCertStoreException);
          localCertStoreException.printStackTrace();
        }
        localCertStoreTypeException = new PKIX.CertStoreTypeException(localCertStore.getType(), localCertStoreException);
      }
    }
    if ((localArrayList.isEmpty()) && (localCertStoreTypeException != null)) {
      throw localCertStoreTypeException;
    }
    return localArrayList;
  }
  
  static boolean verifyCRL(X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, X509CRL paramX509CRL, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, Set<TrustAnchor> paramSet, List<CertStore> paramList, Date paramDate, String paramString2)
    throws CRLException, IOException
  {
    if (debug != null) {
      debug.println("DistributionPointFetcher.verifyCRL: checking revocation status for\n  SN: " + Debug.toHexString(paramX509CertImpl.getSerialNumber()) + "\n  Subject: " + paramX509CertImpl.getSubjectX500Principal() + "\n  Issuer: " + paramX509CertImpl.getIssuerX500Principal());
    }
    int i = 0;
    X509CRLImpl localX509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
    IssuingDistributionPointExtension localIssuingDistributionPointExtension = localX509CRLImpl.getIssuingDistributionPointExtension();
    X500Name localX500Name1 = (X500Name)paramX509CertImpl.getIssuerDN();
    X500Name localX500Name2 = (X500Name)localX509CRLImpl.getIssuerDN();
    GeneralNames localGeneralNames = paramDistributionPoint.getCRLIssuer();
    X500Name localX500Name3 = null;
    Object localObject3;
    if (localGeneralNames != null)
    {
      if ((localIssuingDistributionPointExtension == null) || (((Boolean)localIssuingDistributionPointExtension.get("indirect_crl")).equals(Boolean.FALSE))) {
        return false;
      }
      int j = 0;
      localObject2 = localGeneralNames.iterator();
      while ((j == 0) && (((Iterator)localObject2).hasNext()))
      {
        localObject3 = ((GeneralName)((Iterator)localObject2).next()).getName();
        if (localX500Name2.equals(localObject3) == true)
        {
          localX500Name3 = (X500Name)localObject3;
          j = 1;
        }
      }
      if (j == 0) {
        return false;
      }
      if (issues(paramX509CertImpl, localX509CRLImpl, paramString1)) {
        paramPublicKey = paramX509CertImpl.getPublicKey();
      } else {
        i = 1;
      }
    }
    else
    {
      if (!localX500Name2.equals(localX500Name1))
      {
        if (debug != null) {
          debug.println("crl issuer does not equal cert issuer.\ncrl issuer: " + localX500Name2 + "\ncert issuer: " + localX500Name1);
        }
        return false;
      }
      localObject1 = paramX509CertImpl.getAuthKeyId();
      localObject2 = localX509CRLImpl.getAuthKeyId();
      if ((localObject1 == null) || (localObject2 == null))
      {
        if (issues(paramX509CertImpl, localX509CRLImpl, paramString1)) {
          paramPublicKey = paramX509CertImpl.getPublicKey();
        }
      }
      else if (!((KeyIdentifier)localObject1).equals(localObject2)) {
        if (issues(paramX509CertImpl, localX509CRLImpl, paramString1)) {
          paramPublicKey = paramX509CertImpl.getPublicKey();
        } else {
          i = 1;
        }
      }
    }
    if ((i == 0) && (!paramBoolean)) {
      return false;
    }
    Object localObject6;
    Object localObject7;
    Object localObject8;
    Object localObject4;
    if (localIssuingDistributionPointExtension != null)
    {
      localObject1 = (DistributionPointName)localIssuingDistributionPointExtension.get("point");
      if (localObject1 != null)
      {
        localObject2 = ((DistributionPointName)localObject1).getFullName();
        if (localObject2 == null)
        {
          localObject3 = ((DistributionPointName)localObject1).getRelativeName();
          if (localObject3 == null)
          {
            if (debug != null) {
              debug.println("IDP must be relative or full DN");
            }
            return false;
          }
          if (debug != null) {
            debug.println("IDP relativeName:" + localObject3);
          }
          localObject2 = getFullNames(localX500Name2, (RDN)localObject3);
        }
        Object localObject5;
        if ((paramDistributionPoint.getFullName() != null) || (paramDistributionPoint.getRelativeName() != null))
        {
          localObject3 = paramDistributionPoint.getFullName();
          if (localObject3 == null)
          {
            RDN localRDN = paramDistributionPoint.getRelativeName();
            if (localRDN == null)
            {
              if (debug != null) {
                debug.println("DP must be relative or full DN");
              }
              return false;
            }
            if (debug != null) {
              debug.println("DP relativeName:" + localRDN);
            }
            if (i != 0)
            {
              if (localGeneralNames.size() != 1)
              {
                if (debug != null) {
                  debug.println("must only be one CRL issuer when relative name present");
                }
                return false;
              }
              localObject3 = getFullNames(localX500Name3, localRDN);
            }
            else
            {
              localObject3 = getFullNames(localX500Name1, localRDN);
            }
          }
          boolean bool2 = false;
          localObject5 = ((GeneralNames)localObject2).iterator();
          while ((!bool2) && (((Iterator)localObject5).hasNext()))
          {
            localObject6 = ((GeneralName)((Iterator)localObject5).next()).getName();
            if (debug != null) {
              debug.println("idpName: " + localObject6);
            }
            localObject7 = ((GeneralNames)localObject3).iterator();
            while ((!bool2) && (((Iterator)localObject7).hasNext()))
            {
              localObject8 = ((GeneralName)((Iterator)localObject7).next()).getName();
              if (debug != null) {
                debug.println("pointName: " + localObject8);
              }
              bool2 = localObject6.equals(localObject8);
            }
          }
          if (!bool2)
          {
            if (debug != null) {
              debug.println("IDP name does not match DP name");
            }
            return false;
          }
        }
        else
        {
          boolean bool1 = false;
          localObject4 = localGeneralNames.iterator();
          while ((!bool1) && (((Iterator)localObject4).hasNext()))
          {
            localObject5 = ((GeneralName)((Iterator)localObject4).next()).getName();
            localObject6 = ((GeneralNames)localObject2).iterator();
            while ((!bool1) && (((Iterator)localObject6).hasNext()))
            {
              localObject7 = ((GeneralName)((Iterator)localObject6).next()).getName();
              bool1 = localObject5.equals(localObject7);
            }
          }
          if (!bool1) {
            return false;
          }
        }
      }
      localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_user_certs");
      if ((((Boolean)localObject2).equals(Boolean.TRUE)) && (paramX509CertImpl.getBasicConstraints() != -1))
      {
        if (debug != null) {
          debug.println("cert must be a EE cert");
        }
        return false;
      }
      localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_ca_certs");
      if ((((Boolean)localObject2).equals(Boolean.TRUE)) && (paramX509CertImpl.getBasicConstraints() == -1))
      {
        if (debug != null) {
          debug.println("cert must be a CA cert");
        }
        return false;
      }
      localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_attribute_certs");
      if (((Boolean)localObject2).equals(Boolean.TRUE))
      {
        if (debug != null) {
          debug.println("cert must not be an AA cert");
        }
        return false;
      }
    }
    Object localObject1 = new boolean[9];
    Object localObject2 = null;
    if (localIssuingDistributionPointExtension != null) {
      localObject2 = (ReasonFlags)localIssuingDistributionPointExtension.get("reasons");
    }
    boolean[] arrayOfBoolean = paramDistributionPoint.getReasonFlags();
    if (localObject2 != null)
    {
      if (arrayOfBoolean != null)
      {
        localObject4 = ((ReasonFlags)localObject2).getFlags();
        for (m = 0; m < localObject1.length; m++) {
          localObject1[m] = ((m < localObject4.length) && (localObject4[m] != 0) && (m < arrayOfBoolean.length) && (arrayOfBoolean[m] != 0) ? 1 : 0);
        }
      }
      else
      {
        localObject1 = (boolean[])((ReasonFlags)localObject2).getFlags().clone();
      }
    }
    else if ((localIssuingDistributionPointExtension == null) || (localObject2 == null)) {
      if (arrayOfBoolean != null) {
        localObject1 = (boolean[])arrayOfBoolean.clone();
      } else {
        Arrays.fill((boolean[])localObject1, true);
      }
    }
    int k = 0;
    for (int m = 0; (m < localObject1.length) && (k == 0); m++) {
      if ((localObject1[m] != 0) && ((m >= paramArrayOfBoolean.length) || (paramArrayOfBoolean[m] == 0))) {
        k = 1;
      }
    }
    if (k == 0) {
      return false;
    }
    if (i != 0)
    {
      X509CertSelector localX509CertSelector = new X509CertSelector();
      localX509CertSelector.setSubject(localX500Name2.asX500Principal());
      localObject6 = new boolean[] { false, false, false, false, false, false, true };
      localX509CertSelector.setKeyUsage((boolean[])localObject6);
      localObject7 = localX509CRLImpl.getAuthKeyIdExtension();
      if (localObject7 != null)
      {
        localObject8 = ((AuthorityKeyIdentifierExtension)localObject7).getEncodedKeyIdentifier();
        if (localObject8 != null) {
          localX509CertSelector.setSubjectKeyIdentifier((byte[])localObject8);
        }
        localObject9 = (SerialNumber)((AuthorityKeyIdentifierExtension)localObject7).get("serial_number");
        if (localObject9 != null) {
          localX509CertSelector.setSerialNumber(((SerialNumber)localObject9).getNumber());
        }
      }
      localObject8 = new HashSet(paramSet);
      if (paramPublicKey != null)
      {
        if (paramX509Certificate != null)
        {
          localObject9 = new TrustAnchor(paramX509Certificate, null);
        }
        else
        {
          X500Principal localX500Principal = paramX509CertImpl.getIssuerX500Principal();
          localObject9 = new TrustAnchor(localX500Principal, paramPublicKey, null);
        }
        ((Set)localObject8).add(localObject9);
      }
      Object localObject9 = null;
      try
      {
        localObject9 = new PKIXBuilderParameters((Set)localObject8, localX509CertSelector);
      }
      catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
      {
        throw new CRLException(localInvalidAlgorithmParameterException);
      }
      ((PKIXBuilderParameters)localObject9).setCertStores(paramList);
      ((PKIXBuilderParameters)localObject9).setSigProvider(paramString1);
      ((PKIXBuilderParameters)localObject9).setDate(paramDate);
      try
      {
        CertPathBuilder localCertPathBuilder = CertPathBuilder.getInstance("PKIX");
        PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)localCertPathBuilder.build((CertPathParameters)localObject9);
        paramPublicKey = localPKIXCertPathBuilderResult.getPublicKey();
      }
      catch (GeneralSecurityException localGeneralSecurityException2)
      {
        throw new CRLException(localGeneralSecurityException2);
      }
    }
    try
    {
      AlgorithmChecker.check(paramPublicKey, paramX509CRL, paramString2);
    }
    catch (CertPathValidatorException localCertPathValidatorException)
    {
      if (debug != null) {
        debug.println("CRL signature algorithm check failed: " + localCertPathValidatorException);
      }
      return false;
    }
    try
    {
      paramX509CRL.verify(paramPublicKey, paramString1);
    }
    catch (GeneralSecurityException localGeneralSecurityException1)
    {
      if (debug != null) {
        debug.println("CRL signature failed to verify");
      }
      return false;
    }
    Set localSet = paramX509CRL.getCriticalExtensionOIDs();
    if (localSet != null)
    {
      localSet.remove(PKIXExtensions.IssuingDistributionPoint_Id.toString());
      if (!localSet.isEmpty())
      {
        if (debug != null)
        {
          debug.println("Unrecognized critical extension(s) in CRL: " + localSet);
          localObject6 = localSet.iterator();
          while (((Iterator)localObject6).hasNext())
          {
            localObject7 = (String)((Iterator)localObject6).next();
            debug.println((String)localObject7);
          }
        }
        return false;
      }
    }
    for (int n = 0; n < paramArrayOfBoolean.length; n++) {
      paramArrayOfBoolean[n] = ((paramArrayOfBoolean[n] != 0) || ((n < localObject1.length) && (localObject1[n] != 0)) ? 1 : false);
    }
    return true;
  }
  
  private static GeneralNames getFullNames(X500Name paramX500Name, RDN paramRDN)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList(paramX500Name.rdns());
    localArrayList.add(paramRDN);
    X500Name localX500Name = new X500Name((RDN[])localArrayList.toArray(new RDN[0]));
    GeneralNames localGeneralNames = new GeneralNames();
    localGeneralNames.add(new GeneralName(localX500Name));
    return localGeneralNames;
  }
  
  private static boolean issues(X509CertImpl paramX509CertImpl, X509CRLImpl paramX509CRLImpl, String paramString)
    throws IOException
  {
    boolean bool = false;
    AdaptableX509CertSelector localAdaptableX509CertSelector = new AdaptableX509CertSelector();
    boolean[] arrayOfBoolean = paramX509CertImpl.getKeyUsage();
    if (arrayOfBoolean != null)
    {
      arrayOfBoolean[6] = true;
      localAdaptableX509CertSelector.setKeyUsage(arrayOfBoolean);
    }
    X500Principal localX500Principal = paramX509CRLImpl.getIssuerX500Principal();
    localAdaptableX509CertSelector.setSubject(localX500Principal);
    AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = paramX509CRLImpl.getAuthKeyIdExtension();
    localAdaptableX509CertSelector.setSkiAndSerialNumber(localAuthorityKeyIdentifierExtension);
    bool = localAdaptableX509CertSelector.match(paramX509CertImpl);
    if ((bool) && ((localAuthorityKeyIdentifierExtension == null) || (paramX509CertImpl.getAuthorityKeyIdentifierExtension() == null))) {
      try
      {
        paramX509CRLImpl.verify(paramX509CertImpl.getPublicKey(), paramString);
        bool = true;
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        bool = false;
      }
    }
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\DistributionPointFetcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */