package java.util.stream;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

abstract interface Sink<T>
  extends Consumer<T>
{
  public void begin(long paramLong) {}
  
  public void end() {}
  
  public boolean cancellationRequested()
  {
    return false;
  }
  
  public void accept(int paramInt)
  {
    throw new IllegalStateException("called wrong accept method");
  }
  
  public void accept(long paramLong)
  {
    throw new IllegalStateException("called wrong accept method");
  }
  
  public void accept(double paramDouble)
  {
    throw new IllegalStateException("called wrong accept method");
  }
  
  public static abstract class ChainedDouble<E_OUT>
    implements Sink.OfDouble
  {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedDouble(Sink<? super E_OUT> paramSink)
    {
      downstream = ((Sink)Objects.requireNonNull(paramSink));
    }
    
    public void begin(long paramLong)
    {
      downstream.begin(paramLong);
    }
    
    public void end()
    {
      downstream.end();
    }
    
    public boolean cancellationRequested()
    {
      return downstream.cancellationRequested();
    }
  }
  
  public static abstract class ChainedInt<E_OUT>
    implements Sink.OfInt
  {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedInt(Sink<? super E_OUT> paramSink)
    {
      downstream = ((Sink)Objects.requireNonNull(paramSink));
    }
    
    public void begin(long paramLong)
    {
      downstream.begin(paramLong);
    }
    
    public void end()
    {
      downstream.end();
    }
    
    public boolean cancellationRequested()
    {
      return downstream.cancellationRequested();
    }
  }
  
  public static abstract class ChainedLong<E_OUT>
    implements Sink.OfLong
  {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedLong(Sink<? super E_OUT> paramSink)
    {
      downstream = ((Sink)Objects.requireNonNull(paramSink));
    }
    
    public void begin(long paramLong)
    {
      downstream.begin(paramLong);
    }
    
    public void end()
    {
      downstream.end();
    }
    
    public boolean cancellationRequested()
    {
      return downstream.cancellationRequested();
    }
  }
  
  public static abstract class ChainedReference<T, E_OUT>
    implements Sink<T>
  {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedReference(Sink<? super E_OUT> paramSink)
    {
      downstream = ((Sink)Objects.requireNonNull(paramSink));
    }
    
    public void begin(long paramLong)
    {
      downstream.begin(paramLong);
    }
    
    public void end()
    {
      downstream.end();
    }
    
    public boolean cancellationRequested()
    {
      return downstream.cancellationRequested();
    }
  }
  
  public static abstract interface OfDouble
    extends Sink<Double>, DoubleConsumer
  {
    public abstract void accept(double paramDouble);
    
    public void accept(Double paramDouble)
    {
      if (Tripwire.ENABLED) {
        Tripwire.trip(getClass(), "{0} calling Sink.OfDouble.accept(Double)");
      }
      accept(paramDouble.doubleValue());
    }
  }
  
  public static abstract interface OfInt
    extends Sink<Integer>, IntConsumer
  {
    public abstract void accept(int paramInt);
    
    public void accept(Integer paramInteger)
    {
      if (Tripwire.ENABLED) {
        Tripwire.trip(getClass(), "{0} calling Sink.OfInt.accept(Integer)");
      }
      accept(paramInteger.intValue());
    }
  }
  
  public static abstract interface OfLong
    extends Sink<Long>, LongConsumer
  {
    public abstract void accept(long paramLong);
    
    public void accept(Long paramLong)
    {
      if (Tripwire.ENABLED) {
        Tripwire.trip(getClass(), "{0} calling Sink.OfLong.accept(Long)");
      }
      accept(paramLong.longValue());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\Sink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */