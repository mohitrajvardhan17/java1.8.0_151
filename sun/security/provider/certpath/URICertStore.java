package sun.security.provider.certpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import sun.security.action.GetIntegerAction;
import sun.security.util.Cache;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AccessDescription;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.URIName;

class URICertStore
  extends CertStoreSpi
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final int CHECK_INTERVAL = 30000;
  private static final int CACHE_SIZE = 185;
  private final CertificateFactory factory;
  private Collection<X509Certificate> certs = Collections.emptySet();
  private X509CRL crl;
  private long lastChecked;
  private long lastModified;
  private URI uri;
  private boolean ldap = false;
  private CertStoreHelper ldapHelper;
  private CertStore ldapCertStore;
  private String ldapPath;
  private static final int DEFAULT_CRL_CONNECT_TIMEOUT = 15000;
  private static final int CRL_CONNECT_TIMEOUT = initializeTimeout();
  private static final Cache<URICertStoreParameters, CertStore> certStoreCache = Cache.newSoftMemoryCache(185);
  
  private static int initializeTimeout()
  {
    Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.crl.timeout"));
    if ((localInteger == null) || (localInteger.intValue() < 0)) {
      return 15000;
    }
    return localInteger.intValue() * 1000;
  }
  
  URICertStore(CertStoreParameters paramCertStoreParameters)
    throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
  {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof URICertStoreParameters)) {
      throw new InvalidAlgorithmParameterException("params must be instanceof URICertStoreParameters");
    }
    uri = uri;
    if (uri.getScheme().toLowerCase(Locale.ENGLISH).equals("ldap"))
    {
      ldap = true;
      ldapHelper = CertStoreHelper.getInstance("LDAP");
      ldapCertStore = ldapHelper.getCertStore(uri);
      ldapPath = uri.getPath();
      if (ldapPath.charAt(0) == '/') {
        ldapPath = ldapPath.substring(1);
      }
    }
    try
    {
      factory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException)
    {
      throw new RuntimeException();
    }
  }
  
  static synchronized CertStore getInstance(URICertStoreParameters paramURICertStoreParameters)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
  {
    if (debug != null) {
      debug.println("CertStore URI:" + uri);
    }
    Object localObject = (CertStore)certStoreCache.get(paramURICertStoreParameters);
    if (localObject == null)
    {
      localObject = new UCS(new URICertStore(paramURICertStoreParameters), null, "URI", paramURICertStoreParameters);
      certStoreCache.put(paramURICertStoreParameters, localObject);
    }
    else if (debug != null)
    {
      debug.println("URICertStore.getInstance: cache hit");
    }
    return (CertStore)localObject;
  }
  
  static CertStore getInstance(AccessDescription paramAccessDescription)
  {
    if (!paramAccessDescription.getAccessMethod().equals(AccessDescription.Ad_CAISSUERS_Id)) {
      return null;
    }
    GeneralNameInterface localGeneralNameInterface = paramAccessDescription.getAccessLocation().getName();
    if (!(localGeneralNameInterface instanceof URIName)) {
      return null;
    }
    URI localURI = ((URIName)localGeneralNameInterface).getURI();
    try
    {
      return getInstance(new URICertStoreParameters(localURI));
    }
    catch (Exception localException)
    {
      if (debug != null)
      {
        debug.println("exception creating CertStore: " + localException);
        localException.printStackTrace();
      }
    }
    return null;
  }
  
  public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
    throws CertStoreException
  {
    if (ldap)
    {
      X509CertSelector localX509CertSelector = (X509CertSelector)paramCertSelector;
      try
      {
        localX509CertSelector = ldapHelper.wrap(localX509CertSelector, localX509CertSelector.getSubject(), ldapPath);
      }
      catch (IOException localIOException1)
      {
        throw new CertStoreException(localIOException1);
      }
      return ldapCertStore.getCertificates(localX509CertSelector);
    }
    long l1 = System.currentTimeMillis();
    if (l1 - lastChecked < 30000L)
    {
      if (debug != null) {
        debug.println("Returning certificates from cache");
      }
      return getMatchingCerts(certs, paramCertSelector);
    }
    lastChecked = l1;
    try
    {
      URLConnection localURLConnection = uri.toURL().openConnection();
      if (lastModified != 0L) {
        localURLConnection.setIfModifiedSince(lastModified);
      }
      long l2 = lastModified;
      InputStream localInputStream = localURLConnection.getInputStream();
      Object localObject1 = null;
      try
      {
        lastModified = localURLConnection.getLastModified();
        if (l2 != 0L)
        {
          Object localObject2;
          if (l2 == lastModified)
          {
            if (debug != null) {
              debug.println("Not modified, using cached copy");
            }
            localObject2 = getMatchingCerts(certs, paramCertSelector);
            return (Collection<X509Certificate>)localObject2;
          }
          if ((localURLConnection instanceof HttpURLConnection))
          {
            localObject2 = (HttpURLConnection)localURLConnection;
            if (((HttpURLConnection)localObject2).getResponseCode() == 304)
            {
              if (debug != null) {
                debug.println("Not modified, using cached copy");
              }
              Collection localCollection = getMatchingCerts(certs, paramCertSelector);
              return localCollection;
            }
          }
        }
        if (debug != null) {
          debug.println("Downloading new certificates...");
        }
        certs = factory.generateCertificates(localInputStream);
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localInputStream.close();
            }
            catch (Throwable localThrowable5)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable5);
            }
          } else {
            localInputStream.close();
          }
        }
      }
      return getMatchingCerts(certs, paramCertSelector);
    }
    catch (IOException|CertificateException localIOException2)
    {
      if (debug != null)
      {
        debug.println("Exception fetching certificates:");
        localIOException2.printStackTrace();
      }
      lastModified = 0L;
      certs = Collections.emptySet();
    }
    return certs;
  }
  
  private static Collection<X509Certificate> getMatchingCerts(Collection<X509Certificate> paramCollection, CertSelector paramCertSelector)
  {
    if (paramCertSelector == null) {
      return paramCollection;
    }
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
      if (paramCertSelector.match(localX509Certificate)) {
        localArrayList.add(localX509Certificate);
      }
    }
    return localArrayList;
  }
  
  public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
    throws CertStoreException
  {
    if (ldap)
    {
      X509CRLSelector localX509CRLSelector = (X509CRLSelector)paramCRLSelector;
      try
      {
        localX509CRLSelector = ldapHelper.wrap(localX509CRLSelector, null, ldapPath);
      }
      catch (IOException localIOException1)
      {
        throw new CertStoreException(localIOException1);
      }
      try
      {
        return ldapCertStore.getCRLs(localX509CRLSelector);
      }
      catch (CertStoreException localCertStoreException)
      {
        throw new PKIX.CertStoreTypeException("LDAP", localCertStoreException);
      }
    }
    long l1 = System.currentTimeMillis();
    if (l1 - lastChecked < 30000L)
    {
      if (debug != null) {
        debug.println("Returning CRL from cache");
      }
      return getMatchingCRLs(crl, paramCRLSelector);
    }
    lastChecked = l1;
    try
    {
      URLConnection localURLConnection = uri.toURL().openConnection();
      if (lastModified != 0L) {
        localURLConnection.setIfModifiedSince(lastModified);
      }
      long l2 = lastModified;
      localURLConnection.setConnectTimeout(CRL_CONNECT_TIMEOUT);
      InputStream localInputStream = localURLConnection.getInputStream();
      Object localObject1 = null;
      try
      {
        lastModified = localURLConnection.getLastModified();
        if (l2 != 0L)
        {
          Object localObject2;
          if (l2 == lastModified)
          {
            if (debug != null) {
              debug.println("Not modified, using cached copy");
            }
            localObject2 = getMatchingCRLs(crl, paramCRLSelector);
            return (Collection<X509CRL>)localObject2;
          }
          if ((localURLConnection instanceof HttpURLConnection))
          {
            localObject2 = (HttpURLConnection)localURLConnection;
            if (((HttpURLConnection)localObject2).getResponseCode() == 304)
            {
              if (debug != null) {
                debug.println("Not modified, using cached copy");
              }
              Collection localCollection = getMatchingCRLs(crl, paramCRLSelector);
              return localCollection;
            }
          }
        }
        if (debug != null) {
          debug.println("Downloading new CRL...");
        }
        crl = ((X509CRL)factory.generateCRL(localInputStream));
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localInputStream.close();
            }
            catch (Throwable localThrowable5)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable5);
            }
          } else {
            localInputStream.close();
          }
        }
      }
      return getMatchingCRLs(crl, paramCRLSelector);
    }
    catch (IOException|CRLException localIOException2)
    {
      if (debug != null)
      {
        debug.println("Exception fetching CRL:");
        localIOException2.printStackTrace();
      }
      lastModified = 0L;
      crl = null;
      throw new PKIX.CertStoreTypeException("URI", new CertStoreException(localIOException2));
    }
  }
  
  private static Collection<X509CRL> getMatchingCRLs(X509CRL paramX509CRL, CRLSelector paramCRLSelector)
  {
    if ((paramCRLSelector == null) || ((paramX509CRL != null) && (paramCRLSelector.match(paramX509CRL)))) {
      return Collections.singletonList(paramX509CRL);
    }
    return Collections.emptyList();
  }
  
  private static class UCS
    extends CertStore
  {
    protected UCS(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters)
    {
      super(paramProvider, paramString, paramCertStoreParameters);
    }
  }
  
  static class URICertStoreParameters
    implements CertStoreParameters
  {
    private final URI uri;
    private volatile int hashCode = 0;
    
    URICertStoreParameters(URI paramURI)
    {
      uri = paramURI;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof URICertStoreParameters)) {
        return false;
      }
      URICertStoreParameters localURICertStoreParameters = (URICertStoreParameters)paramObject;
      return uri.equals(uri);
    }
    
    public int hashCode()
    {
      if (hashCode == 0)
      {
        int i = 17;
        i = 37 * i + uri.hashCode();
        hashCode = i;
      }
      return hashCode;
    }
    
    public Object clone()
    {
      try
      {
        return super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\URICertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */