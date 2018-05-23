package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ClassSet
  implements Serializable
{
  private HashMap _map = new HashMap();
  
  public ClassSet() {}
  
  public boolean add(JavaClass paramJavaClass)
  {
    boolean bool = false;
    if (!_map.containsKey(paramJavaClass.getClassName()))
    {
      bool = true;
      _map.put(paramJavaClass.getClassName(), paramJavaClass);
    }
    return bool;
  }
  
  public void remove(JavaClass paramJavaClass)
  {
    _map.remove(paramJavaClass.getClassName());
  }
  
  public boolean empty()
  {
    return _map.isEmpty();
  }
  
  public JavaClass[] toArray()
  {
    Collection localCollection = _map.values();
    JavaClass[] arrayOfJavaClass = new JavaClass[localCollection.size()];
    localCollection.toArray(arrayOfJavaClass);
    return arrayOfJavaClass;
  }
  
  public String[] getClassNames()
  {
    return (String[])_map.keySet().toArray(new String[_map.keySet().size()]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ClassSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */