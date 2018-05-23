package sun.text.bidi;

import java.text.Bidi;
import java.util.Arrays;

public final class BidiLine
{
  public BidiLine() {}
  
  static void setTrailingWSStart(BidiBase paramBidiBase)
  {
    byte[] arrayOfByte1 = dirProps;
    byte[] arrayOfByte2 = levels;
    int i = length;
    int j = paraLevel;
    if (BidiBase.NoContextRTL(arrayOfByte1[(i - 1)]) == 7)
    {
      trailingWSStart = i;
      return;
    }
    while ((i > 0) && ((BidiBase.DirPropFlagNC(arrayOfByte1[(i - 1)]) & BidiBase.MASK_WS) != 0)) {
      i--;
    }
    while ((i > 0) && (arrayOfByte2[(i - 1)] == j)) {
      i--;
    }
    trailingWSStart = i;
  }
  
  public static Bidi setLine(Bidi paramBidi1, BidiBase paramBidiBase1, Bidi paramBidi2, BidiBase paramBidiBase2, int paramInt1, int paramInt2)
  {
    BidiBase localBidiBase = paramBidiBase2;
    int i = length = originalLength = resultLength = paramInt2 - paramInt1;
    text = new char[i];
    System.arraycopy(text, paramInt1, text, 0, i);
    paraLevel = paramBidiBase1.GetParaLevelAt(paramInt1);
    paraCount = paraCount;
    runs = new BidiRun[0];
    if (controlCount > 0)
    {
      for (int j = paramInt1; j < paramInt2; j++) {
        if (BidiBase.IsBidiControlChar(text[j])) {
          controlCount += 1;
        }
      }
      resultLength -= controlCount;
    }
    localBidiBase.getDirPropsMemory(i);
    dirProps = dirPropsMemory;
    System.arraycopy(dirProps, paramInt1, dirProps, 0, i);
    localBidiBase.getLevelsMemory(i);
    levels = levelsMemory;
    System.arraycopy(levels, paramInt1, levels, 0, i);
    runCount = -1;
    if (direction != 2)
    {
      direction = direction;
      if (trailingWSStart <= paramInt1) {
        trailingWSStart = 0;
      } else if (trailingWSStart < paramInt2) {
        trailingWSStart -= paramInt1;
      } else {
        trailingWSStart = i;
      }
    }
    else
    {
      byte[] arrayOfByte = levels;
      setTrailingWSStart(localBidiBase);
      int m = trailingWSStart;
      if (m == 0)
      {
        direction = ((byte)(paraLevel & 0x1));
      }
      else
      {
        byte b = (byte)(arrayOfByte[0] & 0x1);
        if ((m < i) && ((paraLevel & 0x1) != b)) {
          direction = 2;
        } else {
          for (int k = 1;; k++)
          {
            if (k == m)
            {
              direction = b;
              break;
            }
            if ((arrayOfByte[k] & 0x1) != b)
            {
              direction = 2;
              break;
            }
          }
        }
      }
      switch (direction)
      {
      case 0: 
        paraLevel = ((byte)(paraLevel + 1 & 0xFFFFFFFE));
        trailingWSStart = 0;
        break;
      case 1: 
        BidiBase tmp471_469 = localBidiBase;
        471469paraLevel = ((byte)(471469paraLevel | 0x1));
        trailingWSStart = 0;
        break;
      }
    }
    paraBidi = paramBidiBase1;
    return paramBidi2;
  }
  
  static byte getLevelAt(BidiBase paramBidiBase, int paramInt)
  {
    if ((direction != 2) || (paramInt >= trailingWSStart)) {
      return paramBidiBase.GetParaLevelAt(paramInt);
    }
    return levels[paramInt];
  }
  
  static byte[] getLevels(BidiBase paramBidiBase)
  {
    int i = trailingWSStart;
    int j = length;
    if (i != j)
    {
      Arrays.fill(levels, i, j, paraLevel);
      trailingWSStart = j;
    }
    if (j < levels.length)
    {
      byte[] arrayOfByte = new byte[j];
      System.arraycopy(levels, 0, arrayOfByte, 0, j);
      return arrayOfByte;
    }
    return levels;
  }
  
  static BidiRun getLogicalRun(BidiBase paramBidiBase, int paramInt)
  {
    BidiRun localBidiRun1 = new BidiRun();
    getRuns(paramBidiBase);
    int i = runCount;
    int j = 0;
    int k = 0;
    BidiRun localBidiRun2 = runs[0];
    for (int m = 0; m < i; m++)
    {
      localBidiRun2 = runs[m];
      k = start + limit - j;
      if ((paramInt >= start) && (paramInt < k)) {
        break;
      }
      j = limit;
    }
    start = start;
    limit = k;
    level = level;
    return localBidiRun1;
  }
  
  private static void getSingleRun(BidiBase paramBidiBase, byte paramByte)
  {
    runs = simpleRuns;
    runCount = 1;
    runs[0] = new BidiRun(0, length, paramByte);
  }
  
  private static void reorderLine(BidiBase paramBidiBase, byte paramByte1, byte paramByte2)
  {
    if (paramByte2 <= (paramByte1 | 0x1)) {
      return;
    }
    paramByte1 = (byte)(paramByte1 + 1);
    BidiRun[] arrayOfBidiRun = runs;
    byte[] arrayOfByte = levels;
    int m = runCount;
    if (trailingWSStart < length) {
      m--;
    }
    paramByte2 = (byte)(paramByte2 - 1);
    int i;
    BidiRun localBidiRun;
    if (paramByte2 >= paramByte1)
    {
      i = 0;
      for (;;)
      {
        if ((i < m) && (arrayOfByte[start] < paramByte2))
        {
          i++;
        }
        else
        {
          if (i >= m) {
            break;
          }
          int k = i;
          do
          {
            k++;
          } while ((k < m) && (arrayOfByte[start] >= paramByte2));
          for (int j = k - 1; i < j; j--)
          {
            localBidiRun = arrayOfBidiRun[i];
            arrayOfBidiRun[i] = arrayOfBidiRun[j];
            arrayOfBidiRun[j] = localBidiRun;
            i++;
          }
          if (k == m) {
            break;
          }
          i = k + 1;
        }
      }
    }
    if ((paramByte1 & 0x1) == 0)
    {
      i = 0;
      if (trailingWSStart == length) {
        m--;
      }
      while (i < m)
      {
        localBidiRun = arrayOfBidiRun[i];
        arrayOfBidiRun[i] = arrayOfBidiRun[m];
        arrayOfBidiRun[m] = localBidiRun;
        i++;
        m--;
      }
    }
  }
  
  static int getRunFromLogicalIndex(BidiBase paramBidiBase, int paramInt)
  {
    BidiRun[] arrayOfBidiRun = runs;
    int i = runCount;
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m = limit - j;
      int n = start;
      if ((paramInt >= n) && (paramInt < n + m)) {
        return k;
      }
      j += m;
    }
    throw new IllegalStateException("Internal ICU error in getRunFromLogicalIndex");
  }
  
  static void getRuns(BidiBase paramBidiBase)
  {
    if (runCount >= 0) {
      return;
    }
    int k;
    if (direction != 2)
    {
      getSingleRun(paramBidiBase, paraLevel);
    }
    else
    {
      int i = length;
      byte[] arrayOfByte = levels;
      byte b1 = 126;
      k = trailingWSStart;
      int i1 = 0;
      for (int n = 0; n < k; n++) {
        if (arrayOfByte[n] != b1)
        {
          i1++;
          b1 = arrayOfByte[n];
        }
      }
      if ((i1 == 1) && (k == i))
      {
        getSingleRun(paramBidiBase, arrayOfByte[0]);
      }
      else
      {
        byte b2 = 62;
        byte b3 = 0;
        if (k < i) {
          i1++;
        }
        paramBidiBase.getRunsMemory(i1);
        BidiRun[] arrayOfBidiRun = runsMemory;
        int i2 = 0;
        n = 0;
        do
        {
          int i3 = n;
          b1 = arrayOfByte[n];
          if (b1 < b2) {
            b2 = b1;
          }
          if (b1 > b3) {
            b3 = b1;
          }
          do
          {
            n++;
          } while ((n < k) && (arrayOfByte[n] == b1));
          arrayOfBidiRun[i2] = new BidiRun(i3, n - i3, b1);
          i2++;
        } while (n < k);
        if (k < i)
        {
          arrayOfBidiRun[i2] = new BidiRun(k, i - k, paraLevel);
          if (paraLevel < b2) {
            b2 = paraLevel;
          }
        }
        runs = arrayOfBidiRun;
        runCount = i1;
        reorderLine(paramBidiBase, b2, b3);
        k = 0;
        for (n = 0; n < i1; n++)
        {
          level = arrayOfByte[start];
          k = limit += k;
        }
        if (i2 < i1)
        {
          int i4 = (paraLevel & 0x1) != 0 ? 0 : i2;
          level = paraLevel;
        }
      }
    }
    int m;
    if (insertPoints.size > 0) {
      for (m = 0; m < insertPoints.size; m++)
      {
        BidiBase.Point localPoint = insertPoints.points[m];
        k = getRunFromLogicalIndex(paramBidiBase, pos);
        runs[k].insertRemove |= flag;
      }
    }
    if (controlCount > 0) {
      for (k = 0; k < length; k++)
      {
        m = text[k];
        if (BidiBase.IsBidiControlChar(m))
        {
          int j = getRunFromLogicalIndex(paramBidiBase, k);
          runs[j].insertRemove -= 1;
        }
      }
    }
  }
  
  static int[] prepareReorder(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte1.length <= 0)) {
      return null;
    }
    int k = 62;
    int m = 0;
    int i = paramArrayOfByte1.length;
    while (i > 0)
    {
      int j = paramArrayOfByte1[(--i)];
      if (j > 62) {
        return null;
      }
      if (j < k) {
        k = j;
      }
      if (j > m) {
        m = j;
      }
    }
    paramArrayOfByte2[0] = k;
    paramArrayOfByte3[0] = m;
    int[] arrayOfInt = new int[paramArrayOfByte1.length];
    i = paramArrayOfByte1.length;
    while (i > 0)
    {
      i--;
      arrayOfInt[i] = i;
    }
    return arrayOfInt;
  }
  
  static int[] reorderVisual(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = new byte[1];
    byte[] arrayOfByte2 = new byte[1];
    int[] arrayOfInt = prepareReorder(paramArrayOfByte, arrayOfByte1, arrayOfByte2);
    if (arrayOfInt == null) {
      return null;
    }
    int n = arrayOfByte1[0];
    int i1 = arrayOfByte2[0];
    if ((n == i1) && ((n & 0x1) == 0)) {
      return arrayOfInt;
    }
    n = (byte)(n | 0x1);
    do
    {
      int i = 0;
      for (;;)
      {
        if ((i < paramArrayOfByte.length) && (paramArrayOfByte[i] < i1))
        {
          i++;
        }
        else
        {
          if (i >= paramArrayOfByte.length) {
            break;
          }
          int k = i;
          do
          {
            k++;
          } while ((k < paramArrayOfByte.length) && (paramArrayOfByte[k] >= i1));
          for (int j = k - 1; i < j; j--)
          {
            int m = arrayOfInt[i];
            arrayOfInt[i] = arrayOfInt[j];
            arrayOfInt[j] = m;
            i++;
          }
          if (k == paramArrayOfByte.length) {
            break;
          }
          i = k + 1;
        }
      }
      i1 = (byte)(i1 - 1);
    } while (i1 >= n);
    return arrayOfInt;
  }
  
  static int[] getVisualMap(BidiBase paramBidiBase)
  {
    BidiRun[] arrayOfBidiRun = runs;
    int m = length > resultLength ? length : resultLength;
    int[] arrayOfInt1 = new int[m];
    int j = 0;
    int n = 0;
    int i;
    int k;
    for (int i1 = 0; i1 < runCount; i1++)
    {
      i = start;
      k = limit;
      if (arrayOfBidiRun[i1].isEvenRun())
      {
        do
        {
          arrayOfInt1[(n++)] = (i++);
          j++;
        } while (j < k);
      }
      else
      {
        i += k - j;
        do
        {
          arrayOfInt1[(n++)] = (--i);
          j++;
        } while (j < k);
      }
    }
    int i2;
    int i4;
    int i3;
    int i6;
    int i5;
    if (insertPoints.size > 0)
    {
      i1 = 0;
      i2 = runCount;
      arrayOfBidiRun = runs;
      for (i4 = 0; i4 < i2; i4++)
      {
        i3 = insertRemove;
        if ((i3 & 0x5) > 0) {
          i1++;
        }
        if ((i3 & 0xA) > 0) {
          i1++;
        }
      }
      i6 = resultLength;
      for (i4 = i2 - 1; (i4 >= 0) && (i1 > 0); i4--)
      {
        i3 = insertRemove;
        if ((i3 & 0xA) > 0)
        {
          arrayOfInt1[(--i6)] = -1;
          i1--;
        }
        j = i4 > 0 ? 1limit : 0;
        for (i5 = limit - 1; (i5 >= j) && (i1 > 0); i5--) {
          arrayOfInt1[(--i6)] = arrayOfInt1[i5];
        }
        if ((i3 & 0x5) > 0)
        {
          arrayOfInt1[(--i6)] = -1;
          i1--;
        }
      }
    }
    else if (controlCount > 0)
    {
      i1 = runCount;
      arrayOfBidiRun = runs;
      j = 0;
      int i7 = 0;
      i5 = 0;
      while (i5 < i1)
      {
        i4 = limit - j;
        i3 = insertRemove;
        if ((i3 == 0) && (i7 == j))
        {
          i7 += i4;
        }
        else
        {
          if (i3 == 0)
          {
            k = limit;
            for (i6 = j; i6 < k; i6++) {
              arrayOfInt1[(i7++)] = arrayOfInt1[i6];
            }
          }
          i = start;
          boolean bool = arrayOfBidiRun[i5].isEvenRun();
          i2 = i + i4 - 1;
          for (i6 = 0; i6 < i4; i6++)
          {
            int i8 = bool ? i + i6 : i2 - i6;
            int i9 = text[i8];
            if (!BidiBase.IsBidiControlChar(i9)) {
              arrayOfInt1[(i7++)] = i8;
            }
          }
        }
        i5++;
        j += i4;
      }
    }
    if (m == resultLength) {
      return arrayOfInt1;
    }
    int[] arrayOfInt2 = new int[resultLength];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, resultLength);
    return arrayOfInt2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\bidi\BidiLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */