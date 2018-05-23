package java.util;

final class DualPivotQuicksort
{
  private static final int MAX_RUN_COUNT = 67;
  private static final int MAX_RUN_LENGTH = 33;
  private static final int QUICKSORT_THRESHOLD = 286;
  private static final int INSERTION_SORT_THRESHOLD = 47;
  private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 29;
  private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200;
  private static final int NUM_SHORT_VALUES = 65536;
  private static final int NUM_CHAR_VALUES = 65536;
  private static final int NUM_BYTE_VALUES = 256;
  
  private DualPivotQuicksort() {}
  
  static void sort(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 < 286)
    {
      sort(paramArrayOfInt1, paramInt1, paramInt2, true);
      return;
    }
    int[] arrayOfInt1 = new int[68];
    int i = 0;
    arrayOfInt1[0] = paramInt1;
    int j = paramInt1;
    int m;
    int n;
    while (j < paramInt2)
    {
      if (paramArrayOfInt1[j] < paramArrayOfInt1[(j + 1)]) {
        for (;;)
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfInt1[(j - 1)] > paramArrayOfInt1[j])) {
            break;
          }
        }
      }
      if (paramArrayOfInt1[j] > paramArrayOfInt1[(j + 1)])
      {
        do
        {
          j++;
        } while ((j <= paramInt2) && (paramArrayOfInt1[(j - 1)] >= paramArrayOfInt1[j]));
        k = arrayOfInt1[i] - 1;
        m = j;
        for (;;)
        {
          k++;
          if (k >= --m) {
            break;
          }
          n = paramArrayOfInt1[k];
          paramArrayOfInt1[k] = paramArrayOfInt1[m];
          paramArrayOfInt1[m] = n;
        }
      }
      else
      {
        k = 33;
        do
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfInt1[(j - 1)] != paramArrayOfInt1[j])) {
            break;
          }
          k--;
        } while (k != 0);
        sort(paramArrayOfInt1, paramInt1, paramInt2, true);
        return;
      }
      i++;
      if (i == 67)
      {
        sort(paramArrayOfInt1, paramInt1, paramInt2, true);
        return;
      }
      arrayOfInt1[i] = j;
    }
    if (arrayOfInt1[i] == paramInt2++) {
      arrayOfInt1[(++i)] = paramInt2;
    } else if (i == 1) {
      return;
    }
    j = 0;
    int k = 1;
    while (k <<= 1 < i) {
      j = (byte)(j ^ 0x1);
    }
    int i1 = paramInt2 - paramInt1;
    if ((paramArrayOfInt2 == null) || (paramInt4 < i1) || (paramInt3 + i1 > paramArrayOfInt2.length))
    {
      paramArrayOfInt2 = new int[i1];
      paramInt3 = 0;
    }
    Object localObject;
    if (j == 0)
    {
      System.arraycopy(paramArrayOfInt1, paramInt1, paramArrayOfInt2, paramInt3, i1);
      localObject = paramArrayOfInt1;
      n = 0;
      paramArrayOfInt1 = paramArrayOfInt2;
      m = paramInt3 - paramInt1;
    }
    else
    {
      localObject = paramArrayOfInt2;
      m = 0;
      n = paramInt3 - paramInt1;
    }
    while (i > 1)
    {
      int i2;
      for (int i3 = (i2 = 0) + 2; i3 <= i; i3 += 2)
      {
        i4 = arrayOfInt1[i3];
        int i5 = arrayOfInt1[(i3 - 1)];
        int i6 = arrayOfInt1[(i3 - 2)];
        int i7 = i6;
        int i8 = i5;
        while (i6 < i4)
        {
          if ((i8 >= i4) || ((i7 < i5) && (paramArrayOfInt1[(i7 + m)] <= paramArrayOfInt1[(i8 + m)]))) {
            localObject[(i6 + n)] = paramArrayOfInt1[(i7++ + m)];
          } else {
            localObject[(i6 + n)] = paramArrayOfInt1[(i8++ + m)];
          }
          i6++;
        }
        arrayOfInt1[(++i2)] = i4;
      }
      if ((i & 0x1) != 0)
      {
        i3 = paramInt2;
        i4 = arrayOfInt1[(i - 1)];
        for (;;)
        {
          i3--;
          if (i3 < i4) {
            break;
          }
          localObject[(i3 + n)] = paramArrayOfInt1[(i3 + m)];
        }
        arrayOfInt1[(++i2)] = paramInt2;
      }
      int[] arrayOfInt2 = paramArrayOfInt1;
      paramArrayOfInt1 = (int[])localObject;
      localObject = arrayOfInt2;
      int i4 = m;
      m = n;
      n = i4;
      i = i2;
    }
  }
  
  private static void sort(int[] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47)
    {
      if (paramBoolean)
      {
        j = paramInt1;
        for (k = j; j < paramInt2; k = j)
        {
          m = paramArrayOfInt[(j + 1)];
          while (m < paramArrayOfInt[k])
          {
            paramArrayOfInt[(k + 1)] = paramArrayOfInt[k];
            if (k-- == paramInt1) {
              break;
            }
          }
          paramArrayOfInt[(k + 1)] = m;
          j++;
        }
      }
      else
      {
        do
        {
          if (paramInt1 >= paramInt2) {
            return;
          }
        } while (paramArrayOfInt[(++paramInt1)] >= paramArrayOfInt[(paramInt1 - 1)]);
        for (j = paramInt1;; j = paramInt1)
        {
          paramInt1++;
          if (paramInt1 > paramInt2) {
            break;
          }
          k = paramArrayOfInt[j];
          m = paramArrayOfInt[paramInt1];
          if (k < m)
          {
            m = k;
            k = paramArrayOfInt[paramInt1];
          }
          while (k < paramArrayOfInt[(--j)]) {
            paramArrayOfInt[(j + 2)] = paramArrayOfInt[j];
          }
          paramArrayOfInt[(++j + 1)] = k;
          while (m < paramArrayOfInt[(--j)]) {
            paramArrayOfInt[(j + 1)] = paramArrayOfInt[j];
          }
          paramArrayOfInt[(j + 1)] = m;
          paramInt1++;
        }
        j = paramArrayOfInt[paramInt2];
        while (j < paramArrayOfInt[(--paramInt2)]) {
          paramArrayOfInt[(paramInt2 + 1)] = paramArrayOfInt[paramInt2];
        }
        paramArrayOfInt[(paramInt2 + 1)] = j;
      }
      return;
    }
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfInt[m] < paramArrayOfInt[n])
    {
      i3 = paramArrayOfInt[m];
      paramArrayOfInt[m] = paramArrayOfInt[n];
      paramArrayOfInt[n] = i3;
    }
    if (paramArrayOfInt[k] < paramArrayOfInt[m])
    {
      i3 = paramArrayOfInt[k];
      paramArrayOfInt[k] = paramArrayOfInt[m];
      paramArrayOfInt[m] = i3;
      if (i3 < paramArrayOfInt[n])
      {
        paramArrayOfInt[m] = paramArrayOfInt[n];
        paramArrayOfInt[n] = i3;
      }
    }
    if (paramArrayOfInt[i1] < paramArrayOfInt[k])
    {
      i3 = paramArrayOfInt[i1];
      paramArrayOfInt[i1] = paramArrayOfInt[k];
      paramArrayOfInt[k] = i3;
      if (i3 < paramArrayOfInt[m])
      {
        paramArrayOfInt[k] = paramArrayOfInt[m];
        paramArrayOfInt[m] = i3;
        if (i3 < paramArrayOfInt[n])
        {
          paramArrayOfInt[m] = paramArrayOfInt[n];
          paramArrayOfInt[n] = i3;
        }
      }
    }
    if (paramArrayOfInt[i2] < paramArrayOfInt[i1])
    {
      i3 = paramArrayOfInt[i2];
      paramArrayOfInt[i2] = paramArrayOfInt[i1];
      paramArrayOfInt[i1] = i3;
      if (i3 < paramArrayOfInt[k])
      {
        paramArrayOfInt[i1] = paramArrayOfInt[k];
        paramArrayOfInt[k] = i3;
        if (i3 < paramArrayOfInt[m])
        {
          paramArrayOfInt[k] = paramArrayOfInt[m];
          paramArrayOfInt[m] = i3;
          if (i3 < paramArrayOfInt[n])
          {
            paramArrayOfInt[m] = paramArrayOfInt[n];
            paramArrayOfInt[n] = i3;
          }
        }
      }
    }
    int i3 = paramInt1;
    int i4 = paramInt2;
    int i5;
    int i6;
    int i7;
    if ((paramArrayOfInt[n] != paramArrayOfInt[m]) && (paramArrayOfInt[m] != paramArrayOfInt[k]) && (paramArrayOfInt[k] != paramArrayOfInt[i1]) && (paramArrayOfInt[i1] != paramArrayOfInt[i2]))
    {
      i5 = paramArrayOfInt[m];
      i6 = paramArrayOfInt[i1];
      paramArrayOfInt[m] = paramArrayOfInt[paramInt1];
      paramArrayOfInt[i1] = paramArrayOfInt[paramInt2];
      while (paramArrayOfInt[(++i3)] < i5) {}
      while (paramArrayOfInt[(--i4)] > i6) {}
      i7 = i3 - 1;
      int i8;
      for (;;)
      {
        i7++;
        if (i7 > i4) {
          break;
        }
        i8 = paramArrayOfInt[i7];
        if (i8 < i5)
        {
          paramArrayOfInt[i7] = paramArrayOfInt[i3];
          paramArrayOfInt[i3] = i8;
          i3++;
        }
        else if (i8 > i6)
        {
          while (paramArrayOfInt[i4] > i6) {
            if (i4-- == i7) {
              break label808;
            }
          }
          if (paramArrayOfInt[i4] < i5)
          {
            paramArrayOfInt[i7] = paramArrayOfInt[i3];
            paramArrayOfInt[i3] = paramArrayOfInt[i4];
            i3++;
          }
          else
          {
            paramArrayOfInt[i7] = paramArrayOfInt[i4];
          }
          paramArrayOfInt[i4] = i8;
          i4--;
        }
      }
      label808:
      paramArrayOfInt[paramInt1] = paramArrayOfInt[(i3 - 1)];
      paramArrayOfInt[(i3 - 1)] = i5;
      paramArrayOfInt[paramInt2] = paramArrayOfInt[(i4 + 1)];
      paramArrayOfInt[(i4 + 1)] = i6;
      sort(paramArrayOfInt, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfInt, i4 + 2, paramInt2, false);
      if ((i3 < n) && (i2 < i4))
      {
        while (paramArrayOfInt[i3] == i5) {
          i3++;
        }
        while (paramArrayOfInt[i4] == i6) {
          i4--;
        }
        i7 = i3 - 1;
        for (;;)
        {
          i7++;
          if (i7 > i4) {
            break;
          }
          i8 = paramArrayOfInt[i7];
          if (i8 == i5)
          {
            paramArrayOfInt[i7] = paramArrayOfInt[i3];
            paramArrayOfInt[i3] = i8;
            i3++;
          }
          else if (i8 == i6)
          {
            while (paramArrayOfInt[i4] == i6) {
              if (i4-- == i7) {
                break label1033;
              }
            }
            if (paramArrayOfInt[i4] == i5)
            {
              paramArrayOfInt[i7] = paramArrayOfInt[i3];
              paramArrayOfInt[i3] = i5;
              i3++;
            }
            else
            {
              paramArrayOfInt[i7] = paramArrayOfInt[i4];
            }
            paramArrayOfInt[i4] = i8;
            i4--;
          }
        }
      }
      label1033:
      sort(paramArrayOfInt, i3, i4, false);
    }
    else
    {
      i5 = paramArrayOfInt[k];
      for (i6 = i3; i6 <= i4; i6++) {
        if (paramArrayOfInt[i6] != i5)
        {
          i7 = paramArrayOfInt[i6];
          if (i7 < i5)
          {
            paramArrayOfInt[i6] = paramArrayOfInt[i3];
            paramArrayOfInt[i3] = i7;
            i3++;
          }
          else
          {
            while (paramArrayOfInt[i4] > i5) {
              i4--;
            }
            if (paramArrayOfInt[i4] < i5)
            {
              paramArrayOfInt[i6] = paramArrayOfInt[i3];
              paramArrayOfInt[i3] = paramArrayOfInt[i4];
              i3++;
            }
            else
            {
              paramArrayOfInt[i6] = i5;
            }
            paramArrayOfInt[i4] = i7;
            i4--;
          }
        }
      }
      sort(paramArrayOfInt, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfInt, i4 + 1, paramInt2, false);
    }
  }
  
  static void sort(long[] paramArrayOfLong1, int paramInt1, int paramInt2, long[] paramArrayOfLong2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 < 286)
    {
      sort(paramArrayOfLong1, paramInt1, paramInt2, true);
      return;
    }
    int[] arrayOfInt = new int[68];
    int i = 0;
    arrayOfInt[0] = paramInt1;
    int j = paramInt1;
    int m;
    while (j < paramInt2)
    {
      if (paramArrayOfLong1[j] < paramArrayOfLong1[(j + 1)]) {
        for (;;)
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfLong1[(j - 1)] > paramArrayOfLong1[j])) {
            break;
          }
        }
      }
      if (paramArrayOfLong1[j] > paramArrayOfLong1[(j + 1)])
      {
        do
        {
          j++;
        } while ((j <= paramInt2) && (paramArrayOfLong1[(j - 1)] >= paramArrayOfLong1[j]));
        k = arrayOfInt[i] - 1;
        m = j;
        for (;;)
        {
          k++;
          if (k >= --m) {
            break;
          }
          long l = paramArrayOfLong1[k];
          paramArrayOfLong1[k] = paramArrayOfLong1[m];
          paramArrayOfLong1[m] = l;
        }
      }
      else
      {
        k = 33;
        do
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfLong1[(j - 1)] != paramArrayOfLong1[j])) {
            break;
          }
          k--;
        } while (k != 0);
        sort(paramArrayOfLong1, paramInt1, paramInt2, true);
        return;
      }
      i++;
      if (i == 67)
      {
        sort(paramArrayOfLong1, paramInt1, paramInt2, true);
        return;
      }
      arrayOfInt[i] = j;
    }
    if (arrayOfInt[i] == paramInt2++) {
      arrayOfInt[(++i)] = paramInt2;
    } else if (i == 1) {
      return;
    }
    j = 0;
    int k = 1;
    while (k <<= 1 < i) {
      j = (byte)(j ^ 0x1);
    }
    int i1 = paramInt2 - paramInt1;
    if ((paramArrayOfLong2 == null) || (paramInt4 < i1) || (paramInt3 + i1 > paramArrayOfLong2.length))
    {
      paramArrayOfLong2 = new long[i1];
      paramInt3 = 0;
    }
    Object localObject;
    int n;
    if (j == 0)
    {
      System.arraycopy(paramArrayOfLong1, paramInt1, paramArrayOfLong2, paramInt3, i1);
      localObject = paramArrayOfLong1;
      n = 0;
      paramArrayOfLong1 = paramArrayOfLong2;
      m = paramInt3 - paramInt1;
    }
    else
    {
      localObject = paramArrayOfLong2;
      m = 0;
      n = paramInt3 - paramInt1;
    }
    while (i > 1)
    {
      int i2;
      for (int i3 = (i2 = 0) + 2; i3 <= i; i3 += 2)
      {
        i4 = arrayOfInt[i3];
        int i5 = arrayOfInt[(i3 - 1)];
        int i6 = arrayOfInt[(i3 - 2)];
        int i7 = i6;
        int i8 = i5;
        while (i6 < i4)
        {
          if ((i8 >= i4) || ((i7 < i5) && (paramArrayOfLong1[(i7 + m)] <= paramArrayOfLong1[(i8 + m)]))) {
            localObject[(i6 + n)] = paramArrayOfLong1[(i7++ + m)];
          } else {
            localObject[(i6 + n)] = paramArrayOfLong1[(i8++ + m)];
          }
          i6++;
        }
        arrayOfInt[(++i2)] = i4;
      }
      if ((i & 0x1) != 0)
      {
        i3 = paramInt2;
        i4 = arrayOfInt[(i - 1)];
        for (;;)
        {
          i3--;
          if (i3 < i4) {
            break;
          }
          localObject[(i3 + n)] = paramArrayOfLong1[(i3 + m)];
        }
        arrayOfInt[(++i2)] = paramInt2;
      }
      long[] arrayOfLong = paramArrayOfLong1;
      paramArrayOfLong1 = (long[])localObject;
      localObject = arrayOfLong;
      int i4 = m;
      m = n;
      n = i4;
      i = i2;
    }
  }
  
  private static void sort(long[] paramArrayOfLong, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47)
    {
      int j;
      if (paramBoolean)
      {
        j = paramInt1;
        for (int m = j; j < paramInt2; m = j)
        {
          long l3 = paramArrayOfLong[(j + 1)];
          while (l3 < paramArrayOfLong[m])
          {
            paramArrayOfLong[(m + 1)] = paramArrayOfLong[m];
            if (m-- == paramInt1) {
              break;
            }
          }
          paramArrayOfLong[(m + 1)] = l3;
          j++;
        }
      }
      else
      {
        do
        {
          if (paramInt1 >= paramInt2) {
            return;
          }
        } while (paramArrayOfLong[(++paramInt1)] >= paramArrayOfLong[(paramInt1 - 1)]);
        for (j = paramInt1;; j = paramInt1)
        {
          paramInt1++;
          if (paramInt1 > paramInt2) {
            break;
          }
          long l2 = paramArrayOfLong[j];
          long l4 = paramArrayOfLong[paramInt1];
          if (l2 < l4)
          {
            l4 = l2;
            l2 = paramArrayOfLong[paramInt1];
          }
          while (l2 < paramArrayOfLong[(--j)]) {
            paramArrayOfLong[(j + 2)] = paramArrayOfLong[j];
          }
          paramArrayOfLong[(++j + 1)] = l2;
          while (l4 < paramArrayOfLong[(--j)]) {
            paramArrayOfLong[(j + 1)] = paramArrayOfLong[j];
          }
          paramArrayOfLong[(j + 1)] = l4;
          paramInt1++;
        }
        long l1 = paramArrayOfLong[paramInt2];
        while (l1 < paramArrayOfLong[(--paramInt2)]) {
          paramArrayOfLong[(paramInt2 + 1)] = paramArrayOfLong[paramInt2];
        }
        paramArrayOfLong[(paramInt2 + 1)] = l1;
      }
      return;
    }
    int k = (i >> 3) + (i >> 6) + 1;
    int n = paramInt1 + paramInt2 >>> 1;
    int i1 = n - k;
    int i2 = i1 - k;
    int i3 = n + k;
    int i4 = i3 + k;
    long l5;
    if (paramArrayOfLong[i1] < paramArrayOfLong[i2])
    {
      l5 = paramArrayOfLong[i1];
      paramArrayOfLong[i1] = paramArrayOfLong[i2];
      paramArrayOfLong[i2] = l5;
    }
    if (paramArrayOfLong[n] < paramArrayOfLong[i1])
    {
      l5 = paramArrayOfLong[n];
      paramArrayOfLong[n] = paramArrayOfLong[i1];
      paramArrayOfLong[i1] = l5;
      if (l5 < paramArrayOfLong[i2])
      {
        paramArrayOfLong[i1] = paramArrayOfLong[i2];
        paramArrayOfLong[i2] = l5;
      }
    }
    if (paramArrayOfLong[i3] < paramArrayOfLong[n])
    {
      l5 = paramArrayOfLong[i3];
      paramArrayOfLong[i3] = paramArrayOfLong[n];
      paramArrayOfLong[n] = l5;
      if (l5 < paramArrayOfLong[i1])
      {
        paramArrayOfLong[n] = paramArrayOfLong[i1];
        paramArrayOfLong[i1] = l5;
        if (l5 < paramArrayOfLong[i2])
        {
          paramArrayOfLong[i1] = paramArrayOfLong[i2];
          paramArrayOfLong[i2] = l5;
        }
      }
    }
    if (paramArrayOfLong[i4] < paramArrayOfLong[i3])
    {
      l5 = paramArrayOfLong[i4];
      paramArrayOfLong[i4] = paramArrayOfLong[i3];
      paramArrayOfLong[i3] = l5;
      if (l5 < paramArrayOfLong[n])
      {
        paramArrayOfLong[i3] = paramArrayOfLong[n];
        paramArrayOfLong[n] = l5;
        if (l5 < paramArrayOfLong[i1])
        {
          paramArrayOfLong[n] = paramArrayOfLong[i1];
          paramArrayOfLong[i1] = l5;
          if (l5 < paramArrayOfLong[i2])
          {
            paramArrayOfLong[i1] = paramArrayOfLong[i2];
            paramArrayOfLong[i2] = l5;
          }
        }
      }
    }
    long l6 = paramInt1;
    long l7 = paramInt2;
    long l8;
    long l9;
    if ((paramArrayOfLong[i2] != paramArrayOfLong[i1]) && (paramArrayOfLong[i1] != paramArrayOfLong[n]) && (paramArrayOfLong[n] != paramArrayOfLong[i3]) && (paramArrayOfLong[i3] != paramArrayOfLong[i4]))
    {
      l8 = paramArrayOfLong[i1];
      l9 = paramArrayOfLong[i3];
      paramArrayOfLong[i1] = paramArrayOfLong[paramInt1];
      paramArrayOfLong[i3] = paramArrayOfLong[paramInt2];
      while (paramArrayOfLong[(++l6)] < l8) {}
      while (paramArrayOfLong[(--l7)] > l9) {}
      long l11 = l6 - 1;
      long l13;
      for (;;)
      {
        l11++;
        if (l11 > l7) {
          break;
        }
        l13 = paramArrayOfLong[l11];
        if (l13 < l8)
        {
          paramArrayOfLong[l11] = paramArrayOfLong[l6];
          paramArrayOfLong[l6] = l13;
          l6++;
        }
        else if (l13 > l9)
        {
          while (paramArrayOfLong[l7] > l9) {
            if (l7-- == l11) {
              break label834;
            }
          }
          if (paramArrayOfLong[l7] < l8)
          {
            paramArrayOfLong[l11] = paramArrayOfLong[l6];
            paramArrayOfLong[l6] = paramArrayOfLong[l7];
            l6++;
          }
          else
          {
            paramArrayOfLong[l11] = paramArrayOfLong[l7];
          }
          paramArrayOfLong[l7] = l13;
          l7--;
        }
      }
      label834:
      paramArrayOfLong[paramInt1] = paramArrayOfLong[(l6 - 1)];
      paramArrayOfLong[(l6 - 1)] = l8;
      paramArrayOfLong[paramInt2] = paramArrayOfLong[(l7 + 1)];
      paramArrayOfLong[(l7 + 1)] = l9;
      sort(paramArrayOfLong, paramInt1, l6 - 2, paramBoolean);
      sort(paramArrayOfLong, l7 + 2, paramInt2, false);
      if ((l6 < i2) && (i4 < l7))
      {
        while (paramArrayOfLong[l6] == l8) {
          l6++;
        }
        while (paramArrayOfLong[l7] == l9) {
          l7--;
        }
        long l12 = l6 - 1;
        for (;;)
        {
          l12++;
          if (l12 > l7) {
            break;
          }
          l13 = paramArrayOfLong[l12];
          if (l13 == l8)
          {
            paramArrayOfLong[l12] = paramArrayOfLong[l6];
            paramArrayOfLong[l6] = l13;
            l6++;
          }
          else if (l13 == l9)
          {
            while (paramArrayOfLong[l7] == l9) {
              if (l7-- == l12) {
                break label1065;
              }
            }
            if (paramArrayOfLong[l7] == l8)
            {
              paramArrayOfLong[l12] = paramArrayOfLong[l6];
              paramArrayOfLong[l6] = l8;
              l6++;
            }
            else
            {
              paramArrayOfLong[l12] = paramArrayOfLong[l7];
            }
            paramArrayOfLong[l7] = l13;
            l7--;
          }
        }
      }
      label1065:
      sort(paramArrayOfLong, l6, l7, false);
    }
    else
    {
      l8 = paramArrayOfLong[n];
      for (l9 = l6; l9 <= l7; l9++) {
        if (paramArrayOfLong[l9] != l8)
        {
          long l10 = paramArrayOfLong[l9];
          if (l10 < l8)
          {
            paramArrayOfLong[l9] = paramArrayOfLong[l6];
            paramArrayOfLong[l6] = l10;
            l6++;
          }
          else
          {
            while (paramArrayOfLong[l7] > l8) {
              l7--;
            }
            if (paramArrayOfLong[l7] < l8)
            {
              paramArrayOfLong[l9] = paramArrayOfLong[l6];
              paramArrayOfLong[l6] = paramArrayOfLong[l7];
              l6++;
            }
            else
            {
              paramArrayOfLong[l9] = l8;
            }
            paramArrayOfLong[l7] = l10;
            l7--;
          }
        }
      }
      sort(paramArrayOfLong, paramInt1, l6 - 1, paramBoolean);
      sort(paramArrayOfLong, l7 + 1, paramInt2, false);
    }
  }
  
  static void sort(short[] paramArrayOfShort1, int paramInt1, int paramInt2, short[] paramArrayOfShort2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 > 3200)
    {
      int[] arrayOfInt = new int[65536];
      int i = paramInt1 - 1;
      for (;;)
      {
        i++;
        if (i > paramInt2) {
          break;
        }
        arrayOfInt[(paramArrayOfShort1[i] - Short.MIN_VALUE)] += 1;
      }
      i = 65536;
      int j = paramInt2 + 1;
      while (j > paramInt1)
      {
        while (arrayOfInt[(--i)] == 0) {}
        int k = (short)(i + 32768);
        int m = arrayOfInt[i];
        do
        {
          paramArrayOfShort1[(--j)] = k;
          m--;
        } while (m > 0);
      }
    }
    else
    {
      doSort(paramArrayOfShort1, paramInt1, paramInt2, paramArrayOfShort2, paramInt3, paramInt4);
    }
  }
  
  private static void doSort(short[] paramArrayOfShort1, int paramInt1, int paramInt2, short[] paramArrayOfShort2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 < 286)
    {
      sort(paramArrayOfShort1, paramInt1, paramInt2, true);
      return;
    }
    int[] arrayOfInt = new int[68];
    int i = 0;
    arrayOfInt[0] = paramInt1;
    int j = paramInt1;
    int m;
    int n;
    while (j < paramInt2)
    {
      if (paramArrayOfShort1[j] < paramArrayOfShort1[(j + 1)]) {
        for (;;)
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfShort1[(j - 1)] > paramArrayOfShort1[j])) {
            break;
          }
        }
      }
      if (paramArrayOfShort1[j] > paramArrayOfShort1[(j + 1)])
      {
        do
        {
          j++;
        } while ((j <= paramInt2) && (paramArrayOfShort1[(j - 1)] >= paramArrayOfShort1[j]));
        k = arrayOfInt[i] - 1;
        m = j;
        for (;;)
        {
          k++;
          if (k >= --m) {
            break;
          }
          n = paramArrayOfShort1[k];
          paramArrayOfShort1[k] = paramArrayOfShort1[m];
          paramArrayOfShort1[m] = n;
        }
      }
      else
      {
        k = 33;
        do
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfShort1[(j - 1)] != paramArrayOfShort1[j])) {
            break;
          }
          k--;
        } while (k != 0);
        sort(paramArrayOfShort1, paramInt1, paramInt2, true);
        return;
      }
      i++;
      if (i == 67)
      {
        sort(paramArrayOfShort1, paramInt1, paramInt2, true);
        return;
      }
      arrayOfInt[i] = j;
    }
    if (arrayOfInt[i] == paramInt2++) {
      arrayOfInt[(++i)] = paramInt2;
    } else if (i == 1) {
      return;
    }
    j = 0;
    int k = 1;
    while (k <<= 1 < i) {
      j = (byte)(j ^ 0x1);
    }
    int i1 = paramInt2 - paramInt1;
    if ((paramArrayOfShort2 == null) || (paramInt4 < i1) || (paramInt3 + i1 > paramArrayOfShort2.length))
    {
      paramArrayOfShort2 = new short[i1];
      paramInt3 = 0;
    }
    Object localObject;
    if (j == 0)
    {
      System.arraycopy(paramArrayOfShort1, paramInt1, paramArrayOfShort2, paramInt3, i1);
      localObject = paramArrayOfShort1;
      n = 0;
      paramArrayOfShort1 = paramArrayOfShort2;
      m = paramInt3 - paramInt1;
    }
    else
    {
      localObject = paramArrayOfShort2;
      m = 0;
      n = paramInt3 - paramInt1;
    }
    while (i > 1)
    {
      int i2;
      for (int i3 = (i2 = 0) + 2; i3 <= i; i3 += 2)
      {
        i4 = arrayOfInt[i3];
        int i5 = arrayOfInt[(i3 - 1)];
        int i6 = arrayOfInt[(i3 - 2)];
        int i7 = i6;
        int i8 = i5;
        while (i6 < i4)
        {
          if ((i8 >= i4) || ((i7 < i5) && (paramArrayOfShort1[(i7 + m)] <= paramArrayOfShort1[(i8 + m)]))) {
            localObject[(i6 + n)] = paramArrayOfShort1[(i7++ + m)];
          } else {
            localObject[(i6 + n)] = paramArrayOfShort1[(i8++ + m)];
          }
          i6++;
        }
        arrayOfInt[(++i2)] = i4;
      }
      if ((i & 0x1) != 0)
      {
        i3 = paramInt2;
        i4 = arrayOfInt[(i - 1)];
        for (;;)
        {
          i3--;
          if (i3 < i4) {
            break;
          }
          localObject[(i3 + n)] = paramArrayOfShort1[(i3 + m)];
        }
        arrayOfInt[(++i2)] = paramInt2;
      }
      short[] arrayOfShort = paramArrayOfShort1;
      paramArrayOfShort1 = (short[])localObject;
      localObject = arrayOfShort;
      int i4 = m;
      m = n;
      n = i4;
      i = i2;
    }
  }
  
  private static void sort(short[] paramArrayOfShort, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47)
    {
      if (paramBoolean)
      {
        j = paramInt1;
        for (k = j; j < paramInt2; k = j)
        {
          m = paramArrayOfShort[(j + 1)];
          while (m < paramArrayOfShort[k])
          {
            paramArrayOfShort[(k + 1)] = paramArrayOfShort[k];
            if (k-- == paramInt1) {
              break;
            }
          }
          paramArrayOfShort[(k + 1)] = m;
          j++;
        }
      }
      else
      {
        do
        {
          if (paramInt1 >= paramInt2) {
            return;
          }
        } while (paramArrayOfShort[(++paramInt1)] >= paramArrayOfShort[(paramInt1 - 1)]);
        for (j = paramInt1;; j = paramInt1)
        {
          paramInt1++;
          if (paramInt1 > paramInt2) {
            break;
          }
          k = paramArrayOfShort[j];
          m = paramArrayOfShort[paramInt1];
          if (k < m)
          {
            m = k;
            k = paramArrayOfShort[paramInt1];
          }
          while (k < paramArrayOfShort[(--j)]) {
            paramArrayOfShort[(j + 2)] = paramArrayOfShort[j];
          }
          paramArrayOfShort[(++j + 1)] = k;
          while (m < paramArrayOfShort[(--j)]) {
            paramArrayOfShort[(j + 1)] = paramArrayOfShort[j];
          }
          paramArrayOfShort[(j + 1)] = m;
          paramInt1++;
        }
        j = paramArrayOfShort[paramInt2];
        while (j < paramArrayOfShort[(--paramInt2)]) {
          paramArrayOfShort[(paramInt2 + 1)] = paramArrayOfShort[paramInt2];
        }
        paramArrayOfShort[(paramInt2 + 1)] = j;
      }
      return;
    }
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfShort[m] < paramArrayOfShort[n])
    {
      i3 = paramArrayOfShort[m];
      paramArrayOfShort[m] = paramArrayOfShort[n];
      paramArrayOfShort[n] = i3;
    }
    if (paramArrayOfShort[k] < paramArrayOfShort[m])
    {
      i3 = paramArrayOfShort[k];
      paramArrayOfShort[k] = paramArrayOfShort[m];
      paramArrayOfShort[m] = i3;
      if (i3 < paramArrayOfShort[n])
      {
        paramArrayOfShort[m] = paramArrayOfShort[n];
        paramArrayOfShort[n] = i3;
      }
    }
    if (paramArrayOfShort[i1] < paramArrayOfShort[k])
    {
      i3 = paramArrayOfShort[i1];
      paramArrayOfShort[i1] = paramArrayOfShort[k];
      paramArrayOfShort[k] = i3;
      if (i3 < paramArrayOfShort[m])
      {
        paramArrayOfShort[k] = paramArrayOfShort[m];
        paramArrayOfShort[m] = i3;
        if (i3 < paramArrayOfShort[n])
        {
          paramArrayOfShort[m] = paramArrayOfShort[n];
          paramArrayOfShort[n] = i3;
        }
      }
    }
    if (paramArrayOfShort[i2] < paramArrayOfShort[i1])
    {
      i3 = paramArrayOfShort[i2];
      paramArrayOfShort[i2] = paramArrayOfShort[i1];
      paramArrayOfShort[i1] = i3;
      if (i3 < paramArrayOfShort[k])
      {
        paramArrayOfShort[i1] = paramArrayOfShort[k];
        paramArrayOfShort[k] = i3;
        if (i3 < paramArrayOfShort[m])
        {
          paramArrayOfShort[k] = paramArrayOfShort[m];
          paramArrayOfShort[m] = i3;
          if (i3 < paramArrayOfShort[n])
          {
            paramArrayOfShort[m] = paramArrayOfShort[n];
            paramArrayOfShort[n] = i3;
          }
        }
      }
    }
    int i3 = paramInt1;
    int i4 = paramInt2;
    int i5;
    int i6;
    int i7;
    if ((paramArrayOfShort[n] != paramArrayOfShort[m]) && (paramArrayOfShort[m] != paramArrayOfShort[k]) && (paramArrayOfShort[k] != paramArrayOfShort[i1]) && (paramArrayOfShort[i1] != paramArrayOfShort[i2]))
    {
      i5 = paramArrayOfShort[m];
      i6 = paramArrayOfShort[i1];
      paramArrayOfShort[m] = paramArrayOfShort[paramInt1];
      paramArrayOfShort[i1] = paramArrayOfShort[paramInt2];
      while (paramArrayOfShort[(++i3)] < i5) {}
      while (paramArrayOfShort[(--i4)] > i6) {}
      i7 = i3 - 1;
      int i8;
      for (;;)
      {
        i7++;
        if (i7 > i4) {
          break;
        }
        i8 = paramArrayOfShort[i7];
        if (i8 < i5)
        {
          paramArrayOfShort[i7] = paramArrayOfShort[i3];
          paramArrayOfShort[i3] = i8;
          i3++;
        }
        else if (i8 > i6)
        {
          while (paramArrayOfShort[i4] > i6) {
            if (i4-- == i7) {
              break label808;
            }
          }
          if (paramArrayOfShort[i4] < i5)
          {
            paramArrayOfShort[i7] = paramArrayOfShort[i3];
            paramArrayOfShort[i3] = paramArrayOfShort[i4];
            i3++;
          }
          else
          {
            paramArrayOfShort[i7] = paramArrayOfShort[i4];
          }
          paramArrayOfShort[i4] = i8;
          i4--;
        }
      }
      label808:
      paramArrayOfShort[paramInt1] = paramArrayOfShort[(i3 - 1)];
      paramArrayOfShort[(i3 - 1)] = i5;
      paramArrayOfShort[paramInt2] = paramArrayOfShort[(i4 + 1)];
      paramArrayOfShort[(i4 + 1)] = i6;
      sort(paramArrayOfShort, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfShort, i4 + 2, paramInt2, false);
      if ((i3 < n) && (i2 < i4))
      {
        while (paramArrayOfShort[i3] == i5) {
          i3++;
        }
        while (paramArrayOfShort[i4] == i6) {
          i4--;
        }
        i7 = i3 - 1;
        for (;;)
        {
          i7++;
          if (i7 > i4) {
            break;
          }
          i8 = paramArrayOfShort[i7];
          if (i8 == i5)
          {
            paramArrayOfShort[i7] = paramArrayOfShort[i3];
            paramArrayOfShort[i3] = i8;
            i3++;
          }
          else if (i8 == i6)
          {
            while (paramArrayOfShort[i4] == i6) {
              if (i4-- == i7) {
                break label1033;
              }
            }
            if (paramArrayOfShort[i4] == i5)
            {
              paramArrayOfShort[i7] = paramArrayOfShort[i3];
              paramArrayOfShort[i3] = i5;
              i3++;
            }
            else
            {
              paramArrayOfShort[i7] = paramArrayOfShort[i4];
            }
            paramArrayOfShort[i4] = i8;
            i4--;
          }
        }
      }
      label1033:
      sort(paramArrayOfShort, i3, i4, false);
    }
    else
    {
      i5 = paramArrayOfShort[k];
      for (i6 = i3; i6 <= i4; i6++) {
        if (paramArrayOfShort[i6] != i5)
        {
          i7 = paramArrayOfShort[i6];
          if (i7 < i5)
          {
            paramArrayOfShort[i6] = paramArrayOfShort[i3];
            paramArrayOfShort[i3] = i7;
            i3++;
          }
          else
          {
            while (paramArrayOfShort[i4] > i5) {
              i4--;
            }
            if (paramArrayOfShort[i4] < i5)
            {
              paramArrayOfShort[i6] = paramArrayOfShort[i3];
              paramArrayOfShort[i3] = paramArrayOfShort[i4];
              i3++;
            }
            else
            {
              paramArrayOfShort[i6] = i5;
            }
            paramArrayOfShort[i4] = i7;
            i4--;
          }
        }
      }
      sort(paramArrayOfShort, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfShort, i4 + 1, paramInt2, false);
    }
  }
  
  static void sort(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 > 3200)
    {
      int[] arrayOfInt = new int[65536];
      int i = paramInt1 - 1;
      for (;;)
      {
        i++;
        if (i > paramInt2) {
          break;
        }
        arrayOfInt[paramArrayOfChar1[i]] += 1;
      }
      i = 65536;
      int j = paramInt2 + 1;
      while (j > paramInt1)
      {
        while (arrayOfInt[(--i)] == 0) {}
        int k = (char)i;
        int m = arrayOfInt[i];
        do
        {
          paramArrayOfChar1[(--j)] = k;
          m--;
        } while (m > 0);
      }
    }
    else
    {
      doSort(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4);
    }
  }
  
  private static void doSort(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 < 286)
    {
      sort(paramArrayOfChar1, paramInt1, paramInt2, true);
      return;
    }
    int[] arrayOfInt = new int[68];
    int i = 0;
    arrayOfInt[0] = paramInt1;
    int j = paramInt1;
    int m;
    int n;
    while (j < paramInt2)
    {
      if (paramArrayOfChar1[j] < paramArrayOfChar1[(j + 1)]) {
        for (;;)
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfChar1[(j - 1)] > paramArrayOfChar1[j])) {
            break;
          }
        }
      }
      if (paramArrayOfChar1[j] > paramArrayOfChar1[(j + 1)])
      {
        do
        {
          j++;
        } while ((j <= paramInt2) && (paramArrayOfChar1[(j - 1)] >= paramArrayOfChar1[j]));
        k = arrayOfInt[i] - 1;
        m = j;
        for (;;)
        {
          k++;
          if (k >= --m) {
            break;
          }
          n = paramArrayOfChar1[k];
          paramArrayOfChar1[k] = paramArrayOfChar1[m];
          paramArrayOfChar1[m] = n;
        }
      }
      else
      {
        k = 33;
        do
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfChar1[(j - 1)] != paramArrayOfChar1[j])) {
            break;
          }
          k--;
        } while (k != 0);
        sort(paramArrayOfChar1, paramInt1, paramInt2, true);
        return;
      }
      i++;
      if (i == 67)
      {
        sort(paramArrayOfChar1, paramInt1, paramInt2, true);
        return;
      }
      arrayOfInt[i] = j;
    }
    if (arrayOfInt[i] == paramInt2++) {
      arrayOfInt[(++i)] = paramInt2;
    } else if (i == 1) {
      return;
    }
    j = 0;
    int k = 1;
    while (k <<= 1 < i) {
      j = (byte)(j ^ 0x1);
    }
    int i1 = paramInt2 - paramInt1;
    if ((paramArrayOfChar2 == null) || (paramInt4 < i1) || (paramInt3 + i1 > paramArrayOfChar2.length))
    {
      paramArrayOfChar2 = new char[i1];
      paramInt3 = 0;
    }
    Object localObject;
    if (j == 0)
    {
      System.arraycopy(paramArrayOfChar1, paramInt1, paramArrayOfChar2, paramInt3, i1);
      localObject = paramArrayOfChar1;
      n = 0;
      paramArrayOfChar1 = paramArrayOfChar2;
      m = paramInt3 - paramInt1;
    }
    else
    {
      localObject = paramArrayOfChar2;
      m = 0;
      n = paramInt3 - paramInt1;
    }
    while (i > 1)
    {
      int i2;
      for (int i3 = (i2 = 0) + 2; i3 <= i; i3 += 2)
      {
        i4 = arrayOfInt[i3];
        int i5 = arrayOfInt[(i3 - 1)];
        int i6 = arrayOfInt[(i3 - 2)];
        int i7 = i6;
        int i8 = i5;
        while (i6 < i4)
        {
          if ((i8 >= i4) || ((i7 < i5) && (paramArrayOfChar1[(i7 + m)] <= paramArrayOfChar1[(i8 + m)]))) {
            localObject[(i6 + n)] = paramArrayOfChar1[(i7++ + m)];
          } else {
            localObject[(i6 + n)] = paramArrayOfChar1[(i8++ + m)];
          }
          i6++;
        }
        arrayOfInt[(++i2)] = i4;
      }
      if ((i & 0x1) != 0)
      {
        i3 = paramInt2;
        i4 = arrayOfInt[(i - 1)];
        for (;;)
        {
          i3--;
          if (i3 < i4) {
            break;
          }
          localObject[(i3 + n)] = paramArrayOfChar1[(i3 + m)];
        }
        arrayOfInt[(++i2)] = paramInt2;
      }
      char[] arrayOfChar = paramArrayOfChar1;
      paramArrayOfChar1 = (char[])localObject;
      localObject = arrayOfChar;
      int i4 = m;
      m = n;
      n = i4;
      i = i2;
    }
  }
  
  private static void sort(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47)
    {
      if (paramBoolean)
      {
        j = paramInt1;
        for (k = j; j < paramInt2; k = j)
        {
          m = paramArrayOfChar[(j + 1)];
          while (m < paramArrayOfChar[k])
          {
            paramArrayOfChar[(k + 1)] = paramArrayOfChar[k];
            if (k-- == paramInt1) {
              break;
            }
          }
          paramArrayOfChar[(k + 1)] = m;
          j++;
        }
      }
      else
      {
        do
        {
          if (paramInt1 >= paramInt2) {
            return;
          }
        } while (paramArrayOfChar[(++paramInt1)] >= paramArrayOfChar[(paramInt1 - 1)]);
        for (j = paramInt1;; j = paramInt1)
        {
          paramInt1++;
          if (paramInt1 > paramInt2) {
            break;
          }
          k = paramArrayOfChar[j];
          m = paramArrayOfChar[paramInt1];
          if (k < m)
          {
            m = k;
            k = paramArrayOfChar[paramInt1];
          }
          while (k < paramArrayOfChar[(--j)]) {
            paramArrayOfChar[(j + 2)] = paramArrayOfChar[j];
          }
          paramArrayOfChar[(++j + 1)] = k;
          while (m < paramArrayOfChar[(--j)]) {
            paramArrayOfChar[(j + 1)] = paramArrayOfChar[j];
          }
          paramArrayOfChar[(j + 1)] = m;
          paramInt1++;
        }
        j = paramArrayOfChar[paramInt2];
        while (j < paramArrayOfChar[(--paramInt2)]) {
          paramArrayOfChar[(paramInt2 + 1)] = paramArrayOfChar[paramInt2];
        }
        paramArrayOfChar[(paramInt2 + 1)] = j;
      }
      return;
    }
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfChar[m] < paramArrayOfChar[n])
    {
      i3 = paramArrayOfChar[m];
      paramArrayOfChar[m] = paramArrayOfChar[n];
      paramArrayOfChar[n] = i3;
    }
    if (paramArrayOfChar[k] < paramArrayOfChar[m])
    {
      i3 = paramArrayOfChar[k];
      paramArrayOfChar[k] = paramArrayOfChar[m];
      paramArrayOfChar[m] = i3;
      if (i3 < paramArrayOfChar[n])
      {
        paramArrayOfChar[m] = paramArrayOfChar[n];
        paramArrayOfChar[n] = i3;
      }
    }
    if (paramArrayOfChar[i1] < paramArrayOfChar[k])
    {
      i3 = paramArrayOfChar[i1];
      paramArrayOfChar[i1] = paramArrayOfChar[k];
      paramArrayOfChar[k] = i3;
      if (i3 < paramArrayOfChar[m])
      {
        paramArrayOfChar[k] = paramArrayOfChar[m];
        paramArrayOfChar[m] = i3;
        if (i3 < paramArrayOfChar[n])
        {
          paramArrayOfChar[m] = paramArrayOfChar[n];
          paramArrayOfChar[n] = i3;
        }
      }
    }
    if (paramArrayOfChar[i2] < paramArrayOfChar[i1])
    {
      i3 = paramArrayOfChar[i2];
      paramArrayOfChar[i2] = paramArrayOfChar[i1];
      paramArrayOfChar[i1] = i3;
      if (i3 < paramArrayOfChar[k])
      {
        paramArrayOfChar[i1] = paramArrayOfChar[k];
        paramArrayOfChar[k] = i3;
        if (i3 < paramArrayOfChar[m])
        {
          paramArrayOfChar[k] = paramArrayOfChar[m];
          paramArrayOfChar[m] = i3;
          if (i3 < paramArrayOfChar[n])
          {
            paramArrayOfChar[m] = paramArrayOfChar[n];
            paramArrayOfChar[n] = i3;
          }
        }
      }
    }
    int i3 = paramInt1;
    int i4 = paramInt2;
    int i5;
    int i6;
    int i7;
    if ((paramArrayOfChar[n] != paramArrayOfChar[m]) && (paramArrayOfChar[m] != paramArrayOfChar[k]) && (paramArrayOfChar[k] != paramArrayOfChar[i1]) && (paramArrayOfChar[i1] != paramArrayOfChar[i2]))
    {
      i5 = paramArrayOfChar[m];
      i6 = paramArrayOfChar[i1];
      paramArrayOfChar[m] = paramArrayOfChar[paramInt1];
      paramArrayOfChar[i1] = paramArrayOfChar[paramInt2];
      while (paramArrayOfChar[(++i3)] < i5) {}
      while (paramArrayOfChar[(--i4)] > i6) {}
      i7 = i3 - 1;
      int i8;
      for (;;)
      {
        i7++;
        if (i7 > i4) {
          break;
        }
        i8 = paramArrayOfChar[i7];
        if (i8 < i5)
        {
          paramArrayOfChar[i7] = paramArrayOfChar[i3];
          paramArrayOfChar[i3] = i8;
          i3++;
        }
        else if (i8 > i6)
        {
          while (paramArrayOfChar[i4] > i6) {
            if (i4-- == i7) {
              break label808;
            }
          }
          if (paramArrayOfChar[i4] < i5)
          {
            paramArrayOfChar[i7] = paramArrayOfChar[i3];
            paramArrayOfChar[i3] = paramArrayOfChar[i4];
            i3++;
          }
          else
          {
            paramArrayOfChar[i7] = paramArrayOfChar[i4];
          }
          paramArrayOfChar[i4] = i8;
          i4--;
        }
      }
      label808:
      paramArrayOfChar[paramInt1] = paramArrayOfChar[(i3 - 1)];
      paramArrayOfChar[(i3 - 1)] = i5;
      paramArrayOfChar[paramInt2] = paramArrayOfChar[(i4 + 1)];
      paramArrayOfChar[(i4 + 1)] = i6;
      sort(paramArrayOfChar, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfChar, i4 + 2, paramInt2, false);
      if ((i3 < n) && (i2 < i4))
      {
        while (paramArrayOfChar[i3] == i5) {
          i3++;
        }
        while (paramArrayOfChar[i4] == i6) {
          i4--;
        }
        i7 = i3 - 1;
        for (;;)
        {
          i7++;
          if (i7 > i4) {
            break;
          }
          i8 = paramArrayOfChar[i7];
          if (i8 == i5)
          {
            paramArrayOfChar[i7] = paramArrayOfChar[i3];
            paramArrayOfChar[i3] = i8;
            i3++;
          }
          else if (i8 == i6)
          {
            while (paramArrayOfChar[i4] == i6) {
              if (i4-- == i7) {
                break label1033;
              }
            }
            if (paramArrayOfChar[i4] == i5)
            {
              paramArrayOfChar[i7] = paramArrayOfChar[i3];
              paramArrayOfChar[i3] = i5;
              i3++;
            }
            else
            {
              paramArrayOfChar[i7] = paramArrayOfChar[i4];
            }
            paramArrayOfChar[i4] = i8;
            i4--;
          }
        }
      }
      label1033:
      sort(paramArrayOfChar, i3, i4, false);
    }
    else
    {
      i5 = paramArrayOfChar[k];
      for (i6 = i3; i6 <= i4; i6++) {
        if (paramArrayOfChar[i6] != i5)
        {
          i7 = paramArrayOfChar[i6];
          if (i7 < i5)
          {
            paramArrayOfChar[i6] = paramArrayOfChar[i3];
            paramArrayOfChar[i3] = i7;
            i3++;
          }
          else
          {
            while (paramArrayOfChar[i4] > i5) {
              i4--;
            }
            if (paramArrayOfChar[i4] < i5)
            {
              paramArrayOfChar[i6] = paramArrayOfChar[i3];
              paramArrayOfChar[i3] = paramArrayOfChar[i4];
              i3++;
            }
            else
            {
              paramArrayOfChar[i6] = i5;
            }
            paramArrayOfChar[i4] = i7;
            i4--;
          }
        }
      }
      sort(paramArrayOfChar, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfChar, i4 + 1, paramInt2, false);
    }
  }
  
  static void sort(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int j;
    int k;
    if (paramInt2 - paramInt1 > 29)
    {
      int[] arrayOfInt = new int['Ā'];
      j = paramInt1 - 1;
      for (;;)
      {
        j++;
        if (j > paramInt2) {
          break;
        }
        arrayOfInt[(paramArrayOfByte[j] - Byte.MIN_VALUE)] += 1;
      }
      j = 256;
      k = paramInt2 + 1;
      while (k > paramInt1)
      {
        while (arrayOfInt[(--j)] == 0) {}
        int m = (byte)(j + -128);
        int n = arrayOfInt[j];
        do
        {
          paramArrayOfByte[(--k)] = m;
          n--;
        } while (n > 0);
      }
    }
    else
    {
      int i = paramInt1;
      for (j = i; i < paramInt2; j = i)
      {
        k = paramArrayOfByte[(i + 1)];
        while (k < paramArrayOfByte[j])
        {
          paramArrayOfByte[(j + 1)] = paramArrayOfByte[j];
          if (j-- == paramInt1) {
            break;
          }
        }
        paramArrayOfByte[(j + 1)] = k;
        i++;
      }
    }
  }
  
  static void sort(float[] paramArrayOfFloat1, int paramInt1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4)
  {
    while ((paramInt1 <= paramInt2) && (Float.isNaN(paramArrayOfFloat1[paramInt2]))) {
      paramInt2--;
    }
    int i = paramInt2;
    for (;;)
    {
      i--;
      if (i < paramInt1) {
        break;
      }
      float f1 = paramArrayOfFloat1[i];
      if (f1 != f1)
      {
        paramArrayOfFloat1[i] = paramArrayOfFloat1[paramInt2];
        paramArrayOfFloat1[paramInt2] = f1;
        paramInt2--;
      }
    }
    doSort(paramArrayOfFloat1, paramInt1, paramInt2, paramArrayOfFloat2, paramInt3, paramInt4);
    i = paramInt2;
    while (paramInt1 < i)
    {
      j = paramInt1 + i >>> 1;
      float f2 = paramArrayOfFloat1[j];
      if (f2 < 0.0F) {
        paramInt1 = j + 1;
      } else {
        i = j;
      }
    }
    while ((paramInt1 <= paramInt2) && (Float.floatToRawIntBits(paramArrayOfFloat1[paramInt1]) < 0)) {
      paramInt1++;
    }
    int j = paramInt1;
    int k = paramInt1 - 1;
    for (;;)
    {
      j++;
      if (j > paramInt2) {
        break;
      }
      float f3 = paramArrayOfFloat1[j];
      if (f3 != 0.0F) {
        break;
      }
      if (Float.floatToRawIntBits(f3) < 0)
      {
        paramArrayOfFloat1[j] = 0.0F;
        paramArrayOfFloat1[(++k)] = -0.0F;
      }
    }
  }
  
  private static void doSort(float[] paramArrayOfFloat1, int paramInt1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 < 286)
    {
      sort(paramArrayOfFloat1, paramInt1, paramInt2, true);
      return;
    }
    int[] arrayOfInt = new int[68];
    int i = 0;
    arrayOfInt[0] = paramInt1;
    int j = paramInt1;
    int m;
    while (j < paramInt2)
    {
      if (paramArrayOfFloat1[j] < paramArrayOfFloat1[(j + 1)]) {
        for (;;)
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfFloat1[(j - 1)] > paramArrayOfFloat1[j])) {
            break;
          }
        }
      }
      if (paramArrayOfFloat1[j] > paramArrayOfFloat1[(j + 1)])
      {
        do
        {
          j++;
        } while ((j <= paramInt2) && (paramArrayOfFloat1[(j - 1)] >= paramArrayOfFloat1[j]));
        k = arrayOfInt[i] - 1;
        m = j;
        for (;;)
        {
          k++;
          if (k >= --m) {
            break;
          }
          float f = paramArrayOfFloat1[k];
          paramArrayOfFloat1[k] = paramArrayOfFloat1[m];
          paramArrayOfFloat1[m] = f;
        }
      }
      else
      {
        k = 33;
        do
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfFloat1[(j - 1)] != paramArrayOfFloat1[j])) {
            break;
          }
          k--;
        } while (k != 0);
        sort(paramArrayOfFloat1, paramInt1, paramInt2, true);
        return;
      }
      i++;
      if (i == 67)
      {
        sort(paramArrayOfFloat1, paramInt1, paramInt2, true);
        return;
      }
      arrayOfInt[i] = j;
    }
    if (arrayOfInt[i] == paramInt2++) {
      arrayOfInt[(++i)] = paramInt2;
    } else if (i == 1) {
      return;
    }
    j = 0;
    int k = 1;
    while (k <<= 1 < i) {
      j = (byte)(j ^ 0x1);
    }
    int i1 = paramInt2 - paramInt1;
    if ((paramArrayOfFloat2 == null) || (paramInt4 < i1) || (paramInt3 + i1 > paramArrayOfFloat2.length))
    {
      paramArrayOfFloat2 = new float[i1];
      paramInt3 = 0;
    }
    Object localObject;
    int n;
    if (j == 0)
    {
      System.arraycopy(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt3, i1);
      localObject = paramArrayOfFloat1;
      n = 0;
      paramArrayOfFloat1 = paramArrayOfFloat2;
      m = paramInt3 - paramInt1;
    }
    else
    {
      localObject = paramArrayOfFloat2;
      m = 0;
      n = paramInt3 - paramInt1;
    }
    while (i > 1)
    {
      int i2;
      for (int i3 = (i2 = 0) + 2; i3 <= i; i3 += 2)
      {
        i4 = arrayOfInt[i3];
        int i5 = arrayOfInt[(i3 - 1)];
        int i6 = arrayOfInt[(i3 - 2)];
        int i7 = i6;
        int i8 = i5;
        while (i6 < i4)
        {
          if ((i8 >= i4) || ((i7 < i5) && (paramArrayOfFloat1[(i7 + m)] <= paramArrayOfFloat1[(i8 + m)]))) {
            localObject[(i6 + n)] = paramArrayOfFloat1[(i7++ + m)];
          } else {
            localObject[(i6 + n)] = paramArrayOfFloat1[(i8++ + m)];
          }
          i6++;
        }
        arrayOfInt[(++i2)] = i4;
      }
      if ((i & 0x1) != 0)
      {
        i3 = paramInt2;
        i4 = arrayOfInt[(i - 1)];
        for (;;)
        {
          i3--;
          if (i3 < i4) {
            break;
          }
          localObject[(i3 + n)] = paramArrayOfFloat1[(i3 + m)];
        }
        arrayOfInt[(++i2)] = paramInt2;
      }
      float[] arrayOfFloat = paramArrayOfFloat1;
      paramArrayOfFloat1 = (float[])localObject;
      localObject = arrayOfFloat;
      int i4 = m;
      m = n;
      n = i4;
      i = i2;
    }
  }
  
  private static void sort(float[] paramArrayOfFloat, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47)
    {
      int j;
      float f3;
      if (paramBoolean)
      {
        j = paramInt1;
        for (int m = j; j < paramInt2; m = j)
        {
          f3 = paramArrayOfFloat[(j + 1)];
          while (f3 < paramArrayOfFloat[m])
          {
            paramArrayOfFloat[(m + 1)] = paramArrayOfFloat[m];
            if (m-- == paramInt1) {
              break;
            }
          }
          paramArrayOfFloat[(m + 1)] = f3;
          j++;
        }
      }
      else
      {
        do
        {
          if (paramInt1 >= paramInt2) {
            return;
          }
        } while (paramArrayOfFloat[(++paramInt1)] >= paramArrayOfFloat[(paramInt1 - 1)]);
        for (j = paramInt1;; j = paramInt1)
        {
          paramInt1++;
          if (paramInt1 > paramInt2) {
            break;
          }
          float f2 = paramArrayOfFloat[j];
          f3 = paramArrayOfFloat[paramInt1];
          if (f2 < f3)
          {
            f3 = f2;
            f2 = paramArrayOfFloat[paramInt1];
          }
          while (f2 < paramArrayOfFloat[(--j)]) {
            paramArrayOfFloat[(j + 2)] = paramArrayOfFloat[j];
          }
          paramArrayOfFloat[(++j + 1)] = f2;
          while (f3 < paramArrayOfFloat[(--j)]) {
            paramArrayOfFloat[(j + 1)] = paramArrayOfFloat[j];
          }
          paramArrayOfFloat[(j + 1)] = f3;
          paramInt1++;
        }
        float f1 = paramArrayOfFloat[paramInt2];
        while (f1 < paramArrayOfFloat[(--paramInt2)]) {
          paramArrayOfFloat[(paramInt2 + 1)] = paramArrayOfFloat[paramInt2];
        }
        paramArrayOfFloat[(paramInt2 + 1)] = f1;
      }
      return;
    }
    int k = (i >> 3) + (i >> 6) + 1;
    int n = paramInt1 + paramInt2 >>> 1;
    int i1 = n - k;
    int i2 = i1 - k;
    int i3 = n + k;
    int i4 = i3 + k;
    float f4;
    if (paramArrayOfFloat[i1] < paramArrayOfFloat[i2])
    {
      f4 = paramArrayOfFloat[i1];
      paramArrayOfFloat[i1] = paramArrayOfFloat[i2];
      paramArrayOfFloat[i2] = f4;
    }
    if (paramArrayOfFloat[n] < paramArrayOfFloat[i1])
    {
      f4 = paramArrayOfFloat[n];
      paramArrayOfFloat[n] = paramArrayOfFloat[i1];
      paramArrayOfFloat[i1] = f4;
      if (f4 < paramArrayOfFloat[i2])
      {
        paramArrayOfFloat[i1] = paramArrayOfFloat[i2];
        paramArrayOfFloat[i2] = f4;
      }
    }
    if (paramArrayOfFloat[i3] < paramArrayOfFloat[n])
    {
      f4 = paramArrayOfFloat[i3];
      paramArrayOfFloat[i3] = paramArrayOfFloat[n];
      paramArrayOfFloat[n] = f4;
      if (f4 < paramArrayOfFloat[i1])
      {
        paramArrayOfFloat[n] = paramArrayOfFloat[i1];
        paramArrayOfFloat[i1] = f4;
        if (f4 < paramArrayOfFloat[i2])
        {
          paramArrayOfFloat[i1] = paramArrayOfFloat[i2];
          paramArrayOfFloat[i2] = f4;
        }
      }
    }
    if (paramArrayOfFloat[i4] < paramArrayOfFloat[i3])
    {
      f4 = paramArrayOfFloat[i4];
      paramArrayOfFloat[i4] = paramArrayOfFloat[i3];
      paramArrayOfFloat[i3] = f4;
      if (f4 < paramArrayOfFloat[n])
      {
        paramArrayOfFloat[i3] = paramArrayOfFloat[n];
        paramArrayOfFloat[n] = f4;
        if (f4 < paramArrayOfFloat[i1])
        {
          paramArrayOfFloat[n] = paramArrayOfFloat[i1];
          paramArrayOfFloat[i1] = f4;
          if (f4 < paramArrayOfFloat[i2])
          {
            paramArrayOfFloat[i1] = paramArrayOfFloat[i2];
            paramArrayOfFloat[i2] = f4;
          }
        }
      }
    }
    float f5 = paramInt1;
    float f6 = paramInt2;
    float f7;
    float f8;
    label834:
    float f10;
    if ((paramArrayOfFloat[i2] != paramArrayOfFloat[i1]) && (paramArrayOfFloat[i1] != paramArrayOfFloat[n]) && (paramArrayOfFloat[n] != paramArrayOfFloat[i3]) && (paramArrayOfFloat[i3] != paramArrayOfFloat[i4]))
    {
      f7 = paramArrayOfFloat[i1];
      f8 = paramArrayOfFloat[i3];
      paramArrayOfFloat[i1] = paramArrayOfFloat[paramInt1];
      paramArrayOfFloat[i3] = paramArrayOfFloat[paramInt2];
      while (paramArrayOfFloat[(++f5)] < f7) {}
      while (paramArrayOfFloat[(--f6)] > f8) {}
      float f9 = f5 - 1;
      float f11;
      for (;;)
      {
        f9++;
        if (f9 > f6) {
          break;
        }
        f11 = paramArrayOfFloat[f9];
        if (f11 < f7)
        {
          paramArrayOfFloat[f9] = paramArrayOfFloat[f5];
          paramArrayOfFloat[f5] = f11;
          f5++;
        }
        else if (f11 > f8)
        {
          while (paramArrayOfFloat[f6] > f8) {
            if (f6-- == f9) {
              break label834;
            }
          }
          if (paramArrayOfFloat[f6] < f7)
          {
            paramArrayOfFloat[f9] = paramArrayOfFloat[f5];
            paramArrayOfFloat[f5] = paramArrayOfFloat[f6];
            f5++;
          }
          else
          {
            paramArrayOfFloat[f9] = paramArrayOfFloat[f6];
          }
          paramArrayOfFloat[f6] = f11;
          f6--;
        }
      }
      paramArrayOfFloat[paramInt1] = paramArrayOfFloat[(f5 - 1)];
      paramArrayOfFloat[(f5 - 1)] = f7;
      paramArrayOfFloat[paramInt2] = paramArrayOfFloat[(f6 + 1)];
      paramArrayOfFloat[(f6 + 1)] = f8;
      sort(paramArrayOfFloat, paramInt1, f5 - 2, paramBoolean);
      sort(paramArrayOfFloat, f6 + 2, paramInt2, false);
      if ((f5 < i2) && (i4 < f6))
      {
        while (paramArrayOfFloat[f5] == f7) {
          f5++;
        }
        while (paramArrayOfFloat[f6] == f8) {
          f6--;
        }
        f10 = f5 - 1;
        for (;;)
        {
          f10++;
          if (f10 > f6) {
            break;
          }
          f11 = paramArrayOfFloat[f10];
          if (f11 == f7)
          {
            paramArrayOfFloat[f10] = paramArrayOfFloat[f5];
            paramArrayOfFloat[f5] = f11;
            f5++;
          }
          else if (f11 == f8)
          {
            while (paramArrayOfFloat[f6] == f8) {
              if (f6-- == f10) {
                break label1067;
              }
            }
            if (paramArrayOfFloat[f6] == f7)
            {
              paramArrayOfFloat[f10] = paramArrayOfFloat[f5];
              paramArrayOfFloat[f5] = paramArrayOfFloat[f6];
              f5++;
            }
            else
            {
              paramArrayOfFloat[f10] = paramArrayOfFloat[f6];
            }
            paramArrayOfFloat[f6] = f11;
            f6--;
          }
        }
      }
      label1067:
      sort(paramArrayOfFloat, f5, f6, false);
    }
    else
    {
      f7 = paramArrayOfFloat[n];
      for (f8 = f5; f8 <= f6; f8++) {
        if (paramArrayOfFloat[f8] != f7)
        {
          f10 = paramArrayOfFloat[f8];
          if (f10 < f7)
          {
            paramArrayOfFloat[f8] = paramArrayOfFloat[f5];
            paramArrayOfFloat[f5] = f10;
            f5++;
          }
          else
          {
            while (paramArrayOfFloat[f6] > f7) {
              f6--;
            }
            if (paramArrayOfFloat[f6] < f7)
            {
              paramArrayOfFloat[f8] = paramArrayOfFloat[f5];
              paramArrayOfFloat[f5] = paramArrayOfFloat[f6];
              f5++;
            }
            else
            {
              paramArrayOfFloat[f8] = paramArrayOfFloat[f6];
            }
            paramArrayOfFloat[f6] = f10;
            f6--;
          }
        }
      }
      sort(paramArrayOfFloat, paramInt1, f5 - 1, paramBoolean);
      sort(paramArrayOfFloat, f6 + 1, paramInt2, false);
    }
  }
  
  static void sort(double[] paramArrayOfDouble1, int paramInt1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4)
  {
    while ((paramInt1 <= paramInt2) && (Double.isNaN(paramArrayOfDouble1[paramInt2]))) {
      paramInt2--;
    }
    int i = paramInt2;
    for (;;)
    {
      i--;
      if (i < paramInt1) {
        break;
      }
      double d1 = paramArrayOfDouble1[i];
      if (d1 != d1)
      {
        paramArrayOfDouble1[i] = paramArrayOfDouble1[paramInt2];
        paramArrayOfDouble1[paramInt2] = d1;
        paramInt2--;
      }
    }
    doSort(paramArrayOfDouble1, paramInt1, paramInt2, paramArrayOfDouble2, paramInt3, paramInt4);
    i = paramInt2;
    while (paramInt1 < i)
    {
      j = paramInt1 + i >>> 1;
      double d2 = paramArrayOfDouble1[j];
      if (d2 < 0.0D) {
        paramInt1 = j + 1;
      } else {
        i = j;
      }
    }
    while ((paramInt1 <= paramInt2) && (Double.doubleToRawLongBits(paramArrayOfDouble1[paramInt1]) < 0L)) {
      paramInt1++;
    }
    int j = paramInt1;
    int k = paramInt1 - 1;
    for (;;)
    {
      j++;
      if (j > paramInt2) {
        break;
      }
      double d3 = paramArrayOfDouble1[j];
      if (d3 != 0.0D) {
        break;
      }
      if (Double.doubleToRawLongBits(d3) < 0L)
      {
        paramArrayOfDouble1[j] = 0.0D;
        paramArrayOfDouble1[(++k)] = -0.0D;
      }
    }
  }
  
  private static void doSort(double[] paramArrayOfDouble1, int paramInt1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4)
  {
    if (paramInt2 - paramInt1 < 286)
    {
      sort(paramArrayOfDouble1, paramInt1, paramInt2, true);
      return;
    }
    int[] arrayOfInt = new int[68];
    int i = 0;
    arrayOfInt[0] = paramInt1;
    int j = paramInt1;
    int m;
    while (j < paramInt2)
    {
      if (paramArrayOfDouble1[j] < paramArrayOfDouble1[(j + 1)]) {
        for (;;)
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfDouble1[(j - 1)] > paramArrayOfDouble1[j])) {
            break;
          }
        }
      }
      if (paramArrayOfDouble1[j] > paramArrayOfDouble1[(j + 1)])
      {
        do
        {
          j++;
        } while ((j <= paramInt2) && (paramArrayOfDouble1[(j - 1)] >= paramArrayOfDouble1[j]));
        k = arrayOfInt[i] - 1;
        m = j;
        for (;;)
        {
          k++;
          if (k >= --m) {
            break;
          }
          double d = paramArrayOfDouble1[k];
          paramArrayOfDouble1[k] = paramArrayOfDouble1[m];
          paramArrayOfDouble1[m] = d;
        }
      }
      else
      {
        k = 33;
        do
        {
          j++;
          if ((j > paramInt2) || (paramArrayOfDouble1[(j - 1)] != paramArrayOfDouble1[j])) {
            break;
          }
          k--;
        } while (k != 0);
        sort(paramArrayOfDouble1, paramInt1, paramInt2, true);
        return;
      }
      i++;
      if (i == 67)
      {
        sort(paramArrayOfDouble1, paramInt1, paramInt2, true);
        return;
      }
      arrayOfInt[i] = j;
    }
    if (arrayOfInt[i] == paramInt2++) {
      arrayOfInt[(++i)] = paramInt2;
    } else if (i == 1) {
      return;
    }
    j = 0;
    int k = 1;
    while (k <<= 1 < i) {
      j = (byte)(j ^ 0x1);
    }
    int i1 = paramInt2 - paramInt1;
    if ((paramArrayOfDouble2 == null) || (paramInt4 < i1) || (paramInt3 + i1 > paramArrayOfDouble2.length))
    {
      paramArrayOfDouble2 = new double[i1];
      paramInt3 = 0;
    }
    Object localObject;
    int n;
    if (j == 0)
    {
      System.arraycopy(paramArrayOfDouble1, paramInt1, paramArrayOfDouble2, paramInt3, i1);
      localObject = paramArrayOfDouble1;
      n = 0;
      paramArrayOfDouble1 = paramArrayOfDouble2;
      m = paramInt3 - paramInt1;
    }
    else
    {
      localObject = paramArrayOfDouble2;
      m = 0;
      n = paramInt3 - paramInt1;
    }
    while (i > 1)
    {
      int i2;
      for (int i3 = (i2 = 0) + 2; i3 <= i; i3 += 2)
      {
        i4 = arrayOfInt[i3];
        int i5 = arrayOfInt[(i3 - 1)];
        int i6 = arrayOfInt[(i3 - 2)];
        int i7 = i6;
        int i8 = i5;
        while (i6 < i4)
        {
          if ((i8 >= i4) || ((i7 < i5) && (paramArrayOfDouble1[(i7 + m)] <= paramArrayOfDouble1[(i8 + m)]))) {
            localObject[(i6 + n)] = paramArrayOfDouble1[(i7++ + m)];
          } else {
            localObject[(i6 + n)] = paramArrayOfDouble1[(i8++ + m)];
          }
          i6++;
        }
        arrayOfInt[(++i2)] = i4;
      }
      if ((i & 0x1) != 0)
      {
        i3 = paramInt2;
        i4 = arrayOfInt[(i - 1)];
        for (;;)
        {
          i3--;
          if (i3 < i4) {
            break;
          }
          localObject[(i3 + n)] = paramArrayOfDouble1[(i3 + m)];
        }
        arrayOfInt[(++i2)] = paramInt2;
      }
      double[] arrayOfDouble = paramArrayOfDouble1;
      paramArrayOfDouble1 = (double[])localObject;
      localObject = arrayOfDouble;
      int i4 = m;
      m = n;
      n = i4;
      i = i2;
    }
  }
  
  private static void sort(double[] paramArrayOfDouble, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47)
    {
      int j;
      if (paramBoolean)
      {
        j = paramInt1;
        for (int m = j; j < paramInt2; m = j)
        {
          double d3 = paramArrayOfDouble[(j + 1)];
          while (d3 < paramArrayOfDouble[m])
          {
            paramArrayOfDouble[(m + 1)] = paramArrayOfDouble[m];
            if (m-- == paramInt1) {
              break;
            }
          }
          paramArrayOfDouble[(m + 1)] = d3;
          j++;
        }
      }
      else
      {
        do
        {
          if (paramInt1 >= paramInt2) {
            return;
          }
        } while (paramArrayOfDouble[(++paramInt1)] >= paramArrayOfDouble[(paramInt1 - 1)]);
        for (j = paramInt1;; j = paramInt1)
        {
          paramInt1++;
          if (paramInt1 > paramInt2) {
            break;
          }
          double d2 = paramArrayOfDouble[j];
          double d4 = paramArrayOfDouble[paramInt1];
          if (d2 < d4)
          {
            d4 = d2;
            d2 = paramArrayOfDouble[paramInt1];
          }
          while (d2 < paramArrayOfDouble[(--j)]) {
            paramArrayOfDouble[(j + 2)] = paramArrayOfDouble[j];
          }
          paramArrayOfDouble[(++j + 1)] = d2;
          while (d4 < paramArrayOfDouble[(--j)]) {
            paramArrayOfDouble[(j + 1)] = paramArrayOfDouble[j];
          }
          paramArrayOfDouble[(j + 1)] = d4;
          paramInt1++;
        }
        double d1 = paramArrayOfDouble[paramInt2];
        while (d1 < paramArrayOfDouble[(--paramInt2)]) {
          paramArrayOfDouble[(paramInt2 + 1)] = paramArrayOfDouble[paramInt2];
        }
        paramArrayOfDouble[(paramInt2 + 1)] = d1;
      }
      return;
    }
    int k = (i >> 3) + (i >> 6) + 1;
    int n = paramInt1 + paramInt2 >>> 1;
    int i1 = n - k;
    int i2 = i1 - k;
    int i3 = n + k;
    int i4 = i3 + k;
    double d5;
    if (paramArrayOfDouble[i1] < paramArrayOfDouble[i2])
    {
      d5 = paramArrayOfDouble[i1];
      paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
      paramArrayOfDouble[i2] = d5;
    }
    if (paramArrayOfDouble[n] < paramArrayOfDouble[i1])
    {
      d5 = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = paramArrayOfDouble[i1];
      paramArrayOfDouble[i1] = d5;
      if (d5 < paramArrayOfDouble[i2])
      {
        paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
        paramArrayOfDouble[i2] = d5;
      }
    }
    if (paramArrayOfDouble[i3] < paramArrayOfDouble[n])
    {
      d5 = paramArrayOfDouble[i3];
      paramArrayOfDouble[i3] = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = d5;
      if (d5 < paramArrayOfDouble[i1])
      {
        paramArrayOfDouble[n] = paramArrayOfDouble[i1];
        paramArrayOfDouble[i1] = d5;
        if (d5 < paramArrayOfDouble[i2])
        {
          paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
          paramArrayOfDouble[i2] = d5;
        }
      }
    }
    if (paramArrayOfDouble[i4] < paramArrayOfDouble[i3])
    {
      d5 = paramArrayOfDouble[i4];
      paramArrayOfDouble[i4] = paramArrayOfDouble[i3];
      paramArrayOfDouble[i3] = d5;
      if (d5 < paramArrayOfDouble[n])
      {
        paramArrayOfDouble[i3] = paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d5;
        if (d5 < paramArrayOfDouble[i1])
        {
          paramArrayOfDouble[n] = paramArrayOfDouble[i1];
          paramArrayOfDouble[i1] = d5;
          if (d5 < paramArrayOfDouble[i2])
          {
            paramArrayOfDouble[i1] = paramArrayOfDouble[i2];
            paramArrayOfDouble[i2] = d5;
          }
        }
      }
    }
    double d6 = paramInt1;
    double d7 = paramInt2;
    double d8;
    double d9;
    if ((paramArrayOfDouble[i2] != paramArrayOfDouble[i1]) && (paramArrayOfDouble[i1] != paramArrayOfDouble[n]) && (paramArrayOfDouble[n] != paramArrayOfDouble[i3]) && (paramArrayOfDouble[i3] != paramArrayOfDouble[i4]))
    {
      d8 = paramArrayOfDouble[i1];
      d9 = paramArrayOfDouble[i3];
      paramArrayOfDouble[i1] = paramArrayOfDouble[paramInt1];
      paramArrayOfDouble[i3] = paramArrayOfDouble[paramInt2];
      while (paramArrayOfDouble[(++d6)] < d8) {}
      while (paramArrayOfDouble[(--d7)] > d9) {}
      double d11 = d6 - 1;
      double d13;
      for (;;)
      {
        d11++;
        if (d11 > d7) {
          break;
        }
        d13 = paramArrayOfDouble[d11];
        if (d13 < d8)
        {
          paramArrayOfDouble[d11] = paramArrayOfDouble[d6];
          paramArrayOfDouble[d6] = d13;
          d6++;
        }
        else if (d13 > d9)
        {
          while (paramArrayOfDouble[d7] > d9) {
            if (d7-- == d11) {
              break label834;
            }
          }
          if (paramArrayOfDouble[d7] < d8)
          {
            paramArrayOfDouble[d11] = paramArrayOfDouble[d6];
            paramArrayOfDouble[d6] = paramArrayOfDouble[d7];
            d6++;
          }
          else
          {
            paramArrayOfDouble[d11] = paramArrayOfDouble[d7];
          }
          paramArrayOfDouble[d7] = d13;
          d7--;
        }
      }
      label834:
      paramArrayOfDouble[paramInt1] = paramArrayOfDouble[(d6 - 1)];
      paramArrayOfDouble[(d6 - 1)] = d8;
      paramArrayOfDouble[paramInt2] = paramArrayOfDouble[(d7 + 1)];
      paramArrayOfDouble[(d7 + 1)] = d9;
      sort(paramArrayOfDouble, paramInt1, d6 - 2, paramBoolean);
      sort(paramArrayOfDouble, d7 + 2, paramInt2, false);
      if ((d6 < i2) && (i4 < d7))
      {
        while (paramArrayOfDouble[d6] == d8) {
          d6++;
        }
        while (paramArrayOfDouble[d7] == d9) {
          d7--;
        }
        double d12 = d6 - 1;
        for (;;)
        {
          d12++;
          if (d12 > d7) {
            break;
          }
          d13 = paramArrayOfDouble[d12];
          if (d13 == d8)
          {
            paramArrayOfDouble[d12] = paramArrayOfDouble[d6];
            paramArrayOfDouble[d6] = d13;
            d6++;
          }
          else if (d13 == d9)
          {
            while (paramArrayOfDouble[d7] == d9) {
              if (d7-- == d12) {
                break label1067;
              }
            }
            if (paramArrayOfDouble[d7] == d8)
            {
              paramArrayOfDouble[d12] = paramArrayOfDouble[d6];
              paramArrayOfDouble[d6] = paramArrayOfDouble[d7];
              d6++;
            }
            else
            {
              paramArrayOfDouble[d12] = paramArrayOfDouble[d7];
            }
            paramArrayOfDouble[d7] = d13;
            d7--;
          }
        }
      }
      label1067:
      sort(paramArrayOfDouble, d6, d7, false);
    }
    else
    {
      d8 = paramArrayOfDouble[n];
      for (d9 = d6; d9 <= d7; d9++) {
        if (paramArrayOfDouble[d9] != d8)
        {
          double d10 = paramArrayOfDouble[d9];
          if (d10 < d8)
          {
            paramArrayOfDouble[d9] = paramArrayOfDouble[d6];
            paramArrayOfDouble[d6] = d10;
            d6++;
          }
          else
          {
            while (paramArrayOfDouble[d7] > d8) {
              d7--;
            }
            if (paramArrayOfDouble[d7] < d8)
            {
              paramArrayOfDouble[d9] = paramArrayOfDouble[d6];
              paramArrayOfDouble[d6] = paramArrayOfDouble[d7];
              d6++;
            }
            else
            {
              paramArrayOfDouble[d9] = paramArrayOfDouble[d7];
            }
            paramArrayOfDouble[d7] = d10;
            d7--;
          }
        }
      }
      sort(paramArrayOfDouble, paramInt1, d6 - 1, paramBoolean);
      sort(paramArrayOfDouble, d7 + 1, paramInt2, false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\DualPivotQuicksort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */