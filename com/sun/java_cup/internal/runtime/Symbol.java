package com.sun.java_cup.internal.runtime;

public class Symbol
{
  public int sym;
  public int parse_state;
  boolean used_by_parser = false;
  public int left;
  public int right;
  public Object value;
  
  public Symbol(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    this(paramInt1);
    left = paramInt2;
    right = paramInt3;
    value = paramObject;
  }
  
  public Symbol(int paramInt, Object paramObject)
  {
    this(paramInt);
    left = -1;
    right = -1;
    value = paramObject;
  }
  
  public Symbol(int paramInt1, int paramInt2, int paramInt3)
  {
    sym = paramInt1;
    left = paramInt2;
    right = paramInt3;
    value = null;
  }
  
  public Symbol(int paramInt)
  {
    this(paramInt, -1);
    left = -1;
    right = -1;
    value = null;
  }
  
  public Symbol(int paramInt1, int paramInt2)
  {
    sym = paramInt1;
    parse_state = paramInt2;
  }
  
  public String toString()
  {
    return "#" + sym;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java_cup\internal\runtime\Symbol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */