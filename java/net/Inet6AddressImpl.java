package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet6AddressImpl
  implements InetAddressImpl
{
  private InetAddress anyLocalAddress;
  private InetAddress loopbackAddress;
  
  Inet6AddressImpl() {}
  
  public native String getLocalHostName()
    throws UnknownHostException;
  
  public native InetAddress[] lookupAllHostAddr(String paramString)
    throws UnknownHostException;
  
  public native String getHostByAddr(byte[] paramArrayOfByte)
    throws UnknownHostException;
  
  private native boolean isReachable0(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
    throws IOException;
  
  public boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2)
    throws IOException
  {
    byte[] arrayOfByte = null;
    int i = -1;
    int j = -1;
    if (paramNetworkInterface != null)
    {
      Enumeration localEnumeration = paramNetworkInterface.getInetAddresses();
      InetAddress localInetAddress = null;
      while (localEnumeration.hasMoreElements())
      {
        localInetAddress = (InetAddress)localEnumeration.nextElement();
        if (localInetAddress.getClass().isInstance(paramInetAddress))
        {
          arrayOfByte = localInetAddress.getAddress();
          if ((localInetAddress instanceof Inet6Address)) {
            j = ((Inet6Address)localInetAddress).getScopeId();
          }
        }
      }
      if (arrayOfByte == null) {
        return false;
      }
    }
    if ((paramInetAddress instanceof Inet6Address)) {
      i = ((Inet6Address)paramInetAddress).getScopeId();
    }
    return isReachable0(paramInetAddress.getAddress(), i, paramInt1, arrayOfByte, paramInt2, j);
  }
  
  public synchronized InetAddress anyLocalAddress()
  {
    if (anyLocalAddress == null) {
      if (InetAddress.preferIPv6Address)
      {
        anyLocalAddress = new Inet6Address();
        anyLocalAddress.holder().hostName = "::";
      }
      else
      {
        anyLocalAddress = new Inet4AddressImpl().anyLocalAddress();
      }
    }
    return anyLocalAddress;
  }
  
  public synchronized InetAddress loopbackAddress()
  {
    if (loopbackAddress == null) {
      if (InetAddress.preferIPv6Address)
      {
        byte[] arrayOfByte = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
        loopbackAddress = new Inet6Address("localhost", arrayOfByte);
      }
      else
      {
        loopbackAddress = new Inet4AddressImpl().loopbackAddress();
      }
    }
    return loopbackAddress;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Inet6AddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */