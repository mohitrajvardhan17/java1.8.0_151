package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibEntry;
import com.sun.jmx.snmp.agent.SnmpMibNode;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;

public class JvmMemGCEntryMeta
  extends SnmpMibEntry
  implements Serializable, SnmpStandardMetaServer
{
  static final long serialVersionUID = 6082082529298387063L;
  protected JvmMemGCEntryMBean node;
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmMemGCEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    objectserver = paramSnmpStandardObjectServer;
    varList = new int[2];
    varList[0] = 3;
    varList[1] = 2;
    SnmpMibNode.sort(varList);
  }
  
  public SnmpValue get(long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 3: 
      return new SnmpCounter64(node.getJvmMemGCTimeMs());
    case 2: 
      return new SnmpCounter64(node.getJvmMemGCCount());
    }
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 3: 
      throw new SnmpStatusException(17);
    case 2: 
      throw new SnmpStatusException(17);
    }
    throw new SnmpStatusException(17);
  }
  
  public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 3: 
      throw new SnmpStatusException(17);
    case 2: 
      throw new SnmpStatusException(17);
    }
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmMemGCEntryMBean paramJvmMemGCEntryMBean)
  {
    node = paramJvmMemGCEntryMBean;
  }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    objectserver.get(this, paramSnmpMibSubRequest, paramInt);
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    objectserver.set(this, paramSnmpMibSubRequest, paramInt);
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    objectserver.check(this, paramSnmpMibSubRequest, paramInt);
  }
  
  public boolean isVariable(long paramLong)
  {
    switch ((int)paramLong)
    {
    case 2: 
    case 3: 
      return true;
    }
    return false;
  }
  
  public boolean isReadable(long paramLong)
  {
    switch ((int)paramLong)
    {
    case 2: 
    case 3: 
      return true;
    }
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt)
  {
    switch ((int)paramLong)
    {
    case 2: 
    case 3: 
      if (paramInt == 0) {
        return true;
      }
      break;
    }
    return super.skipVariable(paramLong, paramObject, paramInt);
  }
  
  public String getAttributeName(long paramLong)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 3: 
      return "JvmMemGCTimeMs";
    case 2: 
      return "JvmMemGCCount";
    }
    throw new SnmpStatusException(225);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmMemGCEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */