package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.ServiceNotFoundException;

public class MLet
  extends URLClassLoader
  implements MLetMBean, MBeanRegistration, Externalizable
{
  private static final long serialVersionUID = 3636148327800330130L;
  private MBeanServer server = null;
  private List<MLetContent> mletList = new ArrayList();
  private String libraryDirectory;
  private ObjectName mletObjectName = null;
  private URL[] myUrls = null;
  private transient ClassLoaderRepository currentClr;
  private transient boolean delegateToCLR;
  private Map<String, Class<?>> primitiveClasses = new HashMap(8);
  
  public MLet()
  {
    this(new URL[0]);
  }
  
  public MLet(URL[] paramArrayOfURL)
  {
    this(paramArrayOfURL, true);
  }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
  {
    this(paramArrayOfURL, paramClassLoader, true);
  }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory)
  {
    this(paramArrayOfURL, paramClassLoader, paramURLStreamHandlerFactory, true);
  }
  
  public MLet(URL[] paramArrayOfURL, boolean paramBoolean)
  {
    super(paramArrayOfURL);
    primitiveClasses.put(Boolean.TYPE.toString(), Boolean.class);
    primitiveClasses.put(Character.TYPE.toString(), Character.class);
    primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
    primitiveClasses.put(Short.TYPE.toString(), Short.class);
    primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
    primitiveClasses.put(Long.TYPE.toString(), Long.class);
    primitiveClasses.put(Float.TYPE.toString(), Float.class);
    primitiveClasses.put(Double.TYPE.toString(), Double.class);
    init(paramBoolean);
  }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, boolean paramBoolean)
  {
    super(paramArrayOfURL, paramClassLoader);
    primitiveClasses.put(Boolean.TYPE.toString(), Boolean.class);
    primitiveClasses.put(Character.TYPE.toString(), Character.class);
    primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
    primitiveClasses.put(Short.TYPE.toString(), Short.class);
    primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
    primitiveClasses.put(Long.TYPE.toString(), Long.class);
    primitiveClasses.put(Float.TYPE.toString(), Float.class);
    primitiveClasses.put(Double.TYPE.toString(), Double.class);
    init(paramBoolean);
  }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory, boolean paramBoolean)
  {
    super(paramArrayOfURL, paramClassLoader, paramURLStreamHandlerFactory);
    primitiveClasses.put(Boolean.TYPE.toString(), Boolean.class);
    primitiveClasses.put(Character.TYPE.toString(), Character.class);
    primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
    primitiveClasses.put(Short.TYPE.toString(), Short.class);
    primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
    primitiveClasses.put(Long.TYPE.toString(), Long.class);
    primitiveClasses.put(Float.TYPE.toString(), Float.class);
    primitiveClasses.put(Double.TYPE.toString(), Double.class);
    init(paramBoolean);
  }
  
  private void init(boolean paramBoolean)
  {
    delegateToCLR = paramBoolean;
    try
    {
      libraryDirectory = System.getProperty("jmx.mlet.library.dir");
      if (libraryDirectory == null) {
        libraryDirectory = getTmpDir();
      }
    }
    catch (SecurityException localSecurityException) {}
  }
  
  public void addURL(URL paramURL)
  {
    if (!Arrays.asList(getURLs()).contains(paramURL)) {
      super.addURL(paramURL);
    }
  }
  
  public void addURL(String paramString)
    throws ServiceNotFoundException
  {
    try
    {
      URL localURL = new URL(paramString);
      if (!Arrays.asList(getURLs()).contains(localURL)) {
        super.addURL(localURL);
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "addUrl", "Malformed URL: " + paramString, localMalformedURLException);
      }
      throw new ServiceNotFoundException("The specified URL is malformed");
    }
  }
  
  public URL[] getURLs()
  {
    return super.getURLs();
  }
  
  public Set<Object> getMBeansFromURL(URL paramURL)
    throws ServiceNotFoundException
  {
    if (paramURL == null) {
      throw new ServiceNotFoundException("The specified URL is null");
    }
    return getMBeansFromURL(paramURL.toString());
  }
  
  public Set<Object> getMBeansFromURL(String paramString)
    throws ServiceNotFoundException
  {
    String str1 = "getMBeansFromURL";
    if (server == null) {
      throw new IllegalStateException("This MLet MBean is not registered with an MBeanServer.");
    }
    if (paramString == null)
    {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "URL is null");
      throw new ServiceNotFoundException("The specified URL is null");
    }
    paramString = paramString.replace(File.separatorChar, '/');
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "<URL = " + paramString + ">");
    }
    try
    {
      MLetParser localMLetParser = new MLetParser();
      mletList = localMLetParser.parseURL(paramString);
    }
    catch (Exception localException1)
    {
      localObject2 = "Problems while parsing URL [" + paramString + "], got exception [" + localException1.toString() + "]";
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, (String)localObject2);
      throw ((ServiceNotFoundException)EnvHelp.initCause(new ServiceNotFoundException((String)localObject2), localException1));
    }
    if (mletList.size() == 0)
    {
      localObject1 = "File " + paramString + " not found or MLET tag not defined in file";
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, (String)localObject1);
      throw new ServiceNotFoundException((String)localObject1);
    }
    Object localObject1 = new HashSet();
    Object localObject2 = mletList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      MLetContent localMLetContent = (MLetContent)((Iterator)localObject2).next();
      String str2 = localMLetContent.getCode();
      if ((str2 != null) && (str2.endsWith(".class"))) {
        str2 = str2.substring(0, str2.length() - 6);
      }
      String str3 = localMLetContent.getName();
      URL localURL1 = localMLetContent.getCodeBase();
      String str4 = localMLetContent.getVersion();
      String str5 = localMLetContent.getSerializedObject();
      String str6 = localMLetContent.getJarFiles();
      URL localURL2 = localMLetContent.getDocumentBase();
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
      {
        localObject3 = new StringBuilder().append("\n\tMLET TAG     = ").append(localMLetContent.getAttributes()).append("\n\tCODEBASE     = ").append(localURL1).append("\n\tARCHIVE      = ").append(str6).append("\n\tCODE         = ").append(str2).append("\n\tOBJECT       = ").append(str5).append("\n\tNAME         = ").append(str3).append("\n\tVERSION      = ").append(str4).append("\n\tDOCUMENT URL = ").append(localURL2);
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, ((StringBuilder)localObject3).toString());
      }
      Object localObject3 = new StringTokenizer(str6, ",", false);
      Object localObject4;
      while (((StringTokenizer)localObject3).hasMoreTokens())
      {
        localObject4 = ((StringTokenizer)localObject3).nextToken().trim();
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "Load archive for codebase <" + localURL1 + ">, file <" + (String)localObject4 + ">");
        }
        try
        {
          localURL1 = check(str4, localURL1, (String)localObject4, localMLetContent);
        }
        catch (Exception localException2)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), str1, "Got unexpected exception", localException2);
          ((Set)localObject1).add(localException2);
        }
        continue;
        try
        {
          if (!Arrays.asList(getURLs()).contains(new URL(localURL1.toString() + (String)localObject4))) {
            addURL(localURL1 + (String)localObject4);
          }
        }
        catch (MalformedURLException localMalformedURLException) {}
      }
      if ((str2 != null) && (str5 != null))
      {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "CODE and OBJECT parameters cannot be specified at the same time in tag MLET");
        ((Set)localObject1).add(new Error("CODE and OBJECT parameters cannot be specified at the same time in tag MLET"));
      }
      else if ((str2 == null) && (str5 == null))
      {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "Either CODE or OBJECT parameter must be specified in tag MLET");
        ((Set)localObject1).add(new Error("Either CODE or OBJECT parameter must be specified in tag MLET"));
      }
      else
      {
        ObjectInstance localObjectInstance;
        try
        {
          if (str2 != null)
          {
            List localList1 = localMLetContent.getParameterTypes();
            List localList2 = localMLetContent.getParameterValues();
            ArrayList localArrayList = new ArrayList();
            for (int i = 0; i < localList1.size(); i++) {
              localArrayList.add(constructParameter((String)localList2.get(i), (String)localList1.get(i)));
            }
            if (localList1.isEmpty())
            {
              if (str3 == null) {
                localObjectInstance = server.createMBean(str2, null, mletObjectName);
              } else {
                localObjectInstance = server.createMBean(str2, new ObjectName(str3), mletObjectName);
              }
            }
            else
            {
              Object[] arrayOfObject = localArrayList.toArray();
              String[] arrayOfString = new String[localList1.size()];
              localList1.toArray(arrayOfString);
              if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
              {
                StringBuilder localStringBuilder = new StringBuilder();
                for (int j = 0; j < arrayOfString.length; j++) {
                  localStringBuilder.append("\n\tSignature     = ").append(arrayOfString[j]).append("\t\nParams        = ").append(arrayOfObject[j]);
                }
                JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), str1, localStringBuilder.toString());
              }
              if (str3 == null) {
                localObjectInstance = server.createMBean(str2, null, mletObjectName, arrayOfObject, arrayOfString);
              } else {
                localObjectInstance = server.createMBean(str2, new ObjectName(str3), mletObjectName, arrayOfObject, arrayOfString);
              }
            }
          }
          else
          {
            localObject4 = loadSerializedObject(localURL1, str5);
            if (str3 == null) {
              server.registerMBean(localObject4, null);
            } else {
              server.registerMBean(localObject4, new ObjectName(str3));
            }
            localObjectInstance = new ObjectInstance(str3, localObject4.getClass().getName());
          }
        }
        catch (ReflectionException localReflectionException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "ReflectionException", localReflectionException);
          ((Set)localObject1).add(localReflectionException);
          continue;
        }
        catch (InstanceAlreadyExistsException localInstanceAlreadyExistsException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "InstanceAlreadyExistsException", localInstanceAlreadyExistsException);
          ((Set)localObject1).add(localInstanceAlreadyExistsException);
          continue;
        }
        catch (MBeanRegistrationException localMBeanRegistrationException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "MBeanRegistrationException", localMBeanRegistrationException);
          ((Set)localObject1).add(localMBeanRegistrationException);
          continue;
        }
        catch (MBeanException localMBeanException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "MBeanException", localMBeanException);
          ((Set)localObject1).add(localMBeanException);
          continue;
        }
        catch (NotCompliantMBeanException localNotCompliantMBeanException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "NotCompliantMBeanException", localNotCompliantMBeanException);
          ((Set)localObject1).add(localNotCompliantMBeanException);
          continue;
        }
        catch (InstanceNotFoundException localInstanceNotFoundException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "InstanceNotFoundException", localInstanceNotFoundException);
          ((Set)localObject1).add(localInstanceNotFoundException);
          continue;
        }
        catch (IOException localIOException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "IOException", localIOException);
          ((Set)localObject1).add(localIOException);
          continue;
        }
        catch (SecurityException localSecurityException)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "SecurityException", localSecurityException);
          ((Set)localObject1).add(localSecurityException);
          continue;
        }
        catch (Exception localException3)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "Exception", localException3);
          ((Set)localObject1).add(localException3);
          continue;
        }
        catch (Error localError)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str1, "Error", localError);
          ((Set)localObject1).add(localError);
        }
        continue;
        ((Set)localObject1).add(localObjectInstance);
      }
    }
    return (Set<Object>)localObject1;
  }
  
  public synchronized String getLibraryDirectory()
  {
    return libraryDirectory;
  }
  
  public synchronized void setLibraryDirectory(String paramString)
  {
    libraryDirectory = paramString;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    setMBeanServer(paramMBeanServer);
    if (paramObjectName == null) {
      paramObjectName = new ObjectName(paramMBeanServer.getDefaultDomain() + ":" + "type=MLet");
    }
    mletObjectName = paramObjectName;
    return mletObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {}
  
  public void postDeregister() {}
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException, UnsupportedOperationException
  {
    throw new UnsupportedOperationException("MLet.writeExternal");
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException, UnsupportedOperationException
  {
    throw new UnsupportedOperationException("MLet.readExternal");
  }
  
  public synchronized Class<?> loadClass(String paramString, ClassLoaderRepository paramClassLoaderRepository)
    throws ClassNotFoundException
  {
    ClassLoaderRepository localClassLoaderRepository = currentClr;
    try
    {
      currentClr = paramClassLoaderRepository;
      Class localClass = loadClass(paramString);
      return localClass;
    }
    finally
    {
      currentClr = localClassLoaderRepository;
    }
  }
  
  protected Class<?> findClass(String paramString)
    throws ClassNotFoundException
  {
    return findClass(paramString, currentClr);
  }
  
  Class<?> findClass(String paramString, ClassLoaderRepository paramClassLoaderRepository)
    throws ClassNotFoundException
  {
    Class localClass = null;
    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", paramString);
    try
    {
      localClass = super.findClass(paramString);
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + paramString + " loaded through MLet classloader");
      }
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + paramString + " not found locally");
      }
    }
    if ((localClass == null) && (delegateToCLR) && (paramClassLoaderRepository != null)) {
      try
      {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + paramString + " : looking in CLR");
        }
        localClass = paramClassLoaderRepository.loadClassBefore(this, paramString);
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + paramString + " loaded through the default classloader repository");
        }
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + paramString + " not found in CLR");
        }
      }
    }
    if (localClass == null)
    {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Failed to load class " + paramString);
      throw new ClassNotFoundException(paramString);
    }
    return localClass;
  }
  
  protected String findLibrary(String paramString)
  {
    String str2 = "findLibrary";
    String str3 = System.mapLibraryName(paramString);
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "Search " + paramString + " in all JAR files");
    }
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "loadLibraryAsResource(" + str3 + ")");
    }
    String str1 = loadLibraryAsResource(str3);
    if (str1 != null)
    {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, str3 + " loaded, absolute path = " + str1);
      }
      return str1;
    }
    str3 = removeSpace(System.getProperty("os.name")) + File.separator + removeSpace(System.getProperty("os.arch")) + File.separator + removeSpace(System.getProperty("os.version")) + File.separator + "lib" + File.separator + str3;
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "loadLibraryAsResource(" + str3 + ")");
    }
    str1 = loadLibraryAsResource(str3);
    if (str1 != null)
    {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, str3 + " loaded, absolute path = " + str1);
      }
      return str1;
    }
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
    {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, paramString + " not found in any JAR file");
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "Search " + paramString + " along the path specified as the java.library.path property");
    }
    return null;
  }
  
  private String getTmpDir()
  {
    String str1 = System.getProperty("java.io.tmpdir");
    if (str1 != null) {
      return str1;
    }
    File localFile = null;
    try
    {
      localFile = File.createTempFile("tmp", "jmx");
      if (localFile == null)
      {
        localObject1 = null;
        boolean bool1;
        return (String)localObject1;
      }
      Object localObject1 = localFile.getParentFile();
      if (localObject1 == null)
      {
        str2 = null;
        boolean bool2;
        return str2;
      }
      str2 = ((File)localObject1).getAbsolutePath();
      boolean bool3;
      return str2;
    }
    catch (Exception localException1)
    {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to determine system temporary dir");
      String str2 = null;
      boolean bool4;
      return str2;
    }
    finally
    {
      if (localFile != null) {
        try
        {
          boolean bool5 = localFile.delete();
          if (!bool5) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
          }
        }
        catch (Exception localException6)
        {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temporary file", localException6);
        }
      }
    }
  }
  
  private synchronized String loadLibraryAsResource(String paramString)
  {
    try
    {
      InputStream localInputStream = getResourceAsStream(paramString.replace(File.separatorChar, '/'));
      if (localInputStream != null) {
        try
        {
          File localFile1 = new File(libraryDirectory);
          localFile1.mkdirs();
          File localFile2 = Files.createTempFile(localFile1.toPath(), paramString + ".", null, new FileAttribute[0]).toFile();
          localFile2.deleteOnExit();
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
          Object localObject1;
          try
          {
            localObject1 = new byte['က'];
            int i;
            while ((i = localInputStream.read((byte[])localObject1)) >= 0) {
              localFileOutputStream.write((byte[])localObject1, 0, i);
            }
          }
          finally {}
          if (localFile2.exists())
          {
            localObject1 = localFile2.getAbsolutePath();
            return (String)localObject1;
          }
        }
        finally
        {
          localInputStream.close();
        }
      }
    }
    catch (Exception localException)
    {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadLibraryAsResource", "Failed to load library : " + paramString, localException);
      return null;
    }
    return null;
  }
  
  private static String removeSpace(String paramString)
  {
    return paramString.trim().replace(" ", "");
  }
  
  protected URL check(String paramString1, URL paramURL, String paramString2, MLetContent paramMLetContent)
    throws Exception
  {
    return paramURL;
  }
  
  private Object loadSerializedObject(URL paramURL, String paramString)
    throws IOException, ClassNotFoundException
  {
    if (paramString != null) {
      paramString = paramString.replace(File.separatorChar, '/');
    }
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "loadSerializedObject", paramURL.toString() + paramString);
    }
    InputStream localInputStream = getResourceAsStream(paramString);
    if (localInputStream != null) {
      try
      {
        MLetObjectInputStream localMLetObjectInputStream = new MLetObjectInputStream(localInputStream, this);
        Object localObject = localMLetObjectInputStream.readObject();
        localMLetObjectInputStream.close();
        return localObject;
      }
      catch (IOException localIOException)
      {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Exception while deserializing " + paramString, localIOException);
        }
        throw localIOException;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Exception while deserializing " + paramString, localClassNotFoundException);
        }
        throw localClassNotFoundException;
      }
    }
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Error: File " + paramString + " containing serialized object not found");
    }
    throw new Error("File " + paramString + " containing serialized object not found");
  }
  
  private Object constructParameter(String paramString1, String paramString2)
  {
    Class localClass = (Class)primitiveClasses.get(paramString2);
    if (localClass != null) {
      try
      {
        Constructor localConstructor = localClass.getConstructor(new Class[] { String.class });
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = paramString1;
        return localConstructor.newInstance(arrayOfObject);
      }
      catch (Exception localException)
      {
        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "constructParameter", "Got unexpected exception", localException);
      }
    }
    if (paramString2.compareTo("java.lang.Boolean") == 0) {
      return Boolean.valueOf(paramString1);
    }
    if (paramString2.compareTo("java.lang.Byte") == 0) {
      return new Byte(paramString1);
    }
    if (paramString2.compareTo("java.lang.Short") == 0) {
      return new Short(paramString1);
    }
    if (paramString2.compareTo("java.lang.Long") == 0) {
      return new Long(paramString1);
    }
    if (paramString2.compareTo("java.lang.Integer") == 0) {
      return new Integer(paramString1);
    }
    if (paramString2.compareTo("java.lang.Float") == 0) {
      return new Float(paramString1);
    }
    if (paramString2.compareTo("java.lang.Double") == 0) {
      return new Double(paramString1);
    }
    if (paramString2.compareTo("java.lang.String") == 0) {
      return paramString1;
    }
    return paramString1;
  }
  
  private synchronized void setMBeanServer(final MBeanServer paramMBeanServer)
  {
    server = paramMBeanServer;
    PrivilegedAction local1 = new PrivilegedAction()
    {
      public ClassLoaderRepository run()
      {
        return paramMBeanServer.getClassLoaderRepository();
      }
    };
    currentClr = ((ClassLoaderRepository)AccessController.doPrivileged(local1));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\MLet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */