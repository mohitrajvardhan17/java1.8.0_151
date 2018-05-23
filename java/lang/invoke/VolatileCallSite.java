package java.lang.invoke;

public class VolatileCallSite
  extends CallSite
{
  public VolatileCallSite(MethodType paramMethodType)
  {
    super(paramMethodType);
  }
  
  public VolatileCallSite(MethodHandle paramMethodHandle)
  {
    super(paramMethodHandle);
  }
  
  public final MethodHandle getTarget()
  {
    return getTargetVolatile();
  }
  
  public void setTarget(MethodHandle paramMethodHandle)
  {
    checkTargetChange(getTargetVolatile(), paramMethodHandle);
    setTargetVolatile(paramMethodHandle);
  }
  
  public final MethodHandle dynamicInvoker()
  {
    return makeDynamicInvoker();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\VolatileCallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */