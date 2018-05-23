package sun.net.www;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;

class MimeLauncher
  extends Thread
{
  URLConnection uc;
  MimeEntry m;
  String genericTempFileTemplate;
  InputStream is;
  String execPath;
  
  MimeLauncher(MimeEntry paramMimeEntry, URLConnection paramURLConnection, InputStream paramInputStream, String paramString1, String paramString2)
    throws ApplicationLaunchException
  {
    super(paramString2);
    m = paramMimeEntry;
    uc = paramURLConnection;
    is = paramInputStream;
    genericTempFileTemplate = paramString1;
    String str1 = m.getLaunchString();
    if (!findExecutablePath(str1))
    {
      int i = str1.indexOf(' ');
      String str2;
      if (i != -1) {
        str2 = str1.substring(0, i);
      } else {
        str2 = str1;
      }
      throw new ApplicationLaunchException(str2);
    }
  }
  
  protected String getTempFileName(URL paramURL, String paramString)
  {
    String str1 = paramString;
    int i = str1.lastIndexOf("%s");
    String str2 = str1.substring(0, i);
    String str3 = "";
    if (i < str1.length() - 2) {
      str3 = str1.substring(i + 2);
    }
    long l = System.currentTimeMillis() / 1000L;
    int j = 0;
    while ((j = str2.indexOf("%s")) >= 0) {
      str2 = str2.substring(0, j) + l + str2.substring(j + 2);
    }
    String str4 = paramURL.getFile();
    String str5 = "";
    int k = str4.lastIndexOf('.');
    if ((k >= 0) && (k > str4.lastIndexOf('/'))) {
      str5 = str4.substring(k);
    }
    str4 = "HJ" + paramURL.hashCode();
    str1 = str2 + str4 + l + str5 + str3;
    return str1;
  }
  
  public void run()
  {
    try
    {
      String str = m.getTempFileTemplate();
      if (str == null) {
        str = genericTempFileTemplate;
      }
      str = getTempFileName(uc.getURL(), str);
      try
      {
        FileOutputStream localFileOutputStream = new FileOutputStream(str);
        localObject1 = new byte['ࠀ'];
        j = 0;
        try
        {
          while ((j = is.read((byte[])localObject1)) >= 0) {
            localFileOutputStream.write((byte[])localObject1, 0, j);
          }
        }
        catch (IOException localIOException3) {}finally
        {
          localFileOutputStream.close();
          is.close();
        }
      }
      catch (IOException localIOException2) {}
      int i = 0;
      for (Object localObject1 = execPath; (i = ((String)localObject1).indexOf("%t")) >= 0; localObject1 = ((String)localObject1).substring(0, i) + uc.getContentType() + ((String)localObject1).substring(i + 2)) {}
      for (int j = 0; (i = ((String)localObject1).indexOf("%s")) >= 0; j = 1) {
        localObject1 = ((String)localObject1).substring(0, i) + str + ((String)localObject1).substring(i + 2);
      }
      if (j == 0) {
        localObject1 = (String)localObject1 + " <" + str;
      }
      Runtime.getRuntime().exec((String)localObject1);
    }
    catch (IOException localIOException1) {}
  }
  
  private boolean findExecutablePath(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return false;
    }
    int i = paramString.indexOf(' ');
    String str1;
    if (i != -1) {
      str1 = paramString.substring(0, i);
    } else {
      str1 = paramString;
    }
    File localFile = new File(str1);
    if (localFile.isFile())
    {
      execPath = paramString;
      return true;
    }
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("exec.path"));
    if (str2 == null) {
      return false;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(str2, "|");
    while (localStringTokenizer.hasMoreElements())
    {
      String str3 = (String)localStringTokenizer.nextElement();
      String str4 = str3 + File.separator + str1;
      localFile = new File(str4);
      if (localFile.isFile())
      {
        execPath = (str3 + File.separator + paramString);
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\MimeLauncher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */