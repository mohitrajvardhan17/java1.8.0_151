package com.sun.jmx.remote.security;

import javax.management.Notification;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public abstract interface NotificationAccessController
{
  public abstract void addNotificationListener(String paramString, ObjectName paramObjectName, Subject paramSubject)
    throws SecurityException;
  
  public abstract void removeNotificationListener(String paramString, ObjectName paramObjectName, Subject paramSubject)
    throws SecurityException;
  
  public abstract void fetchNotification(String paramString, ObjectName paramObjectName, Notification paramNotification, Subject paramSubject)
    throws SecurityException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\security\NotificationAccessController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */