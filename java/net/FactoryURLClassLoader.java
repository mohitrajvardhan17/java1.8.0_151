package java.net;

import java.security.AccessControlContext;

final class FactoryURLClassLoader
  extends URLClassLoader
{
  FactoryURLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader, AccessControlContext paramAccessControlContext)
  {
    super(paramArrayOfURL, paramClassLoader, paramAccessControlContext);
  }
  
  FactoryURLClassLoader(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext)
  {
    super(paramArrayOfURL, paramAccessControlContext);
  }
  
  public final Class<?> loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      int i = paramString.lastIndexOf('.');
      if (i != -1) {
        localSecurityManager.checkPackageAccess(paramString.substring(0, i));
      }
    }
    return super.loadClass(paramString, paramBoolean);
  }
  
  static
  {
    ClassLoader.registerAsParallelCapable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\FactoryURLClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */