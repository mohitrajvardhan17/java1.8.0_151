package com.sun.jmx.snmp;

public class SnmpNull
  extends SnmpValue
{
  private static final long serialVersionUID = 1783782515994279177L;
  static final String name = "Null";
  private int tag = 5;
  
  public SnmpNull()
  {
    tag = 5;
  }
  
  public SnmpNull(String paramString)
  {
    this();
  }
  
  public SnmpNull(int paramInt)
  {
    tag = paramInt;
  }
  
  public int getTag()
  {
    return tag;
  }
  
  public String toString()
  {
    String str = "";
    if (tag != 5) {
      str = str + "[" + tag + "] ";
    }
    str = str + "NULL";
    switch (tag)
    {
    case 128: 
      str = str + " (noSuchObject)";
      break;
    case 129: 
      str = str + " (noSuchInstance)";
      break;
    case 130: 
      str = str + " (endOfMibView)";
    }
    return str;
  }
  
  public SnmpOid toOid()
  {
    throw new IllegalArgumentException();
  }
  
  public final synchronized SnmpValue duplicate()
  {
    return (SnmpValue)clone();
  }
  
  public final synchronized Object clone()
  {
    SnmpNull localSnmpNull = null;
    try
    {
      localSnmpNull = (SnmpNull)super.clone();
      tag = tag;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    return localSnmpNull;
  }
  
  public final String getTypeName()
  {
    return "Null";
  }
  
  public boolean isNoSuchObjectValue()
  {
    return tag == 128;
  }
  
  public boolean isNoSuchInstanceValue()
  {
    return tag == 129;
  }
  
  public boolean isEndOfMibViewValue()
  {
    return tag == 130;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */