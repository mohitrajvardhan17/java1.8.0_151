package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class FileInputStream
  extends InputStream
{
  private final FileDescriptor fd;
  private final String path;
  private FileChannel channel = null;
  private final Object closeLock = new Object();
  private volatile boolean closed = false;
  
  public FileInputStream(String paramString)
    throws FileNotFoundException
  {
    this(paramString != null ? new File(paramString) : null);
  }
  
  public FileInputStream(File paramFile)
    throws FileNotFoundException
  {
    String str = paramFile != null ? paramFile.getPath() : null;
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(str);
    }
    if (str == null) {
      throw new NullPointerException();
    }
    if (paramFile.isInvalid()) {
      throw new FileNotFoundException("Invalid file path");
    }
    fd = new FileDescriptor();
    fd.attach(this);
    path = str;
    open(str);
  }
  
  public FileInputStream(FileDescriptor paramFileDescriptor)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (paramFileDescriptor == null) {
      throw new NullPointerException();
    }
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(paramFileDescriptor);
    }
    fd = paramFileDescriptor;
    path = null;
    fd.attach(this);
  }
  
  private native void open0(String paramString)
    throws FileNotFoundException;
  
  private void open(String paramString)
    throws FileNotFoundException
  {
    open0(paramString);
  }
  
  public int read()
    throws IOException
  {
    return read0();
  }
  
  private native int read0()
    throws IOException;
  
  private native int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return readBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return readBytes(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public native long skip(long paramLong)
    throws IOException;
  
  public native int available()
    throws IOException;
  
  public void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (closed) {
        return;
      }
      closed = true;
    }
    if (channel != null) {
      channel.close();
    }
    fd.closeAll(new Closeable()
    {
      public void close()
        throws IOException
      {
        FileInputStream.this.close0();
      }
    });
  }
  
  public final FileDescriptor getFD()
    throws IOException
  {
    if (fd != null) {
      return fd;
    }
    throw new IOException();
  }
  
  public FileChannel getChannel()
  {
    synchronized (this)
    {
      if (channel == null) {
        channel = FileChannelImpl.open(fd, path, true, false, this);
      }
      return channel;
    }
  }
  
  private static native void initIDs();
  
  private native void close0()
    throws IOException;
  
  protected void finalize()
    throws IOException
  {
    if ((fd != null) && (fd != FileDescriptor.in)) {
      close();
    }
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FileInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */