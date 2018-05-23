package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.ArrayNotificationBuffer;
import com.sun.jmx.remote.internal.NotificationBuffer;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import com.sun.jmx.remote.util.ClassLogger;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.remote.JMXAuthenticator;
import javax.security.auth.Subject;

public abstract class RMIServerImpl
  implements Closeable, RMIServer
{
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIServerImpl");
  private final List<WeakReference<RMIConnection>> clientList = new ArrayList();
  private ClassLoader cl;
  private MBeanServer mbeanServer;
  private final Map<String, ?> env;
  private RMIConnectorServer connServer;
  private static int connectionIdNumber;
  private NotificationBuffer notifBuffer;
  
  public RMIServerImpl(Map<String, ?> paramMap)
  {
    env = (paramMap == null ? Collections.emptyMap() : paramMap);
  }
  
  void setRMIConnectorServer(RMIConnectorServer paramRMIConnectorServer)
    throws IOException
  {
    connServer = paramRMIConnectorServer;
  }
  
  protected abstract void export()
    throws IOException;
  
  public abstract Remote toStub()
    throws IOException;
  
  public synchronized void setDefaultClassLoader(ClassLoader paramClassLoader)
  {
    cl = paramClassLoader;
  }
  
  public synchronized ClassLoader getDefaultClassLoader()
  {
    return cl;
  }
  
  public synchronized void setMBeanServer(MBeanServer paramMBeanServer)
  {
    mbeanServer = paramMBeanServer;
  }
  
  public synchronized MBeanServer getMBeanServer()
  {
    return mbeanServer;
  }
  
  public String getVersion()
  {
    try
    {
      return "1.0 java_runtime_" + System.getProperty("java.runtime.version");
    }
    catch (SecurityException localSecurityException) {}
    return "1.0 ";
  }
  
  public RMIConnection newClient(Object paramObject)
    throws IOException
  {
    return doNewClient(paramObject);
  }
  
  RMIConnection doNewClient(Object paramObject)
    throws IOException
  {
    boolean bool = logger.traceOn();
    if (bool) {
      logger.trace("newClient", "making new client");
    }
    if (getMBeanServer() == null) {
      throw new IllegalStateException("Not attached to an MBean server");
    }
    Subject localSubject = null;
    Object localObject1 = (JMXAuthenticator)env.get("jmx.remote.authenticator");
    if ((localObject1 == null) && ((env.get("jmx.remote.x.password.file") != null) || (env.get("jmx.remote.x.login.config") != null))) {
      localObject1 = new JMXPluggableAuthenticator(env);
    }
    if (localObject1 != null)
    {
      if (bool) {
        logger.trace("newClient", "got authenticator: " + localObject1.getClass().getName());
      }
      try
      {
        localSubject = ((JMXAuthenticator)localObject1).authenticate(paramObject);
      }
      catch (SecurityException localSecurityException)
      {
        logger.trace("newClient", "Authentication failed: " + localSecurityException);
        throw localSecurityException;
      }
    }
    if (bool) {
      if (localSubject != null) {
        logger.trace("newClient", "subject is not null");
      } else {
        logger.trace("newClient", "no subject");
      }
    }
    String str = makeConnectionId(getProtocol(), localSubject);
    if (bool) {
      logger.trace("newClient", "making new connection: " + str);
    }
    RMIConnection localRMIConnection = makeClient(str, localSubject);
    dropDeadReferences();
    WeakReference localWeakReference = new WeakReference(localRMIConnection);
    synchronized (clientList)
    {
      clientList.add(localWeakReference);
    }
    connServer.connectionOpened(str, "Connection opened", null);
    synchronized (clientList)
    {
      if (!clientList.contains(localWeakReference)) {
        throw new IOException("The connection is refused.");
      }
    }
    if (bool) {
      logger.trace("newClient", "new connection done: " + str);
    }
    return localRMIConnection;
  }
  
  protected abstract RMIConnection makeClient(String paramString, Subject paramSubject)
    throws IOException;
  
  protected abstract void closeClient(RMIConnection paramRMIConnection)
    throws IOException;
  
  protected abstract String getProtocol();
  
  protected void clientClosed(RMIConnection paramRMIConnection)
    throws IOException
  {
    boolean bool = logger.debugOn();
    if (bool) {
      logger.trace("clientClosed", "client=" + paramRMIConnection);
    }
    if (paramRMIConnection == null) {
      throw new NullPointerException("Null client");
    }
    synchronized (clientList)
    {
      dropDeadReferences();
      Iterator localIterator = clientList.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        if (localWeakReference.get() == paramRMIConnection)
        {
          localIterator.remove();
          break;
        }
      }
    }
    if (bool) {
      logger.trace("clientClosed", "closing client.");
    }
    closeClient(paramRMIConnection);
    if (bool) {
      logger.trace("clientClosed", "sending notif");
    }
    connServer.connectionClosed(paramRMIConnection.getConnectionId(), "Client connection closed", null);
    if (bool) {
      logger.trace("clientClosed", "done");
    }
  }
  
  public synchronized void close()
    throws IOException
  {
    boolean bool1 = logger.traceOn();
    boolean bool2 = logger.debugOn();
    if (bool1) {
      logger.trace("close", "closing");
    }
    Object localObject1 = null;
    try
    {
      if (bool2) {
        logger.debug("close", "closing Server");
      }
      closeServer();
    }
    catch (IOException localIOException1)
    {
      if (bool1) {
        logger.trace("close", "Failed to close server: " + localIOException1);
      }
      if (bool2) {
        logger.debug("close", localIOException1);
      }
      localObject1 = localIOException1;
    }
    if (bool2) {
      logger.debug("close", "closing Clients");
    }
    for (;;)
    {
      synchronized (clientList)
      {
        if (bool2) {
          logger.debug("close", "droping dead references");
        }
        dropDeadReferences();
        if (bool2) {
          logger.debug("close", "client count: " + clientList.size());
        }
        if (clientList.size() == 0) {
          break;
        }
        Iterator localIterator = clientList.iterator();
        if (localIterator.hasNext())
        {
          WeakReference localWeakReference = (WeakReference)localIterator.next();
          RMIConnection localRMIConnection = (RMIConnection)localWeakReference.get();
          localIterator.remove();
          if (localRMIConnection != null) {
            try
            {
              localRMIConnection.close();
            }
            catch (IOException localIOException2)
            {
              if (bool1) {
                logger.trace("close", "Failed to close client: " + localIOException2);
              }
              if (bool2) {
                logger.debug("close", localIOException2);
              }
              if (localObject1 == null) {
                localObject1 = localIOException2;
              }
            }
          } else {
            continue;
          }
        }
      }
    }
    if (notifBuffer != null) {
      notifBuffer.dispose();
    }
    if (localObject1 != null)
    {
      if (bool1) {
        logger.trace("close", "close failed.");
      }
      throw ((Throwable)localObject1);
    }
    if (bool1) {
      logger.trace("close", "closed.");
    }
  }
  
  protected abstract void closeServer()
    throws IOException;
  
  private static synchronized String makeConnectionId(String paramString, Subject paramSubject)
  {
    connectionIdNumber += 1;
    String str1 = "";
    try
    {
      str1 = RemoteServer.getClientHost();
      if (str1.contains(":")) {
        str1 = "[" + str1 + "]";
      }
    }
    catch (ServerNotActiveException localServerNotActiveException)
    {
      logger.trace("makeConnectionId", "getClientHost", localServerNotActiveException);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString).append(":");
    if (str1.length() > 0) {
      localStringBuilder.append("//").append(str1);
    }
    localStringBuilder.append(" ");
    if (paramSubject != null)
    {
      Set localSet = paramSubject.getPrincipals();
      String str2 = "";
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Principal localPrincipal = (Principal)localIterator.next();
        String str3 = localPrincipal.getName().replace(' ', '_').replace(';', ':');
        localStringBuilder.append(str2).append(str3);
        str2 = ";";
      }
    }
    localStringBuilder.append(" ").append(connectionIdNumber);
    if (logger.traceOn()) {
      logger.trace("newConnectionId", "connectionId=" + localStringBuilder);
    }
    return localStringBuilder.toString();
  }
  
  private void dropDeadReferences()
  {
    synchronized (clientList)
    {
      Iterator localIterator = clientList.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        if (localWeakReference.get() == null) {
          localIterator.remove();
        }
      }
    }
  }
  
  synchronized NotificationBuffer getNotifBuffer()
  {
    if (notifBuffer == null) {
      notifBuffer = ArrayNotificationBuffer.getNotificationBuffer(mbeanServer, env);
    }
    return notifBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\rmi\RMIServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */