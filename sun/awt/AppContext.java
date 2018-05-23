package sun.awt;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import sun.misc.JavaAWTAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public final class AppContext
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.AppContext");
  public static final Object EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
  public static final Object EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
  public static final Object EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
  private static final Map<ThreadGroup, AppContext> threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap());
  private static volatile AppContext mainAppContext = null;
  private static final Object getAppContextLock = new GetAppContextLock(null);
  private final Map<Object, Object> table = new HashMap();
  private final ThreadGroup threadGroup;
  private PropertyChangeSupport changeSupport = null;
  public static final String DISPOSED_PROPERTY_NAME = "disposed";
  public static final String GUI_DISPOSED = "guidisposed";
  private volatile State state = State.VALID;
  private static final AtomicInteger numAppContexts = new AtomicInteger(0);
  private final ClassLoader contextClassLoader;
  private static final ThreadLocal<AppContext> threadAppContext = new ThreadLocal();
  private long DISPOSAL_TIMEOUT = 5000L;
  private long THREAD_INTERRUPT_TIMEOUT = 1000L;
  private MostRecentKeyValue mostRecentKeyValue = null;
  private MostRecentKeyValue shadowMostRecentKeyValue = null;
  
  /* Error */
  public static Set<AppContext> getAppContexts()
  {
    // Byte code:
    //   0: getstatic 383	sun/awt/AppContext:threadGroup2appContext	Ljava/util/Map;
    //   3: dup
    //   4: astore_0
    //   5: monitorenter
    //   6: new 206	java/util/HashSet
    //   9: dup
    //   10: getstatic 383	sun/awt/AppContext:threadGroup2appContext	Ljava/util/Map;
    //   13: invokeinterface 468 1 0
    //   18: invokespecial 435	java/util/HashSet:<init>	(Ljava/util/Collection;)V
    //   21: aload_0
    //   22: monitorexit
    //   23: areturn
    //   24: astore_1
    //   25: aload_0
    //   26: monitorexit
    //   27: aload_1
    //   28: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   4	22	0	Ljava/lang/Object;	Object
    //   24	4	1	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	23	24	finally
    //   24	27	24	finally
  }
  
  public boolean isDisposed()
  {
    return state == State.DISPOSED;
  }
  
  AppContext(ThreadGroup paramThreadGroup)
  {
    numAppContexts.incrementAndGet();
    threadGroup = paramThreadGroup;
    threadGroup2appContext.put(paramThreadGroup, this);
    contextClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    }));
    ReentrantLock localReentrantLock = new ReentrantLock();
    put(EVENT_QUEUE_LOCK_KEY, localReentrantLock);
    Condition localCondition = localReentrantLock.newCondition();
    put(EVENT_QUEUE_COND_KEY, localCondition);
  }
  
  private static final void initMainAppContext()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Object localObject = Thread.currentThread().getThreadGroup();
        for (ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent(); localThreadGroup != null; localThreadGroup = ((ThreadGroup)localObject).getParent()) {
          localObject = localThreadGroup;
        }
        AppContext.access$102(SunToolkit.createNewAppContext((ThreadGroup)localObject));
        return null;
      }
    });
  }
  
  public static final AppContext getAppContext()
  {
    if ((numAppContexts.get() == 1) && (mainAppContext != null)) {
      return mainAppContext;
    }
    AppContext localAppContext = (AppContext)threadAppContext.get();
    if (null == localAppContext) {
      localAppContext = (AppContext)AccessController.doPrivileged(new PrivilegedAction()
      {
        public AppContext run()
        {
          ThreadGroup localThreadGroup1 = Thread.currentThread().getThreadGroup();
          ThreadGroup localThreadGroup2 = localThreadGroup1;
          synchronized (AppContext.getAppContextLock)
          {
            if (AppContext.numAppContexts.get() == 0) {
              if ((System.getProperty("javaplugin.version") == null) && (System.getProperty("javawebstart.version") == null)) {
                AppContext.access$400();
              } else if ((System.getProperty("javafx.version") != null) && (localThreadGroup2.getParent() != null)) {
                SunToolkit.createNewAppContext();
              }
            }
          }
          for (??? = (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup2); ??? == null; ??? = (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup2))
          {
            localThreadGroup2 = localThreadGroup2.getParent();
            if (localThreadGroup2 == null)
            {
              localObject2 = System.getSecurityManager();
              if (localObject2 != null)
              {
                ThreadGroup localThreadGroup3 = ((SecurityManager)localObject2).getThreadGroup();
                if (localThreadGroup3 != null) {
                  return (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup3);
                }
              }
              return null;
            }
          }
          for (Object localObject2 = localThreadGroup1; localObject2 != localThreadGroup2; localObject2 = ((ThreadGroup)localObject2).getParent()) {
            AppContext.threadGroup2appContext.put(localObject2, ???);
          }
          AppContext.threadAppContext.set(???);
          return (AppContext)???;
        }
      });
    }
    return localAppContext;
  }
  
  public static final boolean isMainContext(AppContext paramAppContext)
  {
    return (paramAppContext != null) && (paramAppContext == mainAppContext);
  }
  
  private static final AppContext getExecutionAppContext()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && ((localSecurityManager instanceof AWTSecurityManager)))
    {
      AWTSecurityManager localAWTSecurityManager = (AWTSecurityManager)localSecurityManager;
      AppContext localAppContext = localAWTSecurityManager.getAppContext();
      return localAppContext;
    }
    return null;
  }
  
  public void dispose()
    throws IllegalThreadStateException
  {
    if (threadGroup.parentOf(Thread.currentThread().getThreadGroup())) {
      throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed.");
    }
    synchronized (this)
    {
      if (state != State.VALID) {
        return;
      }
      state = State.BEING_DISPOSED;
    }
    ??? = changeSupport;
    if (??? != null) {
      ((PropertyChangeSupport)???).firePropertyChange("disposed", false, true);
    }
    final Object localObject2 = new Object();
    Object localObject3 = new Runnable()
    {
      public void run()
      {
        Window[] arrayOfWindow1 = Window.getOwnerlessWindows();
        for (Window localWindow : arrayOfWindow1) {
          try
          {
            localWindow.dispose();
          }
          catch (Throwable localThrowable)
          {
            AppContext.log.finer("exception occurred while disposing app context", localThrowable);
          }
        }
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            if ((!GraphicsEnvironment.isHeadless()) && (SystemTray.isSupported()))
            {
              SystemTray localSystemTray = SystemTray.getSystemTray();
              TrayIcon[] arrayOfTrayIcon1 = localSystemTray.getTrayIcons();
              for (TrayIcon localTrayIcon : arrayOfTrayIcon1) {
                localSystemTray.remove(localTrayIcon);
              }
            }
            return null;
          }
        });
        if (Ljava/lang/Object; != null) {
          Ljava/lang/Object;.firePropertyChange("guidisposed", false, true);
        }
        synchronized (localObject2)
        {
          localObject2.notifyAll();
        }
      }
    };
    synchronized (localObject2)
    {
      SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), (Runnable)localObject3));
      try
      {
        localObject2.wait(DISPOSAL_TIMEOUT);
      }
      catch (InterruptedException localInterruptedException1) {}
    }
    localObject3 = new Runnable()
    {
      public void run()
      {
        synchronized (localObject2)
        {
          localObject2.notifyAll();
        }
      }
    };
    synchronized (localObject2)
    {
      SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), (Runnable)localObject3));
      try
      {
        localObject2.wait(DISPOSAL_TIMEOUT);
      }
      catch (InterruptedException localInterruptedException2) {}
    }
    synchronized (this)
    {
      state = State.DISPOSED;
    }
    threadGroup.interrupt();
    long l1 = System.currentTimeMillis();
    long l2 = l1 + THREAD_INTERRUPT_TIMEOUT;
    while ((threadGroup.activeCount() > 0) && (System.currentTimeMillis() < l2)) {
      try
      {
        Thread.sleep(10L);
      }
      catch (InterruptedException localInterruptedException3) {}
    }
    threadGroup.stop();
    l1 = System.currentTimeMillis();
    l2 = l1 + THREAD_INTERRUPT_TIMEOUT;
    while ((threadGroup.activeCount() > 0) && (System.currentTimeMillis() < l2)) {
      try
      {
        Thread.sleep(10L);
      }
      catch (InterruptedException localInterruptedException4) {}
    }
    int i = threadGroup.activeGroupCount();
    if (i > 0)
    {
      ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[i];
      i = threadGroup.enumerate(arrayOfThreadGroup);
      for (int j = 0; j < i; j++) {
        threadGroup2appContext.remove(arrayOfThreadGroup[j]);
      }
    }
    threadGroup2appContext.remove(threadGroup);
    threadAppContext.set(null);
    try
    {
      threadGroup.destroy();
    }
    catch (IllegalThreadStateException localIllegalThreadStateException) {}
    synchronized (table)
    {
      table.clear();
    }
    numAppContexts.decrementAndGet();
    mostRecentKeyValue = null;
  }
  
  static void stopEventDispatchThreads()
  {
    Iterator localIterator = getAppContexts().iterator();
    while (localIterator.hasNext())
    {
      AppContext localAppContext = (AppContext)localIterator.next();
      if (!localAppContext.isDisposed())
      {
        PostShutdownEventRunnable localPostShutdownEventRunnable = new PostShutdownEventRunnable(localAppContext);
        if (localAppContext != getAppContext())
        {
          CreateThreadAction localCreateThreadAction = new CreateThreadAction(localAppContext, localPostShutdownEventRunnable);
          Thread localThread = (Thread)AccessController.doPrivileged(localCreateThreadAction);
          localThread.start();
        }
        else
        {
          localPostShutdownEventRunnable.run();
        }
      }
    }
  }
  
  public Object get(Object paramObject)
  {
    synchronized (table)
    {
      MostRecentKeyValue localMostRecentKeyValue1 = mostRecentKeyValue;
      if ((localMostRecentKeyValue1 != null) && (key == paramObject)) {
        return value;
      }
      Object localObject1 = table.get(paramObject);
      if (mostRecentKeyValue == null)
      {
        mostRecentKeyValue = new MostRecentKeyValue(paramObject, localObject1);
        shadowMostRecentKeyValue = new MostRecentKeyValue(paramObject, localObject1);
      }
      else
      {
        MostRecentKeyValue localMostRecentKeyValue2 = mostRecentKeyValue;
        shadowMostRecentKeyValue.setPair(paramObject, localObject1);
        mostRecentKeyValue = shadowMostRecentKeyValue;
        shadowMostRecentKeyValue = localMostRecentKeyValue2;
      }
      return localObject1;
    }
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    synchronized (table)
    {
      MostRecentKeyValue localMostRecentKeyValue = mostRecentKeyValue;
      if ((localMostRecentKeyValue != null) && (key == paramObject1)) {
        value = paramObject2;
      }
      return table.put(paramObject1, paramObject2);
    }
  }
  
  public Object remove(Object paramObject)
  {
    synchronized (table)
    {
      MostRecentKeyValue localMostRecentKeyValue = mostRecentKeyValue;
      if ((localMostRecentKeyValue != null) && (key == paramObject)) {
        value = null;
      }
      return table.remove(paramObject);
    }
  }
  
  public ThreadGroup getThreadGroup()
  {
    return threadGroup;
  }
  
  public ClassLoader getContextClassLoader()
  {
    return contextClassLoader;
  }
  
  public String toString()
  {
    return getClass().getName() + "[threadGroup=" + threadGroup.getName() + "]";
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners()
  {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }
  
  public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener == null) {
      return;
    }
    if (changeSupport == null) {
      changeSupport = new PropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if ((paramPropertyChangeListener == null) || (changeSupport == null)) {
      return;
    }
    changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners(paramString);
  }
  
  public static <T> T getSoftReferenceValue(Object paramObject, Supplier<T> paramSupplier)
  {
    AppContext localAppContext = getAppContext();
    SoftReference localSoftReference = (SoftReference)localAppContext.get(paramObject);
    if (localSoftReference != null)
    {
      localObject = localSoftReference.get();
      if (localObject != null) {
        return (T)localObject;
      }
    }
    Object localObject = paramSupplier.get();
    localSoftReference = new SoftReference(localObject);
    localAppContext.put(paramObject, localSoftReference);
    return (T)localObject;
  }
  
  static
  {
    SharedSecrets.setJavaAWTAccess(new JavaAWTAccess()
    {
      private boolean hasRootThreadGroup(final AppContext paramAnonymousAppContext)
      {
        ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Boolean run()
          {
            return Boolean.valueOf(paramAnonymousAppContextthreadGroup.getParent() == null);
          }
        })).booleanValue();
      }
      
      public Object getAppletContext()
      {
        if (AppContext.numAppContexts.get() == 0) {
          return null;
        }
        AppContext localAppContext = AppContext.access$900();
        if (AppContext.numAppContexts.get() > 0) {
          localAppContext = localAppContext != null ? localAppContext : AppContext.getAppContext();
        }
        int i = (localAppContext == null) || (AppContext.mainAppContext == localAppContext) || ((AppContext.mainAppContext == null) && (hasRootThreadGroup(localAppContext))) ? 1 : 0;
        return i != 0 ? null : localAppContext;
      }
    });
  }
  
  static final class CreateThreadAction
    implements PrivilegedAction<Thread>
  {
    private final AppContext appContext;
    private final Runnable runnable;
    
    public CreateThreadAction(AppContext paramAppContext, Runnable paramRunnable)
    {
      appContext = paramAppContext;
      runnable = paramRunnable;
    }
    
    public Thread run()
    {
      Thread localThread = new Thread(appContext.getThreadGroup(), runnable);
      localThread.setContextClassLoader(appContext.getContextClassLoader());
      localThread.setPriority(6);
      localThread.setDaemon(true);
      return localThread;
    }
  }
  
  private static class GetAppContextLock
  {
    private GetAppContextLock() {}
  }
  
  static final class PostShutdownEventRunnable
    implements Runnable
  {
    private final AppContext appContext;
    
    public PostShutdownEventRunnable(AppContext paramAppContext)
    {
      appContext = paramAppContext;
    }
    
    public void run()
    {
      EventQueue localEventQueue = (EventQueue)appContext.get(AppContext.EVENT_QUEUE_KEY);
      if (localEventQueue != null) {
        localEventQueue.postEvent(AWTAutoShutdown.getShutdownEvent());
      }
    }
  }
  
  private static enum State
  {
    VALID,  BEING_DISPOSED,  DISPOSED;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\AppContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */