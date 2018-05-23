package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MimeTypeFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

public class MimetypesFileTypeMap
  extends FileTypeMap
{
  private MimeTypeFile[] DB;
  private static final int PROG = 0;
  private static String defaultType = "application/octet-stream";
  
  public MimetypesFileTypeMap()
  {
    Vector localVector = new Vector(5);
    MimeTypeFile localMimeTypeFile = null;
    localVector.addElement(null);
    LogSupport.log("MimetypesFileTypeMap: load HOME");
    try
    {
      String str1 = System.getProperty("user.home");
      if (str1 != null)
      {
        String str3 = str1 + File.separator + ".mime.types";
        localMimeTypeFile = loadFile(str3);
        if (localMimeTypeFile != null) {
          localVector.addElement(localMimeTypeFile);
        }
      }
    }
    catch (SecurityException localSecurityException1) {}
    LogSupport.log("MimetypesFileTypeMap: load SYS");
    try
    {
      String str2 = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mime.types";
      localMimeTypeFile = loadFile(str2);
      if (localMimeTypeFile != null) {
        localVector.addElement(localMimeTypeFile);
      }
    }
    catch (SecurityException localSecurityException2) {}
    LogSupport.log("MimetypesFileTypeMap: load JAR");
    loadAllResources(localVector, "META-INF/mime.types");
    LogSupport.log("MimetypesFileTypeMap: load DEF");
    localMimeTypeFile = loadResource("/META-INF/mimetypes.default");
    if (localMimeTypeFile != null) {
      localVector.addElement(localMimeTypeFile);
    }
    DB = new MimeTypeFile[localVector.size()];
    localVector.copyInto(DB);
  }
  
  private MimeTypeFile loadResource(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = SecuritySupport.getResourceAsStream(getClass(), paramString);
      if (localInputStream != null)
      {
        MimeTypeFile localMimeTypeFile1 = new MimeTypeFile(localInputStream);
        if (LogSupport.isLoggable()) {
          LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types file: " + paramString);
        }
        MimeTypeFile localMimeTypeFile2 = localMimeTypeFile1;
        return localMimeTypeFile2;
      }
      if (LogSupport.isLoggable()) {
        LogSupport.log("MimetypesFileTypeMap: not loading mime types file: " + paramString);
      }
      return null;
    }
    catch (IOException localIOException2)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MimetypesFileTypeMap: can't load " + paramString, localIOException2);
      }
    }
    catch (SecurityException localSecurityException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MimetypesFileTypeMap: can't load " + paramString, localSecurityException);
      }
    }
    finally
    {
      try
      {
        if (localInputStream != null) {
          localInputStream.close();
        }
      }
      catch (IOException localIOException6) {}
    }
  }
  
  private void loadAllResources(Vector paramVector, String paramString)
  {
    int i = 0;
    try
    {
      ClassLoader localClassLoader = null;
      localClassLoader = SecuritySupport.getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = getClass().getClassLoader();
      }
      URL[] arrayOfURL;
      if (localClassLoader != null) {
        arrayOfURL = SecuritySupport.getResources(localClassLoader, paramString);
      } else {
        arrayOfURL = SecuritySupport.getSystemResources(paramString);
      }
      if (arrayOfURL != null)
      {
        if (LogSupport.isLoggable()) {
          LogSupport.log("MimetypesFileTypeMap: getResources");
        }
        int j = 0;
        while (j < arrayOfURL.length)
        {
          URL localURL = arrayOfURL[j];
          InputStream localInputStream = null;
          if (LogSupport.isLoggable()) {
            LogSupport.log("MimetypesFileTypeMap: URL " + localURL);
          }
          try
          {
            localInputStream = SecuritySupport.openStream(localURL);
            if (localInputStream != null)
            {
              paramVector.addElement(new MimeTypeFile(localInputStream));
              i = 1;
              if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types from URL: " + localURL);
              }
            }
            else if (LogSupport.isLoggable())
            {
              LogSupport.log("MimetypesFileTypeMap: not loading mime types from URL: " + localURL);
            }
            try
            {
              if (localInputStream != null) {
                localInputStream.close();
              }
            }
            catch (IOException localIOException1) {}
            j++;
          }
          catch (IOException localIOException2)
          {
            if (LogSupport.isLoggable()) {
              LogSupport.log("MimetypesFileTypeMap: can't load " + localURL, localIOException2);
            }
          }
          catch (SecurityException localSecurityException)
          {
            if (LogSupport.isLoggable()) {
              LogSupport.log("MimetypesFileTypeMap: can't load " + localURL, localSecurityException);
            }
          }
          finally
          {
            try
            {
              if (localInputStream != null) {
                localInputStream.close();
              }
            }
            catch (IOException localIOException5) {}
          }
        }
      }
    }
    catch (Exception localException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MimetypesFileTypeMap: can't load " + paramString, localException);
      }
    }
    if (i == 0)
    {
      LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
      MimeTypeFile localMimeTypeFile = loadResource("/" + paramString);
      if (localMimeTypeFile != null) {
        paramVector.addElement(localMimeTypeFile);
      }
    }
  }
  
  private MimeTypeFile loadFile(String paramString)
  {
    MimeTypeFile localMimeTypeFile = null;
    try
    {
      localMimeTypeFile = new MimeTypeFile(paramString);
    }
    catch (IOException localIOException) {}
    return localMimeTypeFile;
  }
  
  public MimetypesFileTypeMap(String paramString)
    throws IOException
  {
    this();
    DB[0] = new MimeTypeFile(paramString);
  }
  
  public MimetypesFileTypeMap(InputStream paramInputStream)
  {
    this();
    try
    {
      DB[0] = new MimeTypeFile(paramInputStream);
    }
    catch (IOException localIOException) {}
  }
  
  public synchronized void addMimeTypes(String paramString)
  {
    if (DB[0] == null) {
      DB[0] = new MimeTypeFile();
    }
    DB[0].appendToRegistry(paramString);
  }
  
  public String getContentType(File paramFile)
  {
    return getContentType(paramFile.getName());
  }
  
  public synchronized String getContentType(String paramString)
  {
    int i = paramString.lastIndexOf(".");
    if (i < 0) {
      return defaultType;
    }
    String str1 = paramString.substring(i + 1);
    if (str1.length() == 0) {
      return defaultType;
    }
    for (int j = 0; j < DB.length; j++) {
      if (DB[j] != null)
      {
        String str2 = DB[j].getMIMETypeString(str1);
        if (str2 != null) {
          return str2;
        }
      }
    }
    return defaultType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\MimetypesFileTypeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */