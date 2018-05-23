package javax.xml.bind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class ContextFinder
{
  private static final Logger logger = Logger.getLogger("javax.xml.bind");
  private static final String PLATFORM_DEFAULT_FACTORY_CLASS = "com.sun.xml.internal.bind.v2.ContextFactory";
  
  ContextFinder() {}
  
  private static void handleInvocationTargetException(InvocationTargetException paramInvocationTargetException)
    throws JAXBException
  {
    Throwable localThrowable = paramInvocationTargetException.getTargetException();
    if (localThrowable != null)
    {
      if ((localThrowable instanceof JAXBException)) {
        throw ((JAXBException)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
    }
  }
  
  private static JAXBException handleClassCastException(Class paramClass1, Class paramClass2)
  {
    URL localURL = which(paramClass2);
    return new JAXBException(Messages.format("JAXBContext.IllegalCast", getClassClassLoader(paramClass1).getResource("javax/xml/bind/JAXBContext.class"), localURL));
  }
  
  static JAXBContext newInstance(String paramString1, String paramString2, ClassLoader paramClassLoader, Map paramMap)
    throws JAXBException
  {
    try
    {
      Class localClass = safeLoadClass(paramString2, paramClassLoader);
      return newInstance(paramString1, localClass, paramClassLoader, paramMap);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new JAXBException(Messages.format("ContextFinder.ProviderNotFound", paramString2), localClassNotFoundException);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", paramString2, localException), localException);
    }
  }
  
  static JAXBContext newInstance(String paramString, Class paramClass, ClassLoader paramClassLoader, Map paramMap)
    throws JAXBException
  {
    try
    {
      Object localObject1 = null;
      try
      {
        Method localMethod = paramClass.getMethod("createContext", new Class[] { String.class, ClassLoader.class, Map.class });
        localObject1 = localMethod.invoke(null, new Object[] { paramString, paramClassLoader, paramMap });
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
      if (localObject1 == null)
      {
        localObject2 = paramClass.getMethod("createContext", new Class[] { String.class, ClassLoader.class });
        localObject1 = ((Method)localObject2).invoke(null, new Object[] { paramString, paramClassLoader });
      }
      if (!(localObject1 instanceof JAXBContext)) {
        throw handleClassCastException(localObject1.getClass(), JAXBContext.class);
      }
      return (JAXBContext)localObject1;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      handleInvocationTargetException(localInvocationTargetException);
      Object localObject2 = localInvocationTargetException;
      if (localInvocationTargetException.getTargetException() != null) {
        localObject2 = localInvocationTargetException.getTargetException();
      }
      throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", paramClass, localObject2), (Throwable)localObject2);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", paramClass, localException), localException);
    }
  }
  
  static JAXBContext newInstance(Class[] paramArrayOfClass, Map paramMap, String paramString)
    throws JAXBException
  {
    ClassLoader localClassLoader = getContextClassLoader();
    Class localClass;
    try
    {
      localClass = safeLoadClass(paramString, localClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new JAXBException(localClassNotFoundException);
    }
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "loaded {0} from {1}", new Object[] { paramString, which(localClass) });
    }
    return newInstance(paramArrayOfClass, paramMap, localClass);
  }
  
  static JAXBContext newInstance(Class[] paramArrayOfClass, Map paramMap, Class paramClass)
    throws JAXBException
  {
    Method localMethod;
    try
    {
      localMethod = paramClass.getMethod("createContext", new Class[] { Class[].class, Map.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new JAXBException(localNoSuchMethodException);
    }
    try
    {
      Object localObject1 = localMethod.invoke(null, new Object[] { paramArrayOfClass, paramMap });
      if (!(localObject1 instanceof JAXBContext)) {
        throw handleClassCastException(localObject1.getClass(), JAXBContext.class);
      }
      return (JAXBContext)localObject1;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new JAXBException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      handleInvocationTargetException(localInvocationTargetException);
      Object localObject2 = localInvocationTargetException;
      if (localInvocationTargetException.getTargetException() != null) {
        localObject2 = localInvocationTargetException.getTargetException();
      }
      throw new JAXBException((Throwable)localObject2);
    }
  }
  
  static JAXBContext find(String paramString1, String paramString2, ClassLoader paramClassLoader, Map paramMap)
    throws JAXBException
  {
    String str1 = JAXBContext.class.getName();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, ":");
    if (!localStringTokenizer.hasMoreTokens()) {
      throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath"));
    }
    logger.fine("Searching jaxb.properties");
    while (localStringTokenizer.hasMoreTokens())
    {
      localObject1 = localStringTokenizer.nextToken(":").replace('.', '/');
      StringBuilder localStringBuilder1 = new StringBuilder().append((String)localObject1).append("/jaxb.properties");
      localObject2 = loadJAXBProperties(paramClassLoader, localStringBuilder1.toString());
      if (localObject2 != null)
      {
        if (((Properties)localObject2).containsKey(paramString1))
        {
          str2 = ((Properties)localObject2).getProperty(paramString1);
          return newInstance(paramString2, str2, paramClassLoader, paramMap);
        }
        throw new JAXBException(Messages.format("ContextFinder.MissingProperty", localObject1, paramString1));
      }
    }
    logger.fine("Searching the system property");
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("javax.xml.bind.context.factory"));
    if (str2 != null) {
      return newInstance(paramString2, str2, paramClassLoader, paramMap);
    }
    str2 = (String)AccessController.doPrivileged(new GetPropertyAction(str1));
    if (str2 != null) {
      return newInstance(paramString2, str2, paramClassLoader, paramMap);
    }
    Object localObject1 = lookupJaxbContextUsingOsgiServiceLoader();
    if (localObject1 != null)
    {
      logger.fine("OSGi environment detected");
      return newInstance(paramString2, (Class)localObject1, paramClassLoader, paramMap);
    }
    logger.fine("Searching META-INF/services");
    Object localObject2 = null;
    try
    {
      StringBuilder localStringBuilder2 = new StringBuilder().append("META-INF/services/").append(str1);
      InputStream localInputStream = paramClassLoader.getResourceAsStream(localStringBuilder2.toString());
      if (localInputStream != null)
      {
        localObject2 = new BufferedReader(new InputStreamReader(localInputStream, "UTF-8"));
        str2 = ((BufferedReader)localObject2).readLine();
        if (str2 != null) {
          str2 = str2.trim();
        }
        ((BufferedReader)localObject2).close();
        JAXBContext localJAXBContext = newInstance(paramString2, str2, paramClassLoader, paramMap);
        return localJAXBContext;
      }
      logger.log(Level.FINE, "Unable to load:{0}", localStringBuilder2.toString());
      try
      {
        if (localObject2 != null) {
          ((BufferedReader)localObject2).close();
        }
      }
      catch (IOException localIOException1)
      {
        Logger.getLogger(ContextFinder.class.getName()).log(Level.SEVERE, null, localIOException1);
      }
      logger.fine("Trying to create the platform default provider");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new JAXBException(localUnsupportedEncodingException);
    }
    catch (IOException localIOException2)
    {
      throw new JAXBException(localIOException2);
    }
    finally
    {
      try
      {
        if (localObject2 != null) {
          ((BufferedReader)localObject2).close();
        }
      }
      catch (IOException localIOException4)
      {
        Logger.getLogger(ContextFinder.class.getName()).log(Level.SEVERE, null, localIOException4);
      }
    }
    return newInstance(paramString2, "com.sun.xml.internal.bind.v2.ContextFactory", paramClassLoader, paramMap);
  }
  
  static JAXBContext find(Class[] paramArrayOfClass, Map paramMap)
    throws JAXBException
  {
    String str1 = JAXBContext.class.getName();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    for (localObject2 : paramArrayOfClass)
    {
      localObject3 = getClassClassLoader((Class)localObject2);
      localObject4 = ((Class)localObject2).getPackage();
      if (localObject4 != null)
      {
        String str4 = ((Package)localObject4).getName().replace('.', '/');
        String str5 = str4 + "/jaxb.properties";
        logger.log(Level.FINE, "Trying to locate {0}", str5);
        Properties localProperties = loadJAXBProperties((ClassLoader)localObject3, str5);
        if (localProperties == null)
        {
          logger.fine("  not found");
        }
        else
        {
          logger.fine("  found");
          if (localProperties.containsKey("javax.xml.bind.context.factory"))
          {
            str2 = localProperties.getProperty("javax.xml.bind.context.factory").trim();
            return newInstance(paramArrayOfClass, paramMap, str2);
          }
          throw new JAXBException(Messages.format("ContextFinder.MissingProperty", str4, "javax.xml.bind.context.factory"));
        }
      }
    }
    logger.log(Level.FINE, "Checking system property {0}", "javax.xml.bind.context.factory");
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("javax.xml.bind.context.factory"));
    if (str2 != null)
    {
      logger.log(Level.FINE, "  found {0}", str2);
      return newInstance(paramArrayOfClass, paramMap, str2);
    }
    logger.fine("  not found");
    logger.log(Level.FINE, "Checking system property {0}", str1);
    str2 = (String)AccessController.doPrivileged(new GetPropertyAction(str1));
    if (str2 != null)
    {
      logger.log(Level.FINE, "  found {0}", str2);
      return newInstance(paramArrayOfClass, paramMap, str2);
    }
    logger.fine("  not found");
    ??? = lookupJaxbContextUsingOsgiServiceLoader();
    if (??? != null)
    {
      logger.fine("OSGi environment detected");
      return newInstance(paramArrayOfClass, paramMap, (Class)???);
    }
    logger.fine("Checking META-INF/services");
    BufferedReader localBufferedReader = null;
    try
    {
      String str3 = "META-INF/services/" + str1;
      localObject2 = getContextClassLoader();
      if (localObject2 == null) {
        localObject3 = ClassLoader.getSystemResource(str3);
      } else {
        localObject3 = ((ClassLoader)localObject2).getResource(str3);
      }
      if (localObject3 != null)
      {
        logger.log(Level.FINE, "Reading {0}", localObject3);
        localBufferedReader = new BufferedReader(new InputStreamReader(((URL)localObject3).openStream(), "UTF-8"));
        str2 = localBufferedReader.readLine();
        if (str2 != null) {
          str2 = str2.trim();
        }
        localObject4 = newInstance(paramArrayOfClass, paramMap, str2);
        return (JAXBContext)localObject4;
      }
      logger.log(Level.FINE, "Unable to find: {0}", str3);
      if (localBufferedReader != null) {
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException1)
        {
          logger.log(Level.FINE, "Unable to close stream", localIOException1);
        }
      }
      logger.fine("Trying to create the platform default provider");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new JAXBException(localUnsupportedEncodingException);
    }
    catch (IOException localIOException2)
    {
      throw new JAXBException(localIOException2);
    }
    finally
    {
      if (localBufferedReader != null) {
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException4)
        {
          logger.log(Level.FINE, "Unable to close stream", localIOException4);
        }
      }
    }
    return newInstance(paramArrayOfClass, paramMap, "com.sun.xml.internal.bind.v2.ContextFactory");
  }
  
  private static Class lookupJaxbContextUsingOsgiServiceLoader()
  {
    try
    {
      Class localClass = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
      Method localMethod = localClass.getMethod("lookupProviderClasses", new Class[] { Class.class });
      Iterator localIterator = ((Iterable)localMethod.invoke(null, new Object[] { JAXBContext.class })).iterator();
      return localIterator.hasNext() ? (Class)localIterator.next() : null;
    }
    catch (Exception localException)
    {
      logger.log(Level.FINE, "Unable to find from OSGi: javax.xml.bind.JAXBContext");
    }
    return null;
  }
  
  private static Properties loadJAXBProperties(ClassLoader paramClassLoader, String paramString)
    throws JAXBException
  {
    Properties localProperties = null;
    try
    {
      URL localURL;
      if (paramClassLoader == null) {
        localURL = ClassLoader.getSystemResource(paramString);
      } else {
        localURL = paramClassLoader.getResource(paramString);
      }
      if (localURL != null)
      {
        logger.log(Level.FINE, "loading props from {0}", localURL);
        localProperties = new Properties();
        InputStream localInputStream = localURL.openStream();
        localProperties.load(localInputStream);
        localInputStream.close();
      }
    }
    catch (IOException localIOException)
    {
      logger.log(Level.FINE, "Unable to load " + paramString, localIOException);
      throw new JAXBException(localIOException.toString(), localIOException);
    }
    return localProperties;
  }
  
  static URL which(Class paramClass, ClassLoader paramClassLoader)
  {
    String str = paramClass.getName().replace('.', '/') + ".class";
    if (paramClassLoader == null) {
      paramClassLoader = getSystemClassLoader();
    }
    return paramClassLoader.getResource(str);
  }
  
  static URL which(Class paramClass)
  {
    return which(paramClass, getClassClassLoader(paramClass));
  }
  
  private static Class safeLoadClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    logger.log(Level.FINE, "Trying to load {0}", paramString);
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        int i = paramString.lastIndexOf('.');
        if (i != -1) {
          localSecurityManager.checkPackageAccess(paramString.substring(0, i));
        }
      }
      if (paramClassLoader == null) {
        return Class.forName(paramString);
      }
      return paramClassLoader.loadClass(paramString);
    }
    catch (SecurityException localSecurityException)
    {
      if ("com.sun.xml.internal.bind.v2.ContextFactory".equals(paramString)) {
        return Class.forName(paramString);
      }
      throw localSecurityException;
    }
  }
  
  private static ClassLoader getContextClassLoader()
  {
    if (System.getSecurityManager() == null) {
      return Thread.currentThread().getContextClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
  }
  
  private static ClassLoader getClassClassLoader(Class paramClass)
  {
    if (System.getSecurityManager() == null) {
      return paramClass.getClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return val$c.getClassLoader();
      }
    });
  }
  
  private static ClassLoader getSystemClassLoader()
  {
    if (System.getSecurityManager() == null) {
      return ClassLoader.getSystemClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return ClassLoader.getSystemClassLoader();
      }
    });
  }
  
  static
  {
    try
    {
      if (AccessController.doPrivileged(new GetPropertyAction("jaxb.debug")) != null)
      {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        ConsoleHandler localConsoleHandler = new ConsoleHandler();
        localConsoleHandler.setLevel(Level.ALL);
        logger.addHandler(localConsoleHandler);
      }
    }
    catch (Throwable localThrowable) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\ContextFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */