package com.sun.jmx.snmp;

public class BerEncoder
{
  public static final int BooleanTag = 1;
  public static final int IntegerTag = 2;
  public static final int OctetStringTag = 4;
  public static final int NullTag = 5;
  public static final int OidTag = 6;
  public static final int SequenceTag = 48;
  protected final byte[] bytes;
  protected int start = -1;
  protected final int[] stackBuf = new int['È'];
  protected int stackTop = 0;
  
  public BerEncoder(byte[] paramArrayOfByte)
  {
    bytes = paramArrayOfByte;
    start = paramArrayOfByte.length;
    stackTop = 0;
  }
  
  public int trim()
  {
    int i = bytes.length - start;
    if (i > 0) {
      System.arraycopy(bytes, start, bytes, 0, i);
    }
    start = bytes.length;
    stackTop = 0;
    return i;
  }
  
  public void putInteger(int paramInt)
  {
    putInteger(paramInt, 2);
  }
  
  public void putInteger(int paramInt1, int paramInt2)
  {
    putIntegerValue(paramInt1);
    putTag(paramInt2);
  }
  
  public void putInteger(long paramLong)
  {
    putInteger(paramLong, 2);
  }
  
  public void putInteger(long paramLong, int paramInt)
  {
    putIntegerValue(paramLong);
    putTag(paramInt);
  }
  
  public void putOctetString(byte[] paramArrayOfByte)
  {
    putOctetString(paramArrayOfByte, 4);
  }
  
  public void putOctetString(byte[] paramArrayOfByte, int paramInt)
  {
    putStringValue(paramArrayOfByte);
    putTag(paramInt);
  }
  
  public void putOid(long[] paramArrayOfLong)
  {
    putOid(paramArrayOfLong, 6);
  }
  
  public void putOid(long[] paramArrayOfLong, int paramInt)
  {
    putOidValue(paramArrayOfLong);
    putTag(paramInt);
  }
  
  public void putNull()
  {
    putNull(5);
  }
  
  public void putNull(int paramInt)
  {
    putLength(0);
    putTag(paramInt);
  }
  
  public void putAny(byte[] paramArrayOfByte)
  {
    putAny(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void putAny(byte[] paramArrayOfByte, int paramInt)
  {
    System.arraycopy(paramArrayOfByte, 0, bytes, start - paramInt, paramInt);
    start -= paramInt;
  }
  
  public void openSequence()
  {
    stackBuf[(stackTop++)] = start;
  }
  
  public void closeSequence()
  {
    closeSequence(48);
  }
  
  public void closeSequence(int paramInt)
  {
    int i = stackBuf[(--stackTop)];
    putLength(i - start);
    putTag(paramInt);
  }
  
  protected final void putTag(int paramInt)
  {
    if (paramInt < 256) {
      bytes[(--start)] = ((byte)paramInt);
    } else {
      while (paramInt != 0)
      {
        bytes[(--start)] = ((byte)(paramInt & 0x7F));
        paramInt <<= 7;
      }
    }
  }
  
  protected final void putLength(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    if (paramInt < 128)
    {
      bytes[(--start)] = ((byte)paramInt);
    }
    else if (paramInt < 256)
    {
      bytes[(--start)] = ((byte)paramInt);
      bytes[(--start)] = -127;
    }
    else if (paramInt < 65536)
    {
      bytes[(--start)] = ((byte)paramInt);
      bytes[(--start)] = ((byte)(paramInt >> 8));
      bytes[(--start)] = -126;
    }
    else if (paramInt < 16777126)
    {
      bytes[(--start)] = ((byte)paramInt);
      bytes[(--start)] = ((byte)(paramInt >> 8));
      bytes[(--start)] = ((byte)(paramInt >> 16));
      bytes[(--start)] = -125;
    }
    else
    {
      bytes[(--start)] = ((byte)paramInt);
      bytes[(--start)] = ((byte)(paramInt >> 8));
      bytes[(--start)] = ((byte)(paramInt >> 16));
      bytes[(--start)] = ((byte)(paramInt >> 24));
      bytes[(--start)] = -124;
    }
  }
  
  protected final void putIntegerValue(int paramInt)
  {
    int i = start;
    int j = 2139095040;
    int k = 4;
    if (paramInt < 0) {
      while (((j & paramInt) == j) && (k > 1))
      {
        j >>= 8;
        k--;
      }
    }
    while (((j & paramInt) == 0) && (k > 1))
    {
      j >>= 8;
      k--;
    }
    for (int m = 0; m < k; m++)
    {
      bytes[(--start)] = ((byte)paramInt);
      paramInt >>= 8;
    }
    putLength(i - start);
  }
  
  protected final void putIntegerValue(long paramLong)
  {
    int i = start;
    long l = 9187343239835811840L;
    int j = 8;
    if (paramLong < 0L) {
      while (((l & paramLong) == l) && (j > 1))
      {
        l >>= 8;
        j--;
      }
    }
    while (((l & paramLong) == 0L) && (j > 1))
    {
      l >>= 8;
      j--;
    }
    for (int k = 0; k < j; k++)
    {
      bytes[(--start)] = ((byte)(int)paramLong);
      paramLong >>= 8;
    }
    putLength(i - start);
  }
  
  protected final void putStringValue(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    System.arraycopy(paramArrayOfByte, 0, bytes, start - i, i);
    start -= i;
    putLength(i);
  }
  
  protected final void putOidValue(long[] paramArrayOfLong)
  {
    int i = start;
    int j = paramArrayOfLong.length;
    if ((j < 2) || (paramArrayOfLong[0] > 2L) || (paramArrayOfLong[1] >= 40L)) {
      throw new IllegalArgumentException();
    }
    for (int k = j - 1; k >= 2; k--)
    {
      long l = paramArrayOfLong[k];
      if (l < 0L) {
        throw new IllegalArgumentException();
      }
      if (l < 128L)
      {
        bytes[(--start)] = ((byte)(int)l);
      }
      else
      {
        bytes[(--start)] = ((byte)(int)(l & 0x7F));
        for (l >>= 7; l != 0L; l >>= 7) {
          bytes[(--start)] = ((byte)(int)(l | 0x80));
        }
      }
    }
    bytes[(--start)] = ((byte)(int)(paramArrayOfLong[0] * 40L + paramArrayOfLong[1]));
    putLength(i - start);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\BerEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */