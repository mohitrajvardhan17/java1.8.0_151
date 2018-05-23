package sun.misc;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.net.util.URLUtil;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

public class URLClassPath
{
  static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
  static final String JAVA_VERSION = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
  private static final boolean DEBUG = AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debug")) != null;
  private static final boolean DEBUG_LOOKUP_CACHE = AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debugLookupCache")) != null;
  private static final boolean DISABLE_JAR_CHECKING;
  private static final boolean DISABLE_ACC_CHECKING;
  private ArrayList<URL> path = new ArrayList();
  Stack<URL> urls = new Stack();
  ArrayList<Loader> loaders = new ArrayList();
  HashMap<String, Loader> lmap = new HashMap();
  private URLStreamHandler jarHandler;
  private boolean closed = false;
  private final AccessControlContext acc;
  private static volatile boolean lookupCacheEnabled = "true".equals(VM.getSavedProperty("sun.cds.enableSharedLookupCache"));
  private URL[] lookupCacheURLs;
  private ClassLoader lookupCacheLoader;
  
  public URLClassPath(URL[] paramArrayOfURL, URLStreamHandlerFactory paramURLStreamHandlerFactory, AccessControlContext paramAccessControlContext)
  {
    for (int i = 0; i < paramArrayOfURL.length; i++) {
      path.add(paramArrayOfURL[i]);
    }
    push(paramArrayOfURL);
    if (paramURLStreamHandlerFactory != null) {
      jarHandler = paramURLStreamHandlerFactory.createURLStreamHandler("jar");
    }
    if (DISABLE_ACC_CHECKING) {
      acc = null;
    } else {
      acc = paramAccessControlContext;
    }
  }
  
  public URLClassPath(URL[] paramArrayOfURL)
  {
    this(paramArrayOfURL, null, null);
  }
  
  public URLClassPath(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext)
  {
    this(paramArrayOfURL, null, paramAccessControlContext);
  }
  
  public synchronized List<IOException> closeLoaders()
  {
    if (closed) {
      return Collections.emptyList();
    }
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = loaders.iterator();
    while (localIterator.hasNext())
    {
      Loader localLoader = (Loader)localIterator.next();
      try
      {
        localLoader.close();
      }
      catch (IOException localIOException)
      {
        localLinkedList.add(localIOException);
      }
    }
    closed = true;
    return localLinkedList;
  }
  
  public synchronized void addURL(URL paramURL)
  {
    if (closed) {
      return;
    }
    synchronized (urls)
    {
      if ((paramURL == null) || (path.contains(paramURL))) {
        return;
      }
      urls.add(0, paramURL);
      path.add(paramURL);
      if (lookupCacheURLs != null) {
        disableAllLookupCaches();
      }
    }
  }
  
  /* Error */
  public URL[] getURLs()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 414	sun/misc/URLClassPath:urls	Ljava/util/Stack;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 412	sun/misc/URLClassPath:path	Ljava/util/ArrayList;
    //   11: aload_0
    //   12: getfield 412	sun/misc/URLClassPath:path	Ljava/util/ArrayList;
    //   15: invokevirtual 441	java/util/ArrayList:size	()I
    //   18: anewarray 221	java/net/URL
    //   21: invokevirtual 447	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   24: checkcast 206	[Ljava/net/URL;
    //   27: aload_1
    //   28: monitorexit
    //   29: areturn
    //   30: astore_2
    //   31: aload_1
    //   32: monitorexit
    //   33: aload_2
    //   34: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	URLClassPath
    //   5	27	1	Ljava/lang/Object;	Object
    //   30	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	29	30	finally
    //   30	33	30	finally
  }
  
  public URL findResource(String paramString, boolean paramBoolean)
  {
    int[] arrayOfInt = getLookupCache(paramString);
    Loader localLoader;
    for (int i = 0; (localLoader = getNextLoader(arrayOfInt, i)) != null; i++)
    {
      URL localURL = localLoader.findResource(paramString, paramBoolean);
      if (localURL != null) {
        return localURL;
      }
    }
    return null;
  }
  
  public Resource getResource(String paramString, boolean paramBoolean)
  {
    if (DEBUG) {
      System.err.println("URLClassPath.getResource(\"" + paramString + "\")");
    }
    int[] arrayOfInt = getLookupCache(paramString);
    Loader localLoader;
    for (int i = 0; (localLoader = getNextLoader(arrayOfInt, i)) != null; i++)
    {
      Resource localResource = localLoader.getResource(paramString, paramBoolean);
      if (localResource != null) {
        return localResource;
      }
    }
    return null;
  }
  
  public Enumeration<URL> findResources(final String paramString, final boolean paramBoolean)
  {
    new Enumeration()
    {
      private int index = 0;
      private int[] cache = URLClassPath.this.getLookupCache(paramString);
      private URL url = null;
      
      private boolean next()
      {
        if (url != null) {
          return true;
        }
        URLClassPath.Loader localLoader;
        while ((localLoader = URLClassPath.this.getNextLoader(cache, index++)) != null)
        {
          url = localLoader.findResource(paramString, paramBoolean);
          if (url != null) {
            return true;
          }
        }
        return false;
      }
      
      public boolean hasMoreElements()
      {
        return next();
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
    };
  }
  
  public Resource getResource(String paramString)
  {
    return getResource(paramString, true);
  }
  
  public Enumeration<Resource> getResources(final String paramString, final boolean paramBoolean)
  {
    new Enumeration()
    {
      private int index = 0;
      private int[] cache = URLClassPath.this.getLookupCache(paramString);
      private Resource res = null;
      
      private boolean next()
      {
        if (res != null) {
          return true;
        }
        URLClassPath.Loader localLoader;
        while ((localLoader = URLClassPath.this.getNextLoader(cache, index++)) != null)
        {
          res = localLoader.getResource(paramString, paramBoolean);
          if (res != null) {
            return true;
          }
        }
        return false;
      }
      
      public boolean hasMoreElements()
      {
        return next();
      }
      
      public Resource nextElement()
      {
        if (!next()) {
          throw new NoSuchElementException();
        }
        Resource localResource = res;
        res = null;
        return localResource;
      }
    };
  }
  
  public Enumeration<Resource> getResources(String paramString)
  {
    return getResources(paramString, true);
  }
  
  synchronized void initLookupCache(ClassLoader paramClassLoader)
  {
    if ((lookupCacheURLs = getLookupCacheURLs(paramClassLoader)) != null) {
      lookupCacheLoader = paramClassLoader;
    } else {
      disableAllLookupCaches();
    }
  }
  
  static void disableAllLookupCaches()
  {
    lookupCacheEnabled = false;
  }
  
  private static native URL[] getLookupCacheURLs(ClassLoader paramClassLoader);
  
  private static native int[] getLookupCacheForClassLoader(ClassLoader paramClassLoader, String paramString);
  
  private static native boolean knownToNotExist0(ClassLoader paramClassLoader, String paramString);
  
  synchronized boolean knownToNotExist(String paramString)
  {
    if ((lookupCacheURLs != null) && (lookupCacheEnabled)) {
      return knownToNotExist0(lookupCacheLoader, paramString);
    }
    return false;
  }
  
  private synchronized int[] getLookupCache(String paramString)
  {
    if ((lookupCacheURLs == null) || (!lookupCacheEnabled)) {
      return null;
    }
    int[] arrayOfInt = getLookupCacheForClassLoader(lookupCacheLoader, paramString);
    if ((arrayOfInt != null) && (arrayOfInt.length > 0))
    {
      int i = arrayOfInt[(arrayOfInt.length - 1)];
      if (!ensureLoaderOpened(i))
      {
        if (DEBUG_LOOKUP_CACHE) {
          System.out.println("Expanded loaders FAILED " + loaders.size() + " for maxindex=" + i);
        }
        return null;
      }
    }
    return arrayOfInt;
  }
  
  private boolean ensureLoaderOpened(int paramInt)
  {
    if (loaders.size() <= paramInt)
    {
      if (getLoader(paramInt) == null) {
        return false;
      }
      if (!lookupCacheEnabled) {
        return false;
      }
      if (DEBUG_LOOKUP_CACHE) {
        System.out.println("Expanded loaders " + loaders.size() + " to index=" + paramInt);
      }
    }
    return true;
  }
  
  private synchronized void validateLookupCache(int paramInt, String paramString)
  {
    if ((lookupCacheURLs != null) && (lookupCacheEnabled))
    {
      if ((paramInt < lookupCacheURLs.length) && (paramString.equals(URLUtil.urlNoFragString(lookupCacheURLs[paramInt])))) {
        return;
      }
      if ((DEBUG) || (DEBUG_LOOKUP_CACHE)) {
        System.out.println("WARNING: resource lookup cache invalidated for lookupCacheLoader at " + paramInt);
      }
      disableAllLookupCaches();
    }
  }
  
  private synchronized Loader getNextLoader(int[] paramArrayOfInt, int paramInt)
  {
    if (closed) {
      return null;
    }
    if (paramArrayOfInt != null)
    {
      if (paramInt < paramArrayOfInt.length)
      {
        Loader localLoader = (Loader)loaders.get(paramArrayOfInt[paramInt]);
        if (DEBUG_LOOKUP_CACHE) {
          System.out.println("HASCACHE: Loading from : " + paramArrayOfInt[paramInt] + " = " + localLoader.getBaseURL());
        }
        return localLoader;
      }
      return null;
    }
    return getLoader(paramInt);
  }
  
  private synchronized Loader getLoader(int paramInt)
  {
    if (closed) {
      return null;
    }
    while (loaders.size() < paramInt + 1)
    {
      URL localURL;
      synchronized (urls)
      {
        if (urls.empty()) {
          return null;
        }
        localURL = (URL)urls.pop();
      }
      ??? = URLUtil.urlNoFragString(localURL);
      if (!lmap.containsKey(???))
      {
        Loader localLoader;
        try
        {
          localLoader = getLoader(localURL);
          URL[] arrayOfURL = localLoader.getClassPath();
          if (arrayOfURL != null) {
            push(arrayOfURL);
          }
        }
        catch (IOException localIOException)
        {
          continue;
        }
        catch (SecurityException localSecurityException)
        {
          if (DEBUG) {
            System.err.println("Failed to access " + localURL + ", " + localSecurityException);
          }
        }
        continue;
        validateLookupCache(loaders.size(), (String)???);
        loaders.add(localLoader);
        lmap.put(???, localLoader);
      }
    }
    if (DEBUG_LOOKUP_CACHE) {
      System.out.println("NOCACHE: Loading from : " + paramInt);
    }
    return (Loader)loaders.get(paramInt);
  }
  
  private Loader getLoader(final URL paramURL)
    throws IOException
  {
    try
    {
      (Loader)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public URLClassPath.Loader run()
          throws IOException
        {
          String str = paramURL.getFile();
          if ((str != null) && (str.endsWith("/")))
          {
            if ("file".equals(paramURL.getProtocol())) {
              return new URLClassPath.FileLoader(paramURL);
            }
            return new URLClassPath.Loader(paramURL);
          }
          return new URLClassPath.JarLoader(paramURL, jarHandler, lmap, acc);
        }
      }, acc);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  private void push(URL[] paramArrayOfURL)
  {
    synchronized (urls)
    {
      for (int i = paramArrayOfURL.length - 1; i >= 0; i--) {
        urls.push(paramArrayOfURL[i]);
      }
    }
  }
  
  public static URL[] pathToURLs(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
    Object localObject1 = new URL[localStringTokenizer.countTokens()];
    int i = 0;
    Object localObject2;
    while (localStringTokenizer.hasMoreTokens())
    {
      localObject2 = new File(localStringTokenizer.nextToken());
      try
      {
        localObject2 = new File(((File)localObject2).getCanonicalPath());
      }
      catch (IOException localIOException1) {}
      try
      {
        localObject1[(i++)] = ParseUtil.fileToEncodedURL((File)localObject2);
      }
      catch (IOException localIOException2) {}
    }
    if (localObject1.length != i)
    {
      localObject2 = new URL[i];
      System.arraycopy(localObject1, 0, localObject2, 0, i);
      localObject1 = localObject2;
    }
    return (URL[])localObject1;
  }
  
  public URL checkURL(URL paramURL)
  {
    try
    {
      check(paramURL);
    }
    catch (Exception localException)
    {
      return null;
    }
    return paramURL;
  }
  
  static void check(URL paramURL)
    throws IOException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      URLConnection localURLConnection = paramURL.openConnection();
      Permission localPermission = localURLConnection.getPermission();
      if (localPermission != null) {
        try
        {
          localSecurityManager.checkPermission(localPermission);
        }
        catch (SecurityException localSecurityException)
        {
          if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1))
          {
            localSecurityManager.checkRead(localPermission.getName());
          }
          else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1))
          {
            URL localURL = paramURL;
            if ((localURLConnection instanceof JarURLConnection)) {
              localURL = ((JarURLConnection)localURLConnection).getJarFileURL();
            }
            localSecurityManager.checkConnect(localURL.getHost(), localURL.getPort());
          }
          else
          {
            throw localSecurityException;
          }
        }
      }
    }
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.disableJarChecking"));
    DISABLE_JAR_CHECKING = (str.equals("true")) || (str.equals(""));
    str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.net.URLClassPath.disableRestrictedPermissions"));
    DISABLE_ACC_CHECKING = (str.equals("true")) || (str.equals(""));
  }
  
  private static class FileLoader
    extends URLClassPath.Loader
  {
    private File dir;
    
    FileLoader(URL paramURL)
      throws IOException
    {
      super();
      if (!"file".equals(paramURL.getProtocol())) {
        throw new IllegalArgumentException("url");
      }
      String str = paramURL.getFile().replace('/', File.separatorChar);
      str = ParseUtil.decode(str);
      dir = new File(str).getCanonicalFile();
    }
    
    URL findResource(String paramString, boolean paramBoolean)
    {
      Resource localResource = getResource(paramString, paramBoolean);
      if (localResource != null) {
        return localResource.getURL();
      }
      return null;
    }
    
    Resource getResource(final String paramString, boolean paramBoolean)
    {
      try
      {
        URL localURL2 = new URL(getBaseURL(), ".");
        final URL localURL1 = new URL(getBaseURL(), ParseUtil.encodePath(paramString, false));
        if (!localURL1.getFile().startsWith(localURL2.getFile())) {
          return null;
        }
        if (paramBoolean) {
          URLClassPath.check(localURL1);
        }
        final File localFile;
        if (paramString.indexOf("..") != -1)
        {
          localFile = new File(dir, paramString.replace('/', File.separatorChar)).getCanonicalFile();
          if (!localFile.getPath().startsWith(dir.getPath())) {
            return null;
          }
        }
        else
        {
          localFile = new File(dir, paramString.replace('/', File.separatorChar));
        }
        if (localFile.exists()) {
          new Resource()
          {
            public String getName()
            {
              return paramString;
            }
            
            public URL getURL()
            {
              return localURL1;
            }
            
            public URL getCodeSourceURL()
            {
              return getBaseURL();
            }
            
            public InputStream getInputStream()
              throws IOException
            {
              return new FileInputStream(localFile);
            }
            
            public int getContentLength()
              throws IOException
            {
              return (int)localFile.length();
            }
          };
        }
      }
      catch (Exception localException)
      {
        return null;
      }
      return null;
    }
  }
  
  static class JarLoader
    extends URLClassPath.Loader
  {
    private JarFile jar;
    private final URL csu;
    private JarIndex index;
    private MetaIndex metaIndex;
    private URLStreamHandler handler;
    private final HashMap<String, URLClassPath.Loader> lmap;
    private final AccessControlContext acc;
    private boolean closed = false;
    private static final JavaUtilZipFileAccess zipAccess = ;
    
    JarLoader(URL paramURL, URLStreamHandler paramURLStreamHandler, HashMap<String, URLClassPath.Loader> paramHashMap, AccessControlContext paramAccessControlContext)
      throws IOException
    {
      super();
      csu = paramURL;
      handler = paramURLStreamHandler;
      lmap = paramHashMap;
      acc = paramAccessControlContext;
      if (!isOptimizable(paramURL))
      {
        ensureOpen();
      }
      else
      {
        String str = paramURL.getFile();
        if (str != null)
        {
          str = ParseUtil.decode(str);
          File localFile = new File(str);
          metaIndex = MetaIndex.forJar(localFile);
          if ((metaIndex != null) && (!localFile.exists())) {
            metaIndex = null;
          }
        }
        if (metaIndex == null) {
          ensureOpen();
        }
      }
    }
    
    public void close()
      throws IOException
    {
      if (!closed)
      {
        closed = true;
        ensureOpen();
        jar.close();
      }
    }
    
    JarFile getJarFile()
    {
      return jar;
    }
    
    private boolean isOptimizable(URL paramURL)
    {
      return "file".equals(paramURL.getProtocol());
    }
    
    private void ensureOpen()
      throws IOException
    {
      if (jar == null) {
        try
        {
          AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            public Void run()
              throws IOException
            {
              if (URLClassPath.DEBUG)
              {
                System.err.println("Opening " + csu);
                Thread.dumpStack();
              }
              jar = URLClassPath.JarLoader.this.getJarFile(csu);
              index = JarIndex.getJarIndex(jar, metaIndex);
              if (index != null)
              {
                String[] arrayOfString = index.getJarFiles();
                for (int i = 0; i < arrayOfString.length; i++) {
                  try
                  {
                    URL localURL = new URL(csu, arrayOfString[i]);
                    String str = URLUtil.urlNoFragString(localURL);
                    if (!lmap.containsKey(str)) {
                      lmap.put(str, null);
                    }
                  }
                  catch (MalformedURLException localMalformedURLException) {}
                }
              }
              return null;
            }
          }, acc);
        }
        catch (PrivilegedActionException localPrivilegedActionException)
        {
          throw ((IOException)localPrivilegedActionException.getException());
        }
      }
    }
    
    static JarFile checkJar(JarFile paramJarFile)
      throws IOException
    {
      if ((System.getSecurityManager() != null) && (!URLClassPath.DISABLE_JAR_CHECKING) && (!zipAccess.startsWithLocHeader(paramJarFile)))
      {
        IOException localIOException1 = new IOException("Invalid Jar file");
        try
        {
          paramJarFile.close();
        }
        catch (IOException localIOException2)
        {
          localIOException1.addSuppressed(localIOException2);
        }
        throw localIOException1;
      }
      return paramJarFile;
    }
    
    private JarFile getJarFile(URL paramURL)
      throws IOException
    {
      if (isOptimizable(paramURL))
      {
        localObject = new FileURLMapper(paramURL);
        if (!((FileURLMapper)localObject).exists()) {
          throw new FileNotFoundException(((FileURLMapper)localObject).getPath());
        }
        return checkJar(new JarFile(((FileURLMapper)localObject).getPath()));
      }
      Object localObject = getBaseURL().openConnection();
      ((URLConnection)localObject).setRequestProperty("UA-Java-Version", URLClassPath.JAVA_VERSION);
      JarFile localJarFile = ((JarURLConnection)localObject).getJarFile();
      return checkJar(localJarFile);
    }
    
    JarIndex getIndex()
    {
      try
      {
        ensureOpen();
      }
      catch (IOException localIOException)
      {
        throw new InternalError(localIOException);
      }
      return index;
    }
    
    Resource checkResource(final String paramString, boolean paramBoolean, final JarEntry paramJarEntry)
    {
      final URL localURL;
      try
      {
        localURL = new URL(getBaseURL(), ParseUtil.encodePath(paramString, false));
        if (paramBoolean) {
          URLClassPath.check(localURL);
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        return null;
      }
      catch (IOException localIOException)
      {
        return null;
      }
      catch (AccessControlException localAccessControlException)
      {
        return null;
      }
      new Resource()
      {
        public String getName()
        {
          return paramString;
        }
        
        public URL getURL()
        {
          return localURL;
        }
        
        public URL getCodeSourceURL()
        {
          return csu;
        }
        
        public InputStream getInputStream()
          throws IOException
        {
          return jar.getInputStream(paramJarEntry);
        }
        
        public int getContentLength()
        {
          return (int)paramJarEntry.getSize();
        }
        
        public Manifest getManifest()
          throws IOException
        {
          return jar.getManifest();
        }
        
        public Certificate[] getCertificates()
        {
          return paramJarEntry.getCertificates();
        }
        
        public CodeSigner[] getCodeSigners()
        {
          return paramJarEntry.getCodeSigners();
        }
      };
    }
    
    boolean validIndex(String paramString)
    {
      String str1 = paramString;
      int i;
      if ((i = paramString.lastIndexOf("/")) != -1) {
        str1 = paramString.substring(0, i);
      }
      Enumeration localEnumeration = jar.entries();
      while (localEnumeration.hasMoreElements())
      {
        ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
        String str2 = localZipEntry.getName();
        if ((i = str2.lastIndexOf("/")) != -1) {
          str2 = str2.substring(0, i);
        }
        if (str2.equals(str1)) {
          return true;
        }
      }
      return false;
    }
    
    URL findResource(String paramString, boolean paramBoolean)
    {
      Resource localResource = getResource(paramString, paramBoolean);
      if (localResource != null) {
        return localResource.getURL();
      }
      return null;
    }
    
    Resource getResource(String paramString, boolean paramBoolean)
    {
      if ((metaIndex != null) && (!metaIndex.mayContain(paramString))) {
        return null;
      }
      try
      {
        ensureOpen();
      }
      catch (IOException localIOException)
      {
        throw new InternalError(localIOException);
      }
      JarEntry localJarEntry = jar.getJarEntry(paramString);
      if (localJarEntry != null) {
        return checkResource(paramString, paramBoolean, localJarEntry);
      }
      if (index == null) {
        return null;
      }
      HashSet localHashSet = new HashSet();
      return getResource(paramString, paramBoolean, localHashSet);
    }
    
    Resource getResource(String paramString, boolean paramBoolean, Set<String> paramSet)
    {
      int i = 0;
      LinkedList localLinkedList = null;
      if ((localLinkedList = index.get(paramString)) == null) {
        return null;
      }
      do
      {
        int j = localLinkedList.size();
        String[] arrayOfString = (String[])localLinkedList.toArray(new String[j]);
        while (i < j)
        {
          String str1 = arrayOfString[(i++)];
          final URL localURL;
          JarLoader localJarLoader;
          try
          {
            localURL = new URL(csu, str1);
            String str2 = URLUtil.urlNoFragString(localURL);
            if ((localJarLoader = (JarLoader)lmap.get(str2)) == null)
            {
              localJarLoader = (JarLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
              {
                public URLClassPath.JarLoader run()
                  throws IOException
                {
                  return new URLClassPath.JarLoader(localURL, handler, lmap, acc);
                }
              }, acc);
              JarIndex localJarIndex = localJarLoader.getIndex();
              if (localJarIndex != null)
              {
                int m = str1.lastIndexOf("/");
                localJarIndex.merge(index, m == -1 ? null : str1.substring(0, m + 1));
              }
              lmap.put(str2, localJarLoader);
            }
          }
          catch (PrivilegedActionException localPrivilegedActionException)
          {
            continue;
          }
          catch (MalformedURLException localMalformedURLException) {}
          continue;
          int k = !paramSet.add(URLUtil.urlNoFragString(localURL)) ? 1 : 0;
          if (k == 0)
          {
            try
            {
              localJarLoader.ensureOpen();
            }
            catch (IOException localIOException)
            {
              throw new InternalError(localIOException);
            }
            JarEntry localJarEntry = jar.getJarEntry(paramString);
            if (localJarEntry != null) {
              return localJarLoader.checkResource(paramString, paramBoolean, localJarEntry);
            }
            if (!localJarLoader.validIndex(paramString)) {
              throw new InvalidJarIndexException("Invalid index");
            }
          }
          if ((k == 0) && (localJarLoader != this) && (localJarLoader.getIndex() != null))
          {
            Resource localResource;
            if ((localResource = localJarLoader.getResource(paramString, paramBoolean, paramSet)) != null) {
              return localResource;
            }
          }
        }
        localLinkedList = index.get(paramString);
      } while (i < localLinkedList.size());
      return null;
    }
    
    URL[] getClassPath()
      throws IOException
    {
      if (index != null) {
        return null;
      }
      if (metaIndex != null) {
        return null;
      }
      ensureOpen();
      parseExtensionsDependencies();
      if (SharedSecrets.javaUtilJarAccess().jarFileHasClassPathAttribute(jar))
      {
        Manifest localManifest = jar.getManifest();
        if (localManifest != null)
        {
          Attributes localAttributes = localManifest.getMainAttributes();
          if (localAttributes != null)
          {
            String str = localAttributes.getValue(Attributes.Name.CLASS_PATH);
            if (str != null) {
              return parseClassPath(csu, str);
            }
          }
        }
      }
      return null;
    }
    
    private void parseExtensionsDependencies()
      throws IOException
    {
      ExtensionDependency.checkExtensionsDependencies(jar);
    }
    
    private URL[] parseClassPath(URL paramURL, String paramString)
      throws MalformedURLException
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      URL[] arrayOfURL = new URL[localStringTokenizer.countTokens()];
      for (int i = 0; localStringTokenizer.hasMoreTokens(); i++)
      {
        String str = localStringTokenizer.nextToken();
        arrayOfURL[i] = new URL(paramURL, str);
      }
      return arrayOfURL;
    }
  }
  
  private static class Loader
    implements Closeable
  {
    private final URL base;
    private JarFile jarfile;
    
    Loader(URL paramURL)
    {
      base = paramURL;
    }
    
    URL getBaseURL()
    {
      return base;
    }
    
    URL findResource(String paramString, boolean paramBoolean)
    {
      URL localURL;
      try
      {
        localURL = new URL(base, ParseUtil.encodePath(paramString, false));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new IllegalArgumentException("name");
      }
      try
      {
        if (paramBoolean) {
          URLClassPath.check(localURL);
        }
        URLConnection localURLConnection = localURL.openConnection();
        Object localObject;
        if ((localURLConnection instanceof HttpURLConnection))
        {
          localObject = (HttpURLConnection)localURLConnection;
          ((HttpURLConnection)localObject).setRequestMethod("HEAD");
          if (((HttpURLConnection)localObject).getResponseCode() >= 400) {
            return null;
          }
        }
        else
        {
          localURLConnection.setUseCaches(false);
          localObject = localURLConnection.getInputStream();
          ((InputStream)localObject).close();
        }
        return localURL;
      }
      catch (Exception localException) {}
      return null;
    }
    
    Resource getResource(final String paramString, boolean paramBoolean)
    {
      final URL localURL;
      try
      {
        localURL = new URL(base, ParseUtil.encodePath(paramString, false));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new IllegalArgumentException("name");
      }
      final URLConnection localURLConnection;
      try
      {
        if (paramBoolean) {
          URLClassPath.check(localURL);
        }
        localURLConnection = localURL.openConnection();
        InputStream localInputStream = localURLConnection.getInputStream();
        if ((localURLConnection instanceof JarURLConnection))
        {
          JarURLConnection localJarURLConnection = (JarURLConnection)localURLConnection;
          jarfile = URLClassPath.JarLoader.checkJar(localJarURLConnection.getJarFile());
        }
      }
      catch (Exception localException)
      {
        return null;
      }
      new Resource()
      {
        public String getName()
        {
          return paramString;
        }
        
        public URL getURL()
        {
          return localURL;
        }
        
        public URL getCodeSourceURL()
        {
          return base;
        }
        
        public InputStream getInputStream()
          throws IOException
        {
          return localURLConnection.getInputStream();
        }
        
        public int getContentLength()
          throws IOException
        {
          return localURLConnection.getContentLength();
        }
      };
    }
    
    Resource getResource(String paramString)
    {
      return getResource(paramString, true);
    }
    
    public void close()
      throws IOException
    {
      if (jarfile != null) {
        jarfile.close();
      }
    }
    
    URL[] getClassPath()
      throws IOException
    {
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\URLClassPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */