package sun.misc;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract interface JavaNioAccess
{
  public abstract BufferPool getDirectBufferPool();
  
  public abstract ByteBuffer newDirectByteBuffer(long paramLong, int paramInt, Object paramObject);
  
  public abstract void truncate(Buffer paramBuffer);
  
  public static abstract interface BufferPool
  {
    public abstract String getName();
    
    public abstract long getCount();
    
    public abstract long getTotalCapacity();
    
    public abstract long getMemoryUsed();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JavaNioAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */