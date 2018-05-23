package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class Enumerated
  implements Serializable
{
  protected int value;
  
  public Enumerated()
    throws IllegalArgumentException
  {
    Enumeration localEnumeration = getIntTable().keys();
    if (localEnumeration.hasMoreElements()) {
      value = ((Integer)localEnumeration.nextElement()).intValue();
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  public Enumerated(int paramInt)
    throws IllegalArgumentException
  {
    if (getIntTable().get(new Integer(paramInt)) == null) {
      throw new IllegalArgumentException();
    }
    value = paramInt;
  }
  
  public Enumerated(Integer paramInteger)
    throws IllegalArgumentException
  {
    if (getIntTable().get(paramInteger) == null) {
      throw new IllegalArgumentException();
    }
    value = paramInteger.intValue();
  }
  
  public Enumerated(String paramString)
    throws IllegalArgumentException
  {
    Integer localInteger = (Integer)getStringTable().get(paramString);
    if (localInteger == null) {
      throw new IllegalArgumentException();
    }
    value = localInteger.intValue();
  }
  
  public int intValue()
  {
    return value;
  }
  
  public Enumeration<Integer> valueIndexes()
  {
    return getIntTable().keys();
  }
  
  public Enumeration<String> valueStrings()
  {
    return getStringTable().keys();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && (getClass() == paramObject.getClass()) && (value == value);
  }
  
  public int hashCode()
  {
    String str = getClass().getName() + String.valueOf(value);
    return str.hashCode();
  }
  
  public String toString()
  {
    return (String)getIntTable().get(new Integer(value));
  }
  
  protected abstract Hashtable<Integer, String> getIntTable();
  
  protected abstract Hashtable<String, Integer> getStringTable();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\Enumerated.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */