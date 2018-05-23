package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class EmptyIterator
  implements DTMAxisIterator
{
  private static final EmptyIterator INSTANCE = new EmptyIterator();
  
  public static DTMAxisIterator getInstance()
  {
    return INSTANCE;
  }
  
  private EmptyIterator() {}
  
  public final int next()
  {
    return -1;
  }
  
  public final DTMAxisIterator reset()
  {
    return this;
  }
  
  public final int getLast()
  {
    return 0;
  }
  
  public final int getPosition()
  {
    return 1;
  }
  
  public final void setMark() {}
  
  public final void gotoMark() {}
  
  public final DTMAxisIterator setStartNode(int paramInt)
  {
    return this;
  }
  
  public final int getStartNode()
  {
    return -1;
  }
  
  public final boolean isReverse()
  {
    return false;
  }
  
  public final DTMAxisIterator cloneIterator()
  {
    return this;
  }
  
  public final void setRestartable(boolean paramBoolean) {}
  
  public final int getNodeByPosition(int paramInt)
  {
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\EmptyIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */