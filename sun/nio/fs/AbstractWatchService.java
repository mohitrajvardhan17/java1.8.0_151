package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

abstract class AbstractWatchService
  implements WatchService
{
  private final LinkedBlockingDeque<WatchKey> pendingKeys = new LinkedBlockingDeque();
  private final WatchKey CLOSE_KEY = new AbstractWatchKey(null, null)
  {
    public boolean isValid()
    {
      return true;
    }
    
    public void cancel() {}
  };
  private volatile boolean closed;
  private final Object closeLock = new Object();
  
  protected AbstractWatchService() {}
  
  abstract WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs)
    throws IOException;
  
  final void enqueueKey(WatchKey paramWatchKey)
  {
    pendingKeys.offer(paramWatchKey);
  }
  
  private void checkOpen()
  {
    if (closed) {
      throw new ClosedWatchServiceException();
    }
  }
  
  private void checkKey(WatchKey paramWatchKey)
  {
    if (paramWatchKey == CLOSE_KEY) {
      enqueueKey(paramWatchKey);
    }
    checkOpen();
  }
  
  public final WatchKey poll()
  {
    checkOpen();
    WatchKey localWatchKey = (WatchKey)pendingKeys.poll();
    checkKey(localWatchKey);
    return localWatchKey;
  }
  
  public final WatchKey poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    checkOpen();
    WatchKey localWatchKey = (WatchKey)pendingKeys.poll(paramLong, paramTimeUnit);
    checkKey(localWatchKey);
    return localWatchKey;
  }
  
  public final WatchKey take()
    throws InterruptedException
  {
    checkOpen();
    WatchKey localWatchKey = (WatchKey)pendingKeys.take();
    checkKey(localWatchKey);
    return localWatchKey;
  }
  
  final boolean isOpen()
  {
    return !closed;
  }
  
  final Object closeLock()
  {
    return closeLock;
  }
  
  abstract void implClose()
    throws IOException;
  
  public final void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (closed) {
        return;
      }
      closed = true;
      implClose();
      pendingKeys.clear();
      pendingKeys.offer(CLOSE_KEY);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\AbstractWatchService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */