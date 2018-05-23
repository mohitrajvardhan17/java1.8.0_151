package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
import sun.management.snmp.jvmmib.JvmThreadingMeta;

public class JvmThreadingMetaImpl
  extends JvmThreadingMeta
{
  static final long serialVersionUID = -2104788458393251457L;
  
  public JvmThreadingMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
  }
  
  protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmThreadInstanceTableMetaImpl(paramSnmpMib, objectserver);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmThreadingMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */