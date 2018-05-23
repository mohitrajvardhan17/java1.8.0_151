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

public class JvmRTBootClassPathTableMeta
  extends SnmpMibTable
  implements Serializable
{
  static final long serialVersionUID = 42471379600792135L;
  private JvmRTBootClassPathEntryMeta node;
  protected SnmpStandardObjectServer objectserver;
  
  public JvmRTBootClassPathTableMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    super(paramSnmpMib);
    objectserver = paramSnmpStandardObjectServer;
  }
  
  protected JvmRTBootClassPathEntryMeta createJvmRTBootClassPathEntryMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
  {
    return new JvmRTBootClassPathEntryMeta(paramSnmpMib, objectserver);
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
    node = createJvmRTBootClassPathEntryMetaNode("JvmRTBootClassPathEntry", "JvmRTBootClassPathTable", paramSnmpMib, paramMBeanServer);
  }
  
  public synchronized void addEntry(SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject)
    throws SnmpStatusException
  {
    if (!(paramObject instanceof JvmRTBootClassPathEntryMBean)) {
      throw new ClassCastException("Entries for Table \"JvmRTBootClassPathTable\" must implement the \"JvmRTBootClassPathEntryMBean\" interface.");
    }
    super.addEntry(paramSnmpOid, paramObjectName, paramObject);
  }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    JvmRTBootClassPathEntryMBean localJvmRTBootClassPathEntryMBean = (JvmRTBootClassPathEntryMBean)getEntry(paramSnmpOid);
    synchronized (this)
    {
      node.setInstance(localJvmRTBootClassPathEntryMBean);
      node.get(paramSnmpMibSubRequest, paramInt);
    }
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    if (paramSnmpMibSubRequest.getSize() == 0) {
      return;
    }
    JvmRTBootClassPathEntryMBean localJvmRTBootClassPathEntryMBean = (JvmRTBootClassPathEntryMBean)getEntry(paramSnmpOid);
    synchronized (this)
    {
      node.setInstance(localJvmRTBootClassPathEntryMBean);
      node.set(paramSnmpMibSubRequest, paramInt);
    }
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    if (paramSnmpMibSubRequest.getSize() == 0) {
      return;
    }
    JvmRTBootClassPathEntryMBean localJvmRTBootClassPathEntryMBean = (JvmRTBootClassPathEntryMBean)getEntry(paramSnmpOid);
    synchronized (this)
    {
      node.setInstance(localJvmRTBootClassPathEntryMBean);
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
      JvmRTBootClassPathEntryMBean localJvmRTBootClassPathEntryMBean = (JvmRTBootClassPathEntryMBean)getEntry(paramSnmpOid);
      synchronized (this)
      {
        node.setInstance(localJvmRTBootClassPathEntryMBean);
        return node.skipVariable(paramLong, paramObject, paramInt);
      }
      return false;
    }
    catch (SnmpStatusException localSnmpStatusException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmRTBootClassPathTableMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */