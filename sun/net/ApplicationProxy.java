package sun.net;

import java.net.Proxy;

public final class ApplicationProxy
  extends Proxy
{
  private ApplicationProxy(Proxy paramProxy)
  {
    super(paramProxy.type(), paramProxy.address());
  }
  
  public static ApplicationProxy create(Proxy paramProxy)
  {
    return new ApplicationProxy(paramProxy);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ApplicationProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */