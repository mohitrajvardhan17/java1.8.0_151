package sun.management;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class FileSystemImpl
  extends FileSystem
{
  public FileSystemImpl() {}
  
  public boolean supportsFileSecurity(File paramFile)
    throws IOException
  {
    return isSecuritySupported0(paramFile.getAbsolutePath());
  }
  
  public boolean isAccessUserOnly(File paramFile)
    throws IOException
  {
    String str = paramFile.getAbsolutePath();
    if (!isSecuritySupported0(str)) {
      throw new UnsupportedOperationException("File system does not support file security");
    }
    return isAccessUserOnly0(str);
  }
  
  static native void init0();
  
  static native boolean isSecuritySupported0(String paramString)
    throws IOException;
  
  static native boolean isAccessUserOnly0(String paramString)
    throws IOException;
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("management");
        return null;
      }
    });
    init0();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\FileSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */