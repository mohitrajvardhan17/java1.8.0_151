package com.sun.jmx.snmp;

public class SnmpStringFixed
  extends SnmpString
{
  private static final long serialVersionUID = -9120939046874646063L;
  
  public SnmpStringFixed(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  public SnmpStringFixed(Byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  public SnmpStringFixed(String paramString)
  {
    super(paramString);
  }
  
  public SnmpStringFixed(int paramInt, byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    if ((paramInt <= 0) || (paramArrayOfByte == null)) {
      throw new IllegalArgumentException();
    }
    int i = Math.min(paramInt, paramArrayOfByte.length);
    value = new byte[paramInt];
    for (int j = 0; j < i; j++) {
      value[j] = paramArrayOfByte[j];
    }
    for (j = i; j < paramInt; j++) {
      value[j] = 0;
    }
  }
  
  public SnmpStringFixed(int paramInt, Byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    if ((paramInt <= 0) || (paramArrayOfByte == null)) {
      throw new IllegalArgumentException();
    }
    int i = Math.min(paramInt, paramArrayOfByte.length);
    value = new byte[paramInt];
    for (int j = 0; j < i; j++) {
      value[j] = paramArrayOfByte[j].byteValue();
    }
    for (j = i; j < paramInt; j++) {
      value[j] = 0;
    }
  }
  
  public SnmpStringFixed(int paramInt, String paramString)
    throws IllegalArgumentException
  {
    if ((paramInt <= 0) || (paramString == null)) {
      throw new IllegalArgumentException();
    }
    byte[] arrayOfByte = paramString.getBytes();
    int i = Math.min(paramInt, arrayOfByte.length);
    value = new byte[paramInt];
    for (int j = 0; j < i; j++) {
      value[j] = arrayOfByte[j];
    }
    for (j = i; j < paramInt; j++) {
      value[j] = 0;
    }
  }
  
  public static SnmpOid toOid(int paramInt1, long[] paramArrayOfLong, int paramInt2)
    throws SnmpStatusException
  {
    try
    {
      long[] arrayOfLong = new long[paramInt1];
      for (int i = 0; i < paramInt1; i++) {
        arrayOfLong[i] = paramArrayOfLong[(paramInt2 + i)];
      }
      return new SnmpOid(arrayOfLong);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new SnmpStatusException(2);
    }
  }
  
  public static int nextOid(int paramInt1, long[] paramArrayOfLong, int paramInt2)
    throws SnmpStatusException
  {
    int i = paramInt2 + paramInt1;
    if (i > paramArrayOfLong.length) {
      throw new SnmpStatusException(2);
    }
    return i;
  }
  
  public static void appendToOid(int paramInt, SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2)
  {
    paramSnmpOid2.append(paramSnmpOid1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpStringFixed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */