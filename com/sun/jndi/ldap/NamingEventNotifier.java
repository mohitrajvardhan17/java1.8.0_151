package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.io.IOException;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.InterruptedNamingException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventContext;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.LdapName;

final class NamingEventNotifier
  implements Runnable
{
  private static final boolean debug = false;
  private Vector<NamingListener> namingListeners;
  private Thread worker;
  private LdapCtx context;
  private EventContext eventSrc;
  private EventSupport support;
  private NamingEnumeration<SearchResult> results;
  NotifierArgs info;
  
  NamingEventNotifier(EventSupport paramEventSupport, LdapCtx paramLdapCtx, NotifierArgs paramNotifierArgs, NamingListener paramNamingListener)
    throws NamingException
  {
    info = paramNotifierArgs;
    support = paramEventSupport;
    PersistentSearchControl localPersistentSearchControl;
    try
    {
      localPersistentSearchControl = new PersistentSearchControl(mask, true, true, true);
    }
    catch (IOException localIOException)
    {
      NamingException localNamingException = new NamingException("Problem creating persistent search control");
      localNamingException.setRootCause(localIOException);
      throw localNamingException;
    }
    context = ((LdapCtx)paramLdapCtx.newInstance(new Control[] { localPersistentSearchControl }));
    eventSrc = paramLdapCtx;
    namingListeners = new Vector();
    namingListeners.addElement(paramNamingListener);
    worker = Obj.helper.createThread(this);
    worker.setDaemon(true);
    worker.start();
  }
  
  void addNamingListener(NamingListener paramNamingListener)
  {
    namingListeners.addElement(paramNamingListener);
  }
  
  void removeNamingListener(NamingListener paramNamingListener)
  {
    namingListeners.removeElement(paramNamingListener);
  }
  
  boolean hasNamingListeners()
  {
    return namingListeners.size() > 0;
  }
  
  public void run()
  {
    try
    {
      Continuation localContinuation = new Continuation();
      localContinuation.setError(this, info.name);
      Name localName = (info.name == null) || (info.name.equals("")) ? new CompositeName() : new CompositeName().add(info.name);
      results = context.searchAux(localName, info.filter, info.controls, true, false, localContinuation);
      ((LdapSearchEnumeration)results).setStartName(context.currentParsedDN);
      while (results.hasMore())
      {
        SearchResult localSearchResult = (SearchResult)results.next();
        Object localObject1 = (localSearchResult instanceof HasControls) ? ((HasControls)localSearchResult).getControls() : null;
        if (localObject1 != null)
        {
          int i = 0;
          if ((i < localObject1.length) && ((localObject1[i] instanceof EntryChangeResponseControl)))
          {
            EntryChangeResponseControl localEntryChangeResponseControl = (EntryChangeResponseControl)localObject1[i];
            long l = localEntryChangeResponseControl.getChangeNumber();
            switch (localEntryChangeResponseControl.getChangeType())
            {
            case 1: 
              fireObjectAdded(localSearchResult, l);
              break;
            case 2: 
              fireObjectRemoved(localSearchResult, l);
              break;
            case 4: 
              fireObjectChanged(localSearchResult, l);
              break;
            case 8: 
              fireObjectRenamed(localSearchResult, localEntryChangeResponseControl.getPreviousDN(), l);
            }
          }
        }
      }
    }
    catch (InterruptedNamingException localInterruptedNamingException) {}catch (NamingException localNamingException)
    {
      fireNamingException(localNamingException);
      support.removeDeadNotifier(info);
    }
    finally
    {
      cleanup();
    }
  }
  
  private void cleanup()
  {
    try
    {
      if (results != null)
      {
        results.close();
        results = null;
      }
      if (context != null)
      {
        context.close();
        context = null;
      }
    }
    catch (NamingException localNamingException) {}
  }
  
  void stop()
  {
    if (worker != null)
    {
      worker.interrupt();
      worker = null;
    }
  }
  
  private void fireObjectAdded(Binding paramBinding, long paramLong)
  {
    if ((namingListeners == null) || (namingListeners.size() == 0)) {
      return;
    }
    NamingEvent localNamingEvent = new NamingEvent(eventSrc, 0, paramBinding, null, new Long(paramLong));
    support.queueEvent(localNamingEvent, namingListeners);
  }
  
  private void fireObjectRemoved(Binding paramBinding, long paramLong)
  {
    if ((namingListeners == null) || (namingListeners.size() == 0)) {
      return;
    }
    NamingEvent localNamingEvent = new NamingEvent(eventSrc, 1, null, paramBinding, new Long(paramLong));
    support.queueEvent(localNamingEvent, namingListeners);
  }
  
  private void fireObjectChanged(Binding paramBinding, long paramLong)
  {
    if ((namingListeners == null) || (namingListeners.size() == 0)) {
      return;
    }
    Binding localBinding = new Binding(paramBinding.getName(), null, paramBinding.isRelative());
    NamingEvent localNamingEvent = new NamingEvent(eventSrc, 3, paramBinding, localBinding, new Long(paramLong));
    support.queueEvent(localNamingEvent, namingListeners);
  }
  
  private void fireObjectRenamed(Binding paramBinding, String paramString, long paramLong)
  {
    if ((namingListeners == null) || (namingListeners.size() == 0)) {
      return;
    }
    Binding localBinding = null;
    try
    {
      LdapName localLdapName = new LdapName(paramString);
      if (localLdapName.startsWith(context.currentParsedDN))
      {
        String str = localLdapName.getSuffix(context.currentParsedDN.size()).toString();
        localBinding = new Binding(str, null);
      }
    }
    catch (NamingException localNamingException) {}
    if (localBinding == null) {
      localBinding = new Binding(paramString, null, false);
    }
    NamingEvent localNamingEvent = new NamingEvent(eventSrc, 2, paramBinding, localBinding, new Long(paramLong));
    support.queueEvent(localNamingEvent, namingListeners);
  }
  
  private void fireNamingException(NamingException paramNamingException)
  {
    if ((namingListeners == null) || (namingListeners.size() == 0)) {
      return;
    }
    NamingExceptionEvent localNamingExceptionEvent = new NamingExceptionEvent(eventSrc, paramNamingException);
    support.queueEvent(localNamingExceptionEvent, namingListeners);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\NamingEventNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */