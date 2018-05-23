package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.RequestCanceledException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;

public class BufferManagerReadStream
  implements BufferManagerRead, MarkAndResetHandler
{
  private boolean receivedCancel = false;
  private int cancelReqId = 0;
  private boolean endOfStream = true;
  private BufferQueue fragmentQueue = new BufferQueue();
  private long FRAGMENT_TIMEOUT = 60000L;
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private boolean debug = false;
  private boolean markEngaged = false;
  private LinkedList fragmentStack = null;
  private RestorableInputStream inputStream = null;
  private Object streamMemento = null;
  
  BufferManagerReadStream(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    debug = transportDebugFlag;
  }
  
  public void cancelProcessing(int paramInt)
  {
    synchronized (fragmentQueue)
    {
      receivedCancel = true;
      cancelReqId = paramInt;
      fragmentQueue.notify();
    }
  }
  
  public void processFragment(ByteBuffer paramByteBuffer, FragmentMessage paramFragmentMessage)
  {
    ByteBufferWithInfo localByteBufferWithInfo = new ByteBufferWithInfo(orb, paramByteBuffer, paramFragmentMessage.getHeaderLength());
    synchronized (fragmentQueue)
    {
      if (debug)
      {
        int i = System.identityHashCode(paramByteBuffer);
        StringBuffer localStringBuffer = new StringBuffer(80);
        localStringBuffer.append("processFragment() - queueing ByteBuffer id (");
        localStringBuffer.append(i).append(") to fragment queue.");
        String str = localStringBuffer.toString();
        dprint(str);
      }
      fragmentQueue.enqueue(localByteBufferWithInfo);
      endOfStream = (!paramFragmentMessage.moreFragmentsToFollow());
      fragmentQueue.notify();
    }
  }
  
  public ByteBufferWithInfo underflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    ByteBufferWithInfo localByteBufferWithInfo = null;
    synchronized (fragmentQueue)
    {
      if (receivedCancel) {
        throw new RequestCanceledException(cancelReqId);
      }
      int i;
      while (fragmentQueue.size() == 0)
      {
        if (endOfStream) {
          throw wrapper.endOfStream();
        }
        i = 0;
        try
        {
          fragmentQueue.wait(FRAGMENT_TIMEOUT);
        }
        catch (InterruptedException localInterruptedException)
        {
          i = 1;
        }
        if ((i == 0) && (fragmentQueue.size() == 0)) {
          throw wrapper.bufferReadManagerTimeout();
        }
        if (receivedCancel) {
          throw new RequestCanceledException(cancelReqId);
        }
      }
      localByteBufferWithInfo = fragmentQueue.dequeue();
      fragmented = true;
      Object localObject1;
      if (debug)
      {
        i = System.identityHashCode(byteBuffer);
        StringBuffer localStringBuffer = new StringBuffer(80);
        localStringBuffer.append("underflow() - dequeued ByteBuffer id (");
        localStringBuffer.append(i).append(") from fragment queue.");
        localObject1 = localStringBuffer.toString();
        dprint((String)localObject1);
      }
      if ((!markEngaged) && (paramByteBufferWithInfo != null) && (byteBuffer != null))
      {
        ByteBufferPool localByteBufferPool = getByteBufferPool();
        if (debug)
        {
          int j = System.identityHashCode(byteBuffer);
          localObject1 = new StringBuffer(80);
          ((StringBuffer)localObject1).append("underflow() - releasing ByteBuffer id (");
          ((StringBuffer)localObject1).append(j).append(") to ByteBufferPool.");
          String str = ((StringBuffer)localObject1).toString();
          dprint(str);
        }
        localByteBufferPool.releaseByteBuffer(byteBuffer);
        byteBuffer = null;
        paramByteBufferWithInfo = null;
      }
    }
    return localByteBufferWithInfo;
  }
  
  public void init(Message paramMessage)
  {
    if (paramMessage != null) {
      endOfStream = (!paramMessage.moreFragmentsToFollow());
    }
  }
  
  public void close(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    int i = 0;
    Object localObject1;
    Object localObject2;
    int j;
    StringBuffer localStringBuffer;
    String str;
    if (fragmentQueue != null)
    {
      synchronized (fragmentQueue)
      {
        if (paramByteBufferWithInfo != null) {
          i = System.identityHashCode(byteBuffer);
        }
        localObject1 = null;
        localObject2 = getByteBufferPool();
        while (fragmentQueue.size() != 0)
        {
          localObject1 = fragmentQueue.dequeue();
          if ((localObject1 != null) && (byteBuffer != null))
          {
            j = System.identityHashCode(byteBuffer);
            if ((i != j) && (debug))
            {
              localStringBuffer = new StringBuffer(80);
              localStringBuffer.append("close() - fragmentQueue is ").append("releasing ByteBuffer id (").append(j).append(") to ").append("ByteBufferPool.");
              str = localStringBuffer.toString();
              dprint(str);
            }
            ((ByteBufferPool)localObject2).releaseByteBuffer(byteBuffer);
          }
        }
      }
      fragmentQueue = null;
    }
    if ((fragmentStack != null) && (fragmentStack.size() != 0))
    {
      if (paramByteBufferWithInfo != null) {
        i = System.identityHashCode(byteBuffer);
      }
      ??? = null;
      localObject1 = getByteBufferPool();
      localObject2 = fragmentStack.listIterator();
      while (((ListIterator)localObject2).hasNext())
      {
        ??? = (ByteBufferWithInfo)((ListIterator)localObject2).next();
        if ((??? != null) && (byteBuffer != null))
        {
          j = System.identityHashCode(byteBuffer);
          if (i != j)
          {
            if (debug)
            {
              localStringBuffer = new StringBuffer(80);
              localStringBuffer.append("close() - fragmentStack - releasing ").append("ByteBuffer id (" + j + ") to ").append("ByteBufferPool.");
              str = localStringBuffer.toString();
              dprint(str);
            }
            ((ByteBufferPool)localObject1).releaseByteBuffer(byteBuffer);
          }
        }
      }
      fragmentStack = null;
    }
  }
  
  protected ByteBufferPool getByteBufferPool()
  {
    return orb.getByteBufferPool();
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("BufferManagerReadStream", paramString);
  }
  
  public void mark(RestorableInputStream paramRestorableInputStream)
  {
    inputStream = paramRestorableInputStream;
    markEngaged = true;
    streamMemento = paramRestorableInputStream.createStreamMemento();
    if (fragmentStack != null) {
      fragmentStack.clear();
    }
  }
  
  public void fragmentationOccured(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    if (!markEngaged) {
      return;
    }
    if (fragmentStack == null) {
      fragmentStack = new LinkedList();
    }
    fragmentStack.addFirst(new ByteBufferWithInfo(paramByteBufferWithInfo));
  }
  
  public void reset()
  {
    if (!markEngaged) {
      return;
    }
    markEngaged = false;
    if ((fragmentStack != null) && (fragmentStack.size() != 0))
    {
      ListIterator localListIterator = fragmentStack.listIterator();
      synchronized (fragmentQueue)
      {
        while (localListIterator.hasNext()) {
          fragmentQueue.push((ByteBufferWithInfo)localListIterator.next());
        }
      }
      fragmentStack.clear();
    }
    inputStream.restoreInternalState(streamMemento);
  }
  
  public MarkAndResetHandler getMarkAndResetHandler()
  {
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerReadStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */