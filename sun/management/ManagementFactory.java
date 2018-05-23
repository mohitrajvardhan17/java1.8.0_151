package sun.management;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;

class ManagementFactory
{
  private ManagementFactory() {}
  
  private static MemoryPoolMXBean createMemoryPool(String paramString, boolean paramBoolean, long paramLong1, long paramLong2)
  {
    return new MemoryPoolImpl(paramString, paramBoolean, paramLong1, paramLong2);
  }
  
  private static MemoryManagerMXBean createMemoryManager(String paramString)
  {
    return new MemoryManagerImpl(paramString);
  }
  
  private static GarbageCollectorMXBean createGarbageCollector(String paramString1, String paramString2)
  {
    return new GarbageCollectorImpl(paramString1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ManagementFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */