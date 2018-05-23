package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTLibraryPathEntryMBean;

public class JvmRTLibraryPathEntryImpl
  implements JvmRTLibraryPathEntryMBean, Serializable
{
  static final long serialVersionUID = -3322438153507369765L;
  private final String item = validPathElementTC(paramString);
  private final int index;
  
  public JvmRTLibraryPathEntryImpl(String paramString, int paramInt)
  {
    index = paramInt;
  }
  
  private String validPathElementTC(String paramString)
  {
    return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString);
  }
  
  public String getJvmRTLibraryPathItem()
    throws SnmpStatusException
  {
    return item;
  }
  
  public Integer getJvmRTLibraryPathIndex()
    throws SnmpStatusException
  {
    return new Integer(index);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmRTLibraryPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */