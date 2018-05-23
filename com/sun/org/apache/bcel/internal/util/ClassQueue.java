package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.LinkedList;

public class ClassQueue
  implements Serializable
{
  protected LinkedList vec = new LinkedList();
  
  public ClassQueue() {}
  
  public void enqueue(JavaClass paramJavaClass)
  {
    vec.addLast(paramJavaClass);
  }
  
  public JavaClass dequeue()
  {
    return (JavaClass)vec.removeFirst();
  }
  
  public boolean empty()
  {
    return vec.isEmpty();
  }
  
  public String toString()
  {
    return vec.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ClassQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */