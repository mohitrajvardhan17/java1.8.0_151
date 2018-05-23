package sun.applet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import sun.net.www.ParseUtil;

public class Main
{
  static File theUserPropertiesFile;
  static final String[][] avDefaultUserProps = { { "http.proxyHost", "" }, { "http.proxyPort", "80" }, { "package.restrict.access.sun", "true" } };
  private static AppletMessageHandler amh = new AppletMessageHandler("appletviewer");
  private boolean debugFlag = false;
  private boolean helpFlag = false;
  private String encoding = null;
  private boolean noSecurityFlag = false;
  private static boolean cmdLineTestFlag = false;
  private static Vector urlList = new Vector(1);
  public static final String theVersion = System.getProperty("java.version");
  
  public Main() {}
  
  public static void main(String[] paramArrayOfString)
  {
    Main localMain = new Main();
    int i = localMain.run(paramArrayOfString);
    if ((i != 0) || (cmdLineTestFlag)) {
      System.exit(i);
    }
  }
  
  private int run(String[] paramArrayOfString)
  {
    try
    {
      if (paramArrayOfString.length == 0)
      {
        usage();
        return 0;
      }
      int i = 0;
      while (i < paramArrayOfString.length)
      {
        int k = decodeArg(paramArrayOfString, i);
        if (k == 0) {
          throw new ParseException(lookup("main.err.unrecognizedarg", paramArrayOfString[i]));
        }
        i += k;
      }
    }
    catch (ParseException localParseException)
    {
      System.err.println(localParseException.getMessage());
      return 1;
    }
    if (helpFlag)
    {
      usage();
      return 0;
    }
    if (urlList.size() == 0)
    {
      System.err.println(lookup("main.err.inputfile"));
      return 1;
    }
    if (debugFlag) {
      return invokeDebugger(paramArrayOfString);
    }
    if ((!noSecurityFlag) && (System.getSecurityManager() == null)) {
      init();
    }
    for (int j = 0; j < urlList.size(); j++) {
      try
      {
        AppletViewer.parse((URL)urlList.elementAt(j), encoding);
      }
      catch (IOException localIOException)
      {
        System.err.println(lookup("main.err.io", localIOException.getMessage()));
        return 1;
      }
    }
    return 0;
  }
  
  private static void usage()
  {
    System.out.println(lookup("usage"));
  }
  
  private int decodeArg(String[] paramArrayOfString, int paramInt)
    throws Main.ParseException
  {
    String str = paramArrayOfString[paramInt];
    int i = paramArrayOfString.length;
    if (("-help".equalsIgnoreCase(str)) || ("-?".equals(str)))
    {
      helpFlag = true;
      return 1;
    }
    if (("-encoding".equals(str)) && (paramInt < i - 1))
    {
      if (encoding != null) {
        throw new ParseException(lookup("main.err.dupoption", str));
      }
      encoding = paramArrayOfString[(++paramInt)];
      return 2;
    }
    if ("-debug".equals(str))
    {
      debugFlag = true;
      return 1;
    }
    if ("-Xnosecurity".equals(str))
    {
      System.err.println();
      System.err.println(lookup("main.warn.nosecmgr"));
      System.err.println();
      noSecurityFlag = true;
      return 1;
    }
    if ("-XcmdLineTest".equals(str))
    {
      cmdLineTestFlag = true;
      return 1;
    }
    if (str.startsWith("-")) {
      throw new ParseException(lookup("main.err.unsupportedopt", str));
    }
    URL localURL = parseURL(str);
    if (localURL != null)
    {
      urlList.addElement(localURL);
      return 1;
    }
    return 0;
  }
  
  private URL parseURL(String paramString)
    throws Main.ParseException
  {
    URL localURL = null;
    String str1 = "file:";
    try
    {
      if (paramString.indexOf(':') <= 1)
      {
        localURL = ParseUtil.fileToEncodedURL(new File(paramString));
      }
      else if ((paramString.startsWith(str1)) && (paramString.length() != str1.length()) && (!new File(paramString.substring(str1.length())).isAbsolute()))
      {
        String str2 = ParseUtil.fileToEncodedURL(new File(System.getProperty("user.dir"))).getPath() + paramString.substring(str1.length());
        localURL = new URL("file", "", str2);
      }
      else
      {
        localURL = new URL(paramString);
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new ParseException(lookup("main.err.badurl", paramString, localMalformedURLException.getMessage()));
    }
    return localURL;
  }
  
  private int invokeDebugger(String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[paramArrayOfString.length + 1];
    int i = 0;
    String str = System.getProperty("java.home") + File.separator + "phony";
    arrayOfString[(i++)] = ("-Djava.class.path=" + str);
    arrayOfString[(i++)] = "sun.applet.Main";
    for (int j = 0; j < paramArrayOfString.length; j++) {
      if (!"-debug".equals(paramArrayOfString[j])) {
        arrayOfString[(i++)] = paramArrayOfString[j];
      }
    }
    try
    {
      Class localClass = Class.forName("com.sun.tools.example.debug.tty.TTY", true, ClassLoader.getSystemClassLoader());
      Method localMethod = localClass.getDeclaredMethod("main", new Class[] { String[].class });
      localMethod.invoke(null, new Object[] { arrayOfString });
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println(lookup("main.debug.cantfinddebug"));
      return 1;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      System.err.println(lookup("main.debug.cantfindmain"));
      return 1;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      System.err.println(lookup("main.debug.exceptionindebug"));
      return 1;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      System.err.println(lookup("main.debug.cantaccess"));
      return 1;
    }
    return 0;
  }
  
  private void init()
  {
    Properties localProperties1 = getAVProps();
    localProperties1.put("browser", "sun.applet.AppletViewer");
    localProperties1.put("browser.version", "1.06");
    localProperties1.put("browser.vendor", "Oracle Corporation");
    localProperties1.put("http.agent", "Java(tm) 2 SDK, Standard Edition v" + theVersion);
    localProperties1.put("package.restrict.definition.java", "true");
    localProperties1.put("package.restrict.definition.sun", "true");
    localProperties1.put("java.version.applet", "true");
    localProperties1.put("java.vendor.applet", "true");
    localProperties1.put("java.vendor.url.applet", "true");
    localProperties1.put("java.class.version.applet", "true");
    localProperties1.put("os.name.applet", "true");
    localProperties1.put("os.version.applet", "true");
    localProperties1.put("os.arch.applet", "true");
    localProperties1.put("file.separator.applet", "true");
    localProperties1.put("path.separator.applet", "true");
    localProperties1.put("line.separator.applet", "true");
    Properties localProperties2 = System.getProperties();
    Enumeration localEnumeration = localProperties2.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = localProperties2.getProperty(str1);
      String str3;
      if ((str3 = (String)localProperties1.setProperty(str1, str2)) != null) {
        System.err.println(lookup("main.warn.prop.overwrite", str1, str3, str2));
      }
    }
    System.setProperties(localProperties1);
    if (!noSecurityFlag) {
      System.setSecurityManager(new AppletSecurity());
    } else {
      System.err.println(lookup("main.nosecmgr"));
    }
  }
  
  private Properties getAVProps()
  {
    Properties localProperties = new Properties();
    File localFile1 = theUserPropertiesFile;
    if (localFile1.exists())
    {
      if (localFile1.canRead())
      {
        localProperties = getAVProps(localFile1);
      }
      else
      {
        System.err.println(lookup("main.warn.cantreadprops", localFile1.toString()));
        localProperties = setDefaultAVProps();
      }
    }
    else
    {
      File localFile2 = new File(System.getProperty("user.home"));
      File localFile3 = new File(localFile2, ".hotjava");
      localFile3 = new File(localFile3, "properties");
      if (localFile3.exists())
      {
        localProperties = getAVProps(localFile3);
      }
      else
      {
        System.err.println(lookup("main.warn.cantreadprops", localFile3.toString()));
        localProperties = setDefaultAVProps();
      }
      try
      {
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile1);
        Object localObject1 = null;
        try
        {
          localProperties.store(localFileOutputStream, lookup("main.prop.store"));
        }
        catch (Throwable localThrowable2)
        {
          localObject1 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localFileOutputStream != null) {
            if (localObject1 != null) {
              try
              {
                localFileOutputStream.close();
              }
              catch (Throwable localThrowable3)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable3);
              }
            } else {
              localFileOutputStream.close();
            }
          }
        }
      }
      catch (IOException localIOException)
      {
        System.err.println(lookup("main.err.prop.cantsave", localFile1.toString()));
      }
    }
    return localProperties;
  }
  
  private Properties setDefaultAVProps()
  {
    Properties localProperties = new Properties();
    for (int i = 0; i < avDefaultUserProps.length; i++) {
      localProperties.setProperty(avDefaultUserProps[i][0], avDefaultUserProps[i][1]);
    }
    return localProperties;
  }
  
  private Properties getAVProps(File paramFile)
  {
    Properties localProperties1 = new Properties();
    Properties localProperties2 = new Properties();
    Object localObject1;
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(paramFile);
      localObject1 = null;
      try
      {
        localProperties2.load(new BufferedInputStream(localFileInputStream));
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localFileInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localFileInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localFileInputStream.close();
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      System.err.println(lookup("main.err.prop.cantread", paramFile.toString()));
    }
    for (int i = 0; i < avDefaultUserProps.length; i++)
    {
      localObject1 = localProperties2.getProperty(avDefaultUserProps[i][0]);
      if (localObject1 != null) {
        localProperties1.setProperty(avDefaultUserProps[i][0], (String)localObject1);
      } else {
        localProperties1.setProperty(avDefaultUserProps[i][0], avDefaultUserProps[i][1]);
      }
    }
    return localProperties1;
  }
  
  private static String lookup(String paramString)
  {
    return amh.getMessage(paramString);
  }
  
  private static String lookup(String paramString1, String paramString2)
  {
    return amh.getMessage(paramString1, paramString2);
  }
  
  private static String lookup(String paramString1, String paramString2, String paramString3)
  {
    return amh.getMessage(paramString1, paramString2, paramString3);
  }
  
  private static String lookup(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    return amh.getMessage(paramString1, paramString2, paramString3, paramString4);
  }
  
  static
  {
    File localFile = new File(System.getProperty("user.home"));
    localFile.canWrite();
    theUserPropertiesFile = new File(localFile, ".appletviewer");
  }
  
  class ParseException
    extends RuntimeException
  {
    Throwable t = null;
    
    public ParseException(String paramString)
    {
      super();
    }
    
    public ParseException(Throwable paramThrowable)
    {
      super();
      t = paramThrowable;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */