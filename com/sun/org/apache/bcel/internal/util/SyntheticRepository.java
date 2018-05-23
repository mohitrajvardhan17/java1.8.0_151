package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SyntheticRepository
  implements Repository
{
  private static final String DEFAULT_PATH = ;
  private static HashMap _instances = new HashMap();
  private ClassPath _path = null;
  private HashMap _loadedClasses = new HashMap();
  
  private SyntheticRepository(ClassPath paramClassPath)
  {
    _path = paramClassPath;
  }
  
  public static SyntheticRepository getInstance()
  {
    return getInstance(ClassPath.SYSTEM_CLASS_PATH);
  }
  
  public static SyntheticRepository getInstance(ClassPath paramClassPath)
  {
    SyntheticRepository localSyntheticRepository = (SyntheticRepository)_instances.get(paramClassPath);
    if (localSyntheticRepository == null)
    {
      localSyntheticRepository = new SyntheticRepository(paramClassPath);
      _instances.put(paramClassPath, localSyntheticRepository);
    }
    return localSyntheticRepository;
  }
  
  public void storeClass(JavaClass paramJavaClass)
  {
    _loadedClasses.put(paramJavaClass.getClassName(), paramJavaClass);
    paramJavaClass.setRepository(this);
  }
  
  public void removeClass(JavaClass paramJavaClass)
  {
    _loadedClasses.remove(paramJavaClass.getClassName());
  }
  
  public JavaClass findClass(String paramString)
  {
    return (JavaClass)_loadedClasses.get(paramString);
  }
  
  public JavaClass loadClass(String paramString)
    throws ClassNotFoundException
  {
    if ((paramString == null) || (paramString.equals(""))) {
      throw new IllegalArgumentException("Invalid class name " + paramString);
    }
    paramString = paramString.replace('/', '.');
    try
    {
      return loadClass(_path.getInputStream(paramString), paramString);
    }
    catch (IOException localIOException)
    {
      throw new ClassNotFoundException("Exception while looking for class " + paramString + ": " + localIOException.toString());
    }
  }
  
  public JavaClass loadClass(Class paramClass)
    throws ClassNotFoundException
  {
    String str1 = paramClass.getName();
    String str2 = str1;
    int i = str2.lastIndexOf('.');
    if (i > 0) {
      str2 = str2.substring(i + 1);
    }
    return loadClass(paramClass.getResourceAsStream(str2 + ".class"), str1);
  }
  
  private JavaClass loadClass(InputStream paramInputStream, String paramString)
    throws ClassNotFoundException
  {
    JavaClass localJavaClass = findClass(paramString);
    if (localJavaClass != null) {
      return localJavaClass;
    }
    try
    {
      if (paramInputStream != null)
      {
        ClassParser localClassParser = new ClassParser(paramInputStream, paramString);
        localJavaClass = localClassParser.parse();
        storeClass(localJavaClass);
        return localJavaClass;
      }
    }
    catch (IOException localIOException)
    {
      throw new ClassNotFoundException("Exception while looking for class " + paramString + ": " + localIOException.toString());
    }
    throw new ClassNotFoundException("SyntheticRepository could not load " + paramString);
  }
  
  public void clear()
  {
    _loadedClasses.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\SyntheticRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */