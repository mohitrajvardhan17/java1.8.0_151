package sun.management;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import sun.management.jdp.JdpController;
import sun.management.jdp.JdpException;
import sun.management.jmxremote.ConnectorBootstrap;
import sun.misc.VMSupport;

public class Agent
{
  private static Properties mgmtProps;
  private static ResourceBundle messageRB;
  private static final String CONFIG_FILE = "com.sun.management.config.file";
  private static final String SNMP_PORT = "com.sun.management.snmp.port";
  private static final String JMXREMOTE = "com.sun.management.jmxremote";
  private static final String JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
  private static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
  private static final String ENABLE_THREAD_CONTENTION_MONITORING = "com.sun.management.enableThreadContentionMonitoring";
  private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
  private static final String SNMP_ADAPTOR_BOOTSTRAP_CLASS_NAME = "sun.management.snmp.AdaptorBootstrap";
  private static final String JDP_DEFAULT_ADDRESS = "224.0.23.178";
  private static final int JDP_DEFAULT_PORT = 7095;
  private static JMXConnectorServer jmxServer = null;
  
  public Agent() {}
  
  private static Properties parseString(String paramString)
  {
    Properties localProperties = new Properties();
    if (paramString != null) {
      for (String str1 : paramString.split(","))
      {
        String[] arrayOfString2 = str1.split("=", 2);
        String str2 = arrayOfString2[0].trim();
        String str3 = arrayOfString2.length > 1 ? arrayOfString2[1].trim() : "";
        if (!str2.startsWith("com.sun.management.")) {
          error("agent.err.invalid.option", str2);
        }
        localProperties.setProperty(str2, str3);
      }
    }
    return localProperties;
  }
  
  public static void premain(String paramString)
    throws Exception
  {
    agentmain(paramString);
  }
  
  public static void agentmain(String paramString)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      paramString = "com.sun.management.jmxremote";
    }
    Properties localProperties1 = parseString(paramString);
    Properties localProperties2 = new Properties();
    String str = localProperties1.getProperty("com.sun.management.config.file");
    readConfiguration(str, localProperties2);
    localProperties2.putAll(localProperties1);
    startAgent(localProperties2);
  }
  
  private static synchronized void startLocalManagementAgent()
  {
    Properties localProperties = VMSupport.getAgentProperties();
    if (localProperties.get("com.sun.management.jmxremote.localConnectorAddress") == null)
    {
      JMXConnectorServer localJMXConnectorServer = ConnectorBootstrap.startLocalConnectorServer();
      String str = localJMXConnectorServer.getAddress().toString();
      localProperties.put("com.sun.management.jmxremote.localConnectorAddress", str);
      try
      {
        ConnectorAddressLink.export(str);
      }
      catch (Exception localException)
      {
        warning("agent.err.exportaddress.failed", localException.getMessage());
      }
    }
  }
  
  private static synchronized void startRemoteManagementAgent(String paramString)
    throws Exception
  {
    if (jmxServer != null) {
      throw new RuntimeException(getText("agent.err.invalid.state", new String[] { "Agent already started" }));
    }
    Properties localProperties1 = parseString(paramString);
    Properties localProperties2 = new Properties();
    String str1 = System.getProperty("com.sun.management.config.file");
    readConfiguration(str1, localProperties2);
    Properties localProperties3 = System.getProperties();
    synchronized (localProperties3)
    {
      localProperties2.putAll(localProperties3);
    }
    ??? = localProperties1.getProperty("com.sun.management.config.file");
    if (??? != null) {
      readConfiguration((String)???, localProperties2);
    }
    localProperties2.putAll(localProperties1);
    String str2 = localProperties2.getProperty("com.sun.management.enableThreadContentionMonitoring");
    if (str2 != null) {
      ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
    }
    String str3 = localProperties2.getProperty("com.sun.management.jmxremote.port");
    if (str3 != null)
    {
      jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str3, localProperties2);
      startDiscoveryService(localProperties2);
    }
  }
  
  private static synchronized void stopRemoteManagementAgent()
    throws Exception
  {
    
    if (jmxServer != null)
    {
      ConnectorBootstrap.unexportRegistry();
      jmxServer.stop();
      jmxServer = null;
    }
  }
  
  private static void startAgent(Properties paramProperties)
    throws Exception
  {
    String str1 = paramProperties.getProperty("com.sun.management.snmp.port");
    String str2 = paramProperties.getProperty("com.sun.management.jmxremote");
    String str3 = paramProperties.getProperty("com.sun.management.jmxremote.port");
    String str4 = paramProperties.getProperty("com.sun.management.enableThreadContentionMonitoring");
    if (str4 != null) {
      ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
    }
    try
    {
      if (str1 != null) {
        loadSnmpAgent(str1, paramProperties);
      }
      if ((str2 != null) || (str3 != null))
      {
        if (str3 != null)
        {
          jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str3, paramProperties);
          startDiscoveryService(paramProperties);
        }
        startLocalManagementAgent();
      }
    }
    catch (AgentConfigurationError localAgentConfigurationError)
    {
      error(localAgentConfigurationError.getError(), localAgentConfigurationError.getParams());
    }
    catch (Exception localException)
    {
      error(localException);
    }
  }
  
  private static void startDiscoveryService(Properties paramProperties)
    throws IOException
  {
    String str1 = paramProperties.getProperty("com.sun.management.jdp.port");
    String str2 = paramProperties.getProperty("com.sun.management.jdp.address");
    String str3 = paramProperties.getProperty("com.sun.management.jmxremote.autodiscovery");
    boolean bool = false;
    if (str3 == null) {
      bool = str1 != null;
    } else {
      try
      {
        bool = Boolean.parseBoolean(str3);
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        throw new AgentConfigurationError("Couldn't parse autodiscovery argument");
      }
    }
    if (bool)
    {
      InetAddress localInetAddress;
      try
      {
        localInetAddress = str2 == null ? InetAddress.getByName("224.0.23.178") : InetAddress.getByName(str2);
      }
      catch (UnknownHostException localUnknownHostException)
      {
        throw new AgentConfigurationError("Unable to broadcast to requested address", localUnknownHostException);
      }
      int i = 7095;
      if (str1 != null) {
        try
        {
          i = Integer.parseInt(str1);
        }
        catch (NumberFormatException localNumberFormatException2)
        {
          throw new AgentConfigurationError("Couldn't parse JDP port argument");
        }
      }
      String str4 = paramProperties.getProperty("com.sun.management.jmxremote.port");
      String str5 = paramProperties.getProperty("com.sun.management.jmxremote.rmi.port");
      JMXServiceURL localJMXServiceURL = jmxServer.getAddress();
      String str6 = localJMXServiceURL.getHost();
      String str7 = str5 != null ? String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", new Object[] { str6, str5, str6, str4 }) : String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", new Object[] { str6, str4 });
      String str8 = paramProperties.getProperty("com.sun.management.jdp.name");
      try
      {
        JdpController.startDiscoveryService(localInetAddress, i, str8, str7);
      }
      catch (JdpException localJdpException)
      {
        throw new AgentConfigurationError("Couldn't start JDP service", localJdpException);
      }
    }
  }
  
  public static Properties loadManagementProperties()
  {
    Properties localProperties1 = new Properties();
    String str = System.getProperty("com.sun.management.config.file");
    readConfiguration(str, localProperties1);
    Properties localProperties2 = System.getProperties();
    synchronized (localProperties2)
    {
      localProperties1.putAll(localProperties2);
    }
    return localProperties1;
  }
  
  public static synchronized Properties getManagementProperties()
  {
    if (mgmtProps == null)
    {
      String str1 = System.getProperty("com.sun.management.config.file");
      String str2 = System.getProperty("com.sun.management.snmp.port");
      String str3 = System.getProperty("com.sun.management.jmxremote");
      String str4 = System.getProperty("com.sun.management.jmxremote.port");
      if ((str1 == null) && (str2 == null) && (str3 == null) && (str4 == null)) {
        return null;
      }
      mgmtProps = loadManagementProperties();
    }
    return mgmtProps;
  }
  
  private static void loadSnmpAgent(String paramString, Properties paramProperties)
  {
    try
    {
      Class localClass = Class.forName("sun.management.snmp.AdaptorBootstrap", true, null);
      localObject = localClass.getMethod("initialize", new Class[] { String.class, Properties.class });
      ((Method)localObject).invoke(null, new Object[] { paramString, paramProperties });
    }
    catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException localClassNotFoundException)
    {
      throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", localClassNotFoundException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject = localInvocationTargetException.getCause();
      if ((localObject instanceof RuntimeException)) {
        throw ((RuntimeException)localObject);
      }
      if ((localObject instanceof Error)) {
        throw ((Error)localObject);
      }
      throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", (Throwable)localObject);
    }
  }
  
  private static void readConfiguration(String paramString, Properties paramProperties)
  {
    if (paramString == null)
    {
      localObject1 = System.getProperty("java.home");
      if (localObject1 == null) {
        throw new Error("Can't find java.home ??");
      }
      localObject2 = new StringBuffer((String)localObject1);
      ((StringBuffer)localObject2).append(File.separator).append("lib");
      ((StringBuffer)localObject2).append(File.separator).append("management");
      ((StringBuffer)localObject2).append(File.separator).append("management.properties");
      paramString = ((StringBuffer)localObject2).toString();
    }
    Object localObject1 = new File(paramString);
    if (!((File)localObject1).exists()) {
      error("agent.err.configfile.notfound", paramString);
    }
    Object localObject2 = null;
    try
    {
      localObject2 = new FileInputStream((File)localObject1);
      BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject2);
      paramProperties.load(localBufferedInputStream);
      return;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      error("agent.err.configfile.failed", localFileNotFoundException.getMessage());
    }
    catch (IOException localIOException3)
    {
      error("agent.err.configfile.failed", localIOException3.getMessage());
    }
    catch (SecurityException localSecurityException)
    {
      error("agent.err.configfile.access.denied", paramString);
    }
    finally
    {
      if (localObject2 != null) {
        try
        {
          ((InputStream)localObject2).close();
        }
        catch (IOException localIOException6)
        {
          error("agent.err.configfile.closed.failed", paramString);
        }
      }
    }
  }
  
  public static void startAgent()
    throws Exception
  {
    String str1 = System.getProperty("com.sun.management.agent.class");
    if (str1 == null)
    {
      localObject1 = getManagementProperties();
      if (localObject1 != null) {
        startAgent((Properties)localObject1);
      }
      return;
    }
    Object localObject1 = str1.split(":");
    if ((localObject1.length < 1) || (localObject1.length > 2)) {
      error("agent.err.invalid.agentclass", "\"" + str1 + "\"");
    }
    String str2 = localObject1[0];
    Object localObject2 = localObject1.length == 2 ? localObject1[1] : null;
    if ((str2 == null) || (str2.length() == 0)) {
      error("agent.err.invalid.agentclass", "\"" + str1 + "\"");
    }
    if (str2 != null) {
      try
      {
        Class localClass = ClassLoader.getSystemClassLoader().loadClass(str2);
        localObject3 = localClass.getMethod("premain", new Class[] { String.class });
        ((Method)localObject3).invoke(null, new Object[] { localObject2 });
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        error("agent.err.agentclass.notfound", "\"" + str2 + "\"");
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        error("agent.err.premain.notfound", "\"" + str2 + "\"");
      }
      catch (SecurityException localSecurityException)
      {
        error("agent.err.agentclass.access.denied");
      }
      catch (Exception localException)
      {
        Object localObject3 = localException.getCause() == null ? localException.getMessage() : localException.getCause().getMessage();
        error("agent.err.agentclass.failed", (String)localObject3);
      }
    }
  }
  
  public static void error(String paramString)
  {
    String str = getText(paramString);
    System.err.print(getText("agent.err.error") + ": " + str);
    throw new RuntimeException(str);
  }
  
  public static void error(String paramString, String[] paramArrayOfString)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
    {
      error(paramString);
    }
    else
    {
      StringBuffer localStringBuffer = new StringBuffer(paramArrayOfString[0]);
      for (int i = 1; i < paramArrayOfString.length; i++) {
        localStringBuffer.append(" " + paramArrayOfString[i]);
      }
      error(paramString, localStringBuffer.toString());
    }
  }
  
  public static void error(String paramString1, String paramString2)
  {
    String str = getText(paramString1);
    System.err.print(getText("agent.err.error") + ": " + str);
    System.err.println(": " + paramString2);
    throw new RuntimeException(str);
  }
  
  public static void error(Exception paramException)
  {
    paramException.printStackTrace();
    System.err.println(getText("agent.err.exception") + ": " + paramException.toString());
    throw new RuntimeException(paramException);
  }
  
  public static void warning(String paramString1, String paramString2)
  {
    System.err.print(getText("agent.err.warning") + ": " + getText(paramString1));
    System.err.println(": " + paramString2);
  }
  
  private static void initResource()
  {
    try
    {
      messageRB = ResourceBundle.getBundle("sun.management.resources.agent");
    }
    catch (MissingResourceException localMissingResourceException)
    {
      throw new Error("Fatal: Resource for management agent is missing");
    }
  }
  
  public static String getText(String paramString)
  {
    if (messageRB == null) {
      initResource();
    }
    try
    {
      return messageRB.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException) {}
    return "Missing management agent resource bundle: key = \"" + paramString + "\"";
  }
  
  public static String getText(String paramString, String... paramVarArgs)
  {
    if (messageRB == null) {
      initResource();
    }
    String str = messageRB.getString(paramString);
    if (str == null) {
      str = "missing resource key: key = \"" + paramString + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
    }
    return MessageFormat.format(str, (Object[])paramVarArgs);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\Agent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */