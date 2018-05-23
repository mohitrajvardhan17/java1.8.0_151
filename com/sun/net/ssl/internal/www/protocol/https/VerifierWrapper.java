package com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import sun.security.util.DerValue;
import sun.security.util.HostnameChecker;
import sun.security.x509.X500Name;

class VerifierWrapper
  implements javax.net.ssl.HostnameVerifier
{
  private com.sun.net.ssl.HostnameVerifier verifier;
  
  VerifierWrapper(com.sun.net.ssl.HostnameVerifier paramHostnameVerifier)
  {
    verifier = paramHostnameVerifier;
  }
  
  public boolean verify(String paramString, SSLSession paramSSLSession)
  {
    try
    {
      String str;
      if (paramSSLSession.getCipherSuite().startsWith("TLS_KRB5"))
      {
        str = HostnameChecker.getServerName(getPeerPrincipal(paramSSLSession));
      }
      else
      {
        Certificate[] arrayOfCertificate = paramSSLSession.getPeerCertificates();
        if ((arrayOfCertificate == null) || (arrayOfCertificate.length == 0)) {
          return false;
        }
        if (!(arrayOfCertificate[0] instanceof X509Certificate)) {
          return false;
        }
        X509Certificate localX509Certificate = (X509Certificate)arrayOfCertificate[0];
        str = getServername(localX509Certificate);
      }
      if (str == null) {
        return false;
      }
      return verifier.verify(paramString, str);
    }
    catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {}
    return false;
  }
  
  private Principal getPeerPrincipal(SSLSession paramSSLSession)
    throws SSLPeerUnverifiedException
  {
    Principal localPrincipal;
    try
    {
      localPrincipal = paramSSLSession.getPeerPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      localPrincipal = null;
    }
    return localPrincipal;
  }
  
  private static String getServername(X509Certificate paramX509Certificate)
  {
    try
    {
      Collection localCollection = paramX509Certificate.getSubjectAlternativeNames();
      String str;
      if (localCollection != null)
      {
        localObject1 = localCollection.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (List)((Iterator)localObject1).next();
          if (((Integer)((List)localObject2).get(0)).intValue() == 2)
          {
            str = (String)((List)localObject2).get(1);
            return str;
          }
        }
      }
      Object localObject1 = HostnameChecker.getSubjectX500Name(paramX509Certificate);
      Object localObject2 = ((X500Name)localObject1).findMostSpecificAttribute(X500Name.commonName_oid);
      if (localObject2 != null) {
        try
        {
          str = ((DerValue)localObject2).getAsString();
          return str;
        }
        catch (IOException localIOException) {}
      }
    }
    catch (CertificateException localCertificateException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\internal\www\protocol\https\VerifierWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */