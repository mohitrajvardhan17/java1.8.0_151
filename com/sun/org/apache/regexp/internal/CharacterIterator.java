package com.sun.org.apache.regexp.internal;

public abstract interface CharacterIterator
{
  public abstract String substring(int paramInt1, int paramInt2);
  
  public abstract String substring(int paramInt);
  
  public abstract char charAt(int paramInt);
  
  public abstract boolean isEnd(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\CharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */