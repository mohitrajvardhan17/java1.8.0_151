package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpGauge;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibGroup;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;

public class JvmClassLoadingMeta
  extends SnmpMibGroup
  implements Serializable, SnmpStandardMetaServer
{
  static final long serialVersionUID = 5722857476941218568L;
  protected JvmClassLoadingMBean node;
  protected SnmpStandardObjectServer objectserver = null;
  
  public JvmClassLoadingMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    objectserver = paramSnmpStandardObjectServer;
    try
    {
      registerObject(4L);
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
    case 4: 
      return new SnmpInt(node.getJvmClassesVerboseLevel());
    case 3: 
      return new SnmpCounter64(node.getJvmClassesUnloadedCount());
    case 2: 
      return new SnmpCounter64(node.getJvmClassesTotalLoadedCount());
    case 1: 
      return new SnmpGauge(node.getJvmClassesLoadedCount());
    }
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 4: 
      if ((paramSnmpValue instanceof SnmpInt))
      {
        try
        {
          node.setJvmClassesVerboseLevel(new EnumJvmClassesVerboseLevel(((SnmpInt)paramSnmpValue).toInteger()));
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          throw new SnmpStatusException(10);
        }
        return new SnmpInt(node.getJvmClassesVerboseLevel());
      }
      throw new SnmpStatusException(7);
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
    case 4: 
      if ((paramSnmpValue instanceof SnmpInt)) {
        try
        {
          node.checkJvmClassesVerboseLevel(new EnumJvmClassesVerboseLevel(((SnmpInt)paramSnmpValue).toInteger()));
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          throw new SnmpStatusException(10);
        }
      } else {
        throw new SnmpStatusException(7);
      }
      break;
    case 3: 
      throw new SnmpStatusException(17);
    case 2: 
      throw new SnmpStatusException(17);
    case 1: 
      throw new SnmpStatusException(17);
    default: 
      throw new SnmpStatusException(17);
    }
  }
  
  protected void setInstance(JvmClassLoadingMBean paramJvmClassLoadingMBean)
  {
    node = paramJvmClassLoadingMBean;
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
    case 4: 
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
    case 4: 
      return "JvmClassesVerboseLevel";
    case 3: 
      return "JvmClassesUnloadedCount";
    case 2: 
      return "JvmClassesTotalLoadedCount";
    case 1: 
      return "JvmClassesLoadedCount";
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmClassLoadingMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */