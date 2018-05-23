package sun.net;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;

public final class SocksProxy
  extends Proxy
{
  private final int version;
  
  private SocksProxy(SocketAddress paramSocketAddress, int paramInt)
  {
    super(Proxy.Type.SOCKS, paramSocketAddress);
    version = paramInt;
  }
  
  public static SocksProxy create(SocketAddress paramSocketAddress, int paramInt)
  {
    return new SocksProxy(paramSocketAddress, paramInt);
  }
  
  public int protocolVersion()
  {
    return version;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\SocksProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */