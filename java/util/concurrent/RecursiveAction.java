package java.util.concurrent;

public abstract class RecursiveAction
  extends ForkJoinTask<Void>
{
  private static final long serialVersionUID = 5232453952276485070L;
  
  public RecursiveAction() {}
  
  protected abstract void compute();
  
  public final Void getRawResult()
  {
    return null;
  }
  
  protected final void setRawResult(Void paramVoid) {}
  
  protected final boolean exec()
  {
    compute();
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\RecursiveAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */