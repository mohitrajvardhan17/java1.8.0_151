package com.sun.jndi.dns;

import java.io.UnsupportedEncodingException;
import javax.naming.InvalidNameException;

public class ResourceRecord
{
  static final int TYPE_A = 1;
  static final int TYPE_NS = 2;
  static final int TYPE_CNAME = 5;
  static final int TYPE_SOA = 6;
  static final int TYPE_PTR = 12;
  static final int TYPE_HINFO = 13;
  static final int TYPE_MX = 15;
  static final int TYPE_TXT = 16;
  static final int TYPE_AAAA = 28;
  static final int TYPE_SRV = 33;
  static final int TYPE_NAPTR = 35;
  static final int QTYPE_AXFR = 252;
  static final int QTYPE_STAR = 255;
  static final String[] rrTypeNames = { null, "A", "NS", null, null, "CNAME", "SOA", null, null, null, null, null, "PTR", "HINFO", null, "MX", "TXT", null, null, null, null, null, null, null, null, null, null, null, "AAAA", null, null, null, null, "SRV", null, "NAPTR" };
  static final int CLASS_INTERNET = 1;
  static final int CLASS_HESIOD = 2;
  static final int QCLASS_STAR = 255;
  static final String[] rrClassNames = { null, "IN", null, null, "HS" };
  byte[] msg;
  int msgLen;
  boolean qSection;
  int offset;
  int rrlen;
  DnsName name;
  int rrtype;
  String rrtypeName;
  int rrclass;
  String rrclassName;
  int ttl = 0;
  int rdlen = 0;
  Object rdata = null;
  
  ResourceRecord(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws InvalidNameException
  {
    msg = paramArrayOfByte;
    msgLen = paramInt1;
    offset = paramInt2;
    qSection = paramBoolean1;
    decode(paramBoolean2);
  }
  
  public String toString()
  {
    String str = name + " " + rrclassName + " " + rrtypeName;
    if (!qSection) {
      str = str + " " + ttl + " " + (rdata != null ? rdata : "[n/a]");
    }
    return str;
  }
  
  public DnsName getName()
  {
    return name;
  }
  
  public int size()
  {
    return rrlen;
  }
  
  public int getType()
  {
    return rrtype;
  }
  
  public int getRrclass()
  {
    return rrclass;
  }
  
  public Object getRdata()
  {
    return rdata;
  }
  
  public static String getTypeName(int paramInt)
  {
    return valueToName(paramInt, rrTypeNames);
  }
  
  public static int getType(String paramString)
  {
    return nameToValue(paramString, rrTypeNames);
  }
  
  public static String getRrclassName(int paramInt)
  {
    return valueToName(paramInt, rrClassNames);
  }
  
  public static int getRrclass(String paramString)
  {
    return nameToValue(paramString, rrClassNames);
  }
  
  private static String valueToName(int paramInt, String[] paramArrayOfString)
  {
    String str = null;
    if ((paramInt > 0) && (paramInt < paramArrayOfString.length)) {
      str = paramArrayOfString[paramInt];
    } else if (paramInt == 255) {
      str = "*";
    }
    if (str == null) {
      str = Integer.toString(paramInt);
    }
    return str;
  }
  
  private static int nameToValue(String paramString, String[] paramArrayOfString)
  {
    if (paramString.equals("")) {
      return -1;
    }
    if (paramString.equals("*")) {
      return 255;
    }
    if (Character.isDigit(paramString.charAt(0))) {
      try
      {
        return Integer.parseInt(paramString);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    for (int i = 1; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i] != null) && (paramString.equalsIgnoreCase(paramArrayOfString[i]))) {
        return i;
      }
    }
    return -1;
  }
  
  public static int compareSerialNumbers(long paramLong1, long paramLong2)
  {
    long l = paramLong2 - paramLong1;
    if (l == 0L) {
      return 0;
    }
    if (((l > 0L) && (l <= 2147483647L)) || ((l < 0L) && (-l > 2147483647L))) {
      return -1;
    }
    return 1;
  }
  
  private void decode(boolean paramBoolean)
    throws InvalidNameException
  {
    int i = offset;
    name = new DnsName();
    i = decodeName(i, name);
    rrtype = getUShort(i);
    rrtypeName = (rrtype < rrTypeNames.length ? rrTypeNames[rrtype] : null);
    if (rrtypeName == null) {
      rrtypeName = Integer.toString(rrtype);
    }
    i += 2;
    rrclass = getUShort(i);
    rrclassName = (rrclass < rrClassNames.length ? rrClassNames[rrclass] : null);
    if (rrclassName == null) {
      rrclassName = Integer.toString(rrclass);
    }
    i += 2;
    if (!qSection)
    {
      ttl = getInt(i);
      i += 4;
      rdlen = getUShort(i);
      i += 2;
      rdata = ((paramBoolean) || (rrtype == 6) ? decodeRdata(i) : null);
      if ((rdata instanceof DnsName)) {
        rdata = rdata.toString();
      }
      i += rdlen;
    }
    rrlen = (i - offset);
    msg = null;
  }
  
  private int getUByte(int paramInt)
  {
    return msg[paramInt] & 0xFF;
  }
  
  private int getUShort(int paramInt)
  {
    return (msg[paramInt] & 0xFF) << 8 | msg[(paramInt + 1)] & 0xFF;
  }
  
  private int getInt(int paramInt)
  {
    return getUShort(paramInt) << 16 | getUShort(paramInt + 2);
  }
  
  private long getUInt(int paramInt)
  {
    return getInt(paramInt) & 0xFFFFFFFF;
  }
  
  private DnsName decodeName(int paramInt)
    throws InvalidNameException
  {
    DnsName localDnsName = new DnsName();
    decodeName(paramInt, localDnsName);
    return localDnsName;
  }
  
  private int decodeName(int paramInt, DnsName paramDnsName)
    throws InvalidNameException
  {
    if (msg[paramInt] == 0)
    {
      paramDnsName.add(0, "");
      return paramInt + 1;
    }
    if ((msg[paramInt] & 0xC0) != 0)
    {
      decodeName(getUShort(paramInt) & 0x3FFF, paramDnsName);
      return paramInt + 2;
    }
    int i = msg[(paramInt++)];
    try
    {
      paramDnsName.add(0, new String(msg, paramInt, i, "ISO-8859-1"));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return decodeName(paramInt + i, paramDnsName);
  }
  
  private Object decodeRdata(int paramInt)
    throws InvalidNameException
  {
    if (rrclass == 1) {
      switch (rrtype)
      {
      case 1: 
        return decodeA(paramInt);
      case 28: 
        return decodeAAAA(paramInt);
      case 2: 
      case 5: 
      case 12: 
        return decodeName(paramInt);
      case 15: 
        return decodeMx(paramInt);
      case 6: 
        return decodeSoa(paramInt);
      case 33: 
        return decodeSrv(paramInt);
      case 35: 
        return decodeNaptr(paramInt);
      case 16: 
        return decodeTxt(paramInt);
      case 13: 
        return decodeHinfo(paramInt);
      }
    }
    byte[] arrayOfByte = new byte[rdlen];
    System.arraycopy(msg, paramInt, arrayOfByte, 0, rdlen);
    return arrayOfByte;
  }
  
  private String decodeMx(int paramInt)
    throws InvalidNameException
  {
    int i = getUShort(paramInt);
    paramInt += 2;
    DnsName localDnsName = decodeName(paramInt);
    return i + " " + localDnsName;
  }
  
  private String decodeSoa(int paramInt)
    throws InvalidNameException
  {
    DnsName localDnsName1 = new DnsName();
    paramInt = decodeName(paramInt, localDnsName1);
    DnsName localDnsName2 = new DnsName();
    paramInt = decodeName(paramInt, localDnsName2);
    long l1 = getUInt(paramInt);
    paramInt += 4;
    long l2 = getUInt(paramInt);
    paramInt += 4;
    long l3 = getUInt(paramInt);
    paramInt += 4;
    long l4 = getUInt(paramInt);
    paramInt += 4;
    long l5 = getUInt(paramInt);
    paramInt += 4;
    return localDnsName1 + " " + localDnsName2 + " " + l1 + " " + l2 + " " + l3 + " " + l4 + " " + l5;
  }
  
  private String decodeSrv(int paramInt)
    throws InvalidNameException
  {
    int i = getUShort(paramInt);
    paramInt += 2;
    int j = getUShort(paramInt);
    paramInt += 2;
    int k = getUShort(paramInt);
    paramInt += 2;
    DnsName localDnsName = decodeName(paramInt);
    return i + " " + j + " " + k + " " + localDnsName;
  }
  
  private String decodeNaptr(int paramInt)
    throws InvalidNameException
  {
    int i = getUShort(paramInt);
    paramInt += 2;
    int j = getUShort(paramInt);
    paramInt += 2;
    StringBuffer localStringBuffer1 = new StringBuffer();
    paramInt += decodeCharString(paramInt, localStringBuffer1);
    StringBuffer localStringBuffer2 = new StringBuffer();
    paramInt += decodeCharString(paramInt, localStringBuffer2);
    StringBuffer localStringBuffer3 = new StringBuffer(rdlen);
    paramInt += decodeCharString(paramInt, localStringBuffer3);
    DnsName localDnsName = decodeName(paramInt);
    return i + " " + j + " " + localStringBuffer1 + " " + localStringBuffer2 + " " + localStringBuffer3 + " " + localDnsName;
  }
  
  private String decodeTxt(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer(rdlen);
    int i = paramInt + rdlen;
    while (paramInt < i)
    {
      paramInt += decodeCharString(paramInt, localStringBuffer);
      if (paramInt < i) {
        localStringBuffer.append(' ');
      }
    }
    return localStringBuffer.toString();
  }
  
  private String decodeHinfo(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer(rdlen);
    paramInt += decodeCharString(paramInt, localStringBuffer);
    localStringBuffer.append(' ');
    paramInt += decodeCharString(paramInt, localStringBuffer);
    return localStringBuffer.toString();
  }
  
  private int decodeCharString(int paramInt, StringBuffer paramStringBuffer)
  {
    int i = paramStringBuffer.length();
    int j = getUByte(paramInt++);
    int k = j == 0 ? 1 : 0;
    for (int m = 0; m < j; m++)
    {
      int n = getUByte(paramInt++);
      k |= (n == 32 ? 1 : 0);
      if ((n == 92) || (n == 34))
      {
        k = 1;
        paramStringBuffer.append('\\');
      }
      paramStringBuffer.append((char)n);
    }
    if (k != 0)
    {
      paramStringBuffer.insert(i, '"');
      paramStringBuffer.append('"');
    }
    return j + 1;
  }
  
  private String decodeA(int paramInt)
  {
    return (msg[paramInt] & 0xFF) + "." + (msg[(paramInt + 1)] & 0xFF) + "." + (msg[(paramInt + 2)] & 0xFF) + "." + (msg[(paramInt + 3)] & 0xFF);
  }
  
  private String decodeAAAA(int paramInt)
  {
    int[] arrayOfInt = new int[8];
    for (int i = 0; i < 8; i++)
    {
      arrayOfInt[i] = getUShort(paramInt);
      paramInt += 2;
    }
    i = -1;
    int j = 0;
    int k = -1;
    int m = 0;
    for (int n = 0; n < 8; n++) {
      if (arrayOfInt[n] == 0)
      {
        if (i == -1)
        {
          i = n;
          j = 1;
        }
        else
        {
          j++;
          if ((j >= 2) && (j > m))
          {
            k = i;
            m = j;
          }
        }
      }
      else {
        i = -1;
      }
    }
    if (k == 0)
    {
      if ((m == 6) || ((m == 7) && (arrayOfInt[7] > 1))) {
        return "::" + decodeA(paramInt - 4);
      }
      if ((m == 5) && (arrayOfInt[5] == 65535)) {
        return "::ffff:" + decodeA(paramInt - 4);
      }
    }
    n = k != -1 ? 1 : 0;
    StringBuffer localStringBuffer = new StringBuffer(40);
    if (k == 0) {
      localStringBuffer.append(':');
    }
    for (int i1 = 0; i1 < 8; i1++) {
      if ((n == 0) || (i1 < k) || (i1 >= k + m))
      {
        localStringBuffer.append(Integer.toHexString(arrayOfInt[i1]));
        if (i1 < 7) {
          localStringBuffer.append(':');
        }
      }
      else if ((n != 0) && (i1 == k))
      {
        localStringBuffer.append(':');
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\ResourceRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */