package java.util;

class ComparableTimSort
{
  private static final int MIN_MERGE = 32;
  private final Object[] a;
  private static final int MIN_GALLOP = 7;
  private int minGallop = 7;
  private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
  private Object[] tmp;
  private int tmpBase;
  private int tmpLen;
  private int stackSize = 0;
  private final int[] runBase;
  private final int[] runLen;
  
  private ComparableTimSort(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt1, int paramInt2)
  {
    a = paramArrayOfObject1;
    int i = paramArrayOfObject1.length;
    int j = i < 512 ? i >>> 1 : 256;
    if ((paramArrayOfObject2 == null) || (paramInt2 < j) || (paramInt1 + j > paramArrayOfObject2.length))
    {
      tmp = new Object[j];
      tmpBase = 0;
      tmpLen = j;
    }
    else
    {
      tmp = paramArrayOfObject2;
      tmpBase = paramInt1;
      tmpLen = paramInt2;
    }
    int k = i < 119151 ? 24 : i < 1542 ? 10 : i < 120 ? 5 : 49;
    runBase = new int[k];
    runLen = new int[k];
  }
  
  static void sort(Object[] paramArrayOfObject1, int paramInt1, int paramInt2, Object[] paramArrayOfObject2, int paramInt3, int paramInt4)
  {
    assert ((paramArrayOfObject1 != null) && (paramInt1 >= 0) && (paramInt1 <= paramInt2) && (paramInt2 <= paramArrayOfObject1.length));
    int i = paramInt2 - paramInt1;
    if (i < 2) {
      return;
    }
    if (i < 32)
    {
      int j = countRunAndMakeAscending(paramArrayOfObject1, paramInt1, paramInt2);
      binarySort(paramArrayOfObject1, paramInt1, paramInt2, paramInt1 + j);
      return;
    }
    ComparableTimSort localComparableTimSort = new ComparableTimSort(paramArrayOfObject1, paramArrayOfObject2, paramInt3, paramInt4);
    int k = minRunLength(i);
    do
    {
      int m = countRunAndMakeAscending(paramArrayOfObject1, paramInt1, paramInt2);
      if (m < k)
      {
        int n = i <= k ? i : k;
        binarySort(paramArrayOfObject1, paramInt1, paramInt1 + n, paramInt1 + m);
        m = n;
      }
      localComparableTimSort.pushRun(paramInt1, m);
      localComparableTimSort.mergeCollapse();
      paramInt1 += m;
      i -= m;
    } while (i != 0);
    assert (paramInt1 == paramInt2);
    localComparableTimSort.mergeForceCollapse();
    assert (stackSize == 1);
  }
  
  private static void binarySort(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
  {
    assert ((paramInt1 <= paramInt3) && (paramInt3 <= paramInt2));
    if (paramInt3 == paramInt1) {
      paramInt3++;
    }
    while (paramInt3 < paramInt2)
    {
      Comparable localComparable = (Comparable)paramArrayOfObject[paramInt3];
      int i = paramInt1;
      int j = paramInt3;
      assert (i <= j);
      while (i < j)
      {
        k = i + j >>> 1;
        if (localComparable.compareTo(paramArrayOfObject[k]) < 0) {
          j = k;
        } else {
          i = k + 1;
        }
      }
      assert (i == j);
      int k = paramInt3 - i;
      switch (k)
      {
      case 2: 
        paramArrayOfObject[(i + 2)] = paramArrayOfObject[(i + 1)];
      case 1: 
        paramArrayOfObject[(i + 1)] = paramArrayOfObject[i];
        break;
      default: 
        System.arraycopy(paramArrayOfObject, i, paramArrayOfObject, i + 1, k);
      }
      paramArrayOfObject[i] = localComparable;
      paramInt3++;
    }
  }
  
  private static int countRunAndMakeAscending(Object[] paramArrayOfObject, int paramInt1, int paramInt2)
  {
    assert (paramInt1 < paramInt2);
    int i = paramInt1 + 1;
    if (i == paramInt2) {
      return 1;
    }
    if (((Comparable)paramArrayOfObject[(i++)]).compareTo(paramArrayOfObject[paramInt1]) < 0)
    {
      while ((i < paramInt2) && (((Comparable)paramArrayOfObject[i]).compareTo(paramArrayOfObject[(i - 1)]) < 0)) {
        i++;
      }
      reverseRange(paramArrayOfObject, paramInt1, i);
    }
    else
    {
      while ((i < paramInt2) && (((Comparable)paramArrayOfObject[i]).compareTo(paramArrayOfObject[(i - 1)]) >= 0)) {
        i++;
      }
    }
    return i - paramInt1;
  }
  
  private static void reverseRange(Object[] paramArrayOfObject, int paramInt1, int paramInt2)
  {
    
    while (paramInt1 < paramInt2)
    {
      Object localObject = paramArrayOfObject[paramInt1];
      paramArrayOfObject[(paramInt1++)] = paramArrayOfObject[paramInt2];
      paramArrayOfObject[(paramInt2--)] = localObject;
    }
  }
  
  private static int minRunLength(int paramInt)
  {
    assert (paramInt >= 0);
    int i = 0;
    while (paramInt >= 32)
    {
      i |= paramInt & 0x1;
      paramInt >>= 1;
    }
    return paramInt + i;
  }
  
  private void pushRun(int paramInt1, int paramInt2)
  {
    runBase[stackSize] = paramInt1;
    runLen[stackSize] = paramInt2;
    stackSize += 1;
  }
  
  private void mergeCollapse()
  {
    while (stackSize > 1)
    {
      int i = stackSize - 2;
      if ((i > 0) && (runLen[(i - 1)] <= runLen[i] + runLen[(i + 1)]))
      {
        if (runLen[(i - 1)] < runLen[(i + 1)]) {
          i--;
        }
        mergeAt(i);
      }
      else
      {
        if (runLen[i] > runLen[(i + 1)]) {
          break;
        }
        mergeAt(i);
      }
    }
  }
  
  private void mergeForceCollapse()
  {
    while (stackSize > 1)
    {
      int i = stackSize - 2;
      if ((i > 0) && (runLen[(i - 1)] < runLen[(i + 1)])) {
        i--;
      }
      mergeAt(i);
    }
  }
  
  private void mergeAt(int paramInt)
  {
    assert (stackSize >= 2);
    assert (paramInt >= 0);
    assert ((paramInt == stackSize - 2) || (paramInt == stackSize - 3));
    int i = runBase[paramInt];
    int j = runLen[paramInt];
    int k = runBase[(paramInt + 1)];
    int m = runLen[(paramInt + 1)];
    assert ((j > 0) && (m > 0));
    assert (i + j == k);
    runLen[paramInt] = (j + m);
    if (paramInt == stackSize - 3)
    {
      runBase[(paramInt + 1)] = runBase[(paramInt + 2)];
      runLen[(paramInt + 1)] = runLen[(paramInt + 2)];
    }
    stackSize -= 1;
    int n = gallopRight((Comparable)a[k], a, i, j, 0);
    assert (n >= 0);
    i += n;
    j -= n;
    if (j == 0) {
      return;
    }
    m = gallopLeft((Comparable)a[(i + j - 1)], a, k, m, m - 1);
    assert (m >= 0);
    if (m == 0) {
      return;
    }
    if (j <= m) {
      mergeLo(i, j, k, m);
    } else {
      mergeHi(i, j, k, m);
    }
  }
  
  private static int gallopLeft(Comparable<Object> paramComparable, Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
  {
    assert ((paramInt2 > 0) && (paramInt3 >= 0) && (paramInt3 < paramInt2));
    int i = 0;
    int j = 1;
    int k;
    if (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + paramInt3)]) > 0)
    {
      k = paramInt2 - paramInt3;
      while ((j < k) && (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + paramInt3 + j)]) > 0))
      {
        i = j;
        j = (j << 1) + 1;
        if (j <= 0) {
          j = k;
        }
      }
      if (j > k) {
        j = k;
      }
      i += paramInt3;
      j += paramInt3;
    }
    else
    {
      k = paramInt3 + 1;
      while ((j < k) && (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + paramInt3 - j)]) <= 0))
      {
        i = j;
        j = (j << 1) + 1;
        if (j <= 0) {
          j = k;
        }
      }
      if (j > k) {
        j = k;
      }
      int m = i;
      i = paramInt3 - j;
      j = paramInt3 - m;
    }
    assert ((-1 <= i) && (i < j) && (j <= paramInt2));
    i++;
    while (i < j)
    {
      k = i + (j - i >>> 1);
      if (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + k)]) > 0) {
        i = k + 1;
      } else {
        j = k;
      }
    }
    assert (i == j);
    return j;
  }
  
  private static int gallopRight(Comparable<Object> paramComparable, Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
  {
    assert ((paramInt2 > 0) && (paramInt3 >= 0) && (paramInt3 < paramInt2));
    int i = 1;
    int j = 0;
    int k;
    if (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + paramInt3)]) < 0)
    {
      k = paramInt3 + 1;
      while ((i < k) && (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + paramInt3 - i)]) < 0))
      {
        j = i;
        i = (i << 1) + 1;
        if (i <= 0) {
          i = k;
        }
      }
      if (i > k) {
        i = k;
      }
      int m = j;
      j = paramInt3 - i;
      i = paramInt3 - m;
    }
    else
    {
      k = paramInt2 - paramInt3;
      while ((i < k) && (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + paramInt3 + i)]) >= 0))
      {
        j = i;
        i = (i << 1) + 1;
        if (i <= 0) {
          i = k;
        }
      }
      if (i > k) {
        i = k;
      }
      j += paramInt3;
      i += paramInt3;
    }
    assert ((-1 <= j) && (j < i) && (i <= paramInt2));
    j++;
    while (j < i)
    {
      k = j + (i - j >>> 1);
      if (paramComparable.compareTo(paramArrayOfObject[(paramInt1 + k)]) < 0) {
        i = k;
      } else {
        j = k + 1;
      }
    }
    assert (j == i);
    return i;
  }
  
  private void mergeLo(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assert ((paramInt2 > 0) && (paramInt4 > 0) && (paramInt1 + paramInt2 == paramInt3));
    Object[] arrayOfObject1 = a;
    Object[] arrayOfObject2 = ensureCapacity(paramInt2);
    int i = tmpBase;
    int j = paramInt3;
    int k = paramInt1;
    System.arraycopy(arrayOfObject1, paramInt1, arrayOfObject2, i, paramInt2);
    arrayOfObject1[(k++)] = arrayOfObject1[(j++)];
    paramInt4--;
    if (paramInt4 == 0)
    {
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, k, paramInt2);
      return;
    }
    if (paramInt2 == 1)
    {
      System.arraycopy(arrayOfObject1, j, arrayOfObject1, k, paramInt4);
      arrayOfObject1[(k + paramInt4)] = arrayOfObject2[i];
      return;
    }
    for (int m = minGallop;; m += 2)
    {
      int n = 0;
      int i1 = 0;
      do
      {
        assert ((paramInt2 > 1) && (paramInt4 > 0));
        if (((Comparable)arrayOfObject1[j]).compareTo(arrayOfObject2[i]) < 0)
        {
          arrayOfObject1[(k++)] = arrayOfObject1[(j++)];
          i1++;
          n = 0;
          paramInt4--;
          if (paramInt4 == 0) {
            break;
          }
        }
        else
        {
          arrayOfObject1[(k++)] = arrayOfObject2[(i++)];
          n++;
          i1 = 0;
          paramInt2--;
          if (paramInt2 == 1) {
            break;
          }
        }
      } while ((n | i1) < m);
      do
      {
        assert ((paramInt2 > 1) && (paramInt4 > 0));
        n = gallopRight((Comparable)arrayOfObject1[j], arrayOfObject2, i, paramInt2, 0);
        if (n != 0)
        {
          System.arraycopy(arrayOfObject2, i, arrayOfObject1, k, n);
          k += n;
          i += n;
          paramInt2 -= n;
          if (paramInt2 <= 1) {
            break;
          }
        }
        arrayOfObject1[(k++)] = arrayOfObject1[(j++)];
        paramInt4--;
        if (paramInt4 == 0) {
          break;
        }
        i1 = gallopLeft((Comparable)arrayOfObject2[i], arrayOfObject1, j, paramInt4, 0);
        if (i1 != 0)
        {
          System.arraycopy(arrayOfObject1, j, arrayOfObject1, k, i1);
          k += i1;
          j += i1;
          paramInt4 -= i1;
          if (paramInt4 == 0) {
            break;
          }
        }
        arrayOfObject1[(k++)] = arrayOfObject2[(i++)];
        paramInt2--;
        if (paramInt2 == 1) {
          break;
        }
        m--;
      } while (((n >= 7 ? 1 : 0) | (i1 >= 7 ? 1 : 0)) != 0);
      if (m < 0) {
        m = 0;
      }
    }
    minGallop = (m < 1 ? 1 : m);
    if (paramInt2 == 1)
    {
      assert (paramInt4 > 0);
      System.arraycopy(arrayOfObject1, j, arrayOfObject1, k, paramInt4);
      arrayOfObject1[(k + paramInt4)] = arrayOfObject2[i];
    }
    else
    {
      if (paramInt2 == 0) {
        throw new IllegalArgumentException("Comparison method violates its general contract!");
      }
      assert (paramInt4 == 0);
      assert (paramInt2 > 1);
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, k, paramInt2);
    }
  }
  
  private void mergeHi(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assert ((paramInt2 > 0) && (paramInt4 > 0) && (paramInt1 + paramInt2 == paramInt3));
    Object[] arrayOfObject1 = a;
    Object[] arrayOfObject2 = ensureCapacity(paramInt4);
    int i = tmpBase;
    System.arraycopy(arrayOfObject1, paramInt3, arrayOfObject2, i, paramInt4);
    int j = paramInt1 + paramInt2 - 1;
    int k = i + paramInt4 - 1;
    int m = paramInt3 + paramInt4 - 1;
    arrayOfObject1[(m--)] = arrayOfObject1[(j--)];
    paramInt2--;
    if (paramInt2 == 0)
    {
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, m - (paramInt4 - 1), paramInt4);
      return;
    }
    if (paramInt4 == 1)
    {
      m -= paramInt2;
      j -= paramInt2;
      System.arraycopy(arrayOfObject1, j + 1, arrayOfObject1, m + 1, paramInt2);
      arrayOfObject1[m] = arrayOfObject2[k];
      return;
    }
    for (int n = minGallop;; n += 2)
    {
      int i1 = 0;
      int i2 = 0;
      do
      {
        assert ((paramInt2 > 0) && (paramInt4 > 1));
        if (((Comparable)arrayOfObject2[k]).compareTo(arrayOfObject1[j]) < 0)
        {
          arrayOfObject1[(m--)] = arrayOfObject1[(j--)];
          i1++;
          i2 = 0;
          paramInt2--;
          if (paramInt2 == 0) {
            break;
          }
        }
        else
        {
          arrayOfObject1[(m--)] = arrayOfObject2[(k--)];
          i2++;
          i1 = 0;
          paramInt4--;
          if (paramInt4 == 1) {
            break;
          }
        }
      } while ((i1 | i2) < n);
      do
      {
        assert ((paramInt2 > 0) && (paramInt4 > 1));
        i1 = paramInt2 - gallopRight((Comparable)arrayOfObject2[k], arrayOfObject1, paramInt1, paramInt2, paramInt2 - 1);
        if (i1 != 0)
        {
          m -= i1;
          j -= i1;
          paramInt2 -= i1;
          System.arraycopy(arrayOfObject1, j + 1, arrayOfObject1, m + 1, i1);
          if (paramInt2 == 0) {
            break;
          }
        }
        arrayOfObject1[(m--)] = arrayOfObject2[(k--)];
        paramInt4--;
        if (paramInt4 == 1) {
          break;
        }
        i2 = paramInt4 - gallopLeft((Comparable)arrayOfObject1[j], arrayOfObject2, i, paramInt4, paramInt4 - 1);
        if (i2 != 0)
        {
          m -= i2;
          k -= i2;
          paramInt4 -= i2;
          System.arraycopy(arrayOfObject2, k + 1, arrayOfObject1, m + 1, i2);
          if (paramInt4 <= 1) {
            break;
          }
        }
        arrayOfObject1[(m--)] = arrayOfObject1[(j--)];
        paramInt2--;
        if (paramInt2 == 0) {
          break;
        }
        n--;
      } while (((i1 >= 7 ? 1 : 0) | (i2 >= 7 ? 1 : 0)) != 0);
      if (n < 0) {
        n = 0;
      }
    }
    minGallop = (n < 1 ? 1 : n);
    if (paramInt4 == 1)
    {
      assert (paramInt2 > 0);
      m -= paramInt2;
      j -= paramInt2;
      System.arraycopy(arrayOfObject1, j + 1, arrayOfObject1, m + 1, paramInt2);
      arrayOfObject1[m] = arrayOfObject2[k];
    }
    else
    {
      if (paramInt4 == 0) {
        throw new IllegalArgumentException("Comparison method violates its general contract!");
      }
      assert (paramInt2 == 0);
      assert (paramInt4 > 0);
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, m - (paramInt4 - 1), paramInt4);
    }
  }
  
  private Object[] ensureCapacity(int paramInt)
  {
    if (tmpLen < paramInt)
    {
      int i = paramInt;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      i++;
      if (i < 0) {
        i = paramInt;
      } else {
        i = Math.min(i, a.length >>> 1);
      }
      Object[] arrayOfObject = new Object[i];
      tmp = arrayOfObject;
      tmpLen = i;
      tmpBase = 0;
    }
    return tmp;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ComparableTimSort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */