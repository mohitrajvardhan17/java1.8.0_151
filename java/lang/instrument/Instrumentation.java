package java.lang.instrument;

import java.util.jar.JarFile;

public abstract interface Instrumentation
{
  public abstract void addTransformer(ClassFileTransformer paramClassFileTransformer, boolean paramBoolean);
  
  public abstract void addTransformer(ClassFileTransformer paramClassFileTransformer);
  
  public abstract boolean removeTransformer(ClassFileTransformer paramClassFileTransformer);
  
  public abstract boolean isRetransformClassesSupported();
  
  public abstract void retransformClasses(Class<?>... paramVarArgs)
    throws UnmodifiableClassException;
  
  public abstract boolean isRedefineClassesSupported();
  
  public abstract void redefineClasses(ClassDefinition... paramVarArgs)
    throws ClassNotFoundException, UnmodifiableClassException;
  
  public abstract boolean isModifiableClass(Class<?> paramClass);
  
  public abstract Class[] getAllLoadedClasses();
  
  public abstract Class[] getInitiatedClasses(ClassLoader paramClassLoader);
  
  public abstract long getObjectSize(Object paramObject);
  
  public abstract void appendToBootstrapClassLoaderSearch(JarFile paramJarFile);
  
  public abstract void appendToSystemClassLoaderSearch(JarFile paramJarFile);
  
  public abstract boolean isNativeMethodPrefixSupported();
  
  public abstract void setNativeMethodPrefix(ClassFileTransformer paramClassFileTransformer, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\instrument\Instrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */