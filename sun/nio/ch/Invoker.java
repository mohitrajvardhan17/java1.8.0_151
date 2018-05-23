package sun.nio.ch;

import java.nio.channels.AsynchronousChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import sun.misc.InnocuousThread;
import sun.security.action.GetIntegerAction;

class Invoker
{
  private static final int maxHandlerInvokeCount = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.nio.ch.maxCompletionHandlersOnStack", 16))).intValue();
  private static final ThreadLocal<GroupAndInvokeCount> myGroupAndInvokeCount = new ThreadLocal()
  {
    protected Invoker.GroupAndInvokeCount initialValue()
    {
      return null;
    }
  };
  
  private Invoker() {}
  
  static void bindToGroup(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
  {
    myGroupAndInvokeCount.set(new GroupAndInvokeCount(paramAsynchronousChannelGroupImpl));
  }
  
  static GroupAndInvokeCount getGroupAndInvokeCount()
  {
    return (GroupAndInvokeCount)myGroupAndInvokeCount.get();
  }
  
  static boolean isBoundToAnyGroup()
  {
    return myGroupAndInvokeCount.get() != null;
  }
  
  static boolean mayInvokeDirect(GroupAndInvokeCount paramGroupAndInvokeCount, AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
  {
    return (paramGroupAndInvokeCount != null) && (paramGroupAndInvokeCount.group() == paramAsynchronousChannelGroupImpl) && (paramGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount);
  }
  
  static <V, A> void invokeUnchecked(CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      paramCompletionHandler.completed(paramV, paramA);
    } else {
      paramCompletionHandler.failed(paramThrowable, paramA);
    }
    Thread.interrupted();
    if (System.getSecurityManager() != null)
    {
      Thread localThread = Thread.currentThread();
      if ((localThread instanceof InnocuousThread))
      {
        GroupAndInvokeCount localGroupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
        ((InnocuousThread)localThread).eraseThreadLocals();
        if (localGroupAndInvokeCount != null) {
          myGroupAndInvokeCount.set(localGroupAndInvokeCount);
        }
      }
    }
  }
  
  static <V, A> void invokeDirect(GroupAndInvokeCount paramGroupAndInvokeCount, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable)
  {
    paramGroupAndInvokeCount.incrementInvokeCount();
    invokeUnchecked(paramCompletionHandler, paramA, paramV, paramThrowable);
  }
  
  static <V, A> void invoke(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable)
  {
    int i = 0;
    int j = 0;
    GroupAndInvokeCount localGroupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
    if (localGroupAndInvokeCount != null)
    {
      if (localGroupAndInvokeCount.group() == ((Groupable)paramAsynchronousChannel).group()) {
        j = 1;
      }
      if ((j != 0) && (localGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount)) {
        i = 1;
      }
    }
    if (i != 0) {
      invokeDirect(localGroupAndInvokeCount, paramCompletionHandler, paramA, paramV, paramThrowable);
    } else {
      try
      {
        invokeIndirectly(paramAsynchronousChannel, paramCompletionHandler, paramA, paramV, paramThrowable);
      }
      catch (RejectedExecutionException localRejectedExecutionException)
      {
        if (j != 0) {
          invokeDirect(localGroupAndInvokeCount, paramCompletionHandler, paramA, paramV, paramThrowable);
        } else {
          throw new ShutdownChannelGroupException();
        }
      }
    }
  }
  
  static <V, A> void invokeIndirectly(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, final A paramA, final V paramV, final Throwable paramThrowable)
  {
    try
    {
      ((Groupable)paramAsynchronousChannel).group().executeOnPooledThread(new Runnable()
      {
        public void run()
        {
          Invoker.GroupAndInvokeCount localGroupAndInvokeCount = (Invoker.GroupAndInvokeCount)Invoker.myGroupAndInvokeCount.get();
          if (localGroupAndInvokeCount != null) {
            localGroupAndInvokeCount.setInvokeCount(1);
          }
          Invoker.invokeUnchecked(val$handler, paramA, paramV, paramThrowable);
        }
      });
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      throw new ShutdownChannelGroupException();
    }
  }
  
  static <V, A> void invokeIndirectly(CompletionHandler<V, ? super A> paramCompletionHandler, final A paramA, final V paramV, final Throwable paramThrowable, Executor paramExecutor)
  {
    try
    {
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          Invoker.invokeUnchecked(val$handler, paramA, paramV, paramThrowable);
        }
      });
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      throw new ShutdownChannelGroupException();
    }
  }
  
  static void invokeOnThreadInThreadPool(Groupable paramGroupable, Runnable paramRunnable)
  {
    GroupAndInvokeCount localGroupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
    AsynchronousChannelGroupImpl localAsynchronousChannelGroupImpl = paramGroupable.group();
    int i;
    if (localGroupAndInvokeCount == null) {
      i = 0;
    } else {
      i = group == localAsynchronousChannelGroupImpl ? 1 : 0;
    }
    try
    {
      if (i != 0) {
        paramRunnable.run();
      } else {
        localAsynchronousChannelGroupImpl.executeOnPooledThread(paramRunnable);
      }
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      throw new ShutdownChannelGroupException();
    }
  }
  
  static <V, A> void invokeUnchecked(PendingFuture<V, A> paramPendingFuture)
  {
    assert (paramPendingFuture.isDone());
    CompletionHandler localCompletionHandler = paramPendingFuture.handler();
    if (localCompletionHandler != null) {
      invokeUnchecked(localCompletionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception());
    }
  }
  
  static <V, A> void invoke(PendingFuture<V, A> paramPendingFuture)
  {
    assert (paramPendingFuture.isDone());
    CompletionHandler localCompletionHandler = paramPendingFuture.handler();
    if (localCompletionHandler != null) {
      invoke(paramPendingFuture.channel(), localCompletionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception());
    }
  }
  
  static <V, A> void invokeIndirectly(PendingFuture<V, A> paramPendingFuture)
  {
    assert (paramPendingFuture.isDone());
    CompletionHandler localCompletionHandler = paramPendingFuture.handler();
    if (localCompletionHandler != null) {
      invokeIndirectly(paramPendingFuture.channel(), localCompletionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception());
    }
  }
  
  static class GroupAndInvokeCount
  {
    private final AsynchronousChannelGroupImpl group;
    private int handlerInvokeCount;
    
    GroupAndInvokeCount(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
    {
      group = paramAsynchronousChannelGroupImpl;
    }
    
    AsynchronousChannelGroupImpl group()
    {
      return group;
    }
    
    int invokeCount()
    {
      return handlerInvokeCount;
    }
    
    void setInvokeCount(int paramInt)
    {
      handlerInvokeCount = paramInt;
    }
    
    void resetInvokeCount()
    {
      handlerInvokeCount = 0;
    }
    
    void incrementInvokeCount()
    {
      handlerInvokeCount += 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\Invoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */