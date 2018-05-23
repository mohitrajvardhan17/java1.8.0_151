package sun.nio.ch;

import java.nio.ByteBuffer;
import sun.misc.Cleaner;
import sun.misc.Unsafe;

class IOVecWrapper
{
  private static final int BASE_OFFSET = 0;
  private static final int LEN_OFFSET = addressSize;
  private static final int SIZE_IOVEC = (short)(addressSize * 2);
  private final AllocatedNativeObject vecArray;
  private final int size;
  private final ByteBuffer[] buf;
  private final int[] position;
  private final int[] remaining;
  private final ByteBuffer[] shadow;
  final long address;
  static int addressSize;
  private static final ThreadLocal<IOVecWrapper> cached = new ThreadLocal();
  
  private IOVecWrapper(int paramInt)
  {
    size = paramInt;
    buf = new ByteBuffer[paramInt];
    position = new int[paramInt];
    remaining = new int[paramInt];
    shadow = new ByteBuffer[paramInt];
    vecArray = new AllocatedNativeObject(paramInt * SIZE_IOVEC, false);
    address = vecArray.address();
  }
  
  static IOVecWrapper get(int paramInt)
  {
    IOVecWrapper localIOVecWrapper = (IOVecWrapper)cached.get();
    if ((localIOVecWrapper != null) && (size < paramInt))
    {
      vecArray.free();
      localIOVecWrapper = null;
    }
    if (localIOVecWrapper == null)
    {
      localIOVecWrapper = new IOVecWrapper(paramInt);
      Cleaner.create(localIOVecWrapper, new Deallocator(vecArray));
      cached.set(localIOVecWrapper);
    }
    return localIOVecWrapper;
  }
  
  void setBuffer(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3)
  {
    buf[paramInt1] = paramByteBuffer;
    position[paramInt1] = paramInt2;
    remaining[paramInt1] = paramInt3;
  }
  
  void setShadow(int paramInt, ByteBuffer paramByteBuffer)
  {
    shadow[paramInt] = paramByteBuffer;
  }
  
  ByteBuffer getBuffer(int paramInt)
  {
    return buf[paramInt];
  }
  
  int getPosition(int paramInt)
  {
    return position[paramInt];
  }
  
  int getRemaining(int paramInt)
  {
    return remaining[paramInt];
  }
  
  ByteBuffer getShadow(int paramInt)
  {
    return shadow[paramInt];
  }
  
  void clearRefs(int paramInt)
  {
    buf[paramInt] = null;
    shadow[paramInt] = null;
  }
  
  void putBase(int paramInt, long paramLong)
  {
    int i = SIZE_IOVEC * paramInt + 0;
    if (addressSize == 4) {
      vecArray.putInt(i, (int)paramLong);
    } else {
      vecArray.putLong(i, paramLong);
    }
  }
  
  void putLen(int paramInt, long paramLong)
  {
    int i = SIZE_IOVEC * paramInt + LEN_OFFSET;
    if (addressSize == 4) {
      vecArray.putInt(i, (int)paramLong);
    } else {
      vecArray.putLong(i, paramLong);
    }
  }
  
  static
  {
    addressSize = Util.unsafe().addressSize();
  }
  
  private static class Deallocator
    implements Runnable
  {
    private final AllocatedNativeObject obj;
    
    Deallocator(AllocatedNativeObject paramAllocatedNativeObject)
    {
      obj = paramAllocatedNativeObject;
    }
    
    public void run()
    {
      obj.free();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\IOVecWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */