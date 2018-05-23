package com.sun.jmx.snmp;

public class SnmpCounter
  extends SnmpUnsignedInt
{
  private static final long serialVersionUID = 4655264728839396879L;
  static final String name = "Counter32";
  
  public SnmpCounter(int paramInt)
    throws IllegalArgumentException
  {
    super(paramInt);
  }
  
  public SnmpCounter(Integer paramInteger)
    throws IllegalArgumentException
  {
    super(paramInteger);
  }
  
  public SnmpCounter(long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong);
  }
  
  public SnmpCounter(Long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong);
  }
  
  public final String getTypeName()
  {
    return "Counter32";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */