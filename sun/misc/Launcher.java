package sun.misc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.www.ParseUtil;

public class Launcher
{
  private static URLStreamHandlerFactory factory = new Factory(null);
  private static Launcher launcher = new Launcher();
  private static String bootClassPath = System.getProperty("sun.boot.class.path");
  private ClassLoader loader;
  private static URLStreamHandler fileHandler;
  
  public static Launcher getLauncher()
  {
    return launcher;
  }
  
  public Launcher()
  {
    ExtClassLoader localExtClassLoader;
    try
    {
      localExtClassLoader = ExtClassLoader.getExtClassLoader();
    }
    catch (IOException localIOException1)
    {
      throw new InternalError("Could not create extension class loader", localIOException1);
    }
    try
    {
      loader = AppClassLoader.getAppClassLoader(localExtClassLoader);
    }
    catch (IOException localIOException2)
    {
      throw new InternalError("Could not create application class loader", localIOException2);
    }
    Thread.currentThread().setContextClassLoader(loader);
    String str = System.getProperty("java.security.manager");
    if (str != null)
    {
      SecurityManager localSecurityManager = null;
      if (("".equals(str)) || ("default".equals(str))) {
        localSecurityManager = new SecurityManager();
      } else {
        try
        {
          localSecurityManager = (SecurityManager)loader.loadClass(str).newInstance();
        }
        catch (IllegalAccessException localIllegalAccessException) {}catch (InstantiationException localInstantiationException) {}catch (ClassNotFoundException localClassNotFoundException) {}catch (ClassCastException localClassCastException) {}
      }
      if (localSecurityManager != null) {
        System.setSecurityManager(localSecurityManager);
      } else {
        throw new InternalError("Could not create SecurityManager: " + str);
      }
    }
  }
  
  public ClassLoader getClassLoader()
  {
    return loader;
  }
  
  public static URLClassPath getBootstrapClassPath()
  {
    return BootClassPathHolder.bcp;
  }
  
  private static URL[] pathToURLs(File[] paramArrayOfFile)
  {
    URL[] arrayOfURL = new URL[paramArrayOfFile.length];
    for (int i = 0; i < paramArrayOfFile.length; i++) {
      arrayOfURL[i] = getFileURL(paramArrayOfFile[i]);
    }
    return arrayOfURL;
  }
  
  private static File[] getClassPath(String paramString)
  {
    Object localObject;
    if (paramString != null)
    {
      int i = 0;
      int j = 1;
      int k = 0;
      for (int m = 0; (k = paramString.indexOf(File.pathSeparator, m)) != -1; m = k + 1) {
        j++;
      }
      localObject = new File[j];
      for (m = k = 0; (k = paramString.indexOf(File.pathSeparator, m)) != -1; m = k + 1) {
        if (k - m > 0) {
          localObject[(i++)] = new File(paramString.substring(m, k));
        } else {
          localObject[(i++)] = new File(".");
        }
      }
      if (m < paramString.length()) {
        localObject[(i++)] = new File(paramString.substring(m));
      } else {
        localObject[(i++)] = new File(".");
      }
      if (i != j)
      {
        File[] arrayOfFile = new File[i];
        System.arraycopy(localObject, 0, arrayOfFile, 0, i);
        localObject = arrayOfFile;
      }
    }
    else
    {
      localObject = new File[0];
    }
    return (File[])localObject;
  }
  
  static URL getFileURL(File paramFile)
  {
    try
    {
      paramFile = paramFile.getCanonicalFile();
    }
    catch (IOException localIOException) {}
    try
    {
      return ParseUtil.fileToEncodedURL(paramFile);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new InternalError(localMalformedURLException);
    }
  }
  
  static class AppClassLoader
    extends URLClassLoader
  {
    final URLClassPath ucp = SharedSecrets.getJavaNetAccess().getURLClassPath(this);
    
    public static ClassLoader getAppClassLoader(final ClassLoader paramClassLoader)
      throws IOException
    {
      String str = System.getProperty("java.class.path");
      final File[] arrayOfFile = str == null ? new File[0] : Launcher.getClassPath(str);
      (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Launcher.AppClassLoader run()
        {
          URL[] arrayOfURL = val$s == null ? new URL[0] : Launcher.pathToURLs(arrayOfFile);
          return new Launcher.AppClassLoader(arrayOfURL, paramClassLoader);
        }
      });
    }
    
    AppClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
    {
      super(paramClassLoader, Launcher.factory);
      ucp.initLookupCache(this);
    }
    
    public Class<?> loadClass(String paramString, boolean paramBoolean)
      throws ClassNotFoundException
    {
      int i = paramString.lastIndexOf('.');
      Object localObject;
      if (i != -1)
      {
        localObject = System.getSecurityManager();
        if (localObject != null) {
          ((SecurityManager)localObject).checkPackageAccess(paramString.substring(0, i));
        }
      }
      if (ucp.knownToNotExist(paramString))
      {
        localObject = findLoadedClass(paramString);
        if (localObject != null)
        {
          if (paramBoolean) {
            resolveClass((Class)localObject);
          }
          return (Class<?>)localObject;
        }
        throw new ClassNotFoundException(paramString);
      }
      return super.loadClass(paramString, paramBoolean);
    }
    
    protected PermissionCollection getPermissions(CodeSource paramCodeSource)
    {
      PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
      localPermissionCollection.add(new RuntimePermission("exitVM"));
      return localPermissionCollection;
    }
    
    private void appendToClassPathForInstrumentation(String paramString)
    {
      assert (Thread.holdsLock(this));
      super.addURL(Launcher.getFileURL(new File(paramString)));
    }
    
    private static AccessControlContext getContext(File[] paramArrayOfFile)
      throws MalformedURLException
    {
      PathPermissions localPathPermissions = new PathPermissions(paramArrayOfFile);
      ProtectionDomain localProtectionDomain = new ProtectionDomain(new CodeSource(localPathPermissions.getCodeBase(), (Certificate[])null), localPathPermissions);
      AccessControlContext localAccessControlContext = new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
      return localAccessControlContext;
    }
    
    static
    {
      ClassLoader.registerAsParallelCapable();
    }
  }
  
  private static class BootClassPathHolder
  {
    static final URLClassPath bcp;
    
    private BootClassPathHolder() {}
    
    static
    {
      URL[] arrayOfURL;
      if (Launcher.bootClassPath != null) {
        arrayOfURL = (URL[])AccessController.doPrivileged(new PrivilegedAction()
        {
          public URL[] run()
          {
            File[] arrayOfFile = Launcher.getClassPath(Launcher.bootClassPath);
            int i = arrayOfFile.length;
            HashSet localHashSet = new HashSet();
            for (int j = 0; j < i; j++)
            {
              File localFile = arrayOfFile[j];
              if (!localFile.isDirectory()) {
                localFile = localFile.getParentFile();
              }
              if ((localFile != null) && (localHashSet.add(localFile))) {
                MetaIndex.registerDirectory(localFile);
              }
            }
            return Launcher.pathToURLs(arrayOfFile);
          }
        });
      } else {
        arrayOfURL = new URL[0];
      }
      bcp = new URLClassPath(arrayOfURL, Launcher.factory, null);
      bcp.initLookupCache(null);
    }
  }
  
  static class ExtClassLoader
    extends URLClassLoader
  {
    public static ExtClassLoader getExtClassLoader()
      throws IOException
    {
      File[] arrayOfFile = getExtDirs();
      try
      {
        (ExtClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Launcher.ExtClassLoader run()
            throws IOException
          {
            int i = val$dirs.length;
            for (int j = 0; j < i; j++) {
              MetaIndex.registerDirectory(val$dirs[j]);
            }
            return new Launcher.ExtClassLoader(val$dirs);
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((IOException)localPrivilegedActionException.getException());
      }
    }
    
    void addExtURL(URL paramURL)
    {
      super.addURL(paramURL);
    }
    
    public ExtClassLoader(File[] paramArrayOfFile)
      throws IOException
    {
      super(null, Launcher.factory);
      SharedSecrets.getJavaNetAccess().getURLClassPath(this).initLookupCache(this);
    }
    
    private static File[] getExtDirs()
    {
      String str = System.getProperty("java.ext.dirs");
      File[] arrayOfFile;
      if (str != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str, File.pathSeparator);
        int i = localStringTokenizer.countTokens();
        arrayOfFile = new File[i];
        for (int j = 0; j < i; j++) {
          arrayOfFile[j] = new File(localStringTokenizer.nextToken());
        }
      }
      else
      {
        arrayOfFile = new File[0];
      }
      return arrayOfFile;
    }
    
    private static URL[] getExtURLs(File[] paramArrayOfFile)
      throws IOException
    {
      Vector localVector = new Vector();
      for (int i = 0; i < paramArrayOfFile.length; i++)
      {
        String[] arrayOfString = paramArrayOfFile[i].list();
        if (arrayOfString != null) {
          for (int j = 0; j < arrayOfString.length; j++) {
            if (!arrayOfString[j].equals("meta-index"))
            {
              File localFile = new File(paramArrayOfFile[i], arrayOfString[j]);
              localVector.add(Launcher.getFileURL(localFile));
            }
          }
        }
      }
      URL[] arrayOfURL = new URL[localVector.size()];
      localVector.copyInto(arrayOfURL);
      return arrayOfURL;
    }
    
    public String findLibrary(String paramString)
    {
      paramString = System.mapLibraryName(paramString);
      URL[] arrayOfURL = super.getURLs();
      Object localObject = null;
      for (int i = 0; i < arrayOfURL.length; i++)
      {
        URI localURI;
        try
        {
          localURI = arrayOfURL[i].toURI();
        }
        catch (URISyntaxException localURISyntaxException)
        {
          continue;
        }
        File localFile1 = new File(localURI).getParentFile();
        if ((localFile1 != null) && (!localFile1.equals(localObject)))
        {
          String str = VM.getSavedProperty("os.arch");
          if (str != null)
          {
            localFile2 = new File(new File(localFile1, str), paramString);
            if (localFile2.exists()) {
              return localFile2.getAbsolutePath();
            }
          }
          File localFile2 = new File(localFile1, paramString);
          if (localFile2.exists()) {
            return localFile2.getAbsolutePath();
          }
        }
        localObject = localFile1;
      }
      return null;
    }
    
    private static AccessControlContext getContext(File[] paramArrayOfFile)
      throws IOException
    {
      PathPermissions localPathPermissions = new PathPermissions(paramArrayOfFile);
      ProtectionDomain localProtectionDomain = new ProtectionDomain(new CodeSource(localPathPermissions.getCodeBase(), (Certificate[])null), localPathPermissions);
      AccessControlContext localAccessControlContext = new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
      return localAccessControlContext;
    }
    
    static
    {
      ClassLoader.registerAsParallelCapable();
    }
  }
  
  private static class Factory
    implements URLStreamHandlerFactory
  {
    private static String PREFIX = "sun.net.www.protocol";
    
    private Factory() {}
    
    public URLStreamHandler createURLStreamHandler(String paramString)
    {
      String str = PREFIX + "." + paramString + ".Handler";
      try
      {
        Class localClass = Class.forName(str);
        return (URLStreamHandler)localClass.newInstance();
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw new InternalError("could not load " + paramString + "system protocol handler", localReflectiveOperationException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Launcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */