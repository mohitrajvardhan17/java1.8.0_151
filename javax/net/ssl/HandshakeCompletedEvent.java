package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.EventObject;

public class HandshakeCompletedEvent
  extends EventObject
{
  private static final long serialVersionUID = 7914963744257769778L;
  private transient SSLSession session;
  
  public HandshakeCompletedEvent(SSLSocket paramSSLSocket, SSLSession paramSSLSession)
  {
    super(paramSSLSocket);
    session = paramSSLSession;
  }
  
  public SSLSession getSession()
  {
    return session;
  }
  
  public String getCipherSuite()
  {
    return session.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates()
  {
    return session.getLocalCertificates();
  }
  
  public Certificate[] getPeerCertificates()
    throws SSLPeerUnverifiedException
  {
    return session.getPeerCertificates();
  }
  
  public javax.security.cert.X509Certificate[] getPeerCertificateChain()
    throws SSLPeerUnverifiedException
  {
    return session.getPeerCertificateChain();
  }
  
  public Principal getPeerPrincipal()
    throws SSLPeerUnverifiedException
  {
    Object localObject;
    try
    {
      localObject = session.getPeerPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      Certificate[] arrayOfCertificate = getPeerCertificates();
      localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
    }
    return (Principal)localObject;
  }
  
  public Principal getLocalPrincipal()
  {
    Object localObject;
    try
    {
      localObject = session.getLocalPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      localObject = null;
      Certificate[] arrayOfCertificate = getLocalCertificates();
      if (arrayOfCertificate != null) {
        localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
      }
    }
    return (Principal)localObject;
  }
  
  public SSLSocket getSocket()
  {
    return (SSLSocket)getSource();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\HandshakeCompletedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */