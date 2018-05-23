package sun.misc;

import java.io.File;
import java.net.URL;
import sun.net.www.ParseUtil;

public class FileURLMapper
{
  URL url;
  String file;
  
  public FileURLMapper(URL paramURL)
  {
    url = paramURL;
  }
  
  public String getPath()
  {
    if (file != null) {
      return file;
    }
    String str1 = url.getHost();
    if ((str1 != null) && (!str1.equals("")) && (!"localhost".equalsIgnoreCase(str1)))
    {
      str2 = url.getFile();
      String str3 = str1 + ParseUtil.decode(url.getFile());
      file = ("\\\\" + str3.replace('/', '\\'));
      return file;
    }
    String str2 = url.getFile().replace('/', '\\');
    file = ParseUtil.decode(str2);
    return file;
  }
  
  public boolean exists()
  {
    String str = getPath();
    File localFile = new File(str);
    return localFile.exists();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\FileURLMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */