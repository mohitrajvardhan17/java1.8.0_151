package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicMarkableReference<V>
{
  private volatile Pair<V> pair;
  private static final Unsafe UNSAFE = ;
  private static final long pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
  
  public AtomicMarkableReference(V paramV, boolean paramBoolean)
  {
    pair = Pair.of(paramV, paramBoolean);
  }
  
  public V getReference()
  {
    return (V)pair.reference;
  }
  
  public boolean isMarked()
  {
    return pair.mark;
  }
  
  public V get(boolean[] paramArrayOfBoolean)
  {
    Pair localPair = pair;
    paramArrayOfBoolean[0] = mark;
    return (V)reference;
  }
  
  public boolean weakCompareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2)
  {
    return compareAndSet(paramV1, paramV2, paramBoolean1, paramBoolean2);
  }
  
  public boolean compareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2)
  {
    Pair localPair = pair;
    return (paramV1 == reference) && (paramBoolean1 == mark) && (((paramV2 == reference) && (paramBoolean2 == mark)) || (casPair(localPair, Pair.of(paramV2, paramBoolean2))));
  }
  
  public void set(V paramV, boolean paramBoolean)
  {
    Pair localPair = pair;
    if ((paramV != reference) || (paramBoolean != mark)) {
      pair = Pair.of(paramV, paramBoolean);
    }
  }
  
  public boolean attemptMark(V paramV, boolean paramBoolean)
  {
    Pair localPair = pair;
    return (paramV == reference) && ((paramBoolean == mark) || (casPair(localPair, Pair.of(paramV, paramBoolean))));
  }
  
  private boolean casPair(Pair<V> paramPair1, Pair<V> paramPair2)
  {
    return UNSAFE.compareAndSwapObject(this, pairOffset, paramPair1, paramPair2);
  }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass)
  {
    try
    {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      NoSuchFieldError localNoSuchFieldError = new NoSuchFieldError(paramString);
      localNoSuchFieldError.initCause(localNoSuchFieldException);
      throw localNoSuchFieldError;
    }
  }
  
  private static class Pair<T>
  {
    final T reference;
    final boolean mark;
    
    private Pair(T paramT, boolean paramBoolean)
    {
      reference = paramT;
      mark = paramBoolean;
    }
    
    static <T> Pair<T> of(T paramT, boolean paramBoolean)
    {
      return new Pair(paramT, paramBoolean);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicMarkableReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */