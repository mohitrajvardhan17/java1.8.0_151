package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class BitArray
{
  private byte[] repn;
  private int length;
  private static final int BITS_PER_UNIT = 8;
  private static final byte[][] NYBBLE = { { 48, 48, 48, 48 }, { 48, 48, 48, 49 }, { 48, 48, 49, 48 }, { 48, 48, 49, 49 }, { 48, 49, 48, 48 }, { 48, 49, 48, 49 }, { 48, 49, 49, 48 }, { 48, 49, 49, 49 }, { 49, 48, 48, 48 }, { 49, 48, 48, 49 }, { 49, 48, 49, 48 }, { 49, 48, 49, 49 }, { 49, 49, 48, 48 }, { 49, 49, 48, 49 }, { 49, 49, 49, 48 }, { 49, 49, 49, 49 } };
  private static final int BYTES_PER_LINE = 8;
  
  private static int subscript(int paramInt)
  {
    return paramInt / 8;
  }
  
  private static int position(int paramInt)
  {
    return 1 << 7 - paramInt % 8;
  }
  
  public BitArray(int paramInt)
    throws IllegalArgumentException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative length for BitArray");
    }
    length = paramInt;
    repn = new byte[(paramInt + 8 - 1) / 8];
  }
  
  public BitArray(int paramInt, byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative length for BitArray");
    }
    if (paramArrayOfByte.length * 8 < paramInt) {
      throw new IllegalArgumentException("Byte array too short to represent bit array of given length");
    }
    length = paramInt;
    int i = (paramInt + 8 - 1) / 8;
    int j = i * 8 - paramInt;
    int k = (byte)(255 << j);
    repn = new byte[i];
    System.arraycopy(paramArrayOfByte, 0, repn, 0, i);
    if (i > 0)
    {
      int tmp98_97 = (i - 1);
      byte[] tmp98_92 = repn;
      tmp98_92[tmp98_97] = ((byte)(tmp98_92[tmp98_97] & k));
    }
  }
  
  public BitArray(boolean[] paramArrayOfBoolean)
  {
    length = paramArrayOfBoolean.length;
    repn = new byte[(length + 7) / 8];
    for (int i = 0; i < length; i++) {
      set(i, paramArrayOfBoolean[i]);
    }
  }
  
  private BitArray(BitArray paramBitArray)
  {
    length = length;
    repn = ((byte[])repn.clone());
  }
  
  public boolean get(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    if ((paramInt < 0) || (paramInt >= length)) {
      throw new ArrayIndexOutOfBoundsException(Integer.toString(paramInt));
    }
    return (repn[subscript(paramInt)] & position(paramInt)) != 0;
  }
  
  public void set(int paramInt, boolean paramBoolean)
    throws ArrayIndexOutOfBoundsException
  {
    if ((paramInt < 0) || (paramInt >= length)) {
      throw new ArrayIndexOutOfBoundsException(Integer.toString(paramInt));
    }
    int i = subscript(paramInt);
    int j = position(paramInt);
    if (paramBoolean)
    {
      int tmp44_43 = i;
      byte[] tmp44_40 = repn;
      tmp44_40[tmp44_43] = ((byte)(tmp44_40[tmp44_43] | j));
    }
    else
    {
      int tmp59_58 = i;
      byte[] tmp59_55 = repn;
      tmp59_55[tmp59_58] = ((byte)(tmp59_55[tmp59_58] & (j ^ 0xFFFFFFFF)));
    }
  }
  
  public int length()
  {
    return length;
  }
  
  public byte[] toByteArray()
  {
    return (byte[])repn.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof BitArray))) {
      return false;
    }
    BitArray localBitArray = (BitArray)paramObject;
    if (length != length) {
      return false;
    }
    for (int i = 0; i < repn.length; i++) {
      if (repn[i] != repn[i]) {
        return false;
      }
    }
    return true;
  }
  
  public boolean[] toBooleanArray()
  {
    boolean[] arrayOfBoolean = new boolean[length];
    for (int i = 0; i < length; i++) {
      arrayOfBoolean[i] = get(i);
    }
    return arrayOfBoolean;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < repn.length; j++) {
      i = 31 * i + repn[j];
    }
    return i ^ length;
  }
  
  public Object clone()
  {
    return new BitArray(this);
  }
  
  public String toString()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (int i = 0; i < repn.length - 1; i++)
    {
      localByteArrayOutputStream.write(NYBBLE[(repn[i] >> 4 & 0xF)], 0, 4);
      localByteArrayOutputStream.write(NYBBLE[(repn[i] & 0xF)], 0, 4);
      if (i % 8 == 7) {
        localByteArrayOutputStream.write(10);
      } else {
        localByteArrayOutputStream.write(32);
      }
    }
    for (i = 8 * (repn.length - 1); i < length; i++) {
      localByteArrayOutputStream.write(get(i) ? 49 : 48);
    }
    return new String(localByteArrayOutputStream.toByteArray());
  }
  
  public BitArray truncate()
  {
    for (int i = length - 1; i >= 0; i--) {
      if (get(i)) {
        return new BitArray(i + 1, Arrays.copyOf(repn, (i + 8) / 8));
      }
    }
    return new BitArray(1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\BitArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */