package com.sun.jmx.snmp;

public class SnmpGauge
  extends SnmpUnsignedInt
{
  private static final long serialVersionUID = -8366622742122792945L;
  static final String name = "Gauge32";
  
  public SnmpGauge(int paramInt)
    throws IllegalArgumentException
  {
    super(paramInt);
  }
  
  public SnmpGauge(Integer paramInteger)
    throws IllegalArgumentException
  {
    super(paramInteger);
  }
  
  public SnmpGauge(long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong);
  }
  
  public SnmpGauge(Long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong);
  }
  
  public final String getTypeName()
  {
    return "Gauge32";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpGauge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */