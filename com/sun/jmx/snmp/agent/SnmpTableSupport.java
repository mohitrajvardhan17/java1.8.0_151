package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public abstract class SnmpTableSupport
  implements SnmpTableEntryFactory, SnmpTableCallbackHandler, Serializable
{
  protected List<Object> entries;
  protected SnmpMibTable meta;
  protected SnmpMib theMib;
  private boolean registrationRequired = false;
  
  protected SnmpTableSupport(SnmpMib paramSnmpMib)
  {
    theMib = paramSnmpMib;
    meta = getRegisteredTableMeta(paramSnmpMib);
    bindWithTableMeta();
    entries = allocateTable();
  }
  
  public abstract void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt, SnmpMibTable paramSnmpMibTable)
    throws SnmpStatusException;
  
  public Object getEntry(int paramInt)
  {
    if (entries == null) {
      return null;
    }
    return entries.get(paramInt);
  }
  
  public int getSize()
  {
    return meta.getSize();
  }
  
  public void setCreationEnabled(boolean paramBoolean)
  {
    meta.setCreationEnabled(paramBoolean);
  }
  
  public boolean isCreationEnabled()
  {
    return meta.isCreationEnabled();
  }
  
  public boolean isRegistrationRequired()
  {
    return registrationRequired;
  }
  
  public SnmpIndex buildSnmpIndex(SnmpOid paramSnmpOid)
    throws SnmpStatusException
  {
    return buildSnmpIndex(paramSnmpOid.longValue(false), 0);
  }
  
  public abstract SnmpOid buildOidFromIndex(SnmpIndex paramSnmpIndex)
    throws SnmpStatusException;
  
  public abstract ObjectName buildNameFromIndex(SnmpIndex paramSnmpIndex)
    throws SnmpStatusException;
  
  public void addEntryCb(int paramInt, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject, SnmpMibTable paramSnmpMibTable)
    throws SnmpStatusException
  {
    try
    {
      if (entries != null) {
        entries.add(paramInt, paramObject);
      }
    }
    catch (Exception localException)
    {
      throw new SnmpStatusException(2);
    }
  }
  
  public void removeEntryCb(int paramInt, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject, SnmpMibTable paramSnmpMibTable)
    throws SnmpStatusException
  {
    try
    {
      if (entries != null) {
        entries.remove(paramInt);
      }
    }
    catch (Exception localException) {}
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    meta.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    meta.removeNotificationListener(paramNotificationListener);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return meta.getNotificationInfo();
  }
  
  protected abstract SnmpIndex buildSnmpIndex(long[] paramArrayOfLong, int paramInt)
    throws SnmpStatusException;
  
  protected abstract SnmpMibTable getRegisteredTableMeta(SnmpMib paramSnmpMib);
  
  protected List<Object> allocateTable()
  {
    return new ArrayList();
  }
  
  protected void addEntry(SnmpIndex paramSnmpIndex, Object paramObject)
    throws SnmpStatusException
  {
    SnmpOid localSnmpOid = buildOidFromIndex(paramSnmpIndex);
    ObjectName localObjectName = null;
    if (isRegistrationRequired()) {
      localObjectName = buildNameFromIndex(paramSnmpIndex);
    }
    meta.addEntry(localSnmpOid, localObjectName, paramObject);
  }
  
  protected void addEntry(SnmpIndex paramSnmpIndex, ObjectName paramObjectName, Object paramObject)
    throws SnmpStatusException
  {
    SnmpOid localSnmpOid = buildOidFromIndex(paramSnmpIndex);
    meta.addEntry(localSnmpOid, paramObjectName, paramObject);
  }
  
  protected void removeEntry(SnmpIndex paramSnmpIndex, Object paramObject)
    throws SnmpStatusException
  {
    SnmpOid localSnmpOid = buildOidFromIndex(paramSnmpIndex);
    meta.removeEntry(localSnmpOid, paramObject);
  }
  
  protected Object[] getBasicEntries()
  {
    if (entries == null) {
      return null;
    }
    Object[] arrayOfObject = new Object[entries.size()];
    entries.toArray(arrayOfObject);
    return arrayOfObject;
  }
  
  protected void bindWithTableMeta()
  {
    if (meta == null) {
      return;
    }
    registrationRequired = meta.isRegistrationRequired();
    meta.registerEntryFactory(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */