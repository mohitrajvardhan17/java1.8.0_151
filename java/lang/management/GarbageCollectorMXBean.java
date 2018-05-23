package java.lang.management;

public abstract interface GarbageCollectorMXBean
  extends MemoryManagerMXBean
{
  public abstract long getCollectionCount();
  
  public abstract long getCollectionTime();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\GarbageCollectorMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */