package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class AdaptiveCoding
  implements CodingMethod
{
  CodingMethod headCoding;
  int headLength;
  CodingMethod tailCoding;
  public static final int KX_MIN = 0;
  public static final int KX_MAX = 3;
  public static final int KX_LG2BASE = 4;
  public static final int KX_BASE = 16;
  public static final int KB_MIN = 0;
  public static final int KB_MAX = 255;
  public static final int KB_OFFSET = 1;
  public static final int KB_DEFAULT = 3;
  
  public AdaptiveCoding(int paramInt, CodingMethod paramCodingMethod1, CodingMethod paramCodingMethod2)
  {
    assert (isCodableLength(paramInt));
    headLength = paramInt;
    headCoding = paramCodingMethod1;
    tailCoding = paramCodingMethod2;
  }
  
  public void setHeadCoding(CodingMethod paramCodingMethod)
  {
    headCoding = paramCodingMethod;
  }
  
  public void setHeadLength(int paramInt)
  {
    assert (isCodableLength(paramInt));
    headLength = paramInt;
  }
  
  public void setTailCoding(CodingMethod paramCodingMethod)
  {
    tailCoding = paramCodingMethod;
  }
  
  public boolean isTrivial()
  {
    return headCoding == tailCoding;
  }
  
  public void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    writeArray(this, paramOutputStream, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  private static void writeArray(AdaptiveCoding paramAdaptiveCoding, OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    for (;;)
    {
      int i = paramInt1 + headLength;
      assert (i <= paramInt2);
      headCoding.writeArrayTo(paramOutputStream, paramArrayOfInt, paramInt1, i);
      paramInt1 = i;
      if (!(tailCoding instanceof AdaptiveCoding)) {
        break;
      }
      paramAdaptiveCoding = (AdaptiveCoding)tailCoding;
    }
    tailCoding.writeArrayTo(paramOutputStream, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    readArray(this, paramInputStream, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  private static void readArray(AdaptiveCoding paramAdaptiveCoding, InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    for (;;)
    {
      int i = paramInt1 + headLength;
      assert (i <= paramInt2);
      headCoding.readArrayFrom(paramInputStream, paramArrayOfInt, paramInt1, i);
      paramInt1 = i;
      if (!(tailCoding instanceof AdaptiveCoding)) {
        break;
      }
      paramAdaptiveCoding = (AdaptiveCoding)tailCoding;
    }
    tailCoding.readArrayFrom(paramInputStream, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  static int getKXOf(int paramInt)
  {
    for (int i = 0; i <= 3; i++)
    {
      if ((paramInt - 1 & 0xFF00) == 0) {
        return i;
      }
      paramInt >>>= 4;
    }
    return -1;
  }
  
  static int getKBOf(int paramInt)
  {
    int i = getKXOf(paramInt);
    if (i < 0) {
      return -1;
    }
    paramInt >>>= i * 4;
    return paramInt - 1;
  }
  
  static int decodeK(int paramInt1, int paramInt2)
  {
    assert ((0 <= paramInt1) && (paramInt1 <= 3));
    assert ((0 <= paramInt2) && (paramInt2 <= 255));
    return paramInt2 + 1 << paramInt1 * 4;
  }
  
  static int getNextK(int paramInt)
  {
    if (paramInt <= 0) {
      return 1;
    }
    int i = getKXOf(paramInt);
    if (i < 0) {
      return Integer.MAX_VALUE;
    }
    int j = 1 << i * 4;
    int k = 255 << i * 4;
    int m = paramInt + j;
    m &= (j - 1 ^ 0xFFFFFFFF);
    if ((m - j & (k ^ 0xFFFFFFFF)) == 0)
    {
      assert (getKXOf(m) == i);
      return m;
    }
    if (i == 3) {
      return Integer.MAX_VALUE;
    }
    i++;
    int n = 255 << i * 4;
    m |= k & (n ^ 0xFFFFFFFF);
    m += j;
    assert (getKXOf(m) == i);
    return m;
  }
  
  public static boolean isCodableLength(int paramInt)
  {
    int i = getKXOf(paramInt);
    if (i < 0) {
      return false;
    }
    int j = 1 << i * 4;
    int k = 255 << i * 4;
    return (paramInt - j & (k ^ 0xFFFFFFFF)) == 0;
  }
  
  public byte[] getMetaCoding(Coding paramCoding)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(10);
    try
    {
      makeMetaCoding(this, paramCoding, localByteArrayOutputStream);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  private static void makeMetaCoding(AdaptiveCoding paramAdaptiveCoding, Coding paramCoding, ByteArrayOutputStream paramByteArrayOutputStream)
    throws IOException
  {
    CodingMethod localCodingMethod2;
    int m;
    for (;;)
    {
      CodingMethod localCodingMethod1 = headCoding;
      int i = headLength;
      localCodingMethod2 = tailCoding;
      int j = i;
      assert (isCodableLength(j));
      int k = localCodingMethod1 == paramCoding ? 1 : 0;
      m = localCodingMethod2 == paramCoding ? 1 : 0;
      if (k + m > 1) {
        m = 0;
      }
      int n = 1 * k + 2 * m;
      assert (n < 3);
      int i1 = getKXOf(j);
      int i2 = getKBOf(j);
      assert (decodeK(i1, i2) == j);
      int i3 = i2 != 3 ? 1 : 0;
      paramByteArrayOutputStream.write(117 + i1 + 4 * i3 + 8 * n);
      if (i3 != 0) {
        paramByteArrayOutputStream.write(i2);
      }
      if (k == 0) {
        paramByteArrayOutputStream.write(localCodingMethod1.getMetaCoding(paramCoding));
      }
      if (!(localCodingMethod2 instanceof AdaptiveCoding)) {
        break;
      }
      paramAdaptiveCoding = (AdaptiveCoding)localCodingMethod2;
    }
    if (m == 0) {
      paramByteArrayOutputStream.write(localCodingMethod2.getMetaCoding(paramCoding));
    }
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod)
  {
    int i = paramArrayOfByte[(paramInt++)] & 0xFF;
    if ((i < 117) || (i >= 141)) {
      return paramInt - 1;
    }
    Object localObject = null;
    int j = 1;
    while (j != 0)
    {
      j = 0;
      assert (i >= 117);
      i -= 117;
      int k = i % 4;
      int m = i / 4 % 2;
      int n = i / 8;
      assert (n < 3);
      int i1 = n & 0x1;
      int i2 = n & 0x2;
      CodingMethod[] arrayOfCodingMethod1 = { paramCoding };
      CodingMethod[] arrayOfCodingMethod2 = { paramCoding };
      int i3 = 3;
      if (m != 0) {
        i3 = paramArrayOfByte[(paramInt++)] & 0xFF;
      }
      if (i1 == 0) {
        paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod1);
      }
      if ((i2 == 0) && ((i = paramArrayOfByte[paramInt] & 0xFF) >= 117) && (i < 141))
      {
        paramInt++;
        j = 1;
      }
      else if (i2 == 0)
      {
        paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod2);
      }
      AdaptiveCoding localAdaptiveCoding = new AdaptiveCoding(decodeK(k, i3), arrayOfCodingMethod1[0], arrayOfCodingMethod2[0]);
      if (localObject == null) {
        paramArrayOfCodingMethod[0] = localAdaptiveCoding;
      } else {
        tailCoding = localAdaptiveCoding;
      }
      localObject = localAdaptiveCoding;
    }
    return paramInt;
  }
  
  private String keyString(CodingMethod paramCodingMethod)
  {
    if ((paramCodingMethod instanceof Coding)) {
      return ((Coding)paramCodingMethod).keyString();
    }
    return paramCodingMethod.toString();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(20);
    AdaptiveCoding localAdaptiveCoding = this;
    localStringBuilder.append("run(");
    for (;;)
    {
      localStringBuilder.append(headLength).append("*");
      localStringBuilder.append(keyString(headCoding));
      if (!(tailCoding instanceof AdaptiveCoding)) {
        break;
      }
      localAdaptiveCoding = (AdaptiveCoding)tailCoding;
      localStringBuilder.append(" ");
    }
    localStringBuilder.append(" **").append(keyString(tailCoding));
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\AdaptiveCoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */