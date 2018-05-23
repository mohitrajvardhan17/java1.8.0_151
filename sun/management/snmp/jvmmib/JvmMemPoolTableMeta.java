package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import com.sun.jmx.snmp.agent.SnmpTableEntryFactory;
import java.io.Serializable;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class JvmMemPoolTableMeta
  extends SnmpMibTable
  implements Serializable
{
  static final long serialVersionUID = -2799470815264898659L;
  private JvmMemPoolEntryMeta node;
  protected SnmpStandardObjectServer objectserver;
  
  public JvmMemPoolTableMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    super(paramSnmpMib);
    objectserver = paramSnmpStandardObjectServer;
  }
  
  protected JvmMemPoolEntryMeta createJvmMemPoolEntryMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmMemPoolEntryMeta(paramSnmpMib, objectserver);
  }
  
  public void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    if (factory != null) {
      factory.createNewEntry(paramSnmpMibSubRequest, paramSnmpOid, paramInt, this);
    } else {
      throw new SnmpStatusException(6);
    }
  }
  
  public boolean isRegistrationRequired()
  {
    return false;
  }
  
  public void registerEntryNode(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    node = createJvmMemPoolEntryMetaNode("JvmMemPoolEntry", "JvmMemPoolTable", paramSnmpMib, paramMBeanServer);
  }
  
  public synchronized void addEntry(SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject)
    throws SnmpStatusException
  {
    if (!(paramObject instanceof JvmMemPoolEntryMBean)) {
      throw new ClassCastException("Entries for Table \"JvmMemPoolTable\" must implement the \"JvmMemPoolEntryMBean\" interface.");
    }
    super.addEntry(paramSnmpOid, paramObjectName, paramObject);
  }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
    synchronized (this)
    {
      node.setInstance(localJvmMemPoolEntryMBean);
      node.get(paramSnmpMibSubRequest, paramInt);
    }
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    if (paramSnmpMibSubRequest.getSize() == 0) {
      return;
    }
    JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
    synchronized (this)
    {
      node.setInstance(localJvmMemPoolEntryMBean);
      node.set(paramSnmpMibSubRequest, paramInt);
    }
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    if (paramSnmpMibSubRequest.getSize() == 0) {
      return;
    }
    JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
    synchronized (this)
    {
      node.setInstance(localJvmMemPoolEntryMBean);
      node.check(paramSnmpMibSubRequest, paramInt);
    }
  }
  
  public void validateVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    node.validateVarId(paramLong, paramObject);
  }
  
  public boolean isReadableEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    return node.isReadable(paramLong);
  }
  
  public long getNextVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
    throws SnmpStatusException
  {
    for (long l = node.getNextVarId(paramLong, paramObject); !isReadableEntryId(paramSnmpOid, l, paramObject); l = node.getNextVarId(l, paramObject)) {}
    return l;
  }
  
  public boolean skipEntryVariable(SnmpOid paramSnmpOid, long paramLong, Object paramObject, int paramInt)
  {
    try
    {
      JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
      synchronized (this)
      {
        node.setInstance(localJvmMemPoolEntryMBean);
        return node.skipVariable(paramLong, paramObject, paramInt);
      }
      return false;
    }
    catch (SnmpStatusException localSnmpStatusException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmMemPoolTableMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */