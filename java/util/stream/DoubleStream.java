package java.util.stream;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Spliterator.OfDouble;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

public abstract interface DoubleStream
  extends BaseStream<Double, DoubleStream>
{
  public abstract DoubleStream filter(DoublePredicate paramDoublePredicate);
  
  public abstract DoubleStream map(DoubleUnaryOperator paramDoubleUnaryOperator);
  
  public abstract <U> Stream<U> mapToObj(DoubleFunction<? extends U> paramDoubleFunction);
  
  public abstract IntStream mapToInt(DoubleToIntFunction paramDoubleToIntFunction);
  
  public abstract LongStream mapToLong(DoubleToLongFunction paramDoubleToLongFunction);
  
  public abstract DoubleStream flatMap(DoubleFunction<? extends DoubleStream> paramDoubleFunction);
  
  public abstract DoubleStream distinct();
  
  public abstract DoubleStream sorted();
  
  public abstract DoubleStream peek(DoubleConsumer paramDoubleConsumer);
  
  public abstract DoubleStream limit(long paramLong);
  
  public abstract DoubleStream skip(long paramLong);
  
  public abstract void forEach(DoubleConsumer paramDoubleConsumer);
  
  public abstract void forEachOrdered(DoubleConsumer paramDoubleConsumer);
  
  public abstract double[] toArray();
  
  public abstract double reduce(double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator);
  
  public abstract OptionalDouble reduce(DoubleBinaryOperator paramDoubleBinaryOperator);
  
  public abstract <R> R collect(Supplier<R> paramSupplier, ObjDoubleConsumer<R> paramObjDoubleConsumer, BiConsumer<R, R> paramBiConsumer);
  
  public abstract double sum();
  
  public abstract OptionalDouble min();
  
  public abstract OptionalDouble max();
  
  public abstract long count();
  
  public abstract OptionalDouble average();
  
  public abstract DoubleSummaryStatistics summaryStatistics();
  
  public abstract boolean anyMatch(DoublePredicate paramDoublePredicate);
  
  public abstract boolean allMatch(DoublePredicate paramDoublePredicate);
  
  public abstract boolean noneMatch(DoublePredicate paramDoublePredicate);
  
  public abstract OptionalDouble findFirst();
  
  public abstract OptionalDouble findAny();
  
  public abstract Stream<Double> boxed();
  
  public abstract DoubleStream sequential();
  
  public abstract DoubleStream parallel();
  
  public abstract PrimitiveIterator.OfDouble iterator();
  
  public abstract Spliterator.OfDouble spliterator();
  
  public static Builder builder()
  {
    return new Streams.DoubleStreamBuilderImpl();
  }
  
  public static DoubleStream empty()
  {
    return StreamSupport.doubleStream(Spliterators.emptyDoubleSpliterator(), false);
  }
  
  public static DoubleStream of(double paramDouble)
  {
    return StreamSupport.doubleStream(new Streams.DoubleStreamBuilderImpl(paramDouble), false);
  }
  
  public static DoubleStream of(double... paramVarArgs)
  {
    return Arrays.stream(paramVarArgs);
  }
  
  public static DoubleStream iterate(double paramDouble, DoubleUnaryOperator paramDoubleUnaryOperator)
  {
    Objects.requireNonNull(paramDoubleUnaryOperator);
    PrimitiveIterator.OfDouble local1 = new PrimitiveIterator.OfDouble()
    {
      double t = val$seed;
      
      public boolean hasNext()
      {
        return true;
      }
      
      public double nextDouble()
      {
        double d = t;
        t = val$f.applyAsDouble(t);
        return d;
      }
    };
    return StreamSupport.doubleStream(Spliterators.spliteratorUnknownSize(local1, 1296), false);
  }
  
  public static DoubleStream generate(DoubleSupplier paramDoubleSupplier)
  {
    Objects.requireNonNull(paramDoubleSupplier);
    return StreamSupport.doubleStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(Long.MAX_VALUE, paramDoubleSupplier), false);
  }
  
  public static DoubleStream concat(DoubleStream paramDoubleStream1, DoubleStream paramDoubleStream2)
  {
    Objects.requireNonNull(paramDoubleStream1);
    Objects.requireNonNull(paramDoubleStream2);
    Streams.ConcatSpliterator.OfDouble localOfDouble = new Streams.ConcatSpliterator.OfDouble(paramDoubleStream1.spliterator(), paramDoubleStream2.spliterator());
    DoubleStream localDoubleStream = StreamSupport.doubleStream(localOfDouble, (paramDoubleStream1.isParallel()) || (paramDoubleStream2.isParallel()));
    return (DoubleStream)localDoubleStream.onClose(Streams.composedClose(paramDoubleStream1, paramDoubleStream2));
  }
  
  public static abstract interface Builder
    extends DoubleConsumer
  {
    public abstract void accept(double paramDouble);
    
    public Builder add(double paramDouble)
    {
      accept(paramDouble);
      return this;
    }
    
    public abstract DoubleStream build();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\DoubleStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */