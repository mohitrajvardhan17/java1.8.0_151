package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLReason;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.Extension;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXRevocationChecker.Option;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CertImpl;

class RevocationChecker
  extends PKIXRevocationChecker
{
  private static final Debug debug = Debug.getInstance("certpath");
  private TrustAnchor anchor;
  private PKIX.ValidatorParams params;
  private boolean onlyEE;
  private boolean softFail;
  private boolean crlDP;
  private URI responderURI;
  private X509Certificate responderCert;
  private List<CertStore> certStores;
  private Map<X509Certificate, byte[]> ocspResponses;
  private List<Extension> ocspExtensions;
  private final boolean legacy;
  private LinkedList<CertPathValidatorException> softFailExceptions = new LinkedList();
  private OCSPResponse.IssuerInfo issuerInfo;
  private PublicKey prevPubKey;
  private boolean crlSignFlag;
  private int certIndex;
  private Mode mode = Mode.PREFER_OCSP;
  private static final long MAX_CLOCK_SKEW = 900000L;
  private static final String HEX_DIGITS = "0123456789ABCDEFabcdef";
  private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
  private static final boolean[] CRL_SIGN_USAGE = { false, false, false, false, false, false, true };
  
  RevocationChecker()
  {
    legacy = false;
  }
  
  RevocationChecker(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams)
    throws CertPathValidatorException
  {
    legacy = true;
    init(paramTrustAnchor, paramValidatorParams);
  }
  
  void init(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams)
    throws CertPathValidatorException
  {
    RevocationProperties localRevocationProperties = getRevocationProperties();
    URI localURI = getOcspResponder();
    responderURI = (localURI == null ? toURI(ocspUrl) : localURI);
    X509Certificate localX509Certificate = getOcspResponderCert();
    responderCert = (localX509Certificate == null ? getResponderCert(localRevocationProperties, paramValidatorParams.trustAnchors(), paramValidatorParams.certStores()) : localX509Certificate);
    Set localSet = getOptions();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      PKIXRevocationChecker.Option localOption = (PKIXRevocationChecker.Option)localIterator.next();
      switch (localOption)
      {
      case ONLY_END_ENTITY: 
      case PREFER_CRLS: 
      case SOFT_FAIL: 
      case NO_FALLBACK: 
        break;
      default: 
        throw new CertPathValidatorException("Unrecognized revocation parameter option: " + localOption);
      }
    }
    softFail = localSet.contains(PKIXRevocationChecker.Option.SOFT_FAIL);
    if (legacy)
    {
      mode = (ocspEnabled ? Mode.PREFER_OCSP : Mode.ONLY_CRLS);
      onlyEE = onlyEE;
    }
    else
    {
      if (localSet.contains(PKIXRevocationChecker.Option.NO_FALLBACK))
      {
        if (localSet.contains(PKIXRevocationChecker.Option.PREFER_CRLS)) {
          mode = Mode.ONLY_CRLS;
        } else {
          mode = Mode.ONLY_OCSP;
        }
      }
      else if (localSet.contains(PKIXRevocationChecker.Option.PREFER_CRLS)) {
        mode = Mode.PREFER_CRLS;
      }
      onlyEE = localSet.contains(PKIXRevocationChecker.Option.ONLY_END_ENTITY);
    }
    if (legacy) {
      crlDP = crlDPEnabled;
    } else {
      crlDP = true;
    }
    ocspResponses = getOcspResponses();
    ocspExtensions = getOcspExtensions();
    anchor = paramTrustAnchor;
    params = paramValidatorParams;
    certStores = new ArrayList(paramValidatorParams.certStores());
    try
    {
      certStores.add(CertStore.getInstance("Collection", new CollectionCertStoreParameters(paramValidatorParams.certificates())));
    }
    catch (InvalidAlgorithmParameterException|NoSuchAlgorithmException localInvalidAlgorithmParameterException)
    {
      if (debug != null) {
        debug.println("RevocationChecker: error creating Collection CertStore: " + localInvalidAlgorithmParameterException);
      }
    }
  }
  
  private static URI toURI(String paramString)
    throws CertPathValidatorException
  {
    try
    {
      if (paramString != null) {
        return new URI(paramString);
      }
      return null;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new CertPathValidatorException("cannot parse ocsp.responderURL property", localURISyntaxException);
    }
  }
  
  private static RevocationProperties getRevocationProperties()
  {
    (RevocationProperties)AccessController.doPrivileged(new PrivilegedAction()
    {
      public RevocationChecker.RevocationProperties run()
      {
        RevocationChecker.RevocationProperties localRevocationProperties = new RevocationChecker.RevocationProperties(null);
        String str1 = Security.getProperty("com.sun.security.onlyCheckRevocationOfEECert");
        onlyEE = ((str1 != null) && (str1.equalsIgnoreCase("true")));
        String str2 = Security.getProperty("ocsp.enable");
        ocspEnabled = ((str2 != null) && (str2.equalsIgnoreCase("true")));
        ocspUrl = Security.getProperty("ocsp.responderURL");
        ocspSubject = Security.getProperty("ocsp.responderCertSubjectName");
        ocspIssuer = Security.getProperty("ocsp.responderCertIssuerName");
        ocspSerial = Security.getProperty("ocsp.responderCertSerialNumber");
        crlDPEnabled = Boolean.getBoolean("com.sun.security.enableCRLDP");
        return localRevocationProperties;
      }
    });
  }
  
  private static X509Certificate getResponderCert(RevocationProperties paramRevocationProperties, Set<TrustAnchor> paramSet, List<CertStore> paramList)
    throws CertPathValidatorException
  {
    if (ocspSubject != null) {
      return getResponderCert(ocspSubject, paramSet, paramList);
    }
    if ((ocspIssuer != null) && (ocspSerial != null)) {
      return getResponderCert(ocspIssuer, ocspSerial, paramSet, paramList);
    }
    if ((ocspIssuer != null) || (ocspSerial != null)) {
      throw new CertPathValidatorException("Must specify both ocsp.responderCertIssuerName and ocsp.responderCertSerialNumber properties");
    }
    return null;
  }
  
  private static X509Certificate getResponderCert(String paramString, Set<TrustAnchor> paramSet, List<CertStore> paramList)
    throws CertPathValidatorException
  {
    X509CertSelector localX509CertSelector = new X509CertSelector();
    try
    {
      localX509CertSelector.setSubject(new X500Principal(paramString));
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new CertPathValidatorException("cannot parse ocsp.responderCertSubjectName property", localIllegalArgumentException);
    }
    return getResponderCert(localX509CertSelector, paramSet, paramList);
  }
  
  private static X509Certificate getResponderCert(String paramString1, String paramString2, Set<TrustAnchor> paramSet, List<CertStore> paramList)
    throws CertPathValidatorException
  {
    X509CertSelector localX509CertSelector = new X509CertSelector();
    try
    {
      localX509CertSelector.setIssuer(new X500Principal(paramString1));
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new CertPathValidatorException("cannot parse ocsp.responderCertIssuerName property", localIllegalArgumentException);
    }
    try
    {
      localX509CertSelector.setSerialNumber(new BigInteger(stripOutSeparators(paramString2), 16));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new CertPathValidatorException("cannot parse ocsp.responderCertSerialNumber property", localNumberFormatException);
    }
    return getResponderCert(localX509CertSelector, paramSet, paramList);
  }
  
  private static X509Certificate getResponderCert(X509CertSelector paramX509CertSelector, Set<TrustAnchor> paramSet, List<CertStore> paramList)
    throws CertPathValidatorException
  {
    Iterator localIterator = paramSet.iterator();
    Object localObject1;
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (TrustAnchor)localIterator.next();
      localObject2 = ((TrustAnchor)localObject1).getTrustedCert();
      if (localObject2 != null) {
        if (paramX509CertSelector.match((Certificate)localObject2)) {
          return (X509Certificate)localObject2;
        }
      }
    }
    localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (CertStore)localIterator.next();
      try
      {
        localObject2 = ((CertStore)localObject1).getCertificates(paramX509CertSelector);
        if (!((Collection)localObject2).isEmpty()) {
          return (X509Certificate)((Collection)localObject2).iterator().next();
        }
      }
      catch (CertStoreException localCertStoreException)
      {
        if (debug != null) {
          debug.println("CertStore exception:" + localCertStoreException);
        }
      }
    }
    throw new CertPathValidatorException("Cannot find the responder's certificate (set using the OCSP security properties).");
  }
  
  public void init(boolean paramBoolean)
    throws CertPathValidatorException
  {
    if (paramBoolean) {
      throw new CertPathValidatorException("forward checking not supported");
    }
    if (anchor != null)
    {
      issuerInfo = new OCSPResponse.IssuerInfo(anchor);
      prevPubKey = issuerInfo.getPublicKey();
    }
    crlSignFlag = true;
    if ((params != null) && (params.certPath() != null)) {
      certIndex = (params.certPath().getCertificates().size() - 1);
    } else {
      certIndex = -1;
    }
    softFailExceptions.clear();
  }
  
  public boolean isForwardCheckingSupported()
  {
    return false;
  }
  
  public Set<String> getSupportedExtensions()
  {
    return null;
  }
  
  public List<CertPathValidatorException> getSoftFailExceptions()
  {
    return Collections.unmodifiableList(softFailExceptions);
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection)
    throws CertPathValidatorException
  {
    check((X509Certificate)paramCertificate, paramCollection, prevPubKey, crlSignFlag);
  }
  
  private void check(X509Certificate paramX509Certificate, Collection<String> paramCollection, PublicKey paramPublicKey, boolean paramBoolean)
    throws CertPathValidatorException
  {
    if (debug != null) {
      debug.println("RevocationChecker.check: checking cert\n  SN: " + Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Subject: " + paramX509Certificate.getSubjectX500Principal() + "\n  Issuer: " + paramX509Certificate.getIssuerX500Principal());
    }
    try
    {
      if ((onlyEE) && (paramX509Certificate.getBasicConstraints() != -1))
      {
        if (debug != null) {
          debug.println("Skipping revocation check; cert is not an end entity cert");
        }
        return;
      }
      switch (mode)
      {
      case PREFER_OCSP: 
      case ONLY_OCSP: 
        checkOCSP(paramX509Certificate, paramCollection);
        break;
      case PREFER_CRLS: 
      case ONLY_CRLS: 
        checkCRLs(paramX509Certificate, paramCollection, null, paramPublicKey, paramBoolean);
      }
    }
    catch (CertPathValidatorException localCertPathValidatorException1)
    {
      if (localCertPathValidatorException1.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
        throw localCertPathValidatorException1;
      }
      boolean bool = isSoftFailException(localCertPathValidatorException1);
      if (bool)
      {
        if ((mode != Mode.ONLY_OCSP) && (mode != Mode.ONLY_CRLS)) {}
      }
      else if ((mode == Mode.ONLY_OCSP) || (mode == Mode.ONLY_CRLS)) {
        throw localCertPathValidatorException1;
      }
      CertPathValidatorException localCertPathValidatorException2 = localCertPathValidatorException1;
      if (debug != null)
      {
        debug.println("RevocationChecker.check() " + localCertPathValidatorException1.getMessage());
        debug.println("RevocationChecker.check() preparing to failover");
      }
      try
      {
        switch (mode)
        {
        case PREFER_OCSP: 
          checkCRLs(paramX509Certificate, paramCollection, null, paramPublicKey, paramBoolean);
          break;
        case PREFER_CRLS: 
          checkOCSP(paramX509Certificate, paramCollection);
        }
      }
      catch (CertPathValidatorException localCertPathValidatorException3)
      {
        if (debug != null)
        {
          debug.println("RevocationChecker.check() failover failed");
          debug.println("RevocationChecker.check() " + localCertPathValidatorException3.getMessage());
        }
        if (localCertPathValidatorException3.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
          throw localCertPathValidatorException3;
        }
        if (!isSoftFailException(localCertPathValidatorException3))
        {
          localCertPathValidatorException2.addSuppressed(localCertPathValidatorException3);
          throw localCertPathValidatorException2;
        }
        if (!bool) {
          throw localCertPathValidatorException2;
        }
      }
    }
    finally
    {
      updateState(paramX509Certificate);
    }
  }
  
  private boolean isSoftFailException(CertPathValidatorException paramCertPathValidatorException)
  {
    if ((softFail) && (paramCertPathValidatorException.getReason() == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS))
    {
      CertPathValidatorException localCertPathValidatorException = new CertPathValidatorException(paramCertPathValidatorException.getMessage(), paramCertPathValidatorException.getCause(), params.certPath(), certIndex, paramCertPathValidatorException.getReason());
      softFailExceptions.addFirst(localCertPathValidatorException);
      return true;
    }
    return false;
  }
  
  private void updateState(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    issuerInfo = new OCSPResponse.IssuerInfo(anchor, paramX509Certificate);
    PublicKey localPublicKey = paramX509Certificate.getPublicKey();
    if (PKIX.isDSAPublicKeyWithoutParams(localPublicKey)) {
      localPublicKey = BasicChecker.makeInheritedParamsKey(localPublicKey, prevPubKey);
    }
    prevPubKey = localPublicKey;
    crlSignFlag = certCanSignCrl(paramX509Certificate);
    if (certIndex > 0) {
      certIndex -= 1;
    }
  }
  
  private void checkCRLs(X509Certificate paramX509Certificate, Collection<String> paramCollection, Set<X509Certificate> paramSet, PublicKey paramPublicKey, boolean paramBoolean)
    throws CertPathValidatorException
  {
    checkCRLs(paramX509Certificate, paramPublicKey, null, paramBoolean, true, paramSet, params.trustAnchors());
  }
  
  static boolean isCausedByNetworkIssue(String paramString, CertStoreException paramCertStoreException)
  {
    Throwable localThrowable = paramCertStoreException.getCause();
    boolean bool;
    switch (paramString)
    {
    case "LDAP": 
      if (localThrowable != null)
      {
        String str2 = localThrowable.getClass().getName();
        bool = (str2.equals("javax.naming.ServiceUnavailableException")) || (str2.equals("javax.naming.CommunicationException"));
      }
      else
      {
        bool = false;
      }
      break;
    case "SSLServer": 
      bool = (localThrowable != null) && ((localThrowable instanceof IOException));
      break;
    case "URI": 
      bool = (localThrowable != null) && ((localThrowable instanceof IOException));
      break;
    default: 
      return false;
    }
    return bool;
  }
  
  private void checkCRLs(X509Certificate paramX509Certificate1, PublicKey paramPublicKey, X509Certificate paramX509Certificate2, boolean paramBoolean1, boolean paramBoolean2, Set<X509Certificate> paramSet, Set<TrustAnchor> paramSet1)
    throws CertPathValidatorException
  {
    if (debug != null) {
      debug.println("RevocationChecker.checkCRLs() ---checking revocation status ...");
    }
    if ((paramSet != null) && (paramSet.contains(paramX509Certificate1)))
    {
      if (debug != null) {
        debug.println("RevocationChecker.checkCRLs() circular dependency");
      }
      throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    }
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    X509CRLSelector localX509CRLSelector = new X509CRLSelector();
    localX509CRLSelector.setCertificateChecking(paramX509Certificate1);
    CertPathHelper.setDateAndTime(localX509CRLSelector, params.date(), 900000L);
    CertPathValidatorException localCertPathValidatorException1 = null;
    Object localObject = certStores.iterator();
    while (((Iterator)localObject).hasNext())
    {
      CertStore localCertStore = (CertStore)((Iterator)localObject).next();
      try
      {
        Iterator localIterator = localCertStore.getCRLs(localX509CRLSelector).iterator();
        while (localIterator.hasNext())
        {
          CRL localCRL = (CRL)localIterator.next();
          localHashSet1.add((X509CRL)localCRL);
        }
      }
      catch (CertStoreException localCertStoreException2)
      {
        if (debug != null) {
          debug.println("RevocationChecker.checkCRLs() CertStoreException: " + localCertStoreException2.getMessage());
        }
        if ((localCertPathValidatorException1 == null) && (isCausedByNetworkIssue(localCertStore.getType(), localCertStoreException2))) {
          localCertPathValidatorException1 = new CertPathValidatorException("Unable to determine revocation status due to network error", localCertStoreException2, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
        }
      }
    }
    if (debug != null) {
      debug.println("RevocationChecker.checkCRLs() possible crls.size() = " + localHashSet1.size());
    }
    localObject = new boolean[9];
    if (!localHashSet1.isEmpty()) {
      localHashSet2.addAll(verifyPossibleCRLs(localHashSet1, paramX509Certificate1, paramPublicKey, paramBoolean1, (boolean[])localObject, paramSet1));
    }
    if (debug != null) {
      debug.println("RevocationChecker.checkCRLs() approved crls.size() = " + localHashSet2.size());
    }
    if ((!localHashSet2.isEmpty()) && (Arrays.equals((boolean[])localObject, ALL_REASONS)))
    {
      checkApprovedCRLs(paramX509Certificate1, localHashSet2);
    }
    else
    {
      try
      {
        if (crlDP) {
          localHashSet2.addAll(DistributionPointFetcher.getCRLs(localX509CRLSelector, paramBoolean1, paramPublicKey, paramX509Certificate2, params.sigProvider(), certStores, (boolean[])localObject, paramSet1, null, params.variant()));
        }
      }
      catch (CertStoreException localCertStoreException1)
      {
        if ((localCertStoreException1 instanceof PKIX.CertStoreTypeException))
        {
          PKIX.CertStoreTypeException localCertStoreTypeException = (PKIX.CertStoreTypeException)localCertStoreException1;
          if (isCausedByNetworkIssue(localCertStoreTypeException.getType(), localCertStoreException1)) {
            throw new CertPathValidatorException("Unable to determine revocation status due to network error", localCertStoreException1, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
          }
        }
        throw new CertPathValidatorException(localCertStoreException1);
      }
      if ((!localHashSet2.isEmpty()) && (Arrays.equals((boolean[])localObject, ALL_REASONS)))
      {
        checkApprovedCRLs(paramX509Certificate1, localHashSet2);
      }
      else
      {
        if (paramBoolean2) {
          try
          {
            verifyWithSeparateSigningKey(paramX509Certificate1, paramPublicKey, paramBoolean1, paramSet);
            return;
          }
          catch (CertPathValidatorException localCertPathValidatorException2)
          {
            if (localCertPathValidatorException1 != null) {
              throw localCertPathValidatorException1;
            }
            throw localCertPathValidatorException2;
          }
        }
        if (localCertPathValidatorException1 != null) {
          throw localCertPathValidatorException1;
        }
        throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      }
    }
  }
  
  private void checkApprovedCRLs(X509Certificate paramX509Certificate, Set<X509CRL> paramSet)
    throws CertPathValidatorException
  {
    if (debug != null)
    {
      localObject = paramX509Certificate.getSerialNumber();
      debug.println("RevocationChecker.checkApprovedCRLs() starting the final sweep...");
      debug.println("RevocationChecker.checkApprovedCRLs() cert SN: " + ((BigInteger)localObject).toString());
    }
    Object localObject = CRLReason.UNSPECIFIED;
    X509CRLEntryImpl localX509CRLEntryImpl = null;
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      X509CRL localX509CRL = (X509CRL)localIterator.next();
      X509CRLEntry localX509CRLEntry = localX509CRL.getRevokedCertificate(paramX509Certificate);
      if (localX509CRLEntry != null)
      {
        try
        {
          localX509CRLEntryImpl = X509CRLEntryImpl.toImpl(localX509CRLEntry);
        }
        catch (CRLException localCRLException)
        {
          throw new CertPathValidatorException(localCRLException);
        }
        if (debug != null) {
          debug.println("RevocationChecker.checkApprovedCRLs() CRL entry: " + localX509CRLEntryImpl.toString());
        }
        Set localSet = localX509CRLEntryImpl.getCriticalExtensionOIDs();
        if ((localSet != null) && (!localSet.isEmpty()))
        {
          localSet.remove(PKIXExtensions.ReasonCode_Id.toString());
          localSet.remove(PKIXExtensions.CertificateIssuer_Id.toString());
          if (!localSet.isEmpty()) {
            throw new CertPathValidatorException("Unrecognized critical extension(s) in revoked CRL entry");
          }
        }
        localObject = localX509CRLEntryImpl.getRevocationReason();
        if (localObject == null) {
          localObject = CRLReason.UNSPECIFIED;
        }
        Date localDate = localX509CRLEntryImpl.getRevocationDate();
        if (localDate.before(params.date()))
        {
          CertificateRevokedException localCertificateRevokedException = new CertificateRevokedException(localDate, (CRLReason)localObject, localX509CRL.getIssuerX500Principal(), localX509CRLEntryImpl.getExtensions());
          throw new CertPathValidatorException(localCertificateRevokedException.getMessage(), localCertificateRevokedException, null, -1, CertPathValidatorException.BasicReason.REVOKED);
        }
      }
    }
  }
  
  private void checkOCSP(X509Certificate paramX509Certificate, Collection<String> paramCollection)
    throws CertPathValidatorException
  {
    X509CertImpl localX509CertImpl = null;
    try
    {
      localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    }
    catch (CertificateException localCertificateException)
    {
      throw new CertPathValidatorException(localCertificateException);
    }
    OCSPResponse localOCSPResponse = null;
    CertId localCertId = null;
    Object localObject2;
    Object localObject3;
    try
    {
      localCertId = new CertId(issuerInfo.getName(), issuerInfo.getPublicKey(), localX509CertImpl.getSerialNumberObject());
      byte[] arrayOfByte = (byte[])ocspResponses.get(paramX509Certificate);
      if (arrayOfByte != null)
      {
        if (debug != null) {
          debug.println("Found cached OCSP response");
        }
        localOCSPResponse = new OCSPResponse(arrayOfByte);
        localObject1 = null;
        localObject2 = ocspExtensions.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Extension)((Iterator)localObject2).next();
          if (((Extension)localObject3).getId().equals("1.3.6.1.5.5.7.48.1.2")) {
            localObject1 = ((Extension)localObject3).getValue();
          }
        }
        localOCSPResponse.verify(Collections.singletonList(localCertId), issuerInfo, responderCert, params.date(), (byte[])localObject1, params.variant());
      }
      else
      {
        localObject1 = responderURI != null ? responderURI : OCSP.getResponderURI(localX509CertImpl);
        if (localObject1 == null) {
          throw new CertPathValidatorException("Certificate does not specify OCSP responder", null, null, -1);
        }
        localOCSPResponse = OCSP.check(Collections.singletonList(localCertId), (URI)localObject1, issuerInfo, responderCert, null, ocspExtensions, params.variant());
      }
    }
    catch (IOException localIOException)
    {
      throw new CertPathValidatorException("Unable to determine revocation status due to network error", localIOException, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    }
    OCSPResponse.SingleResponse localSingleResponse = localOCSPResponse.getSingleResponse(localCertId);
    Object localObject1 = localSingleResponse.getCertStatus();
    if (localObject1 == OCSP.RevocationStatus.CertStatus.REVOKED)
    {
      localObject2 = localSingleResponse.getRevocationTime();
      if (((Date)localObject2).before(params.date()))
      {
        localObject3 = new CertificateRevokedException((Date)localObject2, localSingleResponse.getRevocationReason(), localOCSPResponse.getSignerCertificate().getSubjectX500Principal(), localSingleResponse.getSingleExtensions());
        throw new CertPathValidatorException(((Throwable)localObject3).getMessage(), (Throwable)localObject3, null, -1, CertPathValidatorException.BasicReason.REVOKED);
      }
    }
    else if (localObject1 == OCSP.RevocationStatus.CertStatus.UNKNOWN)
    {
      throw new CertPathValidatorException("Certificate's revocation status is unknown", null, params.certPath(), -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    }
  }
  
  private static String stripOutSeparators(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < arrayOfChar.length; i++) {
      if ("0123456789ABCDEFabcdef".indexOf(arrayOfChar[i]) != -1) {
        localStringBuilder.append(arrayOfChar[i]);
      }
    }
    return localStringBuilder.toString();
  }
  
  static boolean certCanSignCrl(X509Certificate paramX509Certificate)
  {
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    if (arrayOfBoolean != null) {
      return arrayOfBoolean[6];
    }
    return false;
  }
  
  private Collection<X509CRL> verifyPossibleCRLs(Set<X509CRL> paramSet, X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet1)
    throws CertPathValidatorException
  {
    try
    {
      X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
      if (debug != null) {
        debug.println("RevocationChecker.verifyPossibleCRLs: Checking CRLDPs for " + localX509CertImpl.getSubjectX500Principal());
      }
      CRLDistributionPointsExtension localCRLDistributionPointsExtension = localX509CertImpl.getCRLDistributionPointsExtension();
      List localList = null;
      if (localCRLDistributionPointsExtension == null)
      {
        localObject1 = (X500Name)localX509CertImpl.getIssuerDN();
        localObject2 = new DistributionPoint(new GeneralNames().add(new GeneralName((GeneralNameInterface)localObject1)), null, null);
        localList = Collections.singletonList(localObject2);
      }
      else
      {
        localList = localCRLDistributionPointsExtension.get("points");
      }
      Object localObject1 = new HashSet();
      Object localObject2 = localList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        DistributionPoint localDistributionPoint = (DistributionPoint)((Iterator)localObject2).next();
        Iterator localIterator = paramSet.iterator();
        while (localIterator.hasNext())
        {
          X509CRL localX509CRL = (X509CRL)localIterator.next();
          if (DistributionPointFetcher.verifyCRL(localX509CertImpl, localDistributionPoint, localX509CRL, paramArrayOfBoolean, paramBoolean, paramPublicKey, null, params.sigProvider(), paramSet1, certStores, params.date(), params.variant())) {
            ((Set)localObject1).add(localX509CRL);
          }
        }
        if (Arrays.equals(paramArrayOfBoolean, ALL_REASONS)) {
          break;
        }
      }
      return (Collection<X509CRL>)localObject1;
    }
    catch (CertificateException|CRLException|IOException localCertificateException)
    {
      if (debug != null)
      {
        debug.println("Exception while verifying CRL: " + localCertificateException.getMessage());
        localCertificateException.printStackTrace();
      }
    }
    return Collections.emptySet();
  }
  
  private void verifyWithSeparateSigningKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, Set<X509Certificate> paramSet)
    throws CertPathValidatorException
  {
    String str = "revocation status";
    if (debug != null) {
      debug.println("RevocationChecker.verifyWithSeparateSigningKey() ---checking " + str + "...");
    }
    if ((paramSet != null) && (paramSet.contains(paramX509Certificate)))
    {
      if (debug != null) {
        debug.println("RevocationChecker.verifyWithSeparateSigningKey() circular dependency");
      }
      throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    }
    if (!paramBoolean) {
      buildToNewKey(paramX509Certificate, null, paramSet);
    } else {
      buildToNewKey(paramX509Certificate, paramPublicKey, paramSet);
    }
  }
  
  /* Error */
  private void buildToNewKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, Set<X509Certificate> paramSet)
    throws CertPathValidatorException
  {
    // Byte code:
    //   0: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   3: ifnull +12 -> 15
    //   6: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   9: ldc_w 401
    //   12: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   15: new 462	java/util/HashSet
    //   18: dup
    //   19: invokespecial 968	java/util/HashSet:<init>	()V
    //   22: astore 4
    //   24: aload_2
    //   25: ifnull +12 -> 37
    //   28: aload 4
    //   30: aload_2
    //   31: invokeinterface 1069 2 0
    //   36: pop
    //   37: new 487	sun/security/provider/certpath/RevocationChecker$RejectKeySelector
    //   40: dup
    //   41: aload 4
    //   43: invokespecial 1031	sun/security/provider/certpath/RevocationChecker$RejectKeySelector:<init>	(Ljava/util/Set;)V
    //   46: astore 5
    //   48: aload 5
    //   50: aload_1
    //   51: invokevirtual 959	java/security/cert/X509Certificate:getIssuerX500Principal	()Ljavax/security/auth/x500/X500Principal;
    //   54: invokevirtual 954	java/security/cert/X509CertSelector:setSubject	(Ljavax/security/auth/x500/X500Principal;)V
    //   57: aload 5
    //   59: getstatic 857	sun/security/provider/certpath/RevocationChecker:CRL_SIGN_USAGE	[Z
    //   62: invokevirtual 950	java/security/cert/X509CertSelector:setKeyUsage	([Z)V
    //   65: aload_0
    //   66: getfield 860	sun/security/provider/certpath/RevocationChecker:anchor	Ljava/security/cert/TrustAnchor;
    //   69: ifnonnull +13 -> 82
    //   72: aload_0
    //   73: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   76: invokevirtual 1002	sun/security/provider/certpath/PKIX$ValidatorParams:trustAnchors	()Ljava/util/Set;
    //   79: goto +10 -> 89
    //   82: aload_0
    //   83: getfield 860	sun/security/provider/certpath/RevocationChecker:anchor	Ljava/security/cert/TrustAnchor;
    //   86: invokestatic 966	java/util/Collections:singleton	(Ljava/lang/Object;)Ljava/util/Set;
    //   89: astore 6
    //   91: new 446	java/security/cert/PKIXBuilderParameters
    //   94: dup
    //   95: aload 6
    //   97: aload 5
    //   99: invokespecial 935	java/security/cert/PKIXBuilderParameters:<init>	(Ljava/util/Set;Ljava/security/cert/CertSelector;)V
    //   102: astore 7
    //   104: goto +15 -> 119
    //   107: astore 8
    //   109: new 419	java/lang/RuntimeException
    //   112: dup
    //   113: aload 8
    //   115: invokespecial 889	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   118: athrow
    //   119: aload 7
    //   121: aload_0
    //   122: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   125: invokevirtual 1001	sun/security/provider/certpath/PKIX$ValidatorParams:initialPolicies	()Ljava/util/Set;
    //   128: invokevirtual 934	java/security/cert/PKIXBuilderParameters:setInitialPolicies	(Ljava/util/Set;)V
    //   131: aload 7
    //   133: aload_0
    //   134: getfield 863	sun/security/provider/certpath/RevocationChecker:certStores	Ljava/util/List;
    //   137: invokevirtual 933	java/security/cert/PKIXBuilderParameters:setCertStores	(Ljava/util/List;)V
    //   140: aload 7
    //   142: aload_0
    //   143: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   146: invokevirtual 992	sun/security/provider/certpath/PKIX$ValidatorParams:explicitPolicyRequired	()Z
    //   149: invokevirtual 926	java/security/cert/PKIXBuilderParameters:setExplicitPolicyRequired	(Z)V
    //   152: aload 7
    //   154: aload_0
    //   155: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   158: invokevirtual 993	sun/security/provider/certpath/PKIX$ValidatorParams:policyMappingInhibited	()Z
    //   161: invokevirtual 927	java/security/cert/PKIXBuilderParameters:setPolicyMappingInhibited	(Z)V
    //   164: aload 7
    //   166: aload_0
    //   167: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   170: invokevirtual 991	sun/security/provider/certpath/PKIX$ValidatorParams:anyPolicyInhibited	()Z
    //   173: invokevirtual 925	java/security/cert/PKIXBuilderParameters:setAnyPolicyInhibited	(Z)V
    //   176: aload 7
    //   178: aload_0
    //   179: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   182: invokevirtual 998	sun/security/provider/certpath/PKIX$ValidatorParams:date	()Ljava/util/Date;
    //   185: invokevirtual 931	java/security/cert/PKIXBuilderParameters:setDate	(Ljava/util/Date;)V
    //   188: aload 7
    //   190: aload_0
    //   191: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   194: invokevirtual 997	sun/security/provider/certpath/PKIX$ValidatorParams:getPKIXParameters	()Ljava/security/cert/PKIXParameters;
    //   197: invokevirtual 939	java/security/cert/PKIXParameters:getCertPathCheckers	()Ljava/util/List;
    //   200: invokevirtual 932	java/security/cert/PKIXBuilderParameters:setCertPathCheckers	(Ljava/util/List;)V
    //   203: aload 7
    //   205: aload_0
    //   206: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   209: invokevirtual 994	sun/security/provider/certpath/PKIX$ValidatorParams:sigProvider	()Ljava/lang/String;
    //   212: invokevirtual 929	java/security/cert/PKIXBuilderParameters:setSigProvider	(Ljava/lang/String;)V
    //   215: aload 7
    //   217: iconst_0
    //   218: invokevirtual 928	java/security/cert/PKIXBuilderParameters:setRevocationEnabled	(Z)V
    //   221: getstatic 847	sun/security/provider/certpath/Builder:USE_AIA	Z
    //   224: iconst_1
    //   225: if_icmpne +150 -> 375
    //   228: aconst_null
    //   229: astore 8
    //   231: aload_1
    //   232: invokestatic 1054	sun/security/x509/X509CertImpl:toImpl	(Ljava/security/cert/X509Certificate;)Lsun/security/x509/X509CertImpl;
    //   235: astore 8
    //   237: goto +38 -> 275
    //   240: astore 9
    //   242: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   245: ifnull +30 -> 275
    //   248: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   251: new 421	java/lang/StringBuilder
    //   254: dup
    //   255: invokespecial 894	java/lang/StringBuilder:<init>	()V
    //   258: ldc_w 402
    //   261: invokevirtual 899	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: aload 9
    //   266: invokevirtual 898	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   269: invokevirtual 895	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   272: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   275: aconst_null
    //   276: astore 9
    //   278: aload 8
    //   280: ifnull +10 -> 290
    //   283: aload 8
    //   285: invokevirtual 1051	sun/security/x509/X509CertImpl:getAuthorityInfoAccessExtension	()Lsun/security/x509/AuthorityInfoAccessExtension;
    //   288: astore 9
    //   290: aload 9
    //   292: ifnull +83 -> 375
    //   295: aload 9
    //   297: invokevirtual 1037	sun/security/x509/AuthorityInfoAccessExtension:getAccessDescriptions	()Ljava/util/List;
    //   300: astore 10
    //   302: aload 10
    //   304: ifnull +71 -> 375
    //   307: aload 10
    //   309: invokeinterface 1065 1 0
    //   314: astore 11
    //   316: aload 11
    //   318: invokeinterface 1059 1 0
    //   323: ifeq +52 -> 375
    //   326: aload 11
    //   328: invokeinterface 1060 1 0
    //   333: checkcast 492	sun/security/x509/AccessDescription
    //   336: astore 12
    //   338: aload 12
    //   340: invokestatic 1032	sun/security/provider/certpath/URICertStore:getInstance	(Lsun/security/x509/AccessDescription;)Ljava/security/cert/CertStore;
    //   343: astore 13
    //   345: aload 13
    //   347: ifnull +25 -> 372
    //   350: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   353: ifnull +12 -> 365
    //   356: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   359: ldc_w 406
    //   362: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   365: aload 7
    //   367: aload 13
    //   369: invokevirtual 930	java/security/cert/PKIXBuilderParameters:addCertStore	(Ljava/security/cert/CertStore;)V
    //   372: goto -56 -> 316
    //   375: aconst_null
    //   376: astore 8
    //   378: ldc_w 396
    //   381: invokestatic 906	java/security/cert/CertPathBuilder:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertPathBuilder;
    //   384: astore 8
    //   386: goto +15 -> 401
    //   389: astore 9
    //   391: new 437	java/security/cert/CertPathValidatorException
    //   394: dup
    //   395: aload 9
    //   397: invokespecial 911	java/security/cert/CertPathValidatorException:<init>	(Ljava/lang/Throwable;)V
    //   400: athrow
    //   401: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   404: ifnull +12 -> 416
    //   407: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   410: ldc_w 398
    //   413: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   416: aload 8
    //   418: aload 7
    //   420: invokevirtual 907	java/security/cert/CertPathBuilder:build	(Ljava/security/cert/CertPathParameters;)Ljava/security/cert/CertPathBuilderResult;
    //   423: checkcast 447	java/security/cert/PKIXCertPathBuilderResult
    //   426: astore 9
    //   428: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   431: ifnull +12 -> 443
    //   434: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   437: ldc_w 397
    //   440: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   443: aload_3
    //   444: ifnonnull +11 -> 455
    //   447: new 462	java/util/HashSet
    //   450: dup
    //   451: invokespecial 968	java/util/HashSet:<init>	()V
    //   454: astore_3
    //   455: aload_3
    //   456: aload_1
    //   457: invokeinterface 1069 2 0
    //   462: pop
    //   463: aload 9
    //   465: invokevirtual 938	java/security/cert/PKIXCertPathBuilderResult:getTrustAnchor	()Ljava/security/cert/TrustAnchor;
    //   468: astore 10
    //   470: aload 10
    //   472: invokevirtual 943	java/security/cert/TrustAnchor:getCAPublicKey	()Ljava/security/PublicKey;
    //   475: astore 11
    //   477: aload 11
    //   479: ifnonnull +13 -> 492
    //   482: aload 10
    //   484: invokevirtual 944	java/security/cert/TrustAnchor:getTrustedCert	()Ljava/security/cert/X509Certificate;
    //   487: invokevirtual 958	java/security/cert/X509Certificate:getPublicKey	()Ljava/security/PublicKey;
    //   490: astore 11
    //   492: iconst_1
    //   493: istore 12
    //   495: aload 9
    //   497: invokevirtual 937	java/security/cert/PKIXCertPathBuilderResult:getCertPath	()Ljava/security/cert/CertPath;
    //   500: invokevirtual 905	java/security/cert/CertPath:getCertificates	()Ljava/util/List;
    //   503: astore 13
    //   505: aload 13
    //   507: invokeinterface 1061 1 0
    //   512: iconst_1
    //   513: isub
    //   514: istore 14
    //   516: iload 14
    //   518: iflt +96 -> 614
    //   521: aload 13
    //   523: iload 14
    //   525: invokeinterface 1063 2 0
    //   530: checkcast 456	java/security/cert/X509Certificate
    //   533: astore 15
    //   535: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   538: ifnull +41 -> 579
    //   541: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   544: new 421	java/lang/StringBuilder
    //   547: dup
    //   548: invokespecial 894	java/lang/StringBuilder:<init>	()V
    //   551: ldc_w 400
    //   554: invokevirtual 899	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   557: iload 14
    //   559: invokevirtual 897	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   562: ldc_w 391
    //   565: invokevirtual 899	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   568: aload 15
    //   570: invokevirtual 898	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   573: invokevirtual 895	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   576: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   579: aload_0
    //   580: aload 15
    //   582: aload 11
    //   584: aconst_null
    //   585: iload 12
    //   587: iconst_1
    //   588: aload_3
    //   589: aload 6
    //   591: invokespecial 1028	sun/security/provider/certpath/RevocationChecker:checkCRLs	(Ljava/security/cert/X509Certificate;Ljava/security/PublicKey;Ljava/security/cert/X509Certificate;ZZLjava/util/Set;Ljava/util/Set;)V
    //   594: aload 15
    //   596: invokestatic 1007	sun/security/provider/certpath/RevocationChecker:certCanSignCrl	(Ljava/security/cert/X509Certificate;)Z
    //   599: istore 12
    //   601: aload 15
    //   603: invokevirtual 958	java/security/cert/X509Certificate:getPublicKey	()Ljava/security/PublicKey;
    //   606: astore 11
    //   608: iinc 14 -1
    //   611: goto -95 -> 516
    //   614: goto +21 -> 635
    //   617: astore 14
    //   619: aload 4
    //   621: aload 9
    //   623: invokevirtual 936	java/security/cert/PKIXCertPathBuilderResult:getPublicKey	()Ljava/security/PublicKey;
    //   626: invokeinterface 1069 2 0
    //   631: pop
    //   632: goto -231 -> 401
    //   635: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   638: ifnull +33 -> 671
    //   641: getstatic 869	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
    //   644: new 421	java/lang/StringBuilder
    //   647: dup
    //   648: invokespecial 894	java/lang/StringBuilder:<init>	()V
    //   651: ldc_w 399
    //   654: invokevirtual 899	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   657: aload 9
    //   659: invokevirtual 936	java/security/cert/PKIXCertPathBuilderResult:getPublicKey	()Ljava/security/PublicKey;
    //   662: invokevirtual 898	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   665: invokevirtual 895	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   668: invokevirtual 1033	sun/security/util/Debug:println	(Ljava/lang/String;)V
    //   671: aload 9
    //   673: invokevirtual 936	java/security/cert/PKIXCertPathBuilderResult:getPublicKey	()Ljava/security/PublicKey;
    //   676: astore 14
    //   678: aload 13
    //   680: invokeinterface 1062 1 0
    //   685: ifeq +7 -> 692
    //   688: aconst_null
    //   689: goto +14 -> 703
    //   692: aload 13
    //   694: iconst_0
    //   695: invokeinterface 1063 2 0
    //   700: checkcast 456	java/security/cert/X509Certificate
    //   703: astore 15
    //   705: aload_0
    //   706: aload_1
    //   707: aload 14
    //   709: aload 15
    //   711: iconst_1
    //   712: iconst_0
    //   713: aconst_null
    //   714: aload_0
    //   715: getfield 867	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
    //   718: invokevirtual 1002	sun/security/provider/certpath/PKIX$ValidatorParams:trustAnchors	()Ljava/util/Set;
    //   721: invokespecial 1028	sun/security/provider/certpath/RevocationChecker:checkCRLs	(Ljava/security/cert/X509Certificate;Ljava/security/PublicKey;Ljava/security/cert/X509Certificate;ZZLjava/util/Set;Ljava/util/Set;)V
    //   724: return
    //   725: astore 16
    //   727: aload 16
    //   729: invokevirtual 913	java/security/cert/CertPathValidatorException:getReason	()Ljava/security/cert/CertPathValidatorException$Reason;
    //   732: getstatic 841	java/security/cert/CertPathValidatorException$BasicReason:REVOKED	Ljava/security/cert/CertPathValidatorException$BasicReason;
    //   735: if_acmpne +6 -> 741
    //   738: aload 16
    //   740: athrow
    //   741: aload 4
    //   743: aload 14
    //   745: invokeinterface 1069 2 0
    //   750: pop
    //   751: goto -350 -> 401
    //   754: astore 9
    //   756: new 437	java/security/cert/CertPathValidatorException
    //   759: dup
    //   760: aload 9
    //   762: invokespecial 911	java/security/cert/CertPathValidatorException:<init>	(Ljava/lang/Throwable;)V
    //   765: athrow
    //   766: astore 9
    //   768: new 437	java/security/cert/CertPathValidatorException
    //   771: dup
    //   772: ldc 8
    //   774: aconst_null
    //   775: aconst_null
    //   776: iconst_m1
    //   777: getstatic 842	java/security/cert/CertPathValidatorException$BasicReason:UNDETERMINED_REVOCATION_STATUS	Ljava/security/cert/CertPathValidatorException$BasicReason;
    //   780: invokespecial 916	java/security/cert/CertPathValidatorException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;Ljava/security/cert/CertPath;ILjava/security/cert/CertPathValidatorException$Reason;)V
    //   783: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	784	0	this	RevocationChecker
    //   0	784	1	paramX509Certificate	X509Certificate
    //   0	784	2	paramPublicKey	PublicKey
    //   0	784	3	paramSet	Set<X509Certificate>
    //   22	720	4	localHashSet	HashSet
    //   46	52	5	localRejectKeySelector	RejectKeySelector
    //   89	501	6	localSet	Set
    //   102	317	7	localPKIXBuilderParameters	java.security.cert.PKIXBuilderParameters
    //   107	7	8	localInvalidAlgorithmParameterException1	InvalidAlgorithmParameterException
    //   229	188	8	localObject1	Object
    //   240	25	9	localCertificateException	CertificateException
    //   276	20	9	localAuthorityInfoAccessExtension	sun.security.x509.AuthorityInfoAccessExtension
    //   389	7	9	localNoSuchAlgorithmException	NoSuchAlgorithmException
    //   426	246	9	localPKIXCertPathBuilderResult	java.security.cert.PKIXCertPathBuilderResult
    //   754	7	9	localInvalidAlgorithmParameterException2	InvalidAlgorithmParameterException
    //   766	1	9	localCertPathBuilderException	java.security.cert.CertPathBuilderException
    //   300	183	10	localObject2	Object
    //   314	293	11	localObject3	Object
    //   336	3	12	localAccessDescription	sun.security.x509.AccessDescription
    //   493	107	12	bool	boolean
    //   343	350	13	localObject4	Object
    //   514	95	14	i	int
    //   617	1	14	localCertPathValidatorException1	CertPathValidatorException
    //   676	68	14	localPublicKey	PublicKey
    //   533	177	15	localX509Certificate	X509Certificate
    //   725	14	16	localCertPathValidatorException2	CertPathValidatorException
    // Exception table:
    //   from	to	target	type
    //   91	104	107	java/security/InvalidAlgorithmParameterException
    //   231	237	240	java/security/cert/CertificateException
    //   378	386	389	java/security/NoSuchAlgorithmException
    //   505	614	617	java/security/cert/CertPathValidatorException
    //   705	724	725	java/security/cert/CertPathValidatorException
    //   401	632	754	java/security/InvalidAlgorithmParameterException
    //   635	724	754	java/security/InvalidAlgorithmParameterException
    //   725	751	754	java/security/InvalidAlgorithmParameterException
    //   401	632	766	java/security/cert/CertPathBuilderException
    //   635	724	766	java/security/cert/CertPathBuilderException
    //   725	751	766	java/security/cert/CertPathBuilderException
  }
  
  public RevocationChecker clone()
  {
    RevocationChecker localRevocationChecker = (RevocationChecker)super.clone();
    softFailExceptions = new LinkedList(softFailExceptions);
    return localRevocationChecker;
  }
  
  private static enum Mode
  {
    PREFER_OCSP,  PREFER_CRLS,  ONLY_CRLS,  ONLY_OCSP;
    
    private Mode() {}
  }
  
  private static class RejectKeySelector
    extends X509CertSelector
  {
    private final Set<PublicKey> badKeySet;
    
    RejectKeySelector(Set<PublicKey> paramSet)
    {
      badKeySet = paramSet;
    }
    
    public boolean match(Certificate paramCertificate)
    {
      if (!super.match(paramCertificate)) {
        return false;
      }
      if (badKeySet.contains(paramCertificate.getPublicKey()))
      {
        if (RevocationChecker.debug != null) {
          RevocationChecker.debug.println("RejectKeySelector.match: bad key");
        }
        return false;
      }
      if (RevocationChecker.debug != null) {
        RevocationChecker.debug.println("RejectKeySelector.match: returning true");
      }
      return true;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("RejectKeySelector: [\n");
      localStringBuilder.append(super.toString());
      localStringBuilder.append(badKeySet);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static class RevocationProperties
  {
    boolean onlyEE;
    boolean ocspEnabled;
    boolean crlDPEnabled;
    String ocspUrl;
    String ocspSubject;
    String ocspIssuer;
    String ocspSerial;
    
    private RevocationProperties() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\RevocationChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */