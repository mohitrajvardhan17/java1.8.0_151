package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

@Deprecated
public class DefaultLoaderRepository
{
  public DefaultLoaderRepository() {}
  
  public static Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClass", paramString);
    return load(null, paramString);
  }
  
  public static Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClassWithout", paramString);
    return load(paramClassLoader, paramString);
  }
  
  private static Class<?> load(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    ArrayList localArrayList = MBeanServerFactory.findMBeanServer(null);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      MBeanServer localMBeanServer = (MBeanServer)localIterator.next();
      ClassLoaderRepository localClassLoaderRepository = localMBeanServer.getClassLoaderRepository();
      try
      {
        return localClassLoaderRepository.loadClassWithout(paramClassLoader, paramString);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
    }
    throw new ClassNotFoundException(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\DefaultLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */