package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract interface WatchService
  extends Closeable
{
  public abstract void close()
    throws IOException;
  
  public abstract WatchKey poll();
  
  public abstract WatchKey poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;
  
  public abstract WatchKey take()
    throws InterruptedException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\WatchService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */