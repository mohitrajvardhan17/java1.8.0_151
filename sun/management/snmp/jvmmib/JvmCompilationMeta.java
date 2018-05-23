package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibGroup;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;

public class JvmCompilationMeta
  extends SnmpMibGroup
  implements Serializable, SnmpStandardMetaServer
{
  static final long serialVersionUID = -95492874115033638L;
  protected JvmCompilationMBean node;
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmCompilationMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    objectserver = paramSnmpStandardObjectServer;
    try
    {
      registerObject(3L);
      registerObject(2L);
      registerObject(1L);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException.getMessage());
    }
  }
  
  public SnmpValue get(long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 3: 
      return new SnmpInt(node.getJvmJITCompilerTimeMonitoring());
    case 2: 
      return new SnmpCounter64(node.getJvmJITCompilerTimeMs());
    case 1: 
      return new SnmpString(node.getJvmJITCompilerName());
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
  
  protected void setInstance(JvmCompilationMBean paramJvmCompilationMBean)
  {
    node = paramJvmCompilationMBean;
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
    case 1: 
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
      return "JvmJITCompilerTimeMonitoring";
    case 2: 
      return "JvmJITCompilerTimeMs";
    case 1: 
      return "JvmJITCompilerName";
    }
    throw new SnmpStatusException(225);
  }
  
  public boolean isTable(long paramLong)
  {
    switch ((int)paramLong)
    {
    }
    return false;
  }
  
  public SnmpMibTable getTable(long paramLong)
  {
    return null;
  }
  
  public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmCompilationMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */