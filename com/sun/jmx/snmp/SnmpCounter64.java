package com.sun.jmx.snmp;

public class SnmpCounter64
  extends SnmpValue
{
  private static final long serialVersionUID = 8784850650494679937L;
  static final String name = "Counter64";
  private long value = 0L;
  
  public SnmpCounter64(long paramLong)
    throws IllegalArgumentException
  {
    if ((paramLong < 0L) || (paramLong > Long.MAX_VALUE)) {
      throw new IllegalArgumentException();
    }
    value = paramLong;
  }
  
  public SnmpCounter64(Long paramLong)
    throws IllegalArgumentException
  {
    this(paramLong.longValue());
  }
  
  public long longValue()
  {
    return value;
  }
  
  public Long toLong()
  {
    return new Long(value);
  }
  
  public int intValue()
  {
    return (int)value;
  }
  
  public Integer toInteger()
  {
    return new Integer((int)value);
  }
  
  public String toString()
  {
    return String.valueOf(value);
  }
  
  public SnmpOid toOid()
  {
    return new SnmpOid(value);
  }
  
  public static SnmpOid toOid(long[] paramArrayOfLong, int paramInt)
    throws SnmpStatusException
  {
    try
    {
      return new SnmpOid(paramArrayOfLong[paramInt]);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new SnmpStatusException(2);
    }
  }
  
  public static int nextOid(long[] paramArrayOfLong, int paramInt)
    throws SnmpStatusException
  {
    if (paramInt >= paramArrayOfLong.length) {
      throw new SnmpStatusException(2);
    }
    return paramInt + 1;
  }
  
  public static void appendToOid(SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2)
  {
    if (paramSnmpOid1.getLength() != 1) {
      throw new IllegalArgumentException();
    }
    paramSnmpOid2.append(paramSnmpOid1);
  }
  
  public final synchronized SnmpValue duplicate()
  {
    return (SnmpValue)clone();
  }
  
  public final synchronized Object clone()
  {
    SnmpCounter64 localSnmpCounter64 = null;
    try
    {
      localSnmpCounter64 = (SnmpCounter64)super.clone();
      value = value;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    return localSnmpCounter64;
  }
  
  public final String getTypeName()
  {
    return "Counter64";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpCounter64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */