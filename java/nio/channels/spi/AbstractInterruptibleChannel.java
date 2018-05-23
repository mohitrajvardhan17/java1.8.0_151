package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptibleChannel;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.Interruptible;

public abstract class AbstractInterruptibleChannel
  implements Channel, InterruptibleChannel
{
  private final Object closeLock = new Object();
  private volatile boolean open = true;
  private Interruptible interruptor;
  private volatile Thread interrupted;
  
  protected AbstractInterruptibleChannel() {}
  
  public final void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (!open) {
        return;
      }
      open = false;
      implCloseChannel();
    }
  }
  
  protected abstract void implCloseChannel()
    throws IOException;
  
  public final boolean isOpen()
  {
    return open;
  }
  
  protected final void begin()
  {
    if (interruptor == null) {
      interruptor = new Interruptible()
      {
        public void interrupt(Thread paramAnonymousThread)
        {
          synchronized (closeLock)
          {
            if (!open) {
              return;
            }
            open = false;
            interrupted = paramAnonymousThread;
            try
            {
              implCloseChannel();
            }
            catch (IOException localIOException) {}
          }
        }
      };
    }
    blockedOn(interruptor);
    Thread localThread = Thread.currentThread();
    if (localThread.isInterrupted()) {
      interruptor.interrupt(localThread);
    }
  }
  
  protected final void end(boolean paramBoolean)
    throws AsynchronousCloseException
  {
    blockedOn(null);
    Thread localThread = interrupted;
    if ((localThread != null) && (localThread == Thread.currentThread()))
    {
      localThread = null;
      throw new ClosedByInterruptException();
    }
    if ((!paramBoolean) && (!open)) {
      throw new AsynchronousCloseException();
    }
  }
  
  static void blockedOn(Interruptible paramInterruptible)
  {
    SharedSecrets.getJavaLangAccess().blockedOn(Thread.currentThread(), paramInterruptible);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\spi\AbstractInterruptibleChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */