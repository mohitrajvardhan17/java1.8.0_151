package java.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class OptionalInt
{
  private static final OptionalInt EMPTY = new OptionalInt();
  private final boolean isPresent;
  private final int value;
  
  private OptionalInt()
  {
    isPresent = false;
    value = 0;
  }
  
  public static OptionalInt empty()
  {
    return EMPTY;
  }
  
  private OptionalInt(int paramInt)
  {
    isPresent = true;
    value = paramInt;
  }
  
  public static OptionalInt of(int paramInt)
  {
    return new OptionalInt(paramInt);
  }
  
  public int getAsInt()
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
  
  public void ifPresent(IntConsumer paramIntConsumer)
  {
    if (isPresent) {
      paramIntConsumer.accept(value);
    }
  }
  
  public int orElse(int paramInt)
  {
    return isPresent ? value : paramInt;
  }
  
  public int orElseGet(IntSupplier paramIntSupplier)
  {
    return isPresent ? value : paramIntSupplier.getAsInt();
  }
  
  public <X extends Throwable> int orElseThrow(Supplier<X> paramSupplier)
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
    if (!(paramObject instanceof OptionalInt)) {
      return false;
    }
    OptionalInt localOptionalInt = (OptionalInt)paramObject;
    return value == value;
  }
  
  public int hashCode()
  {
    return isPresent ? Integer.hashCode(value) : 0;
  }
  
  public String toString()
  {
    return isPresent ? String.format("OptionalInt[%s]", new Object[] { Integer.valueOf(value) }) : "OptionalInt.empty";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\OptionalInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */