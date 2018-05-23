package com.sun.jmx.snmp;

public class SnmpTimeticks
  extends SnmpUnsignedInt
{
  static final String name = "TimeTicks";
  private static final long serialVersionUID = -5486435222360030630L;
  
  public SnmpTimeticks(int paramInt)
    throws IllegalArgumentException
  {
    super(paramInt);
  }
  
  public SnmpTimeticks(Integer paramInteger)
    throws IllegalArgumentException
  {
    super(paramInteger);
  }
  
  public SnmpTimeticks(long paramLong)
    throws IllegalArgumentException
  {
    super(paramLong > 0L ? paramLong & 0xFFFFFFFF : paramLong);
  }
  
  public SnmpTimeticks(Long paramLong)
    throws IllegalArgumentException
  {
    this(paramLong.longValue());
  }
  
  public static final String printTimeTicks(long paramLong)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    paramLong /= 100L;
    int m = (int)(paramLong / 86400L);
    paramLong %= 86400L;
    int k = (int)(paramLong / 3600L);
    paramLong %= 3600L;
    int j = (int)(paramLong / 60L);
    int i = (int)(paramLong % 60L);
    if (m == 0)
    {
      localStringBuffer.append(k + ":" + j + ":" + i);
      return localStringBuffer.toString();
    }
    if (m == 1) {
      localStringBuffer.append("1 day ");
    } else {
      localStringBuffer.append(m + " days ");
    }
    localStringBuffer.append(k + ":" + j + ":" + i);
    return localStringBuffer.toString();
  }
  
  public final String toString()
  {
    return printTimeTicks(value);
  }
  
  public final String getTypeName()
  {
    return "TimeTicks";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpTimeticks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */