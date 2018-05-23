package java.lang.invoke;

public class ConstantCallSite
  extends CallSite
{
  private final boolean isFrozen = true;
  
  public ConstantCallSite(MethodHandle paramMethodHandle)
  {
    super(paramMethodHandle);
  }
  
  protected ConstantCallSite(MethodType paramMethodType, MethodHandle paramMethodHandle)
    throws Throwable
  {
    super(paramMethodType, paramMethodHandle);
  }
  
  public final MethodHandle getTarget()
  {
    if (!isFrozen) {
      throw new IllegalStateException();
    }
    return target;
  }
  
  public final void setTarget(MethodHandle paramMethodHandle)
  {
    throw new UnsupportedOperationException();
  }
  
  public final MethodHandle dynamicInvoker()
  {
    return getTarget();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\ConstantCallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */