package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class FileOutputStream
  extends OutputStream
{
  private final FileDescriptor fd;
  private final boolean append;
  private FileChannel channel;
  private final String path;
  private final Object closeLock = new Object();
  private volatile boolean closed = false;
  
  public FileOutputStream(String paramString)
    throws FileNotFoundException
  {
    this(paramString != null ? new File(paramString) : null, false);
  }
  
  public FileOutputStream(String paramString, boolean paramBoolean)
    throws FileNotFoundException
  {
    this(paramString != null ? new File(paramString) : null, paramBoolean);
  }
  
  public FileOutputStream(File paramFile)
    throws FileNotFoundException
  {
    this(paramFile, false);
  }
  
  public FileOutputStream(File paramFile, boolean paramBoolean)
    throws FileNotFoundException
  {
    String str = paramFile != null ? paramFile.getPath() : null;
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(str);
    }
    if (str == null) {
      throw new NullPointerException();
    }
    if (paramFile.isInvalid()) {
      throw new FileNotFoundException("Invalid file path");
    }
    fd = new FileDescriptor();
    fd.attach(this);
    append = paramBoolean;
    path = str;
    open(str, paramBoolean);
  }
  
  public FileOutputStream(FileDescriptor paramFileDescriptor)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (paramFileDescriptor == null) {
      throw new NullPointerException();
    }
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(paramFileDescriptor);
    }
    fd = paramFileDescriptor;
    append = false;
    path = null;
    fd.attach(this);
  }
  
  private native void open0(String paramString, boolean paramBoolean)
    throws FileNotFoundException;
  
  private void open(String paramString, boolean paramBoolean)
    throws FileNotFoundException
  {
    open0(paramString, paramBoolean);
  }
  
  private native void write(int paramInt, boolean paramBoolean)
    throws IOException;
  
  public void write(int paramInt)
    throws IOException
  {
    write(paramInt, append);
  }
  
  private native void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException;
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    writeBytes(paramArrayOfByte, 0, paramArrayOfByte.length, append);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    writeBytes(paramArrayOfByte, paramInt1, paramInt2, append);
  }
  
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
        FileOutputStream.this.close0();
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
        channel = FileChannelImpl.open(fd, path, false, true, append, this);
      }
      return channel;
    }
  }
  
  protected void finalize()
    throws IOException
  {
    if (fd != null) {
      if ((fd == FileDescriptor.out) || (fd == FileDescriptor.err)) {
        flush();
      } else {
        close();
      }
    }
  }
  
  private native void close0()
    throws IOException;
  
  private static native void initIDs();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FileOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */