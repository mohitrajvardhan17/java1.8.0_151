package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTClassPathEntryMBean;

public class JvmRTClassPathEntryImpl
  implements JvmRTClassPathEntryMBean, Serializable
{
  static final long serialVersionUID = 8524792845083365742L;
  private final String item = validPathElementTC(paramString);
  private final int index;
  
  public JvmRTClassPathEntryImpl(String paramString, int paramInt)
  {
    index = paramInt;
  }
  
  private String validPathElementTC(String paramString)
  {
    return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString);
  }
  
  public String getJvmRTClassPathItem()
    throws SnmpStatusException
  {
    return item;
  }
  
  public Integer getJvmRTClassPathIndex()
    throws SnmpStatusException
  {
    return new Integer(index);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmRTClassPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */