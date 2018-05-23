package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.event.EventListenerList;

public class Timer
  implements Serializable
{
  protected EventListenerList listenerList = new EventListenerList();
  private final transient AtomicBoolean notify = new AtomicBoolean(false);
  private volatile int initialDelay;
  private volatile int delay;
  private volatile boolean repeats = true;
  private volatile boolean coalesce = true;
  private final transient Runnable doPostEvent;
  private static volatile boolean logTimers;
  private final transient Lock lock = new ReentrantLock();
  transient TimerQueue.DelayedTimer delayedTimer = null;
  private volatile String actionCommand;
  private volatile transient AccessControlContext acc = AccessController.getContext();
  
  public Timer(int paramInt, ActionListener paramActionListener)
  {
    delay = paramInt;
    initialDelay = paramInt;
    doPostEvent = new DoPostEvent();
    if (paramActionListener != null) {
      addActionListener(paramActionListener);
    }
  }
  
  final AccessControlContext getAccessControlContext()
  {
    if (acc == null) {
      throw new SecurityException("Timer is missing AccessControlContext");
    }
    return acc;
  }
  
  public void addActionListener(ActionListener paramActionListener)
  {
    listenerList.add(ActionListener.class, paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener)
  {
    listenerList.remove(ActionListener.class, paramActionListener);
  }
  
  public ActionListener[] getActionListeners()
  {
    return (ActionListener[])listenerList.getListeners(ActionListener.class);
  }
  
  protected void fireActionPerformed(ActionEvent paramActionEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class) {
        ((ActionListener)arrayOfObject[(i + 1)]).actionPerformed(paramActionEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  private TimerQueue timerQueue()
  {
    return TimerQueue.sharedInstance();
  }
  
  public static void setLogTimers(boolean paramBoolean)
  {
    logTimers = paramBoolean;
  }
  
  public static boolean getLogTimers()
  {
    return logTimers;
  }
  
  public void setDelay(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Invalid delay: " + paramInt);
    }
    delay = paramInt;
  }
  
  public int getDelay()
  {
    return delay;
  }
  
  public void setInitialDelay(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Invalid initial delay: " + paramInt);
    }
    initialDelay = paramInt;
  }
  
  public int getInitialDelay()
  {
    return initialDelay;
  }
  
  public void setRepeats(boolean paramBoolean)
  {
    repeats = paramBoolean;
  }
  
  public boolean isRepeats()
  {
    return repeats;
  }
  
  public void setCoalesce(boolean paramBoolean)
  {
    boolean bool = coalesce;
    coalesce = paramBoolean;
    if ((!bool) && (coalesce)) {
      cancelEvent();
    }
  }
  
  public boolean isCoalesce()
  {
    return coalesce;
  }
  
  public void setActionCommand(String paramString)
  {
    actionCommand = paramString;
  }
  
  public String getActionCommand()
  {
    return actionCommand;
  }
  
  public void start()
  {
    timerQueue().addTimer(this, getInitialDelay());
  }
  
  public boolean isRunning()
  {
    return timerQueue().containsTimer(this);
  }
  
  /* Error */
  public void stop()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 234	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 248 1 0
    //   9: aload_0
    //   10: invokevirtual 228	javax/swing/Timer:cancelEvent	()V
    //   13: aload_0
    //   14: invokespecial 235	javax/swing/Timer:timerQueue	()Ljavax/swing/TimerQueue;
    //   17: aload_0
    //   18: invokevirtual 238	javax/swing/TimerQueue:removeTimer	(Ljavax/swing/Timer;)V
    //   21: aload_0
    //   22: invokevirtual 234	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   25: invokeinterface 249 1 0
    //   30: goto +15 -> 45
    //   33: astore_1
    //   34: aload_0
    //   35: invokevirtual 234	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   38: invokeinterface 249 1 0
    //   43: aload_1
    //   44: athrow
    //   45: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	46	0	this	Timer
    //   33	11	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	33	finally
  }
  
  /* Error */
  public void restart()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 234	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 248 1 0
    //   9: aload_0
    //   10: invokevirtual 230	javax/swing/Timer:stop	()V
    //   13: aload_0
    //   14: invokevirtual 229	javax/swing/Timer:start	()V
    //   17: aload_0
    //   18: invokevirtual 234	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   21: invokeinterface 249 1 0
    //   26: goto +15 -> 41
    //   29: astore_1
    //   30: aload_0
    //   31: invokevirtual 234	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   34: invokeinterface 249 1 0
    //   39: aload_1
    //   40: athrow
    //   41: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	Timer
    //   29	11	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	17	29	finally
  }
  
  void cancelEvent()
  {
    notify.set(false);
  }
  
  void post()
  {
    if ((notify.compareAndSet(false, true)) || (!coalesce)) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          SwingUtilities.invokeLater(doPostEvent);
          return null;
        }
      }, getAccessControlContext());
    }
  }
  
  Lock getLock()
  {
    return lock;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    acc = AccessController.getContext();
    paramObjectInputStream.defaultReadObject();
  }
  
  private Object readResolve()
  {
    Timer localTimer = new Timer(getDelay(), null);
    listenerList = listenerList;
    initialDelay = initialDelay;
    delay = delay;
    repeats = repeats;
    coalesce = coalesce;
    actionCommand = actionCommand;
    return localTimer;
  }
  
  class DoPostEvent
    implements Runnable
  {
    DoPostEvent() {}
    
    public void run()
    {
      if (Timer.logTimers) {
        System.out.println("Timer ringing: " + Timer.this);
      }
      if (notify.get())
      {
        fireActionPerformed(new ActionEvent(Timer.this, 0, getActionCommand(), System.currentTimeMillis(), 0));
        if (coalesce) {
          cancelEvent();
        }
      }
    }
    
    Timer getTimer()
    {
      return Timer.this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */