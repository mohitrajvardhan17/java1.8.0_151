package java.nio.channels;

import java.io.IOException;

public abstract class FileLock
  implements AutoCloseable
{
  private final Channel channel;
  private final long position;
  private final long size;
  private final boolean shared;
  
  protected FileLock(FileChannel paramFileChannel, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Negative position");
    }
    if (paramLong2 < 0L) {
      throw new IllegalArgumentException("Negative size");
    }
    if (paramLong1 + paramLong2 < 0L) {
      throw new IllegalArgumentException("Negative position + size");
    }
    channel = paramFileChannel;
    position = paramLong1;
    size = paramLong2;
    shared = paramBoolean;
  }
  
  protected FileLock(AsynchronousFileChannel paramAsynchronousFileChannel, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Negative position");
    }
    if (paramLong2 < 0L) {
      throw new IllegalArgumentException("Negative size");
    }
    if (paramLong1 + paramLong2 < 0L) {
      throw new IllegalArgumentException("Negative position + size");
    }
    channel = paramAsynchronousFileChannel;
    position = paramLong1;
    size = paramLong2;
    shared = paramBoolean;
  }
  
  public final FileChannel channel()
  {
    return (channel instanceof FileChannel) ? (FileChannel)channel : null;
  }
  
  public Channel acquiredBy()
  {
    return channel;
  }
  
  public final long position()
  {
    return position;
  }
  
  public final long size()
  {
    return size;
  }
  
  public final boolean isShared()
  {
    return shared;
  }
  
  public final boolean overlaps(long paramLong1, long paramLong2)
  {
    if (paramLong1 + paramLong2 <= position) {
      return false;
    }
    return position + size > paramLong1;
  }
  
  public abstract boolean isValid();
  
  public abstract void release()
    throws IOException;
  
  public final void close()
    throws IOException
  {
    release();
  }
  
  public final String toString()
  {
    return getClass().getName() + "[" + position + ":" + size + " " + (shared ? "shared" : "exclusive") + " " + (isValid() ? "valid" : "invalid") + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\FileLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */