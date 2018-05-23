package java.net;

public final class StandardSocketOptions
{
  public static final SocketOption<Boolean> SO_BROADCAST = new StdSocketOption("SO_BROADCAST", Boolean.class);
  public static final SocketOption<Boolean> SO_KEEPALIVE = new StdSocketOption("SO_KEEPALIVE", Boolean.class);
  public static final SocketOption<Integer> SO_SNDBUF = new StdSocketOption("SO_SNDBUF", Integer.class);
  public static final SocketOption<Integer> SO_RCVBUF = new StdSocketOption("SO_RCVBUF", Integer.class);
  public static final SocketOption<Boolean> SO_REUSEADDR = new StdSocketOption("SO_REUSEADDR", Boolean.class);
  public static final SocketOption<Integer> SO_LINGER = new StdSocketOption("SO_LINGER", Integer.class);
  public static final SocketOption<Integer> IP_TOS = new StdSocketOption("IP_TOS", Integer.class);
  public static final SocketOption<NetworkInterface> IP_MULTICAST_IF = new StdSocketOption("IP_MULTICAST_IF", NetworkInterface.class);
  public static final SocketOption<Integer> IP_MULTICAST_TTL = new StdSocketOption("IP_MULTICAST_TTL", Integer.class);
  public static final SocketOption<Boolean> IP_MULTICAST_LOOP = new StdSocketOption("IP_MULTICAST_LOOP", Boolean.class);
  public static final SocketOption<Boolean> TCP_NODELAY = new StdSocketOption("TCP_NODELAY", Boolean.class);
  
  private StandardSocketOptions() {}
  
  private static class StdSocketOption<T>
    implements SocketOption<T>
  {
    private final String name;
    private final Class<T> type;
    
    StdSocketOption(String paramString, Class<T> paramClass)
    {
      name = paramString;
      type = paramClass;
    }
    
    public String name()
    {
      return name;
    }
    
    public Class<T> type()
    {
      return type;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\StandardSocketOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */