package java.nio.channels.spi;

import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.nio.ch.DefaultSelectorProvider;

public abstract class SelectorProvider
{
  private static final Object lock = new Object();
  private static SelectorProvider provider = null;
  
  protected SelectorProvider()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("selectorProvider"));
    }
  }
  
  private static boolean loadProviderFromProperty()
  {
    String str = System.getProperty("java.nio.channels.spi.SelectorProvider");
    if (str == null) {
      return false;
    }
    try
    {
      Class localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
      provider = (SelectorProvider)localClass.newInstance();
      return true;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ServiceConfigurationError(null, localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ServiceConfigurationError(null, localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new ServiceConfigurationError(null, localInstantiationException);
    }
    catch (SecurityException localSecurityException)
    {
      throw new ServiceConfigurationError(null, localSecurityException);
    }
  }
  
  private static boolean loadProviderAsService()
  {
    ServiceLoader localServiceLoader = ServiceLoader.load(SelectorProvider.class, ClassLoader.getSystemClassLoader());
    Iterator localIterator = localServiceLoader.iterator();
    do
    {
      try
      {
        if (!localIterator.hasNext()) {
          return false;
        }
        provider = (SelectorProvider)localIterator.next();
        return true;
      }
      catch (ServiceConfigurationError localServiceConfigurationError) {}
    } while ((localServiceConfigurationError.getCause() instanceof SecurityException));
    throw localServiceConfigurationError;
  }
  
  public static SelectorProvider provider()
  {
    synchronized (lock)
    {
      if (provider != null) {
        return provider;
      }
      (SelectorProvider)AccessController.doPrivileged(new PrivilegedAction()
      {
        public SelectorProvider run()
        {
          if (SelectorProvider.access$000()) {
            return SelectorProvider.provider;
          }
          if (SelectorProvider.access$200()) {
            return SelectorProvider.provider;
          }
          SelectorProvider.access$102(DefaultSelectorProvider.create());
          return SelectorProvider.provider;
        }
      });
    }
  }
  
  public abstract DatagramChannel openDatagramChannel()
    throws IOException;
  
  public abstract DatagramChannel openDatagramChannel(ProtocolFamily paramProtocolFamily)
    throws IOException;
  
  public abstract Pipe openPipe()
    throws IOException;
  
  public abstract AbstractSelector openSelector()
    throws IOException;
  
  public abstract ServerSocketChannel openServerSocketChannel()
    throws IOException;
  
  public abstract SocketChannel openSocketChannel()
    throws IOException;
  
  public Channel inheritedChannel()
    throws IOException
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\spi\SelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */