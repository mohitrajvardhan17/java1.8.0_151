package java.util.stream;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract interface Collector<T, A, R>
{
  public abstract Supplier<A> supplier();
  
  public abstract BiConsumer<A, T> accumulator();
  
  public abstract BinaryOperator<A> combiner();
  
  public abstract Function<A, R> finisher();
  
  public abstract Set<Characteristics> characteristics();
  
  public static <T, R> Collector<T, R, R> of(Supplier<R> paramSupplier, BiConsumer<R, T> paramBiConsumer, BinaryOperator<R> paramBinaryOperator, Characteristics... paramVarArgs)
  {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramBiConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    Objects.requireNonNull(paramVarArgs);
    Set localSet = paramVarArgs.length == 0 ? Collectors.CH_ID : Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH, paramVarArgs));
    return new Collectors.CollectorImpl(paramSupplier, paramBiConsumer, paramBinaryOperator, localSet);
  }
  
  public static <T, A, R> Collector<T, A, R> of(Supplier<A> paramSupplier, BiConsumer<A, T> paramBiConsumer, BinaryOperator<A> paramBinaryOperator, Function<A, R> paramFunction, Characteristics... paramVarArgs)
  {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramBiConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    Objects.requireNonNull(paramFunction);
    Objects.requireNonNull(paramVarArgs);
    Object localObject = Collectors.CH_NOID;
    if (paramVarArgs.length > 0)
    {
      localObject = EnumSet.noneOf(Characteristics.class);
      Collections.addAll((Collection)localObject, paramVarArgs);
      localObject = Collections.unmodifiableSet((Set)localObject);
    }
    return new Collectors.CollectorImpl(paramSupplier, paramBiConsumer, paramBinaryOperator, paramFunction, (Set)localObject);
  }
  
  public static enum Characteristics
  {
    CONCURRENT,  UNORDERED,  IDENTITY_FINISH;
    
    private Characteristics() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\Collector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */