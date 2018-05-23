package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import sun.misc.CompoundEnumeration;
import sun.misc.Launcher;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.misc.URLClassPath;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public abstract class ClassLoader
{
  private final ClassLoader parent;
  private final ConcurrentHashMap<String, Object> parallelLockMap;
  private final Map<String, Certificate[]> package2certs;
  private static final Certificate[] nocerts = new Certificate[0];
  private final Vector<Class<?>> classes = new Vector();
  private final ProtectionDomain defaultDomain = new ProtectionDomain(new CodeSource(null, (Certificate[])null), null, this, null);
  private final Set<ProtectionDomain> domains;
  private final HashMap<String, Package> packages = new HashMap();
  private static ClassLoader scl;
  private static boolean sclSet;
  private static Vector<String> loadedLibraryNames = new Vector();
  private static Vector<NativeLibrary> systemNativeLibraries = new Vector();
  private Vector<NativeLibrary> nativeLibraries = new Vector();
  private static Stack<NativeLibrary> nativeLibraryContext = new Stack();
  private static String[] usr_paths;
  private static String[] sys_paths;
  final Object assertionLock;
  private boolean defaultAssertionStatus = false;
  private Map<String, Boolean> packageAssertionStatus = null;
  Map<String, Boolean> classAssertionStatus = null;
  
  private static native void registerNatives();
  
  void addClass(Class<?> paramClass)
  {
    classes.addElement(paramClass);
  }
  
  private static Void checkCreateClassLoader()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    return null;
  }
  
  private ClassLoader(Void paramVoid, ClassLoader paramClassLoader)
  {
    parent = paramClassLoader;
    if (ParallelLoaders.isRegistered(getClass()))
    {
      parallelLockMap = new ConcurrentHashMap();
      package2certs = new ConcurrentHashMap();
      domains = Collections.synchronizedSet(new HashSet());
      assertionLock = new Object();
    }
    else
    {
      parallelLockMap = null;
      package2certs = new Hashtable();
      domains = new HashSet();
      assertionLock = this;
    }
  }
  
  protected ClassLoader(ClassLoader paramClassLoader)
  {
    this(checkCreateClassLoader(), paramClassLoader);
  }
  
  protected ClassLoader()
  {
    this(checkCreateClassLoader(), getSystemClassLoader());
  }
  
  public Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    return loadClass(paramString, false);
  }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    synchronized (getClassLoadingLock(paramString))
    {
      Class localClass = findLoadedClass(paramString);
      if (localClass == null)
      {
        long l1 = System.nanoTime();
        try
        {
          if (parent != null) {
            localClass = parent.loadClass(paramString, false);
          } else {
            localClass = findBootstrapClassOrNull(paramString);
          }
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
        if (localClass == null)
        {
          long l2 = System.nanoTime();
          localClass = findClass(paramString);
          PerfCounter.getParentDelegationTime().addTime(l2 - l1);
          PerfCounter.getFindClassTime().addElapsedTimeFrom(l2);
          PerfCounter.getFindClasses().increment();
        }
      }
      if (paramBoolean) {
        resolveClass(localClass);
      }
      return localClass;
    }
  }
  
  protected Object getClassLoadingLock(String paramString)
  {
    Object localObject1 = this;
    if (parallelLockMap != null)
    {
      Object localObject2 = new Object();
      localObject1 = parallelLockMap.putIfAbsent(paramString, localObject2);
      if (localObject1 == null) {
        localObject1 = localObject2;
      }
    }
    return localObject1;
  }
  
  /* Error */
  private Class<?> loadClassInternal(String paramString)
    throws ClassNotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 667	java/lang/ClassLoader:parallelLockMap	Ljava/util/concurrent/ConcurrentHashMap;
    //   4: ifnonnull +20 -> 24
    //   7: aload_0
    //   8: dup
    //   9: astore_2
    //   10: monitorenter
    //   11: aload_0
    //   12: aload_1
    //   13: invokevirtual 703	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   16: aload_2
    //   17: monitorexit
    //   18: areturn
    //   19: astore_3
    //   20: aload_2
    //   21: monitorexit
    //   22: aload_3
    //   23: athrow
    //   24: aload_0
    //   25: aload_1
    //   26: invokevirtual 703	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   29: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	ClassLoader
    //   0	30	1	paramString	String
    //   9	12	2	Ljava/lang/Object;	Object
    //   19	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   11	18	19	finally
    //   19	22	19	finally
  }
  
  private void checkPackageAccess(Class<?> paramClass, ProtectionDomain paramProtectionDomain)
  {
    final SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (ReflectUtil.isNonPublicProxyClass(paramClass))
      {
        for (Class localClass : paramClass.getInterfaces()) {
          checkPackageAccess(localClass, paramProtectionDomain);
        }
        return;
      }
      ??? = paramClass.getName();
      ??? = ((String)???).lastIndexOf('.');
      if (??? != -1) {
        AccessController.doPrivileged(new PrivilegedAction()new AccessControlContext
        {
          public Void run()
          {
            localSecurityManager.checkPackageAccess(localObject.substring(0, i));
            return null;
          }
        }, new AccessControlContext(new ProtectionDomain[] { paramProtectionDomain }));
      }
    }
    domains.add(paramProtectionDomain);
  }
  
  protected Class<?> findClass(String paramString)
    throws ClassNotFoundException
  {
    throw new ClassNotFoundException(paramString);
  }
  
  @Deprecated
  protected final Class<?> defineClass(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ClassFormatError
  {
    return defineClass(null, paramArrayOfByte, paramInt1, paramInt2, null);
  }
  
  protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ClassFormatError
  {
    return defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, null);
  }
  
  private ProtectionDomain preDefineClass(String paramString, ProtectionDomain paramProtectionDomain)
  {
    if (!checkName(paramString)) {
      throw new NoClassDefFoundError("IllegalName: " + paramString);
    }
    if ((paramString != null) && (paramString.startsWith("java."))) {
      throw new SecurityException("Prohibited package name: " + paramString.substring(0, paramString.lastIndexOf('.')));
    }
    if (paramProtectionDomain == null) {
      paramProtectionDomain = defaultDomain;
    }
    if (paramString != null) {
      checkCerts(paramString, paramProtectionDomain.getCodeSource());
    }
    return paramProtectionDomain;
  }
  
  private String defineClassSourceLocation(ProtectionDomain paramProtectionDomain)
  {
    CodeSource localCodeSource = paramProtectionDomain.getCodeSource();
    String str = null;
    if ((localCodeSource != null) && (localCodeSource.getLocation() != null)) {
      str = localCodeSource.getLocation().toString();
    }
    return str;
  }
  
  private void postDefineClass(Class<?> paramClass, ProtectionDomain paramProtectionDomain)
  {
    if (paramProtectionDomain.getCodeSource() != null)
    {
      Certificate[] arrayOfCertificate = paramProtectionDomain.getCodeSource().getCertificates();
      if (arrayOfCertificate != null) {
        setSigners(paramClass, arrayOfCertificate);
      }
    }
  }
  
  protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain)
    throws ClassFormatError
  {
    paramProtectionDomain = preDefineClass(paramString, paramProtectionDomain);
    String str = defineClassSourceLocation(paramProtectionDomain);
    Class localClass = defineClass1(paramString, paramArrayOfByte, paramInt1, paramInt2, paramProtectionDomain, str);
    postDefineClass(localClass, paramProtectionDomain);
    return localClass;
  }
  
  protected final Class<?> defineClass(String paramString, ByteBuffer paramByteBuffer, ProtectionDomain paramProtectionDomain)
    throws ClassFormatError
  {
    int i = paramByteBuffer.remaining();
    if (!paramByteBuffer.isDirect())
    {
      if (paramByteBuffer.hasArray()) {
        return defineClass(paramString, paramByteBuffer.array(), paramByteBuffer.position() + paramByteBuffer.arrayOffset(), i, paramProtectionDomain);
      }
      localObject = new byte[i];
      paramByteBuffer.get((byte[])localObject);
      return defineClass(paramString, (byte[])localObject, 0, i, paramProtectionDomain);
    }
    paramProtectionDomain = preDefineClass(paramString, paramProtectionDomain);
    Object localObject = defineClassSourceLocation(paramProtectionDomain);
    Class localClass = defineClass2(paramString, paramByteBuffer, paramByteBuffer.position(), i, paramProtectionDomain, (String)localObject);
    postDefineClass(localClass, paramProtectionDomain);
    return localClass;
  }
  
  private native Class<?> defineClass0(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain);
  
  private native Class<?> defineClass1(String paramString1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, String paramString2);
  
  private native Class<?> defineClass2(String paramString1, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, String paramString2);
  
  private boolean checkName(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return true;
    }
    return (paramString.indexOf('/') == -1) && ((VM.allowArraySyntax()) || (paramString.charAt(0) != '['));
  }
  
  private void checkCerts(String paramString, CodeSource paramCodeSource)
  {
    int i = paramString.lastIndexOf('.');
    String str = i == -1 ? "" : paramString.substring(0, i);
    Certificate[] arrayOfCertificate1 = null;
    if (paramCodeSource != null) {
      arrayOfCertificate1 = paramCodeSource.getCertificates();
    }
    Certificate[] arrayOfCertificate2 = null;
    if (parallelLockMap == null) {
      synchronized (this)
      {
        arrayOfCertificate2 = (Certificate[])package2certs.get(str);
        if (arrayOfCertificate2 == null) {
          package2certs.put(str, arrayOfCertificate1 == null ? nocerts : arrayOfCertificate1);
        }
      }
    } else {
      arrayOfCertificate2 = (Certificate[])((ConcurrentHashMap)package2certs).putIfAbsent(str, arrayOfCertificate1 == null ? nocerts : arrayOfCertificate1);
    }
    if ((arrayOfCertificate2 != null) && (!compareCerts(arrayOfCertificate2, arrayOfCertificate1))) {
      throw new SecurityException("class \"" + paramString + "\"'s signer information does not match signer information of other classes in the same package");
    }
  }
  
  private boolean compareCerts(Certificate[] paramArrayOfCertificate1, Certificate[] paramArrayOfCertificate2)
  {
    if ((paramArrayOfCertificate2 == null) || (paramArrayOfCertificate2.length == 0)) {
      return paramArrayOfCertificate1.length == 0;
    }
    if (paramArrayOfCertificate2.length != paramArrayOfCertificate1.length) {
      return false;
    }
    int i;
    int k;
    for (int j = 0; j < paramArrayOfCertificate2.length; j++)
    {
      i = 0;
      for (k = 0; k < paramArrayOfCertificate1.length; k++) {
        if (paramArrayOfCertificate2[j].equals(paramArrayOfCertificate1[k]))
        {
          i = 1;
          break;
        }
      }
      if (i == 0) {
        return false;
      }
    }
    for (j = 0; j < paramArrayOfCertificate1.length; j++)
    {
      i = 0;
      for (k = 0; k < paramArrayOfCertificate2.length; k++) {
        if (paramArrayOfCertificate1[j].equals(paramArrayOfCertificate2[k]))
        {
          i = 1;
          break;
        }
      }
      if (i == 0) {
        return false;
      }
    }
    return true;
  }
  
  protected final void resolveClass(Class<?> paramClass)
  {
    resolveClass0(paramClass);
  }
  
  private native void resolveClass0(Class<?> paramClass);
  
  protected final Class<?> findSystemClass(String paramString)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = getSystemClassLoader();
    if (localClassLoader == null)
    {
      if (!checkName(paramString)) {
        throw new ClassNotFoundException(paramString);
      }
      Class localClass = findBootstrapClass(paramString);
      if (localClass == null) {
        throw new ClassNotFoundException(paramString);
      }
      return localClass;
    }
    return localClassLoader.loadClass(paramString);
  }
  
  private Class<?> findBootstrapClassOrNull(String paramString)
  {
    if (!checkName(paramString)) {
      return null;
    }
    return findBootstrapClass(paramString);
  }
  
  private native Class<?> findBootstrapClass(String paramString);
  
  protected final Class<?> findLoadedClass(String paramString)
  {
    if (!checkName(paramString)) {
      return null;
    }
    return findLoadedClass0(paramString);
  }
  
  private final native Class<?> findLoadedClass0(String paramString);
  
  protected final void setSigners(Class<?> paramClass, Object[] paramArrayOfObject)
  {
    paramClass.setSigners(paramArrayOfObject);
  }
  
  public URL getResource(String paramString)
  {
    URL localURL;
    if (parent != null) {
      localURL = parent.getResource(paramString);
    } else {
      localURL = getBootstrapResource(paramString);
    }
    if (localURL == null) {
      localURL = findResource(paramString);
    }
    return localURL;
  }
  
  public Enumeration<URL> getResources(String paramString)
    throws IOException
  {
    Enumeration[] arrayOfEnumeration = (Enumeration[])new Enumeration[2];
    if (parent != null) {
      arrayOfEnumeration[0] = parent.getResources(paramString);
    } else {
      arrayOfEnumeration[0] = getBootstrapResources(paramString);
    }
    arrayOfEnumeration[1] = findResources(paramString);
    return new CompoundEnumeration(arrayOfEnumeration);
  }
  
  protected URL findResource(String paramString)
  {
    return null;
  }
  
  protected Enumeration<URL> findResources(String paramString)
    throws IOException
  {
    return Collections.emptyEnumeration();
  }
  
  @CallerSensitive
  protected static boolean registerAsParallelCapable()
  {
    Class localClass = Reflection.getCallerClass().asSubclass(ClassLoader.class);
    return ParallelLoaders.register(localClass);
  }
  
  public static URL getSystemResource(String paramString)
  {
    ClassLoader localClassLoader = getSystemClassLoader();
    if (localClassLoader == null) {
      return getBootstrapResource(paramString);
    }
    return localClassLoader.getResource(paramString);
  }
  
  public static Enumeration<URL> getSystemResources(String paramString)
    throws IOException
  {
    ClassLoader localClassLoader = getSystemClassLoader();
    if (localClassLoader == null) {
      return getBootstrapResources(paramString);
    }
    return localClassLoader.getResources(paramString);
  }
  
  private static URL getBootstrapResource(String paramString)
  {
    URLClassPath localURLClassPath = getBootstrapClassPath();
    Resource localResource = localURLClassPath.getResource(paramString);
    return localResource != null ? localResource.getURL() : null;
  }
  
  private static Enumeration<URL> getBootstrapResources(String paramString)
    throws IOException
  {
    Enumeration localEnumeration = getBootstrapClassPath().getResources(paramString);
    new Enumeration()
    {
      public URL nextElement()
      {
        return ((Resource)val$e.nextElement()).getURL();
      }
      
      public boolean hasMoreElements()
      {
        return val$e.hasMoreElements();
      }
    };
  }
  
  static URLClassPath getBootstrapClassPath()
  {
    return Launcher.getBootstrapClassPath();
  }
  
  public InputStream getResourceAsStream(String paramString)
  {
    URL localURL = getResource(paramString);
    try
    {
      return localURL != null ? localURL.openStream() : null;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public static InputStream getSystemResourceAsStream(String paramString)
  {
    URL localURL = getSystemResource(paramString);
    try
    {
      return localURL != null ? localURL.openStream() : null;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  @CallerSensitive
  public final ClassLoader getParent()
  {
    if (parent == null) {
      return null;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      checkClassLoaderPermission(parent, Reflection.getCallerClass());
    }
    return parent;
  }
  
  @CallerSensitive
  public static ClassLoader getSystemClassLoader()
  {
    
    if (scl == null) {
      return null;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      checkClassLoaderPermission(scl, Reflection.getCallerClass());
    }
    return scl;
  }
  
  private static synchronized void initSystemClassLoader()
  {
    if (!sclSet)
    {
      if (scl != null) {
        throw new IllegalStateException("recursive invocation");
      }
      Launcher localLauncher = Launcher.getLauncher();
      if (localLauncher != null)
      {
        Throwable localThrowable = null;
        scl = localLauncher.getClassLoader();
        try
        {
          scl = (ClassLoader)AccessController.doPrivileged(new SystemClassLoaderAction(scl));
        }
        catch (PrivilegedActionException localPrivilegedActionException)
        {
          localThrowable = localPrivilegedActionException.getCause();
          if ((localThrowable instanceof InvocationTargetException)) {
            localThrowable = localThrowable.getCause();
          }
        }
        if (localThrowable != null)
        {
          if ((localThrowable instanceof Error)) {
            throw ((Error)localThrowable);
          }
          throw new Error(localThrowable);
        }
      }
      sclSet = true;
    }
  }
  
  boolean isAncestor(ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader = this;
    do
    {
      localClassLoader = parent;
      if (paramClassLoader == localClassLoader) {
        return true;
      }
    } while (localClassLoader != null);
    return false;
  }
  
  private static boolean needsClassLoaderPermissionCheck(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    if (paramClassLoader1 == paramClassLoader2) {
      return false;
    }
    if (paramClassLoader1 == null) {
      return false;
    }
    return !paramClassLoader2.isAncestor(paramClassLoader1);
  }
  
  static ClassLoader getClassLoader(Class<?> paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    return paramClass.getClassLoader0();
  }
  
  static void checkClassLoaderPermission(ClassLoader paramClassLoader, Class<?> paramClass)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      ClassLoader localClassLoader = getClassLoader(paramClass);
      if (needsClassLoaderPermissionCheck(localClassLoader, paramClassLoader)) {
        localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
      }
    }
  }
  
  protected Package definePackage(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL)
    throws IllegalArgumentException
  {
    synchronized (packages)
    {
      Package localPackage = getPackage(paramString1);
      if (localPackage != null) {
        throw new IllegalArgumentException(paramString1);
      }
      localPackage = new Package(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramURL, this);
      packages.put(paramString1, localPackage);
      return localPackage;
    }
  }
  
  protected Package getPackage(String paramString)
  {
    Object localObject1;
    synchronized (packages)
    {
      localObject1 = (Package)packages.get(paramString);
    }
    if (localObject1 == null)
    {
      if (parent != null) {
        localObject1 = parent.getPackage(paramString);
      } else {
        localObject1 = Package.getSystemPackage(paramString);
      }
      if (localObject1 != null) {
        synchronized (packages)
        {
          Package localPackage = (Package)packages.get(paramString);
          if (localPackage == null) {
            packages.put(paramString, localObject1);
          } else {
            localObject1 = localPackage;
          }
        }
      }
    }
    return (Package)localObject1;
  }
  
  protected Package[] getPackages()
  {
    HashMap localHashMap;
    synchronized (packages)
    {
      localHashMap = new HashMap(packages);
    }
    if (parent != null) {
      ??? = parent.getPackages();
    } else {
      ??? = Package.getSystemPackages();
    }
    if (??? != null) {
      for (int i = 0; i < ???.length; i++)
      {
        String str = ???[i].getName();
        if (localHashMap.get(str) == null) {
          localHashMap.put(str, ???[i]);
        }
      }
    }
    return (Package[])localHashMap.values().toArray(new Package[localHashMap.size()]);
  }
  
  protected String findLibrary(String paramString)
  {
    return null;
  }
  
  private static String[] initializePath(String paramString)
  {
    String str1 = System.getProperty(paramString, "");
    String str2 = File.pathSeparator;
    int i = str1.length();
    int j = str1.indexOf(str2);
    int m = 0;
    while (j >= 0)
    {
      m++;
      j = str1.indexOf(str2, j + 1);
    }
    String[] arrayOfString = new String[m + 1];
    m = j = 0;
    for (int k = str1.indexOf(str2); k >= 0; k = str1.indexOf(str2, j))
    {
      if (k - j > 0) {
        arrayOfString[(m++)] = str1.substring(j, k);
      } else if (k - j == 0) {
        arrayOfString[(m++)] = ".";
      }
      j = k + 1;
    }
    arrayOfString[m] = str1.substring(j, i);
    return arrayOfString;
  }
  
  static void loadLibrary(Class<?> paramClass, String paramString, boolean paramBoolean)
  {
    ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
    if (sys_paths == null)
    {
      usr_paths = initializePath("java.library.path");
      sys_paths = initializePath("sun.boot.library.path");
    }
    if (paramBoolean)
    {
      if (loadLibrary0(paramClass, new File(paramString))) {
        return;
      }
      throw new UnsatisfiedLinkError("Can't load library: " + paramString);
    }
    File localFile;
    if (localClassLoader != null)
    {
      String str = localClassLoader.findLibrary(paramString);
      if (str != null)
      {
        localFile = new File(str);
        if (!localFile.isAbsolute()) {
          throw new UnsatisfiedLinkError("ClassLoader.findLibrary failed to return an absolute path: " + str);
        }
        if (loadLibrary0(paramClass, localFile)) {
          return;
        }
        throw new UnsatisfiedLinkError("Can't load " + str);
      }
    }
    for (int i = 0; i < sys_paths.length; i++)
    {
      localFile = new File(sys_paths[i], System.mapLibraryName(paramString));
      if (loadLibrary0(paramClass, localFile)) {
        return;
      }
      localFile = ClassLoaderHelper.mapAlternativeName(localFile);
      if ((localFile != null) && (loadLibrary0(paramClass, localFile))) {
        return;
      }
    }
    if (localClassLoader != null) {
      for (i = 0; i < usr_paths.length; i++)
      {
        localFile = new File(usr_paths[i], System.mapLibraryName(paramString));
        if (loadLibrary0(paramClass, localFile)) {
          return;
        }
        localFile = ClassLoaderHelper.mapAlternativeName(localFile);
        if ((localFile != null) && (loadLibrary0(paramClass, localFile))) {
          return;
        }
      }
    }
    throw new UnsatisfiedLinkError("no " + paramString + " in java.library.path");
  }
  
  private static native String findBuiltinLib(String paramString);
  
  private static boolean loadLibrary0(Class<?> paramClass, File paramFile)
  {
    String str = findBuiltinLib(paramFile.getName());
    boolean bool = str != null;
    if (!bool)
    {
      int i = AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return val$file.exists() ? Boolean.TRUE : null;
        }
      }) != null ? 1 : 0;
      if (i == 0) {
        return false;
      }
      try
      {
        str = paramFile.getCanonicalPath();
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
    Vector localVector = localClassLoader != null ? nativeLibraries : systemNativeLibraries;
    synchronized (localVector)
    {
      int j = localVector.size();
      for (int k = 0; k < j; k++)
      {
        NativeLibrary localNativeLibrary1 = (NativeLibrary)localVector.elementAt(k);
        if (str.equals(name)) {
          return true;
        }
      }
      synchronized (loadedLibraryNames)
      {
        if (loadedLibraryNames.contains(str)) {
          throw new UnsatisfiedLinkError("Native Library " + str + " already loaded in another classloader");
        }
        int m = nativeLibraryContext.size();
        for (int n = 0; n < m; n++)
        {
          NativeLibrary localNativeLibrary3 = (NativeLibrary)nativeLibraryContext.elementAt(n);
          if (str.equals(name))
          {
            if (localClassLoader == fromClass.getClassLoader()) {
              return true;
            }
            throw new UnsatisfiedLinkError("Native Library " + str + " is being loaded in another classloader");
          }
        }
        NativeLibrary localNativeLibrary2 = new NativeLibrary(paramClass, str, bool);
        nativeLibraryContext.push(localNativeLibrary2);
        try
        {
          localNativeLibrary2.load(str, bool);
        }
        finally
        {
          nativeLibraryContext.pop();
        }
        if (loaded)
        {
          loadedLibraryNames.addElement(str);
          localVector.addElement(localNativeLibrary2);
          return true;
        }
        return false;
      }
    }
  }
  
  static long findNative(ClassLoader paramClassLoader, String paramString)
  {
    Vector localVector = paramClassLoader != null ? nativeLibraries : systemNativeLibraries;
    synchronized (localVector)
    {
      int i = localVector.size();
      for (int j = 0; j < i; j++)
      {
        NativeLibrary localNativeLibrary = (NativeLibrary)localVector.elementAt(j);
        long l = localNativeLibrary.find(paramString);
        if (l != 0L) {
          return l;
        }
      }
    }
    return 0L;
  }
  
  public void setDefaultAssertionStatus(boolean paramBoolean)
  {
    synchronized (assertionLock)
    {
      if (classAssertionStatus == null) {
        initializeJavaAssertionMaps();
      }
      defaultAssertionStatus = paramBoolean;
    }
  }
  
  public void setPackageAssertionStatus(String paramString, boolean paramBoolean)
  {
    synchronized (assertionLock)
    {
      if (packageAssertionStatus == null) {
        initializeJavaAssertionMaps();
      }
      packageAssertionStatus.put(paramString, Boolean.valueOf(paramBoolean));
    }
  }
  
  public void setClassAssertionStatus(String paramString, boolean paramBoolean)
  {
    synchronized (assertionLock)
    {
      if (classAssertionStatus == null) {
        initializeJavaAssertionMaps();
      }
      classAssertionStatus.put(paramString, Boolean.valueOf(paramBoolean));
    }
  }
  
  public void clearAssertionStatus()
  {
    synchronized (assertionLock)
    {
      classAssertionStatus = new HashMap();
      packageAssertionStatus = new HashMap();
      defaultAssertionStatus = false;
    }
  }
  
  boolean desiredAssertionStatus(String paramString)
  {
    synchronized (assertionLock)
    {
      Boolean localBoolean = (Boolean)classAssertionStatus.get(paramString);
      if (localBoolean != null) {
        return localBoolean.booleanValue();
      }
      int i = paramString.lastIndexOf(".");
      if (i < 0)
      {
        localBoolean = (Boolean)packageAssertionStatus.get(null);
        if (localBoolean != null) {
          return localBoolean.booleanValue();
        }
      }
      while (i > 0)
      {
        paramString = paramString.substring(0, i);
        localBoolean = (Boolean)packageAssertionStatus.get(paramString);
        if (localBoolean != null) {
          return localBoolean.booleanValue();
        }
        i = paramString.lastIndexOf(".", i - 1);
      }
      return defaultAssertionStatus;
    }
  }
  
  private void initializeJavaAssertionMaps()
  {
    classAssertionStatus = new HashMap();
    packageAssertionStatus = new HashMap();
    AssertionStatusDirectives localAssertionStatusDirectives = retrieveDirectives();
    for (int i = 0; i < classes.length; i++) {
      classAssertionStatus.put(classes[i], Boolean.valueOf(classEnabled[i]));
    }
    for (i = 0; i < packages.length; i++) {
      packageAssertionStatus.put(packages[i], Boolean.valueOf(packageEnabled[i]));
    }
    defaultAssertionStatus = deflt;
  }
  
  private static native AssertionStatusDirectives retrieveDirectives();
  
  static {}
  
  static class NativeLibrary
  {
    long handle;
    private int jniVersion;
    private final Class<?> fromClass;
    String name;
    boolean isBuiltin;
    boolean loaded;
    
    native void load(String paramString, boolean paramBoolean);
    
    native long find(String paramString);
    
    native void unload(String paramString, boolean paramBoolean);
    
    public NativeLibrary(Class<?> paramClass, String paramString, boolean paramBoolean)
    {
      name = paramString;
      fromClass = paramClass;
      isBuiltin = paramBoolean;
    }
    
    protected void finalize()
    {
      synchronized (ClassLoader.loadedLibraryNames)
      {
        if ((fromClass.getClassLoader() != null) && (loaded))
        {
          int i = ClassLoader.loadedLibraryNames.size();
          for (int j = 0; j < i; j++) {
            if (name.equals(ClassLoader.loadedLibraryNames.elementAt(j)))
            {
              ClassLoader.loadedLibraryNames.removeElementAt(j);
              break;
            }
          }
          ClassLoader.nativeLibraryContext.push(this);
          try
          {
            unload(name, isBuiltin);
          }
          finally
          {
            ClassLoader.nativeLibraryContext.pop();
          }
        }
      }
    }
    
    static Class<?> getFromClass()
    {
      return nativeLibraryContextpeekfromClass;
    }
  }
  
  private static class ParallelLoaders
  {
    private static final Set<Class<? extends ClassLoader>> loaderTypes = Collections.newSetFromMap(new WeakHashMap());
    
    private ParallelLoaders() {}
    
    static boolean register(Class<? extends ClassLoader> paramClass)
    {
      synchronized (loaderTypes)
      {
        if (loaderTypes.contains(paramClass.getSuperclass()))
        {
          loaderTypes.add(paramClass);
          return true;
        }
        return false;
      }
    }
    
    /* Error */
    static boolean isRegistered(Class<? extends ClassLoader> paramClass)
    {
      // Byte code:
      //   0: getstatic 44	java/lang/ClassLoader$ParallelLoaders:loaderTypes	Ljava/util/Set;
      //   3: dup
      //   4: astore_1
      //   5: monitorenter
      //   6: getstatic 44	java/lang/ClassLoader$ParallelLoaders:loaderTypes	Ljava/util/Set;
      //   9: aload_0
      //   10: invokeinterface 50 2 0
      //   15: aload_1
      //   16: monitorexit
      //   17: ireturn
      //   18: astore_2
      //   19: aload_1
      //   20: monitorexit
      //   21: aload_2
      //   22: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	23	0	paramClass	Class<? extends ClassLoader>
      //   4	16	1	Ljava/lang/Object;	Object
      //   18	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   6	17	18	finally
      //   18	21	18	finally
    }
    
    static
    {
      synchronized (loaderTypes)
      {
        loaderTypes.add(ClassLoader.class);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */