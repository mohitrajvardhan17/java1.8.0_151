package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface SeekableByteChannel
  extends ByteChannel
{
  public abstract int read(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract int write(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract long position()
    throws IOException;
  
  public abstract SeekableByteChannel position(long paramLong)
    throws IOException;
  
  public abstract long size()
    throws IOException;
  
  public abstract SeekableByteChannel truncate(long paramLong)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\SeekableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */