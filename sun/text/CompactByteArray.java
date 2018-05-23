package sun.text;

public final class CompactByteArray
  implements Cloneable
{
  public static final int UNICODECOUNT = 65536;
  private static final int BLOCKSHIFT = 7;
  private static final int BLOCKCOUNT = 128;
  private static final int INDEXSHIFT = 9;
  private static final int INDEXCOUNT = 512;
  private static final int BLOCKMASK = 127;
  private byte[] values;
  private short[] indices;
  private boolean isCompact;
  private int[] hashes;
  
  public CompactByteArray(byte paramByte)
  {
    values = new byte[65536];
    indices = new short['Ȁ'];
    hashes = new int['Ȁ'];
    for (int i = 0; i < 65536; i++) {
      values[i] = paramByte;
    }
    for (i = 0; i < 512; i++)
    {
      indices[i] = ((short)(i << 7));
      hashes[i] = 0;
    }
    isCompact = false;
  }
  
  public CompactByteArray(short[] paramArrayOfShort, byte[] paramArrayOfByte)
  {
    if (paramArrayOfShort.length != 512) {
      throw new IllegalArgumentException("Index out of bounds!");
    }
    for (int i = 0; i < 512; i++)
    {
      int j = paramArrayOfShort[i];
      if ((j < 0) || (j >= paramArrayOfByte.length + 128)) {
        throw new IllegalArgumentException("Index out of bounds!");
      }
    }
    indices = paramArrayOfShort;
    values = paramArrayOfByte;
    isCompact = true;
  }
  
  public byte elementAt(char paramChar)
  {
    return values[((indices[(paramChar >> '\007')] & 0xFFFF) + (paramChar & 0x7F))];
  }
  
  public void setElementAt(char paramChar, byte paramByte)
  {
    if (isCompact) {
      expand();
    }
    values[paramChar] = paramByte;
    touchBlock(paramChar >> '\007', paramByte);
  }
  
  public void setElementAt(char paramChar1, char paramChar2, byte paramByte)
  {
    if (isCompact) {
      expand();
    }
    for (char c = paramChar1; c <= paramChar2; c++)
    {
      values[c] = paramByte;
      touchBlock(c >> '\007', paramByte);
    }
  }
  
  public void compact()
  {
    if (!isCompact)
    {
      int i = 0;
      int j = 0;
      int k = -1;
      int m = 0;
      while (m < indices.length)
      {
        indices[m] = -1;
        boolean bool = blockTouched(m);
        if ((!bool) && (k != -1))
        {
          indices[m] = k;
        }
        else
        {
          int n = 0;
          int i1 = 0;
          i1 = 0;
          while (i1 < i)
          {
            if ((hashes[m] == hashes[i1]) && (arrayRegionMatches(values, j, values, n, 128)))
            {
              indices[m] = ((short)n);
              break;
            }
            i1++;
            n += 128;
          }
          if (indices[m] == -1)
          {
            System.arraycopy(values, j, values, n, 128);
            indices[m] = ((short)n);
            hashes[i1] = hashes[m];
            i++;
            if (!bool) {
              k = (short)n;
            }
          }
        }
        m++;
        j += 128;
      }
      m = i * 128;
      byte[] arrayOfByte = new byte[m];
      System.arraycopy(values, 0, arrayOfByte, 0, m);
      values = arrayOfByte;
      isCompact = true;
      hashes = null;
    }
  }
  
  static final boolean arrayRegionMatches(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 - paramInt1;
    for (int k = paramInt1; k < i; k++) {
      if (paramArrayOfByte1[k] != paramArrayOfByte2[(k + j)]) {
        return false;
      }
    }
    return true;
  }
  
  private final void touchBlock(int paramInt1, int paramInt2)
  {
    hashes[paramInt1] = (hashes[paramInt1] + (paramInt2 << 1) | 0x1);
  }
  
  private final boolean blockTouched(int paramInt)
  {
    return hashes[paramInt] != 0;
  }
  
  public short[] getIndexArray()
  {
    return indices;
  }
  
  public byte[] getStringArray()
  {
    return values;
  }
  
  public Object clone()
  {
    try
    {
      CompactByteArray localCompactByteArray = (CompactByteArray)super.clone();
      values = ((byte[])values.clone());
      indices = ((short[])indices.clone());
      if (hashes != null) {
        hashes = ((int[])hashes.clone());
      }
      return localCompactByteArray;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    CompactByteArray localCompactByteArray = (CompactByteArray)paramObject;
    for (int i = 0; i < 65536; i++) {
      if (elementAt((char)i) != localCompactByteArray.elementAt((char)i)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    int j = Math.min(3, values.length / 16);
    int k = 0;
    while (k < values.length)
    {
      i = i * 37 + values[k];
      k += j;
    }
    return i;
  }
  
  private void expand()
  {
    if (isCompact)
    {
      hashes = new int['Ȁ'];
      byte[] arrayOfByte = new byte[65536];
      for (int i = 0; i < 65536; i++)
      {
        int j = elementAt((char)i);
        arrayOfByte[i] = j;
        touchBlock(i >> 7, j);
      }
      for (i = 0; i < 512; i++) {
        indices[i] = ((short)(i << 7));
      }
      values = null;
      values = arrayOfByte;
      isCompact = false;
    }
  }
  
  private byte[] getArray()
  {
    return values;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\CompactByteArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */