package sun.net.sdp;

import java.io.FileDescriptor;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;

public final class SdpSupport
{
  private static final String os = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
  private static final boolean isSupported = (os.equals("SunOS")) || (os.equals("Linux"));
  private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  private SdpSupport() {}
  
  public static FileDescriptor createSocket()
    throws IOException
  {
    if (!isSupported) {
      throw new UnsupportedOperationException("SDP not supported on this platform");
    }
    int i = create0();
    FileDescriptor localFileDescriptor = new FileDescriptor();
    fdAccess.set(localFileDescriptor, i);
    return localFileDescriptor;
  }
  
  public static void convertSocket(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    if (!isSupported) {
      throw new UnsupportedOperationException("SDP not supported on this platform");
    }
    int i = fdAccess.get(paramFileDescriptor);
    convert0(i);
  }
  
  private static native int create0()
    throws IOException;
  
  private static native void convert0(int paramInt)
    throws IOException;
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\sdp\SdpSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */