package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.LongBinaryOperator;

public class LongAccumulator
  extends Striped64
  implements Serializable
{
  private static final long serialVersionUID = 7249069246863182397L;
  private final LongBinaryOperator function;
  private final long identity;
  
  public LongAccumulator(LongBinaryOperator paramLongBinaryOperator, long paramLong)
  {
    function = paramLongBinaryOperator;
    base = (identity = paramLong);
  }
  
  public void accumulate(long paramLong)
  {
    Striped64.Cell[] arrayOfCell;
    long l1;
    long l3;
    if (((arrayOfCell = cells) != null) || (((l3 = function.applyAsLong(l1 = base, paramLong)) != l1) && (!casBase(l1, l3))))
    {
      boolean bool = true;
      int i;
      Striped64.Cell localCell;
      if ((arrayOfCell != null) && ((i = arrayOfCell.length - 1) >= 0) && ((localCell = arrayOfCell[(getProbe() & i)]) != null))
      {
        long l2;
        if ((bool = ((l3 = function.applyAsLong(l2 = value, paramLong)) == l2) || (localCell.cas(l2, l3)) ? 1 : 0) != 0) {}
      }
      else
      {
        longAccumulate(paramLong, function, bool);
      }
    }
  }
  
  public long get()
  {
    Striped64.Cell[] arrayOfCell = cells;
    long l = base;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          l = function.applyAsLong(l, value);
        }
      }
    }
    return l;
  }
  
  public void reset()
  {
    Striped64.Cell[] arrayOfCell = cells;
    base = identity;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          value = identity;
        }
      }
    }
  }
  
  public long getThenReset()
  {
    Striped64.Cell[] arrayOfCell = cells;
    long l1 = base;
    base = identity;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null)
        {
          long l2 = value;
          value = identity;
          l1 = function.applyAsLong(l1, l2);
        }
      }
    }
    return l1;
  }
  
  public String toString()
  {
    return Long.toString(get());
  }
  
  public long longValue()
  {
    return get();
  }
  
  public int intValue()
  {
    return (int)get();
  }
  
  public float floatValue()
  {
    return (float)get();
  }
  
  public double doubleValue()
  {
    return get();
  }
  
  private Object writeReplace()
  {
    return new SerializationProxy(this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Proxy required");
  }
  
  private static class SerializationProxy
    implements Serializable
  {
    private static final long serialVersionUID = 7249069246863182397L;
    private final long value;
    private final LongBinaryOperator function;
    private final long identity;
    
    SerializationProxy(LongAccumulator paramLongAccumulator)
    {
      function = function;
      identity = identity;
      value = paramLongAccumulator.get();
    }
    
    private Object readResolve()
    {
      LongAccumulator localLongAccumulator = new LongAccumulator(function, identity);
      base = value;
      return localLongAccumulator;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\LongAccumulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */