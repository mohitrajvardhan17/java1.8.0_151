package java.time.temporal;

@FunctionalInterface
public abstract interface TemporalQuery<R>
{
  public abstract R queryFrom(TemporalAccessor paramTemporalAccessor);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\TemporalQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */