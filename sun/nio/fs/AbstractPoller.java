package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

abstract class AbstractPoller
  implements Runnable
{
  private final LinkedList<Request> requestList = new LinkedList();
  private boolean shutdown = false;
  
  protected AbstractPoller() {}
  
  public void start()
  {
    final AbstractPoller localAbstractPoller = this;
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Thread localThread = new Thread(localAbstractPoller);
        localThread.setDaemon(true);
        localThread.start();
        return null;
      }
    });
  }
  
  abstract void wakeup()
    throws IOException;
  
  abstract Object implRegister(Path paramPath, Set<? extends WatchEvent.Kind<?>> paramSet, WatchEvent.Modifier... paramVarArgs);
  
  abstract void implCancelKey(WatchKey paramWatchKey);
  
  abstract void implCloseAll();
  
  final WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs)
    throws IOException
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    HashSet localHashSet = new HashSet(paramArrayOfKind.length);
    for (WatchEvent.Kind<?> localKind : paramArrayOfKind) {
      if ((localKind == StandardWatchEventKinds.ENTRY_CREATE) || (localKind == StandardWatchEventKinds.ENTRY_MODIFY) || (localKind == StandardWatchEventKinds.ENTRY_DELETE))
      {
        localHashSet.add(localKind);
      }
      else if (localKind != StandardWatchEventKinds.OVERFLOW)
      {
        if (localKind == null) {
          throw new NullPointerException("An element in event set is 'null'");
        }
        throw new UnsupportedOperationException(localKind.name());
      }
    }
    if (localHashSet.isEmpty()) {
      throw new IllegalArgumentException("No events to register");
    }
    return (WatchKey)invoke(RequestType.REGISTER, new Object[] { paramPath, localHashSet, paramVarArgs });
  }
  
  final void cancel(WatchKey paramWatchKey)
  {
    try
    {
      invoke(RequestType.CANCEL, new Object[] { paramWatchKey });
    }
    catch (IOException localIOException)
    {
      throw new AssertionError(localIOException.getMessage());
    }
  }
  
  final void close()
    throws IOException
  {
    invoke(RequestType.CLOSE, new Object[0]);
  }
  
  private Object invoke(RequestType paramRequestType, Object... paramVarArgs)
    throws IOException
  {
    Request localRequest = new Request(paramRequestType, paramVarArgs);
    synchronized (requestList)
    {
      if (shutdown) {
        throw new ClosedWatchServiceException();
      }
      requestList.add(localRequest);
    }
    wakeup();
    ??? = localRequest.awaitResult();
    if ((??? instanceof RuntimeException)) {
      throw ((RuntimeException)???);
    }
    if ((??? instanceof IOException)) {
      throw ((IOException)???);
    }
    return ???;
  }
  
  boolean processRequests()
  {
    synchronized (requestList)
    {
      Request localRequest;
      while ((localRequest = (Request)requestList.poll()) != null)
      {
        if (shutdown) {
          localRequest.release(new ClosedWatchServiceException());
        }
        Object[] arrayOfObject;
        Object localObject1;
        switch (localRequest.type())
        {
        case REGISTER: 
          arrayOfObject = localRequest.parameters();
          localObject1 = (Path)arrayOfObject[0];
          Set localSet = (Set)arrayOfObject[1];
          WatchEvent.Modifier[] arrayOfModifier = (WatchEvent.Modifier[])arrayOfObject[2];
          localRequest.release(implRegister((Path)localObject1, localSet, arrayOfModifier));
          break;
        case CANCEL: 
          arrayOfObject = localRequest.parameters();
          localObject1 = (WatchKey)arrayOfObject[0];
          implCancelKey((WatchKey)localObject1);
          localRequest.release(null);
          break;
        case CLOSE: 
          implCloseAll();
          localRequest.release(null);
          shutdown = true;
          break;
        default: 
          localRequest.release(new IOException("request not recognized"));
        }
      }
    }
    return shutdown;
  }
  
  private static class Request
  {
    private final AbstractPoller.RequestType type;
    private final Object[] params;
    private boolean completed = false;
    private Object result = null;
    
    Request(AbstractPoller.RequestType paramRequestType, Object... paramVarArgs)
    {
      type = paramRequestType;
      params = paramVarArgs;
    }
    
    AbstractPoller.RequestType type()
    {
      return type;
    }
    
    Object[] parameters()
    {
      return params;
    }
    
    void release(Object paramObject)
    {
      synchronized (this)
      {
        completed = true;
        result = paramObject;
        notifyAll();
      }
    }
    
    Object awaitResult()
    {
      int i = 0;
      synchronized (this)
      {
        while (!completed) {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            i = 1;
          }
        }
        if (i != 0) {
          Thread.currentThread().interrupt();
        }
        return result;
      }
    }
  }
  
  private static enum RequestType
  {
    REGISTER,  CANCEL,  CLOSE;
    
    private RequestType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\AbstractPoller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */