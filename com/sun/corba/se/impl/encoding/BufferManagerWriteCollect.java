package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import java.util.Iterator;

public class BufferManagerWriteCollect
  extends BufferManagerWrite
{
  private BufferQueue queue = new BufferQueue();
  private boolean sentFragment = false;
  private boolean debug = false;
  
  BufferManagerWriteCollect(ORB paramORB)
  {
    super(paramORB);
    if (paramORB != null) {
      debug = transportDebugFlag;
    }
  }
  
  public boolean sentFragment()
  {
    return sentFragment;
  }
  
  public int getBufferSize()
  {
    return orb.getORBData().getGIOPFragmentSize();
  }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    MessageBase.setFlag(byteBuffer, 2);
    queue.enqueue(paramByteBufferWithInfo);
    ByteBufferWithInfo localByteBufferWithInfo = new ByteBufferWithInfo(orb, this);
    fragmented = true;
    ((CDROutputObject)outputObject).setByteBufferWithInfo(localByteBufferWithInfo);
    FragmentMessage localFragmentMessage = ((CDROutputObject)outputObject).getMessageHeader().createFragmentMessage();
    localFragmentMessage.write((CDROutputObject)outputObject);
  }
  
  public void sendMessage()
  {
    queue.enqueue(((CDROutputObject)outputObject).getByteBufferWithInfo());
    Iterator localIterator = iterator();
    Connection localConnection = ((OutputObject)outputObject).getMessageMediator().getConnection();
    localConnection.writeLock();
    try
    {
      ByteBufferPool localByteBufferPool = orb.getByteBufferPool();
      while (localIterator.hasNext())
      {
        ByteBufferWithInfo localByteBufferWithInfo = (ByteBufferWithInfo)localIterator.next();
        ((CDROutputObject)outputObject).setByteBufferWithInfo(localByteBufferWithInfo);
        localConnection.sendWithoutLock((CDROutputObject)outputObject);
        sentFragment = true;
        if (debug)
        {
          int i = System.identityHashCode(byteBuffer);
          StringBuffer localStringBuffer = new StringBuffer(80);
          localStringBuffer.append("sendMessage() - releasing ByteBuffer id (");
          localStringBuffer.append(i).append(") to ByteBufferPool.");
          String str = localStringBuffer.toString();
          dprint(str);
        }
        localByteBufferPool.releaseByteBuffer(byteBuffer);
        byteBuffer = null;
        localByteBufferWithInfo = null;
      }
      sentFullMessage = true;
    }
    finally
    {
      localConnection.writeUnlock();
    }
  }
  
  public void close()
  {
    Iterator localIterator = iterator();
    ByteBufferPool localByteBufferPool = orb.getByteBufferPool();
    while (localIterator.hasNext())
    {
      ByteBufferWithInfo localByteBufferWithInfo = (ByteBufferWithInfo)localIterator.next();
      if ((localByteBufferWithInfo != null) && (byteBuffer != null))
      {
        if (debug)
        {
          int i = System.identityHashCode(byteBuffer);
          StringBuffer localStringBuffer = new StringBuffer(80);
          localStringBuffer.append("close() - releasing ByteBuffer id (");
          localStringBuffer.append(i).append(") to ByteBufferPool.");
          String str = localStringBuffer.toString();
          dprint(str);
        }
        localByteBufferPool.releaseByteBuffer(byteBuffer);
        byteBuffer = null;
        localByteBufferWithInfo = null;
      }
    }
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("BufferManagerWriteCollect", paramString);
  }
  
  private Iterator iterator()
  {
    return new BufferManagerWriteCollectIterator(null);
  }
  
  private class BufferManagerWriteCollectIterator
    implements Iterator
  {
    private BufferManagerWriteCollectIterator() {}
    
    public boolean hasNext()
    {
      return queue.size() != 0;
    }
    
    public Object next()
    {
      return queue.dequeue();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWriteCollect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */