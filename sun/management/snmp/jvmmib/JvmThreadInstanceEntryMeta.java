package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpOid;
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

public class JvmThreadInstanceEntryMeta
  extends SnmpMibEntry
  implements Serializable, SnmpStandardMetaServer
{
  static final long serialVersionUID = -2015330111801477399L;
  protected JvmThreadInstanceEntryMBean node;
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmThreadInstanceEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    objectserver = paramSnmpStandardObjectServer;
    varList = new int[10];
    varList[0] = 9;
    varList[1] = 8;
    varList[2] = 7;
    varList[3] = 6;
    varList[4] = 5;
    varList[5] = 4;
    varList[6] = 3;
    varList[7] = 11;
    varList[8] = 2;
    varList[9] = 10;
    SnmpMibNode.sort(varList);
  }
  
  public SnmpValue get(long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 9: 
      return new SnmpString(node.getJvmThreadInstName());
    case 8: 
      return new SnmpCounter64(node.getJvmThreadInstCpuTimeNs());
    case 7: 
      return new SnmpCounter64(node.getJvmThreadInstWaitTimeMs());
    case 6: 
      return new SnmpCounter64(node.getJvmThreadInstWaitCount());
    case 5: 
      return new SnmpCounter64(node.getJvmThreadInstBlockTimeMs());
    case 4: 
      return new SnmpCounter64(node.getJvmThreadInstBlockCount());
    case 3: 
      return new SnmpString(node.getJvmThreadInstState());
    case 11: 
      return new SnmpOid(node.getJvmThreadInstLockOwnerPtr());
    case 2: 
      return new SnmpCounter64(node.getJvmThreadInstId());
    case 10: 
      return new SnmpString(node.getJvmThreadInstLockName());
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
    case 9: 
      throw new SnmpStatusException(17);
    case 8: 
      throw new SnmpStatusException(17);
    case 7: 
      throw new SnmpStatusException(17);
    case 6: 
      throw new SnmpStatusException(17);
    case 5: 
      throw new SnmpStatusException(17);
    case 4: 
      throw new SnmpStatusException(17);
    case 3: 
      throw new SnmpStatusException(17);
    case 11: 
      throw new SnmpStatusException(17);
    case 2: 
      throw new SnmpStatusException(17);
    case 10: 
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
    case 9: 
      throw new SnmpStatusException(17);
    case 8: 
      throw new SnmpStatusException(17);
    case 7: 
      throw new SnmpStatusException(17);
    case 6: 
      throw new SnmpStatusException(17);
    case 5: 
      throw new SnmpStatusException(17);
    case 4: 
      throw new SnmpStatusException(17);
    case 3: 
      throw new SnmpStatusException(17);
    case 11: 
      throw new SnmpStatusException(17);
    case 2: 
      throw new SnmpStatusException(17);
    case 10: 
      throw new SnmpStatusException(17);
    case 1: 
      throw new SnmpStatusException(17);
    }
    throw new SnmpStatusException(17);
  }
  
  protected void setInstance(JvmThreadInstanceEntryMBean paramJvmThreadInstanceEntryMBean)
  {
    node = paramJvmThreadInstanceEntryMBean;
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
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
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
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
      return true;
    }
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt)
  {
    switch ((int)paramLong)
    {
    case 2: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
      if (paramInt == 0) {
        return true;
      }
      break;
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
    case 9: 
      return "JvmThreadInstName";
    case 8: 
      return "JvmThreadInstCpuTimeNs";
    case 7: 
      return "JvmThreadInstWaitTimeMs";
    case 6: 
      return "JvmThreadInstWaitCount";
    case 5: 
      return "JvmThreadInstBlockTimeMs";
    case 4: 
      return "JvmThreadInstBlockCount";
    case 3: 
      return "JvmThreadInstState";
    case 11: 
      return "JvmThreadInstLockOwnerPtr";
    case 2: 
      return "JvmThreadInstId";
    case 10: 
      return "JvmThreadInstLockName";
    case 1: 
      return "JvmThreadInstIndex";
    }
    throw new SnmpStatusException(225);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmThreadInstanceEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */