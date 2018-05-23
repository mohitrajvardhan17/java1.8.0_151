package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import sun.management.snmp.jvmmib.JvmMemMgrPoolRelEntryMBean;

public class JvmMemMgrPoolRelEntryImpl
  implements JvmMemMgrPoolRelEntryMBean
{
  protected final int JvmMemManagerIndex;
  protected final int JvmMemPoolIndex;
  protected final String mmmName;
  protected final String mpmName;
  
  public JvmMemMgrPoolRelEntryImpl(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    JvmMemManagerIndex = paramInt1;
    JvmMemPoolIndex = paramInt2;
    mmmName = paramString1;
    mpmName = paramString2;
  }
  
  public String getJvmMemMgrRelPoolName()
    throws SnmpStatusException
  {
    return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(mpmName);
  }
  
  public String getJvmMemMgrRelManagerName()
    throws SnmpStatusException
  {
    return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(mmmName);
  }
  
  public Integer getJvmMemManagerIndex()
    throws SnmpStatusException
  {
    return new Integer(JvmMemManagerIndex);
  }
  
  public Integer getJvmMemPoolIndex()
    throws SnmpStatusException
  {
    return new Integer(JvmMemPoolIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmMemMgrPoolRelEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */