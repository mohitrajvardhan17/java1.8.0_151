package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import sun.awt.AppContext;
import sun.swing.AccumulativeRunnable;

public abstract class SwingWorker<T, V>
  implements RunnableFuture<T>
{
  private static final int MAX_WORKER_THREADS = 10;
  private volatile int progress;
  private volatile StateValue state;
  private final FutureTask<T> future;
  private final PropertyChangeSupport propertyChangeSupport;
  private AccumulativeRunnable<V> doProcess;
  private AccumulativeRunnable<Integer> doNotifyProgressChange;
  private final AccumulativeRunnable<Runnable> doSubmit = getDoSubmit();
  private static final Object DO_SUBMIT_KEY = new StringBuilder("doSubmit");
  
  public SwingWorker()
  {
    Callable local1 = new Callable()
    {
      public T call()
        throws Exception
      {
        SwingWorker.this.setState(SwingWorker.StateValue.STARTED);
        return (T)doInBackground();
      }
    };
    future = new FutureTask(local1)
    {
      protected void done()
      {
        SwingWorker.this.doneEDT();
        SwingWorker.this.setState(SwingWorker.StateValue.DONE);
      }
    };
    state = StateValue.PENDING;
    propertyChangeSupport = new SwingWorkerPropertyChangeSupport(this);
    doProcess = null;
    doNotifyProgressChange = null;
  }
  
  protected abstract T doInBackground()
    throws Exception;
  
  public final void run()
  {
    future.run();
  }
  
  @SafeVarargs
  protected final void publish(V... paramVarArgs)
  {
    synchronized (this)
    {
      if (doProcess == null) {
        doProcess = new AccumulativeRunnable()
        {
          public void run(List<V> paramAnonymousList)
          {
            process(paramAnonymousList);
          }
          
          protected void submit()
          {
            doSubmit.add(new Runnable[] { this });
          }
        };
      }
    }
    doProcess.add(paramVarArgs);
  }
  
  protected void process(List<V> paramList) {}
  
  protected void done() {}
  
  protected final void setProgress(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 100)) {
      throw new IllegalArgumentException("the value should be from 0 to 100");
    }
    if (progress == paramInt) {
      return;
    }
    int i = progress;
    progress = paramInt;
    if (!getPropertyChangeSupport().hasListeners("progress")) {
      return;
    }
    synchronized (this)
    {
      if (doNotifyProgressChange == null) {
        doNotifyProgressChange = new AccumulativeRunnable()
        {
          public void run(List<Integer> paramAnonymousList)
          {
            firePropertyChange("progress", paramAnonymousList.get(0), paramAnonymousList.get(paramAnonymousList.size() - 1));
          }
          
          protected void submit()
          {
            doSubmit.add(new Runnable[] { this });
          }
        };
      }
    }
    doNotifyProgressChange.add(new Integer[] { Integer.valueOf(i), Integer.valueOf(paramInt) });
  }
  
  public final int getProgress()
  {
    return progress;
  }
  
  public final void execute()
  {
    getWorkersExecutorService().execute(this);
  }
  
  public final boolean cancel(boolean paramBoolean)
  {
    return future.cancel(paramBoolean);
  }
  
  public final boolean isCancelled()
  {
    return future.isCancelled();
  }
  
  public final boolean isDone()
  {
    return future.isDone();
  }
  
  public final T get()
    throws InterruptedException, ExecutionException
  {
    return (T)future.get();
  }
  
  public final T get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return (T)future.get(paramLong, paramTimeUnit);
  }
  
  public final void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    getPropertyChangeSupport().addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public final void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    getPropertyChangeSupport().removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public final void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    getPropertyChangeSupport().firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public final PropertyChangeSupport getPropertyChangeSupport()
  {
    return propertyChangeSupport;
  }
  
  public final StateValue getState()
  {
    if (isDone()) {
      return StateValue.DONE;
    }
    return state;
  }
  
  private void setState(StateValue paramStateValue)
  {
    StateValue localStateValue = state;
    state = paramStateValue;
    firePropertyChange("state", localStateValue, paramStateValue);
  }
  
  private void doneEDT()
  {
    Runnable local5 = new Runnable()
    {
      public void run()
      {
        done();
      }
    };
    if (SwingUtilities.isEventDispatchThread()) {
      local5.run();
    } else {
      doSubmit.add(new Runnable[] { local5 });
    }
  }
  
  private static synchronized ExecutorService getWorkersExecutorService()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject1 = (ExecutorService)localAppContext.get(SwingWorker.class);
    if (localObject1 == null)
    {
      ThreadFactory local6 = new ThreadFactory()
      {
        final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        
        public Thread newThread(Runnable paramAnonymousRunnable)
        {
          Thread localThread = defaultFactory.newThread(paramAnonymousRunnable);
          localThread.setName("SwingWorker-" + localThread.getName());
          localThread.setDaemon(true);
          return localThread;
        }
      };
      localObject1 = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MINUTES, new LinkedBlockingQueue(), local6);
      localAppContext.put(SwingWorker.class, localObject1);
      Object localObject2 = localObject1;
      localAppContext.addPropertyChangeListener("disposed", new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
        {
          boolean bool = ((Boolean)paramAnonymousPropertyChangeEvent.getNewValue()).booleanValue();
          if (bool)
          {
            WeakReference localWeakReference = new WeakReference(val$es);
            final ExecutorService localExecutorService = (ExecutorService)localWeakReference.get();
            if (localExecutorService != null) {
              AccessController.doPrivileged(new PrivilegedAction()
              {
                public Void run()
                {
                  localExecutorService.shutdown();
                  return null;
                }
              });
            }
          }
        }
      });
    }
    return (ExecutorService)localObject1;
  }
  
  private static AccumulativeRunnable<Runnable> getDoSubmit()
  {
    synchronized (DO_SUBMIT_KEY)
    {
      AppContext localAppContext = AppContext.getAppContext();
      Object localObject1 = localAppContext.get(DO_SUBMIT_KEY);
      if (localObject1 == null)
      {
        localObject1 = new DoSubmitAccumulativeRunnable(null);
        localAppContext.put(DO_SUBMIT_KEY, localObject1);
      }
      return (AccumulativeRunnable)localObject1;
    }
  }
  
  private static class DoSubmitAccumulativeRunnable
    extends AccumulativeRunnable<Runnable>
    implements ActionListener
  {
    private static final int DELAY = 33;
    
    private DoSubmitAccumulativeRunnable() {}
    
    protected void run(List<Runnable> paramList)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Runnable localRunnable = (Runnable)localIterator.next();
        localRunnable.run();
      }
    }
    
    protected void submit()
    {
      Timer localTimer = new Timer(33, this);
      localTimer.setRepeats(false);
      localTimer.start();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      run();
    }
  }
  
  public static enum StateValue
  {
    PENDING,  STARTED,  DONE;
    
    private StateValue() {}
  }
  
  private class SwingWorkerPropertyChangeSupport
    extends PropertyChangeSupport
  {
    SwingWorkerPropertyChangeSupport(Object paramObject)
    {
      super();
    }
    
    public void firePropertyChange(final PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (SwingUtilities.isEventDispatchThread()) {
        super.firePropertyChange(paramPropertyChangeEvent);
      } else {
        doSubmit.add(new Runnable[] { new Runnable()
        {
          public void run()
          {
            firePropertyChange(paramPropertyChangeEvent);
          }
        } });
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SwingWorker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */