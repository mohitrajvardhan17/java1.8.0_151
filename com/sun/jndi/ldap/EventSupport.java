package com.sun.jndi.ldap;

import java.util.Collection;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.event.ObjectChangeListener;
import javax.naming.ldap.UnsolicitedNotification;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;

final class EventSupport
{
  private static final boolean debug = false;
  private LdapCtx ctx;
  private Hashtable<NotifierArgs, NamingEventNotifier> notifiers = new Hashtable(11);
  private Vector<UnsolicitedNotificationListener> unsolicited = null;
  private EventQueue eventQueue;
  
  EventSupport(LdapCtx paramLdapCtx)
  {
    ctx = paramLdapCtx;
  }
  
  synchronized void addNamingListener(String paramString, int paramInt, NamingListener paramNamingListener)
    throws NamingException
  {
    if (((paramNamingListener instanceof ObjectChangeListener)) || ((paramNamingListener instanceof NamespaceChangeListener)))
    {
      NotifierArgs localNotifierArgs = new NotifierArgs(paramString, paramInt, paramNamingListener);
      NamingEventNotifier localNamingEventNotifier = (NamingEventNotifier)notifiers.get(localNotifierArgs);
      if (localNamingEventNotifier == null)
      {
        localNamingEventNotifier = new NamingEventNotifier(this, ctx, localNotifierArgs, paramNamingListener);
        notifiers.put(localNotifierArgs, localNamingEventNotifier);
      }
      else
      {
        localNamingEventNotifier.addNamingListener(paramNamingListener);
      }
    }
    if ((paramNamingListener instanceof UnsolicitedNotificationListener))
    {
      if (unsolicited == null) {
        unsolicited = new Vector(3);
      }
      unsolicited.addElement((UnsolicitedNotificationListener)paramNamingListener);
    }
  }
  
  synchronized void addNamingListener(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener)
    throws NamingException
  {
    if (((paramNamingListener instanceof ObjectChangeListener)) || ((paramNamingListener instanceof NamespaceChangeListener)))
    {
      NotifierArgs localNotifierArgs = new NotifierArgs(paramString1, paramString2, paramSearchControls, paramNamingListener);
      NamingEventNotifier localNamingEventNotifier = (NamingEventNotifier)notifiers.get(localNotifierArgs);
      if (localNamingEventNotifier == null)
      {
        localNamingEventNotifier = new NamingEventNotifier(this, ctx, localNotifierArgs, paramNamingListener);
        notifiers.put(localNotifierArgs, localNamingEventNotifier);
      }
      else
      {
        localNamingEventNotifier.addNamingListener(paramNamingListener);
      }
    }
    if ((paramNamingListener instanceof UnsolicitedNotificationListener))
    {
      if (unsolicited == null) {
        unsolicited = new Vector(3);
      }
      unsolicited.addElement((UnsolicitedNotificationListener)paramNamingListener);
    }
  }
  
  synchronized void removeNamingListener(NamingListener paramNamingListener)
  {
    Iterator localIterator = notifiers.values().iterator();
    while (localIterator.hasNext())
    {
      NamingEventNotifier localNamingEventNotifier = (NamingEventNotifier)localIterator.next();
      if (localNamingEventNotifier != null)
      {
        localNamingEventNotifier.removeNamingListener(paramNamingListener);
        if (!localNamingEventNotifier.hasNamingListeners())
        {
          localNamingEventNotifier.stop();
          notifiers.remove(info);
        }
      }
    }
    if (unsolicited != null) {
      unsolicited.removeElement(paramNamingListener);
    }
  }
  
  synchronized boolean hasUnsolicited()
  {
    return (unsolicited != null) && (unsolicited.size() > 0);
  }
  
  synchronized void removeDeadNotifier(NotifierArgs paramNotifierArgs)
  {
    notifiers.remove(paramNotifierArgs);
  }
  
  synchronized void fireUnsolicited(Object paramObject)
  {
    if ((unsolicited == null) || (unsolicited.size() == 0)) {
      return;
    }
    Object localObject;
    if ((paramObject instanceof UnsolicitedNotification))
    {
      localObject = new UnsolicitedNotificationEvent(ctx, (UnsolicitedNotification)paramObject);
      queueEvent((EventObject)localObject, unsolicited);
    }
    else if ((paramObject instanceof NamingException))
    {
      localObject = new NamingExceptionEvent(ctx, (NamingException)paramObject);
      queueEvent((EventObject)localObject, unsolicited);
      unsolicited = null;
    }
  }
  
  synchronized void cleanup()
  {
    if (notifiers != null)
    {
      Iterator localIterator = notifiers.values().iterator();
      while (localIterator.hasNext())
      {
        NamingEventNotifier localNamingEventNotifier = (NamingEventNotifier)localIterator.next();
        localNamingEventNotifier.stop();
      }
      notifiers = null;
    }
    if (eventQueue != null)
    {
      eventQueue.stop();
      eventQueue = null;
    }
  }
  
  synchronized void queueEvent(EventObject paramEventObject, Vector<? extends NamingListener> paramVector)
  {
    if (eventQueue == null) {
      eventQueue = new EventQueue();
    }
    Vector localVector = (Vector)paramVector.clone();
    eventQueue.enqueue(paramEventObject, localVector);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\EventSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */