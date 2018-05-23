package com.sun.org.apache.xml.internal.utils;

import java.util.EmptyStackException;

public class ObjectStack
  extends ObjectVector
{
  public ObjectStack() {}
  
  public ObjectStack(int paramInt)
  {
    super(paramInt);
  }
  
  public ObjectStack(ObjectStack paramObjectStack)
  {
    super(paramObjectStack);
  }
  
  public Object push(Object paramObject)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      Object[] arrayOfObject = new Object[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfObject, 0, m_firstFree + 1);
      m_map = arrayOfObject;
    }
    m_map[m_firstFree] = paramObject;
    m_firstFree += 1;
    return paramObject;
  }
  
  public Object pop()
  {
    Object localObject = m_map[(--m_firstFree)];
    m_map[m_firstFree] = null;
    return localObject;
  }
  
  public void quickPop(int paramInt)
  {
    m_firstFree -= paramInt;
  }
  
  public Object peek()
  {
    try
    {
      return m_map[(m_firstFree - 1)];
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new EmptyStackException();
    }
  }
  
  public Object peek(int paramInt)
  {
    try
    {
      return m_map[(m_firstFree - (1 + paramInt))];
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new EmptyStackException();
    }
  }
  
  public void setTop(Object paramObject)
  {
    try
    {
      m_map[(m_firstFree - 1)] = paramObject;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new EmptyStackException();
    }
  }
  
  public boolean empty()
  {
    return m_firstFree == 0;
  }
  
  public int search(Object paramObject)
  {
    int i = lastIndexOf(paramObject);
    if (i >= 0) {
      return size() - i;
    }
    return -1;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return (ObjectStack)super.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\ObjectStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */