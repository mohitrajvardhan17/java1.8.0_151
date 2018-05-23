package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTInputArgsEntryMBean;

public class JvmRTInputArgsEntryImpl
  implements JvmRTInputArgsEntryMBean, Serializable
{
  static final long serialVersionUID = 1000306518436503395L;
  private final String item = validArgValueTC(paramString);
  private final int index;
  
  public JvmRTInputArgsEntryImpl(String paramString, int paramInt)
  {
    index = paramInt;
  }
  
  private String validArgValueTC(String paramString)
  {
    return JVM_MANAGEMENT_MIB_IMPL.validArgValueTC(paramString);
  }
  
  public String getJvmRTInputArgsItem()
    throws SnmpStatusException
  {
    return item;
  }
  
  public Integer getJvmRTInputArgsIndex()
    throws SnmpStatusException
  {
    return new Integer(index);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmRTInputArgsEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */