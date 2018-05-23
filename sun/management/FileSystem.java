package sun.management;

import java.io.File;
import java.io.IOException;

public abstract class FileSystem
{
  private static final Object lock = new Object();
  private static FileSystem fs;
  
  protected FileSystem() {}
  
  public static FileSystem open()
  {
    synchronized (lock)
    {
      if (fs == null) {
        fs = new FileSystemImpl();
      }
      return fs;
    }
  }
  
  public abstract boolean supportsFileSecurity(File paramFile)
    throws IOException;
  
  public abstract boolean isAccessUserOnly(File paramFile)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\FileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */