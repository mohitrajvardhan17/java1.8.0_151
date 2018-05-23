package java.util.concurrent.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;
import sun.misc.Contended;
import sun.misc.Unsafe;

abstract class Striped64
  extends Number
{
  static final int NCPU = Runtime.getRuntime().availableProcessors();
  volatile transient Cell[] cells;
  volatile transient long base;
  volatile transient int cellsBusy;
  private static final Unsafe UNSAFE;
  private static final long BASE;
  private static final long CELLSBUSY;
  private static final long PROBE;
  
  Striped64() {}
  
  final boolean casBase(long paramLong1, long paramLong2)
  {
    return UNSAFE.compareAndSwapLong(this, BASE, paramLong1, paramLong2);
  }
  
  final boolean casCellsBusy()
  {
    return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1);
  }
  
  static final int getProbe()
  {
    return UNSAFE.getInt(Thread.currentThread(), PROBE);
  }
  
  static final int advanceProbe(int paramInt)
  {
    paramInt ^= paramInt << 13;
    paramInt ^= paramInt >>> 17;
    paramInt ^= paramInt << 5;
    UNSAFE.putInt(Thread.currentThread(), PROBE, paramInt);
    return paramInt;
  }
  
  final void longAccumulate(long paramLong, LongBinaryOperator paramLongBinaryOperator, boolean paramBoolean)
  {
    int i;
    if ((i = getProbe()) == 0)
    {
      ThreadLocalRandom.current();
      i = getProbe();
      paramBoolean = true;
    }
    int j = 0;
    for (;;)
    {
      Cell[] arrayOfCell1;
      int k;
      long l;
      if (((arrayOfCell1 = cells) != null) && ((k = arrayOfCell1.length) > 0))
      {
        Cell localCell;
        Object localObject1;
        int n;
        if ((localCell = arrayOfCell1[(k - 1 & i)]) == null)
        {
          if (cellsBusy == 0)
          {
            localObject1 = new Cell(paramLong);
            if ((cellsBusy == 0) && (casCellsBusy()))
            {
              n = 0;
              try
              {
                Cell[] arrayOfCell3;
                int i1;
                int i2;
                if (((arrayOfCell3 = cells) != null) && ((i1 = arrayOfCell3.length) > 0) && (arrayOfCell3[(i2 = i1 - 1 & i)] == null))
                {
                  arrayOfCell3[i2] = localObject1;
                  n = 1;
                }
              }
              finally
              {
                cellsBusy = 0;
              }
              if (n == 0) {
                continue;
              }
              break;
            }
          }
          j = 0;
        }
        else if (!paramBoolean)
        {
          paramBoolean = true;
        }
        else
        {
          if (localCell.cas(l = value, paramLongBinaryOperator == null ? l + paramLong : paramLongBinaryOperator.applyAsLong(l, paramLong))) {
            break;
          }
          if ((k >= NCPU) || (cells != arrayOfCell1))
          {
            j = 0;
          }
          else if (j == 0)
          {
            j = 1;
          }
          else if ((cellsBusy == 0) && (casCellsBusy()))
          {
            try
            {
              if (cells == arrayOfCell1)
              {
                localObject1 = new Cell[k << 1];
                for (n = 0; n < k; n++) {
                  localObject1[n] = arrayOfCell1[n];
                }
                cells = ((Cell[])localObject1);
              }
            }
            finally
            {
              cellsBusy = 0;
            }
            j = 0;
            continue;
          }
        }
        i = advanceProbe(i);
      }
      else if ((cellsBusy == 0) && (cells == arrayOfCell1) && (casCellsBusy()))
      {
        int m = 0;
        try
        {
          if (cells == arrayOfCell1)
          {
            Cell[] arrayOfCell2 = new Cell[2];
            arrayOfCell2[(i & 0x1)] = new Cell(paramLong);
            cells = arrayOfCell2;
            m = 1;
          }
        }
        finally
        {
          cellsBusy = 0;
        }
        if (m != 0) {
          break;
        }
      }
      else
      {
        if (casBase(l = base, paramLongBinaryOperator == null ? l + paramLong : paramLongBinaryOperator.applyAsLong(l, paramLong))) {
          break;
        }
      }
    }
  }
  
  final void doubleAccumulate(double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator, boolean paramBoolean)
  {
    int i;
    if ((i = getProbe()) == 0)
    {
      ThreadLocalRandom.current();
      i = getProbe();
      paramBoolean = true;
    }
    int j = 0;
    for (;;)
    {
      Cell[] arrayOfCell1;
      int k;
      long l;
      if (((arrayOfCell1 = cells) != null) && ((k = arrayOfCell1.length) > 0))
      {
        Cell localCell;
        Object localObject1;
        int n;
        if ((localCell = arrayOfCell1[(k - 1 & i)]) == null)
        {
          if (cellsBusy == 0)
          {
            localObject1 = new Cell(Double.doubleToRawLongBits(paramDouble));
            if ((cellsBusy == 0) && (casCellsBusy()))
            {
              n = 0;
              try
              {
                Cell[] arrayOfCell3;
                int i1;
                int i2;
                if (((arrayOfCell3 = cells) != null) && ((i1 = arrayOfCell3.length) > 0) && (arrayOfCell3[(i2 = i1 - 1 & i)] == null))
                {
                  arrayOfCell3[i2] = localObject1;
                  n = 1;
                }
              }
              finally
              {
                cellsBusy = 0;
              }
              if (n == 0) {
                continue;
              }
              break;
            }
          }
          j = 0;
        }
        else if (!paramBoolean)
        {
          paramBoolean = true;
        }
        else
        {
          if (localCell.cas(l = value, paramDoubleBinaryOperator == null ? Double.doubleToRawLongBits(Double.longBitsToDouble(l) + paramDouble) : Double.doubleToRawLongBits(paramDoubleBinaryOperator.applyAsDouble(Double.longBitsToDouble(l), paramDouble)))) {
            break;
          }
          if ((k >= NCPU) || (cells != arrayOfCell1))
          {
            j = 0;
          }
          else if (j == 0)
          {
            j = 1;
          }
          else if ((cellsBusy == 0) && (casCellsBusy()))
          {
            try
            {
              if (cells == arrayOfCell1)
              {
                localObject1 = new Cell[k << 1];
                for (n = 0; n < k; n++) {
                  localObject1[n] = arrayOfCell1[n];
                }
                cells = ((Cell[])localObject1);
              }
            }
            finally
            {
              cellsBusy = 0;
            }
            j = 0;
            continue;
          }
        }
        i = advanceProbe(i);
      }
      else if ((cellsBusy == 0) && (cells == arrayOfCell1) && (casCellsBusy()))
      {
        int m = 0;
        try
        {
          if (cells == arrayOfCell1)
          {
            Cell[] arrayOfCell2 = new Cell[2];
            arrayOfCell2[(i & 0x1)] = new Cell(Double.doubleToRawLongBits(paramDouble));
            cells = arrayOfCell2;
            m = 1;
          }
        }
        finally
        {
          cellsBusy = 0;
        }
        if (m != 0) {
          break;
        }
      }
      else
      {
        if (casBase(l = base, paramDoubleBinaryOperator == null ? Double.doubleToRawLongBits(Double.longBitsToDouble(l) + paramDouble) : Double.doubleToRawLongBits(paramDoubleBinaryOperator.applyAsDouble(Double.longBitsToDouble(l), paramDouble)))) {
          break;
        }
      }
    }
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass1 = Striped64.class;
      BASE = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("base"));
      CELLSBUSY = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("cellsBusy"));
      Class localClass2 = Thread.class;
      PROBE = UNSAFE.objectFieldOffset(localClass2.getDeclaredField("threadLocalRandomProbe"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  @Contended
  static final class Cell
  {
    volatile long value;
    private static final Unsafe UNSAFE;
    private static final long valueOffset;
    
    Cell(long paramLong)
    {
      value = paramLong;
    }
    
    final boolean cas(long paramLong1, long paramLong2)
    {
      return UNSAFE.compareAndSwapLong(this, valueOffset, paramLong1, paramLong2);
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Class localClass = Cell.class;
        valueOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("value"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\Striped64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */