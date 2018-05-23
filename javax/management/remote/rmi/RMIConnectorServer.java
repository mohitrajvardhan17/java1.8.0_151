package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.IIOPHelper;
import com.sun.jmx.remote.security.MBeanServerFileAccessController;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RMIConnectorServer
  extends JMXConnectorServer
{
  public static final String JNDI_REBIND_ATTRIBUTE = "jmx.remote.jndi.rebind";
  public static final String RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.client.socket.factory";
  public static final String RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.server.socket.factory";
  private static final char[] intToAlpha = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectorServer");
  private JMXServiceURL address;
  private RMIServerImpl rmiServerImpl;
  private final Map<String, ?> attributes;
  private ClassLoader defaultClassLoader = null;
  private String boundJndiUrl;
  private static final int CREATED = 0;
  private static final int STARTED = 1;
  private static final int STOPPED = 2;
  private int state = 0;
  private static final Set<RMIConnectorServer> openedServers = new HashSet();
  
  public RMIConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
    throws IOException
  {
    this(paramJMXServiceURL, paramMap, (MBeanServer)null);
  }
  
  public RMIConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer)
    throws IOException
  {
    this(paramJMXServiceURL, paramMap, (RMIServerImpl)null, paramMBeanServer);
  }
  
  public RMIConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, RMIServerImpl paramRMIServerImpl, MBeanServer paramMBeanServer)
    throws IOException
  {
    super(paramMBeanServer);
    if (paramJMXServiceURL == null) {
      throw new IllegalArgumentException("Null JMXServiceURL");
    }
    if (paramRMIServerImpl == null)
    {
      String str1 = paramJMXServiceURL.getProtocol();
      if ((str1 == null) || ((!str1.equals("rmi")) && (!str1.equals("iiop"))))
      {
        str2 = "Invalid protocol type: " + str1;
        throw new MalformedURLException(str2);
      }
      String str2 = paramJMXServiceURL.getURLPath();
      if ((!str2.equals("")) && (!str2.equals("/")) && (!str2.startsWith("/jndi/"))) {
        throw new MalformedURLException("URL path must be empty or start with /jndi/");
      }
    }
    if (paramMap == null)
    {
      attributes = Collections.emptyMap();
    }
    else
    {
      EnvHelp.checkAttributes(paramMap);
      attributes = Collections.unmodifiableMap(paramMap);
    }
    address = paramJMXServiceURL;
    rmiServerImpl = paramRMIServerImpl;
  }
  
  public JMXConnector toJMXConnector(Map<String, ?> paramMap)
    throws IOException
  {
    if (!isActive()) {
      throw new IllegalStateException("Connector is not active");
    }
    Object localObject = new HashMap(attributes == null ? Collections.emptyMap() : attributes);
    if (paramMap != null)
    {
      EnvHelp.checkAttributes(paramMap);
      ((Map)localObject).putAll(paramMap);
    }
    localObject = EnvHelp.filterAttributes((Map)localObject);
    RMIServer localRMIServer = (RMIServer)rmiServerImpl.toStub();
    return new RMIConnector(localRMIServer, (Map)localObject);
  }
  
  public synchronized void start()
    throws IOException
  {
    boolean bool1 = logger.traceOn();
    if (state == 1)
    {
      if (bool1) {
        logger.trace("start", "already started");
      }
      return;
    }
    if (state == 2)
    {
      if (bool1) {
        logger.trace("start", "already stopped");
      }
      throw new IOException("The server has been stopped.");
    }
    if (getMBeanServer() == null) {
      throw new IllegalStateException("This connector server is not attached to an MBean server");
    }
    Object localObject1;
    if (attributes != null)
    {
      String str1 = (String)attributes.get("jmx.remote.x.access.file");
      if (str1 != null)
      {
        try
        {
          localObject1 = new MBeanServerFileAccessController(str1);
        }
        catch (IOException localIOException)
        {
          throw ((IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(localIOException.getMessage()), localIOException));
        }
        setMBeanServerForwarder((MBeanServerForwarder)localObject1);
      }
    }
    try
    {
      if (bool1) {
        logger.trace("start", "setting default class loader");
      }
      defaultClassLoader = EnvHelp.resolveServerClassLoader(attributes, getMBeanServer());
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      localObject1 = new IllegalArgumentException("ClassLoader not found: " + localInstanceNotFoundException);
      throw ((IllegalArgumentException)EnvHelp.initCause((Throwable)localObject1, localInstanceNotFoundException));
    }
    if (bool1) {
      logger.trace("start", "setting RMIServer object");
    }
    RMIServerImpl localRMIServerImpl;
    if (rmiServerImpl != null) {
      localRMIServerImpl = rmiServerImpl;
    } else {
      localRMIServerImpl = newServer();
    }
    localRMIServerImpl.setMBeanServer(getMBeanServer());
    localRMIServerImpl.setDefaultClassLoader(defaultClassLoader);
    localRMIServerImpl.setRMIConnectorServer(this);
    localRMIServerImpl.export();
    try
    {
      if (bool1) {
        logger.trace("start", "getting RMIServer object to export");
      }
      localObject1 = objectToBind(localRMIServerImpl, attributes);
      if ((address != null) && (address.getURLPath().startsWith("/jndi/")))
      {
        String str2 = address.getURLPath().substring(6);
        if (bool1) {
          logger.trace("start", "Using external directory: " + str2);
        }
        String str3 = (String)attributes.get("jmx.remote.jndi.rebind");
        boolean bool2 = EnvHelp.computeBooleanFromString(str3);
        if (bool1) {
          logger.trace("start", "jmx.remote.jndi.rebind=" + bool2);
        }
        try
        {
          if (bool1) {
            logger.trace("start", "binding to " + str2);
          }
          Hashtable localHashtable = EnvHelp.mapToHashtable(attributes);
          bind(str2, localHashtable, (RMIServer)localObject1, bool2);
          boundJndiUrl = str2;
        }
        catch (NamingException localNamingException)
        {
          throw newIOException("Cannot bind to URL [" + str2 + "]: " + localNamingException, localNamingException);
        }
      }
      else
      {
        if (bool1) {
          logger.trace("start", "Encoding URL");
        }
        encodeStubInAddress((RMIServer)localObject1, attributes);
        if (bool1) {
          logger.trace("start", "Encoded URL: " + address);
        }
      }
    }
    catch (Exception localException1)
    {
      try
      {
        localRMIServerImpl.close();
      }
      catch (Exception localException2) {}
      if ((localException1 instanceof RuntimeException)) {
        throw ((RuntimeException)localException1);
      }
      if ((localException1 instanceof IOException)) {
        throw ((IOException)localException1);
      }
      throw newIOException("Got unexpected exception while starting the connector server: " + localException1, localException1);
    }
    rmiServerImpl = localRMIServerImpl;
    synchronized (openedServers)
    {
      openedServers.add(this);
    }
    state = 1;
    if (bool1)
    {
      logger.trace("start", "Connector Server Address = " + address);
      logger.trace("start", "started.");
    }
  }
  
  public void stop()
    throws IOException
  {
    boolean bool = logger.traceOn();
    synchronized (this)
    {
      if (state == 2)
      {
        if (bool) {
          logger.trace("stop", "already stopped.");
        }
        return;
      }
      if ((state == 0) && (bool)) {
        logger.trace("stop", "not started yet.");
      }
      if (bool) {
        logger.trace("stop", "stopping.");
      }
      state = 2;
    }
    synchronized (openedServers)
    {
      openedServers.remove(this);
    }
    ??? = null;
    if (rmiServerImpl != null) {
      try
      {
        if (bool) {
          logger.trace("stop", "closing RMI server.");
        }
        rmiServerImpl.close();
      }
      catch (IOException localIOException)
      {
        if (bool) {
          logger.trace("stop", "failed to close RMI server: " + localIOException);
        }
        if (logger.debugOn()) {
          logger.debug("stop", localIOException);
        }
        ??? = localIOException;
      }
    }
    if (boundJndiUrl != null) {
      try
      {
        if (bool) {
          logger.trace("stop", "unbind from external directory: " + boundJndiUrl);
        }
        Hashtable localHashtable = EnvHelp.mapToHashtable(attributes);
        InitialContext localInitialContext = new InitialContext(localHashtable);
        localInitialContext.unbind(boundJndiUrl);
        localInitialContext.close();
      }
      catch (NamingException localNamingException)
      {
        if (bool) {
          logger.trace("stop", "failed to unbind RMI server: " + localNamingException);
        }
        if (logger.debugOn()) {
          logger.debug("stop", localNamingException);
        }
        if (??? == null) {
          ??? = newIOException("Cannot bind to URL: " + localNamingException, localNamingException);
        }
      }
    }
    if (??? != null) {
      throw ((Throwable)???);
    }
    if (bool) {
      logger.trace("stop", "stopped");
    }
  }
  
  public synchronized boolean isActive()
  {
    return state == 1;
  }
  
  public JMXServiceURL getAddress()
  {
    if (!isActive()) {
      return null;
    }
    return address;
  }
  
  public Map<String, ?> getAttributes()
  {
    Map localMap = EnvHelp.filterAttributes(attributes);
    return Collections.unmodifiableMap(localMap);
  }
  
  public synchronized void setMBeanServerForwarder(MBeanServerForwarder paramMBeanServerForwarder)
  {
    super.setMBeanServerForwarder(paramMBeanServerForwarder);
    if (rmiServerImpl != null) {
      rmiServerImpl.setMBeanServer(getMBeanServer());
    }
  }
  
  protected void connectionOpened(String paramString1, String paramString2, Object paramObject)
  {
    super.connectionOpened(paramString1, paramString2, paramObject);
  }
  
  protected void connectionClosed(String paramString1, String paramString2, Object paramObject)
  {
    super.connectionClosed(paramString1, paramString2, paramObject);
  }
  
  protected void connectionFailed(String paramString1, String paramString2, Object paramObject)
  {
    super.connectionFailed(paramString1, paramString2, paramObject);
  }
  
  void bind(String paramString, Hashtable<?, ?> paramHashtable, RMIServer paramRMIServer, boolean paramBoolean)
    throws NamingException, MalformedURLException
  {
    InitialContext localInitialContext = new InitialContext(paramHashtable);
    if (paramBoolean) {
      localInitialContext.rebind(paramString, paramRMIServer);
    } else {
      localInitialContext.bind(paramString, paramRMIServer);
    }
    localInitialContext.close();
  }
  
  RMIServerImpl newServer()
    throws IOException
  {
    boolean bool = isIiopURL(address, true);
    int i;
    if (address == null) {
      i = 0;
    } else {
      i = address.getPort();
    }
    if (bool) {
      return newIIOPServer(attributes);
    }
    return newJRMPServer(attributes, i);
  }
  
  private void encodeStubInAddress(RMIServer paramRMIServer, Map<String, ?> paramMap)
    throws IOException
  {
    String str1;
    String str2;
    int i;
    if (address == null)
    {
      if (IIOPHelper.isStub(paramRMIServer)) {
        str1 = "iiop";
      } else {
        str1 = "rmi";
      }
      str2 = null;
      i = 0;
    }
    else
    {
      str1 = address.getProtocol();
      str2 = address.getHost().equals("") ? null : address.getHost();
      i = address.getPort();
    }
    String str3 = encodeStub(paramRMIServer, paramMap);
    address = new JMXServiceURL(str1, str2, i, str3);
  }
  
  static boolean isIiopURL(JMXServiceURL paramJMXServiceURL, boolean paramBoolean)
    throws MalformedURLException
  {
    String str = paramJMXServiceURL.getProtocol();
    if (str.equals("rmi")) {
      return false;
    }
    if (str.equals("iiop")) {
      return true;
    }
    if (paramBoolean) {
      throw new MalformedURLException("URL must have protocol \"rmi\" or \"iiop\": \"" + str + "\"");
    }
    return false;
  }
  
  static String encodeStub(RMIServer paramRMIServer, Map<String, ?> paramMap)
    throws IOException
  {
    if (IIOPHelper.isStub(paramRMIServer)) {
      return "/ior/" + encodeIIOPStub(paramRMIServer, paramMap);
    }
    return "/stub/" + encodeJRMPStub(paramRMIServer, paramMap);
  }
  
  static String encodeJRMPStub(RMIServer paramRMIServer, Map<String, ?> paramMap)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
    localObjectOutputStream.writeObject(paramRMIServer);
    localObjectOutputStream.close();
    byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
    return byteArrayToBase64(arrayOfByte);
  }
  
  static String encodeIIOPStub(RMIServer paramRMIServer, Map<String, ?> paramMap)
    throws IOException
  {
    try
    {
      Object localObject = IIOPHelper.getOrb(paramRMIServer);
      return IIOPHelper.objectToString(localObject, paramRMIServer);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw newIOException(localRuntimeException.getMessage(), localRuntimeException);
    }
  }
  
  private static RMIServer objectToBind(RMIServerImpl paramRMIServerImpl, Map<String, ?> paramMap)
    throws IOException
  {
    return RMIConnector.connectStub((RMIServer)paramRMIServerImpl.toStub(), paramMap);
  }
  
  private static RMIServerImpl newJRMPServer(Map<String, ?> paramMap, int paramInt)
    throws IOException
  {
    RMIClientSocketFactory localRMIClientSocketFactory = (RMIClientSocketFactory)paramMap.get("jmx.remote.rmi.client.socket.factory");
    RMIServerSocketFactory localRMIServerSocketFactory = (RMIServerSocketFactory)paramMap.get("jmx.remote.rmi.server.socket.factory");
    return new RMIJRMPServerImpl(paramInt, localRMIClientSocketFactory, localRMIServerSocketFactory, paramMap);
  }
  
  private static RMIServerImpl newIIOPServer(Map<String, ?> paramMap)
    throws IOException
  {
    return new RMIIIOPServerImpl(paramMap);
  }
  
  private static String byteArrayToBase64(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    int j = i / 3;
    int k = i - 3 * j;
    int m = 4 * ((i + 2) / 3);
    StringBuilder localStringBuilder = new StringBuilder(m);
    int n = 0;
    int i2;
    for (int i1 = 0; i1 < j; i1++)
    {
      i2 = paramArrayOfByte[(n++)] & 0xFF;
      int i3 = paramArrayOfByte[(n++)] & 0xFF;
      int i4 = paramArrayOfByte[(n++)] & 0xFF;
      localStringBuilder.append(intToAlpha[(i2 >> 2)]);
      localStringBuilder.append(intToAlpha[(i2 << 4 & 0x3F | i3 >> 4)]);
      localStringBuilder.append(intToAlpha[(i3 << 2 & 0x3F | i4 >> 6)]);
      localStringBuilder.append(intToAlpha[(i4 & 0x3F)]);
    }
    if (k != 0)
    {
      i1 = paramArrayOfByte[(n++)] & 0xFF;
      localStringBuilder.append(intToAlpha[(i1 >> 2)]);
      if (k == 1)
      {
        localStringBuilder.append(intToAlpha[(i1 << 4 & 0x3F)]);
        localStringBuilder.append("==");
      }
      else
      {
        i2 = paramArrayOfByte[(n++)] & 0xFF;
        localStringBuilder.append(intToAlpha[(i1 << 4 & 0x3F | i2 >> 4)]);
        localStringBuilder.append(intToAlpha[(i2 << 2 & 0x3F)]);
        localStringBuilder.append('=');
      }
    }
    return localStringBuilder.toString();
  }
  
  private static IOException newIOException(String paramString, Throwable paramThrowable)
  {
    IOException localIOException = new IOException(paramString);
    return (IOException)EnvHelp.initCause(localIOException, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\rmi\RMIConnectorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */