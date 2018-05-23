package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class AsynchronousChannelGroup
{
  private final AsynchronousChannelProvider provider;
  
  protected AsynchronousChannelGroup(AsynchronousChannelProvider paramAsynchronousChannelProvider)
  {
    provider = paramAsynchronousChannelProvider;
  }
  
  public final AsynchronousChannelProvider provider()
  {
    return provider;
  }
  
  public static AsynchronousChannelGroup withFixedThreadPool(int paramInt, ThreadFactory paramThreadFactory)
    throws IOException
  {
    return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(paramInt, paramThreadFactory);
  }
  
  public static AsynchronousChannelGroup withCachedThreadPool(ExecutorService paramExecutorService, int paramInt)
    throws IOException
  {
    return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(paramExecutorService, paramInt);
  }
  
  public static AsynchronousChannelGroup withThreadPool(ExecutorService paramExecutorService)
    throws IOException
  {
    return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(paramExecutorService, 0);
  }
  
  public abstract boolean isShutdown();
  
  public abstract boolean isTerminated();
  
  public abstract void shutdown();
  
  public abstract void shutdownNow()
    throws IOException;
  
  public abstract boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\AsynchronousChannelGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */