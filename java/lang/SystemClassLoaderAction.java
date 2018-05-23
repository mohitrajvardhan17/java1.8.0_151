package java.lang;

import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;

class SystemClassLoaderAction
  implements PrivilegedExceptionAction<ClassLoader>
{
  private ClassLoader parent;
  
  SystemClassLoaderAction(ClassLoader paramClassLoader)
  {
    parent = paramClassLoader;
  }
  
  public ClassLoader run()
    throws Exception
  {
    String str = System.getProperty("java.system.class.loader");
    if (str == null) {
      return parent;
    }
    Constructor localConstructor = Class.forName(str, true, parent).getDeclaredConstructor(new Class[] { ClassLoader.class });
    ClassLoader localClassLoader = (ClassLoader)localConstructor.newInstance(new Object[] { parent });
    Thread.currentThread().setContextClassLoader(localClassLoader);
    return localClassLoader;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\SystemClassLoaderAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */