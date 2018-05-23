package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTBootClassPathEntryMBean;

public class JvmRTBootClassPathEntryImpl
  implements JvmRTBootClassPathEntryMBean, Serializable
{
  static final long serialVersionUID = -2282652055235913013L;
  private final String item = validPathElementTC(paramString);
  private final int index;
  
  public JvmRTBootClassPathEntryImpl(String paramString, int paramInt)
  {
    index = paramInt;
  }
  
  private String validPathElementTC(String paramString)
  {
    return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString);
  }
  
  public String getJvmRTBootClassPathItem()
    throws SnmpStatusException
  {
    return item;
  }
  
  public Integer getJvmRTBootClassPathIndex()
    throws SnmpStatusException
  {
    return new Integer(index);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmRTBootClassPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */