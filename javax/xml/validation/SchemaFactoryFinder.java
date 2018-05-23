package javax.xml.validation;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class SchemaFactoryFinder
{
  private static boolean debug;
  private static final SecuritySupport ss;
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
  private static final Properties cacheProps;
  private static volatile boolean firstTime;
  private final ClassLoader classLoader;
  private static final Class<SchemaFactory> SERVICE_CLASS = SchemaFactory.class;
  
  private static void debugPrintln(String paramString)
  {
    if (debug) {
      System.err.println("JAXP: " + paramString);
    }
  }
  
  public SchemaFactoryFinder(ClassLoader paramClassLoader)
  {
    classLoader = paramClassLoader;
    if (debug) {
      debugDisplayClassLoader();
    }
  }
  
  private void debugDisplayClassLoader()
  {
    try
    {
      if (classLoader == ss.getContextClassLoader())
      {
        debugPrintln("using thread context class loader (" + classLoader + ") for search");
        return;
      }
    }
    catch (Throwable localThrowable) {}
    if (classLoader == ClassLoader.getSystemClassLoader())
    {
      debugPrintln("using system class loader (" + classLoader + ") for search");
      return;
    }
    debugPrintln("using class loader (" + classLoader + ") for search");
  }
  
  public SchemaFactory newFactory(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    SchemaFactory localSchemaFactory = _newFactory(paramString);
    if (localSchemaFactory != null) {
      debugPrintln("factory '" + localSchemaFactory.getClass().getName() + "' was found for " + paramString);
    } else {
      debugPrintln("unable to find a factory for " + paramString);
    }
    return localSchemaFactory;
  }
  
  private SchemaFactory _newFactory(String paramString)
  {
    String str1 = SERVICE_CLASS.getName() + ":" + paramString;
    SchemaFactory localSchemaFactory1;
    try
    {
      debugPrintln("Looking up system property '" + str1 + "'");
      String str2 = ss.getSystemProperty(str1);
      if (str2 != null)
      {
        debugPrintln("The value is '" + str2 + "'");
        localSchemaFactory1 = createInstance(str2, true);
        if (localSchemaFactory1 != null) {
          return localSchemaFactory1;
        }
      }
      else
      {
        debugPrintln("The property is undefined.");
      }
    }
    catch (Throwable localThrowable)
    {
      if (debug)
      {
        debugPrintln("failed to look up system property '" + str1 + "'");
        localThrowable.printStackTrace();
      }
    }
    String str3 = ss.getSystemProperty("java.home");
    String str4 = str3 + File.separator + "lib" + File.separator + "jaxp.properties";
    try
    {
      if (firstTime) {
        synchronized (cacheProps)
        {
          if (firstTime)
          {
            File localFile = new File(str4);
            firstTime = false;
            if (ss.doesFileExist(localFile))
            {
              debugPrintln("Read properties file " + localFile);
              cacheProps.load(ss.getFileInputStream(localFile));
            }
          }
        }
      }
      ??? = cacheProps.getProperty(str1);
      debugPrintln("found " + (String)??? + " in $java.home/jaxp.properties");
      if (??? != null)
      {
        localSchemaFactory1 = createInstance((String)???, true);
        if (localSchemaFactory1 != null) {
          return localSchemaFactory1;
        }
      }
    }
    catch (Exception localException)
    {
      if (debug) {
        localException.printStackTrace();
      }
    }
    SchemaFactory localSchemaFactory2 = findServiceProvider(paramString);
    if (localSchemaFactory2 != null) {
      return localSchemaFactory2;
    }
    if (paramString.equals("http://www.w3.org/2001/XMLSchema"))
    {
      debugPrintln("attempting to use the platform default XML Schema validator");
      return createInstance("com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory", true);
    }
    debugPrintln("all things were tried, but none was found. bailing out.");
    return null;
  }
  
  private Class<?> createClass(String paramString)
  {
    int i = 0;
    if ((System.getSecurityManager() != null) && (paramString != null) && (paramString.startsWith("com.sun.org.apache.xerces.internal"))) {
      i = 1;
    }
    Class localClass;
    try
    {
      if ((classLoader != null) && (i == 0)) {
        localClass = Class.forName(paramString, false, classLoader);
      } else {
        localClass = Class.forName(paramString);
      }
    }
    catch (Throwable localThrowable)
    {
      if (debug) {
        localThrowable.printStackTrace();
      }
      return null;
    }
    return localClass;
  }
  
  SchemaFactory createInstance(String paramString)
  {
    return createInstance(paramString, false);
  }
  
  SchemaFactory createInstance(String paramString, boolean paramBoolean)
  {
    SchemaFactory localSchemaFactory = null;
    debugPrintln("createInstance(" + paramString + ")");
    Class localClass = createClass(paramString);
    if (localClass == null)
    {
      debugPrintln("failed to getClass(" + paramString + ")");
      return null;
    }
    debugPrintln("loaded " + paramString + " from " + which(localClass));
    try
    {
      if (!SchemaFactory.class.isAssignableFrom(localClass)) {
        throw new ClassCastException(localClass.getName() + " cannot be cast to " + SchemaFactory.class);
      }
      if (!paramBoolean) {
        localSchemaFactory = newInstanceNoServiceLoader(localClass);
      }
      if (localSchemaFactory == null) {
        localSchemaFactory = (SchemaFactory)localClass.newInstance();
      }
    }
    catch (ClassCastException localClassCastException)
    {
      debugPrintln("could not instantiate " + localClass.getName());
      if (debug) {
        localClassCastException.printStackTrace();
      }
      return null;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      debugPrintln("could not instantiate " + localClass.getName());
      if (debug) {
        localIllegalAccessException.printStackTrace();
      }
      return null;
    }
    catch (InstantiationException localInstantiationException)
    {
      debugPrintln("could not instantiate " + localClass.getName());
      if (debug) {
        localInstantiationException.printStackTrace();
      }
      return null;
    }
    return localSchemaFactory;
  }
  
  private static SchemaFactory newInstanceNoServiceLoader(Class<?> paramClass)
  {
    if (System.getSecurityManager() == null) {
      return null;
    }
    try
    {
      Method localMethod = paramClass.getDeclaredMethod("newXMLSchemaFactoryNoServiceLoader", new Class[0]);
      int i = localMethod.getModifiers();
      if ((!Modifier.isStatic(i)) || (!Modifier.isPublic(i))) {
        return null;
      }
      Class localClass = localMethod.getReturnType();
      if (SERVICE_CLASS.isAssignableFrom(localClass)) {
        return (SchemaFactory)SERVICE_CLASS.cast(localMethod.invoke(null, (Object[])null));
      }
      throw new ClassCastException(localClass + " cannot be cast to " + SERVICE_CLASS);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new SchemaFactoryConfigurationError(localClassCastException.getMessage(), localClassCastException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    catch (Exception localException) {}
    return null;
  }
  
  private boolean isSchemaLanguageSupportedBy(final SchemaFactory paramSchemaFactory, final String paramString, AccessControlContext paramAccessControlContext)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(paramSchemaFactory.isSchemaLanguageSupported(paramString));
      }
    }, paramAccessControlContext)).booleanValue();
  }
  
  private SchemaFactory findServiceProvider(final String paramString)
  {
    assert (paramString != null);
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    try
    {
      (SchemaFactory)AccessController.doPrivileged(new PrivilegedAction()
      {
        public SchemaFactory run()
        {
          ServiceLoader localServiceLoader = ServiceLoader.load(SchemaFactoryFinder.SERVICE_CLASS);
          Iterator localIterator = localServiceLoader.iterator();
          while (localIterator.hasNext())
          {
            SchemaFactory localSchemaFactory = (SchemaFactory)localIterator.next();
            if (SchemaFactoryFinder.this.isSchemaLanguageSupportedBy(localSchemaFactory, paramString, localAccessControlContext)) {
              return localSchemaFactory;
            }
          }
          return null;
        }
      });
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      throw new SchemaFactoryConfigurationError("Provider for " + SERVICE_CLASS + " cannot be created", localServiceConfigurationError);
    }
  }
  
  private static String which(Class<?> paramClass)
  {
    return which(paramClass.getName(), paramClass.getClassLoader());
  }
  
  private static String which(String paramString, ClassLoader paramClassLoader)
  {
    String str = paramString.replace('.', '/') + ".class";
    if (paramClassLoader == null) {
      paramClassLoader = ClassLoader.getSystemClassLoader();
    }
    URL localURL = ss.getResourceAsURL(paramClassLoader, str);
    if (localURL != null) {
      return localURL.toString();
    }
    return null;
  }
  
  static
  {
    debug = false;
    ss = new SecuritySupport();
    cacheProps = new Properties();
    firstTime = true;
    try
    {
      debug = ss.getSystemProperty("jaxp.debug") != null;
    }
    catch (Exception localException)
    {
      debug = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\validation\SchemaFactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */