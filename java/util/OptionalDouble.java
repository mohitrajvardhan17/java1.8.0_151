package java.util;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class OptionalDouble
{
  private static final OptionalDouble EMPTY = new OptionalDouble();
  private final boolean isPresent;
  private final double value;
  
  private OptionalDouble()
  {
    isPresent = false;
    value = NaN.0D;
  }
  
  public static OptionalDouble empty()
  {
    return EMPTY;
  }
  
  private OptionalDouble(double paramDouble)
  {
    isPresent = true;
    value = paramDouble;
  }
  
  public static OptionalDouble of(double paramDouble)
  {
    return new OptionalDouble(paramDouble);
  }
  
  public double getAsDouble()
  {
    if (!isPresent) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }
  
  public boolean isPresent()
  {
    return isPresent;
  }
  
  public void ifPresent(DoubleConsumer paramDoubleConsumer)
  {
    if (isPresent) {
      paramDoubleConsumer.accept(value);
    }
  }
  
  public double orElse(double paramDouble)
  {
    return isPresent ? value : paramDouble;
  }
  
  public double orElseGet(DoubleSupplier paramDoubleSupplier)
  {
    return isPresent ? value : paramDoubleSupplier.getAsDouble();
  }
  
  public <X extends Throwable> double orElseThrow(Supplier<X> paramSupplier)
    throws Throwable
  {
    if (isPresent) {
      return value;
    }
    throw ((Throwable)paramSupplier.get());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof OptionalDouble)) {
      return false;
    }
    OptionalDouble localOptionalDouble = (OptionalDouble)paramObject;
    return Double.compare(value, value) == 0;
  }
  
  public int hashCode()
  {
    return isPresent ? Double.hashCode(value) : 0;
  }
  
  public String toString()
  {
    return isPresent ? String.format("OptionalDouble[%s]", new Object[] { Double.valueOf(value) }) : "OptionalDouble.empty";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\OptionalDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */