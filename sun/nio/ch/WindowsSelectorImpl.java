package sun.nio.ch;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class WindowsSelectorImpl
  extends SelectorImpl
{
  private final int INIT_CAP = 8;
  private static final int MAX_SELECTABLE_FDS = 1024;
  private SelectionKeyImpl[] channelArray = new SelectionKeyImpl[8];
  private PollArrayWrapper pollWrapper = new PollArrayWrapper(8);
  private int totalChannels = 1;
  private int threadsCount = 0;
  private final List<SelectThread> threads = new ArrayList();
  private final Pipe wakeupPipe = Pipe.open();
  private final int wakeupSourceFd = ((SelChImpl)wakeupPipe.source()).getFDVal();
  private final int wakeupSinkFd;
  private Object closeLock = new Object();
  private final FdMap fdMap = new FdMap(null);
  private final SubSelector subSelector = new SubSelector(null);
  private long timeout;
  private final Object interruptLock = new Object();
  private volatile boolean interruptTriggered = false;
  private final StartLock startLock = new StartLock(null);
  private final FinishLock finishLock = new FinishLock(null);
  private long updateCount = 0L;
  
  WindowsSelectorImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    super(paramSelectorProvider);
    SinkChannelImpl localSinkChannelImpl = (SinkChannelImpl)wakeupPipe.sink();
    sc.socket().setTcpNoDelay(true);
    wakeupSinkFd = localSinkChannelImpl.getFDVal();
    pollWrapper.addWakeupSocket(wakeupSourceFd, 0);
  }
  
  protected int doSelect(long paramLong)
    throws IOException
  {
    if (channelArray == null) {
      throw new ClosedSelectorException();
    }
    timeout = paramLong;
    processDeregisterQueue();
    if (interruptTriggered)
    {
      resetWakeupSocket();
      return 0;
    }
    adjustThreadsCount();
    finishLock.reset();
    startLock.startThreads();
    try
    {
      begin();
      try
      {
        subSelector.poll();
      }
      catch (IOException localIOException)
      {
        finishLock.setException(localIOException);
      }
      if (threads.size() > 0) {
        finishLock.waitForHelperThreads();
      }
    }
    finally
    {
      end();
    }
    finishLock.checkForException();
    processDeregisterQueue();
    int i = updateSelectedKeys();
    resetWakeupSocket();
    return i;
  }
  
  private void adjustThreadsCount()
  {
    int i;
    if (threadsCount > threads.size()) {
      for (i = threads.size(); i < threadsCount; i++)
      {
        SelectThread localSelectThread = new SelectThread(i, null);
        threads.add(localSelectThread);
        localSelectThread.setDaemon(true);
        localSelectThread.start();
      }
    } else if (threadsCount < threads.size()) {
      for (i = threads.size() - 1; i >= threadsCount; i--) {
        ((SelectThread)threads.remove(i)).makeZombie();
      }
    }
  }
  
  private void setWakeupSocket()
  {
    setWakeupSocket0(wakeupSinkFd);
  }
  
  private native void setWakeupSocket0(int paramInt);
  
  private void resetWakeupSocket()
  {
    synchronized (interruptLock)
    {
      if (!interruptTriggered) {
        return;
      }
      resetWakeupSocket0(wakeupSourceFd);
      interruptTriggered = false;
    }
  }
  
  private native void resetWakeupSocket0(int paramInt);
  
  private native boolean discardUrgentData(int paramInt);
  
  private int updateSelectedKeys()
  {
    updateCount += 1L;
    int i = 0;
    i += subSelector.processSelectedKeys(updateCount);
    Iterator localIterator = threads.iterator();
    while (localIterator.hasNext())
    {
      SelectThread localSelectThread = (SelectThread)localIterator.next();
      i += subSelector.processSelectedKeys(updateCount);
    }
    return i;
  }
  
  protected void implClose()
    throws IOException
  {
    synchronized (closeLock)
    {
      if ((channelArray != null) && (pollWrapper != null))
      {
        synchronized (interruptLock)
        {
          interruptTriggered = true;
        }
        wakeupPipe.sink().close();
        wakeupPipe.source().close();
        Object localObject2;
        for (int i = 1; i < totalChannels; i++) {
          if (i % 1024 != 0)
          {
            deregister(channelArray[i]);
            localObject2 = channelArray[i].channel();
            if ((!((SelectableChannel)localObject2).isOpen()) && (!((SelectableChannel)localObject2).isRegistered())) {
              ((SelChImpl)localObject2).kill();
            }
          }
        }
        pollWrapper.free();
        pollWrapper = null;
        selectedKeys = null;
        channelArray = null;
        Iterator localIterator = threads.iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (SelectThread)localIterator.next();
          ((SelectThread)localObject2).makeZombie();
        }
        startLock.startThreads();
      }
    }
  }
  
  protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl)
  {
    synchronized (closeLock)
    {
      if (pollWrapper == null) {
        throw new ClosedSelectorException();
      }
      growIfNeeded();
      channelArray[totalChannels] = paramSelectionKeyImpl;
      paramSelectionKeyImpl.setIndex(totalChannels);
      fdMap.put(paramSelectionKeyImpl);
      keys.add(paramSelectionKeyImpl);
      pollWrapper.addEntry(totalChannels, paramSelectionKeyImpl);
      totalChannels += 1;
    }
  }
  
  private void growIfNeeded()
  {
    if (channelArray.length == totalChannels)
    {
      int i = totalChannels * 2;
      SelectionKeyImpl[] arrayOfSelectionKeyImpl = new SelectionKeyImpl[i];
      System.arraycopy(channelArray, 1, arrayOfSelectionKeyImpl, 1, totalChannels - 1);
      channelArray = arrayOfSelectionKeyImpl;
      pollWrapper.grow(i);
    }
    if (totalChannels % 1024 == 0)
    {
      pollWrapper.addWakeupSocket(wakeupSourceFd, totalChannels);
      totalChannels += 1;
      threadsCount += 1;
    }
  }
  
  protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl)
    throws IOException
  {
    int i = paramSelectionKeyImpl.getIndex();
    assert (i >= 0);
    synchronized (closeLock)
    {
      if (i != totalChannels - 1)
      {
        SelectionKeyImpl localSelectionKeyImpl = channelArray[(totalChannels - 1)];
        channelArray[i] = localSelectionKeyImpl;
        localSelectionKeyImpl.setIndex(i);
        pollWrapper.replaceEntry(pollWrapper, totalChannels - 1, pollWrapper, i);
      }
      paramSelectionKeyImpl.setIndex(-1);
    }
    channelArray[(totalChannels - 1)] = null;
    totalChannels -= 1;
    if ((totalChannels != 1) && (totalChannels % 1024 == 1))
    {
      totalChannels -= 1;
      threadsCount -= 1;
    }
    fdMap.remove(paramSelectionKeyImpl);
    keys.remove(paramSelectionKeyImpl);
    selectedKeys.remove(paramSelectionKeyImpl);
    deregister(paramSelectionKeyImpl);
    ??? = paramSelectionKeyImpl.channel();
    if ((!((SelectableChannel)???).isOpen()) && (!((SelectableChannel)???).isRegistered())) {
      ((SelChImpl)???).kill();
    }
  }
  
  public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt)
  {
    synchronized (closeLock)
    {
      if (pollWrapper == null) {
        throw new ClosedSelectorException();
      }
      int i = paramSelectionKeyImpl.getIndex();
      if (i == -1) {
        throw new CancelledKeyException();
      }
      pollWrapper.putEventOps(i, paramInt);
    }
  }
  
  public Selector wakeup()
  {
    synchronized (interruptLock)
    {
      if (!interruptTriggered)
      {
        setWakeupSocket();
        interruptTriggered = true;
      }
    }
    return this;
  }
  
  static
  {
    IOUtil.load();
  }
  
  private static final class FdMap
    extends HashMap<Integer, WindowsSelectorImpl.MapEntry>
  {
    static final long serialVersionUID = 0L;
    
    private FdMap() {}
    
    private WindowsSelectorImpl.MapEntry get(int paramInt)
    {
      return (WindowsSelectorImpl.MapEntry)get(new Integer(paramInt));
    }
    
    private WindowsSelectorImpl.MapEntry put(SelectionKeyImpl paramSelectionKeyImpl)
    {
      return (WindowsSelectorImpl.MapEntry)put(new Integer(channel.getFDVal()), new WindowsSelectorImpl.MapEntry(paramSelectionKeyImpl));
    }
    
    private WindowsSelectorImpl.MapEntry remove(SelectionKeyImpl paramSelectionKeyImpl)
    {
      Integer localInteger = new Integer(channel.getFDVal());
      WindowsSelectorImpl.MapEntry localMapEntry = (WindowsSelectorImpl.MapEntry)get(localInteger);
      if ((localMapEntry != null) && (ski.channel == channel)) {
        return (WindowsSelectorImpl.MapEntry)remove(localInteger);
      }
      return null;
    }
  }
  
  private final class FinishLock
  {
    private int threadsToFinish;
    IOException exception = null;
    
    private FinishLock() {}
    
    private void reset()
    {
      threadsToFinish = threads.size();
    }
    
    private synchronized void threadFinished()
    {
      if (threadsToFinish == threads.size()) {
        wakeup();
      }
      threadsToFinish -= 1;
      if (threadsToFinish == 0) {
        notify();
      }
    }
    
    private synchronized void waitForHelperThreads()
    {
      if (threadsToFinish == threads.size()) {
        wakeup();
      }
      while (threadsToFinish != 0) {
        try
        {
          finishLock.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
        }
      }
    }
    
    private synchronized void setException(IOException paramIOException)
    {
      exception = paramIOException;
    }
    
    private void checkForException()
      throws IOException
    {
      if (exception == null) {
        return;
      }
      StringBuffer localStringBuffer = new StringBuffer("An exception occurred during the execution of select(): \n");
      localStringBuffer.append(exception);
      localStringBuffer.append('\n');
      exception = null;
      throw new IOException(localStringBuffer.toString());
    }
  }
  
  private static final class MapEntry
  {
    SelectionKeyImpl ski;
    long updateCount = 0L;
    long clearedCount = 0L;
    
    MapEntry(SelectionKeyImpl paramSelectionKeyImpl)
    {
      ski = paramSelectionKeyImpl;
    }
  }
  
  private final class SelectThread
    extends Thread
  {
    private final int index;
    final WindowsSelectorImpl.SubSelector subSelector;
    private long lastRun = 0L;
    private volatile boolean zombie;
    
    private SelectThread(int paramInt)
    {
      index = paramInt;
      subSelector = new WindowsSelectorImpl.SubSelector(WindowsSelectorImpl.this, paramInt, null);
      lastRun = WindowsSelectorImpl.StartLock.access$2400(startLock);
    }
    
    void makeZombie()
    {
      zombie = true;
    }
    
    boolean isZombie()
    {
      return zombie;
    }
    
    public void run()
    {
      for (;;)
      {
        if (WindowsSelectorImpl.StartLock.access$2500(startLock, this)) {
          return;
        }
        try
        {
          WindowsSelectorImpl.SubSelector.access$2600(subSelector, index);
        }
        catch (IOException localIOException)
        {
          finishLock.setException(localIOException);
        }
        finishLock.threadFinished();
      }
    }
  }
  
  private final class StartLock
  {
    private long runsCounter;
    
    private StartLock() {}
    
    private synchronized void startThreads()
    {
      runsCounter += 1L;
      notifyAll();
    }
    
    private synchronized boolean waitForStart(WindowsSelectorImpl.SelectThread paramSelectThread)
    {
      while (runsCounter == lastRun) {
        try
        {
          startLock.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
        }
      }
      if (paramSelectThread.isZombie()) {
        return true;
      }
      lastRun = runsCounter;
      return false;
    }
  }
  
  private final class SubSelector
  {
    private final int pollArrayIndex;
    private final int[] readFds = new int['Ё'];
    private final int[] writeFds = new int['Ё'];
    private final int[] exceptFds = new int['Ё'];
    
    private SubSelector()
    {
      pollArrayIndex = 0;
    }
    
    private SubSelector(int paramInt)
    {
      pollArrayIndex = ((paramInt + 1) * 1024);
    }
    
    private int poll()
      throws IOException
    {
      return poll0(pollWrapper.pollArrayAddress, Math.min(totalChannels, 1024), readFds, writeFds, exceptFds, timeout);
    }
    
    private int poll(int paramInt)
      throws IOException
    {
      return poll0(pollWrapper.pollArrayAddress + pollArrayIndex * PollArrayWrapper.SIZE_POLLFD, Math.min(1024, totalChannels - (paramInt + 1) * 1024), readFds, writeFds, exceptFds, timeout);
    }
    
    private native int poll0(long paramLong1, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, long paramLong2);
    
    private int processSelectedKeys(long paramLong)
    {
      int i = 0;
      i += processFDSet(paramLong, readFds, Net.POLLIN, false);
      i += processFDSet(paramLong, writeFds, Net.POLLCONN | Net.POLLOUT, false);
      i += processFDSet(paramLong, exceptFds, Net.POLLIN | Net.POLLCONN | Net.POLLOUT, true);
      return i;
    }
    
    private int processFDSet(long paramLong, int[] paramArrayOfInt, int paramInt, boolean paramBoolean)
    {
      int i = 0;
      for (int j = 1; j <= paramArrayOfInt[0]; j++)
      {
        int k = paramArrayOfInt[j];
        if (k == wakeupSourceFd)
        {
          synchronized (interruptLock)
          {
            interruptTriggered = true;
          }
        }
        else
        {
          ??? = fdMap.get(k);
          if (??? != null)
          {
            SelectionKeyImpl localSelectionKeyImpl = ski;
            if ((!paramBoolean) || (!(localSelectionKeyImpl.channel() instanceof SocketChannelImpl)) || (!WindowsSelectorImpl.this.discardUrgentData(k))) {
              if (selectedKeys.contains(localSelectionKeyImpl))
              {
                if (clearedCount != paramLong)
                {
                  if ((channel.translateAndSetReadyOps(paramInt, localSelectionKeyImpl)) && (updateCount != paramLong))
                  {
                    updateCount = paramLong;
                    i++;
                  }
                }
                else if ((channel.translateAndUpdateReadyOps(paramInt, localSelectionKeyImpl)) && (updateCount != paramLong))
                {
                  updateCount = paramLong;
                  i++;
                }
                clearedCount = paramLong;
              }
              else
              {
                if (clearedCount != paramLong)
                {
                  channel.translateAndSetReadyOps(paramInt, localSelectionKeyImpl);
                  if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0)
                  {
                    selectedKeys.add(localSelectionKeyImpl);
                    updateCount = paramLong;
                    i++;
                  }
                }
                else
                {
                  channel.translateAndUpdateReadyOps(paramInt, localSelectionKeyImpl);
                  if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0)
                  {
                    selectedKeys.add(localSelectionKeyImpl);
                    updateCount = paramLong;
                    i++;
                  }
                }
                clearedCount = paramLong;
              }
            }
          }
        }
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\WindowsSelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */