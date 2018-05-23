package jdk.management.cmm;

import java.lang.management.PlatformManagedObject;
import jdk.Exported;

@Exported
public abstract interface SystemResourcePressureMXBean
  extends PlatformManagedObject
{
  public abstract int getMemoryPressure();
  
  public abstract void setMemoryPressure(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\cmm\SystemResourcePressureMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */