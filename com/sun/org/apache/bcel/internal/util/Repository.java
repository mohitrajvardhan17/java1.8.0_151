package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;

public abstract interface Repository
  extends Serializable
{
  public abstract void storeClass(JavaClass paramJavaClass);
  
  public abstract void removeClass(JavaClass paramJavaClass);
  
  public abstract JavaClass findClass(String paramString);
  
  public abstract JavaClass loadClass(String paramString)
    throws ClassNotFoundException;
  
  public abstract JavaClass loadClass(Class paramClass)
    throws ClassNotFoundException;
  
  public abstract void clear();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\Repository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */