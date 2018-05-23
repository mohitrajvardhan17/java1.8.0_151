package com.sun.jmx.snmp;

public class SnmpInt
  extends SnmpValue
{
  private static final long serialVersionUID = -7163624758070343373L;
  static final String name = "Integer32";
  protected long value = 0L;
  
  public SnmpInt(int paramInt)
    throws IllegalArgumentException
  {
    if (!isInitValueValid(paramInt)) {
      throw new IllegalArgumentException();
    }
    value = paramInt;
  }
  
  public SnmpInt(Integer paramInteger)
    throws IllegalArgumentException
  {
    this(paramInteger.intValue());
  }
  
  public SnmpInt(long paramLong)
    throws IllegalArgumentException
  {
    if (!isInitValueValid(paramLong)) {
      throw new IllegalArgumentException();
    }
    value = paramLong;
  }
  
  public SnmpInt(Long paramLong)
    throws IllegalArgumentException
  {
    this(paramLong.longValue());
  }
  
  public SnmpInt(Enumerated paramEnumerated)
    throws IllegalArgumentException
  {
    this(paramEnumerated.intValue());
  }
  
  public SnmpInt(boolean paramBoolean)
  {
    value = (paramBoolean ? 1L : 2L);
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
    SnmpInt localSnmpInt = null;
    try
    {
      localSnmpInt = (SnmpInt)super.clone();
      value = value;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    return localSnmpInt;
  }
  
  public String getTypeName()
  {
    return "Integer32";
  }
  
  boolean isInitValueValid(int paramInt)
  {
    return (paramInt >= Integer.MIN_VALUE) && (paramInt <= Integer.MAX_VALUE);
  }
  
  boolean isInitValueValid(long paramLong)
  {
    return (paramLong >= -2147483648L) && (paramLong <= 2147483647L);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */