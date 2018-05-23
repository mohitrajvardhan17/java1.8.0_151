package com.sun.corba.se.pept.transport;

import java.nio.ByteBuffer;

public abstract interface ByteBufferPool
{
  public abstract ByteBuffer getByteBuffer(int paramInt);
  
  public abstract void releaseByteBuffer(ByteBuffer paramByteBuffer);
  
  public abstract int activeCount();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\ByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */