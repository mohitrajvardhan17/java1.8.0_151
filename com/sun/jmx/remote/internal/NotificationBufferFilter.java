package com.sun.jmx.remote.internal;

import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.TargetedNotification;

public abstract interface NotificationBufferFilter
{
  public abstract void apply(List<TargetedNotification> paramList, ObjectName paramObjectName, Notification paramNotification);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\NotificationBufferFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */