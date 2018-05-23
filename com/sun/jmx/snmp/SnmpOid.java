package com.sun.jmx.snmp;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SnmpOid
  extends SnmpValue
{
  protected long[] components = null;
  protected int componentCount = 0;
  static final String name = "Object Identifier";
  private static SnmpOidTable meta = null;
  static final long serialVersionUID = 8956237235607885096L;
  
  public SnmpOid()
  {
    components = new long[15];
    componentCount = 0;
  }
  
  public SnmpOid(long[] paramArrayOfLong)
  {
    components = ((long[])paramArrayOfLong.clone());
    componentCount = components.length;
  }
  
  public SnmpOid(long paramLong)
  {
    components = new long[1];
    components[0] = paramLong;
    componentCount = components.length;
  }
  
  public SnmpOid(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    components = new long[4];
    components[0] = paramLong1;
    components[1] = paramLong2;
    components[2] = paramLong3;
    components[3] = paramLong4;
    componentCount = components.length;
  }
  
  public SnmpOid(String paramString)
    throws IllegalArgumentException
  {
    String str = paramString;
    if (!paramString.startsWith(".")) {
      try
      {
        str = resolveVarName(paramString);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        throw new IllegalArgumentException(localSnmpStatusException.getMessage());
      }
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(str, ".", false);
    componentCount = localStringTokenizer.countTokens();
    if (componentCount == 0)
    {
      components = new long[15];
    }
    else
    {
      components = new long[componentCount];
      try
      {
        for (int i = 0; i < componentCount; i++) {
          try
          {
            components[i] = Long.parseLong(localStringTokenizer.nextToken());
          }
          catch (NoSuchElementException localNoSuchElementException) {}
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException(paramString);
      }
    }
  }
  
  public int getLength()
  {
    return componentCount;
  }
  
  public long[] longValue()
  {
    long[] arrayOfLong = new long[componentCount];
    System.arraycopy(components, 0, arrayOfLong, 0, componentCount);
    return arrayOfLong;
  }
  
  public final long[] longValue(boolean paramBoolean)
  {
    return longValue();
  }
  
  public final long getOidArc(int paramInt)
    throws SnmpStatusException
  {
    try
    {
      return components[paramInt];
    }
    catch (Exception localException)
    {
      throw new SnmpStatusException(6);
    }
  }
  
  public Long toLong()
  {
    if (componentCount != 1) {
      throw new IllegalArgumentException();
    }
    return new Long(components[0]);
  }
  
  public Integer toInteger()
  {
    if ((componentCount != 1) || (components[0] > 2147483647L)) {
      throw new IllegalArgumentException();
    }
    return new Integer((int)components[0]);
  }
  
  public String toString()
  {
    String str = "";
    if (componentCount >= 1)
    {
      for (int i = 0; i < componentCount - 1; i++) {
        str = str + components[i] + ".";
      }
      str = str + components[(componentCount - 1)];
    }
    return str;
  }
  
  public Boolean toBoolean()
  {
    if ((componentCount != 1) && (components[0] != 1L) && (components[0] != 2L)) {
      throw new IllegalArgumentException();
    }
    return Boolean.valueOf(components[0] == 1L);
  }
  
  public Byte[] toByte()
  {
    Byte[] arrayOfByte = new Byte[componentCount];
    for (int i = 0; i < componentCount; i++)
    {
      if (components[0] > 255L) {
        throw new IllegalArgumentException();
      }
      arrayOfByte[i] = new Byte((byte)(int)components[i]);
    }
    return arrayOfByte;
  }
  
  public SnmpOid toOid()
  {
    long[] arrayOfLong = new long[componentCount];
    for (int i = 0; i < componentCount; i++) {
      arrayOfLong[i] = components[i];
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
  
  public Object clone()
  {
    try
    {
      SnmpOid localSnmpOid = (SnmpOid)super.clone();
      components = new long[componentCount];
      System.arraycopy(components, 0, components, 0, componentCount);
      return localSnmpOid;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  public void insert(long paramLong)
  {
    enlargeIfNeeded(1);
    for (int i = componentCount - 1; i >= 0; i--) {
      components[(i + 1)] = components[i];
    }
    components[0] = paramLong;
    componentCount += 1;
  }
  
  public void insert(int paramInt)
  {
    insert(paramInt);
  }
  
  public void append(SnmpOid paramSnmpOid)
  {
    enlargeIfNeeded(componentCount);
    for (int i = 0; i < componentCount; i++) {
      components[(componentCount + i)] = components[i];
    }
    componentCount += componentCount;
  }
  
  public void append(long paramLong)
  {
    enlargeIfNeeded(1);
    components[componentCount] = paramLong;
    componentCount += 1;
  }
  
  public void addToOid(String paramString)
    throws SnmpStatusException
  {
    SnmpOid localSnmpOid = new SnmpOid(paramString);
    append(localSnmpOid);
  }
  
  public void addToOid(long[] paramArrayOfLong)
    throws SnmpStatusException
  {
    SnmpOid localSnmpOid = new SnmpOid(paramArrayOfLong);
    append(localSnmpOid);
  }
  
  public boolean isValid()
  {
    return (componentCount >= 2) && (0L <= components[0]) && (components[0] < 3L) && (0L <= components[1]) && (components[1] < 40L);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof SnmpOid))
    {
      SnmpOid localSnmpOid = (SnmpOid)paramObject;
      if (componentCount == componentCount)
      {
        int i = 0;
        long[] arrayOfLong = components;
        while ((i < componentCount) && (components[i] == arrayOfLong[i])) {
          i++;
        }
        bool = i == componentCount;
      }
    }
    return bool;
  }
  
  public int hashCode()
  {
    long l = 0L;
    for (int i = 0; i < componentCount; i++) {
      l = l * 31L + components[i];
    }
    return (int)l;
  }
  
  public int compareTo(SnmpOid paramSnmpOid)
  {
    int i = 0;
    int j = 0;
    int k = Math.min(componentCount, componentCount);
    long[] arrayOfLong = components;
    for (j = 0; (j < k) && (components[j] == arrayOfLong[j]); j++) {}
    if ((j == componentCount) && (j == componentCount)) {
      i = 0;
    } else if (j == componentCount) {
      i = -1;
    } else if (j == componentCount) {
      i = 1;
    } else {
      i = components[j] < arrayOfLong[j] ? -1 : 1;
    }
    return i;
  }
  
  public String resolveVarName(String paramString)
    throws SnmpStatusException
  {
    int i = paramString.indexOf('.');
    try
    {
      return handleLong(paramString, i);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      SnmpOidTable localSnmpOidTable = getSnmpOidTable();
      if (localSnmpOidTable == null) {
        throw new SnmpStatusException(2);
      }
      if (i <= 0)
      {
        localSnmpOidRecord = localSnmpOidTable.resolveVarName(paramString);
        return localSnmpOidRecord.getOid();
      }
      SnmpOidRecord localSnmpOidRecord = localSnmpOidTable.resolveVarName(paramString.substring(0, i));
      return localSnmpOidRecord.getOid() + paramString.substring(i);
    }
  }
  
  public String getTypeName()
  {
    return "Object Identifier";
  }
  
  public static SnmpOidTable getSnmpOidTable()
  {
    return meta;
  }
  
  public static void setSnmpOidTable(SnmpOidTable paramSnmpOidTable)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SnmpPermission("setSnmpOidTable"));
    }
    meta = paramSnmpOidTable;
  }
  
  public String toOctetString()
  {
    return new String(tobyte());
  }
  
  private byte[] tobyte()
  {
    byte[] arrayOfByte = new byte[componentCount];
    for (int i = 0; i < componentCount; i++)
    {
      if (components[0] > 255L) {
        throw new IllegalArgumentException();
      }
      arrayOfByte[i] = ((byte)(int)components[i]);
    }
    return arrayOfByte;
  }
  
  private void enlargeIfNeeded(int paramInt)
  {
    int i = components.length;
    while (componentCount + paramInt > i) {
      i *= 2;
    }
    if (i > components.length)
    {
      long[] arrayOfLong = new long[i];
      for (int j = 0; j < components.length; j++) {
        arrayOfLong[j] = components[j];
      }
      components = arrayOfLong;
    }
  }
  
  private String handleLong(String paramString, int paramInt)
    throws NumberFormatException, SnmpStatusException
  {
    String str;
    if (paramInt > 0) {
      str = paramString.substring(0, paramInt);
    } else {
      str = paramString;
    }
    Long.parseLong(str);
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */