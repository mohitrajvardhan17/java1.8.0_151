package java.net;

class InetAddressImplFactory
{
  InetAddressImplFactory() {}
  
  static InetAddressImpl create()
  {
    return InetAddress.loadImpl(isIPv6Supported() ? "Inet6AddressImpl" : "Inet4AddressImpl");
  }
  
  static native boolean isIPv6Supported();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\InetAddressImplFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */