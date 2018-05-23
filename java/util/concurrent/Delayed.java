package java.util.concurrent;

public abstract interface Delayed
  extends Comparable<Delayed>
{
  public abstract long getDelay(TimeUnit paramTimeUnit);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\Delayed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */