package sun.nio.ch;

import java.net.ProtocolFamily;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.util.HashMap;
import java.util.Map;

class SocketOptionRegistry
{
  private SocketOptionRegistry() {}
  
  public static OptionKey findOption(SocketOption<?> paramSocketOption, ProtocolFamily paramProtocolFamily)
  {
    RegistryKey localRegistryKey = new RegistryKey(paramSocketOption, paramProtocolFamily);
    return (OptionKey)LazyInitialization.options.get(localRegistryKey);
  }
  
  private static class LazyInitialization
  {
    static final Map<SocketOptionRegistry.RegistryKey, OptionKey> options = ;
    
    private LazyInitialization() {}
    
    private static Map<SocketOptionRegistry.RegistryKey, OptionKey> options()
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_BROADCAST, Net.UNSPEC), new OptionKey(65535, 32));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_KEEPALIVE, Net.UNSPEC), new OptionKey(65535, 8));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_LINGER, Net.UNSPEC), new OptionKey(65535, 128));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_SNDBUF, Net.UNSPEC), new OptionKey(65535, 4097));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_RCVBUF, Net.UNSPEC), new OptionKey(65535, 4098));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_REUSEADDR, Net.UNSPEC), new OptionKey(65535, 4));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.TCP_NODELAY, Net.UNSPEC), new OptionKey(6, 1));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET), new OptionKey(0, 3));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET), new OptionKey(0, 9));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET), new OptionKey(0, 10));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET), new OptionKey(0, 11));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET6), new OptionKey(41, 39));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET6), new OptionKey(41, 9));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET6), new OptionKey(41, 10));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET6), new OptionKey(41, 11));
      localHashMap.put(new SocketOptionRegistry.RegistryKey(ExtendedSocketOption.SO_OOBINLINE, Net.UNSPEC), new OptionKey(65535, 256));
      return localHashMap;
    }
  }
  
  private static class RegistryKey
  {
    private final SocketOption<?> name;
    private final ProtocolFamily family;
    
    RegistryKey(SocketOption<?> paramSocketOption, ProtocolFamily paramProtocolFamily)
    {
      name = paramSocketOption;
      family = paramProtocolFamily;
    }
    
    public int hashCode()
    {
      return name.hashCode() + family.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (!(paramObject instanceof RegistryKey)) {
        return false;
      }
      RegistryKey localRegistryKey = (RegistryKey)paramObject;
      if (name != name) {
        return false;
      }
      return family == family;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\SocketOptionRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */