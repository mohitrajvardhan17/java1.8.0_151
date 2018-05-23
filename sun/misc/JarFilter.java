package sun.misc;

import java.io.File;
import java.io.FilenameFilter;

public class JarFilter
  implements FilenameFilter
{
  public JarFilter() {}
  
  public boolean accept(File paramFile, String paramString)
  {
    String str = paramString.toLowerCase();
    return (str.endsWith(".jar")) || (str.endsWith(".zip"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JarFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */