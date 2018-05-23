package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;

public class ClassLoader
  extends java.lang.ClassLoader
{
  private Hashtable classes = new Hashtable();
  private String[] ignored_packages = { "java.", "javax.", "sun." };
  private Repository repository = SyntheticRepository.getInstance();
  private java.lang.ClassLoader deferTo = getSystemClassLoader();
  
  public ClassLoader() {}
  
  public ClassLoader(java.lang.ClassLoader paramClassLoader)
  {
    deferTo = paramClassLoader;
    repository = new ClassLoaderRepository(paramClassLoader);
  }
  
  public ClassLoader(String[] paramArrayOfString)
  {
    addIgnoredPkgs(paramArrayOfString);
  }
  
  public ClassLoader(java.lang.ClassLoader paramClassLoader, String[] paramArrayOfString)
  {
    deferTo = paramClassLoader;
    repository = new ClassLoaderRepository(paramClassLoader);
    addIgnoredPkgs(paramArrayOfString);
  }
  
  private void addIgnoredPkgs(String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[paramArrayOfString.length + ignored_packages.length];
    System.arraycopy(ignored_packages, 0, arrayOfString, 0, ignored_packages.length);
    System.arraycopy(paramArrayOfString, 0, arrayOfString, ignored_packages.length, paramArrayOfString.length);
    ignored_packages = arrayOfString;
  }
  
  protected Class loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    Class localClass = null;
    if ((localClass = (Class)classes.get(paramString)) == null)
    {
      for (int i = 0; i < ignored_packages.length; i++) {
        if (paramString.startsWith(ignored_packages[i]))
        {
          localClass = deferTo.loadClass(paramString);
          break;
        }
      }
      if (localClass == null)
      {
        JavaClass localJavaClass = null;
        if (paramString.indexOf("$$BCEL$$") >= 0) {
          localJavaClass = createClass(paramString);
        } else if ((localJavaClass = repository.loadClass(paramString)) != null) {
          localJavaClass = modifyClass(localJavaClass);
        } else {
          throw new ClassNotFoundException(paramString);
        }
        if (localJavaClass != null)
        {
          byte[] arrayOfByte = localJavaClass.getBytes();
          localClass = defineClass(paramString, arrayOfByte, 0, arrayOfByte.length);
        }
        else
        {
          localClass = Class.forName(paramString);
        }
      }
      if (paramBoolean) {
        resolveClass(localClass);
      }
    }
    classes.put(paramString, localClass);
    return localClass;
  }
  
  protected JavaClass modifyClass(JavaClass paramJavaClass)
  {
    return paramJavaClass;
  }
  
  protected JavaClass createClass(String paramString)
  {
    int i = paramString.indexOf("$$BCEL$$");
    String str = paramString.substring(i + 8);
    JavaClass localJavaClass = null;
    try
    {
      byte[] arrayOfByte = Utility.decode(str, true);
      localObject = new ClassParser(new ByteArrayInputStream(arrayOfByte), "foo");
      localJavaClass = ((ClassParser)localObject).parse();
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      return null;
    }
    ConstantPool localConstantPool = localJavaClass.getConstantPool();
    Object localObject = (ConstantClass)localConstantPool.getConstant(localJavaClass.getClassNameIndex(), (byte)7);
    ConstantUtf8 localConstantUtf8 = (ConstantUtf8)localConstantPool.getConstant(((ConstantClass)localObject).getNameIndex(), (byte)1);
    localConstantUtf8.setBytes(paramString.replace('.', '/'));
    return localJavaClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */