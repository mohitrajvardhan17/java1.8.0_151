package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet4AddressImpl
  implements InetAddressImpl
{
  private InetAddress anyLocalAddress;
  private InetAddress loopbackAddress;
  
  Inet4AddressImpl() {}
  
  public native String getLocalHostName()
    throws UnknownHostException;
  
  public native InetAddress[] lookupAllHostAddr(String paramString)
    throws UnknownHostException;
  
  public native String getHostByAddr(byte[] paramArrayOfByte)
    throws UnknownHostException;
  
  private native boolean isReachable0(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2)
    throws IOException;
  
  public synchronized InetAddress anyLocalAddress()
  {
    if (anyLocalAddress == null)
    {
      anyLocalAddress = new Inet4Address();
      anyLocalAddress.holder().hostName = "0.0.0.0";
    }
    return anyLocalAddress;
  }
  
  public synchronized InetAddress loopbackAddress()
  {
    if (loopbackAddress == null)
    {
      byte[] arrayOfByte = { Byte.MAX_VALUE, 0, 0, 1 };
      loopbackAddress = new Inet4Address("localhost", arrayOfByte);
    }
    return loopbackAddress;
  }
  
  public boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2)
    throws IOException
  {
    byte[] arrayOfByte = null;
    if (paramNetworkInterface != null)
    {
      Enumeration localEnumeration = paramNetworkInterface.getInetAddresses();
      for (InetAddress localInetAddress = null; (!(localInetAddress instanceof Inet4Address)) && (localEnumeration.hasMoreElements()); localInetAddress = (InetAddress)localEnumeration.nextElement()) {}
      if ((localInetAddress instanceof Inet4Address)) {
        arrayOfByte = localInetAddress.getAddress();
      }
    }
    return isReachable0(paramInetAddress.getAddress(), paramInt1, arrayOfByte, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Inet4AddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */