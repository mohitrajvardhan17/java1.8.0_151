package sun.nio.ch;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectionKey;

public class SelectionKeyImpl
  extends AbstractSelectionKey
{
  final SelChImpl channel;
  public final SelectorImpl selector;
  private int index;
  private volatile int interestOps;
  private int readyOps;
  
  SelectionKeyImpl(SelChImpl paramSelChImpl, SelectorImpl paramSelectorImpl)
  {
    channel = paramSelChImpl;
    selector = paramSelectorImpl;
  }
  
  public SelectableChannel channel()
  {
    return (SelectableChannel)channel;
  }
  
  public Selector selector()
  {
    return selector;
  }
  
  int getIndex()
  {
    return index;
  }
  
  void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  private void ensureValid()
  {
    if (!isValid()) {
      throw new CancelledKeyException();
    }
  }
  
  public int interestOps()
  {
    ensureValid();
    return interestOps;
  }
  
  public SelectionKey interestOps(int paramInt)
  {
    ensureValid();
    return nioInterestOps(paramInt);
  }
  
  public int readyOps()
  {
    ensureValid();
    return readyOps;
  }
  
  public void nioReadyOps(int paramInt)
  {
    readyOps = paramInt;
  }
  
  public int nioReadyOps()
  {
    return readyOps;
  }
  
  public SelectionKey nioInterestOps(int paramInt)
  {
    if ((paramInt & (channel().validOps() ^ 0xFFFFFFFF)) != 0) {
      throw new IllegalArgumentException();
    }
    channel.translateAndSetInterestOps(paramInt, this);
    interestOps = paramInt;
    return this;
  }
  
  public int nioInterestOps()
  {
    return interestOps;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\SelectionKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */