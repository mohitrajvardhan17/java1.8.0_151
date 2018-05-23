package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.ArrayList;

public class ClassVector
  implements Serializable
{
  protected ArrayList vec = new ArrayList();
  
  public ClassVector() {}
  
  public void addElement(JavaClass paramJavaClass)
  {
    vec.add(paramJavaClass);
  }
  
  public JavaClass elementAt(int paramInt)
  {
    return (JavaClass)vec.get(paramInt);
  }
  
  public void removeElementAt(int paramInt)
  {
    vec.remove(paramInt);
  }
  
  public JavaClass[] toArray()
  {
    JavaClass[] arrayOfJavaClass = new JavaClass[vec.size()];
    vec.toArray(arrayOfJavaClass);
    return arrayOfJavaClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ClassVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */