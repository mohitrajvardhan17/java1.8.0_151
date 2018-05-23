package sun.rmi.server;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.rmi.server.LogStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

public final class LoaderHandler
{
  static final int logLevel = LogStream.parseLevel((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.loader.logLevel")));
  static final Log loaderLog = Log.getLog("sun.rmi.loader", "loader", logLevel);
  private static String codebaseProperty = null;
  private static URL[] codebaseURLs;
  private static final Map<ClassLoader, Void> codebaseLoaders;
  private static final HashMap<LoaderKey, LoaderEntry> loaderTable = new HashMap(5);
  private static final ReferenceQueue<Loader> refQueue = new ReferenceQueue();
  private static final Map<String, Object[]> pathToURLsCache = new WeakHashMap(5);
  
  private LoaderHandler() {}
  
  private static synchronized URL[] getDefaultCodebaseURLs()
    throws MalformedURLException
  {
    if (codebaseURLs == null) {
      if (codebaseProperty != null) {
        codebaseURLs = pathToURLs(codebaseProperty);
      } else {
        codebaseURLs = new URL[0];
      }
    }
    return codebaseURLs;
  }
  
  public static Class<?> loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws MalformedURLException, ClassNotFoundException
  {
    if (loaderLog.isLoggable(Log.BRIEF)) {
      loaderLog.log(Log.BRIEF, "name = \"" + paramString2 + "\", codebase = \"" + (paramString1 != null ? paramString1 : "") + "\"" + (paramClassLoader != null ? ", defaultLoader = " + paramClassLoader : ""));
    }
    URL[] arrayOfURL;
    if (paramString1 != null) {
      arrayOfURL = pathToURLs(paramString1);
    } else {
      arrayOfURL = getDefaultCodebaseURLs();
    }
    if (paramClassLoader != null) {
      try
      {
        Class localClass = loadClassForName(paramString2, false, paramClassLoader);
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          loaderLog.log(Log.VERBOSE, "class \"" + paramString2 + "\" found via defaultLoader, defined by " + localClass.getClassLoader());
        }
        return localClass;
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
    }
    return loadClass(arrayOfURL, paramString2);
  }
  
  public static String getClassAnnotation(Class<?> paramClass)
  {
    String str1 = paramClass.getName();
    int i = str1.length();
    if ((i > 0) && (str1.charAt(0) == '['))
    {
      for (int j = 1; (i > j) && (str1.charAt(j) == '['); j++) {}
      if ((i > j) && (str1.charAt(j) != 'L')) {
        return null;
      }
    }
    ClassLoader localClassLoader = paramClass.getClassLoader();
    if ((localClassLoader == null) || (codebaseLoaders.containsKey(localClassLoader))) {
      return codebaseProperty;
    }
    String str2 = null;
    if ((localClassLoader instanceof Loader)) {
      str2 = ((Loader)localClassLoader).getClassAnnotation();
    } else if ((localClassLoader instanceof URLClassLoader)) {
      try
      {
        URL[] arrayOfURL = ((URLClassLoader)localClassLoader).getURLs();
        if (arrayOfURL != null)
        {
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null)
          {
            Permissions localPermissions = new Permissions();
            for (int k = 0; k < arrayOfURL.length; k++)
            {
              Permission localPermission = arrayOfURL[k].openConnection().getPermission();
              if ((localPermission != null) && (!localPermissions.implies(localPermission)))
              {
                localSecurityManager.checkPermission(localPermission);
                localPermissions.add(localPermission);
              }
            }
          }
          str2 = urlsToPath(arrayOfURL);
        }
      }
      catch (SecurityException|IOException localSecurityException) {}
    }
    if (str2 != null) {
      return str2;
    }
    return codebaseProperty;
  }
  
  public static ClassLoader getClassLoader(String paramString)
    throws MalformedURLException
  {
    ClassLoader localClassLoader = getRMIContextClassLoader();
    URL[] arrayOfURL;
    if (paramString != null) {
      arrayOfURL = pathToURLs(paramString);
    } else {
      arrayOfURL = getDefaultCodebaseURLs();
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("getClassLoader"));
    } else {
      return localClassLoader;
    }
    Loader localLoader = lookupLoader(arrayOfURL, localClassLoader);
    if (localLoader != null) {
      localLoader.checkPermissions();
    }
    return localLoader;
  }
  
  public static Object getSecurityContext(ClassLoader paramClassLoader)
  {
    if ((paramClassLoader instanceof Loader))
    {
      URL[] arrayOfURL = ((Loader)paramClassLoader).getURLs();
      if (arrayOfURL.length > 0) {
        return arrayOfURL[0];
      }
    }
    return null;
  }
  
  public static void registerCodebaseLoader(ClassLoader paramClassLoader)
  {
    codebaseLoaders.put(paramClassLoader, null);
  }
  
  private static Class<?> loadClass(URL[] paramArrayOfURL, String paramString)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = getRMIContextClassLoader();
    if (loaderLog.isLoggable(Log.VERBOSE)) {
      loaderLog.log(Log.VERBOSE, "(thread context class loader: " + localClassLoader + ")");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      try
      {
        Class localClass1 = Class.forName(paramString, false, localClassLoader);
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via thread context class loader (no security manager: codebase disabled), defined by " + localClass1.getClassLoader());
        }
        return localClass1;
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        if (loaderLog.isLoggable(Log.BRIEF)) {
          loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via thread context class loader (no security manager: codebase disabled)", localClassNotFoundException1);
        }
        throw new ClassNotFoundException(localClassNotFoundException1.getMessage() + " (no security manager: RMI class loader disabled)", localClassNotFoundException1.getException());
      }
    }
    Loader localLoader = lookupLoader(paramArrayOfURL, localClassLoader);
    try
    {
      if (localLoader != null) {
        localLoader.checkPermissions();
      }
    }
    catch (SecurityException localSecurityException)
    {
      try
      {
        Class localClass3 = loadClassForName(paramString, false, localClassLoader);
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via thread context class loader (access to codebase denied), defined by " + localClass3.getClassLoader());
        }
        return localClass3;
      }
      catch (ClassNotFoundException localClassNotFoundException3)
      {
        if (loaderLog.isLoggable(Log.BRIEF)) {
          loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via thread context class loader (access to codebase denied)", localSecurityException);
        }
        throw new ClassNotFoundException("access to class loader denied", localSecurityException);
      }
    }
    try
    {
      Class localClass2 = loadClassForName(paramString, false, localLoader);
      if (loaderLog.isLoggable(Log.VERBOSE)) {
        loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via codebase, defined by " + localClass2.getClassLoader());
      }
      return localClass2;
    }
    catch (ClassNotFoundException localClassNotFoundException2)
    {
      if (loaderLog.isLoggable(Log.BRIEF)) {
        loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via codebase", localClassNotFoundException2);
      }
      throw localClassNotFoundException2;
    }
  }
  
  public static Class<?> loadProxyClass(String paramString, String[] paramArrayOfString, ClassLoader paramClassLoader)
    throws MalformedURLException, ClassNotFoundException
  {
    if (loaderLog.isLoggable(Log.BRIEF)) {
      loaderLog.log(Log.BRIEF, "interfaces = " + Arrays.asList(paramArrayOfString) + ", codebase = \"" + (paramString != null ? paramString : "") + "\"" + (paramClassLoader != null ? ", defaultLoader = " + paramClassLoader : ""));
    }
    ClassLoader localClassLoader = getRMIContextClassLoader();
    if (loaderLog.isLoggable(Log.VERBOSE)) {
      loaderLog.log(Log.VERBOSE, "(thread context class loader: " + localClassLoader + ")");
    }
    URL[] arrayOfURL;
    if (paramString != null) {
      arrayOfURL = pathToURLs(paramString);
    } else {
      arrayOfURL = getDefaultCodebaseURLs();
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      try
      {
        Class localClass1 = loadProxyClass(paramArrayOfString, paramClassLoader, localClassLoader, false);
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          loaderLog.log(Log.VERBOSE, "(no security manager: codebase disabled) proxy class defined by " + localClass1.getClassLoader());
        }
        return localClass1;
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        if (loaderLog.isLoggable(Log.BRIEF)) {
          loaderLog.log(Log.BRIEF, "(no security manager: codebase disabled) proxy class resolution failed", localClassNotFoundException1);
        }
        throw new ClassNotFoundException(localClassNotFoundException1.getMessage() + " (no security manager: RMI class loader disabled)", localClassNotFoundException1.getException());
      }
    }
    Loader localLoader = lookupLoader(arrayOfURL, localClassLoader);
    try
    {
      if (localLoader != null) {
        localLoader.checkPermissions();
      }
    }
    catch (SecurityException localSecurityException)
    {
      try
      {
        Class localClass3 = loadProxyClass(paramArrayOfString, paramClassLoader, localClassLoader, false);
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          loaderLog.log(Log.VERBOSE, "(access to codebase denied) proxy class defined by " + localClass3.getClassLoader());
        }
        return localClass3;
      }
      catch (ClassNotFoundException localClassNotFoundException3)
      {
        if (loaderLog.isLoggable(Log.BRIEF)) {
          loaderLog.log(Log.BRIEF, "(access to codebase denied) proxy class resolution failed", localSecurityException);
        }
        throw new ClassNotFoundException("access to class loader denied", localSecurityException);
      }
    }
    try
    {
      Class localClass2 = loadProxyClass(paramArrayOfString, paramClassLoader, localLoader, true);
      if (loaderLog.isLoggable(Log.VERBOSE)) {
        loaderLog.log(Log.VERBOSE, "proxy class defined by " + localClass2.getClassLoader());
      }
      return localClass2;
    }
    catch (ClassNotFoundException localClassNotFoundException2)
    {
      if (loaderLog.isLoggable(Log.BRIEF)) {
        loaderLog.log(Log.BRIEF, "proxy class resolution failed", localClassNotFoundException2);
      }
      throw localClassNotFoundException2;
    }
  }
  
  private static Class<?> loadProxyClass(String[] paramArrayOfString, ClassLoader paramClassLoader1, ClassLoader paramClassLoader2, boolean paramBoolean)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = null;
    Class[] arrayOfClass = new Class[paramArrayOfString.length];
    boolean[] arrayOfBoolean = { false };
    int i;
    if (paramClassLoader1 != null)
    {
      try
      {
        localClassLoader = loadProxyInterfaces(paramArrayOfString, paramClassLoader1, arrayOfClass, arrayOfBoolean);
        if (loaderLog.isLoggable(Log.VERBOSE))
        {
          ClassLoader[] arrayOfClassLoader1 = new ClassLoader[arrayOfClass.length];
          for (i = 0; i < arrayOfClassLoader1.length; i++) {
            arrayOfClassLoader1[i] = arrayOfClass[i].getClassLoader();
          }
          loaderLog.log(Log.VERBOSE, "proxy interfaces found via defaultLoader, defined by " + Arrays.asList(arrayOfClassLoader1));
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        break label155;
      }
      if (arrayOfBoolean[0] == 0)
      {
        if (paramBoolean) {
          try
          {
            return Proxy.getProxyClass(paramClassLoader2, arrayOfClass);
          }
          catch (IllegalArgumentException localIllegalArgumentException) {}
        }
        localClassLoader = paramClassLoader1;
      }
      return loadProxyClass(localClassLoader, arrayOfClass);
    }
    label155:
    arrayOfBoolean[0] = false;
    localClassLoader = loadProxyInterfaces(paramArrayOfString, paramClassLoader2, arrayOfClass, arrayOfBoolean);
    if (loaderLog.isLoggable(Log.VERBOSE))
    {
      ClassLoader[] arrayOfClassLoader2 = new ClassLoader[arrayOfClass.length];
      for (i = 0; i < arrayOfClassLoader2.length; i++) {
        arrayOfClassLoader2[i] = arrayOfClass[i].getClassLoader();
      }
      loaderLog.log(Log.VERBOSE, "proxy interfaces found via codebase, defined by " + Arrays.asList(arrayOfClassLoader2));
    }
    if (arrayOfBoolean[0] == 0) {
      localClassLoader = paramClassLoader2;
    }
    return loadProxyClass(localClassLoader, arrayOfClass);
  }
  
  private static Class<?> loadProxyClass(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
    throws ClassNotFoundException
  {
    try
    {
      return Proxy.getProxyClass(paramClassLoader, paramArrayOfClass);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ClassNotFoundException("error creating dynamic proxy class", localIllegalArgumentException);
    }
  }
  
  private static ClassLoader loadProxyInterfaces(String[] paramArrayOfString, ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, boolean[] paramArrayOfBoolean)
    throws ClassNotFoundException
  {
    Object localObject = null;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      Class localClass = paramArrayOfClass[i] = loadClassForName(paramArrayOfString[i], false, paramClassLoader);
      if (!Modifier.isPublic(localClass.getModifiers()))
      {
        ClassLoader localClassLoader = localClass.getClassLoader();
        if (loaderLog.isLoggable(Log.VERBOSE)) {
          loaderLog.log(Log.VERBOSE, "non-public interface \"" + paramArrayOfString[i] + "\" defined by " + localClassLoader);
        }
        if (paramArrayOfBoolean[0] == 0)
        {
          localObject = localClassLoader;
          paramArrayOfBoolean[0] = true;
        }
        else if (localClassLoader != localObject)
        {
          throw new IllegalAccessError("non-public interfaces defined in different class loaders");
        }
      }
    }
    return (ClassLoader)localObject;
  }
  
  private static URL[] pathToURLs(String paramString)
    throws MalformedURLException
  {
    synchronized (pathToURLsCache)
    {
      localObject1 = (Object[])pathToURLsCache.get(paramString);
      if (localObject1 != null) {
        return (URL[])localObject1[0];
      }
    }
    ??? = new StringTokenizer(paramString);
    Object localObject1 = new URL[((StringTokenizer)???).countTokens()];
    for (int i = 0; ((StringTokenizer)???).hasMoreTokens(); i++) {
      localObject1[i] = new URL(((StringTokenizer)???).nextToken());
    }
    synchronized (pathToURLsCache)
    {
      pathToURLsCache.put(paramString, new Object[] { localObject1, new SoftReference(paramString) });
    }
    return (URL[])localObject1;
  }
  
  private static String urlsToPath(URL[] paramArrayOfURL)
  {
    if (paramArrayOfURL.length == 0) {
      return null;
    }
    if (paramArrayOfURL.length == 1) {
      return paramArrayOfURL[0].toExternalForm();
    }
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfURL[0].toExternalForm());
    for (int i = 1; i < paramArrayOfURL.length; i++)
    {
      localStringBuffer.append(' ');
      localStringBuffer.append(paramArrayOfURL[i].toExternalForm());
    }
    return localStringBuffer.toString();
  }
  
  private static ClassLoader getRMIContextClassLoader()
  {
    return Thread.currentThread().getContextClassLoader();
  }
  
  private static Loader lookupLoader(URL[] paramArrayOfURL, final ClassLoader paramClassLoader)
  {
    Loader localLoader;
    synchronized (LoaderHandler.class)
    {
      while ((localLoaderEntry = (LoaderEntry)refQueue.poll()) != null) {
        if (!removed) {
          loaderTable.remove(key);
        }
      }
      LoaderKey localLoaderKey = new LoaderKey(paramArrayOfURL, paramClassLoader);
      LoaderEntry localLoaderEntry = (LoaderEntry)loaderTable.get(localLoaderKey);
      if ((localLoaderEntry == null) || ((localLoader = (Loader)localLoaderEntry.get()) == null))
      {
        if (localLoaderEntry != null)
        {
          loaderTable.remove(localLoaderKey);
          removed = true;
        }
        AccessControlContext localAccessControlContext = getLoaderAccessControlContext(paramArrayOfURL);
        localLoader = (Loader)AccessController.doPrivileged(new PrivilegedAction()
        {
          public LoaderHandler.Loader run()
          {
            return new LoaderHandler.Loader(val$urls, paramClassLoader, null);
          }
        }, localAccessControlContext);
        localLoaderEntry = new LoaderEntry(localLoaderKey, localLoader);
        loaderTable.put(localLoaderKey, localLoaderEntry);
      }
    }
    return localLoader;
  }
  
  private static AccessControlContext getLoaderAccessControlContext(URL[] paramArrayOfURL)
  {
    PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
    {
      public PermissionCollection run()
      {
        CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
        Policy localPolicy = Policy.getPolicy();
        if (localPolicy != null) {
          return localPolicy.getPermissions(localCodeSource);
        }
        return new Permissions();
      }
    });
    localPermissionCollection.add(new RuntimePermission("createClassLoader"));
    localPermissionCollection.add(new PropertyPermission("java.*", "read"));
    addPermissionsForURLs(paramArrayOfURL, localPermissionCollection, true);
    ProtectionDomain localProtectionDomain = new ProtectionDomain(new CodeSource(paramArrayOfURL.length > 0 ? paramArrayOfURL[0] : null, (Certificate[])null), localPermissionCollection);
    return new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
  }
  
  private static void addPermissionsForURLs(URL[] paramArrayOfURL, PermissionCollection paramPermissionCollection, boolean paramBoolean)
  {
    for (int i = 0; i < paramArrayOfURL.length; i++)
    {
      URL localURL = paramArrayOfURL[i];
      try
      {
        URLConnection localURLConnection = localURL.openConnection();
        Permission localPermission = localURLConnection.getPermission();
        if (localPermission != null)
        {
          Object localObject1;
          Object localObject3;
          if ((localPermission instanceof FilePermission))
          {
            localObject1 = localPermission.getName();
            int j = ((String)localObject1).lastIndexOf(File.separatorChar);
            if (j != -1)
            {
              localObject1 = ((String)localObject1).substring(0, j + 1);
              if (((String)localObject1).endsWith(File.separator)) {
                localObject1 = (String)localObject1 + "-";
              }
              localObject3 = new FilePermission((String)localObject1, "read");
              if (!paramPermissionCollection.implies((Permission)localObject3)) {
                paramPermissionCollection.add((Permission)localObject3);
              }
              paramPermissionCollection.add(new FilePermission((String)localObject1, "read"));
            }
            else if (!paramPermissionCollection.implies(localPermission))
            {
              paramPermissionCollection.add(localPermission);
            }
          }
          else
          {
            if (!paramPermissionCollection.implies(localPermission)) {
              paramPermissionCollection.add(localPermission);
            }
            if (paramBoolean)
            {
              localObject1 = localURL;
              for (Object localObject2 = localURLConnection; (localObject2 instanceof JarURLConnection); localObject2 = ((URL)localObject1).openConnection()) {
                localObject1 = ((JarURLConnection)localObject2).getJarFileURL();
              }
              localObject2 = ((URL)localObject1).getHost();
              if ((localObject2 != null) && (localPermission.implies(new SocketPermission((String)localObject2, "resolve"))))
              {
                localObject3 = new SocketPermission((String)localObject2, "connect,accept");
                if (!paramPermissionCollection.implies((Permission)localObject3)) {
                  paramPermissionCollection.add((Permission)localObject3);
                }
              }
            }
          }
        }
      }
      catch (IOException localIOException) {}
    }
  }
  
  private static Class<?> loadClassForName(String paramString, boolean paramBoolean, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    if (paramClassLoader == null) {
      ReflectUtil.checkPackageAccess(paramString);
    }
    return Class.forName(paramString, paramBoolean, paramClassLoader);
  }
  
  static
  {
    Object localObject = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.codebase"));
    if ((localObject != null) && (((String)localObject).trim().length() > 0)) {
      codebaseProperty = (String)localObject;
    }
    codebaseURLs = null;
    codebaseLoaders = Collections.synchronizedMap(new IdentityHashMap(5));
    for (localObject = ClassLoader.getSystemClassLoader(); localObject != null; localObject = ((ClassLoader)localObject).getParent()) {
      codebaseLoaders.put(localObject, null);
    }
  }
  
  private static class Loader
    extends URLClassLoader
  {
    private ClassLoader parent;
    private String annotation;
    private Permissions permissions;
    
    private Loader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      parent = paramClassLoader;
      permissions = new Permissions();
      LoaderHandler.addPermissionsForURLs(paramArrayOfURL, permissions, false);
      annotation = LoaderHandler.urlsToPath(paramArrayOfURL);
    }
    
    public String getClassAnnotation()
    {
      return annotation;
    }
    
    private void checkPermissions()
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        Enumeration localEnumeration = permissions.elements();
        while (localEnumeration.hasMoreElements()) {
          localSecurityManager.checkPermission((Permission)localEnumeration.nextElement());
        }
      }
    }
    
    protected PermissionCollection getPermissions(CodeSource paramCodeSource)
    {
      PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
      return localPermissionCollection;
    }
    
    public String toString()
    {
      return super.toString() + "[\"" + annotation + "\"]";
    }
    
    protected Class<?> loadClass(String paramString, boolean paramBoolean)
      throws ClassNotFoundException
    {
      if (parent == null) {
        ReflectUtil.checkPackageAccess(paramString);
      }
      return super.loadClass(paramString, paramBoolean);
    }
  }
  
  private static class LoaderEntry
    extends WeakReference<LoaderHandler.Loader>
  {
    public LoaderHandler.LoaderKey key;
    public boolean removed = false;
    
    public LoaderEntry(LoaderHandler.LoaderKey paramLoaderKey, LoaderHandler.Loader paramLoader)
    {
      super(LoaderHandler.refQueue);
      key = paramLoaderKey;
    }
  }
  
  private static class LoaderKey
  {
    private URL[] urls;
    private ClassLoader parent;
    private int hashValue;
    
    public LoaderKey(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
    {
      urls = paramArrayOfURL;
      parent = paramClassLoader;
      if (paramClassLoader != null) {
        hashValue = paramClassLoader.hashCode();
      }
      for (int i = 0; i < paramArrayOfURL.length; i++) {
        hashValue ^= paramArrayOfURL[i].hashCode();
      }
    }
    
    public int hashCode()
    {
      return hashValue;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof LoaderKey))
      {
        LoaderKey localLoaderKey = (LoaderKey)paramObject;
        if (parent != parent) {
          return false;
        }
        if (urls == urls) {
          return true;
        }
        if (urls.length != urls.length) {
          return false;
        }
        for (int i = 0; i < urls.length; i++) {
          if (!urls[i].equals(urls[i])) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\LoaderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */