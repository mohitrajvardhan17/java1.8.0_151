package com.sun.jndi.ldap.ext;

import com.sun.jndi.ldap.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.security.util.HostnameChecker;

public final class StartTlsResponseImpl
  extends StartTlsResponse
{
  private static final boolean debug = false;
  private static final int DNSNAME_TYPE = 2;
  private transient String hostname = null;
  private transient Connection ldapConnection = null;
  private transient InputStream originalInputStream = null;
  private transient OutputStream originalOutputStream = null;
  private transient SSLSocket sslSocket = null;
  private transient SSLSocketFactory defaultFactory = null;
  private transient SSLSocketFactory currentFactory = null;
  private transient String[] suites = null;
  private transient HostnameVerifier verifier = null;
  private transient boolean isClosed = true;
  private static final long serialVersionUID = -1126624615143411328L;
  
  public StartTlsResponseImpl() {}
  
  public void setEnabledCipherSuites(String[] paramArrayOfString)
  {
    suites = (paramArrayOfString == null ? null : (String[])paramArrayOfString.clone());
  }
  
  public void setHostnameVerifier(HostnameVerifier paramHostnameVerifier)
  {
    verifier = paramHostnameVerifier;
  }
  
  public SSLSession negotiate()
    throws IOException
  {
    return negotiate(null);
  }
  
  public SSLSession negotiate(SSLSocketFactory paramSSLSocketFactory)
    throws IOException
  {
    if ((isClosed) && (sslSocket != null)) {
      throw new IOException("TLS connection is closed.");
    }
    if (paramSSLSocketFactory == null) {
      paramSSLSocketFactory = getDefaultFactory();
    }
    SSLSession localSSLSession = startHandshake(paramSSLSocketFactory).getSession();
    Object localObject = null;
    try
    {
      if (verify(hostname, localSSLSession))
      {
        isClosed = false;
        return localSSLSession;
      }
    }
    catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException)
    {
      localObject = localSSLPeerUnverifiedException;
    }
    if ((verifier != null) && (verifier.verify(hostname, localSSLSession)))
    {
      isClosed = false;
      return localSSLSession;
    }
    close();
    localSSLSession.invalidate();
    if (localObject == null) {
      localObject = new SSLPeerUnverifiedException("hostname of the server '" + hostname + "' does not match the hostname in the server's certificate.");
    }
    throw ((Throwable)localObject);
  }
  
  public void close()
    throws IOException
  {
    if (isClosed) {
      return;
    }
    ldapConnection.replaceStreams(originalInputStream, originalOutputStream);
    sslSocket.close();
    isClosed = true;
  }
  
  public void setConnection(Connection paramConnection, String paramString)
  {
    ldapConnection = paramConnection;
    hostname = (paramString != null ? paramString : host);
    originalInputStream = inStream;
    originalOutputStream = outStream;
  }
  
  private SSLSocketFactory getDefaultFactory()
    throws IOException
  {
    if (defaultFactory != null) {
      return defaultFactory;
    }
    return defaultFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
  }
  
  private SSLSocket startHandshake(SSLSocketFactory paramSSLSocketFactory)
    throws IOException
  {
    if (ldapConnection == null) {
      throw new IllegalStateException("LDAP connection has not been set. TLS requires an existing LDAP connection.");
    }
    if (paramSSLSocketFactory != currentFactory)
    {
      sslSocket = ((SSLSocket)paramSSLSocketFactory.createSocket(ldapConnection.sock, ldapConnection.host, ldapConnection.port, false));
      currentFactory = paramSSLSocketFactory;
    }
    if (suites != null) {
      sslSocket.setEnabledCipherSuites(suites);
    }
    try
    {
      sslSocket.startHandshake();
      ldapConnection.replaceStreams(sslSocket.getInputStream(), sslSocket.getOutputStream());
    }
    catch (IOException localIOException)
    {
      sslSocket.close();
      isClosed = true;
      throw localIOException;
    }
    return sslSocket;
  }
  
  private boolean verify(String paramString, SSLSession paramSSLSession)
    throws SSLPeerUnverifiedException
  {
    Certificate[] arrayOfCertificate = null;
    if ((paramString != null) && (paramString.startsWith("[")) && (paramString.endsWith("]"))) {
      paramString = paramString.substring(1, paramString.length() - 1);
    }
    try
    {
      HostnameChecker localHostnameChecker = HostnameChecker.getInstance((byte)2);
      if (paramSSLSession.getCipherSuite().startsWith("TLS_KRB5"))
      {
        localObject = getPeerPrincipal(paramSSLSession);
        if (!HostnameChecker.match(paramString, (Principal)localObject)) {
          throw new SSLPeerUnverifiedException("hostname of the kerberos principal:" + localObject + " does not match the hostname:" + paramString);
        }
      }
      else
      {
        arrayOfCertificate = paramSSLSession.getPeerCertificates();
        if ((arrayOfCertificate[0] instanceof X509Certificate)) {
          localObject = (X509Certificate)arrayOfCertificate[0];
        } else {
          throw new SSLPeerUnverifiedException("Received a non X509Certificate from the server");
        }
        localHostnameChecker.match(paramString, (X509Certificate)localObject);
      }
      return true;
    }
    catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException)
    {
      Object localObject = paramSSLSession.getCipherSuite();
      if ((localObject != null) && (((String)localObject).indexOf("_anon_") != -1)) {
        return true;
      }
      throw localSSLPeerUnverifiedException;
    }
    catch (CertificateException localCertificateException)
    {
      throw ((SSLPeerUnverifiedException)new SSLPeerUnverifiedException("hostname of the server '" + paramString + "' does not match the hostname in the server's certificate.").initCause(localCertificateException));
    }
  }
  
  private static Principal getPeerPrincipal(SSLSession paramSSLSession)
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\ext\StartTlsResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */