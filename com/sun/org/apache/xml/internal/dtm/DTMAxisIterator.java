package com.sun.org.apache.xml.internal.dtm;

public abstract interface DTMAxisIterator
  extends Cloneable
{
  public static final int END = -1;
  
  public abstract int next();
  
  public abstract DTMAxisIterator reset();
  
  public abstract int getLast();
  
  public abstract int getPosition();
  
  public abstract void setMark();
  
  public abstract void gotoMark();
  
  public abstract DTMAxisIterator setStartNode(int paramInt);
  
  public abstract int getStartNode();
  
  public abstract boolean isReverse();
  
  public abstract DTMAxisIterator cloneIterator();
  
  public abstract void setRestartable(boolean paramBoolean);
  
  public abstract int getNodeByPosition(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMAxisIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */