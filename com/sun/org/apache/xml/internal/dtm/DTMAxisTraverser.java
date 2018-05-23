package com.sun.org.apache.xml.internal.dtm;

public abstract class DTMAxisTraverser
{
  public DTMAxisTraverser() {}
  
  public int first(int paramInt)
  {
    return next(paramInt, paramInt);
  }
  
  public int first(int paramInt1, int paramInt2)
  {
    return next(paramInt1, paramInt1, paramInt2);
  }
  
  public abstract int next(int paramInt1, int paramInt2);
  
  public abstract int next(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMAxisTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */