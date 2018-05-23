package com.sun.jmx.snmp;

public class BerDecoder
{
  public static final int BooleanTag = 1;
  public static final int IntegerTag = 2;
  public static final int OctetStringTag = 4;
  public static final int NullTag = 5;
  public static final int OidTag = 6;
  public static final int SequenceTag = 48;
  private final byte[] bytes;
  private int next = 0;
  private final int[] stackBuf = new int['È'];
  private int stackTop = 0;
  
  public BerDecoder(byte[] paramArrayOfByte)
  {
    bytes = paramArrayOfByte;
    reset();
  }
  
  public void reset()
  {
    next = 0;
    stackTop = 0;
  }
  
  public int fetchInteger()
    throws BerException
  {
    return fetchInteger(2);
  }
  
  public int fetchInteger(int paramInt)
    throws BerException
  {
    int i = 0;
    int j = next;
    try
    {
      if (fetchTag() != paramInt) {
        throw new BerException();
      }
      i = fetchIntegerValue();
    }
    catch (BerException localBerException)
    {
      next = j;
      throw localBerException;
    }
    return i;
  }
  
  public long fetchIntegerAsLong()
    throws BerException
  {
    return fetchIntegerAsLong(2);
  }
  
  public long fetchIntegerAsLong(int paramInt)
    throws BerException
  {
    long l = 0L;
    int i = next;
    try
    {
      if (fetchTag() != paramInt) {
        throw new BerException();
      }
      l = fetchIntegerValueAsLong();
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
    return l;
  }
  
  public byte[] fetchOctetString()
    throws BerException
  {
    return fetchOctetString(4);
  }
  
  public byte[] fetchOctetString(int paramInt)
    throws BerException
  {
    byte[] arrayOfByte = null;
    int i = next;
    try
    {
      if (fetchTag() != paramInt) {
        throw new BerException();
      }
      arrayOfByte = fetchStringValue();
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
    return arrayOfByte;
  }
  
  public long[] fetchOid()
    throws BerException
  {
    return fetchOid(6);
  }
  
  public long[] fetchOid(int paramInt)
    throws BerException
  {
    long[] arrayOfLong = null;
    int i = next;
    try
    {
      if (fetchTag() != paramInt) {
        throw new BerException();
      }
      arrayOfLong = fetchOidValue();
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
    return arrayOfLong;
  }
  
  public void fetchNull()
    throws BerException
  {
    fetchNull(5);
  }
  
  public void fetchNull(int paramInt)
    throws BerException
  {
    int i = next;
    try
    {
      if (fetchTag() != paramInt) {
        throw new BerException();
      }
      int j = fetchLength();
      if (j != 0) {
        throw new BerException();
      }
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
  }
  
  public byte[] fetchAny()
    throws BerException
  {
    Object localObject = null;
    int i = next;
    try
    {
      int j = fetchTag();
      int k = fetchLength();
      if (k < 0) {
        throw new BerException();
      }
      int m = next + k - i;
      if (k > bytes.length - next) {
        throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      }
      byte[] arrayOfByte = new byte[m];
      System.arraycopy(bytes, i, arrayOfByte, 0, m);
      next += k;
      localObject = arrayOfByte;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = i;
      throw new BerException();
    }
    return (byte[])localObject;
  }
  
  public byte[] fetchAny(int paramInt)
    throws BerException
  {
    if (getTag() != paramInt) {
      throw new BerException();
    }
    return fetchAny();
  }
  
  public void openSequence()
    throws BerException
  {
    openSequence(48);
  }
  
  public void openSequence(int paramInt)
    throws BerException
  {
    int i = next;
    try
    {
      if (fetchTag() != paramInt) {
        throw new BerException();
      }
      int j = fetchLength();
      if (j < 0) {
        throw new BerException();
      }
      if (j > bytes.length - next) {
        throw new BerException();
      }
      stackBuf[(stackTop++)] = (next + j);
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
  }
  
  public void closeSequence()
    throws BerException
  {
    if (stackBuf[(stackTop - 1)] == next) {
      stackTop -= 1;
    } else {
      throw new BerException();
    }
  }
  
  public boolean cannotCloseSequence()
  {
    return next < stackBuf[(stackTop - 1)];
  }
  
  /* Error */
  public int getTag()
    throws BerException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 125	com/sun/jmx/snmp/BerDecoder:next	I
    //   6: istore_2
    //   7: aload_0
    //   8: invokespecial 131	com/sun/jmx/snmp/BerDecoder:fetchTag	()I
    //   11: istore_1
    //   12: aload_0
    //   13: iload_2
    //   14: putfield 125	com/sun/jmx/snmp/BerDecoder:next	I
    //   17: goto +11 -> 28
    //   20: astore_3
    //   21: aload_0
    //   22: iload_2
    //   23: putfield 125	com/sun/jmx/snmp/BerDecoder:next	I
    //   26: aload_3
    //   27: athrow
    //   28: iload_1
    //   29: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	BerDecoder
    //   1	28	1	i	int
    //   6	17	2	j	int
    //   20	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	12	20	finally
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++)
    {
      int j = bytes[i] > 0 ? bytes[i] : bytes[i] + 256;
      if (i == next) {
        localStringBuffer.append("(");
      }
      localStringBuffer.append(Character.forDigit(j / 16, 16));
      localStringBuffer.append(Character.forDigit(j % 16, 16));
      if (i == next) {
        localStringBuffer.append(")");
      }
    }
    if (bytes.length == next) {
      localStringBuffer.append("()");
    }
    return new String(localStringBuffer);
  }
  
  private final int fetchTag()
    throws BerException
  {
    int i = 0;
    int j = next;
    try
    {
      int k = bytes[(next++)];
      i = k >= 0 ? k : k + 256;
      if ((i & 0x1F) == 31) {
        while ((bytes[next] & 0x80) != 0)
        {
          i <<= 7;
          i |= bytes[(next++)] & 0x7F;
        }
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = j;
      throw new BerException();
    }
    return i;
  }
  
  private final int fetchLength()
    throws BerException
  {
    int i = 0;
    int j = next;
    try
    {
      int k = bytes[(next++)];
      if (k >= 0) {
        i = k;
      } else {
        for (int m = 128 + k; m > 0; m--)
        {
          int n = bytes[(next++)];
          i <<= 8;
          i |= (n >= 0 ? n : n + 256);
        }
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = j;
      throw new BerException();
    }
    return i;
  }
  
  private int fetchIntegerValue()
    throws BerException
  {
    int i = 0;
    int j = next;
    try
    {
      int k = fetchLength();
      if (k <= 0) {
        throw new BerException();
      }
      if (k > bytes.length - next) {
        throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      }
      int m = next + k;
      i = bytes[(next++)];
      while (next < m)
      {
        int n = bytes[(next++)];
        if (n < 0) {
          i = i << 8 | 256 + n;
        } else {
          i = i << 8 | n;
        }
      }
    }
    catch (BerException localBerException)
    {
      next = j;
      throw localBerException;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = j;
      throw new BerException();
    }
    catch (ArithmeticException localArithmeticException)
    {
      next = j;
      throw new BerException();
    }
    return i;
  }
  
  private final long fetchIntegerValueAsLong()
    throws BerException
  {
    long l = 0L;
    int i = next;
    try
    {
      int j = fetchLength();
      if (j <= 0) {
        throw new BerException();
      }
      if (j > bytes.length - next) {
        throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      }
      int k = next + j;
      l = bytes[(next++)];
      while (next < k)
      {
        int m = bytes[(next++)];
        if (m < 0) {
          l = l << 8 | 256 + m;
        } else {
          l = l << 8 | m;
        }
      }
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = i;
      throw new BerException();
    }
    catch (ArithmeticException localArithmeticException)
    {
      next = i;
      throw new BerException();
    }
    return l;
  }
  
  private byte[] fetchStringValue()
    throws BerException
  {
    Object localObject = null;
    int i = next;
    try
    {
      int j = fetchLength();
      if (j < 0) {
        throw new BerException();
      }
      if (j > bytes.length - next) {
        throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      }
      byte[] arrayOfByte = new byte[j];
      System.arraycopy(bytes, next, arrayOfByte, 0, j);
      next += j;
      localObject = arrayOfByte;
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = i;
      throw new BerException();
    }
    catch (ArithmeticException localArithmeticException)
    {
      next = i;
      throw new BerException();
    }
    return (byte[])localObject;
  }
  
  private final long[] fetchOidValue()
    throws BerException
  {
    Object localObject = null;
    int i = next;
    try
    {
      int j = fetchLength();
      if (j <= 0) {
        throw new BerException();
      }
      if (j > bytes.length - next) {
        throw new IndexOutOfBoundsException("Decoded length exceeds buffer");
      }
      int k = 2;
      for (int m = 1; m < j; m++) {
        if ((bytes[(next + m)] & 0x80) == 0) {
          k++;
        }
      }
      m = k;
      long[] arrayOfLong = new long[m];
      int n = bytes[(next++)];
      if (n < 0) {
        throw new BerException();
      }
      long l1 = n / 40;
      if (l1 > 2L) {
        throw new BerException();
      }
      long l2 = n % 40;
      arrayOfLong[0] = l1;
      arrayOfLong[1] = l2;
      int i1 = 2;
      while (i1 < m)
      {
        long l3 = 0L;
        for (int i2 = bytes[(next++)]; (i2 & 0x80) != 0; i2 = bytes[(next++)])
        {
          l3 = l3 << 7 | i2 & 0x7F;
          if (l3 < 0L) {
            throw new BerException();
          }
        }
        l3 = l3 << 7 | i2;
        if (l3 < 0L) {
          throw new BerException();
        }
        arrayOfLong[(i1++)] = l3;
      }
      localObject = arrayOfLong;
    }
    catch (BerException localBerException)
    {
      next = i;
      throw localBerException;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      next = i;
      throw new BerException();
    }
    return (long[])localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\BerDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */