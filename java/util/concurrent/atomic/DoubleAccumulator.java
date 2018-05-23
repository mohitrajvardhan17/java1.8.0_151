package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public class DoubleAccumulator
  extends Striped64
  implements Serializable
{
  private static final long serialVersionUID = 7249069246863182397L;
  private final DoubleBinaryOperator function;
  private final long identity;
  
  public DoubleAccumulator(DoubleBinaryOperator paramDoubleBinaryOperator, double paramDouble)
  {
    function = paramDoubleBinaryOperator;
    base = (identity = Double.doubleToRawLongBits(paramDouble));
  }
  
  public void accumulate(double paramDouble)
  {
    Striped64.Cell[] arrayOfCell;
    long l1;
    long l3;
    if (((arrayOfCell = cells) != null) || (((l3 = Double.doubleToRawLongBits(function.applyAsDouble(Double.longBitsToDouble(l1 = base), paramDouble))) != l1) && (!casBase(l1, l3))))
    {
      boolean bool = true;
      int i;
      Striped64.Cell localCell;
      if ((arrayOfCell != null) && ((i = arrayOfCell.length - 1) >= 0) && ((localCell = arrayOfCell[(getProbe() & i)]) != null))
      {
        long l2;
        if ((bool = ((l3 = Double.doubleToRawLongBits(function.applyAsDouble(Double.longBitsToDouble(l2 = value), paramDouble))) == l2) || (localCell.cas(l2, l3)) ? 1 : 0) != 0) {}
      }
      else
      {
        doubleAccumulate(paramDouble, function, bool);
      }
    }
  }
  
  public double get()
  {
    Striped64.Cell[] arrayOfCell = cells;
    double d = Double.longBitsToDouble(base);
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null) {
          d = function.applyAsDouble(d, Double.longBitsToDouble(value));
        }
      }
    }
    return d;
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
  
  public double getThenReset()
  {
    Striped64.Cell[] arrayOfCell = cells;
    double d1 = Double.longBitsToDouble(base);
    base = identity;
    if (arrayOfCell != null) {
      for (int i = 0; i < arrayOfCell.length; i++)
      {
        Striped64.Cell localCell;
        if ((localCell = arrayOfCell[i]) != null)
        {
          double d2 = Double.longBitsToDouble(value);
          value = identity;
          d1 = function.applyAsDouble(d1, d2);
        }
      }
    }
    return d1;
  }
  
  public String toString()
  {
    return Double.toString(get());
  }
  
  public double doubleValue()
  {
    return get();
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
    private final double value;
    private final DoubleBinaryOperator function;
    private final long identity;
    
    SerializationProxy(DoubleAccumulator paramDoubleAccumulator)
    {
      function = function;
      identity = identity;
      value = paramDoubleAccumulator.get();
    }
    
    private Object readResolve()
    {
      double d = Double.longBitsToDouble(identity);
      DoubleAccumulator localDoubleAccumulator = new DoubleAccumulator(function, d);
      base = Double.doubleToRawLongBits(value);
      return localDoubleAccumulator;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\DoubleAccumulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */