package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class LongAdder
  extends Striped64
  implements Serializable
{
  private static final long serialVersionUID = 7249069246863182397L;
  
  public LongAdder() {}
  
  public void add(long paramLong)
  {
    Striped64.Cell[] arrayOfCell;
    long l1;
    if (((arrayOfCell = cells) != null) || (!casBase(l1 = base, l1 + paramLong)))
    {
      boolean bool = true;
      int i;
      Striped64.Cell localCell;
      long l2;
      if ((arrayOfCell == null) || ((i = arrayOfCell.length - 1) < 0) || ((localCell = arrayOfCell[(getProbe() & i)]) == null) || (!(bool = localCell.cas(l2 = value, l2 + paramLong)))) {
        longAccumulate(paramLong, null, bool);
      }
    }
  }
  
  public void increment()
  {
    add(1L);
  }
  
  public void decrement()
  {
    add(-1L);
  }
  
  public long sum()
  {
    Striped64.Cell[] arrayOfCell = cells;
    long l = base;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          l += value;
        }
      }
    }
    return l;
  }
  
  public void reset()
  {
    Striped64.Cell[] arrayOfCell = cells;
    base = 0L;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          value = 0L;
        }
      }
    }
  }
  
  public long sumThenReset()
  {
    Striped64.Cell[] arrayOfCell = cells;
    long l = base;
    base = 0L;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null)
        {
          l += value;
          value = 0L;
        }
      }
    }
    return l;
  }
  
  public String toString()
  {
    return Long.toString(sum());
  }
  
  public long longValue()
  {
    return sum();
  }
  
  public int intValue()
  {
    return (int)sum();
  }
  
  public float floatValue()
  {
    return (float)sum();
  }
  
  public double doubleValue()
  {
    return sum();
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
    
    SerializationProxy(LongAdder paramLongAdder)
    {
      value = paramLongAdder.sum();
    }
    
    private Object readResolve()
    {
      LongAdder localLongAdder = new LongAdder();
      base = value;
      return localLongAdder;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\LongAdder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */