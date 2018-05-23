package com.sun.jmx.snmp.agent;

import javax.management.Notification;
import javax.management.ObjectName;

public class SnmpTableEntryNotification
  extends Notification
{
  public static final String SNMP_ENTRY_ADDED = "jmx.snmp.table.entry.added";
  public static final String SNMP_ENTRY_REMOVED = "jmx.snmp.table.entry.removed";
  private final Object entry;
  private final ObjectName name;
  private static final long serialVersionUID = 5832592016227890252L;
  
  SnmpTableEntryNotification(String paramString, Object paramObject1, long paramLong1, long paramLong2, Object paramObject2, ObjectName paramObjectName)
  {
    super(paramString, paramObject1, paramLong1, paramLong2);
    entry = paramObject2;
    name = paramObjectName;
  }
  
  public Object getEntry()
  {
    return entry;
  }
  
  public ObjectName getEntryName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableEntryNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */