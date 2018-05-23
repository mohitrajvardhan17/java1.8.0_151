package javax.net.ssl;

import java.util.Enumeration;

public abstract interface SSLSessionContext
{
  public abstract SSLSession getSession(byte[] paramArrayOfByte);
  
  public abstract Enumeration<byte[]> getIds();
  
  public abstract void setSessionTimeout(int paramInt)
    throws IllegalArgumentException;
  
  public abstract int getSessionTimeout();
  
  public abstract void setSessionCacheSize(int paramInt)
    throws IllegalArgumentException;
  
  public abstract int getSessionCacheSize();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLSessionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */