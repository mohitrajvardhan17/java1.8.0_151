package sun.security.provider.certpath.ldap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.LDAPCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.provider.certpath.X509CertificatePair;
import sun.security.util.Cache;
import sun.security.util.Debug;
import sun.security.x509.X500Name;

public final class LDAPCertStore
  extends CertStoreSpi
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final boolean DEBUG = false;
  private static final String USER_CERT = "userCertificate;binary";
  private static final String CA_CERT = "cACertificate;binary";
  private static final String CROSS_CERT = "crossCertificatePair;binary";
  private static final String CRL = "certificateRevocationList;binary";
  private static final String ARL = "authorityRevocationList;binary";
  private static final String DELTA_CRL = "deltaRevocationList;binary";
  private static final String[] STRING0 = new String[0];
  private static final byte[][] BB0 = new byte[0][];
  private static final Attributes EMPTY_ATTRIBUTES = new BasicAttributes();
  private static final int DEFAULT_CACHE_SIZE = 750;
  private static final int DEFAULT_CACHE_LIFETIME = 30;
  private static final int LIFETIME;
  private static final String PROP_LIFETIME = "sun.security.certpath.ldap.cache.lifetime";
  private static final String PROP_DISABLE_APP_RESOURCE_FILES = "sun.security.certpath.ldap.disable.app.resource.files";
  private CertificateFactory cf;
  private DirContext ctx;
  private boolean prefetchCRLs = false;
  private final Cache<String, byte[][]> valueCache;
  private int cacheHits = 0;
  private int cacheMisses = 0;
  private int requests = 0;
  private static final Cache<LDAPCertStoreParameters, CertStore> certStoreCache = Cache.newSoftMemoryCache(185);
  
  public LDAPCertStore(CertStoreParameters paramCertStoreParameters)
    throws InvalidAlgorithmParameterException
  {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof LDAPCertStoreParameters)) {
      throw new InvalidAlgorithmParameterException("parameters must be LDAPCertStoreParameters");
    }
    LDAPCertStoreParameters localLDAPCertStoreParameters = (LDAPCertStoreParameters)paramCertStoreParameters;
    createInitialDirContext(localLDAPCertStoreParameters.getServerName(), localLDAPCertStoreParameters.getPort());
    try
    {
      cf = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException)
    {
      throw new InvalidAlgorithmParameterException("unable to create CertificateFactory for X.509");
    }
    if (LIFETIME == 0) {
      valueCache = Cache.newNullCache();
    } else if (LIFETIME < 0) {
      valueCache = Cache.newSoftMemoryCache(750);
    } else {
      valueCache = Cache.newSoftMemoryCache(750, LIFETIME);
    }
  }
  
  static synchronized CertStore getInstance(LDAPCertStoreParameters paramLDAPCertStoreParameters)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
  {
    CertStore localCertStore = (CertStore)certStoreCache.get(paramLDAPCertStoreParameters);
    if (localCertStore == null)
    {
      localCertStore = CertStore.getInstance("LDAP", paramLDAPCertStoreParameters);
      certStoreCache.put(paramLDAPCertStoreParameters, localCertStore);
    }
    else if (debug != null)
    {
      debug.println("LDAPCertStore.getInstance: cache hit");
    }
    return localCertStore;
  }
  
  private void createInitialDirContext(String paramString, int paramInt)
    throws InvalidAlgorithmParameterException
  {
    String str = "ldap://" + paramString + ":" + paramInt;
    Hashtable localHashtable1 = new Hashtable();
    localHashtable1.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    localHashtable1.put("java.naming.provider.url", str);
    boolean bool = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.certpath.ldap.disable.app.resource.files"))).booleanValue();
    if (bool)
    {
      if (debug != null) {
        debug.println("LDAPCertStore disabling app resource files");
      }
      localHashtable1.put("com.sun.naming.disable.app.resource.files", "true");
    }
    try
    {
      ctx = new InitialDirContext(localHashtable1);
      Hashtable localHashtable2 = ctx.getEnvironment();
      if (localHashtable2.get("java.naming.referral") == null) {
        ctx.addToEnvironment("java.naming.referral", "follow-scheme");
      }
    }
    catch (NamingException localNamingException)
    {
      if (debug != null)
      {
        debug.println("LDAPCertStore.engineInit about to throw InvalidAlgorithmParameterException");
        localNamingException.printStackTrace();
      }
      InvalidAlgorithmParameterException localInvalidAlgorithmParameterException = new InvalidAlgorithmParameterException("unable to create InitialDirContext using supplied parameters");
      localInvalidAlgorithmParameterException.initCause(localNamingException);
      throw ((InvalidAlgorithmParameterException)localInvalidAlgorithmParameterException);
    }
  }
  
  private Collection<X509Certificate> getCertificates(LDAPRequest paramLDAPRequest, String paramString, X509CertSelector paramX509CertSelector)
    throws CertStoreException
  {
    byte[][] arrayOfByte;
    try
    {
      arrayOfByte = paramLDAPRequest.getValues(paramString);
    }
    catch (NamingException localNamingException)
    {
      throw new CertStoreException(localNamingException);
    }
    int i = arrayOfByte.length;
    if (i == 0) {
      return Collections.emptySet();
    }
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte[j]);
      try
      {
        Certificate localCertificate = cf.generateCertificate(localByteArrayInputStream);
        if (paramX509CertSelector.match(localCertificate)) {
          localArrayList.add((X509Certificate)localCertificate);
        }
      }
      catch (CertificateException localCertificateException)
      {
        if (debug != null)
        {
          debug.println("LDAPCertStore.getCertificates() encountered exception while parsing cert, skipping the bad data: ");
          HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
          debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
        }
      }
    }
    return localArrayList;
  }
  
  private Collection<X509CertificatePair> getCertPairs(LDAPRequest paramLDAPRequest, String paramString)
    throws CertStoreException
  {
    byte[][] arrayOfByte;
    try
    {
      arrayOfByte = paramLDAPRequest.getValues(paramString);
    }
    catch (NamingException localNamingException)
    {
      throw new CertStoreException(localNamingException);
    }
    int i = arrayOfByte.length;
    if (i == 0) {
      return Collections.emptySet();
    }
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      try
      {
        X509CertificatePair localX509CertificatePair = X509CertificatePair.generateCertificatePair(arrayOfByte[j]);
        localArrayList.add(localX509CertificatePair);
      }
      catch (CertificateException localCertificateException)
      {
        if (debug != null)
        {
          debug.println("LDAPCertStore.getCertPairs() encountered exception while parsing cert, skipping the bad data: ");
          HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
          debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
        }
      }
    }
    return localArrayList;
  }
  
  private Collection<X509Certificate> getMatchingCrossCerts(LDAPRequest paramLDAPRequest, X509CertSelector paramX509CertSelector1, X509CertSelector paramX509CertSelector2)
    throws CertStoreException
  {
    Collection localCollection = getCertPairs(paramLDAPRequest, "crossCertificatePair;binary");
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      X509CertificatePair localX509CertificatePair = (X509CertificatePair)localIterator.next();
      X509Certificate localX509Certificate;
      if (paramX509CertSelector1 != null)
      {
        localX509Certificate = localX509CertificatePair.getForward();
        if ((localX509Certificate != null) && (paramX509CertSelector1.match(localX509Certificate))) {
          localArrayList.add(localX509Certificate);
        }
      }
      if (paramX509CertSelector2 != null)
      {
        localX509Certificate = localX509CertificatePair.getReverse();
        if ((localX509Certificate != null) && (paramX509CertSelector2.match(localX509Certificate))) {
          localArrayList.add(localX509Certificate);
        }
      }
    }
    return localArrayList;
  }
  
  public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
    throws CertStoreException
  {
    if (debug != null) {
      debug.println("LDAPCertStore.engineGetCertificates() selector: " + String.valueOf(paramCertSelector));
    }
    if (paramCertSelector == null) {
      paramCertSelector = new X509CertSelector();
    }
    if (!(paramCertSelector instanceof X509CertSelector)) {
      throw new CertStoreException("LDAPCertStore needs an X509CertSelector to find certs");
    }
    X509CertSelector localX509CertSelector = (X509CertSelector)paramCertSelector;
    int i = localX509CertSelector.getBasicConstraints();
    String str1 = localX509CertSelector.getSubjectAsString();
    String str2 = localX509CertSelector.getIssuerAsString();
    HashSet localHashSet = new HashSet();
    if (debug != null) {
      debug.println("LDAPCertStore.engineGetCertificates() basicConstraints: " + i);
    }
    LDAPRequest localLDAPRequest;
    if (str1 != null)
    {
      if (debug != null) {
        debug.println("LDAPCertStore.engineGetCertificates() subject is not null");
      }
      localLDAPRequest = new LDAPRequest(str1);
      if (i > -2)
      {
        localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
        localLDAPRequest.addRequestedAttribute("cACertificate;binary");
        localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
        if (prefetchCRLs) {
          localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
        }
      }
      if (i < 0) {
        localLDAPRequest.addRequestedAttribute("userCertificate;binary");
      }
      if (i > -2)
      {
        localHashSet.addAll(getMatchingCrossCerts(localLDAPRequest, localX509CertSelector, null));
        if (debug != null) {
          debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(subject,xsel,null),certs.size(): " + localHashSet.size());
        }
        localHashSet.addAll(getCertificates(localLDAPRequest, "cACertificate;binary", localX509CertSelector));
        if (debug != null) {
          debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,CA_CERT,xsel),certs.size(): " + localHashSet.size());
        }
      }
      if (i < 0)
      {
        localHashSet.addAll(getCertificates(localLDAPRequest, "userCertificate;binary", localX509CertSelector));
        if (debug != null) {
          debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,USER_CERT, xsel),certs.size(): " + localHashSet.size());
        }
      }
    }
    else
    {
      if (debug != null) {
        debug.println("LDAPCertStore.engineGetCertificates() subject is null");
      }
      if (i == -2) {
        throw new CertStoreException("need subject to find EE certs");
      }
      if (str2 == null) {
        throw new CertStoreException("need subject or issuer to find certs");
      }
    }
    if (debug != null) {
      debug.println("LDAPCertStore.engineGetCertificates() about to getMatchingCrossCerts...");
    }
    if ((str2 != null) && (i > -2))
    {
      localLDAPRequest = new LDAPRequest(str2);
      localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
      localLDAPRequest.addRequestedAttribute("cACertificate;binary");
      localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
      if (prefetchCRLs) {
        localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
      }
      localHashSet.addAll(getMatchingCrossCerts(localLDAPRequest, null, localX509CertSelector));
      if (debug != null) {
        debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(issuer,null,xsel),certs.size(): " + localHashSet.size());
      }
      localHashSet.addAll(getCertificates(localLDAPRequest, "cACertificate;binary", localX509CertSelector));
      if (debug != null) {
        debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(issuer,CA_CERT,xsel),certs.size(): " + localHashSet.size());
      }
    }
    if (debug != null) {
      debug.println("LDAPCertStore.engineGetCertificates() returning certs");
    }
    return localHashSet;
  }
  
  private Collection<X509CRL> getCRLs(LDAPRequest paramLDAPRequest, String paramString, X509CRLSelector paramX509CRLSelector)
    throws CertStoreException
  {
    byte[][] arrayOfByte;
    try
    {
      arrayOfByte = paramLDAPRequest.getValues(paramString);
    }
    catch (NamingException localNamingException)
    {
      throw new CertStoreException(localNamingException);
    }
    int i = arrayOfByte.length;
    if (i == 0) {
      return Collections.emptySet();
    }
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      try
      {
        CRL localCRL = cf.generateCRL(new ByteArrayInputStream(arrayOfByte[j]));
        if (paramX509CRLSelector.match(localCRL)) {
          localArrayList.add((X509CRL)localCRL);
        }
      }
      catch (CRLException localCRLException)
      {
        if (debug != null)
        {
          debug.println("LDAPCertStore.getCRLs() encountered exception while parsing CRL, skipping the bad data: ");
          HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
          debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
        }
      }
    }
    return localArrayList;
  }
  
  public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
    throws CertStoreException
  {
    if (debug != null) {
      debug.println("LDAPCertStore.engineGetCRLs() selector: " + paramCRLSelector);
    }
    if (paramCRLSelector == null) {
      paramCRLSelector = new X509CRLSelector();
    }
    if (!(paramCRLSelector instanceof X509CRLSelector)) {
      throw new CertStoreException("need X509CRLSelector to find CRLs");
    }
    X509CRLSelector localX509CRLSelector = (X509CRLSelector)paramCRLSelector;
    HashSet localHashSet = new HashSet();
    X509Certificate localX509Certificate = localX509CRLSelector.getCertificateChecking();
    Object localObject1;
    if (localX509Certificate != null)
    {
      localObject1 = new HashSet();
      localObject2 = localX509Certificate.getIssuerX500Principal();
      ((Collection)localObject1).add(((X500Principal)localObject2).getName("RFC2253"));
    }
    else
    {
      localObject1 = localX509CRLSelector.getIssuerNames();
      if (localObject1 == null) {
        throw new CertStoreException("need issuerNames or certChecking to find CRLs");
      }
    }
    Object localObject2 = ((Collection)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject3 = ((Iterator)localObject2).next();
      String str;
      if ((localObject3 instanceof byte[])) {
        try
        {
          X500Principal localX500Principal = new X500Principal((byte[])localObject3);
          str = localX500Principal.getName("RFC2253");
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
      } else {
        str = (String)localObject3;
      }
      Object localObject4 = Collections.emptySet();
      LDAPRequest localLDAPRequest;
      if ((localX509Certificate == null) || (localX509Certificate.getBasicConstraints() != -1))
      {
        localLDAPRequest = new LDAPRequest(str);
        localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
        localLDAPRequest.addRequestedAttribute("cACertificate;binary");
        localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
        if (prefetchCRLs) {
          localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
        }
        try
        {
          localObject4 = getCRLs(localLDAPRequest, "authorityRevocationList;binary", localX509CRLSelector);
          if (((Collection)localObject4).isEmpty()) {
            prefetchCRLs = true;
          } else {
            localHashSet.addAll((Collection)localObject4);
          }
        }
        catch (CertStoreException localCertStoreException)
        {
          if (debug != null)
          {
            debug.println("LDAPCertStore.engineGetCRLs non-fatal error retrieving ARLs:" + localCertStoreException);
            localCertStoreException.printStackTrace();
          }
        }
      }
      if ((((Collection)localObject4).isEmpty()) || (localX509Certificate == null))
      {
        localLDAPRequest = new LDAPRequest(str);
        localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
        localObject4 = getCRLs(localLDAPRequest, "certificateRevocationList;binary", localX509CRLSelector);
        localHashSet.addAll((Collection)localObject4);
      }
    }
    return localHashSet;
  }
  
  static LDAPCertStoreParameters getParameters(URI paramURI)
  {
    String str = paramURI.getHost();
    if (str == null) {
      return new SunLDAPCertStoreParameters();
    }
    int i = paramURI.getPort();
    return i == -1 ? new SunLDAPCertStoreParameters(str) : new SunLDAPCertStoreParameters(str, i);
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.certpath.ldap.cache.lifetime"));
    if (str != null) {
      LIFETIME = Integer.parseInt(str);
    } else {
      LIFETIME = 30;
    }
  }
  
  static class LDAPCRLSelector
    extends X509CRLSelector
  {
    private X509CRLSelector selector;
    private Collection<X500Principal> certIssuers;
    private Collection<X500Principal> issuers;
    private HashSet<Object> issuerNames;
    
    LDAPCRLSelector(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString)
      throws IOException
    {
      selector = (paramX509CRLSelector == null ? new X509CRLSelector() : paramX509CRLSelector);
      certIssuers = paramCollection;
      issuerNames = new HashSet();
      issuerNames.add(paramString);
      issuers = new HashSet();
      issuers.add(new X500Name(paramString).asX500Principal());
    }
    
    public Collection<X500Principal> getIssuers()
    {
      return Collections.unmodifiableCollection(issuers);
    }
    
    public Collection<Object> getIssuerNames()
    {
      return Collections.unmodifiableCollection(issuerNames);
    }
    
    public BigInteger getMinCRL()
    {
      return selector.getMinCRL();
    }
    
    public BigInteger getMaxCRL()
    {
      return selector.getMaxCRL();
    }
    
    public Date getDateAndTime()
    {
      return selector.getDateAndTime();
    }
    
    public X509Certificate getCertificateChecking()
    {
      return selector.getCertificateChecking();
    }
    
    public boolean match(CRL paramCRL)
    {
      selector.setIssuers(certIssuers);
      boolean bool = selector.match(paramCRL);
      selector.setIssuers(issuers);
      return bool;
    }
  }
  
  static class LDAPCertSelector
    extends X509CertSelector
  {
    private X500Principal certSubject;
    private X509CertSelector selector;
    private X500Principal subject;
    
    LDAPCertSelector(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString)
      throws IOException
    {
      selector = (paramX509CertSelector == null ? new X509CertSelector() : paramX509CertSelector);
      certSubject = paramX500Principal;
      subject = new X500Name(paramString).asX500Principal();
    }
    
    public X509Certificate getCertificate()
    {
      return selector.getCertificate();
    }
    
    public BigInteger getSerialNumber()
    {
      return selector.getSerialNumber();
    }
    
    public X500Principal getIssuer()
    {
      return selector.getIssuer();
    }
    
    public String getIssuerAsString()
    {
      return selector.getIssuerAsString();
    }
    
    public byte[] getIssuerAsBytes()
      throws IOException
    {
      return selector.getIssuerAsBytes();
    }
    
    public X500Principal getSubject()
    {
      return subject;
    }
    
    public String getSubjectAsString()
    {
      return subject.getName();
    }
    
    public byte[] getSubjectAsBytes()
      throws IOException
    {
      return subject.getEncoded();
    }
    
    public byte[] getSubjectKeyIdentifier()
    {
      return selector.getSubjectKeyIdentifier();
    }
    
    public byte[] getAuthorityKeyIdentifier()
    {
      return selector.getAuthorityKeyIdentifier();
    }
    
    public Date getCertificateValid()
    {
      return selector.getCertificateValid();
    }
    
    public Date getPrivateKeyValid()
    {
      return selector.getPrivateKeyValid();
    }
    
    public String getSubjectPublicKeyAlgID()
    {
      return selector.getSubjectPublicKeyAlgID();
    }
    
    public PublicKey getSubjectPublicKey()
    {
      return selector.getSubjectPublicKey();
    }
    
    public boolean[] getKeyUsage()
    {
      return selector.getKeyUsage();
    }
    
    public Set<String> getExtendedKeyUsage()
    {
      return selector.getExtendedKeyUsage();
    }
    
    public boolean getMatchAllSubjectAltNames()
    {
      return selector.getMatchAllSubjectAltNames();
    }
    
    public Collection<List<?>> getSubjectAlternativeNames()
    {
      return selector.getSubjectAlternativeNames();
    }
    
    public byte[] getNameConstraints()
    {
      return selector.getNameConstraints();
    }
    
    public int getBasicConstraints()
    {
      return selector.getBasicConstraints();
    }
    
    public Set<String> getPolicy()
    {
      return selector.getPolicy();
    }
    
    public Collection<List<?>> getPathToNames()
    {
      return selector.getPathToNames();
    }
    
    public boolean match(Certificate paramCertificate)
    {
      selector.setSubject(certSubject);
      boolean bool = selector.match(paramCertificate);
      selector.setSubject(subject);
      return bool;
    }
  }
  
  private class LDAPRequest
  {
    private final String name;
    private Map<String, byte[][]> valueMap;
    private final List<String> requestedAttributes;
    
    LDAPRequest(String paramString)
    {
      name = paramString;
      requestedAttributes = new ArrayList(5);
    }
    
    String getName()
    {
      return name;
    }
    
    void addRequestedAttribute(String paramString)
    {
      if (valueMap != null) {
        throw new IllegalStateException("Request already sent");
      }
      requestedAttributes.add(paramString);
    }
    
    byte[][] getValues(String paramString)
      throws NamingException
    {
      String str = name + "|" + paramString;
      byte[][] arrayOfByte = (byte[][])valueCache.get(str);
      if (arrayOfByte != null)
      {
        LDAPCertStore.access$108(LDAPCertStore.this);
        return arrayOfByte;
      }
      LDAPCertStore.access$208(LDAPCertStore.this);
      Map localMap = getValueMap();
      arrayOfByte = (byte[][])localMap.get(paramString);
      return arrayOfByte;
    }
    
    private Map<String, byte[][]> getValueMap()
      throws NamingException
    {
      if (valueMap != null) {
        return valueMap;
      }
      valueMap = new HashMap(8);
      String[] arrayOfString = (String[])requestedAttributes.toArray(LDAPCertStore.STRING0);
      Attributes localAttributes;
      try
      {
        localAttributes = ctx.getAttributes(name, arrayOfString);
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        localAttributes = LDAPCertStore.EMPTY_ATTRIBUTES;
      }
      Iterator localIterator = requestedAttributes.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Attribute localAttribute = localAttributes.get(str);
        byte[][] arrayOfByte = getAttributeValues(localAttribute);
        cacheAttribute(str, arrayOfByte);
        valueMap.put(str, arrayOfByte);
      }
      return valueMap;
    }
    
    private void cacheAttribute(String paramString, byte[][] paramArrayOfByte)
    {
      String str = name + "|" + paramString;
      valueCache.put(str, paramArrayOfByte);
    }
    
    private byte[][] getAttributeValues(Attribute paramAttribute)
      throws NamingException
    {
      byte[][] arrayOfByte;
      if (paramAttribute == null)
      {
        arrayOfByte = LDAPCertStore.BB0;
      }
      else
      {
        arrayOfByte = new byte[paramAttribute.size()][];
        int i = 0;
        NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
        while (localNamingEnumeration.hasMore())
        {
          Object localObject = localNamingEnumeration.next();
          if ((LDAPCertStore.debug != null) && ((localObject instanceof String))) {
            LDAPCertStore.debug.println("LDAPCertStore.getAttrValues() enum.next is a string!: " + localObject);
          }
          byte[] arrayOfByte1 = (byte[])localObject;
          arrayOfByte[(i++)] = arrayOfByte1;
        }
      }
      return arrayOfByte;
    }
  }
  
  private static class SunLDAPCertStoreParameters
    extends LDAPCertStoreParameters
  {
    private volatile int hashCode = 0;
    
    SunLDAPCertStoreParameters(String paramString, int paramInt)
    {
      super(paramInt);
    }
    
    SunLDAPCertStoreParameters(String paramString)
    {
      super();
    }
    
    SunLDAPCertStoreParameters() {}
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof LDAPCertStoreParameters)) {
        return false;
      }
      LDAPCertStoreParameters localLDAPCertStoreParameters = (LDAPCertStoreParameters)paramObject;
      return (getPort() == localLDAPCertStoreParameters.getPort()) && (getServerName().equalsIgnoreCase(localLDAPCertStoreParameters.getServerName()));
    }
    
    public int hashCode()
    {
      if (hashCode == 0)
      {
        int i = 17;
        i = 37 * i + getPort();
        i = 37 * i + getServerName().toLowerCase(Locale.ENGLISH).hashCode();
        hashCode = i;
      }
      return hashCode;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\ldap\LDAPCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */