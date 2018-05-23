package sun.management.snmp.util;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JvmContextFactory
  implements SnmpUserDataFactory
{
  public JvmContextFactory() {}
  
  public Object allocateUserData(SnmpPdu paramSnmpPdu)
    throws SnmpStatusException
  {
    return Collections.synchronizedMap(new HashMap());
  }
  
  public void releaseUserData(Object paramObject, SnmpPdu paramSnmpPdu)
    throws SnmpStatusException
  {
    ((Map)paramObject).clear();
  }
  
  public static Map<Object, Object> getUserData()
  {
    Object localObject = ThreadContext.get("SnmpUserData");
    if ((localObject instanceof Map)) {
      return (Map)Util.cast(localObject);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\util\JvmContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */