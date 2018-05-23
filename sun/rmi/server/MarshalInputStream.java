package sun.rmi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.misc.JavaObjectInputStreamAccess;
import sun.misc.ObjectStreamClassValidator;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

public class MarshalInputStream
  extends ObjectInputStream
{
  private volatile StreamChecker streamChecker = null;
  private static final boolean useCodebaseOnlyProperty = !((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.useCodebaseOnly", "true"))).equalsIgnoreCase("false");
  protected static Map<String, Class<?>> permittedSunClasses = new HashMap(3);
  private boolean skipDefaultResolveClass = false;
  private final Map<Object, Runnable> doneCallbacks = new HashMap(3);
  private boolean useCodebaseOnly = useCodebaseOnlyProperty;
  
  public MarshalInputStream(InputStream paramInputStream)
    throws IOException, StreamCorruptedException
  {
    super(paramInputStream);
  }
  
  public Runnable getDoneCallback(Object paramObject)
  {
    return (Runnable)doneCallbacks.get(paramObject);
  }
  
  public void setDoneCallback(Object paramObject, Runnable paramRunnable)
  {
    doneCallbacks.put(paramObject, paramRunnable);
  }
  
  public void done()
  {
    Iterator localIterator = doneCallbacks.values().iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      localRunnable.run();
    }
    doneCallbacks.clear();
  }
  
  public void close()
    throws IOException
  {
    done();
    super.close();
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    Object localObject = readLocation();
    String str1 = paramObjectStreamClass.getName();
    ClassLoader localClassLoader = skipDefaultResolveClass ? null : latestUserDefinedLoader();
    String str2 = null;
    if ((!useCodebaseOnly) && ((localObject instanceof String))) {
      str2 = (String)localObject;
    }
    try
    {
      return RMIClassLoader.loadClass(str2, str1, localClassLoader);
    }
    catch (AccessControlException localAccessControlException)
    {
      return checkSunClass(str1, localAccessControlException);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      try
      {
        if ((Character.isLowerCase(str1.charAt(0))) && (str1.indexOf('.') == -1)) {
          return super.resolveClass(paramObjectStreamClass);
        }
      }
      catch (ClassNotFoundException localClassNotFoundException2) {}
      throw localClassNotFoundException1;
    }
  }
  
  protected Class<?> resolveProxyClass(String[] paramArrayOfString)
    throws IOException, ClassNotFoundException
  {
    StreamChecker localStreamChecker = streamChecker;
    if (localStreamChecker != null) {
      localStreamChecker.checkProxyInterfaceNames(paramArrayOfString);
    }
    Object localObject = readLocation();
    ClassLoader localClassLoader = skipDefaultResolveClass ? null : latestUserDefinedLoader();
    String str = null;
    if ((!useCodebaseOnly) && ((localObject instanceof String))) {
      str = (String)localObject;
    }
    return RMIClassLoader.loadProxyClass(str, paramArrayOfString, localClassLoader);
  }
  
  private static ClassLoader latestUserDefinedLoader()
  {
    return VM.latestUserDefinedLoader();
  }
  
  private Class<?> checkSunClass(String paramString, AccessControlException paramAccessControlException)
    throws AccessControlException
  {
    Permission localPermission = paramAccessControlException.getPermission();
    String str = null;
    if (localPermission != null) {
      str = localPermission.getName();
    }
    Class localClass = (Class)permittedSunClasses.get(paramString);
    if ((str == null) || (localClass == null) || ((!str.equals("accessClassInPackage.sun.rmi.server")) && (!str.equals("accessClassInPackage.sun.rmi.registry")))) {
      throw paramAccessControlException;
    }
    return localClass;
  }
  
  protected Object readLocation()
    throws IOException, ClassNotFoundException
  {
    return readObject();
  }
  
  void skipDefaultResolveClass()
  {
    skipDefaultResolveClass = true;
  }
  
  void useCodebaseOnly()
  {
    useCodebaseOnly = true;
  }
  
  synchronized void setStreamChecker(StreamChecker paramStreamChecker)
  {
    streamChecker = paramStreamChecker;
    SharedSecrets.getJavaObjectInputStreamAccess().setValidator(this, paramStreamChecker);
  }
  
  protected ObjectStreamClass readClassDescriptor()
    throws IOException, ClassNotFoundException
  {
    ObjectStreamClass localObjectStreamClass = super.readClassDescriptor();
    validateDesc(localObjectStreamClass);
    return localObjectStreamClass;
  }
  
  private void validateDesc(ObjectStreamClass paramObjectStreamClass)
  {
    StreamChecker localStreamChecker;
    synchronized (this)
    {
      localStreamChecker = streamChecker;
    }
    if (localStreamChecker != null) {
      localStreamChecker.validateDescriptor(paramObjectStreamClass);
    }
  }
  
  static
  {
    try
    {
      String str1 = "sun.rmi.server.Activation$ActivationSystemImpl_Stub";
      String str2 = "sun.rmi.registry.RegistryImpl_Stub";
      permittedSunClasses.put(str1, Class.forName(str1));
      permittedSunClasses.put(str2, Class.forName(str2));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new NoClassDefFoundError("Missing system class: " + localClassNotFoundException.getMessage());
    }
  }
  
  static abstract interface StreamChecker
    extends ObjectStreamClassValidator
  {
    public abstract void checkProxyInterfaceNames(String[] paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\MarshalInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */