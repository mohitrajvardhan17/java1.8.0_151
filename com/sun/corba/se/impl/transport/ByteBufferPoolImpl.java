package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteBufferPoolImpl
  implements ByteBufferPool
{
  private ORB itsOrb;
  private int itsByteBufferSize;
  private ArrayList itsPool;
  private int itsObjectCounter = 0;
  private boolean debug;
  
  public ByteBufferPoolImpl(ORB paramORB)
  {
    itsByteBufferSize = paramORB.getORBData().getGIOPFragmentSize();
    itsPool = new ArrayList();
    itsOrb = paramORB;
    debug = transportDebugFlag;
  }
  
  public ByteBuffer getByteBuffer(int paramInt)
  {
    ByteBuffer localByteBuffer = null;
    if ((paramInt <= itsByteBufferSize) && (!itsOrb.getORBData().disableDirectByteBufferUse()))
    {
      int i;
      synchronized (itsPool)
      {
        i = itsPool.size();
        if (i > 0)
        {
          localByteBuffer = (ByteBuffer)itsPool.remove(i - 1);
          localByteBuffer.clear();
        }
      }
      if (i <= 0) {
        localByteBuffer = ByteBuffer.allocateDirect(itsByteBufferSize);
      }
      itsObjectCounter += 1;
    }
    else
    {
      localByteBuffer = ByteBuffer.allocate(paramInt);
    }
    return localByteBuffer;
  }
  
  public void releaseByteBuffer(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.isDirect())
    {
      synchronized (itsPool)
      {
        int i = 0;
        int j = 0;
        Object localObject1;
        if (debug) {
          for (int k = 0; (k < itsPool.size()) && (i == 0); k++)
          {
            localObject1 = (ByteBuffer)itsPool.get(k);
            if (paramByteBuffer == localObject1)
            {
              i = 1;
              j = System.identityHashCode(paramByteBuffer);
            }
          }
        }
        if ((i == 0) || (!debug))
        {
          itsPool.add(paramByteBuffer);
        }
        else
        {
          String str = Thread.currentThread().getName();
          localObject1 = new Throwable(str + ": Duplicate ByteBuffer reference (" + j + ")");
          ((Throwable)localObject1).printStackTrace(System.out);
        }
      }
      itsObjectCounter -= 1;
    }
    else
    {
      paramByteBuffer = null;
    }
  }
  
  public int activeCount()
  {
    return itsObjectCounter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\ByteBufferPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */