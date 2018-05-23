package jdk.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import jdk.Exported;
import sun.net.ExtendedOptionsImpl;

@Exported
public class Sockets
{
  private static final HashMap<Class<?>, Set<SocketOption<?>>> options = new HashMap();
  private static Method siSetOption;
  private static Method siGetOption;
  private static Method dsiSetOption;
  private static Method dsiGetOption;
  
  private static void initMethods()
  {
    try
    {
      Class localClass = Class.forName("java.net.SocketSecrets");
      siSetOption = localClass.getDeclaredMethod("setOption", new Class[] { Object.class, SocketOption.class, Object.class });
      siSetOption.setAccessible(true);
      siGetOption = localClass.getDeclaredMethod("getOption", new Class[] { Object.class, SocketOption.class });
      siGetOption.setAccessible(true);
      dsiSetOption = localClass.getDeclaredMethod("setOption", new Class[] { DatagramSocket.class, SocketOption.class, Object.class });
      dsiSetOption.setAccessible(true);
      dsiGetOption = localClass.getDeclaredMethod("getOption", new Class[] { DatagramSocket.class, SocketOption.class });
      dsiGetOption.setAccessible(true);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new InternalError(localReflectiveOperationException);
    }
  }
  
  private static <T> void invokeSet(Method paramMethod, Object paramObject, SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    try
    {
      paramMethod.invoke(null, new Object[] { paramObject, paramSocketOption, paramT });
    }
    catch (Exception localException)
    {
      if ((localException instanceof InvocationTargetException))
      {
        Throwable localThrowable = ((InvocationTargetException)localException).getTargetException();
        if ((localThrowable instanceof IOException)) {
          throw ((IOException)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
      }
      throw new RuntimeException(localException);
    }
  }
  
  private static <T> T invokeGet(Method paramMethod, Object paramObject, SocketOption<T> paramSocketOption)
    throws IOException
  {
    try
    {
      return (T)paramMethod.invoke(null, new Object[] { paramObject, paramSocketOption });
    }
    catch (Exception localException)
    {
      if ((localException instanceof InvocationTargetException))
      {
        Throwable localThrowable = ((InvocationTargetException)localException).getTargetException();
        if ((localThrowable instanceof IOException)) {
          throw ((IOException)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
      }
      throw new RuntimeException(localException);
    }
  }
  
  private Sockets() {}
  
  public static <T> void setOption(Socket paramSocket, SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    if (!isSupported(Socket.class, paramSocketOption)) {
      throw new UnsupportedOperationException(paramSocketOption.name());
    }
    invokeSet(siSetOption, paramSocket, paramSocketOption, paramT);
  }
  
  public static <T> T getOption(Socket paramSocket, SocketOption<T> paramSocketOption)
    throws IOException
  {
    if (!isSupported(Socket.class, paramSocketOption)) {
      throw new UnsupportedOperationException(paramSocketOption.name());
    }
    return (T)invokeGet(siGetOption, paramSocket, paramSocketOption);
  }
  
  public static <T> void setOption(ServerSocket paramServerSocket, SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    if (!isSupported(ServerSocket.class, paramSocketOption)) {
      throw new UnsupportedOperationException(paramSocketOption.name());
    }
    invokeSet(siSetOption, paramServerSocket, paramSocketOption, paramT);
  }
  
  public static <T> T getOption(ServerSocket paramServerSocket, SocketOption<T> paramSocketOption)
    throws IOException
  {
    if (!isSupported(ServerSocket.class, paramSocketOption)) {
      throw new UnsupportedOperationException(paramSocketOption.name());
    }
    return (T)invokeGet(siGetOption, paramServerSocket, paramSocketOption);
  }
  
  public static <T> void setOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    if (!isSupported(paramDatagramSocket.getClass(), paramSocketOption)) {
      throw new UnsupportedOperationException(paramSocketOption.name());
    }
    invokeSet(dsiSetOption, paramDatagramSocket, paramSocketOption, paramT);
  }
  
  public static <T> T getOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption)
    throws IOException
  {
    if (!isSupported(paramDatagramSocket.getClass(), paramSocketOption)) {
      throw new UnsupportedOperationException(paramSocketOption.name());
    }
    return (T)invokeGet(dsiGetOption, paramDatagramSocket, paramSocketOption);
  }
  
  public static Set<SocketOption<?>> supportedOptions(Class<?> paramClass)
  {
    Set localSet = (Set)options.get(paramClass);
    if (localSet == null) {
      throw new IllegalArgumentException("unknown socket type");
    }
    return localSet;
  }
  
  private static boolean isSupported(Class<?> paramClass, SocketOption<?> paramSocketOption)
  {
    Set localSet = supportedOptions(paramClass);
    return localSet.contains(paramSocketOption);
  }
  
  private static void initOptionSets()
  {
    boolean bool = ExtendedOptionsImpl.flowSupported();
    Object localObject = new HashSet();
    ((Set)localObject).add(StandardSocketOptions.SO_KEEPALIVE);
    ((Set)localObject).add(StandardSocketOptions.SO_SNDBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_RCVBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_REUSEADDR);
    ((Set)localObject).add(StandardSocketOptions.SO_LINGER);
    ((Set)localObject).add(StandardSocketOptions.IP_TOS);
    ((Set)localObject).add(StandardSocketOptions.TCP_NODELAY);
    if (bool) {
      ((Set)localObject).add(ExtendedSocketOptions.SO_FLOW_SLA);
    }
    localObject = Collections.unmodifiableSet((Set)localObject);
    options.put(Socket.class, localObject);
    localObject = new HashSet();
    ((Set)localObject).add(StandardSocketOptions.SO_RCVBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_REUSEADDR);
    ((Set)localObject).add(StandardSocketOptions.IP_TOS);
    localObject = Collections.unmodifiableSet((Set)localObject);
    options.put(ServerSocket.class, localObject);
    localObject = new HashSet();
    ((Set)localObject).add(StandardSocketOptions.SO_SNDBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_RCVBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_REUSEADDR);
    ((Set)localObject).add(StandardSocketOptions.IP_TOS);
    if (bool) {
      ((Set)localObject).add(ExtendedSocketOptions.SO_FLOW_SLA);
    }
    localObject = Collections.unmodifiableSet((Set)localObject);
    options.put(DatagramSocket.class, localObject);
    localObject = new HashSet();
    ((Set)localObject).add(StandardSocketOptions.SO_SNDBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_RCVBUF);
    ((Set)localObject).add(StandardSocketOptions.SO_REUSEADDR);
    ((Set)localObject).add(StandardSocketOptions.IP_TOS);
    ((Set)localObject).add(StandardSocketOptions.IP_MULTICAST_IF);
    ((Set)localObject).add(StandardSocketOptions.IP_MULTICAST_TTL);
    ((Set)localObject).add(StandardSocketOptions.IP_MULTICAST_LOOP);
    if (bool) {
      ((Set)localObject).add(ExtendedSocketOptions.SO_FLOW_SLA);
    }
    localObject = Collections.unmodifiableSet((Set)localObject);
    options.put(MulticastSocket.class, localObject);
  }
  
  static
  {
    initOptionSets();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Sockets.access$000();
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\net\Sockets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */