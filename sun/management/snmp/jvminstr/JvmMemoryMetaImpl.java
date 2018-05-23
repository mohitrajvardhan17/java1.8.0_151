package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.JvmMemGCTableMeta;
import sun.management.snmp.jvmmib.JvmMemManagerTableMeta;
import sun.management.snmp.jvmmib.JvmMemMgrPoolRelTableMeta;
import sun.management.snmp.jvmmib.JvmMemPoolTableMeta;
import sun.management.snmp.jvmmib.JvmMemoryMeta;

public class JvmMemoryMetaImpl
  extends JvmMemoryMeta
{
  static final long serialVersionUID = -6500448253825893071L;
  
  public JvmMemoryMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
  }
  
  protected JvmMemManagerTableMeta createJvmMemManagerTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemManagerTableMetaImpl(paramSnmpMib, objectserver);
  }
  
  protected JvmMemGCTableMeta createJvmMemGCTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemGCTableMetaImpl(paramSnmpMib, objectserver);
  }
  
  protected JvmMemPoolTableMeta createJvmMemPoolTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemPoolTableMetaImpl(paramSnmpMib, objectserver);
  }
  
  protected JvmMemMgrPoolRelTableMeta createJvmMemMgrPoolRelTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemMgrPoolRelTableMetaImpl(paramSnmpMib, objectserver);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmMemoryMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */