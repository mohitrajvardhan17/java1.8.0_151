package sun.usagetracker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public final class UsageTrackerClient
{
  private static final Object LOCK = new Object();
  private static final String ORCL_UT_CONFIG_FILE_NAME = "usagetracker.properties";
  private static final String ORCL_UT_USAGE_DIR = ".oracle_jre_usage";
  private static final String ORCL_UT_PROPERTY_NAME = "com.oracle.usagetracker.";
  private static final String ORCL_UT_PROPERTY_RUN_SYNCHRONOUSLY = "com.oracle.usagetracker.run.synchronous";
  private static final String ORCL_UT_PROPERTY_CONFIG_FILE_NAME = "com.oracle.usagetracker.config.file";
  private static final String ORCL_UT_LOGTOFILE = "com.oracle.usagetracker.logToFile";
  private static final String ORCL_UT_LOGFILEMAXSIZE = "com.oracle.usagetracker.logFileMaxSize";
  private static final String ORCL_UT_LOGTOUDP = "com.oracle.usagetracker.logToUDP";
  private static final String ORCL_UT_TRACK_LAST_USAGE = "com.oracle.usagetracker.track.last.usage";
  private static final String ORCL_UT_VERBOSE = "com.oracle.usagetracker.verbose";
  private static final String ORCL_UT_DEBUG = "com.oracle.usagetracker.debug";
  private static final String ORCL_UT_ADDITIONALPROPERTIES = "com.oracle.usagetracker.additionalProperties";
  private static final String ORCL_UT_SEPARATOR = "com.oracle.usagetracker.separator";
  private static final String ORCL_UT_QUOTE = "com.oracle.usagetracker.quote";
  private static final String ORCL_UT_QUOTE_INNER = "com.oracle.usagetracker.innerQuote";
  private static final String DEFAULT_SEP = ",";
  private static final String DEFAULT_QUOTE = "\"";
  private static final String DEFAULT_QUOTE_INNER = "'";
  private static final AtomicBoolean isFirstRun = new AtomicBoolean(true);
  private static final String javaHome = getPropertyPrivileged("java.home");
  private static final String userHomeKeyword = "${user.home}";
  private static String separator;
  private static String quote;
  private static String innerQuote;
  private static boolean enabled;
  private static boolean verbose;
  private static boolean debug;
  private static boolean trackTime = true;
  private static String[] additionalProperties;
  private static String fullLogFilename;
  private static long logFileMaxSize;
  private static String datagramHost;
  private static int datagramPort;
  private static String staticMessage;
  
  private static String getPropertyPrivileged(String paramString)
  {
    return getPropertyPrivileged(paramString, null);
  }
  
  private static String getPropertyPrivileged(String paramString1, final String paramString2)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return System.getProperty(val$property, paramString2);
      }
    });
  }
  
  private static String getEnvPrivileged(String paramString)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return System.getenv(val$envName);
      }
    });
  }
  
  private static File getConfigFilePrivileged()
  {
    File localFile = null;
    String[] arrayOfString1 = new String[3];
    arrayOfString1[0] = getPropertyPrivileged("com.oracle.usagetracker.config.file");
    arrayOfString1[1] = getOSSpecificConfigFilePath();
    arrayOfString1[2] = (javaHome + File.separator + "lib" + File.separator + "management" + File.separator + "usagetracker.properties");
    for (String str : arrayOfString1) {
      if (str != null)
      {
        localFile = (File)AccessController.doPrivileged(new PrivilegedAction()
        {
          public File run()
          {
            File localFile = new File(val$path);
            return localFile.exists() ? localFile : null;
          }
        });
        if (localFile != null) {
          break;
        }
      }
    }
    return localFile;
  }
  
  private static String getOSSpecificConfigFilePath()
  {
    String str1 = getPropertyPrivileged("os.name");
    if (str1 != null)
    {
      if (str1.toLowerCase().startsWith("sunos")) {
        return "/etc/oracle/java/usagetracker.properties";
      }
      if (str1.toLowerCase().startsWith("mac")) {
        return "/Library/Application Support/Oracle/Java/usagetracker.properties";
      }
      if (str1.toLowerCase().startsWith("win"))
      {
        String str2 = getEnvPrivileged("ProgramFiles");
        return str2 + "\\Java\\conf\\" + "usagetracker.properties";
      }
      if (str1.toLowerCase().startsWith("linux")) {
        return "/etc/oracle/java/usagetracker.properties";
      }
    }
    return null;
  }
  
  private String getFullLogFilename(Properties paramProperties)
  {
    String str = paramProperties.getProperty("com.oracle.usagetracker.logToFile", "");
    if (str.isEmpty()) {
      return null;
    }
    if (str.startsWith("${user.home}"))
    {
      if (str.length() > "${user.home}".length())
      {
        str = getPropertyPrivileged("user.home") + str.substring("${user.home}".length());
      }
      else
      {
        printVerbose("UsageTracker: blank filename after user.home.");
        return null;
      }
    }
    else if (!new File(str).isAbsolute())
    {
      printVerbose("UsageTracker: relative path disallowed.");
      return null;
    }
    return str;
  }
  
  private long getLogFileMaxSize(Properties paramProperties)
  {
    String str = paramProperties.getProperty("com.oracle.usagetracker.logFileMaxSize", "");
    if (!str.isEmpty()) {
      try
      {
        return Long.parseLong(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        printVerbose("UsageTracker: bad maximum file size.");
      }
    }
    return -1L;
  }
  
  private String[] getAdditionalProperties(Properties paramProperties)
  {
    String str = paramProperties.getProperty("com.oracle.usagetracker.additionalProperties", "");
    return str.isEmpty() ? new String[0] : str.split(",");
  }
  
  private String parseDatagramHost(String paramString)
  {
    if (paramString != null)
    {
      int i = paramString.indexOf(':');
      if ((i > 0) && (i < paramString.length() - 1)) {
        return paramString.substring(0, i);
      }
      printVerbose("UsageTracker: bad UDP details.");
    }
    return null;
  }
  
  private int parseDatagramPort(String paramString)
  {
    if (paramString != null)
    {
      int i = paramString.indexOf(':');
      try
      {
        return Integer.parseInt(paramString.substring(i + 1));
      }
      catch (Exception localException)
      {
        printVerbose("UsageTracker: bad UDP port.");
      }
    }
    return 0;
  }
  
  private void printVerbose(String paramString)
  {
    if (verbose) {
      System.err.println(paramString);
    }
  }
  
  private void printDebug(String paramString)
  {
    if (debug) {
      System.err.println(paramString);
    }
  }
  
  private void printDebugStackTrace(Throwable paramThrowable)
  {
    if (debug) {
      paramThrowable.printStackTrace();
    }
  }
  
  public UsageTrackerClient() {}
  
  private void setupAndTimestamp(long paramLong)
  {
    if (isFirstRun.compareAndSet(true, false))
    {
      File localFile = getConfigFilePrivileged();
      if (localFile != null) {
        setup(localFile);
      }
      if (trackTime) {
        registerUsage(paramLong);
      }
    }
  }
  
  public void run(final String paramString1, final String paramString2)
  {
    printDebug("UsageTracker.run: " + paramString1 + ", javaCommand: " + paramString2);
    try
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          long l = System.currentTimeMillis();
          boolean bool = Boolean.parseBoolean(System.getProperty("com.oracle.usagetracker.run.synchronous", "true"));
          if (bool)
          {
            UsageTrackerClient.this.setupAndTimestamp(l);
            UsageTrackerClient.this.printVerbose("UsageTracker: running synchronous.");
          }
          if ((UsageTrackerClient.enabled) || (!bool))
          {
            UsageTrackerClient.UsageTrackerRunnable localUsageTrackerRunnable = new UsageTrackerClient.UsageTrackerRunnable(UsageTrackerClient.this, paramString1, paramString2, l, !bool);
            for (ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup(); localThreadGroup.getParent() != null; localThreadGroup = localThreadGroup.getParent()) {}
            Thread localThread = new Thread(localThreadGroup, localUsageTrackerRunnable, "UsageTracker");
            localThread.setDaemon(true);
            localThread.start();
          }
          return null;
        }
      });
    }
    catch (Throwable localThrowable)
    {
      printVerbose("UsageTracker: error in starting thread.");
      printDebugStackTrace(localThrowable);
    }
  }
  
  private boolean getBooleanProperty(Properties paramProperties, String paramString)
  {
    return Boolean.parseBoolean(paramProperties.getProperty(paramString));
  }
  
  private void setup(File paramFile)
  {
    Properties localProperties = new Properties();
    if (paramFile != null) {
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(paramFile);
        Object localObject1 = null;
        try
        {
          BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
          Object localObject2 = null;
          try
          {
            localProperties.load(localBufferedInputStream);
          }
          catch (Throwable localThrowable4)
          {
            localObject2 = localThrowable4;
            throw localThrowable4;
          }
          finally {}
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
              catch (Throwable localThrowable6)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable6);
              }
            } else {
              localFileInputStream.close();
            }
          }
        }
      }
      catch (Exception localException)
      {
        localProperties.clear();
      }
    }
    verbose = getBooleanProperty(localProperties, "com.oracle.usagetracker.verbose");
    debug = getBooleanProperty(localProperties, "com.oracle.usagetracker.debug");
    separator = localProperties.getProperty("com.oracle.usagetracker.separator", ",");
    quote = localProperties.getProperty("com.oracle.usagetracker.quote", "\"");
    innerQuote = localProperties.getProperty("com.oracle.usagetracker.innerQuote", "'");
    fullLogFilename = getFullLogFilename(localProperties);
    logFileMaxSize = getLogFileMaxSize(localProperties);
    additionalProperties = getAdditionalProperties(localProperties);
    String str = localProperties.getProperty("com.oracle.usagetracker.logToUDP");
    datagramHost = parseDatagramHost(str);
    datagramPort = parseDatagramPort(str);
    enabled = ((fullLogFilename != null) || ((datagramHost != null) && (datagramPort > 0)) ? 1 : 0) == 1;
    trackTime = Boolean.parseBoolean(localProperties.getProperty("com.oracle.usagetracker.track.last.usage", "true"));
  }
  
  private void registerUsage(long paramLong)
  {
    try
    {
      String str1 = new File(System.getProperty("java.home")).getCanonicalPath();
      String str2 = getPropertyPrivileged("os.name");
      File localFile = null;
      Object localObject1;
      if (str2.toLowerCase().startsWith("win"))
      {
        localObject1 = getEnvPrivileged("ProgramData");
        if (localObject1 != null)
        {
          localFile = new File((String)localObject1 + File.separator + "Oracle" + File.separator + "Java" + File.separator + ".oracle_jre_usage", getPathHash(str1) + ".timestamp");
          if (!localFile.exists())
          {
            if (!localFile.getParentFile().exists())
            {
              localFile.getParentFile().mkdirs();
              Runtime.getRuntime().exec("icacls.exe " + localFile.getParentFile() + " /grant \"everyone\":(OI)(CI)M");
            }
            localFile.createNewFile();
            Runtime.getRuntime().exec("icacls.exe " + localFile + " /grant \"everyone\":(OI)(CI)M");
          }
        }
      }
      else
      {
        localObject1 = System.getProperty("user.home");
        if (localObject1 != null)
        {
          localFile = new File((String)localObject1 + File.separator + ".oracle_jre_usage", getPathHash(str1) + ".timestamp");
          if (!localFile.exists())
          {
            if (!localFile.getParentFile().exists()) {
              localFile.getParentFile().mkdirs();
            }
            localFile.createNewFile();
          }
        }
      }
      if (localFile != null) {
        try
        {
          localObject1 = new FileOutputStream(localFile);
          Object localObject2 = null;
          try
          {
            String str3 = str1 + System.lineSeparator() + paramLong + System.lineSeparator();
            ((FileOutputStream)localObject1).write(str3.getBytes("UTF-8"));
          }
          catch (Throwable localThrowable2)
          {
            localObject2 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localObject1 != null) {
              if (localObject2 != null) {
                try
                {
                  ((FileOutputStream)localObject1).close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject2).addSuppressed(localThrowable3);
                }
              } else {
                ((FileOutputStream)localObject1).close();
              }
            }
          }
        }
        catch (IOException localIOException2)
        {
          printDebugStackTrace(localIOException2);
        }
      }
    }
    catch (IOException localIOException1)
    {
      printDebugStackTrace(localIOException1);
    }
  }
  
  private String getPathHash(String paramString)
  {
    long l = 0L;
    for (int i = 0; i < paramString.length(); i++) {
      l = 31L * l + paramString.charAt(i);
    }
    return Long.toHexString(l);
  }
  
  class UsageTrackerRunnable
    implements Runnable
  {
    private String callerName;
    private String javaCommand;
    private long timestamp;
    private boolean runAsync;
    
    UsageTrackerRunnable(String paramString1, String paramString2, long paramLong, boolean paramBoolean)
    {
      callerName = paramString1;
      javaCommand = (paramString2 != null ? paramString2 : "");
      timestamp = paramLong;
      runAsync = paramBoolean;
    }
    
    private String buildMessage(String paramString1, String paramString2, long paramLong)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      appendWithQuotes(localStringBuilder, paramString1);
      localStringBuilder.append(UsageTrackerClient.separator);
      Date localDate = new Date(paramLong);
      appendWithQuotes(localStringBuilder, localDate.toString());
      localStringBuilder.append(UsageTrackerClient.separator);
      String str = "0";
      try
      {
        InetAddress localInetAddress = InetAddress.getLocalHost();
        str = localInetAddress.toString();
      }
      catch (Throwable localThrowable) {}
      appendWithQuotes(localStringBuilder, str);
      localStringBuilder.append(UsageTrackerClient.separator);
      appendWithQuotes(localStringBuilder, paramString2);
      localStringBuilder.append(UsageTrackerClient.separator);
      localStringBuilder.append(getRuntimeDetails());
      localStringBuilder.append("\n");
      return localStringBuilder.toString();
    }
    
    private String getRuntimeDetails()
    {
      synchronized (UsageTrackerClient.LOCK)
      {
        if (UsageTrackerClient.staticMessage == null)
        {
          StringBuilder localStringBuilder1 = new StringBuilder();
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.javaHome);
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.version"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vm.version"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vendor"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vm.vendor"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.name"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.arch"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.version"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          List localList = getInputArguments();
          StringBuilder localStringBuilder2 = new StringBuilder();
          Object localObject1 = localList.iterator();
          Object localObject2;
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (String)((Iterator)localObject1).next();
            localStringBuilder2.append(addQuotesFor((String)localObject2, " ", UsageTrackerClient.innerQuote));
            localStringBuilder2.append(' ');
          }
          appendWithQuotes(localStringBuilder1, localStringBuilder2.toString());
          localStringBuilder1.append(UsageTrackerClient.separator);
          appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.class.path"));
          localStringBuilder1.append(UsageTrackerClient.separator);
          localObject1 = new StringBuilder();
          for (Object localObject3 : UsageTrackerClient.additionalProperties)
          {
            ((StringBuilder)localObject1).append(((String)localObject3).trim());
            ((StringBuilder)localObject1).append("=");
            ((StringBuilder)localObject1).append(addQuotesFor(UsageTrackerClient.getPropertyPrivileged(((String)localObject3).trim()), " ", UsageTrackerClient.innerQuote));
            ((StringBuilder)localObject1).append(" ");
          }
          appendWithQuotes(localStringBuilder1, ((StringBuilder)localObject1).toString());
          UsageTrackerClient.access$502(localStringBuilder1.toString());
        }
        return UsageTrackerClient.staticMessage;
      }
    }
    
    private void appendWithQuotes(StringBuilder paramStringBuilder, String paramString)
    {
      paramStringBuilder.append(UsageTrackerClient.quote);
      paramString = paramString.replace(UsageTrackerClient.quote, UsageTrackerClient.quote + UsageTrackerClient.quote);
      paramStringBuilder.append(paramString);
      paramStringBuilder.append(UsageTrackerClient.quote);
    }
    
    private String addQuotesFor(String paramString1, String paramString2, String paramString3)
    {
      if (paramString1 == null) {
        return paramString1;
      }
      paramString1 = paramString1.replace(paramString3, paramString3 + paramString3);
      if (paramString1.indexOf(paramString2) >= 0) {
        paramString1 = paramString3 + paramString1 + paramString3;
      }
      return paramString1;
    }
    
    private List<String> getInputArguments()
    {
      (List)AccessController.doPrivileged(new PrivilegedAction()
      {
        public List<String> run()
        {
          try
          {
            Class localClass = Class.forName("java.lang.management.ManagementFactory", true, null);
            Method localMethod = localClass.getMethod("getRuntimeMXBean", (Class[])null);
            Object localObject = localMethod.invoke(null, (Object[])null);
            localClass = Class.forName("java.lang.management.RuntimeMXBean", true, null);
            localMethod = localClass.getMethod("getInputArguments", (Class[])null);
            List localList = (List)localMethod.invoke(localObject, (Object[])null);
            return localList;
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            return Collections.singletonList("n/a");
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
            throw new AssertionError(localNoSuchMethodException);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new AssertionError(localIllegalAccessException);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            throw new AssertionError(localInvocationTargetException.getCause());
          }
        }
      });
    }
    
    private void sendDatagram(String paramString)
    {
      UsageTrackerClient.this.printDebug("UsageTracker: sendDatagram");
      try
      {
        DatagramSocket localDatagramSocket = new DatagramSocket();
        Object localObject1 = null;
        try
        {
          byte[] arrayOfByte = paramString.getBytes("UTF-8");
          if (arrayOfByte.length > localDatagramSocket.getSendBufferSize()) {
            UsageTrackerClient.this.printVerbose("UsageTracker: message truncated for Datagram.");
          }
          UsageTrackerClient.this.printDebug("UsageTracker: host=" + UsageTrackerClient.datagramHost + ", port=" + UsageTrackerClient.datagramPort);
          UsageTrackerClient.this.printDebug("UsageTracker: SendBufferSize = " + localDatagramSocket.getSendBufferSize());
          UsageTrackerClient.this.printDebug("UsageTracker: packet length  = " + arrayOfByte.length);
          InetAddress localInetAddress = InetAddress.getByName(UsageTrackerClient.datagramHost);
          DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length > localDatagramSocket.getSendBufferSize() ? localDatagramSocket.getSendBufferSize() : arrayOfByte.length, localInetAddress, UsageTrackerClient.datagramPort);
          localDatagramSocket.send(localDatagramPacket);
          UsageTrackerClient.this.printVerbose("UsageTracker: done sending to UDP.");
          UsageTrackerClient.this.printDebug("UsageTracker: sent size = " + localDatagramPacket.getLength());
        }
        catch (Throwable localThrowable3)
        {
          localObject1 = localThrowable3;
          throw localThrowable3;
        }
        finally
        {
          if (localDatagramSocket != null) {
            if (localObject1 != null) {
              try
              {
                localDatagramSocket.close();
              }
              catch (Throwable localThrowable4)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable4);
              }
            } else {
              localDatagramSocket.close();
            }
          }
        }
      }
      catch (Throwable localThrowable1)
      {
        UsageTrackerClient.this.printVerbose("UsageTracker: error in sendDatagram: " + localThrowable1);
        UsageTrackerClient.this.printDebugStackTrace(localThrowable1);
      }
    }
    
    private void sendToFile(String paramString)
    {
      UsageTrackerClient.this.printDebug("UsageTracker: sendToFile");
      File localFile = new File(UsageTrackerClient.fullLogFilename);
      if ((UsageTrackerClient.logFileMaxSize >= 0L) && (localFile.length() >= UsageTrackerClient.logFileMaxSize))
      {
        UsageTrackerClient.this.printVerbose("UsageTracker: log file size exceeds maximum.");
        return;
      }
      synchronized (UsageTrackerClient.LOCK)
      {
        try
        {
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile, true);
          Object localObject1 = null;
          try
          {
            OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream, "UTF-8");
            Object localObject2 = null;
            try
            {
              localOutputStreamWriter.write(paramString, 0, paramString.length());
              UsageTrackerClient.this.printVerbose("UsageTracker: done sending to file.");
              UsageTrackerClient.this.printDebug("UsageTracker: " + UsageTrackerClient.fullLogFilename);
            }
            catch (Throwable localThrowable5)
            {
              localObject2 = localThrowable5;
              throw localThrowable5;
            }
            finally {}
          }
          catch (Throwable localThrowable3)
          {
            localObject1 = localThrowable3;
            throw localThrowable3;
          }
          finally
          {
            if (localFileOutputStream != null) {
              if (localObject1 != null) {
                try
                {
                  localFileOutputStream.close();
                }
                catch (Throwable localThrowable7)
                {
                  ((Throwable)localObject1).addSuppressed(localThrowable7);
                }
              } else {
                localFileOutputStream.close();
              }
            }
          }
        }
        catch (Throwable localThrowable1)
        {
          UsageTrackerClient.this.printVerbose("UsageTracker: error in sending to file.");
          UsageTrackerClient.this.printDebugStackTrace(localThrowable1);
        }
      }
    }
    
    public void run()
    {
      if (runAsync)
      {
        UsageTrackerClient.this.setupAndTimestamp(timestamp);
        UsageTrackerClient.this.printVerbose("UsageTracker: running asynchronous.");
      }
      if (UsageTrackerClient.enabled)
      {
        UsageTrackerClient.this.printDebug("UsageTrackerRunnable.run: " + callerName + ", javaCommand: " + javaCommand);
        String str = buildMessage(callerName, javaCommand, timestamp);
        if ((UsageTrackerClient.datagramHost != null) && (UsageTrackerClient.datagramPort > 0)) {
          sendDatagram(str);
        }
        if (UsageTrackerClient.fullLogFilename != null) {
          sendToFile(str);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\usagetracker\UsageTrackerClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */