package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class PopulationCoding
  implements CodingMethod
{
  Histogram vHist;
  int[] fValues;
  int fVlen;
  long[] symtab;
  CodingMethod favoredCoding;
  CodingMethod tokenCoding;
  CodingMethod unfavoredCoding;
  int L = -1;
  static final int[] LValuesCoded = { -1, 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252 };
  
  PopulationCoding() {}
  
  public void setFavoredValues(int[] paramArrayOfInt, int paramInt)
  {
    assert (paramArrayOfInt[0] == 0);
    assert (fValues == null);
    fValues = paramArrayOfInt;
    fVlen = paramInt;
    if (L >= 0) {
      setL(L);
    }
  }
  
  public void setFavoredValues(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length - 1;
    setFavoredValues(paramArrayOfInt, i);
  }
  
  public void setHistogram(Histogram paramHistogram)
  {
    vHist = paramHistogram;
  }
  
  public void setL(int paramInt)
  {
    L = paramInt;
    if ((paramInt >= 0) && (fValues != null) && (tokenCoding == null))
    {
      tokenCoding = fitTokenCoding(fVlen, paramInt);
      assert (tokenCoding != null);
    }
  }
  
  public static Coding fitTokenCoding(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 256) {
      return BandStructure.BYTE1;
    }
    Coding localCoding1 = BandStructure.UNSIGNED5.setL(paramInt2);
    if (!localCoding1.canRepresentUnsigned(paramInt1)) {
      return null;
    }
    Object localObject = localCoding1;
    Coding localCoding2 = localCoding1;
    for (;;)
    {
      localCoding2 = localCoding2.setB(localCoding2.B() - 1);
      if (localCoding2.umax() < paramInt1) {
        break;
      }
      localObject = localCoding2;
    }
    return (Coding)localObject;
  }
  
  public void setFavoredCoding(CodingMethod paramCodingMethod)
  {
    favoredCoding = paramCodingMethod;
  }
  
  public void setTokenCoding(CodingMethod paramCodingMethod)
  {
    tokenCoding = paramCodingMethod;
    L = -1;
    if (((paramCodingMethod instanceof Coding)) && (fValues != null))
    {
      Coding localCoding = (Coding)paramCodingMethod;
      if (localCoding == fitTokenCoding(fVlen, localCoding.L())) {
        L = localCoding.L();
      }
    }
  }
  
  public void setUnfavoredCoding(CodingMethod paramCodingMethod)
  {
    unfavoredCoding = paramCodingMethod;
  }
  
  public int favoredValueMaxLength()
  {
    if (L == 0) {
      return Integer.MAX_VALUE;
    }
    return BandStructure.UNSIGNED5.setL(L).umax();
  }
  
  public void resortFavoredValues()
  {
    Coding localCoding = (Coding)tokenCoding;
    fValues = BandStructure.realloc(fValues, 1 + fVlen);
    int i = 1;
    for (int j = 1; j <= localCoding.B(); j++)
    {
      int k = localCoding.byteMax(j);
      if (k > fVlen) {
        k = fVlen;
      }
      if (k < localCoding.byteMin(j)) {
        break;
      }
      int m = i;
      int n = k + 1;
      if (n != m)
      {
        assert (n > m) : (n + "!>" + m);
        assert (localCoding.getLength(m) == j) : (j + " != len(" + m + ") == " + localCoding.getLength(m));
        assert (localCoding.getLength(n - 1) == j) : (j + " != len(" + (n - 1) + ") == " + localCoding.getLength(n - 1));
        int i1 = m + (n - m) / 2;
        int i2 = m;
        int i3 = -1;
        int i4 = m;
        for (int i5 = m; i5 < n; i5++)
        {
          int i6 = fValues[i5];
          int i7 = vHist.getFrequency(i6);
          if (i3 != i7)
          {
            if (j == 1) {
              Arrays.sort(fValues, i4, i5);
            } else if (Math.abs(i2 - i1) > Math.abs(i5 - i1)) {
              i2 = i5;
            }
            i3 = i7;
            i4 = i5;
          }
        }
        if (j == 1)
        {
          Arrays.sort(fValues, i4, n);
        }
        else
        {
          Arrays.sort(fValues, m, i2);
          Arrays.sort(fValues, i2, n);
        }
        assert (localCoding.getLength(m) == localCoding.getLength(i2));
        assert (localCoding.getLength(m) == localCoding.getLength(n - 1));
        i = k + 1;
      }
    }
    assert (i == fValues.length);
    symtab = null;
  }
  
  public int getToken(int paramInt)
  {
    if (symtab == null) {
      symtab = makeSymtab();
    }
    int i = Arrays.binarySearch(symtab, paramInt << 32);
    if (i < 0) {
      i = -i - 1;
    }
    if ((i < symtab.length) && (paramInt == (int)(symtab[i] >>> 32))) {
      return (int)symtab[i];
    }
    return 0;
  }
  
  public int[][] encodeValues(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt1 = new int[paramInt2 - paramInt1];
    int i = 0;
    int m;
    for (int j = 0; j < arrayOfInt1.length; j++)
    {
      k = paramArrayOfInt[(paramInt1 + j)];
      m = getToken(k);
      if (m != 0) {
        arrayOfInt1[j] = m;
      } else {
        i++;
      }
    }
    int[] arrayOfInt2 = new int[i];
    i = 0;
    for (int k = 0; k < arrayOfInt1.length; k++) {
      if (arrayOfInt1[k] == 0)
      {
        m = paramArrayOfInt[(paramInt1 + k)];
        arrayOfInt2[(i++)] = m;
      }
    }
    assert (i == arrayOfInt2.length);
    return new int[][] { arrayOfInt1, arrayOfInt2 };
  }
  
  private long[] makeSymtab()
  {
    long[] arrayOfLong = new long[fVlen];
    for (int i = 1; i <= fVlen; i++) {
      arrayOfLong[(i - 1)] = (fValues[i] << 32 | i);
    }
    Arrays.sort(arrayOfLong);
    return arrayOfLong;
  }
  
  private Coding getTailCoding(CodingMethod paramCodingMethod)
  {
    while ((paramCodingMethod instanceof AdaptiveCoding)) {
      paramCodingMethod = tailCoding;
    }
    return (Coding)paramCodingMethod;
  }
  
  public void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    int[][] arrayOfInt = encodeValues(paramArrayOfInt, paramInt1, paramInt2);
    writeSequencesTo(paramOutputStream, arrayOfInt[0], arrayOfInt[1]);
  }
  
  void writeSequencesTo(OutputStream paramOutputStream, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws IOException
  {
    favoredCoding.writeArrayTo(paramOutputStream, fValues, 1, 1 + fVlen);
    getTailCoding(favoredCoding).writeTo(paramOutputStream, computeSentinelValue());
    tokenCoding.writeArrayTo(paramOutputStream, paramArrayOfInt1, 0, paramArrayOfInt1.length);
    if (paramArrayOfInt2.length > 0) {
      unfavoredCoding.writeArrayTo(paramOutputStream, paramArrayOfInt2, 0, paramArrayOfInt2.length);
    }
  }
  
  int computeSentinelValue()
  {
    Coding localCoding = getTailCoding(favoredCoding);
    if (localCoding.isDelta()) {
      return 0;
    }
    int i = fValues[1];
    int j = i;
    for (int k = 2; k <= fVlen; k++)
    {
      j = fValues[k];
      i = moreCentral(i, j);
    }
    if (localCoding.getLength(i) <= localCoding.getLength(j)) {
      return i;
    }
    return j;
  }
  
  public void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws IOException
  {
    setFavoredValues(readFavoredValuesFrom(paramInputStream, paramInt2 - paramInt1));
    tokenCoding.readArrayFrom(paramInputStream, paramArrayOfInt, paramInt1, paramInt2);
    int i = 0;
    int j = -1;
    int k = 0;
    for (int m = paramInt1; m < paramInt2; m++)
    {
      n = paramArrayOfInt[m];
      if (n == 0)
      {
        if (j < 0) {
          i = m;
        } else {
          paramArrayOfInt[j] = m;
        }
        j = m;
        k++;
      }
      else
      {
        paramArrayOfInt[m] = fValues[n];
      }
    }
    int[] arrayOfInt = new int[k];
    if (k > 0) {
      unfavoredCoding.readArrayFrom(paramInputStream, arrayOfInt, 0, k);
    }
    for (int n = 0; n < k; n++)
    {
      int i1 = paramArrayOfInt[i];
      paramArrayOfInt[i] = arrayOfInt[n];
      i = i1;
    }
  }
  
  int[] readFavoredValuesFrom(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    int[] arrayOfInt = new int['Ϩ'];
    HashSet localHashSet = null;
    assert ((localHashSet = new HashSet()) != null);
    int i = 1;
    paramInt += i;
    int j = Integer.MIN_VALUE;
    int k = 0;
    int i2;
    for (CodingMethod localCodingMethod = favoredCoding; (localCodingMethod instanceof AdaptiveCoding); localCodingMethod = tailCoding)
    {
      localObject = (AdaptiveCoding)localCodingMethod;
      int m = headLength;
      while (i + m > arrayOfInt.length) {
        arrayOfInt = BandStructure.realloc(arrayOfInt);
      }
      int i1 = i + m;
      headCoding.readArrayFrom(paramInputStream, arrayOfInt, i, i1);
      while (i < i1)
      {
        i2 = arrayOfInt[(i++)];
        assert (localHashSet.add(Integer.valueOf(i2)));
        assert (i <= paramInt);
        k = i2;
        j = moreCentral(j, i2);
      }
    }
    Object localObject = (Coding)localCodingMethod;
    if (((Coding)localObject).isDelta())
    {
      long l = 0L;
      for (;;)
      {
        l += ((Coding)localObject).readFrom(paramInputStream);
        if (((Coding)localObject).isSubrange()) {
          i2 = ((Coding)localObject).reduceToUnsignedRange(l);
        } else {
          i2 = (int)l;
        }
        l = i2;
        if ((i > 1) && ((i2 == k) || (i2 == j))) {
          break;
        }
        if (i == arrayOfInt.length) {
          arrayOfInt = BandStructure.realloc(arrayOfInt);
        }
        arrayOfInt[(i++)] = i2;
        assert (localHashSet.add(Integer.valueOf(i2)));
        assert (i <= paramInt);
        k = i2;
        j = moreCentral(j, i2);
      }
    }
    else
    {
      for (;;)
      {
        int n = ((Coding)localObject).readFrom(paramInputStream);
        if ((i > 1) && ((n == k) || (n == j))) {
          break;
        }
        if (i == arrayOfInt.length) {
          arrayOfInt = BandStructure.realloc(arrayOfInt);
        }
        arrayOfInt[(i++)] = n;
        assert (localHashSet.add(Integer.valueOf(n)));
        assert (i <= paramInt);
        k = n;
        j = moreCentral(j, n);
      }
    }
    return BandStructure.realloc(arrayOfInt, i);
  }
  
  private static int moreCentral(int paramInt1, int paramInt2)
  {
    int i = paramInt1 >> 31 ^ paramInt1 << 1;
    int j = paramInt2 >> 31 ^ paramInt2 << 1;
    i -= Integer.MIN_VALUE;
    j -= Integer.MIN_VALUE;
    int k = i < j ? paramInt1 : paramInt2;
    assert (k == moreCentralSlow(paramInt1, paramInt2));
    return k;
  }
  
  private static int moreCentralSlow(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (i < 0) {
      i = -i;
    }
    if (i < 0) {
      return paramInt2;
    }
    int j = paramInt2;
    if (j < 0) {
      j = -j;
    }
    if (j < 0) {
      return paramInt1;
    }
    if (i < j) {
      return paramInt1;
    }
    if (i > j) {
      return paramInt2;
    }
    return paramInt1 < paramInt2 ? paramInt1 : paramInt2;
  }
  
  public byte[] getMetaCoding(Coding paramCoding)
  {
    int i = fVlen;
    int j = 0;
    if ((tokenCoding instanceof Coding))
    {
      localObject = (Coding)tokenCoding;
      if (((Coding)localObject).B() == 1)
      {
        j = 1;
      }
      else if (L >= 0)
      {
        assert (L == ((Coding)localObject).L());
        for (k = 1; k < LValuesCoded.length; k++) {
          if (LValuesCoded[k] == L)
          {
            j = k;
            break;
          }
        }
      }
    }
    Object localObject = null;
    if ((j != 0) && (tokenCoding == fitTokenCoding(fVlen, L))) {
      localObject = tokenCoding;
    }
    int k = favoredCoding == paramCoding ? 1 : 0;
    int m = (unfavoredCoding == paramCoding) || (unfavoredCoding == null) ? 1 : 0;
    int n = tokenCoding == localObject ? 1 : 0;
    int i1 = n == 1 ? j : 0;
    if (!$assertionsDisabled) {
      if (n != (i1 > 0 ? 1 : 0)) {
        throw new AssertionError();
      }
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(10);
    localByteArrayOutputStream.write(141 + k + 2 * m + 4 * i1);
    try
    {
      if (k == 0) {
        localByteArrayOutputStream.write(favoredCoding.getMetaCoding(paramCoding));
      }
      if (n == 0) {
        localByteArrayOutputStream.write(tokenCoding.getMetaCoding(paramCoding));
      }
      if (m == 0) {
        localByteArrayOutputStream.write(unfavoredCoding.getMetaCoding(paramCoding));
      }
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod)
  {
    int i = paramArrayOfByte[(paramInt++)] & 0xFF;
    if ((i < 141) || (i >= 189)) {
      return paramInt - 1;
    }
    i -= 141;
    int j = i % 2;
    int k = i / 2 % 2;
    int m = i / 4;
    int n = m > 0 ? 1 : 0;
    int i1 = LValuesCoded[m];
    CodingMethod[] arrayOfCodingMethod1 = { paramCoding };
    CodingMethod[] arrayOfCodingMethod2 = { null };
    CodingMethod[] arrayOfCodingMethod3 = { paramCoding };
    if (j == 0) {
      paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod1);
    }
    if (n == 0) {
      paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod2);
    }
    if (k == 0) {
      paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod3);
    }
    PopulationCoding localPopulationCoding = new PopulationCoding();
    L = i1;
    favoredCoding = arrayOfCodingMethod1[0];
    tokenCoding = arrayOfCodingMethod2[0];
    unfavoredCoding = arrayOfCodingMethod3[0];
    paramArrayOfCodingMethod[0] = localPopulationCoding;
    return paramInt;
  }
  
  private String keyString(CodingMethod paramCodingMethod)
  {
    if ((paramCodingMethod instanceof Coding)) {
      return ((Coding)paramCodingMethod).keyString();
    }
    if (paramCodingMethod == null) {
      return "none";
    }
    return paramCodingMethod.toString();
  }
  
  public String toString()
  {
    PropMap localPropMap = Utils.currentPropMap();
    int i = (localPropMap != null) && (localPropMap.getBoolean("com.sun.java.util.jar.pack.verbose.pop")) ? 1 : 0;
    StringBuilder localStringBuilder = new StringBuilder(100);
    localStringBuilder.append("pop(").append("fVlen=").append(fVlen);
    if ((i != 0) && (fValues != null))
    {
      localStringBuilder.append(" fV=[");
      for (int j = 1; j <= fVlen; j++) {
        localStringBuilder.append(j == 1 ? "" : ",").append(fValues[j]);
      }
      localStringBuilder.append(";").append(computeSentinelValue());
      localStringBuilder.append("]");
    }
    localStringBuilder.append(" fc=").append(keyString(favoredCoding));
    localStringBuilder.append(" tc=").append(keyString(tokenCoding));
    localStringBuilder.append(" uc=").append(keyString(unfavoredCoding));
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\PopulationCoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */