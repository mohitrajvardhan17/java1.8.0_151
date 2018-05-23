package sun.nio.fs;

import java.nio.file.spi.FileSystemProvider;

public class DefaultFileSystemProvider
{
  private DefaultFileSystemProvider() {}
  
  public static FileSystemProvider create()
  {
    return new WindowsFileSystemProvider();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\DefaultFileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */