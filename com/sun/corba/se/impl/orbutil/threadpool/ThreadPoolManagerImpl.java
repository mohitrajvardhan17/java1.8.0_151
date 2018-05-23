package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolChooser;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManagerImpl
  implements ThreadPoolManager
{
  private ThreadPool threadPool = new ThreadPoolImpl(threadGroup, "default-threadpool");
  private ThreadGroup threadGroup = getThreadGroup();
  private static final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
  private static AtomicInteger tgCount = new AtomicInteger();
  
  public ThreadPoolManagerImpl() {}
  
  private ThreadGroup getThreadGroup()
  {
    ThreadGroup localThreadGroup;
    try
    {
      localThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ThreadGroup run()
        {
          Object localObject1 = Thread.currentThread().getThreadGroup();
          Object localObject2 = localObject1;
          try
          {
            while (localObject2 != null)
            {
              localObject1 = localObject2;
              localObject2 = ((ThreadGroup)localObject1).getParent();
            }
          }
          catch (SecurityException localSecurityException) {}
          return new ThreadGroup((ThreadGroup)localObject1, "ORB ThreadGroup " + ThreadPoolManagerImpl.tgCount.getAndIncrement());
        }
      });
    }
    catch (SecurityException localSecurityException)
    {
      localThreadGroup = Thread.currentThread().getThreadGroup();
    }
    return localThreadGroup;
  }
  
  public void close()
  {
    try
    {
      threadPool.close();
    }
    catch (IOException localIOException)
    {
      wrapper.threadPoolCloseError();
    }
    try
    {
      boolean bool = threadGroup.isDestroyed();
      int i = threadGroup.activeCount();
      int j = threadGroup.activeGroupCount();
      if (bool)
      {
        wrapper.threadGroupIsDestroyed(threadGroup);
      }
      else
      {
        if (i > 0) {
          wrapper.threadGroupHasActiveThreadsInClose(threadGroup, Integer.valueOf(i));
        }
        if (j > 0) {
          wrapper.threadGroupHasSubGroupsInClose(threadGroup, Integer.valueOf(j));
        }
        threadGroup.destroy();
      }
    }
    catch (IllegalThreadStateException localIllegalThreadStateException)
    {
      wrapper.threadGroupDestroyFailed(localIllegalThreadStateException, threadGroup);
    }
    threadGroup = null;
  }
  
  public ThreadPool getThreadPool(String paramString)
    throws NoSuchThreadPoolException
  {
    return threadPool;
  }
  
  public ThreadPool getThreadPool(int paramInt)
    throws NoSuchThreadPoolException
  {
    return threadPool;
  }
  
  public int getThreadPoolNumericId(String paramString)
  {
    return 0;
  }
  
  public String getThreadPoolStringId(int paramInt)
  {
    return "";
  }
  
  public ThreadPool getDefaultThreadPool()
  {
    return threadPool;
  }
  
  public ThreadPoolChooser getThreadPoolChooser(String paramString)
  {
    return null;
  }
  
  public ThreadPoolChooser getThreadPoolChooser(int paramInt)
  {
    return null;
  }
  
  public void setThreadPoolChooser(String paramString, ThreadPoolChooser paramThreadPoolChooser) {}
  
  public int getThreadPoolChooserNumericId(String paramString)
  {
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\threadpool\ThreadPoolManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */