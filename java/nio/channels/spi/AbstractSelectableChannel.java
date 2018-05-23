package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public abstract class AbstractSelectableChannel
  extends SelectableChannel
{
  private final SelectorProvider provider;
  private SelectionKey[] keys = null;
  private int keyCount = 0;
  private final Object keyLock = new Object();
  private final Object regLock = new Object();
  boolean blocking = true;
  
  protected AbstractSelectableChannel(SelectorProvider paramSelectorProvider)
  {
    provider = paramSelectorProvider;
  }
  
  public final SelectorProvider provider()
  {
    return provider;
  }
  
  private void addKey(SelectionKey paramSelectionKey)
  {
    assert (Thread.holdsLock(keyLock));
    int i = 0;
    if ((keys != null) && (keyCount < keys.length)) {
      i = 0;
    }
    while ((i < keys.length) && (keys[i] != null))
    {
      i++;
      continue;
      if (keys == null)
      {
        keys = new SelectionKey[3];
      }
      else
      {
        int j = keys.length * 2;
        SelectionKey[] arrayOfSelectionKey = new SelectionKey[j];
        for (i = 0; i < keys.length; i++) {
          arrayOfSelectionKey[i] = keys[i];
        }
        keys = arrayOfSelectionKey;
        i = keyCount;
      }
    }
    keys[i] = paramSelectionKey;
    keyCount += 1;
  }
  
  private SelectionKey findKey(Selector paramSelector)
  {
    synchronized (keyLock)
    {
      if (keys == null) {
        return null;
      }
      for (int i = 0; i < keys.length; i++) {
        if ((keys[i] != null) && (keys[i].selector() == paramSelector)) {
          return keys[i];
        }
      }
      return null;
    }
  }
  
  void removeKey(SelectionKey paramSelectionKey)
  {
    synchronized (keyLock)
    {
      for (int i = 0; i < keys.length; i++) {
        if (keys[i] == paramSelectionKey)
        {
          keys[i] = null;
          keyCount -= 1;
        }
      }
      ((AbstractSelectionKey)paramSelectionKey).invalidate();
    }
  }
  
  private boolean haveValidKeys()
  {
    synchronized (keyLock)
    {
      if (keyCount == 0) {
        return false;
      }
      for (int i = 0; i < keys.length; i++) {
        if ((keys[i] != null) && (keys[i].isValid())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public final boolean isRegistered()
  {
    synchronized (keyLock)
    {
      return keyCount != 0;
    }
  }
  
  public final SelectionKey keyFor(Selector paramSelector)
  {
    return findKey(paramSelector);
  }
  
  public final SelectionKey register(Selector paramSelector, int paramInt, Object paramObject)
    throws ClosedChannelException
  {
    synchronized (regLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if ((paramInt & (validOps() ^ 0xFFFFFFFF)) != 0) {
        throw new IllegalArgumentException();
      }
      if (blocking) {
        throw new IllegalBlockingModeException();
      }
      SelectionKey localSelectionKey = findKey(paramSelector);
      if (localSelectionKey != null)
      {
        localSelectionKey.interestOps(paramInt);
        localSelectionKey.attach(paramObject);
      }
      if (localSelectionKey == null) {
        synchronized (keyLock)
        {
          if (!isOpen()) {
            throw new ClosedChannelException();
          }
          localSelectionKey = ((AbstractSelector)paramSelector).register(this, paramInt, paramObject);
          addKey(localSelectionKey);
        }
      }
      return localSelectionKey;
    }
  }
  
  protected final void implCloseChannel()
    throws IOException
  {
    implCloseSelectableChannel();
    synchronized (keyLock)
    {
      int i = keys == null ? 0 : keys.length;
      for (int j = 0; j < i; j++)
      {
        SelectionKey localSelectionKey = keys[j];
        if (localSelectionKey != null) {
          localSelectionKey.cancel();
        }
      }
    }
  }
  
  protected abstract void implCloseSelectableChannel()
    throws IOException;
  
  /* Error */
  public final boolean isBlocking()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 116	java/nio/channels/spi/AbstractSelectableChannel:regLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 114	java/nio/channels/spi/AbstractSelectableChannel:blocking	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	AbstractSelectableChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public final Object blockingLock()
  {
    return regLock;
  }
  
  public final SelectableChannel configureBlocking(boolean paramBoolean)
    throws IOException
  {
    synchronized (regLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (blocking == paramBoolean) {
        return this;
      }
      if ((paramBoolean) && (haveValidKeys())) {
        throw new IllegalBlockingModeException();
      }
      implConfigureBlocking(paramBoolean);
      blocking = paramBoolean;
    }
    return this;
  }
  
  protected abstract void implConfigureBlocking(boolean paramBoolean)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\spi\AbstractSelectableChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */