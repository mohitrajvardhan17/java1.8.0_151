package com.sun.jmx.remote.internal;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class ClientListenerInfo
{
  private final ObjectName name;
  private final Integer listenerID;
  private final NotificationFilter filter;
  private final NotificationListener listener;
  private final Object handback;
  private final Subject delegationSubject;
  
  public ClientListenerInfo(Integer paramInteger, ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject, Subject paramSubject)
  {
    listenerID = paramInteger;
    name = paramObjectName;
    listener = paramNotificationListener;
    filter = paramNotificationFilter;
    handback = paramObject;
    delegationSubject = paramSubject;
  }
  
  public ObjectName getObjectName()
  {
    return name;
  }
  
  public Integer getListenerID()
  {
    return listenerID;
  }
  
  public NotificationFilter getNotificationFilter()
  {
    return filter;
  }
  
  public NotificationListener getListener()
  {
    return listener;
  }
  
  public Object getHandback()
  {
    return handback;
  }
  
  public Subject getDelegationSubject()
  {
    return delegationSubject;
  }
  
  public boolean sameAs(ObjectName paramObjectName)
  {
    return getObjectName().equals(paramObjectName);
  }
  
  public boolean sameAs(ObjectName paramObjectName, NotificationListener paramNotificationListener)
  {
    return (getObjectName().equals(paramObjectName)) && (getListener() == paramNotificationListener);
  }
  
  public boolean sameAs(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    return (getObjectName().equals(paramObjectName)) && (getListener() == paramNotificationListener) && (getNotificationFilter() == paramNotificationFilter) && (getHandback() == paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ClientListenerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */