package java.net;

import java.io.Closeable;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.JavaNetAccess;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;
import sun.net.www.protocol.file.FileURLConnection;

public class URLClassLoader
  extends SecureClassLoader
  implements Closeable
{
  private final URLClassPath ucp;
  private final AccessControlContext acc;
  private WeakHashMap<Closeable, Void> closeables = new WeakHashMap();
  
  public URLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
  {
    super(paramClassLoader);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    acc = AccessController.getContext();
    ucp = new URLClassPath(paramArrayOfURL, acc);
  }
  
  URLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, AccessControlContext paramAccessControlContext)
  {
    super(paramClassLoader);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    acc = paramAccessControlContext;
    ucp = new URLClassPath(paramArrayOfURL, paramAccessControlContext);
  }
  
  public URLClassLoader(URL[] paramArrayOfURL)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    acc = AccessController.getContext();
    ucp = new URLClassPath(paramArrayOfURL, acc);
  }
  
  URLClassLoader(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    acc = paramAccessControlContext;
    ucp = new URLClassPath(paramArrayOfURL, paramAccessControlContext);
  }
  
  public URLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory)
  {
    super(paramClassLoader);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    acc = AccessController.getContext();
    ucp = new URLClassPath(paramArrayOfURL, paramURLStreamHandlerFactory, acc);
  }
  
  public InputStream getResourceAsStream(String paramString)
  {
    URL localURL = getResource(paramString);
    try
    {
      if (localURL == null) {
        return null;
      }
      URLConnection localURLConnection = localURL.openConnection();
      InputStream localInputStream = localURLConnection.getInputStream();
      if ((localURLConnection instanceof JarURLConnection))
      {
        JarURLConnection localJarURLConnection = (JarURLConnection)localURLConnection;
        JarFile localJarFile = localJarURLConnection.getJarFile();
        synchronized (closeables)
        {
          if (!closeables.containsKey(localJarFile)) {
            closeables.put(localJarFile, null);
          }
        }
      }
      else if ((localURLConnection instanceof FileURLConnection))
      {
        synchronized (closeables)
        {
          closeables.put(localInputStream, null);
        }
      }
      return localInputStream;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public void close()
    throws IOException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("closeClassLoader"));
    }
    List localList = ucp.closeLoaders();
    Object localObject2;
    synchronized (closeables)
    {
      localObject1 = closeables.keySet();
      localObject2 = ((Set)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Closeable localCloseable = (Closeable)((Iterator)localObject2).next();
        try
        {
          localCloseable.close();
        }
        catch (IOException localIOException)
        {
          localList.add(localIOException);
        }
      }
      closeables.clear();
    }
    if (localList.isEmpty()) {
      return;
    }
    ??? = (IOException)localList.remove(0);
    Object localObject1 = localList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (IOException)((Iterator)localObject1).next();
      ((IOException)???).addSuppressed((Throwable)localObject2);
    }
    throw ((Throwable)???);
  }
  
  protected void addURL(URL paramURL)
  {
    ucp.addURL(paramURL);
  }
  
  public URL[] getURLs()
  {
    return ucp.getURLs();
  }
  
  protected Class<?> findClass(final String paramString)
    throws ClassNotFoundException
  {
    Class localClass;
    try
    {
      localClass = (Class)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Class<?> run()
          throws ClassNotFoundException
        {
          String str = paramString.replace('.', '/').concat(".class");
          Resource localResource = ucp.getResource(str, false);
          if (localResource != null) {
            try
            {
              return URLClassLoader.this.defineClass(paramString, localResource);
            }
            catch (IOException localIOException)
            {
              throw new ClassNotFoundException(paramString, localIOException);
            }
          }
          return null;
        }
      }, acc);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((ClassNotFoundException)localPrivilegedActionException.getException());
    }
    if (localClass == null) {
      throw new ClassNotFoundException(paramString);
    }
    return localClass;
  }
  
  private Package getAndVerifyPackage(String paramString, Manifest paramManifest, URL paramURL)
  {
    Package localPackage = getPackage(paramString);
    if (localPackage != null) {
      if (localPackage.isSealed())
      {
        if (!localPackage.isSealed(paramURL)) {
          throw new SecurityException("sealing violation: package " + paramString + " is sealed");
        }
      }
      else if ((paramManifest != null) && (isSealed(paramString, paramManifest))) {
        throw new SecurityException("sealing violation: can't seal package " + paramString + ": already loaded");
      }
    }
    return localPackage;
  }
  
  private void definePackageInternal(String paramString, Manifest paramManifest, URL paramURL)
  {
    if (getAndVerifyPackage(paramString, paramManifest, paramURL) == null) {
      try
      {
        if (paramManifest != null) {
          definePackage(paramString, paramManifest, paramURL);
        } else {
          definePackage(paramString, null, null, null, null, null, null, null);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        if (getAndVerifyPackage(paramString, paramManifest, paramURL) == null) {
          throw new AssertionError("Cannot find package " + paramString);
        }
      }
    }
  }
  
  private Class<?> defineClass(String paramString, Resource paramResource)
    throws IOException
  {
    long l = System.nanoTime();
    int i = paramString.lastIndexOf('.');
    URL localURL = paramResource.getCodeSourceURL();
    if (i != -1)
    {
      localObject1 = paramString.substring(0, i);
      localObject2 = paramResource.getManifest();
      definePackageInternal((String)localObject1, (Manifest)localObject2, localURL);
    }
    Object localObject1 = paramResource.getByteBuffer();
    if (localObject1 != null)
    {
      localObject2 = paramResource.getCodeSigners();
      localObject3 = new CodeSource(localURL, (CodeSigner[])localObject2);
      PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(l);
      return defineClass(paramString, (ByteBuffer)localObject1, (CodeSource)localObject3);
    }
    Object localObject2 = paramResource.getBytes();
    Object localObject3 = paramResource.getCodeSigners();
    CodeSource localCodeSource = new CodeSource(localURL, (CodeSigner[])localObject3);
    PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(l);
    return defineClass(paramString, (byte[])localObject2, 0, localObject2.length, localCodeSource);
  }
  
  protected Package definePackage(String paramString, Manifest paramManifest, URL paramURL)
    throws IllegalArgumentException
  {
    String str1 = paramString.replace('.', '/').concat("/");
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    URL localURL = null;
    Attributes localAttributes = paramManifest.getAttributes(str1);
    if (localAttributes != null)
    {
      str2 = localAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
      str3 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
      str4 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
      str5 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
      str6 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      str7 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      str8 = localAttributes.getValue(Attributes.Name.SEALED);
    }
    localAttributes = paramManifest.getMainAttributes();
    if (localAttributes != null)
    {
      if (str2 == null) {
        str2 = localAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
      }
      if (str3 == null) {
        str3 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
      }
      if (str4 == null) {
        str4 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
      }
      if (str5 == null) {
        str5 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
      }
      if (str6 == null) {
        str6 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      }
      if (str7 == null) {
        str7 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      }
      if (str8 == null) {
        str8 = localAttributes.getValue(Attributes.Name.SEALED);
      }
    }
    if ("true".equalsIgnoreCase(str8)) {
      localURL = paramURL;
    }
    return definePackage(paramString, str2, str3, str4, str5, str6, str7, localURL);
  }
  
  private boolean isSealed(String paramString, Manifest paramManifest)
  {
    String str1 = paramString.replace('.', '/').concat("/");
    Attributes localAttributes = paramManifest.getAttributes(str1);
    String str2 = null;
    if (localAttributes != null) {
      str2 = localAttributes.getValue(Attributes.Name.SEALED);
    }
    if ((str2 == null) && ((localAttributes = paramManifest.getMainAttributes()) != null)) {
      str2 = localAttributes.getValue(Attributes.Name.SEALED);
    }
    return "true".equalsIgnoreCase(str2);
  }
  
  public URL findResource(final String paramString)
  {
    URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
    {
      public URL run()
      {
        return ucp.findResource(paramString, true);
      }
    }, acc);
    return localURL != null ? ucp.checkURL(localURL) : null;
  }
  
  public Enumeration<URL> findResources(String paramString)
    throws IOException
  {
    final Enumeration localEnumeration = ucp.findResources(paramString, true);
    new Enumeration()
    {
      private URL url = null;
      
      private boolean next()
      {
        if (url != null) {
          return true;
        }
        do
        {
          URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
          {
            public URL run()
            {
              if (!val$e.hasMoreElements()) {
                return null;
              }
              return (URL)val$e.nextElement();
            }
          }, acc);
          if (localURL == null) {
            break;
          }
          url = ucp.checkURL(localURL);
        } while (url == null);
        return url != null;
      }
      
      public URL nextElement()
      {
        if (!next()) {
          throw new NoSuchElementException();
        }
        URL localURL = url;
        url = null;
        return localURL;
      }
      
      public boolean hasMoreElements()
      {
        return next();
      }
    };
  }
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    URL localURL = paramCodeSource.getLocation();
    URLConnection localURLConnection;
    Object localObject1;
    try
    {
      localURLConnection = localURL.openConnection();
      localObject1 = localURLConnection.getPermission();
    }
    catch (IOException localIOException)
    {
      localObject1 = null;
      localURLConnection = null;
    }
    final Object localObject2;
    final Object localObject3;
    if ((localObject1 instanceof FilePermission))
    {
      localObject2 = ((Permission)localObject1).getName();
      if (((String)localObject2).endsWith(File.separator))
      {
        localObject2 = (String)localObject2 + "-";
        localObject1 = new FilePermission((String)localObject2, "read");
      }
    }
    else if ((localObject1 == null) && (localURL.getProtocol().equals("file")))
    {
      localObject2 = localURL.getFile().replace('/', File.separatorChar);
      localObject2 = ParseUtil.decode((String)localObject2);
      if (((String)localObject2).endsWith(File.separator)) {
        localObject2 = (String)localObject2 + "-";
      }
      localObject1 = new FilePermission((String)localObject2, "read");
    }
    else
    {
      localObject2 = localURL;
      if ((localURLConnection instanceof JarURLConnection)) {
        localObject2 = ((JarURLConnection)localURLConnection).getJarFileURL();
      }
      localObject3 = ((URL)localObject2).getHost();
      if ((localObject3 != null) && (((String)localObject3).length() > 0)) {
        localObject1 = new SocketPermission((String)localObject3, "connect,accept");
      }
    }
    if (localObject1 != null)
    {
      localObject2 = System.getSecurityManager();
      if (localObject2 != null)
      {
        localObject3 = localObject1;
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
            throws SecurityException
          {
            localObject2.checkPermission(localObject3);
            return null;
          }
        }, acc);
      }
      localPermissionCollection.add((Permission)localObject1);
    }
    return localPermissionCollection;
  }
  
  public static URLClassLoader newInstance(URL[] paramArrayOfURL, final ClassLoader paramClassLoader)
  {
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    URLClassLoader localURLClassLoader = (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public URLClassLoader run()
      {
        return new FactoryURLClassLoader(val$urls, paramClassLoader, localAccessControlContext);
      }
    });
    return localURLClassLoader;
  }
  
  public static URLClassLoader newInstance(URL[] paramArrayOfURL)
  {
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    URLClassLoader localURLClassLoader = (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public URLClassLoader run()
      {
        return new FactoryURLClassLoader(val$urls, localAccessControlContext);
      }
    });
    return localURLClassLoader;
  }
  
  static
  {
    SharedSecrets.setJavaNetAccess(new JavaNetAccess()
    {
      public URLClassPath getURLClassPath(URLClassLoader paramAnonymousURLClassLoader)
      {
        return ucp;
      }
      
      public String getOriginalHostName(InetAddress paramAnonymousInetAddress)
      {
        return holder.getOriginalHostName();
      }
    });
    ClassLoader.registerAsParallelCapable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URLClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */