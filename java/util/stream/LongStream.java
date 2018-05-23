package java.util.stream;

import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator.OfLong;
import java.util.Spliterator.OfLong;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

public abstract interface LongStream
  extends BaseStream<Long, LongStream>
{
  public abstract LongStream filter(LongPredicate paramLongPredicate);
  
  public abstract LongStream map(LongUnaryOperator paramLongUnaryOperator);
  
  public abstract <U> Stream<U> mapToObj(LongFunction<? extends U> paramLongFunction);
  
  public abstract IntStream mapToInt(LongToIntFunction paramLongToIntFunction);
  
  public abstract DoubleStream mapToDouble(LongToDoubleFunction paramLongToDoubleFunction);
  
  public abstract LongStream flatMap(LongFunction<? extends LongStream> paramLongFunction);
  
  public abstract LongStream distinct();
  
  public abstract LongStream sorted();
  
  public abstract LongStream peek(LongConsumer paramLongConsumer);
  
  public abstract LongStream limit(long paramLong);
  
  public abstract LongStream skip(long paramLong);
  
  public abstract void forEach(LongConsumer paramLongConsumer);
  
  public abstract void forEachOrdered(LongConsumer paramLongConsumer);
  
  public abstract long[] toArray();
  
  public abstract long reduce(long paramLong, LongBinaryOperator paramLongBinaryOperator);
  
  public abstract OptionalLong reduce(LongBinaryOperator paramLongBinaryOperator);
  
  public abstract <R> R collect(Supplier<R> paramSupplier, ObjLongConsumer<R> paramObjLongConsumer, BiConsumer<R, R> paramBiConsumer);
  
  public abstract long sum();
  
  public abstract OptionalLong min();
  
  public abstract OptionalLong max();
  
  public abstract long count();
  
  public abstract OptionalDouble average();
  
  public abstract LongSummaryStatistics summaryStatistics();
  
  public abstract boolean anyMatch(LongPredicate paramLongPredicate);
  
  public abstract boolean allMatch(LongPredicate paramLongPredicate);
  
  public abstract boolean noneMatch(LongPredicate paramLongPredicate);
  
  public abstract OptionalLong findFirst();
  
  public abstract OptionalLong findAny();
  
  public abstract DoubleStream asDoubleStream();
  
  public abstract Stream<Long> boxed();
  
  public abstract LongStream sequential();
  
  public abstract LongStream parallel();
  
  public abstract PrimitiveIterator.OfLong iterator();
  
  public abstract Spliterator.OfLong spliterator();
  
  public static Builder builder()
  {
    return new Streams.LongStreamBuilderImpl();
  }
  
  public static LongStream empty()
  {
    return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false);
  }
  
  public static LongStream of(long paramLong)
  {
    return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(paramLong), false);
  }
  
  public static LongStream of(long... paramVarArgs)
  {
    return Arrays.stream(paramVarArgs);
  }
  
  public static LongStream iterate(long paramLong, LongUnaryOperator paramLongUnaryOperator)
  {
    Objects.requireNonNull(paramLongUnaryOperator);
    PrimitiveIterator.OfLong local1 = new PrimitiveIterator.OfLong()
    {
      long t = val$seed;
      
      public boolean hasNext()
      {
        return true;
      }
      
      public long nextLong()
      {
        long l = t;
        t = val$f.applyAsLong(t);
        return l;
      }
    };
    return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(local1, 1296), false);
  }
  
  public static LongStream generate(LongSupplier paramLongSupplier)
  {
    Objects.requireNonNull(paramLongSupplier);
    return StreamSupport.longStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Long.MAX_VALUE, paramLongSupplier), false);
  }
  
  public static LongStream range(long paramLong1, long paramLong2)
  {
    if (paramLong1 >= paramLong2) {
      return empty();
    }
    if (paramLong2 - paramLong1 < 0L)
    {
      long l = paramLong1 + Long.divideUnsigned(paramLong2 - paramLong1, 2L) + 1L;
      return concat(range(paramLong1, l), range(l, paramLong2));
    }
    return StreamSupport.longStream(new Streams.RangeLongSpliterator(paramLong1, paramLong2, false), false);
  }
  
  public static LongStream rangeClosed(long paramLong1, long paramLong2)
  {
    if (paramLong1 > paramLong2) {
      return empty();
    }
    if (paramLong2 - paramLong1 + 1L <= 0L)
    {
      long l = paramLong1 + Long.divideUnsigned(paramLong2 - paramLong1, 2L) + 1L;
      return concat(range(paramLong1, l), rangeClosed(l, paramLong2));
    }
    return StreamSupport.longStream(new Streams.RangeLongSpliterator(paramLong1, paramLong2, true), false);
  }
  
  public static LongStream concat(LongStream paramLongStream1, LongStream paramLongStream2)
  {
    Objects.requireNonNull(paramLongStream1);
    Objects.requireNonNull(paramLongStream2);
    Streams.ConcatSpliterator.OfLong localOfLong = new Streams.ConcatSpliterator.OfLong(paramLongStream1.spliterator(), paramLongStream2.spliterator());
    LongStream localLongStream = StreamSupport.longStream(localOfLong, (paramLongStream1.isParallel()) || (paramLongStream2.isParallel()));
    return (LongStream)localLongStream.onClose(Streams.composedClose(paramLongStream1, paramLongStream2));
  }
  
  public static abstract interface Builder
    extends LongConsumer
  {
    public abstract void accept(long paramLong);
    
    public Builder add(long paramLong)
    {
      accept(paramLong);
      return this;
    }
    
    public abstract LongStream build();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\LongStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */