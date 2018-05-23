package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.MonitorInfoCompositeData;

public class MonitorInfo
  extends LockInfo
{
  private int stackDepth;
  private StackTraceElement stackFrame;
  
  public MonitorInfo(String paramString, int paramInt1, int paramInt2, StackTraceElement paramStackTraceElement)
  {
    super(paramString, paramInt1);
    if ((paramInt2 >= 0) && (paramStackTraceElement == null)) {
      throw new IllegalArgumentException("Parameter stackDepth is " + paramInt2 + " but stackFrame is null");
    }
    if ((paramInt2 < 0) && (paramStackTraceElement != null)) {
      throw new IllegalArgumentException("Parameter stackDepth is " + paramInt2 + " but stackFrame is not null");
    }
    stackDepth = paramInt2;
    stackFrame = paramStackTraceElement;
  }
  
  public int getLockedStackDepth()
  {
    return stackDepth;
  }
  
  public StackTraceElement getLockedStackFrame()
  {
    return stackFrame;
  }
  
  public static MonitorInfo from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof MonitorInfoCompositeData)) {
      return ((MonitorInfoCompositeData)paramCompositeData).getMonitorInfo();
    }
    MonitorInfoCompositeData.validateCompositeData(paramCompositeData);
    String str = MonitorInfoCompositeData.getClassName(paramCompositeData);
    int i = MonitorInfoCompositeData.getIdentityHashCode(paramCompositeData);
    int j = MonitorInfoCompositeData.getLockedStackDepth(paramCompositeData);
    StackTraceElement localStackTraceElement = MonitorInfoCompositeData.getLockedStackFrame(paramCompositeData);
    return new MonitorInfo(str, i, j, localStackTraceElement);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\MonitorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */