package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockImpl
  extends FileLock
{
  private volatile boolean valid = true;
  
  FileLockImpl(FileChannel paramFileChannel, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    super(paramFileChannel, paramLong1, paramLong2, paramBoolean);
  }
  
  FileLockImpl(AsynchronousFileChannel paramAsynchronousFileChannel, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    super(paramAsynchronousFileChannel, paramLong1, paramLong2, paramBoolean);
  }
  
  public boolean isValid()
  {
    return valid;
  }
  
  void invalidate()
  {
    assert (Thread.holdsLock(this));
    valid = false;
  }
  
  public synchronized void release()
    throws IOException
  {
    Channel localChannel = acquiredBy();
    if (!localChannel.isOpen()) {
      throw new ClosedChannelException();
    }
    if (valid)
    {
      if ((localChannel instanceof FileChannelImpl)) {
        ((FileChannelImpl)localChannel).release(this);
      } else if ((localChannel instanceof AsynchronousFileChannelImpl)) {
        ((AsynchronousFileChannelImpl)localChannel).release(this);
      } else {
        throw new AssertionError();
      }
      valid = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\FileLockImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */