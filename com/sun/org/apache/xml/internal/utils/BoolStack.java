package com.sun.org.apache.xml.internal.utils;

public final class BoolStack
  implements Cloneable
{
  private boolean[] m_values;
  private int m_allocatedSize;
  private int m_index;
  
  public BoolStack()
  {
    this(32);
  }
  
  public BoolStack(int paramInt)
  {
    m_allocatedSize = paramInt;
    m_values = new boolean[paramInt];
    m_index = -1;
  }
  
  public final int size()
  {
    return m_index + 1;
  }
  
  public final void clear()
  {
    m_index = -1;
  }
  
  public final boolean push(boolean paramBoolean)
  {
    if (m_index == m_allocatedSize - 1) {
      grow();
    }
    return m_values[(++m_index)] = paramBoolean;
  }
  
  public final boolean pop()
  {
    return m_values[(m_index--)];
  }
  
  public final boolean popAndTop()
  {
    m_index -= 1;
    return m_index >= 0 ? m_values[m_index] : false;
  }
  
  public final void setTop(boolean paramBoolean)
  {
    m_values[m_index] = paramBoolean;
  }
  
  public final boolean peek()
  {
    return m_values[m_index];
  }
  
  public final boolean peekOrFalse()
  {
    return m_index > -1 ? m_values[m_index] : false;
  }
  
  public final boolean peekOrTrue()
  {
    return m_index > -1 ? m_values[m_index] : true;
  }
  
  public boolean isEmpty()
  {
    return m_index == -1;
  }
  
  private void grow()
  {
    m_allocatedSize *= 2;
    boolean[] arrayOfBoolean = new boolean[m_allocatedSize];
    System.arraycopy(m_values, 0, arrayOfBoolean, 0, m_index + 1);
    m_values = arrayOfBoolean;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\BoolStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */