package sun.nio.ch;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class SelectorImpl
  extends AbstractSelector
{
  protected Set<SelectionKey> selectedKeys = new HashSet();
  protected HashSet<SelectionKey> keys = new HashSet();
  private Set<SelectionKey> publicKeys;
  private Set<SelectionKey> publicSelectedKeys;
  
  protected SelectorImpl(SelectorProvider paramSelectorProvider)
  {
    super(paramSelectorProvider);
    if (Util.atBugLevel("1.4"))
    {
      publicKeys = keys;
      publicSelectedKeys = selectedKeys;
    }
    else
    {
      publicKeys = Collections.unmodifiableSet(keys);
      publicSelectedKeys = Util.ungrowableSet(selectedKeys);
    }
  }
  
  public Set<SelectionKey> keys()
  {
    if ((!isOpen()) && (!Util.atBugLevel("1.4"))) {
      throw new ClosedSelectorException();
    }
    return publicKeys;
  }
  
  public Set<SelectionKey> selectedKeys()
  {
    if ((!isOpen()) && (!Util.atBugLevel("1.4"))) {
      throw new ClosedSelectorException();
    }
    return publicSelectedKeys;
  }
  
  protected abstract int doSelect(long paramLong)
    throws IOException;
  
  /* Error */
  private int lockAndDoSelect(long paramLong)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_3
    //   3: monitorenter
    //   4: aload_0
    //   5: invokevirtual 145	sun/nio/ch/SelectorImpl:isOpen	()Z
    //   8: ifne +11 -> 19
    //   11: new 73	java/nio/channels/ClosedSelectorException
    //   14: dup
    //   15: invokespecial 136	java/nio/channels/ClosedSelectorException:<init>	()V
    //   18: athrow
    //   19: aload_0
    //   20: getfield 131	sun/nio/ch/SelectorImpl:publicKeys	Ljava/util/Set;
    //   23: dup
    //   24: astore 4
    //   26: monitorenter
    //   27: aload_0
    //   28: getfield 132	sun/nio/ch/SelectorImpl:publicSelectedKeys	Ljava/util/Set;
    //   31: dup
    //   32: astore 5
    //   34: monitorenter
    //   35: aload_0
    //   36: lload_1
    //   37: invokevirtual 146	sun/nio/ch/SelectorImpl:doSelect	(J)I
    //   40: aload 5
    //   42: monitorexit
    //   43: aload 4
    //   45: monitorexit
    //   46: aload_3
    //   47: monitorexit
    //   48: ireturn
    //   49: astore 6
    //   51: aload 5
    //   53: monitorexit
    //   54: aload 6
    //   56: athrow
    //   57: astore 7
    //   59: aload 4
    //   61: monitorexit
    //   62: aload 7
    //   64: athrow
    //   65: astore 8
    //   67: aload_3
    //   68: monitorexit
    //   69: aload 8
    //   71: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	SelectorImpl
    //   0	72	1	paramLong	long
    //   2	66	3	Ljava/lang/Object;	Object
    //   24	36	4	Ljava/lang/Object;	Object
    //   32	20	5	Ljava/lang/Object;	Object
    //   49	6	6	localObject1	Object
    //   57	6	7	localObject2	Object
    //   65	5	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   35	43	49	finally
    //   49	54	49	finally
    //   27	46	57	finally
    //   49	62	57	finally
    //   4	48	65	finally
    //   49	69	65	finally
  }
  
  public int select(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative timeout");
    }
    return lockAndDoSelect(paramLong == 0L ? -1L : paramLong);
  }
  
  public int select()
    throws IOException
  {
    return select(0L);
  }
  
  public int selectNow()
    throws IOException
  {
    return lockAndDoSelect(0L);
  }
  
  public void implCloseSelector()
    throws IOException
  {
    wakeup();
    synchronized (this)
    {
      synchronized (publicKeys)
      {
        synchronized (publicSelectedKeys)
        {
          implClose();
        }
      }
    }
  }
  
  protected abstract void implClose()
    throws IOException;
  
  public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {}
  
  protected final SelectionKey register(AbstractSelectableChannel paramAbstractSelectableChannel, int paramInt, Object paramObject)
  {
    if (!(paramAbstractSelectableChannel instanceof SelChImpl)) {
      throw new IllegalSelectorException();
    }
    SelectionKeyImpl localSelectionKeyImpl = new SelectionKeyImpl((SelChImpl)paramAbstractSelectableChannel, this);
    localSelectionKeyImpl.attach(paramObject);
    synchronized (publicKeys)
    {
      implRegister(localSelectionKeyImpl);
    }
    localSelectionKeyImpl.interestOps(paramInt);
    return localSelectionKeyImpl;
  }
  
  protected abstract void implRegister(SelectionKeyImpl paramSelectionKeyImpl);
  
  void processDeregisterQueue()
    throws IOException
  {
    Set localSet = cancelledKeys();
    synchronized (localSet)
    {
      if (!localSet.isEmpty())
      {
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext())
        {
          SelectionKeyImpl localSelectionKeyImpl = (SelectionKeyImpl)localIterator.next();
          try
          {
            implDereg(localSelectionKeyImpl);
          }
          catch (SocketException localSocketException)
          {
            throw new IOException("Error deregistering key", localSocketException);
          }
          finally
          {
            localIterator.remove();
          }
        }
      }
    }
  }
  
  protected abstract void implDereg(SelectionKeyImpl paramSelectionKeyImpl)
    throws IOException;
  
  public abstract Selector wakeup();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\SelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */