package sun.awt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

final class DebugSettings
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.debug.DebugSettings");
  static final String PREFIX = "awtdebug";
  static final String PROP_FILE = "properties";
  private static final String[] DEFAULT_PROPS = { "awtdebug.assert=true", "awtdebug.trace=false", "awtdebug.on=true", "awtdebug.ctrace=false" };
  private static DebugSettings instance = null;
  private Properties props = new Properties();
  private static final String PROP_CTRACE = "ctrace";
  private static final int PROP_CTRACE_LEN = "ctrace".length();
  
  static void init()
  {
    if (instance != null) {
      return;
    }
    NativeLibLoader.loadLibraries();
    instance = new DebugSettings();
    instance.loadNativeSettings();
  }
  
  private DebugSettings()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        DebugSettings.this.loadProperties();
        return null;
      }
    });
  }
  
  private synchronized void loadProperties()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        DebugSettings.this.loadDefaultProperties();
        DebugSettings.this.loadFileProperties();
        DebugSettings.this.loadSystemProperties();
        return null;
      }
    });
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("DebugSettings:\n{0}", new Object[] { this });
    }
  }
  
  public String toString()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
    Iterator localIterator = props.stringPropertyNames().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = props.getProperty(str1, "");
      localPrintStream.println(str1 + " = " + str2);
    }
    return new String(localByteArrayOutputStream.toByteArray());
  }
  
  private void loadDefaultProperties()
  {
    try
    {
      for (int i = 0; i < DEFAULT_PROPS.length; i++)
      {
        StringBufferInputStream localStringBufferInputStream = new StringBufferInputStream(DEFAULT_PROPS[i]);
        props.load(localStringBufferInputStream);
        localStringBufferInputStream.close();
      }
    }
    catch (IOException localIOException) {}
  }
  
  private void loadFileProperties()
  {
    String str = System.getProperty("awtdebug.properties", "");
    if (str.equals("")) {
      str = System.getProperty("user.home", "") + File.separator + "awtdebug" + "." + "properties";
    }
    File localFile = new File(str);
    try
    {
      println("Reading debug settings from '" + localFile.getCanonicalPath() + "'...");
      FileInputStream localFileInputStream = new FileInputStream(localFile);
      props.load(localFileInputStream);
      localFileInputStream.close();
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      println("Did not find settings file.");
    }
    catch (IOException localIOException)
    {
      println("Problem reading settings, IOException: " + localIOException.getMessage());
    }
  }
  
  private void loadSystemProperties()
  {
    Properties localProperties = System.getProperties();
    Iterator localIterator = localProperties.stringPropertyNames().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = localProperties.getProperty(str1, "");
      if (str1.startsWith("awtdebug")) {
        props.setProperty(str1, str2);
      }
    }
  }
  
  public synchronized boolean getBoolean(String paramString, boolean paramBoolean)
  {
    String str = getString(paramString, String.valueOf(paramBoolean));
    return str.equalsIgnoreCase("true");
  }
  
  public synchronized int getInt(String paramString, int paramInt)
  {
    String str = getString(paramString, String.valueOf(paramInt));
    return Integer.parseInt(str);
  }
  
  public synchronized String getString(String paramString1, String paramString2)
  {
    String str1 = "awtdebug." + paramString1;
    String str2 = props.getProperty(str1, paramString2);
    return str2;
  }
  
  private synchronized List<String> getPropertyNames()
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = props.stringPropertyNames().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      str = str.substring("awtdebug".length() + 1);
      localLinkedList.add(str);
    }
    return localLinkedList;
  }
  
  private void println(Object paramObject)
  {
    if (log.isLoggable(PlatformLogger.Level.FINER)) {
      log.finer(paramObject.toString());
    }
  }
  
  private synchronized native void setCTracingOn(boolean paramBoolean);
  
  private synchronized native void setCTracingOn(boolean paramBoolean, String paramString);
  
  private synchronized native void setCTracingOn(boolean paramBoolean, String paramString, int paramInt);
  
  private void loadNativeSettings()
  {
    boolean bool1 = getBoolean("ctrace", false);
    setCTracingOn(bool1);
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = getPropertyNames().iterator();
    String str1;
    while (localIterator.hasNext())
    {
      str1 = (String)localIterator.next();
      if ((str1.startsWith("ctrace")) && (str1.length() > PROP_CTRACE_LEN)) {
        localLinkedList.add(str1);
      }
    }
    Collections.sort(localLinkedList);
    localIterator = localLinkedList.iterator();
    while (localIterator.hasNext())
    {
      str1 = (String)localIterator.next();
      String str2 = str1.substring(PROP_CTRACE_LEN + 1);
      int i = str2.indexOf('@');
      String str3 = i != -1 ? str2.substring(0, i) : str2;
      String str4 = i != -1 ? str2.substring(i + 1) : "";
      boolean bool2 = getBoolean(str1, false);
      if (str4.length() == 0)
      {
        setCTracingOn(bool2, str3);
      }
      else
      {
        int j = Integer.parseInt(str4, 10);
        setCTracingOn(bool2, str3, j);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\DebugSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */