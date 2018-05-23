package com.sun.corba.se.impl.orbutil;

import java.util.EmptyStackException;

public class StackImpl
{
  private Object[] data = new Object[3];
  private int top = -1;
  
  public StackImpl() {}
  
  public final boolean empty()
  {
    return top == -1;
  }
  
  public final Object peek()
  {
    if (empty()) {
      throw new EmptyStackException();
    }
    return data[top];
  }
  
  public final Object pop()
  {
    Object localObject = peek();
    data[top] = null;
    top -= 1;
    return localObject;
  }
  
  private void ensure()
  {
    if (top == data.length - 1)
    {
      int i = 2 * data.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(data, 0, arrayOfObject, 0, data.length);
      data = arrayOfObject;
    }
  }
  
  public final Object push(Object paramObject)
  {
    ensure();
    top += 1;
    data[top] = paramObject;
    return paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\StackImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */