package com.sun.jmx.snmp;

public abstract class SnmpUnsignedInt
  extends SnmpInt
{
  public static final long MAX_VALUE = 4294967295L;
  static final String name = "Unsigned32";
  
  public SnmpUnsignedInt(int paramInt)
    throws IllegalArgumentException
  {
    super(paramInt);
  }
  
  public SnmpUnsignedInt(Integer paramInteger)
    throws IllegalArgumentException
  {
    super(paramInteger);
  }
  
  public SnmpUnsignedInt(long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong);
  }
  
  public SnmpUnsignedInt(Long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong);
  }
  
  public String getTypeName()
  {
    return "Unsigned32";
  }
  
  boolean isInitValueValid(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= 4294967295L);
  }
  
  boolean isInitValueValid(long paramLong)
  {
    return (paramLong >= 0L) && (paramLong <= 4294967295L);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpUnsignedInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */