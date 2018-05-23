package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public final class Spliterators
{
  private static final Spliterator<Object> EMPTY_SPLITERATOR = new Spliterators.EmptySpliterator.OfRef();
  private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR = new Spliterators.EmptySpliterator.OfInt();
  private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR = new Spliterators.EmptySpliterator.OfLong();
  private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR = new Spliterators.EmptySpliterator.OfDouble();
  
  private Spliterators() {}
  
  public static <T> Spliterator<T> emptySpliterator()
  {
    return EMPTY_SPLITERATOR;
  }
  
  public static Spliterator.OfInt emptyIntSpliterator()
  {
    return EMPTY_INT_SPLITERATOR;
  }
  
  public static Spliterator.OfLong emptyLongSpliterator()
  {
    return EMPTY_LONG_SPLITERATOR;
  }
  
  public static Spliterator.OfDouble emptyDoubleSpliterator()
  {
    return EMPTY_DOUBLE_SPLITERATOR;
  }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt)
  {
    return new ArraySpliterator((Object[])Objects.requireNonNull(paramArrayOfObject), paramInt);
  }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((Object[])Objects.requireNonNull(paramArrayOfObject)).length, paramInt1, paramInt2);
    return new ArraySpliterator(paramArrayOfObject, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt)
  {
    return new IntArraySpliterator((int[])Objects.requireNonNull(paramArrayOfInt), paramInt);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((int[])Objects.requireNonNull(paramArrayOfInt)).length, paramInt1, paramInt2);
    return new IntArraySpliterator(paramArrayOfInt, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt)
  {
    return new LongArraySpliterator((long[])Objects.requireNonNull(paramArrayOfLong), paramInt);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((long[])Objects.requireNonNull(paramArrayOfLong)).length, paramInt1, paramInt2);
    return new LongArraySpliterator(paramArrayOfLong, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt)
  {
    return new DoubleArraySpliterator((double[])Objects.requireNonNull(paramArrayOfDouble), paramInt);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
  {
    checkFromToBounds(((double[])Objects.requireNonNull(paramArrayOfDouble)).length, paramInt1, paramInt2);
    return new DoubleArraySpliterator(paramArrayOfDouble, paramInt1, paramInt2, paramInt3);
  }
  
  private static void checkFromToBounds(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 > paramInt3) {
      throw new ArrayIndexOutOfBoundsException("origin(" + paramInt2 + ") > fence(" + paramInt3 + ")");
    }
    if (paramInt2 < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt3 > paramInt1) {
      throw new ArrayIndexOutOfBoundsException(paramInt3);
    }
  }
  
  public static <T> Spliterator<T> spliterator(Collection<? extends T> paramCollection, int paramInt)
  {
    return new IteratorSpliterator((Collection)Objects.requireNonNull(paramCollection), paramInt);
  }
  
  public static <T> Spliterator<T> spliterator(Iterator<? extends T> paramIterator, long paramLong, int paramInt)
  {
    return new IteratorSpliterator((Iterator)Objects.requireNonNull(paramIterator), paramLong, paramInt);
  }
  
  public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> paramIterator, int paramInt)
  {
    return new IteratorSpliterator((Iterator)Objects.requireNonNull(paramIterator), paramInt);
  }
  
  public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt paramOfInt, long paramLong, int paramInt)
  {
    return new IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(paramOfInt), paramLong, paramInt);
  }
  
  public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt paramOfInt, int paramInt)
  {
    return new IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(paramOfInt), paramInt);
  }
  
  public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong paramOfLong, long paramLong, int paramInt)
  {
    return new LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(paramOfLong), paramLong, paramInt);
  }
  
  public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong paramOfLong, int paramInt)
  {
    return new LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(paramOfLong), paramInt);
  }
  
  public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble paramOfDouble, long paramLong, int paramInt)
  {
    return new DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(paramOfDouble), paramLong, paramInt);
  }
  
  public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble paramOfDouble, int paramInt)
  {
    return new DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(paramOfDouble), paramInt);
  }
  
  public static <T> Iterator<T> iterator(Spliterator<? extends T> paramSpliterator)
  {
    Objects.requireNonNull(paramSpliterator);
    new Iterator()
    {
      boolean valueReady = false;
      T nextElement;
      
      public void accept(T paramAnonymousT)
      {
        valueReady = true;
        nextElement = paramAnonymousT;
      }
      
      public boolean hasNext()
      {
        if (!valueReady) {
          val$spliterator.tryAdvance(this);
        }
        return valueReady;
      }
      
      public T next()
      {
        if ((!valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        valueReady = false;
        return (T)nextElement;
      }
    };
  }
  
  public static PrimitiveIterator.OfInt iterator(Spliterator.OfInt paramOfInt)
  {
    Objects.requireNonNull(paramOfInt);
    new PrimitiveIterator.OfInt()
    {
      boolean valueReady = false;
      int nextElement;
      
      public void accept(int paramAnonymousInt)
      {
        valueReady = true;
        nextElement = paramAnonymousInt;
      }
      
      public boolean hasNext()
      {
        if (!valueReady) {
          val$spliterator.tryAdvance(this);
        }
        return valueReady;
      }
      
      public int nextInt()
      {
        if ((!valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        valueReady = false;
        return nextElement;
      }
    };
  }
  
  public static PrimitiveIterator.OfLong iterator(Spliterator.OfLong paramOfLong)
  {
    Objects.requireNonNull(paramOfLong);
    new PrimitiveIterator.OfLong()
    {
      boolean valueReady = false;
      long nextElement;
      
      public void accept(long paramAnonymousLong)
      {
        valueReady = true;
        nextElement = paramAnonymousLong;
      }
      
      public boolean hasNext()
      {
        if (!valueReady) {
          val$spliterator.tryAdvance(this);
        }
        return valueReady;
      }
      
      public long nextLong()
      {
        if ((!valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        valueReady = false;
        return nextElement;
      }
    };
  }
  
  public static PrimitiveIterator.OfDouble iterator(Spliterator.OfDouble paramOfDouble)
  {
    Objects.requireNonNull(paramOfDouble);
    new PrimitiveIterator.OfDouble()
    {
      boolean valueReady = false;
      double nextElement;
      
      public void accept(double paramAnonymousDouble)
      {
        valueReady = true;
        nextElement = paramAnonymousDouble;
      }
      
      public boolean hasNext()
      {
        if (!valueReady) {
          val$spliterator.tryAdvance(this);
        }
        return valueReady;
      }
      
      public double nextDouble()
      {
        if ((!valueReady) && (!hasNext())) {
          throw new NoSuchElementException();
        }
        valueReady = false;
        return nextElement;
      }
    };
  }
  
  public static abstract class AbstractDoubleSpliterator
    implements Spliterator.OfDouble
  {
    static final int MAX_BATCH = 33554432;
    static final int BATCH_UNIT = 1024;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractDoubleSpliterator(long paramLong, int paramInt)
    {
      est = paramLong;
      characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator.OfDouble trySplit()
    {
      HoldingDoubleConsumer localHoldingDoubleConsumer = new HoldingDoubleConsumer();
      long l = est;
      if ((l > 1L) && (tryAdvance(localHoldingDoubleConsumer)))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        double[] arrayOfDouble = new double[i];
        int j = 0;
        do
        {
          arrayOfDouble[j] = value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingDoubleConsumer)));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.DoubleArraySpliterator(arrayOfDouble, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    static final class HoldingDoubleConsumer
      implements DoubleConsumer
    {
      double value;
      
      HoldingDoubleConsumer() {}
      
      public void accept(double paramDouble)
      {
        value = paramDouble;
      }
    }
  }
  
  public static abstract class AbstractIntSpliterator
    implements Spliterator.OfInt
  {
    static final int MAX_BATCH = 33554432;
    static final int BATCH_UNIT = 1024;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractIntSpliterator(long paramLong, int paramInt)
    {
      est = paramLong;
      characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator.OfInt trySplit()
    {
      HoldingIntConsumer localHoldingIntConsumer = new HoldingIntConsumer();
      long l = est;
      if ((l > 1L) && (tryAdvance(localHoldingIntConsumer)))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        int[] arrayOfInt = new int[i];
        int j = 0;
        do
        {
          arrayOfInt[j] = value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingIntConsumer)));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.IntArraySpliterator(arrayOfInt, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    static final class HoldingIntConsumer
      implements IntConsumer
    {
      int value;
      
      HoldingIntConsumer() {}
      
      public void accept(int paramInt)
      {
        value = paramInt;
      }
    }
  }
  
  public static abstract class AbstractLongSpliterator
    implements Spliterator.OfLong
  {
    static final int MAX_BATCH = 33554432;
    static final int BATCH_UNIT = 1024;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractLongSpliterator(long paramLong, int paramInt)
    {
      est = paramLong;
      characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator.OfLong trySplit()
    {
      HoldingLongConsumer localHoldingLongConsumer = new HoldingLongConsumer();
      long l = est;
      if ((l > 1L) && (tryAdvance(localHoldingLongConsumer)))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        long[] arrayOfLong = new long[i];
        int j = 0;
        do
        {
          arrayOfLong[j] = value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingLongConsumer)));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.LongArraySpliterator(arrayOfLong, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    static final class HoldingLongConsumer
      implements LongConsumer
    {
      long value;
      
      HoldingLongConsumer() {}
      
      public void accept(long paramLong)
      {
        value = paramLong;
      }
    }
  }
  
  public static abstract class AbstractSpliterator<T>
    implements Spliterator<T>
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private final int characteristics;
    private long est;
    private int batch;
    
    protected AbstractSpliterator(long paramLong, int paramInt)
    {
      est = paramLong;
      characteristics = ((paramInt & 0x40) != 0 ? paramInt | 0x4000 : paramInt);
    }
    
    public Spliterator<T> trySplit()
    {
      HoldingConsumer localHoldingConsumer = new HoldingConsumer();
      long l = est;
      if ((l > 1L) && (tryAdvance(localHoldingConsumer)))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        Object[] arrayOfObject = new Object[i];
        int j = 0;
        do
        {
          arrayOfObject[j] = value;
          j++;
        } while ((j < i) && (tryAdvance(localHoldingConsumer)));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.ArraySpliterator(arrayOfObject, 0, j, characteristics());
      }
      return null;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    static final class HoldingConsumer<T>
      implements Consumer<T>
    {
      Object value;
      
      HoldingConsumer() {}
      
      public void accept(T paramT)
      {
        value = paramT;
      }
    }
  }
  
  static final class ArraySpliterator<T>
    implements Spliterator<T>
  {
    private final Object[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public ArraySpliterator(Object[] paramArrayOfObject, int paramInt)
    {
      this(paramArrayOfObject, 0, paramArrayOfObject.length, paramInt);
    }
    
    public ArraySpliterator(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
    {
      array = paramArrayOfObject;
      index = paramInt1;
      fence = paramInt2;
      characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator<T> trySplit()
    {
      int i = index;
      int j = i + fence >>> 1;
      return i >= j ? null : new ArraySpliterator(array, i, index = j, characteristics);
    }
    
    public void forEachRemaining(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject;
      int j;
      int i;
      if (((arrayOfObject = array).length >= (j = fence)) && ((i = index) >= 0) && (i < (index = j))) {
        do
        {
          paramConsumer.accept(arrayOfObject[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      if ((index >= 0) && (index < fence))
      {
        Object localObject = array[(index++)];
        paramConsumer.accept(localObject);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super T> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class DoubleArraySpliterator
    implements Spliterator.OfDouble
  {
    private final double[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public DoubleArraySpliterator(double[] paramArrayOfDouble, int paramInt)
    {
      this(paramArrayOfDouble, 0, paramArrayOfDouble.length, paramInt);
    }
    
    public DoubleArraySpliterator(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
    {
      array = paramArrayOfDouble;
      index = paramInt1;
      fence = paramInt2;
      characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator.OfDouble trySplit()
    {
      int i = index;
      int j = i + fence >>> 1;
      return i >= j ? null : new DoubleArraySpliterator(array, i, index = j, characteristics);
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      double[] arrayOfDouble;
      int j;
      int i;
      if (((arrayOfDouble = array).length >= (j = fence)) && ((i = index) >= 0) && (i < (index = j))) {
        do
        {
          paramDoubleConsumer.accept(arrayOfDouble[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      if ((index >= 0) && (index < fence))
      {
        paramDoubleConsumer.accept(array[(index++)]);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super Double> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class DoubleIteratorSpliterator
    implements Spliterator.OfDouble
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private PrimitiveIterator.OfDouble it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble paramOfDouble, long paramLong, int paramInt)
    {
      it = paramOfDouble;
      est = paramLong;
      characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble paramOfDouble, int paramInt)
    {
      it = paramOfDouble;
      est = Long.MAX_VALUE;
      characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator.OfDouble trySplit()
    {
      PrimitiveIterator.OfDouble localOfDouble = it;
      long l = est;
      if ((l > 1L) && (localOfDouble.hasNext()))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        double[] arrayOfDouble = new double[i];
        int j = 0;
        do
        {
          arrayOfDouble[j] = localOfDouble.nextDouble();
          j++;
        } while ((j < i) && (localOfDouble.hasNext()));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.DoubleArraySpliterator(arrayOfDouble, 0, j, characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      it.forEachRemaining(paramDoubleConsumer);
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      if (it.hasNext())
      {
        paramDoubleConsumer.accept(it.nextDouble());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super Double> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C>
  {
    EmptySpliterator() {}
    
    public S trySplit()
    {
      return null;
    }
    
    public boolean tryAdvance(C paramC)
    {
      Objects.requireNonNull(paramC);
      return false;
    }
    
    public void forEachRemaining(C paramC)
    {
      Objects.requireNonNull(paramC);
    }
    
    public long estimateSize()
    {
      return 0L;
    }
    
    public int characteristics()
    {
      return 16448;
    }
    
    private static final class OfDouble
      extends Spliterators.EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer>
      implements Spliterator.OfDouble
    {
      OfDouble() {}
    }
    
    private static final class OfInt
      extends Spliterators.EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer>
      implements Spliterator.OfInt
    {
      OfInt() {}
    }
    
    private static final class OfLong
      extends Spliterators.EmptySpliterator<Long, Spliterator.OfLong, LongConsumer>
      implements Spliterator.OfLong
    {
      OfLong() {}
    }
    
    private static final class OfRef<T>
      extends Spliterators.EmptySpliterator<T, Spliterator<T>, Consumer<? super T>>
      implements Spliterator<T>
    {
      OfRef() {}
    }
  }
  
  static final class IntArraySpliterator
    implements Spliterator.OfInt
  {
    private final int[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public IntArraySpliterator(int[] paramArrayOfInt, int paramInt)
    {
      this(paramArrayOfInt, 0, paramArrayOfInt.length, paramInt);
    }
    
    public IntArraySpliterator(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
    {
      array = paramArrayOfInt;
      index = paramInt1;
      fence = paramInt2;
      characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator.OfInt trySplit()
    {
      int i = index;
      int j = i + fence >>> 1;
      return i >= j ? null : new IntArraySpliterator(array, i, index = j, characteristics);
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      int[] arrayOfInt;
      int j;
      int i;
      if (((arrayOfInt = array).length >= (j = fence)) && ((i = index) >= 0) && (i < (index = j))) {
        do
        {
          paramIntConsumer.accept(arrayOfInt[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      if ((index >= 0) && (index < fence))
      {
        paramIntConsumer.accept(array[(index++)]);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super Integer> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class IntIteratorSpliterator
    implements Spliterator.OfInt
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private PrimitiveIterator.OfInt it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public IntIteratorSpliterator(PrimitiveIterator.OfInt paramOfInt, long paramLong, int paramInt)
    {
      it = paramOfInt;
      est = paramLong;
      characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public IntIteratorSpliterator(PrimitiveIterator.OfInt paramOfInt, int paramInt)
    {
      it = paramOfInt;
      est = Long.MAX_VALUE;
      characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator.OfInt trySplit()
    {
      PrimitiveIterator.OfInt localOfInt = it;
      long l = est;
      if ((l > 1L) && (localOfInt.hasNext()))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        int[] arrayOfInt = new int[i];
        int j = 0;
        do
        {
          arrayOfInt[j] = localOfInt.nextInt();
          j++;
        } while ((j < i) && (localOfInt.hasNext()));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.IntArraySpliterator(arrayOfInt, 0, j, characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      it.forEachRemaining(paramIntConsumer);
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      if (it.hasNext())
      {
        paramIntConsumer.accept(it.nextInt());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super Integer> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static class IteratorSpliterator<T>
    implements Spliterator<T>
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private final Collection<? extends T> collection;
    private Iterator<? extends T> it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public IteratorSpliterator(Collection<? extends T> paramCollection, int paramInt)
    {
      collection = paramCollection;
      it = null;
      characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public IteratorSpliterator(Iterator<? extends T> paramIterator, long paramLong, int paramInt)
    {
      collection = null;
      it = paramIterator;
      est = paramLong;
      characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public IteratorSpliterator(Iterator<? extends T> paramIterator, int paramInt)
    {
      collection = null;
      it = paramIterator;
      est = Long.MAX_VALUE;
      characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator<T> trySplit()
    {
      Iterator localIterator;
      long l;
      if ((localIterator = it) == null)
      {
        localIterator = it = collection.iterator();
        l = est = collection.size();
      }
      else
      {
        l = est;
      }
      if ((l > 1L) && (localIterator.hasNext()))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        Object[] arrayOfObject = new Object[i];
        int j = 0;
        do
        {
          arrayOfObject[j] = localIterator.next();
          j++;
        } while ((j < i) && (localIterator.hasNext()));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.ArraySpliterator(arrayOfObject, 0, j, characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Iterator localIterator;
      if ((localIterator = it) == null)
      {
        localIterator = it = collection.iterator();
        est = collection.size();
      }
      localIterator.forEachRemaining(paramConsumer);
    }
    
    public boolean tryAdvance(Consumer<? super T> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      if (it == null)
      {
        it = collection.iterator();
        est = collection.size();
      }
      if (it.hasNext())
      {
        paramConsumer.accept(it.next());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      if (it == null)
      {
        it = collection.iterator();
        return est = collection.size();
      }
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super T> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class LongArraySpliterator
    implements Spliterator.OfLong
  {
    private final long[] array;
    private int index;
    private final int fence;
    private final int characteristics;
    
    public LongArraySpliterator(long[] paramArrayOfLong, int paramInt)
    {
      this(paramArrayOfLong, 0, paramArrayOfLong.length, paramInt);
    }
    
    public LongArraySpliterator(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3)
    {
      array = paramArrayOfLong;
      index = paramInt1;
      fence = paramInt2;
      characteristics = (paramInt3 | 0x40 | 0x4000);
    }
    
    public Spliterator.OfLong trySplit()
    {
      int i = index;
      int j = i + fence >>> 1;
      return i >= j ? null : new LongArraySpliterator(array, i, index = j, characteristics);
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      long[] arrayOfLong;
      int j;
      int i;
      if (((arrayOfLong = array).length >= (j = fence)) && ((i = index) >= 0) && (i < (index = j))) {
        do
        {
          paramLongConsumer.accept(arrayOfLong[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      if ((index >= 0) && (index < fence))
      {
        paramLongConsumer.accept(array[(index++)]);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super Long> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
  
  static final class LongIteratorSpliterator
    implements Spliterator.OfLong
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    private PrimitiveIterator.OfLong it;
    private final int characteristics;
    private long est;
    private int batch;
    
    public LongIteratorSpliterator(PrimitiveIterator.OfLong paramOfLong, long paramLong, int paramInt)
    {
      it = paramOfLong;
      est = paramLong;
      characteristics = ((paramInt & 0x1000) == 0 ? paramInt | 0x40 | 0x4000 : paramInt);
    }
    
    public LongIteratorSpliterator(PrimitiveIterator.OfLong paramOfLong, int paramInt)
    {
      it = paramOfLong;
      est = Long.MAX_VALUE;
      characteristics = (paramInt & 0xBFBF);
    }
    
    public Spliterator.OfLong trySplit()
    {
      PrimitiveIterator.OfLong localOfLong = it;
      long l = est;
      if ((l > 1L) && (localOfLong.hasNext()))
      {
        int i = batch + 1024;
        if (i > l) {
          i = (int)l;
        }
        if (i > 33554432) {
          i = 33554432;
        }
        long[] arrayOfLong = new long[i];
        int j = 0;
        do
        {
          arrayOfLong[j] = localOfLong.nextLong();
          j++;
        } while ((j < i) && (localOfLong.hasNext()));
        batch = j;
        if (est != Long.MAX_VALUE) {
          est -= j;
        }
        return new Spliterators.LongArraySpliterator(arrayOfLong, 0, j, characteristics);
      }
      return null;
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      it.forEachRemaining(paramLongConsumer);
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      if (it.hasNext())
      {
        paramLongConsumer.accept(it.nextLong());
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public int characteristics()
    {
      return characteristics;
    }
    
    public Comparator<? super Long> getComparator()
    {
      if (hasCharacteristics(4)) {
        return null;
      }
      throw new IllegalStateException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Spliterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */