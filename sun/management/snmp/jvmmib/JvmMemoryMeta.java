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

public class JvmMemoryMeta
  extends SnmpMibGroup
  implements Serializable, SnmpStandardMetaServer
{
  private static final long serialVersionUID = 9047644262627149214L;
  protected JvmMemoryMBean node;
  protected SnmpStandardObjectServer objectserver = null;
  protected JvmMemMgrPoolRelTableMeta tableJvmMemMgrPoolRelTable = null;
  protected JvmMemPoolTableMeta tableJvmMemPoolTable = null;
  protected JvmMemGCTableMeta tableJvmMemGCTable = null;
  protected JvmMemManagerTableMeta tableJvmMemManagerTable = null;
  
  public JvmMemoryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    objectserver = paramSnmpStandardObjectServer;
    try
    {
      registerObject(120L);
      registerObject(23L);
      registerObject(22L);
      registerObject(21L);
      registerObject(110L);
      registerObject(20L);
      registerObject(13L);
      registerObject(12L);
      registerObject(3L);
      registerObject(11L);
      registerObject(2L);
      registerObject(101L);
      registerObject(10L);
      registerObject(1L);
      registerObject(100L);
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
    case 120: 
      throw new SnmpStatusException(224);
    case 23: 
      return new SnmpCounter64(node.getJvmMemoryNonHeapMaxSize());
    case 22: 
      return new SnmpCounter64(node.getJvmMemoryNonHeapCommitted());
    case 21: 
      return new SnmpCounter64(node.getJvmMemoryNonHeapUsed());
    case 110: 
      throw new SnmpStatusException(224);
    case 20: 
      return new SnmpCounter64(node.getJvmMemoryNonHeapInitSize());
    case 13: 
      return new SnmpCounter64(node.getJvmMemoryHeapMaxSize());
    case 12: 
      return new SnmpCounter64(node.getJvmMemoryHeapCommitted());
    case 3: 
      return new SnmpInt(node.getJvmMemoryGCCall());
    case 11: 
      return new SnmpCounter64(node.getJvmMemoryHeapUsed());
    case 2: 
      return new SnmpInt(node.getJvmMemoryGCVerboseLevel());
    case 101: 
      throw new SnmpStatusException(224);
    case 10: 
      return new SnmpCounter64(node.getJvmMemoryHeapInitSize());
    case 1: 
      return new SnmpGauge(node.getJvmMemoryPendingFinalCount());
    case 100: 
      throw new SnmpStatusException(224);
    }
    throw new SnmpStatusException(225);
  }
  
  public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 120: 
      throw new SnmpStatusException(17);
    case 23: 
      throw new SnmpStatusException(17);
    case 22: 
      throw new SnmpStatusException(17);
    case 21: 
      throw new SnmpStatusException(17);
    case 110: 
      throw new SnmpStatusException(17);
    case 20: 
      throw new SnmpStatusException(17);
    case 13: 
      throw new SnmpStatusException(17);
    case 12: 
      throw new SnmpStatusException(17);
    case 3: 
      if ((paramSnmpValue instanceof SnmpInt))
      {
        try
        {
          node.setJvmMemoryGCCall(new EnumJvmMemoryGCCall(((SnmpInt)paramSnmpValue).toInteger()));
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          throw new SnmpStatusException(10);
        }
        return new SnmpInt(node.getJvmMemoryGCCall());
      }
      throw new SnmpStatusException(7);
    case 11: 
      throw new SnmpStatusException(17);
    case 2: 
      if ((paramSnmpValue instanceof SnmpInt))
      {
        try
        {
          node.setJvmMemoryGCVerboseLevel(new EnumJvmMemoryGCVerboseLevel(((SnmpInt)paramSnmpValue).toInteger()));
        }
        catch (IllegalArgumentException localIllegalArgumentException2)
        {
          throw new SnmpStatusException(10);
        }
        return new SnmpInt(node.getJvmMemoryGCVerboseLevel());
      }
      throw new SnmpStatusException(7);
    case 101: 
      throw new SnmpStatusException(17);
    case 10: 
      throw new SnmpStatusException(17);
    case 1: 
      throw new SnmpStatusException(17);
    case 100: 
      throw new SnmpStatusException(17);
    }
    throw new SnmpStatusException(17);
  }
  
  public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    switch ((int)paramLong)
    {
    case 120: 
      throw new SnmpStatusException(17);
    case 23: 
      throw new SnmpStatusException(17);
    case 22: 
      throw new SnmpStatusException(17);
    case 21: 
      throw new SnmpStatusException(17);
    case 110: 
      throw new SnmpStatusException(17);
    case 20: 
      throw new SnmpStatusException(17);
    case 13: 
      throw new SnmpStatusException(17);
    case 12: 
      throw new SnmpStatusException(17);
    case 3: 
      if ((paramSnmpValue instanceof SnmpInt)) {
        try
        {
          node.checkJvmMemoryGCCall(new EnumJvmMemoryGCCall(((SnmpInt)paramSnmpValue).toInteger()));
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          throw new SnmpStatusException(10);
        }
      } else {
        throw new SnmpStatusException(7);
      }
      break;
    case 11: 
      throw new SnmpStatusException(17);
    case 2: 
      if ((paramSnmpValue instanceof SnmpInt)) {
        try
        {
          node.checkJvmMemoryGCVerboseLevel(new EnumJvmMemoryGCVerboseLevel(((SnmpInt)paramSnmpValue).toInteger()));
        }
        catch (IllegalArgumentException localIllegalArgumentException2)
        {
          throw new SnmpStatusException(10);
        }
      } else {
        throw new SnmpStatusException(7);
      }
      break;
    case 101: 
      throw new SnmpStatusException(17);
    case 10: 
      throw new SnmpStatusException(17);
    case 1: 
      throw new SnmpStatusException(17);
    case 100: 
      throw new SnmpStatusException(17);
    default: 
      throw new SnmpStatusException(17);
    }
  }
  
  protected void setInstance(JvmMemoryMBean paramJvmMemoryMBean)
  {
    node = paramJvmMemoryMBean;
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
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
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
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
      return true;
    }
    return false;
  }
  
  public boolean skipVariable(long paramLong, Object paramObject, int paramInt)
  {
    switch ((int)paramLong)
    {
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
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
    case 120: 
      throw new SnmpStatusException(224);
    case 23: 
      return "JvmMemoryNonHeapMaxSize";
    case 22: 
      return "JvmMemoryNonHeapCommitted";
    case 21: 
      return "JvmMemoryNonHeapUsed";
    case 110: 
      throw new SnmpStatusException(224);
    case 20: 
      return "JvmMemoryNonHeapInitSize";
    case 13: 
      return "JvmMemoryHeapMaxSize";
    case 12: 
      return "JvmMemoryHeapCommitted";
    case 3: 
      return "JvmMemoryGCCall";
    case 11: 
      return "JvmMemoryHeapUsed";
    case 2: 
      return "JvmMemoryGCVerboseLevel";
    case 101: 
      throw new SnmpStatusException(224);
    case 10: 
      return "JvmMemoryHeapInitSize";
    case 1: 
      return "JvmMemoryPendingFinalCount";
    case 100: 
      throw new SnmpStatusException(224);
    }
    throw new SnmpStatusException(225);
  }
  
  public boolean isTable(long paramLong)
  {
    switch ((int)paramLong)
    {
    case 120: 
      return true;
    case 110: 
      return true;
    case 101: 
      return true;
    case 100: 
      return true;
    }
    return false;
  }
  
  public SnmpMibTable getTable(long paramLong)
  {
    switch ((int)paramLong)
    {
    case 120: 
      return tableJvmMemMgrPoolRelTable;
    case 110: 
      return tableJvmMemPoolTable;
    case 101: 
      return tableJvmMemGCTable;
    case 100: 
      return tableJvmMemManagerTable;
    }
    return null;
  }
  
  public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    tableJvmMemMgrPoolRelTable = createJvmMemMgrPoolRelTableMetaNode("JvmMemMgrPoolRelTable", "JvmMemory", paramSnmpMib, paramMBeanServer);
    if (tableJvmMemMgrPoolRelTable != null)
    {
      tableJvmMemMgrPoolRelTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmMemMgrPoolRelTable", tableJvmMemMgrPoolRelTable);
    }
    tableJvmMemPoolTable = createJvmMemPoolTableMetaNode("JvmMemPoolTable", "JvmMemory", paramSnmpMib, paramMBeanServer);
    if (tableJvmMemPoolTable != null)
    {
      tableJvmMemPoolTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmMemPoolTable", tableJvmMemPoolTable);
    }
    tableJvmMemGCTable = createJvmMemGCTableMetaNode("JvmMemGCTable", "JvmMemory", paramSnmpMib, paramMBeanServer);
    if (tableJvmMemGCTable != null)
    {
      tableJvmMemGCTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmMemGCTable", tableJvmMemGCTable);
    }
    tableJvmMemManagerTable = createJvmMemManagerTableMetaNode("JvmMemManagerTable", "JvmMemory", paramSnmpMib, paramMBeanServer);
    if (tableJvmMemManagerTable != null)
    {
      tableJvmMemManagerTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
      paramSnmpMib.registerTableMeta("JvmMemManagerTable", tableJvmMemManagerTable);
    }
  }
  
  protected JvmMemMgrPoolRelTableMeta createJvmMemMgrPoolRelTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemMgrPoolRelTableMeta(paramSnmpMib, objectserver);
  }
  
  protected JvmMemPoolTableMeta createJvmMemPoolTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemPoolTableMeta(paramSnmpMib, objectserver);
  }
  
  protected JvmMemGCTableMeta createJvmMemGCTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemGCTableMeta(paramSnmpMib, objectserver);
  }
  
  protected JvmMemManagerTableMeta createJvmMemManagerTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemManagerTableMeta(paramSnmpMib, objectserver);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmMemoryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */