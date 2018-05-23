package java.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Optional<T>
{
  private static final Optional<?> EMPTY = new Optional();
  private final T value;
  
  private Optional()
  {
    value = null;
  }
  
  public static <T> Optional<T> empty()
  {
    Optional localOptional = EMPTY;
    return localOptional;
  }
  
  private Optional(T paramT)
  {
    value = Objects.requireNonNull(paramT);
  }
  
  public static <T> Optional<T> of(T paramT)
  {
    return new Optional(paramT);
  }
  
  public static <T> Optional<T> ofNullable(T paramT)
  {
    return paramT == null ? empty() : of(paramT);
  }
  
  public T get()
  {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return (T)value;
  }
  
  public boolean isPresent()
  {
    return value != null;
  }
  
  public void ifPresent(Consumer<? super T> paramConsumer)
  {
    if (value != null) {
      paramConsumer.accept(value);
    }
  }
  
  public Optional<T> filter(Predicate<? super T> paramPredicate)
  {
    Objects.requireNonNull(paramPredicate);
    if (!isPresent()) {
      return this;
    }
    return paramPredicate.test(value) ? this : empty();
  }
  
  public <U> Optional<U> map(Function<? super T, ? extends U> paramFunction)
  {
    Objects.requireNonNull(paramFunction);
    if (!isPresent()) {
      return empty();
    }
    return ofNullable(paramFunction.apply(value));
  }
  
  public <U> Optional<U> flatMap(Function<? super T, Optional<U>> paramFunction)
  {
    Objects.requireNonNull(paramFunction);
    if (!isPresent()) {
      return empty();
    }
    return (Optional)Objects.requireNonNull(paramFunction.apply(value));
  }
  
  public T orElse(T paramT)
  {
    return (T)(value != null ? value : paramT);
  }
  
  public T orElseGet(Supplier<? extends T> paramSupplier)
  {
    return (T)(value != null ? value : paramSupplier.get());
  }
  
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> paramSupplier)
    throws Throwable
  {
    if (value != null) {
      return (T)value;
    }
    throw ((Throwable)paramSupplier.get());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Optional)) {
      return false;
    }
    Optional localOptional = (Optional)paramObject;
    return Objects.equals(value, value);
  }
  
  public int hashCode()
  {
    return Objects.hashCode(value);
  }
  
  public String toString()
  {
    return value != null ? String.format("Optional[%s]", new Object[] { value }) : "Optional.empty";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Optional.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */