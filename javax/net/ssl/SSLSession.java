package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import javax.security.cert.X509Certificate;

public abstract interface SSLSession
{
  public abstract byte[] getId();
  
  public abstract SSLSessionContext getSessionContext();
  
  public abstract long getCreationTime();
  
  public abstract long getLastAccessedTime();
  
  public abstract void invalidate();
  
  public abstract boolean isValid();
  
  public abstract void putValue(String paramString, Object paramObject);
  
  public abstract Object getValue(String paramString);
  
  public abstract void removeValue(String paramString);
  
  public abstract String[] getValueNames();
  
  public abstract Certificate[] getPeerCertificates()
    throws SSLPeerUnverifiedException;
  
  public abstract Certificate[] getLocalCertificates();
  
  public abstract X509Certificate[] getPeerCertificateChain()
    throws SSLPeerUnverifiedException;
  
  public abstract Principal getPeerPrincipal()
    throws SSLPeerUnverifiedException;
  
  public abstract Principal getLocalPrincipal();
  
  public abstract String getCipherSuite();
  
  public abstract String getProtocol();
  
  public abstract String getPeerHost();
  
  public abstract int getPeerPort();
  
  public abstract int getPacketBufferSize();
  
  public abstract int getApplicationBufferSize();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */