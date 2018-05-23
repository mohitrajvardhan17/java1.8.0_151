package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract interface XSDecimal
{
  public abstract BigDecimal getBigDecimal();
  
  public abstract BigInteger getBigInteger()
    throws NumberFormatException;
  
  public abstract long getLong()
    throws NumberFormatException;
  
  public abstract int getInt()
    throws NumberFormatException;
  
  public abstract short getShort()
    throws NumberFormatException;
  
  public abstract byte getByte()
    throws NumberFormatException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\XSDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */