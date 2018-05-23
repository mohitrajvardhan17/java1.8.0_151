package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.nio.ch.Interruptible;

public abstract class AbstractSelector
  extends Selector
{
  private AtomicBoolean selectorOpen = new AtomicBoolean(true);
  private final SelectorProvider provider;
  private final Set<SelectionKey> cancelledKeys = new HashSet();
  private Interruptible interruptor = null;
  
  protected AbstractSelector(SelectorProvider paramSelectorProvider)
  {
    provider = paramSelectorProvider;
  }
  
  void cancel(SelectionKey paramSelectionKey)
  {
    synchronized (cancelledKeys)
    {
      cancelledKeys.add(paramSelectionKey);
    }
  }
  
  public final void close()
    throws IOException
  {
    boolean bool = selectorOpen.getAndSet(false);
    if (!bool) {
      return;
    }
    implCloseSelector();
  }
  
  protected abstract void implCloseSelector()
    throws IOException;
  
  public final boolean isOpen()
  {
    return selectorOpen.get();
  }
  
  public final SelectorProvider provider()
  {
    return provider;
  }
  
  protected final Set<SelectionKey> cancelledKeys()
  {
    return cancelledKeys;
  }
  
  protected abstract SelectionKey register(AbstractSelectableChannel paramAbstractSelectableChannel, int paramInt, Object paramObject);
  
  protected final void deregister(AbstractSelectionKey paramAbstractSelectionKey)
  {
    ((AbstractSelectableChannel)paramAbstractSelectionKey.channel()).removeKey(paramAbstractSelectionKey);
  }
  
  protected final void begin()
  {
    if (interruptor == null) {
      interruptor = new Interruptible()
      {
        public void interrupt(Thread paramAnonymousThread)
        {
          wakeup();
        }
      };
    }
    AbstractInterruptibleChannel.blockedOn(interruptor);
    Thread localThread = Thread.currentThread();
    if (localThread.isInterrupted()) {
      interruptor.interrupt(localThread);
    }
  }
  
  protected final void end()
  {
    AbstractInterruptibleChannel.blockedOn(null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\spi\AbstractSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */