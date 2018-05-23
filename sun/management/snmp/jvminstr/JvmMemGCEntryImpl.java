package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.GarbageCollectorMXBean;
import sun.management.snmp.jvmmib.JvmMemGCEntryMBean;

public class JvmMemGCEntryImpl
  implements JvmMemGCEntryMBean
{
  protected final int JvmMemManagerIndex;
  protected final GarbageCollectorMXBean gcm;
  
  public JvmMemGCEntryImpl(GarbageCollectorMXBean paramGarbageCollectorMXBean, int paramInt)
  {
    gcm = paramGarbageCollectorMXBean;
    JvmMemManagerIndex = paramInt;
  }
  
  public Long getJvmMemGCTimeMs()
    throws SnmpStatusException
  {
    return new Long(gcm.getCollectionTime());
  }
  
  public Long getJvmMemGCCount()
    throws SnmpStatusException
  {
    return new Long(gcm.getCollectionCount());
  }
  
  public Integer getJvmMemManagerIndex()
    throws SnmpStatusException
  {
    return new Integer(JvmMemManagerIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmMemGCEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */