package java.lang.invoke;

public class SwitchPoint
{
  private static final MethodHandle K_true = MethodHandles.constant(Boolean.TYPE, Boolean.valueOf(true));
  private static final MethodHandle K_false = MethodHandles.constant(Boolean.TYPE, Boolean.valueOf(false));
  private final MutableCallSite mcs = new MutableCallSite(K_true);
  private final MethodHandle mcsInvoker = mcs.dynamicInvoker();
  
  public SwitchPoint() {}
  
  public boolean hasBeenInvalidated()
  {
    return mcs.getTarget() != K_true;
  }
  
  public MethodHandle guardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
  {
    if (mcs.getTarget() == K_false) {
      return paramMethodHandle2;
    }
    return MethodHandles.guardWithTest(mcsInvoker, paramMethodHandle1, paramMethodHandle2);
  }
  
  public static void invalidateAll(SwitchPoint[] paramArrayOfSwitchPoint)
  {
    if (paramArrayOfSwitchPoint.length == 0) {
      return;
    }
    MutableCallSite[] arrayOfMutableCallSite = new MutableCallSite[paramArrayOfSwitchPoint.length];
    for (int i = 0; i < paramArrayOfSwitchPoint.length; i++)
    {
      SwitchPoint localSwitchPoint = paramArrayOfSwitchPoint[i];
      if (localSwitchPoint == null) {
        break;
      }
      arrayOfMutableCallSite[i] = mcs;
      mcs.setTarget(K_false);
    }
    MutableCallSite.syncAll(arrayOfMutableCallSite);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\SwitchPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */