package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpDefinitions;

public class SnmpTools
  implements SnmpDefinitions
{
  public SnmpTools() {}
  
  public static String binary2ascii(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = paramInt * 2 + 2;
    byte[] arrayOfByte = new byte[i];
    arrayOfByte[0] = 48;
    arrayOfByte[1] = 120;
    for (int j = 0; j < paramInt; j++)
    {
      int k = j * 2;
      int m = paramArrayOfByte[j] & 0xF0;
      m >>= 4;
      if (m < 10) {
        arrayOfByte[(k + 2)] = ((byte)(48 + m));
      } else {
        arrayOfByte[(k + 2)] = ((byte)(65 + (m - 10)));
      }
      m = paramArrayOfByte[j] & 0xF;
      if (m < 10) {
        arrayOfByte[(k + 1 + 2)] = ((byte)(48 + m));
      } else {
        arrayOfByte[(k + 1 + 2)] = ((byte)(65 + (m - 10)));
      }
    }
    return new String(arrayOfByte);
  }
  
  public static String binary2ascii(byte[] paramArrayOfByte)
  {
    return binary2ascii(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public static byte[] ascii2binary(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    String str = paramString.substring(2);
    int i = str.length();
    byte[] arrayOfByte1 = new byte[i / 2];
    byte[] arrayOfByte2 = str.getBytes();
    for (int j = 0; j < i / 2; j++)
    {
      int k = j * 2;
      int m = 0;
      if ((arrayOfByte2[k] >= 48) && (arrayOfByte2[k] <= 57)) {
        m = (byte)(arrayOfByte2[k] - 48 << 4);
      } else if ((arrayOfByte2[k] >= 97) && (arrayOfByte2[k] <= 102)) {
        m = (byte)(arrayOfByte2[k] - 97 + 10 << 4);
      } else if ((arrayOfByte2[k] >= 65) && (arrayOfByte2[k] <= 70)) {
        m = (byte)(arrayOfByte2[k] - 65 + 10 << 4);
      } else {
        throw new Error("BAD format :" + paramString);
      }
      if ((arrayOfByte2[(k + 1)] >= 48) && (arrayOfByte2[(k + 1)] <= 57)) {
        m = (byte)(m + (arrayOfByte2[(k + 1)] - 48));
      } else if ((arrayOfByte2[(k + 1)] >= 97) && (arrayOfByte2[(k + 1)] <= 102)) {
        m = (byte)(m + (arrayOfByte2[(k + 1)] - 97 + 10));
      } else if ((arrayOfByte2[(k + 1)] >= 65) && (arrayOfByte2[(k + 1)] <= 70)) {
        m = (byte)(m + (arrayOfByte2[(k + 1)] - 65 + 10));
      } else {
        throw new Error("BAD format :" + paramString);
      }
      arrayOfByte1[j] = m;
    }
    return arrayOfByte1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpTools.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */