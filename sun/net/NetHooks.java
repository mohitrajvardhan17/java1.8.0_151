package sun.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;

public final class NetHooks
{
  public NetHooks() {}
  
  public static void beforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
    throws IOException
  {}
  
  public static void beforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\NetHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */