package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class AccessorInjector
{
  private static final Logger logger = ;
  protected static final boolean noOptimize = Util.getSystemProperty(ClassTailor.class.getName() + ".noOptimize") != null;
  private static final ClassLoader CLASS_LOADER = SecureLoader.getClassClassLoader(AccessorInjector.class);
  
  AccessorInjector() {}
  
  public static Class<?> prepare(Class paramClass, String paramString1, String paramString2, String... paramVarArgs)
  {
    if (noOptimize) {
      return null;
    }
    try
    {
      ClassLoader localClassLoader = SecureLoader.getClassClassLoader(paramClass);
      if (localClassLoader == null) {
        return null;
      }
      Class localClass = Injector.find(localClassLoader, paramString2);
      if (localClass == null)
      {
        byte[] arrayOfByte = tailor(paramString1, paramString2, paramVarArgs);
        if (arrayOfByte == null) {
          return null;
        }
        localClass = Injector.inject(localClassLoader, paramString2, arrayOfByte);
        if (localClass == null) {
          Injector.find(localClassLoader, paramString2);
        }
      }
      return localClass;
    }
    catch (SecurityException localSecurityException)
    {
      logger.log(Level.INFO, "Unable to create an optimized TransducedAccessor ", localSecurityException);
    }
    return null;
  }
  
  private static byte[] tailor(String paramString1, String paramString2, String... paramVarArgs)
  {
    InputStream localInputStream;
    if (CLASS_LOADER != null) {
      localInputStream = CLASS_LOADER.getResourceAsStream(paramString1 + ".class");
    } else {
      localInputStream = ClassLoader.getSystemResourceAsStream(paramString1 + ".class");
    }
    if (localInputStream == null) {
      return null;
    }
    return ClassTailor.tailor(localInputStream, paramString1, paramString2, paramVarArgs);
  }
  
  static
  {
    if (noOptimize) {
      logger.info("The optimized code generation is disabled");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\AccessorInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */