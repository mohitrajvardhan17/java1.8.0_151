package com.sun.java.swing;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import sun.awt.AppContext;
import sun.awt.EventQueueDelegate;
import sun.awt.EventQueueDelegate.Delegate;
import sun.awt.SunToolkit;

public class SwingUtilities3
{
  private static final Object DELEGATE_REPAINT_MANAGER_KEY = new StringBuilder("DelegateRepaintManagerKey");
  private static final Map<Container, Boolean> vsyncedMap = Collections.synchronizedMap(new WeakHashMap());
  
  public SwingUtilities3() {}
  
  public static void setDelegateRepaintManager(JComponent paramJComponent, RepaintManager paramRepaintManager)
  {
    AppContext.getAppContext().put(DELEGATE_REPAINT_MANAGER_KEY, Boolean.TRUE);
    paramJComponent.putClientProperty(DELEGATE_REPAINT_MANAGER_KEY, paramRepaintManager);
  }
  
  public static void setVsyncRequested(Container paramContainer, boolean paramBoolean)
  {
    assert (((paramContainer instanceof Applet)) || ((paramContainer instanceof Window)));
    if (paramBoolean) {
      vsyncedMap.put(paramContainer, Boolean.TRUE);
    } else {
      vsyncedMap.remove(paramContainer);
    }
  }
  
  public static boolean isVsyncRequested(Container paramContainer)
  {
    assert (((paramContainer instanceof Applet)) || ((paramContainer instanceof Window)));
    return Boolean.TRUE == vsyncedMap.get(paramContainer);
  }
  
  public static RepaintManager getDelegateRepaintManager(Component paramComponent)
  {
    RepaintManager localRepaintManager = null;
    if (Boolean.TRUE == SunToolkit.targetToAppContext(paramComponent).get(DELEGATE_REPAINT_MANAGER_KEY)) {
      while ((localRepaintManager == null) && (paramComponent != null))
      {
        while ((paramComponent != null) && (!(paramComponent instanceof JComponent))) {
          paramComponent = paramComponent.getParent();
        }
        if (paramComponent != null)
        {
          localRepaintManager = (RepaintManager)((JComponent)paramComponent).getClientProperty(DELEGATE_REPAINT_MANAGER_KEY);
          paramComponent = paramComponent.getParent();
        }
      }
    }
    return localRepaintManager;
  }
  
  public static void setEventQueueDelegate(Map<String, Map<String, Object>> paramMap)
  {
    EventQueueDelegate.setDelegate(new EventQueueDelegateFromMap(paramMap));
  }
  
  private static class EventQueueDelegateFromMap
    implements EventQueueDelegate.Delegate
  {
    private final AWTEvent[] afterDispatchEventArgument;
    private final Object[] afterDispatchHandleArgument;
    private final Callable<Void> afterDispatchCallable;
    private final AWTEvent[] beforeDispatchEventArgument;
    private final Callable<Object> beforeDispatchCallable;
    private final EventQueue[] getNextEventEventQueueArgument;
    private final Callable<AWTEvent> getNextEventCallable;
    
    public EventQueueDelegateFromMap(Map<String, Map<String, Object>> paramMap)
    {
      Map localMap = (Map)paramMap.get("afterDispatch");
      afterDispatchEventArgument = ((AWTEvent[])localMap.get("event"));
      afterDispatchHandleArgument = ((Object[])localMap.get("handle"));
      afterDispatchCallable = ((Callable)localMap.get("method"));
      localMap = (Map)paramMap.get("beforeDispatch");
      beforeDispatchEventArgument = ((AWTEvent[])localMap.get("event"));
      beforeDispatchCallable = ((Callable)localMap.get("method"));
      localMap = (Map)paramMap.get("getNextEvent");
      getNextEventEventQueueArgument = ((EventQueue[])localMap.get("eventQueue"));
      getNextEventCallable = ((Callable)localMap.get("method"));
    }
    
    public void afterDispatch(AWTEvent paramAWTEvent, Object paramObject)
      throws InterruptedException
    {
      afterDispatchEventArgument[0] = paramAWTEvent;
      afterDispatchHandleArgument[0] = paramObject;
      try
      {
        afterDispatchCallable.call();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw localInterruptedException;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw localRuntimeException;
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
    }
    
    public Object beforeDispatch(AWTEvent paramAWTEvent)
      throws InterruptedException
    {
      beforeDispatchEventArgument[0] = paramAWTEvent;
      try
      {
        return beforeDispatchCallable.call();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw localInterruptedException;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw localRuntimeException;
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
    }
    
    public AWTEvent getNextEvent(EventQueue paramEventQueue)
      throws InterruptedException
    {
      getNextEventEventQueueArgument[0] = paramEventQueue;
      try
      {
        return (AWTEvent)getNextEventCallable.call();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw localInterruptedException;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw localRuntimeException;
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\SwingUtilities3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */