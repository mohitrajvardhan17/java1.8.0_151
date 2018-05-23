package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Stack;

public class ClassStack
  implements Serializable
{
  private Stack stack = new Stack();
  
  public ClassStack() {}
  
  public void push(JavaClass paramJavaClass)
  {
    stack.push(paramJavaClass);
  }
  
  public JavaClass pop()
  {
    return (JavaClass)stack.pop();
  }
  
  public JavaClass top()
  {
    return (JavaClass)stack.peek();
  }
  
  public boolean empty()
  {
    return stack.empty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ClassStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */