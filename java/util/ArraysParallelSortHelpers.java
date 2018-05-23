package java.util;

import java.util.concurrent.CountedCompleter;

class ArraysParallelSortHelpers
{
  ArraysParallelSortHelpers() {}
  
  static final class EmptyCompleter
    extends CountedCompleter<Void>
  {
    static final long serialVersionUID = 2446542900576103244L;
    
    EmptyCompleter(CountedCompleter<?> paramCountedCompleter)
    {
      super();
    }
    
    public final void compute() {}
  }
  
  static final class FJByte
  {
    FJByte() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final byte[] a;
      final byte[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfByte1;
        w = paramArrayOfByte2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        byte[] arrayOfByte1 = a;
        byte[] arrayOfByte2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfByte1 == null) || (arrayOfByte2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        Merger localMerger2;
        Merger localMerger3;
        Merger localMerger1;
        for (;;)
        {
          int i4;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            i4 = arrayOfByte1[((i2 = j >>> 1) + i)];
            localMerger2 = 0;
            while (localMerger2 < i3)
            {
              localMerger3 = localMerger2 + i3 >>> 1;
              if (i4 <= arrayOfByte1[(localMerger3 + k)]) {
                i3 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            i4 = arrayOfByte1[((i3 = m >>> 1) + k)];
            localMerger2 = 0;
            while (localMerger2 < i2)
            {
              localMerger3 = localMerger2 + i2 >>> 1;
              if (i4 <= arrayOfByte1[(localMerger3 + i)]) {
                i2 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          localMerger1 = new Merger(this, arrayOfByte1, arrayOfByte2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger1.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          if ((localMerger2 = arrayOfByte1[i]) <= (localMerger3 = arrayOfByte1[k]))
          {
            i++;
            localMerger1 = localMerger2;
          }
          else
          {
            k++;
            localMerger1 = localMerger3;
          }
          arrayOfByte2[(n++)] = localMerger1;
        }
        if (k < i3) {
          System.arraycopy(arrayOfByte1, k, arrayOfByte2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfByte1, i, arrayOfByte2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final byte[] a;
      final byte[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfByte1;
        w = paramArrayOfByte2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        byte[] arrayOfByte1 = a;
        byte[] arrayOfByte2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger((CountedCompleter)localObject, arrayOfByte2, arrayOfByte1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(localRelay1, arrayOfByte1, arrayOfByte2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfByte1, arrayOfByte2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfByte1, arrayOfByte2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(localRelay1, arrayOfByte1, arrayOfByte2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfByte1, arrayOfByte2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfByte1, i, i + j - 1);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJChar
  {
    FJChar() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final char[] a;
      final char[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, char[] paramArrayOfChar1, char[] paramArrayOfChar2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfChar1;
        w = paramArrayOfChar2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        char[] arrayOfChar1 = a;
        char[] arrayOfChar2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfChar1 == null) || (arrayOfChar2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        Merger localMerger2;
        Merger localMerger3;
        Merger localMerger1;
        for (;;)
        {
          int i4;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            i4 = arrayOfChar1[((i2 = j >>> 1) + i)];
            localMerger2 = 0;
            while (localMerger2 < i3)
            {
              localMerger3 = localMerger2 + i3 >>> 1;
              if (i4 <= arrayOfChar1[(localMerger3 + k)]) {
                i3 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            i4 = arrayOfChar1[((i3 = m >>> 1) + k)];
            localMerger2 = 0;
            while (localMerger2 < i2)
            {
              localMerger3 = localMerger2 + i2 >>> 1;
              if (i4 <= arrayOfChar1[(localMerger3 + i)]) {
                i2 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          localMerger1 = new Merger(this, arrayOfChar1, arrayOfChar2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger1.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          if ((localMerger2 = arrayOfChar1[i]) <= (localMerger3 = arrayOfChar1[k]))
          {
            i++;
            localMerger1 = localMerger2;
          }
          else
          {
            k++;
            localMerger1 = localMerger3;
          }
          arrayOfChar2[(n++)] = localMerger1;
        }
        if (k < i3) {
          System.arraycopy(arrayOfChar1, k, arrayOfChar2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfChar1, i, arrayOfChar2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final char[] a;
      final char[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, char[] paramArrayOfChar1, char[] paramArrayOfChar2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfChar1;
        w = paramArrayOfChar2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        char[] arrayOfChar1 = a;
        char[] arrayOfChar2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger((CountedCompleter)localObject, arrayOfChar2, arrayOfChar1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(localRelay1, arrayOfChar1, arrayOfChar2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfChar1, arrayOfChar2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfChar1, arrayOfChar2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(localRelay1, arrayOfChar1, arrayOfChar2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfChar1, arrayOfChar2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfChar1, i, i + j - 1, arrayOfChar2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJDouble
  {
    FJDouble() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final double[] a;
      final double[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfDouble1;
        w = paramArrayOfDouble2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        double[] arrayOfDouble1 = a;
        double[] arrayOfDouble2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfDouble1 == null) || (arrayOfDouble2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        for (;;)
        {
          double d1;
          int i4;
          int i5;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            d1 = arrayOfDouble1[((i2 = j >>> 1) + i)];
            i4 = 0;
            while (i4 < i3)
            {
              i5 = i4 + i3 >>> 1;
              if (d1 <= arrayOfDouble1[(i5 + k)]) {
                i3 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            d1 = arrayOfDouble1[((i3 = m >>> 1) + k)];
            i4 = 0;
            while (i4 < i2)
            {
              i5 = i4 + i2 >>> 1;
              if (d1 <= arrayOfDouble1[(i5 + i)]) {
                i2 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          Merger localMerger = new Merger(this, arrayOfDouble1, arrayOfDouble2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          double d3;
          double d4;
          double d2;
          if ((d3 = arrayOfDouble1[i]) <= (d4 = arrayOfDouble1[k]))
          {
            i++;
            d2 = d3;
          }
          else
          {
            k++;
            d2 = d4;
          }
          arrayOfDouble2[(n++)] = d2;
        }
        if (k < i3) {
          System.arraycopy(arrayOfDouble1, k, arrayOfDouble2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfDouble1, i, arrayOfDouble2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final double[] a;
      final double[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfDouble1;
        w = paramArrayOfDouble2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        double[] arrayOfDouble1 = a;
        double[] arrayOfDouble2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger((CountedCompleter)localObject, arrayOfDouble2, arrayOfDouble1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(localRelay1, arrayOfDouble1, arrayOfDouble2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfDouble1, arrayOfDouble2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfDouble1, arrayOfDouble2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(localRelay1, arrayOfDouble1, arrayOfDouble2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfDouble1, arrayOfDouble2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfDouble1, i, i + j - 1, arrayOfDouble2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJFloat
  {
    FJFloat() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final float[] a;
      final float[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfFloat1;
        w = paramArrayOfFloat2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        float[] arrayOfFloat1 = a;
        float[] arrayOfFloat2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfFloat1 == null) || (arrayOfFloat2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        for (;;)
        {
          float f1;
          int i4;
          int i5;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            f1 = arrayOfFloat1[((i2 = j >>> 1) + i)];
            i4 = 0;
            while (i4 < i3)
            {
              i5 = i4 + i3 >>> 1;
              if (f1 <= arrayOfFloat1[(i5 + k)]) {
                i3 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            f1 = arrayOfFloat1[((i3 = m >>> 1) + k)];
            i4 = 0;
            while (i4 < i2)
            {
              i5 = i4 + i2 >>> 1;
              if (f1 <= arrayOfFloat1[(i5 + i)]) {
                i2 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          Merger localMerger = new Merger(this, arrayOfFloat1, arrayOfFloat2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          float f3;
          float f4;
          float f2;
          if ((f3 = arrayOfFloat1[i]) <= (f4 = arrayOfFloat1[k]))
          {
            i++;
            f2 = f3;
          }
          else
          {
            k++;
            f2 = f4;
          }
          arrayOfFloat2[(n++)] = f2;
        }
        if (k < i3) {
          System.arraycopy(arrayOfFloat1, k, arrayOfFloat2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfFloat1, i, arrayOfFloat2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final float[] a;
      final float[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfFloat1;
        w = paramArrayOfFloat2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        float[] arrayOfFloat1 = a;
        float[] arrayOfFloat2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger((CountedCompleter)localObject, arrayOfFloat2, arrayOfFloat1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(localRelay1, arrayOfFloat1, arrayOfFloat2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfFloat1, arrayOfFloat2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfFloat1, arrayOfFloat2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(localRelay1, arrayOfFloat1, arrayOfFloat2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfFloat1, arrayOfFloat2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfFloat1, i, i + j - 1, arrayOfFloat2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJInt
  {
    FJInt() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final int[] a;
      final int[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfInt1;
        w = paramArrayOfInt2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        int[] arrayOfInt1 = a;
        int[] arrayOfInt2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfInt1 == null) || (arrayOfInt2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        Merger localMerger2;
        Merger localMerger3;
        Merger localMerger1;
        for (;;)
        {
          int i4;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            i4 = arrayOfInt1[((i2 = j >>> 1) + i)];
            localMerger2 = 0;
            while (localMerger2 < i3)
            {
              localMerger3 = localMerger2 + i3 >>> 1;
              if (i4 <= arrayOfInt1[(localMerger3 + k)]) {
                i3 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            i4 = arrayOfInt1[((i3 = m >>> 1) + k)];
            localMerger2 = 0;
            while (localMerger2 < i2)
            {
              localMerger3 = localMerger2 + i2 >>> 1;
              if (i4 <= arrayOfInt1[(localMerger3 + i)]) {
                i2 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          localMerger1 = new Merger(this, arrayOfInt1, arrayOfInt2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger1.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          if ((localMerger2 = arrayOfInt1[i]) <= (localMerger3 = arrayOfInt1[k]))
          {
            i++;
            localMerger1 = localMerger2;
          }
          else
          {
            k++;
            localMerger1 = localMerger3;
          }
          arrayOfInt2[(n++)] = localMerger1;
        }
        if (k < i3) {
          System.arraycopy(arrayOfInt1, k, arrayOfInt2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfInt1, i, arrayOfInt2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final int[] a;
      final int[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfInt1;
        w = paramArrayOfInt2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        int[] arrayOfInt1 = a;
        int[] arrayOfInt2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger((CountedCompleter)localObject, arrayOfInt2, arrayOfInt1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(localRelay1, arrayOfInt1, arrayOfInt2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfInt1, arrayOfInt2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfInt1, arrayOfInt2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(localRelay1, arrayOfInt1, arrayOfInt2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfInt1, arrayOfInt2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfInt1, i, i + j - 1, arrayOfInt2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJLong
  {
    FJLong() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final long[] a;
      final long[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, long[] paramArrayOfLong1, long[] paramArrayOfLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfLong1;
        w = paramArrayOfLong2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        long[] arrayOfLong1 = a;
        long[] arrayOfLong2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfLong1 == null) || (arrayOfLong2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        for (;;)
        {
          long l1;
          int i4;
          int i5;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            l1 = arrayOfLong1[((i2 = j >>> 1) + i)];
            i4 = 0;
            while (i4 < i3)
            {
              i5 = i4 + i3 >>> 1;
              if (l1 <= arrayOfLong1[(i5 + k)]) {
                i3 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            l1 = arrayOfLong1[((i3 = m >>> 1) + k)];
            i4 = 0;
            while (i4 < i2)
            {
              i5 = i4 + i2 >>> 1;
              if (l1 <= arrayOfLong1[(i5 + i)]) {
                i2 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          Merger localMerger = new Merger(this, arrayOfLong1, arrayOfLong2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          long l3;
          long l4;
          long l2;
          if ((l3 = arrayOfLong1[i]) <= (l4 = arrayOfLong1[k]))
          {
            i++;
            l2 = l3;
          }
          else
          {
            k++;
            l2 = l4;
          }
          arrayOfLong2[(n++)] = l2;
        }
        if (k < i3) {
          System.arraycopy(arrayOfLong1, k, arrayOfLong2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfLong1, i, arrayOfLong2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final long[] a;
      final long[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, long[] paramArrayOfLong1, long[] paramArrayOfLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfLong1;
        w = paramArrayOfLong2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        long[] arrayOfLong1 = a;
        long[] arrayOfLong2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger((CountedCompleter)localObject, arrayOfLong2, arrayOfLong1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(localRelay1, arrayOfLong1, arrayOfLong2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfLong1, arrayOfLong2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfLong1, arrayOfLong2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(localRelay1, arrayOfLong1, arrayOfLong2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfLong1, arrayOfLong2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfLong1, i, i + j - 1, arrayOfLong2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJObject
  {
    FJObject() {}
    
    static final class Merger<T>
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final T[] a;
      final T[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      Comparator<? super T> comparator;
      
      Merger(CountedCompleter<?> paramCountedCompleter, T[] paramArrayOfT1, T[] paramArrayOfT2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Comparator<? super T> paramComparator)
      {
        super();
        a = paramArrayOfT1;
        w = paramArrayOfT2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
        comparator = paramComparator;
      }
      
      public final void compute()
      {
        Comparator localComparator = comparator;
        Object[] arrayOfObject1 = a;
        Object[] arrayOfObject2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfObject1 == null) || (arrayOfObject2 == null) || (i < 0) || (k < 0) || (n < 0) || (localComparator == null)) {
          throw new IllegalStateException();
        }
        Object localObject1;
        for (;;)
        {
          int i4;
          int i5;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            localObject1 = arrayOfObject1[((i2 = j >>> 1) + i)];
            i4 = 0;
            while (i4 < i3)
            {
              i5 = i4 + i3 >>> 1;
              if (localComparator.compare(localObject1, arrayOfObject1[(i5 + k)]) <= 0) {
                i3 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            localObject1 = arrayOfObject1[((i3 = m >>> 1) + k)];
            i4 = 0;
            while (i4 < i2)
            {
              i5 = i4 + i2 >>> 1;
              if (localComparator.compare(localObject1, arrayOfObject1[(i5 + i)]) <= 0) {
                i2 = i5;
              } else {
                i4 = i5 + 1;
              }
            }
          }
          localObject1 = new Merger(this, arrayOfObject1, arrayOfObject2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1, localComparator);
          m = i3;
          j = i2;
          addToPendingCount(1);
          ((Merger)localObject1).fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          Object localObject2;
          Object localObject3;
          if (localComparator.compare(localObject2 = arrayOfObject1[i], localObject3 = arrayOfObject1[k]) <= 0)
          {
            i++;
            localObject1 = localObject2;
          }
          else
          {
            k++;
            localObject1 = localObject3;
          }
          arrayOfObject2[(n++)] = localObject1;
        }
        if (k < i3) {
          System.arraycopy(arrayOfObject1, k, arrayOfObject2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfObject1, i, arrayOfObject2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter<T>
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final T[] a;
      final T[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      Comparator<? super T> comparator;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, T[] paramArrayOfT1, T[] paramArrayOfT2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Comparator<? super T> paramComparator)
      {
        super();
        a = paramArrayOfT1;
        w = paramArrayOfT2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
        comparator = paramComparator;
      }
      
      public final void compute()
      {
        Object localObject = this;
        Comparator localComparator = comparator;
        Object[] arrayOfObject1 = a;
        Object[] arrayOfObject2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger((CountedCompleter)localObject, arrayOfObject2, arrayOfObject1, k, n, k + n, j - n, i, m, localComparator));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(localRelay1, arrayOfObject1, arrayOfObject2, i + n, i1, i + i2, j - i2, k + n, m, localComparator));
          new Sorter(localRelay2, arrayOfObject1, arrayOfObject2, i + i2, j - i2, k + i2, m, localComparator).fork();
          new Sorter(localRelay2, arrayOfObject1, arrayOfObject2, i + n, i1, k + n, m, localComparator).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(localRelay1, arrayOfObject1, arrayOfObject2, i, i1, i + i1, n - i1, k, m, localComparator));
          new Sorter(localRelay3, arrayOfObject1, arrayOfObject2, i + i1, n - i1, k + i1, m, localComparator).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        TimSort.sort(arrayOfObject1, i, i + j, localComparator, arrayOfObject2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class FJShort
  {
    FJShort() {}
    
    static final class Merger
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final short[] a;
      final short[] w;
      final int lbase;
      final int lsize;
      final int rbase;
      final int rsize;
      final int wbase;
      final int gran;
      
      Merger(CountedCompleter<?> paramCountedCompleter, short[] paramArrayOfShort1, short[] paramArrayOfShort2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        a = paramArrayOfShort1;
        w = paramArrayOfShort2;
        lbase = paramInt1;
        lsize = paramInt2;
        rbase = paramInt3;
        rsize = paramInt4;
        wbase = paramInt5;
        gran = paramInt6;
      }
      
      public final void compute()
      {
        short[] arrayOfShort1 = a;
        short[] arrayOfShort2 = w;
        int i = lbase;
        int j = lsize;
        int k = rbase;
        int m = rsize;
        int n = wbase;
        int i1 = gran;
        if ((arrayOfShort1 == null) || (arrayOfShort2 == null) || (i < 0) || (k < 0) || (n < 0)) {
          throw new IllegalStateException();
        }
        Merger localMerger2;
        Merger localMerger3;
        Merger localMerger1;
        for (;;)
        {
          int i4;
          if (j >= m)
          {
            if (j <= i1) {
              break;
            }
            i3 = m;
            i4 = arrayOfShort1[((i2 = j >>> 1) + i)];
            localMerger2 = 0;
            while (localMerger2 < i3)
            {
              localMerger3 = localMerger2 + i3 >>> 1;
              if (i4 <= arrayOfShort1[(localMerger3 + k)]) {
                i3 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          else
          {
            if (m <= i1) {
              break;
            }
            i2 = j;
            i4 = arrayOfShort1[((i3 = m >>> 1) + k)];
            localMerger2 = 0;
            while (localMerger2 < i2)
            {
              localMerger3 = localMerger2 + i2 >>> 1;
              if (i4 <= arrayOfShort1[(localMerger3 + i)]) {
                i2 = localMerger3;
              } else {
                localMerger2 = localMerger3 + 1;
              }
            }
          }
          localMerger1 = new Merger(this, arrayOfShort1, arrayOfShort2, i + i2, j - i2, k + i3, m - i3, n + i2 + i3, i1);
          m = i3;
          j = i2;
          addToPendingCount(1);
          localMerger1.fork();
        }
        int i2 = i + j;
        int i3 = k + m;
        while ((i < i2) && (k < i3))
        {
          if ((localMerger2 = arrayOfShort1[i]) <= (localMerger3 = arrayOfShort1[k]))
          {
            i++;
            localMerger1 = localMerger2;
          }
          else
          {
            k++;
            localMerger1 = localMerger3;
          }
          arrayOfShort2[(n++)] = localMerger1;
        }
        if (k < i3) {
          System.arraycopy(arrayOfShort1, k, arrayOfShort2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfShort1, i, arrayOfShort2, n, i2 - i);
        }
        tryComplete();
      }
    }
    
    static final class Sorter
      extends CountedCompleter<Void>
    {
      static final long serialVersionUID = 2446542900576103244L;
      final short[] a;
      final short[] w;
      final int base;
      final int size;
      final int wbase;
      final int gran;
      
      Sorter(CountedCompleter<?> paramCountedCompleter, short[] paramArrayOfShort1, short[] paramArrayOfShort2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super();
        a = paramArrayOfShort1;
        w = paramArrayOfShort2;
        base = paramInt1;
        size = paramInt2;
        wbase = paramInt3;
        gran = paramInt4;
      }
      
      public final void compute()
      {
        Object localObject = this;
        short[] arrayOfShort1 = a;
        short[] arrayOfShort2 = w;
        int i = base;
        int j = size;
        int k = wbase;
        int m = gran;
        while (j > m)
        {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay localRelay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger((CountedCompleter)localObject, arrayOfShort2, arrayOfShort1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay localRelay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(localRelay1, arrayOfShort1, arrayOfShort2, i + n, i1, i + i2, j - i2, k + n, m));
          new Sorter(localRelay2, arrayOfShort1, arrayOfShort2, i + i2, j - i2, k + i2, m).fork();
          new Sorter(localRelay2, arrayOfShort1, arrayOfShort2, i + n, i1, k + n, m).fork();
          ArraysParallelSortHelpers.Relay localRelay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(localRelay1, arrayOfShort1, arrayOfShort2, i, i1, i + i1, n - i1, k, m));
          new Sorter(localRelay3, arrayOfShort1, arrayOfShort2, i + i1, n - i1, k + i1, m).fork();
          localObject = new ArraysParallelSortHelpers.EmptyCompleter(localRelay3);
          j = i1;
        }
        DualPivotQuicksort.sort(arrayOfShort1, i, i + j - 1, arrayOfShort2, k, j);
        ((CountedCompleter)localObject).tryComplete();
      }
    }
  }
  
  static final class Relay
    extends CountedCompleter<Void>
  {
    static final long serialVersionUID = 2446542900576103244L;
    final CountedCompleter<?> task;
    
    Relay(CountedCompleter<?> paramCountedCompleter)
    {
      super(1);
      task = paramCountedCompleter;
    }
    
    public final void compute() {}
    
    public final void onCompletion(CountedCompleter<?> paramCountedCompleter)
    {
      task.compute();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ArraysParallelSortHelpers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */