package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import java.nio.ByteBuffer;

public class ByteBufferWithInfo
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private boolean debug;
  private int index;
  public ByteBuffer byteBuffer;
  public int buflen;
  public int needed;
  public boolean fragmented;
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    debug = orb.transportDebugFlag;
    byteBuffer = paramByteBuffer;
    if (paramByteBuffer != null) {
      buflen = paramByteBuffer.limit();
    }
    position(paramInt);
    needed = 0;
    fragmented = false;
  }
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer)
  {
    this(paramORB, paramByteBuffer, 0);
  }
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, BufferManagerWrite paramBufferManagerWrite)
  {
    this(paramORB, paramBufferManagerWrite, true);
  }
  
  public ByteBufferWithInfo(org.omg.CORBA.ORB paramORB, BufferManagerWrite paramBufferManagerWrite, boolean paramBoolean)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    debug = orb.transportDebugFlag;
    int i = paramBufferManagerWrite.getBufferSize();
    if (paramBoolean)
    {
      ByteBufferPool localByteBufferPool = orb.getByteBufferPool();
      byteBuffer = localByteBufferPool.getByteBuffer(i);
      if (debug)
      {
        int j = System.identityHashCode(byteBuffer);
        StringBuffer localStringBuffer = new StringBuffer(80);
        localStringBuffer.append("constructor (ORB, BufferManagerWrite) - got ").append("ByteBuffer id (").append(j).append(") from ByteBufferPool.");
        String str = localStringBuffer.toString();
        dprint(str);
      }
    }
    else
    {
      byteBuffer = ByteBuffer.allocate(i);
    }
    position(0);
    buflen = i;
    byteBuffer.limit(buflen);
    needed = 0;
    fragmented = false;
  }
  
  public ByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    orb = orb;
    debug = debug;
    byteBuffer = byteBuffer;
    buflen = buflen;
    byteBuffer.limit(buflen);
    position(paramByteBufferWithInfo.position());
    needed = needed;
    fragmented = fragmented;
  }
  
  public int getSize()
  {
    return position();
  }
  
  public int getLength()
  {
    return buflen;
  }
  
  public int position()
  {
    return index;
  }
  
  public void position(int paramInt)
  {
    byteBuffer.position(paramInt);
    index = paramInt;
  }
  
  public void setLength(int paramInt)
  {
    buflen = paramInt;
    byteBuffer.limit(buflen);
  }
  
  public void growBuffer(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    int i = byteBuffer.limit() * 2;
    while (position() + needed >= i) {
      i *= 2;
    }
    ByteBufferPool localByteBufferPool = paramORB.getByteBufferPool();
    ByteBuffer localByteBuffer = localByteBufferPool.getByteBuffer(i);
    int j;
    StringBuffer localStringBuffer;
    String str;
    if (debug)
    {
      j = System.identityHashCode(localByteBuffer);
      localStringBuffer = new StringBuffer(80);
      localStringBuffer.append("growBuffer() - got ByteBuffer id (");
      localStringBuffer.append(j).append(") from ByteBufferPool.");
      str = localStringBuffer.toString();
      dprint(str);
    }
    byteBuffer.position(0);
    localByteBuffer.put(byteBuffer);
    if (debug)
    {
      j = System.identityHashCode(byteBuffer);
      localStringBuffer = new StringBuffer(80);
      localStringBuffer.append("growBuffer() - releasing ByteBuffer id (");
      localStringBuffer.append(j).append(") to ByteBufferPool.");
      str = localStringBuffer.toString();
      dprint(str);
    }
    localByteBufferPool.releaseByteBuffer(byteBuffer);
    byteBuffer = localByteBuffer;
    buflen = i;
    byteBuffer.limit(buflen);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("ByteBufferWithInfo:");
    localStringBuffer.append(" buflen = " + buflen);
    localStringBuffer.append(" byteBuffer.limit = " + byteBuffer.limit());
    localStringBuffer.append(" index = " + index);
    localStringBuffer.append(" position = " + position());
    localStringBuffer.append(" needed = " + needed);
    localStringBuffer.append(" byteBuffer = " + (byteBuffer == null ? "null" : "not null"));
    localStringBuffer.append(" fragmented = " + fragmented);
    return localStringBuffer.toString();
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("ByteBufferWithInfo", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\ByteBufferWithInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */