package sun.management.counter;

public abstract interface LongArrayCounter
  extends Counter
{
  public abstract long[] longArrayValue();
  
  public abstract long longAt(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\LongArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */