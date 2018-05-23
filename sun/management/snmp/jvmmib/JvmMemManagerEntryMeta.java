package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibEntry;
import com.sun.jmx.snmp.agent.SnmpMibNode;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;

public class JvmMemManagerEntryMeta
  extends SnmpMibEntry
  implements Serializable, SnmpStandardMetaServer
{
  static final long serialVersionUID = 8166956416408970453L;
  protected JvmMemManagerEntryMBean node;
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmMemManagerEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
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
      return new SnmpInt(node.getJvmMemManagerState());
    case 2: 
      return new SnmpString(node.getJvmMemManagerName());
    case 1: 
      throw new SnmpStatusException(224);
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
    case 1: 
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
    case 1: 
      throw new SnmpStatusException(17);
    }
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmMemManagerEntryMBean paramJvmMemManagerEntryMBean)
  {
    node = paramJvmMemManagerEntryMBean;
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
    case 1: 
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
    case 1: 
      return true;
    }
    return super.skipVariable(paramLong, paramObject, paramInt);
  }
  
  public String getAttributeName(long paramLong)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 3: 
      return "JvmMemManagerState";
    case 2: 
      return "JvmMemManagerName";
    case 1: 
      return "JvmMemManagerIndex";
    }
    throw new SnmpStatusException(225);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmMemManagerEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */