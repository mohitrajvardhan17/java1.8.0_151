package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class ClassDefiner
{
  static final Unsafe unsafe = ;
  
  ClassDefiner() {}
  
  static Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return new DelegatingClassLoader(val$parentClassLoader);
      }
    });
    return unsafe.defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, localClassLoader, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\ClassDefiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */