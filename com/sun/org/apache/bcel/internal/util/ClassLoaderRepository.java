package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ClassLoaderRepository
  implements Repository
{
  private ClassLoader loader;
  private HashMap loadedClasses = new HashMap();
  
  public ClassLoaderRepository(ClassLoader paramClassLoader)
  {
    loader = paramClassLoader;
  }
  
  public void storeClass(JavaClass paramJavaClass)
  {
    loadedClasses.put(paramJavaClass.getClassName(), paramJavaClass);
    paramJavaClass.setRepository(this);
  }
  
  public void removeClass(JavaClass paramJavaClass)
  {
    loadedClasses.remove(paramJavaClass.getClassName());
  }
  
  public JavaClass findClass(String paramString)
  {
    if (loadedClasses.containsKey(paramString)) {
      return (JavaClass)loadedClasses.get(paramString);
    }
    return null;
  }
  
  public JavaClass loadClass(String paramString)
    throws ClassNotFoundException
  {
    String str = paramString.replace('.', '/');
    JavaClass localJavaClass = findClass(paramString);
    if (localJavaClass != null) {
      return localJavaClass;
    }
    try
    {
      InputStream localInputStream = loader.getResourceAsStream(str + ".class");
      if (localInputStream == null) {
        throw new ClassNotFoundException(paramString + " not found.");
      }
      ClassParser localClassParser = new ClassParser(localInputStream, paramString);
      localJavaClass = localClassParser.parse();
      storeClass(localJavaClass);
      return localJavaClass;
    }
    catch (IOException localIOException)
    {
      throw new ClassNotFoundException(localIOException.toString());
    }
  }
  
  public JavaClass loadClass(Class paramClass)
    throws ClassNotFoundException
  {
    return loadClass(paramClass.getName());
  }
  
  public void clear()
  {
    loadedClasses.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */