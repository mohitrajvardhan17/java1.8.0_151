package java.lang.invoke;

import java.util.concurrent.atomic.AtomicInteger;

public class MutableCallSite
  extends CallSite
{
  private static final AtomicInteger STORE_BARRIER = new AtomicInteger();
  
  public MutableCallSite(MethodType paramMethodType)
  {
    super(paramMethodType);
  }
  
  public MutableCallSite(MethodHandle paramMethodHandle)
  {
    super(paramMethodHandle);
  }
  
  public final MethodHandle getTarget()
  {
    return target;
  }
  
  public void setTarget(MethodHandle paramMethodHandle)
  {
    checkTargetChange(target, paramMethodHandle);
    setTargetNormal(paramMethodHandle);
  }
  
  public final MethodHandle dynamicInvoker()
  {
    return makeDynamicInvoker();
  }
  
  public static void syncAll(MutableCallSite[] paramArrayOfMutableCallSite)
  {
    if (paramArrayOfMutableCallSite.length == 0) {
      return;
    }
    STORE_BARRIER.lazySet(0);
    for (int i = 0; i < paramArrayOfMutableCallSite.length; i++) {
      paramArrayOfMutableCallSite[i].getClass();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\MutableCallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */