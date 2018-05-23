package java.util.concurrent;

@FunctionalInterface
public abstract interface Callable<V>
{
  public abstract V call()
    throws Exception;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\Callable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */