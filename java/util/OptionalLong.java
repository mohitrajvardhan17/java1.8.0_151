package java.util;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class OptionalLong
{
  private static final OptionalLong EMPTY = new OptionalLong();
  private final boolean isPresent;
  private final long value;
  
  private OptionalLong()
  {
    isPresent = false;
    value = 0L;
  }
  
  public static OptionalLong empty()
  {
    return EMPTY;
  }
  
  private OptionalLong(long paramLong)
  {
    isPresent = true;
    value = paramLong;
  }
  
  public static OptionalLong of(long paramLong)
  {
    return new OptionalLong(paramLong);
  }
  
  public long getAsLong()
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
  
  public void ifPresent(LongConsumer paramLongConsumer)
  {
    if (isPresent) {
      paramLongConsumer.accept(value);
    }
  }
  
  public long orElse(long paramLong)
  {
    return isPresent ? value : paramLong;
  }
  
  public long orElseGet(LongSupplier paramLongSupplier)
  {
    return isPresent ? value : paramLongSupplier.getAsLong();
  }
  
  public <X extends Throwable> long orElseThrow(Supplier<X> paramSupplier)
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
    if (!(paramObject instanceof OptionalLong)) {
      return false;
    }
    OptionalLong localOptionalLong = (OptionalLong)paramObject;
    return value == value;
  }
  
  public int hashCode()
  {
    return isPresent ? Long.hashCode(value) : 0;
  }
  
  public String toString()
  {
    return isPresent ? String.format("OptionalLong[%s]", new Object[] { Long.valueOf(value) }) : "OptionalLong.empty";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\OptionalLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */