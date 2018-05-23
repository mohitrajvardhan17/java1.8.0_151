package java.nio.file;

import java.net.URI;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.List;

public final class Paths
{
  private Paths() {}
  
  public static Path get(String paramString, String... paramVarArgs)
  {
    return FileSystems.getDefault().getPath(paramString, paramVarArgs);
  }
  
  public static Path get(URI paramURI)
  {
    String str = paramURI.getScheme();
    if (str == null) {
      throw new IllegalArgumentException("Missing scheme");
    }
    if (str.equalsIgnoreCase("file")) {
      return FileSystems.getDefault().provider().getPath(paramURI);
    }
    Iterator localIterator = FileSystemProvider.installedProviders().iterator();
    while (localIterator.hasNext())
    {
      FileSystemProvider localFileSystemProvider = (FileSystemProvider)localIterator.next();
      if (localFileSystemProvider.getScheme().equalsIgnoreCase(str)) {
        return localFileSystemProvider.getPath(paramURI);
      }
    }
    throw new FileSystemNotFoundException("Provider \"" + str + "\" not installed");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\Paths.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */