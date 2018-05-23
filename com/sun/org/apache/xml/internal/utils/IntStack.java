package com.sun.org.apache.xml.internal.utils;

import java.util.EmptyStackException;

public class IntStack
  extends IntVector
{
  public IntStack() {}
  
  public IntStack(int paramInt)
  {
    super(paramInt);
  }
  
  public IntStack(IntStack paramIntStack)
  {
    super(paramIntStack);
  }
  
  public int push(int paramInt)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + 1);
      m_map = arrayOfInt;
    }
    m_map[m_firstFree] = paramInt;
    m_firstFree += 1;
    return paramInt;
  }
  
  public final int pop()
  {
    return m_map[(--m_firstFree)];
  }
  
  public final void quickPop(int paramInt)
  {
    m_firstFree -= paramInt;
  }
  
  public final int peek()
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
  
  public int peek(int paramInt)
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
  
  public void setTop(int paramInt)
  {
    try
    {
      m_map[(m_firstFree - 1)] = paramInt;
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
  
  public int search(int paramInt)
  {
    int i = lastIndexOf(paramInt);
    if (i >= 0) {
      return size() - i;
    }
    return -1;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return (IntStack)super.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\IntStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */