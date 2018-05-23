package java.util.concurrent;

public abstract class RecursiveTask<V>
  extends ForkJoinTask<V>
{
  private static final long serialVersionUID = 5232453952276485270L;
  V result;
  
  public RecursiveTask() {}
  
  protected abstract V compute();
  
  public final V getRawResult()
  {
    return (V)result;
  }
  
  protected final void setRawResult(V paramV)
  {
    result = paramV;
  }
  
  protected final boolean exec()
  {
    result = compute();
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\RecursiveTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */