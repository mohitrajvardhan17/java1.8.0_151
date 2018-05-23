package sun.nio.fs;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

class NativeBuffer
{
  private static final Unsafe unsafe = ;
  private final long address;
  private final int size;
  private final Cleaner cleaner;
  private Object owner;
  
  NativeBuffer(int paramInt)
  {
    address = unsafe.allocateMemory(paramInt);
    size = paramInt;
    cleaner = Cleaner.create(this, new Deallocator(address));
  }
  
  void release()
  {
    NativeBuffers.releaseNativeBuffer(this);
  }
  
  long address()
  {
    return address;
  }
  
  int size()
  {
    return size;
  }
  
  Cleaner cleaner()
  {
    return cleaner;
  }
  
  void setOwner(Object paramObject)
  {
    owner = paramObject;
  }
  
  Object owner()
  {
    return owner;
  }
  
  private static class Deallocator
    implements Runnable
  {
    private final long address;
    
    Deallocator(long paramLong)
    {
      address = paramLong;
    }
    
    public void run()
    {
      NativeBuffer.unsafe.freeMemory(address);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\NativeBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */