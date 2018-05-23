package java.lang.management;

public abstract interface MemoryManagerMXBean
  extends PlatformManagedObject
{
  public abstract String getName();
  
  public abstract boolean isValid();
  
  public abstract String[] getMemoryPoolNames();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\MemoryManagerMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */