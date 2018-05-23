package java.net;

import java.io.IOException;

abstract interface InetAddressImpl
{
  public abstract String getLocalHostName()
    throws UnknownHostException;
  
  public abstract InetAddress[] lookupAllHostAddr(String paramString)
    throws UnknownHostException;
  
  public abstract String getHostByAddr(byte[] paramArrayOfByte)
    throws UnknownHostException;
  
  public abstract InetAddress anyLocalAddress();
  
  public abstract InetAddress loopbackAddress();
  
  public abstract boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\InetAddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */