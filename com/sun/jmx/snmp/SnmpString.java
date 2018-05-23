package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SnmpString
  extends SnmpValue
{
  private static final long serialVersionUID = -7011986973225194188L;
  static final String name = "String";
  protected byte[] value = null;
  
  public SnmpString()
  {
    value = new byte[0];
  }
  
  public SnmpString(byte[] paramArrayOfByte)
  {
    value = ((byte[])paramArrayOfByte.clone());
  }
  
  public SnmpString(Byte[] paramArrayOfByte)
  {
    value = new byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      value[i] = paramArrayOfByte[i].byteValue();
    }
  }
  
  public SnmpString(String paramString)
  {
    value = paramString.getBytes();
  }
  
  public SnmpString(InetAddress paramInetAddress)
  {
    value = paramInetAddress.getAddress();
  }
  
  public InetAddress inetAddressValue()
    throws UnknownHostException
  {
    return InetAddress.getByAddress(value);
  }
  
  public static String BinToChar(String paramString)
  {
    char[] arrayOfChar = new char[paramString.length() / 8];
    int i = arrayOfChar.length;
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = ((char)Integer.parseInt(paramString.substring(8 * j, 8 * j + 8), 2));
    }
    return new String(arrayOfChar);
  }
  
  public static String HexToChar(String paramString)
  {
    char[] arrayOfChar = new char[paramString.length() / 2];
    int i = arrayOfChar.length;
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = ((char)Integer.parseInt(paramString.substring(2 * j, 2 * j + 2), 16));
    }
    return new String(arrayOfChar);
  }
  
  public byte[] byteValue()
  {
    return (byte[])value.clone();
  }
  
  public Byte[] toByte()
  {
    Byte[] arrayOfByte = new Byte[value.length];
    for (int i = 0; i < value.length; i++) {
      arrayOfByte[i] = new Byte(value[i]);
    }
    return arrayOfByte;
  }
  
  public String toString()
  {
    return new String(value);
  }
  
  public SnmpOid toOid()
  {
    long[] arrayOfLong = new long[value.length];
    for (int i = 0; i < value.length; i++) {
      arrayOfLong[i] = (value[i] & 0xFF);
    }
    return new SnmpOid(arrayOfLong);
  }
  
  public static SnmpOid toOid(long[] paramArrayOfLong, int paramInt)
    throws SnmpStatusException
  {
    try
    {
      if (paramArrayOfLong[paramInt] > 2147483647L) {
        throw new SnmpStatusException(2);
      }
      int i = (int)paramArrayOfLong[(paramInt++)];
      long[] arrayOfLong = new long[i];
      for (int j = 0; j < i; j++) {
        arrayOfLong[j] = paramArrayOfLong[(paramInt + j)];
      }
      return new SnmpOid(arrayOfLong);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new SnmpStatusException(2);
    }
  }
  
  public static int nextOid(long[] paramArrayOfLong, int paramInt)
    throws SnmpStatusException
  {
    try
    {
      if (paramArrayOfLong[paramInt] > 2147483647L) {
        throw new SnmpStatusException(2);
      }
      int i = (int)paramArrayOfLong[(paramInt++)];
      paramInt += i;
      if (paramInt <= paramArrayOfLong.length) {
        return paramInt;
      }
      throw new SnmpStatusException(2);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new SnmpStatusException(2);
    }
  }
  
  public static void appendToOid(SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2)
  {
    paramSnmpOid2.append(paramSnmpOid1.getLength());
    paramSnmpOid2.append(paramSnmpOid1);
  }
  
  public final synchronized SnmpValue duplicate()
  {
    return (SnmpValue)clone();
  }
  
  public synchronized Object clone()
  {
    SnmpString localSnmpString = null;
    try
    {
      localSnmpString = (SnmpString)super.clone();
      value = new byte[value.length];
      System.arraycopy(value, 0, value, 0, value.length);
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    return localSnmpString;
  }
  
  public String getTypeName()
  {
    return "String";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */