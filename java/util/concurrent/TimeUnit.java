package java.util.concurrent;

public enum TimeUnit
{
  NANOSECONDS,  MICROSECONDS,  MILLISECONDS,  SECONDS,  MINUTES,  HOURS,  DAYS;
  
  static final long C0 = 1L;
  static final long C1 = 1000L;
  static final long C2 = 1000000L;
  static final long C3 = 1000000000L;
  static final long C4 = 60000000000L;
  static final long C5 = 3600000000000L;
  static final long C6 = 86400000000000L;
  static final long MAX = Long.MAX_VALUE;
  
  private TimeUnit() {}
  
  static long x(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 > paramLong3) {
      return Long.MAX_VALUE;
    }
    if (paramLong1 < -paramLong3) {
      return Long.MIN_VALUE;
    }
    return paramLong1 * paramLong2;
  }
  
  public long convert(long paramLong, TimeUnit paramTimeUnit)
  {
    throw new AbstractMethodError();
  }
  
  public long toNanos(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  public long toMicros(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  public long toMillis(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  public long toSeconds(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  public long toMinutes(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  public long toHours(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  public long toDays(long paramLong)
  {
    throw new AbstractMethodError();
  }
  
  abstract int excessNanos(long paramLong1, long paramLong2);
  
  public void timedWait(Object paramObject, long paramLong)
    throws InterruptedException
  {
    if (paramLong > 0L)
    {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      paramObject.wait(l, i);
    }
  }
  
  public void timedJoin(Thread paramThread, long paramLong)
    throws InterruptedException
  {
    if (paramLong > 0L)
    {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      paramThread.join(l, i);
    }
  }
  
  public void sleep(long paramLong)
    throws InterruptedException
  {
    if (paramLong > 0L)
    {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      Thread.sleep(l, i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\TimeUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */