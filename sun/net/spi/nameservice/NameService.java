package sun.net.spi.nameservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract interface NameService
{
  public abstract InetAddress[] lookupAllHostAddr(String paramString)
    throws UnknownHostException;
  
  public abstract String getHostByAddr(byte[] paramArrayOfByte)
    throws UnknownHostException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\spi\nameservice\NameService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */