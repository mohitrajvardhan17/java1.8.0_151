package java.net;

public class Proxy
{
  private Type type;
  private SocketAddress sa;
  public static final Proxy NO_PROXY = new Proxy();
  
  private Proxy()
  {
    type = Type.DIRECT;
    sa = null;
  }
  
  public Proxy(Type paramType, SocketAddress paramSocketAddress)
  {
    if ((paramType == Type.DIRECT) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("type " + paramType + " is not compatible with address " + paramSocketAddress);
    }
    type = paramType;
    sa = paramSocketAddress;
  }
  
  public Type type()
  {
    return type;
  }
  
  public SocketAddress address()
  {
    return sa;
  }
  
  public String toString()
  {
    if (type() == Type.DIRECT) {
      return "DIRECT";
    }
    return type() + " @ " + address();
  }
  
  public final boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Proxy))) {
      return false;
    }
    Proxy localProxy = (Proxy)paramObject;
    if (localProxy.type() == type())
    {
      if (address() == null) {
        return localProxy.address() == null;
      }
      return address().equals(localProxy.address());
    }
    return false;
  }
  
  public final int hashCode()
  {
    if (address() == null) {
      return type().hashCode();
    }
    return type().hashCode() + address().hashCode();
  }
  
  public static enum Type
  {
    DIRECT,  HTTP,  SOCKS;
    
    private Type() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Proxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */