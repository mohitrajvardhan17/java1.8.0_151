package sun.net.www.http;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import sun.net.NetProperties;

class KeepAliveStreamCleaner
  extends LinkedList<KeepAliveCleanerEntry>
  implements Runnable
{
  protected static int MAX_DATA_REMAINING = 512;
  protected static int MAX_CAPACITY = 10;
  protected static final int TIMEOUT = 5000;
  private static final int MAX_RETRIES = 5;
  
  KeepAliveStreamCleaner() {}
  
  public boolean offer(KeepAliveCleanerEntry paramKeepAliveCleanerEntry)
  {
    if (size() >= MAX_CAPACITY) {
      return false;
    }
    return super.offer(paramKeepAliveCleanerEntry);
  }
  
  public void run()
  {
    KeepAliveCleanerEntry localKeepAliveCleanerEntry = null;
    do
    {
      try
      {
        synchronized (this)
        {
          long l1 = System.currentTimeMillis();
          long l6;
          for (long l2 = 5000L; (localKeepAliveCleanerEntry = (KeepAliveCleanerEntry)poll()) == null; l2 -= l6)
          {
            wait(l2);
            long l4 = System.currentTimeMillis();
            l6 = l4 - l1;
            if (l6 > l2)
            {
              localKeepAliveCleanerEntry = (KeepAliveCleanerEntry)poll();
              break;
            }
            l1 = l4;
          }
        }
        if (localKeepAliveCleanerEntry == null) {
          break;
        }
        ??? = localKeepAliveCleanerEntry.getKeepAliveStream();
        if (??? != null) {
          synchronized (???)
          {
            HttpClient localHttpClient = localKeepAliveCleanerEntry.getHttpClient();
            try
            {
              if ((localHttpClient != null) && (!localHttpClient.isInKeepAliveCache()))
              {
                int i = localHttpClient.getReadTimeout();
                localHttpClient.setReadTimeout(5000);
                long l3 = ((KeepAliveStream)???).remainingToRead();
                if (l3 > 0L)
                {
                  long l5 = 0L;
                  int j = 0;
                  while ((l5 < l3) && (j < 5))
                  {
                    l3 -= l5;
                    l5 = ((KeepAliveStream)???).skip(l3);
                    if (l5 == 0L) {
                      j++;
                    }
                  }
                  l3 -= l5;
                }
                if (l3 == 0L)
                {
                  localHttpClient.setReadTimeout(i);
                  localHttpClient.finished();
                }
                else
                {
                  localHttpClient.closeServer();
                }
              }
            }
            catch (IOException localIOException)
            {
              localHttpClient.closeServer();
            }
            finally
            {
              ((KeepAliveStream)???).setClosed();
            }
          }
        }
      }
      catch (InterruptedException localInterruptedException) {}
    } while (localKeepAliveCleanerEntry != null);
  }
  
  static
  {
    int i = ((Integer)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Integer run()
      {
        return NetProperties.getInteger("http.KeepAlive.remainingData", KeepAliveStreamCleaner.MAX_DATA_REMAINING);
      }
    })).intValue() * 1024;
    MAX_DATA_REMAINING = i;
    int j = ((Integer)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Integer run()
      {
        return NetProperties.getInteger("http.KeepAlive.queuedConnections", KeepAliveStreamCleaner.MAX_CAPACITY);
      }
    })).intValue();
    MAX_CAPACITY = j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\KeepAliveStreamCleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */