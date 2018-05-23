package sun.security.krb5.internal;

import java.io.IOException;

public abstract class NetClient
  implements AutoCloseable
{
  public NetClient() {}
  
  public static NetClient getInstance(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramString1.equals("TCP")) {
      return new TCPClient(paramString2, paramInt1, paramInt2);
    }
    return new UDPClient(paramString2, paramInt1, paramInt2);
  }
  
  public abstract void send(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract byte[] receive()
    throws IOException;
  
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\NetClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */