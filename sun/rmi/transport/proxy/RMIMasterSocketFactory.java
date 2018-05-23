package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.server.LogStream;
import java.rmi.server.RMISocketFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Hashtable;
import java.util.Vector;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

public class RMIMasterSocketFactory
  extends RMISocketFactory
{
  static int logLevel = LogStream.parseLevel(getLogLevel());
  static final Log proxyLog = Log.getLog("sun.rmi.transport.tcp.proxy", "transport", logLevel);
  private static long connectTimeout = getConnectTimeout();
  private static final boolean eagerHttpFallback = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.transport.proxy.eagerHttpFallback"))).booleanValue();
  private Hashtable<String, RMISocketFactory> successTable = new Hashtable();
  private static final int MaxRememberedHosts = 64;
  private Vector<String> hostList = new Vector(64);
  protected RMISocketFactory initialFactory = new RMIDirectSocketFactory();
  protected Vector<RMISocketFactory> altFactoryList = new Vector(2);
  
  private static String getLogLevel()
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.proxy.logLevel"));
  }
  
  private static long getConnectTimeout()
  {
    return ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.proxy.connectTimeout", 15000L))).longValue();
  }
  
  public RMIMasterSocketFactory()
  {
    int i = 0;
    try
    {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyHost"));
      if (str == null) {
        str = (String)AccessController.doPrivileged(new GetPropertyAction("proxyHost"));
      }
      boolean bool = ((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.disableHttp", "true"))).equalsIgnoreCase("true");
      if ((!bool) && (str != null) && (str.length() > 0)) {
        i = 1;
      }
    }
    catch (Exception localException) {}
    if (i != 0)
    {
      altFactoryList.addElement(new RMIHttpToPortSocketFactory());
      altFactoryList.addElement(new RMIHttpToCGISocketFactory());
    }
  }
  
  public Socket createSocket(String paramString, int paramInt)
    throws IOException
  {
    if (proxyLog.isLoggable(Log.BRIEF)) {
      proxyLog.log(Log.BRIEF, "host: " + paramString + ", port: " + paramInt);
    }
    if (altFactoryList.size() == 0) {
      return initialFactory.createSocket(paramString, paramInt);
    }
    RMISocketFactory localRMISocketFactory = (RMISocketFactory)successTable.get(paramString);
    if (localRMISocketFactory != null)
    {
      if (proxyLog.isLoggable(Log.BRIEF)) {
        proxyLog.log(Log.BRIEF, "previously successful factory found: " + localRMISocketFactory);
      }
      return localRMISocketFactory.createSocket(paramString, paramInt);
    }
    Socket localSocket1 = null;
    Socket localSocket2 = null;
    AsyncConnector localAsyncConnector = new AsyncConnector(initialFactory, paramString, paramInt, AccessController.getContext());
    Object localObject1 = null;
    try
    {
      synchronized (localAsyncConnector)
      {
        Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(localAsyncConnector, "AsyncConnector", true));
        localThread.start();
        try
        {
          long l1 = System.currentTimeMillis();
          long l2 = l1 + connectTimeout;
          do
          {
            localAsyncConnector.wait(l2 - l1);
            localSocket1 = checkConnector(localAsyncConnector);
            if (localSocket1 != null) {
              break;
            }
            l1 = System.currentTimeMillis();
          } while (l1 < l2);
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new InterruptedIOException("interrupted while waiting for connector");
        }
      }
      if (localSocket1 == null) {
        throw new NoRouteToHostException("connect timed out: " + paramString);
      }
      proxyLog.log(Log.BRIEF, "direct socket connection successful");
      ??? = localSocket1;
      int i;
      Socket localSocket5;
      InputStream localInputStream2;
      int k;
      return (Socket)???;
    }
    catch (UnknownHostException|NoRouteToHostException ???)
    {
      Object localObject3;
      localObject1 = ???;
      if (localObject1 != null)
      {
        if (proxyLog.isLoggable(Log.BRIEF)) {
          proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
        }
        for (??? = 0; ??? < altFactoryList.size(); ???++)
        {
          localRMISocketFactory = (RMISocketFactory)altFactoryList.elementAt(???);
          if (proxyLog.isLoggable(Log.BRIEF)) {
            proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
          }
          try
          {
            Socket localSocket3 = localRMISocketFactory.createSocket(paramString, paramInt);
            localObject2 = null;
            try
            {
              localObject3 = localSocket3.getInputStream();
              j = ((InputStream)localObject3).read();
            }
            catch (Throwable localThrowable2)
            {
              localObject2 = localThrowable2;
              throw localThrowable2;
            }
            finally
            {
              if (localSocket3 != null) {
                if (localObject2 != null) {
                  try
                  {
                    localSocket3.close();
                  }
                  catch (Throwable localThrowable8)
                  {
                    ((Throwable)localObject2).addSuppressed(localThrowable8);
                  }
                } else {
                  localSocket3.close();
                }
              }
            }
          }
          catch (IOException localIOException1)
          {
            if (proxyLog.isLoggable(Log.BRIEF)) {
              proxyLog.log(Log.BRIEF, "factory failed: ", localIOException1);
            }
            continue;
          }
          proxyLog.log(Log.BRIEF, "factory succeeded");
          try
          {
            localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
          }
          catch (IOException localIOException2) {}
        }
      }
    }
    catch (SocketException ???)
    {
      Object localObject2;
      int j;
      if (eagerHttpFallback) {
        localObject1 = ???;
      } else {
        throw ((Throwable)???);
      }
      if (localObject1 != null)
      {
        if (proxyLog.isLoggable(Log.BRIEF)) {
          proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
        }
        for (??? = 0; ??? < altFactoryList.size(); ???++)
        {
          localRMISocketFactory = (RMISocketFactory)altFactoryList.elementAt(???);
          if (proxyLog.isLoggable(Log.BRIEF)) {
            proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
          }
          try
          {
            Socket localSocket4 = localRMISocketFactory.createSocket(paramString, paramInt);
            localObject2 = null;
            try
            {
              InputStream localInputStream1 = localSocket4.getInputStream();
              j = localInputStream1.read();
            }
            catch (Throwable localThrowable4)
            {
              localObject2 = localThrowable4;
              throw localThrowable4;
            }
            finally
            {
              if (localSocket4 != null) {
                if (localObject2 != null) {
                  try
                  {
                    localSocket4.close();
                  }
                  catch (Throwable localThrowable9)
                  {
                    ((Throwable)localObject2).addSuppressed(localThrowable9);
                  }
                } else {
                  localSocket4.close();
                }
              }
            }
          }
          catch (IOException localIOException3)
          {
            if (proxyLog.isLoggable(Log.BRIEF)) {
              proxyLog.log(Log.BRIEF, "factory failed: ", localIOException3);
            }
            continue;
          }
          proxyLog.log(Log.BRIEF, "factory succeeded");
          try
          {
            localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
          }
          catch (IOException localIOException4) {}
        }
      }
    }
    finally
    {
      if (localObject1 != null)
      {
        if (proxyLog.isLoggable(Log.BRIEF)) {
          proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
        }
        for (int m = 0; m < altFactoryList.size(); m++)
        {
          localRMISocketFactory = (RMISocketFactory)altFactoryList.elementAt(m);
          if (proxyLog.isLoggable(Log.BRIEF)) {
            proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
          }
          try
          {
            Socket localSocket6 = localRMISocketFactory.createSocket(paramString, paramInt);
            Object localObject9 = null;
            try
            {
              InputStream localInputStream3 = localSocket6.getInputStream();
              int n = localInputStream3.read();
            }
            catch (Throwable localThrowable11)
            {
              localObject9 = localThrowable11;
              throw localThrowable11;
            }
            finally
            {
              if (localSocket6 != null) {
                if (localObject9 != null) {
                  try
                  {
                    localSocket6.close();
                  }
                  catch (Throwable localThrowable12)
                  {
                    ((Throwable)localObject9).addSuppressed(localThrowable12);
                  }
                } else {
                  localSocket6.close();
                }
              }
            }
          }
          catch (IOException localIOException7)
          {
            if (proxyLog.isLoggable(Log.BRIEF)) {
              proxyLog.log(Log.BRIEF, "factory failed: ", localIOException7);
            }
            continue;
          }
          proxyLog.log(Log.BRIEF, "factory succeeded");
          try
          {
            localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
          }
          catch (IOException localIOException8) {}
        }
      }
    }
    synchronized (successTable)
    {
      try
      {
        synchronized (localAsyncConnector)
        {
          localSocket1 = checkConnector(localAsyncConnector);
        }
        if (localSocket1 != null)
        {
          if (localSocket2 != null) {
            localSocket2.close();
          }
          return localSocket1;
        }
        localAsyncConnector.notUsed();
      }
      catch (UnknownHostException|NoRouteToHostException localUnknownHostException)
      {
        localObject1 = localUnknownHostException;
      }
      catch (SocketException localSocketException)
      {
        if (eagerHttpFallback) {
          localObject1 = localSocketException;
        } else {
          throw localSocketException;
        }
      }
      if (localSocket2 != null)
      {
        rememberFactory(paramString, localRMISocketFactory);
        return localSocket2;
      }
      throw ((Throwable)localObject1);
    }
  }
  
  void rememberFactory(String paramString, RMISocketFactory paramRMISocketFactory)
  {
    synchronized (successTable)
    {
      while (hostList.size() >= 64)
      {
        successTable.remove(hostList.elementAt(0));
        hostList.removeElementAt(0);
      }
      hostList.addElement(paramString);
      successTable.put(paramString, paramRMISocketFactory);
    }
  }
  
  Socket checkConnector(AsyncConnector paramAsyncConnector)
    throws IOException
  {
    Exception localException = paramAsyncConnector.getException();
    if (localException != null)
    {
      localException.fillInStackTrace();
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      if ((localException instanceof RuntimeException)) {
        throw ((RuntimeException)localException);
      }
      throw new Error("internal error: unexpected checked exception: " + localException.toString());
    }
    return paramAsyncConnector.getSocket();
  }
  
  public ServerSocket createServerSocket(int paramInt)
    throws IOException
  {
    return initialFactory.createServerSocket(paramInt);
  }
  
  private class AsyncConnector
    implements Runnable
  {
    private RMISocketFactory factory;
    private String host;
    private int port;
    private AccessControlContext acc;
    private Exception exception = null;
    private Socket socket = null;
    private boolean cleanUp = false;
    
    AsyncConnector(RMISocketFactory paramRMISocketFactory, String paramString, int paramInt, AccessControlContext paramAccessControlContext)
    {
      factory = paramRMISocketFactory;
      host = paramString;
      port = paramInt;
      acc = paramAccessControlContext;
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkConnect(paramString, paramInt);
      }
    }
    
    public void run()
    {
      try
      {
        Socket localSocket = factory.createSocket(host, port);
        synchronized (this)
        {
          socket = localSocket;
          notify();
        }
        rememberFactory(host, factory);
        synchronized (this)
        {
          if (cleanUp) {
            try
            {
              socket.close();
            }
            catch (IOException localIOException) {}
          }
        }
      }
      catch (Exception localException)
      {
        synchronized (this)
        {
          exception = localException;
          notify();
        }
      }
    }
    
    private synchronized Exception getException()
    {
      return exception;
    }
    
    private synchronized Socket getSocket()
    {
      return socket;
    }
    
    synchronized void notUsed()
    {
      if (socket != null) {
        try
        {
          socket.close();
        }
        catch (IOException localIOException) {}
      }
      cleanUp = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\RMIMasterSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */