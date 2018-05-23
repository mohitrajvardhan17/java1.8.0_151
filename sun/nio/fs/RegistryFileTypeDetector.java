package sun.nio.fs;

import java.io.IOException;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class RegistryFileTypeDetector
  extends AbstractFileTypeDetector
{
  public RegistryFileTypeDetector() {}
  
  public String implProbeContentType(Path paramPath)
    throws IOException
  {
    if (!(paramPath instanceof Path)) {
      return null;
    }
    Path localPath = paramPath.getFileName();
    if (localPath == null) {
      return null;
    }
    String str1 = localPath.toString();
    int i = str1.lastIndexOf('.');
    if ((i < 0) || (i == str1.length() - 1)) {
      return null;
    }
    String str2 = str1.substring(i);
    NativeBuffer localNativeBuffer1 = WindowsNativeDispatcher.asNativeBuffer(str2);
    NativeBuffer localNativeBuffer2 = WindowsNativeDispatcher.asNativeBuffer("Content Type");
    try
    {
      String str3 = queryStringValue(localNativeBuffer1.address(), localNativeBuffer2.address());
      return str3;
    }
    finally
    {
      localNativeBuffer2.release();
      localNativeBuffer1.release();
    }
  }
  
  private static native String queryStringValue(long paramLong1, long paramLong2);
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        System.loadLibrary("nio");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\RegistryFileTypeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */