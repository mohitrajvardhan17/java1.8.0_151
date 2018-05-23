package java.nio;

import java.io.FileDescriptor;
import sun.misc.Unsafe;

public abstract class MappedByteBuffer
  extends ByteBuffer
{
  private final FileDescriptor fd;
  private static byte unused;
  
  MappedByteBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, FileDescriptor paramFileDescriptor)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    fd = paramFileDescriptor;
  }
  
  MappedByteBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    fd = null;
  }
  
  private void checkMapped()
  {
    if (fd == null) {
      throw new UnsupportedOperationException();
    }
  }
  
  private long mappingOffset()
  {
    int i = Bits.pageSize();
    long l = address % i;
    return l >= 0L ? l : i + l;
  }
  
  private long mappingAddress(long paramLong)
  {
    return address - paramLong;
  }
  
  private long mappingLength(long paramLong)
  {
    return capacity() + paramLong;
  }
  
  public final boolean isLoaded()
  {
    checkMapped();
    if ((address == 0L) || (capacity() == 0)) {
      return true;
    }
    long l1 = mappingOffset();
    long l2 = mappingLength(l1);
    return isLoaded0(mappingAddress(l1), l2, Bits.pageCount(l2));
  }
  
  public final MappedByteBuffer load()
  {
    checkMapped();
    if ((address == 0L) || (capacity() == 0)) {
      return this;
    }
    long l1 = mappingOffset();
    long l2 = mappingLength(l1);
    load0(mappingAddress(l1), l2);
    Unsafe localUnsafe = Unsafe.getUnsafe();
    int i = Bits.pageSize();
    int j = Bits.pageCount(l2);
    long l3 = mappingAddress(l1);
    byte b = 0;
    for (int k = 0; k < j; k++)
    {
      b = (byte)(b ^ localUnsafe.getByte(l3));
      l3 += i;
    }
    if (unused != 0) {
      unused = b;
    }
    return this;
  }
  
  public final MappedByteBuffer force()
  {
    checkMapped();
    if ((address != 0L) && (capacity() != 0))
    {
      long l = mappingOffset();
      force0(fd, mappingAddress(l), mappingLength(l));
    }
    return this;
  }
  
  private native boolean isLoaded0(long paramLong1, long paramLong2, int paramInt);
  
  private native void load0(long paramLong1, long paramLong2);
  
  private native void force0(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\MappedByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */