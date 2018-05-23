package java.security;

import java.nio.ByteBuffer;
import java.util.HashMap;
import sun.security.util.Debug;

public class SecureClassLoader
  extends ClassLoader
{
  private final boolean initialized;
  private final HashMap<CodeSource, ProtectionDomain> pdcache = new HashMap(11);
  private static final Debug debug = Debug.getInstance("scl");
  
  protected SecureClassLoader(ClassLoader paramClassLoader)
  {
    super(paramClassLoader);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    initialized = true;
  }
  
  protected SecureClassLoader()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkCreateClassLoader();
    }
    initialized = true;
  }
  
  protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, CodeSource paramCodeSource)
  {
    return defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, getProtectionDomain(paramCodeSource));
  }
  
  protected final Class<?> defineClass(String paramString, ByteBuffer paramByteBuffer, CodeSource paramCodeSource)
  {
    return defineClass(paramString, paramByteBuffer, getProtectionDomain(paramCodeSource));
  }
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    check();
    return new Permissions();
  }
  
  private ProtectionDomain getProtectionDomain(CodeSource paramCodeSource)
  {
    if (paramCodeSource == null) {
      return null;
    }
    ProtectionDomain localProtectionDomain = null;
    synchronized (pdcache)
    {
      localProtectionDomain = (ProtectionDomain)pdcache.get(paramCodeSource);
      if (localProtectionDomain == null)
      {
        PermissionCollection localPermissionCollection = getPermissions(paramCodeSource);
        localProtectionDomain = new ProtectionDomain(paramCodeSource, localPermissionCollection, this, null);
        pdcache.put(paramCodeSource, localProtectionDomain);
        if (debug != null)
        {
          debug.println(" getPermissions " + localProtectionDomain);
          debug.println("");
        }
      }
    }
    return localProtectionDomain;
  }
  
  private void check()
  {
    if (!initialized) {
      throw new SecurityException("ClassLoader object not initialized");
    }
  }
  
  static
  {
    ClassLoader.registerAsParallelCapable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\SecureClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */