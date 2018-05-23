package sun.net.www.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.net.NetProperties;
import sun.util.logging.PlatformLogger;

public class HttpCapture
{
  private File file = null;
  private boolean incoming = true;
  private BufferedWriter out = null;
  private static boolean initialized = false;
  private static volatile ArrayList<Pattern> patterns = null;
  private static volatile ArrayList<String> capFiles = null;
  
  private static synchronized void init()
  {
    initialized = true;
    String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return NetProperties.get("sun.net.http.captureRules");
      }
    });
    if ((str1 != null) && (!str1.isEmpty()))
    {
      BufferedReader localBufferedReader;
      try
      {
        localBufferedReader = new BufferedReader(new FileReader(str1));
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        return;
      }
      try
      {
        for (String str2 = localBufferedReader.readLine(); str2 != null; str2 = localBufferedReader.readLine())
        {
          str2 = str2.trim();
          if (!str2.startsWith("#"))
          {
            String[] arrayOfString = str2.split(",");
            if (arrayOfString.length == 2)
            {
              if (patterns == null)
              {
                patterns = new ArrayList();
                capFiles = new ArrayList();
              }
              patterns.add(Pattern.compile(arrayOfString[0].trim()));
              capFiles.add(arrayOfString[1].trim());
            }
          }
        }
        return;
      }
      catch (IOException localIOException2) {}finally
      {
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException4) {}
      }
    }
  }
  
  private static synchronized boolean isInitialized()
  {
    return initialized;
  }
  
  private HttpCapture(File paramFile, URL paramURL)
  {
    file = paramFile;
    try
    {
      out = new BufferedWriter(new FileWriter(file, true));
      out.write("URL: " + paramURL + "\n");
    }
    catch (IOException localIOException)
    {
      PlatformLogger.getLogger(HttpCapture.class.getName()).severe(null, localIOException);
    }
  }
  
  public synchronized void sent(int paramInt)
    throws IOException
  {
    if (incoming)
    {
      out.write("\n------>\n");
      incoming = false;
      out.flush();
    }
    out.write(paramInt);
  }
  
  public synchronized void received(int paramInt)
    throws IOException
  {
    if (!incoming)
    {
      out.write("\n<------\n");
      incoming = true;
      out.flush();
    }
    out.write(paramInt);
  }
  
  public synchronized void flush()
    throws IOException
  {
    out.flush();
  }
  
  public static HttpCapture getCapture(URL paramURL)
  {
    if (!isInitialized()) {
      init();
    }
    if ((patterns == null) || (patterns.isEmpty())) {
      return null;
    }
    String str1 = paramURL.toString();
    for (int i = 0; i < patterns.size(); i++)
    {
      Pattern localPattern = (Pattern)patterns.get(i);
      if (localPattern.matcher(str1).find())
      {
        String str2 = (String)capFiles.get(i);
        File localFile;
        if (str2.indexOf("%d") >= 0)
        {
          Random localRandom = new Random();
          do
          {
            String str3 = str2.replace("%d", Integer.toString(localRandom.nextInt()));
            localFile = new File(str3);
          } while (localFile.exists());
        }
        else
        {
          localFile = new File(str2);
        }
        return new HttpCapture(localFile, paramURL);
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\HttpCapture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */