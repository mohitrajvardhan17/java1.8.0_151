package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

public class ThreadLocalRandom
  extends Random
{
  private static final AtomicInteger probeGenerator = new AtomicInteger();
  private static final AtomicLong seeder = new AtomicLong(initialSeed());
  private static final long GAMMA = -7046029254386353131L;
  private static final int PROBE_INCREMENT = -1640531527;
  private static final long SEEDER_INCREMENT = -4942790177534073029L;
  private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
  private static final float FLOAT_UNIT = 5.9604645E-8F;
  private static final ThreadLocal<Double> nextLocalGaussian = new ThreadLocal();
  boolean initialized = true;
  static final ThreadLocalRandom instance = new ThreadLocalRandom();
  static final String BadBound = "bound must be positive";
  static final String BadRange = "bound must be greater than origin";
  static final String BadSize = "size must be non-negative";
  private static final long serialVersionUID = -5851777807851030925L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("rnd", Long.TYPE), new ObjectStreamField("initialized", Boolean.TYPE) };
  private static final Unsafe UNSAFE;
  private static final long SEED;
  private static final long PROBE;
  private static final long SECONDARY;
  
  private static long initialSeed()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.util.secureRandomSeed"));
    if ((str != null) && (str.equalsIgnoreCase("true")))
    {
      byte[] arrayOfByte = SecureRandom.getSeed(8);
      long l = arrayOfByte[0] & 0xFF;
      for (int i = 1; i < 8; i++) {
        l = l << 8 | arrayOfByte[i] & 0xFF;
      }
      return l;
    }
    return mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
  }
  
  private static long mix64(long paramLong)
  {
    paramLong = (paramLong ^ paramLong >>> 33) * -49064778989728563L;
    paramLong = (paramLong ^ paramLong >>> 33) * -4265267296055464877L;
    return paramLong ^ paramLong >>> 33;
  }
  
  private static int mix32(long paramLong)
  {
    paramLong = (paramLong ^ paramLong >>> 33) * -49064778989728563L;
    return (int)((paramLong ^ paramLong >>> 33) * -4265267296055464877L >>> 32);
  }
  
  private ThreadLocalRandom() {}
  
  static final void localInit()
  {
    int i = probeGenerator.addAndGet(-1640531527);
    int j = i == 0 ? 1 : i;
    long l = mix64(seeder.getAndAdd(-4942790177534073029L));
    Thread localThread = Thread.currentThread();
    UNSAFE.putLong(localThread, SEED, l);
    UNSAFE.putInt(localThread, PROBE, j);
  }
  
  public static ThreadLocalRandom current()
  {
    if (UNSAFE.getInt(Thread.currentThread(), PROBE) == 0) {
      localInit();
    }
    return instance;
  }
  
  public void setSeed(long paramLong)
  {
    if (initialized) {
      throw new UnsupportedOperationException();
    }
  }
  
  final long nextSeed()
  {
    Thread localThread;
    long l;
    UNSAFE.putLong(localThread = Thread.currentThread(), SEED, l = UNSAFE.getLong(localThread, SEED) + -7046029254386353131L);
    return l;
  }
  
  protected int next(int paramInt)
  {
    return (int)(mix64(nextSeed()) >>> 64 - paramInt);
  }
  
  final long internalNextLong(long paramLong1, long paramLong2)
  {
    long l1 = mix64(nextSeed());
    if (paramLong1 < paramLong2)
    {
      long l2 = paramLong2 - paramLong1;
      long l3 = l2 - 1L;
      if ((l2 & l3) == 0L)
      {
        l1 = (l1 & l3) + paramLong1;
      }
      else if (l2 > 0L)
      {
        for (long l4 = l1 >>> 1; l4 + l3 - (l1 = l4 % l2) < 0L; l4 = mix64(nextSeed()) >>> 1) {}
        l1 += paramLong1;
      }
      else
      {
        while ((l1 < paramLong1) || (l1 >= paramLong2)) {
          l1 = mix64(nextSeed());
        }
      }
    }
    return l1;
  }
  
  final int internalNextInt(int paramInt1, int paramInt2)
  {
    int i = mix32(nextSeed());
    if (paramInt1 < paramInt2)
    {
      int j = paramInt2 - paramInt1;
      int k = j - 1;
      if ((j & k) == 0)
      {
        i = (i & k) + paramInt1;
      }
      else if (j > 0)
      {
        for (int m = i >>> 1; m + k - (i = m % j) < 0; m = mix32(nextSeed()) >>> 1) {}
        i += paramInt1;
      }
      else
      {
        while ((i < paramInt1) || (i >= paramInt2)) {
          i = mix32(nextSeed());
        }
      }
    }
    return i;
  }
  
  final double internalNextDouble(double paramDouble1, double paramDouble2)
  {
    double d = (nextLong() >>> 11) * 1.1102230246251565E-16D;
    if (paramDouble1 < paramDouble2)
    {
      d = d * (paramDouble2 - paramDouble1) + paramDouble1;
      if (d >= paramDouble2) {
        d = Double.longBitsToDouble(Double.doubleToLongBits(paramDouble2) - 1L);
      }
    }
    return d;
  }
  
  public int nextInt()
  {
    return mix32(nextSeed());
  }
  
  public int nextInt(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("bound must be positive");
    }
    int i = mix32(nextSeed());
    int j = paramInt - 1;
    if ((paramInt & j) == 0) {
      i &= j;
    } else {
      for (int k = i >>> 1; k + j - (i = k % paramInt) < 0; k = mix32(nextSeed()) >>> 1) {}
    }
    return i;
  }
  
  public int nextInt(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return internalNextInt(paramInt1, paramInt2);
  }
  
  public long nextLong()
  {
    return mix64(nextSeed());
  }
  
  public long nextLong(long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("bound must be positive");
    }
    long l1 = mix64(nextSeed());
    long l2 = paramLong - 1L;
    if ((paramLong & l2) == 0L) {
      l1 &= l2;
    } else {
      for (long l3 = l1 >>> 1; l3 + l2 - (l1 = l3 % paramLong) < 0L; l3 = mix64(nextSeed()) >>> 1) {}
    }
    return l1;
  }
  
  public long nextLong(long paramLong1, long paramLong2)
  {
    if (paramLong1 >= paramLong2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return internalNextLong(paramLong1, paramLong2);
  }
  
  public double nextDouble()
  {
    return (mix64(nextSeed()) >>> 11) * 1.1102230246251565E-16D;
  }
  
  public double nextDouble(double paramDouble)
  {
    if (paramDouble <= 0.0D) {
      throw new IllegalArgumentException("bound must be positive");
    }
    double d = (mix64(nextSeed()) >>> 11) * 1.1102230246251565E-16D * paramDouble;
    return d < paramDouble ? d : Double.longBitsToDouble(Double.doubleToLongBits(paramDouble) - 1L);
  }
  
  public double nextDouble(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 >= paramDouble2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return internalNextDouble(paramDouble1, paramDouble2);
  }
  
  public boolean nextBoolean()
  {
    return mix32(nextSeed()) < 0;
  }
  
  public float nextFloat()
  {
    return (mix32(nextSeed()) >>> 8) * 5.9604645E-8F;
  }
  
  public double nextGaussian()
  {
    Double localDouble = (Double)nextLocalGaussian.get();
    if (localDouble != null)
    {
      nextLocalGaussian.set(null);
      return localDouble.doubleValue();
    }
    double d1;
    double d2;
    double d3;
    do
    {
      d1 = 2.0D * nextDouble() - 1.0D;
      d2 = 2.0D * nextDouble() - 1.0D;
      d3 = d1 * d1 + d2 * d2;
    } while ((d3 >= 1.0D) || (d3 == 0.0D));
    double d4 = StrictMath.sqrt(-2.0D * StrictMath.log(d3) / d3);
    nextLocalGaussian.set(new Double(d2 * d4));
    return d1 * d4;
  }
  
  public IntStream ints(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, paramLong, Integer.MAX_VALUE, 0), false);
  }
  
  public IntStream ints()
  {
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false);
  }
  
  public IntStream ints(long paramLong, int paramInt1, int paramInt2)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, paramLong, paramInt1, paramInt2), false);
  }
  
  public IntStream ints(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, Long.MAX_VALUE, paramInt1, paramInt2), false);
  }
  
  public LongStream longs(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, paramLong, Long.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs()
  {
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    if (paramLong2 >= paramLong3) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, paramLong1, paramLong2, paramLong3), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2)
  {
    if (paramLong1 >= paramLong2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, Long.MAX_VALUE, paramLong1, paramLong2), false);
  }
  
  public DoubleStream doubles(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, paramLong, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles()
  {
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles(long paramLong, double paramDouble1, double paramDouble2)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("size must be non-negative");
    }
    if (paramDouble1 >= paramDouble2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, paramLong, paramDouble1, paramDouble2), false);
  }
  
  public DoubleStream doubles(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 >= paramDouble2) {
      throw new IllegalArgumentException("bound must be greater than origin");
    }
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, Long.MAX_VALUE, paramDouble1, paramDouble2), false);
  }
  
  static final int getProbe()
  {
    return UNSAFE.getInt(Thread.currentThread(), PROBE);
  }
  
  static final int advanceProbe(int paramInt)
  {
    paramInt ^= paramInt << 13;
    paramInt ^= paramInt >>> 17;
    paramInt ^= paramInt << 5;
    UNSAFE.putInt(Thread.currentThread(), PROBE, paramInt);
    return paramInt;
  }
  
  static final int nextSecondarySeed()
  {
    Thread localThread = Thread.currentThread();
    int i;
    if ((i = UNSAFE.getInt(localThread, SECONDARY)) != 0)
    {
      i ^= i << 13;
      i ^= i >>> 17;
      i ^= i << 5;
    }
    else
    {
      localInit();
      if ((i = (int)UNSAFE.getLong(localThread, SEED)) == 0) {
        i = 1;
      }
    }
    UNSAFE.putInt(localThread, SECONDARY, i);
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("rnd", UNSAFE.getLong(Thread.currentThread(), SEED));
    localPutField.put("initialized", true);
    paramObjectOutputStream.writeFields();
  }
  
  private Object readResolve()
  {
    return current();
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = Thread.class;
      SEED = UNSAFE.objectFieldOffset(localClass.getDeclaredField("threadLocalRandomSeed"));
      PROBE = UNSAFE.objectFieldOffset(localClass.getDeclaredField("threadLocalRandomProbe"));
      SECONDARY = UNSAFE.objectFieldOffset(localClass.getDeclaredField("threadLocalRandomSecondarySeed"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class RandomDoublesSpliterator
    implements Spliterator.OfDouble
  {
    long index;
    final long fence;
    final double origin;
    final double bound;
    
    RandomDoublesSpliterator(long paramLong1, long paramLong2, double paramDouble1, double paramDouble2)
    {
      index = paramLong1;
      fence = paramLong2;
      origin = paramDouble1;
      bound = paramDouble2;
    }
    
    public RandomDoublesSpliterator trySplit()
    {
      long l1 = index;
      long l2 = l1 + fence >>> 1;
      return l2 <= l1 ? null : new RandomDoublesSpliterator(l1, index = l2, origin, bound);
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return 17728;
    }
    
    public boolean tryAdvance(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = index;
      long l2 = fence;
      if (l1 < l2)
      {
        paramDoubleConsumer.accept(ThreadLocalRandom.current().internalNextDouble(origin, bound));
        index = (l1 + 1L);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(DoubleConsumer paramDoubleConsumer)
    {
      if (paramDoubleConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = index;
      long l2 = fence;
      if (l1 < l2)
      {
        index = l2;
        double d1 = origin;
        double d2 = bound;
        ThreadLocalRandom localThreadLocalRandom = ThreadLocalRandom.current();
        do
        {
          paramDoubleConsumer.accept(localThreadLocalRandom.internalNextDouble(d1, d2));
        } while (++l1 < l2);
      }
    }
  }
  
  static final class RandomIntsSpliterator
    implements Spliterator.OfInt
  {
    long index;
    final long fence;
    final int origin;
    final int bound;
    
    RandomIntsSpliterator(long paramLong1, long paramLong2, int paramInt1, int paramInt2)
    {
      index = paramLong1;
      fence = paramLong2;
      origin = paramInt1;
      bound = paramInt2;
    }
    
    public RandomIntsSpliterator trySplit()
    {
      long l1 = index;
      long l2 = l1 + fence >>> 1;
      return l2 <= l1 ? null : new RandomIntsSpliterator(l1, index = l2, origin, bound);
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return 17728;
    }
    
    public boolean tryAdvance(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = index;
      long l2 = fence;
      if (l1 < l2)
      {
        paramIntConsumer.accept(ThreadLocalRandom.current().internalNextInt(origin, bound));
        index = (l1 + 1L);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(IntConsumer paramIntConsumer)
    {
      if (paramIntConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = index;
      long l2 = fence;
      if (l1 < l2)
      {
        index = l2;
        int i = origin;
        int j = bound;
        ThreadLocalRandom localThreadLocalRandom = ThreadLocalRandom.current();
        do
        {
          paramIntConsumer.accept(localThreadLocalRandom.internalNextInt(i, j));
        } while (++l1 < l2);
      }
    }
  }
  
  static final class RandomLongsSpliterator
    implements Spliterator.OfLong
  {
    long index;
    final long fence;
    final long origin;
    final long bound;
    
    RandomLongsSpliterator(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      index = paramLong1;
      fence = paramLong2;
      origin = paramLong3;
      bound = paramLong4;
    }
    
    public RandomLongsSpliterator trySplit()
    {
      long l1 = index;
      long l2 = l1 + fence >>> 1;
      return l2 <= l1 ? null : new RandomLongsSpliterator(l1, index = l2, origin, bound);
    }
    
    public long estimateSize()
    {
      return fence - index;
    }
    
    public int characteristics()
    {
      return 17728;
    }
    
    public boolean tryAdvance(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = index;
      long l2 = fence;
      if (l1 < l2)
      {
        paramLongConsumer.accept(ThreadLocalRandom.current().internalNextLong(origin, bound));
        index = (l1 + 1L);
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(LongConsumer paramLongConsumer)
    {
      if (paramLongConsumer == null) {
        throw new NullPointerException();
      }
      long l1 = index;
      long l2 = fence;
      if (l1 < l2)
      {
        index = l2;
        long l3 = origin;
        long l4 = bound;
        ThreadLocalRandom localThreadLocalRandom = ThreadLocalRandom.current();
        do
        {
          paramLongConsumer.accept(localThreadLocalRandom.internalNextLong(l3, l4));
        } while (++l1 < l2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ThreadLocalRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */