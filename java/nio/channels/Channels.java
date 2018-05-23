package java.nio.channels;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import sun.nio.ch.ChannelInputStream;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Channels
{
  private Channels() {}
  
  private static void checkNotNull(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      throw new NullPointerException("\"" + paramString + "\" is null!");
    }
  }
  
  private static void writeFullyImpl(WritableByteChannel paramWritableByteChannel, ByteBuffer paramByteBuffer)
    throws IOException
  {
    while (paramByteBuffer.remaining() > 0)
    {
      int i = paramWritableByteChannel.write(paramByteBuffer);
      if (i <= 0) {
        throw new RuntimeException("no bytes written");
      }
    }
  }
  
  private static void writeFully(WritableByteChannel paramWritableByteChannel, ByteBuffer paramByteBuffer)
    throws IOException
  {
    if ((paramWritableByteChannel instanceof SelectableChannel))
    {
      SelectableChannel localSelectableChannel = (SelectableChannel)paramWritableByteChannel;
      synchronized (localSelectableChannel.blockingLock())
      {
        if (!localSelectableChannel.isBlocking()) {
          throw new IllegalBlockingModeException();
        }
        writeFullyImpl(paramWritableByteChannel, paramByteBuffer);
      }
    }
    else
    {
      writeFullyImpl(paramWritableByteChannel, paramByteBuffer);
    }
  }
  
  public static InputStream newInputStream(ReadableByteChannel paramReadableByteChannel)
  {
    checkNotNull(paramReadableByteChannel, "ch");
    return new ChannelInputStream(paramReadableByteChannel);
  }
  
  public static OutputStream newOutputStream(WritableByteChannel paramWritableByteChannel)
  {
    checkNotNull(paramWritableByteChannel, "ch");
    new OutputStream()
    {
      private ByteBuffer bb = null;
      private byte[] bs = null;
      private byte[] b1 = null;
      
      public synchronized void write(int paramAnonymousInt)
        throws IOException
      {
        if (b1 == null) {
          b1 = new byte[1];
        }
        b1[0] = ((byte)paramAnonymousInt);
        write(b1);
      }
      
      public synchronized void write(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        if ((paramAnonymousInt1 < 0) || (paramAnonymousInt1 > paramAnonymousArrayOfByte.length) || (paramAnonymousInt2 < 0) || (paramAnonymousInt1 + paramAnonymousInt2 > paramAnonymousArrayOfByte.length) || (paramAnonymousInt1 + paramAnonymousInt2 < 0)) {
          throw new IndexOutOfBoundsException();
        }
        if (paramAnonymousInt2 == 0) {
          return;
        }
        ByteBuffer localByteBuffer = bs == paramAnonymousArrayOfByte ? bb : ByteBuffer.wrap(paramAnonymousArrayOfByte);
        localByteBuffer.limit(Math.min(paramAnonymousInt1 + paramAnonymousInt2, localByteBuffer.capacity()));
        localByteBuffer.position(paramAnonymousInt1);
        bb = localByteBuffer;
        bs = paramAnonymousArrayOfByte;
        Channels.writeFully(val$ch, localByteBuffer);
      }
      
      public void close()
        throws IOException
      {
        val$ch.close();
      }
    };
  }
  
  public static InputStream newInputStream(AsynchronousByteChannel paramAsynchronousByteChannel)
  {
    checkNotNull(paramAsynchronousByteChannel, "ch");
    new InputStream()
    {
      private ByteBuffer bb = null;
      private byte[] bs = null;
      private byte[] b1 = null;
      
      public synchronized int read()
        throws IOException
      {
        if (b1 == null) {
          b1 = new byte[1];
        }
        int i = read(b1);
        if (i == 1) {
          return b1[0] & 0xFF;
        }
        return -1;
      }
      
      public synchronized int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        if ((paramAnonymousInt1 < 0) || (paramAnonymousInt1 > paramAnonymousArrayOfByte.length) || (paramAnonymousInt2 < 0) || (paramAnonymousInt1 + paramAnonymousInt2 > paramAnonymousArrayOfByte.length) || (paramAnonymousInt1 + paramAnonymousInt2 < 0)) {
          throw new IndexOutOfBoundsException();
        }
        if (paramAnonymousInt2 == 0) {
          return 0;
        }
        ByteBuffer localByteBuffer = bs == paramAnonymousArrayOfByte ? bb : ByteBuffer.wrap(paramAnonymousArrayOfByte);
        localByteBuffer.position(paramAnonymousInt1);
        localByteBuffer.limit(Math.min(paramAnonymousInt1 + paramAnonymousInt2, localByteBuffer.capacity()));
        bb = localByteBuffer;
        bs = paramAnonymousArrayOfByte;
        int i = 0;
        try
        {
          int j = ((Integer)val$ch.read(localByteBuffer).get()).intValue();
          return j;
        }
        catch (ExecutionException localExecutionException)
        {
          throw new IOException(localExecutionException.getCause());
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            i = 1;
          }
        }
        finally
        {
          if (i != 0) {
            Thread.currentThread().interrupt();
          }
        }
      }
      
      public void close()
        throws IOException
      {
        val$ch.close();
      }
    };
  }
  
  public static OutputStream newOutputStream(AsynchronousByteChannel paramAsynchronousByteChannel)
  {
    checkNotNull(paramAsynchronousByteChannel, "ch");
    new OutputStream()
    {
      private ByteBuffer bb = null;
      private byte[] bs = null;
      private byte[] b1 = null;
      
      public synchronized void write(int paramAnonymousInt)
        throws IOException
      {
        if (b1 == null) {
          b1 = new byte[1];
        }
        b1[0] = ((byte)paramAnonymousInt);
        write(b1);
      }
      
      public synchronized void write(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        if ((paramAnonymousInt1 < 0) || (paramAnonymousInt1 > paramAnonymousArrayOfByte.length) || (paramAnonymousInt2 < 0) || (paramAnonymousInt1 + paramAnonymousInt2 > paramAnonymousArrayOfByte.length) || (paramAnonymousInt1 + paramAnonymousInt2 < 0)) {
          throw new IndexOutOfBoundsException();
        }
        if (paramAnonymousInt2 == 0) {
          return;
        }
        ByteBuffer localByteBuffer = bs == paramAnonymousArrayOfByte ? bb : ByteBuffer.wrap(paramAnonymousArrayOfByte);
        localByteBuffer.limit(Math.min(paramAnonymousInt1 + paramAnonymousInt2, localByteBuffer.capacity()));
        localByteBuffer.position(paramAnonymousInt1);
        bb = localByteBuffer;
        bs = paramAnonymousArrayOfByte;
        int i = 0;
        try
        {
          while (localByteBuffer.remaining() > 0) {
            try
            {
              val$ch.write(localByteBuffer).get();
            }
            catch (ExecutionException localExecutionException)
            {
              throw new IOException(localExecutionException.getCause());
            }
            catch (InterruptedException localInterruptedException)
            {
              i = 1;
            }
          }
        }
        finally
        {
          if (i != 0) {
            Thread.currentThread().interrupt();
          }
        }
      }
      
      public void close()
        throws IOException
      {
        val$ch.close();
      }
    };
  }
  
  public static ReadableByteChannel newChannel(InputStream paramInputStream)
  {
    checkNotNull(paramInputStream, "in");
    if (((paramInputStream instanceof FileInputStream)) && (FileInputStream.class.equals(paramInputStream.getClass()))) {
      return ((FileInputStream)paramInputStream).getChannel();
    }
    return new ReadableByteChannelImpl(paramInputStream);
  }
  
  public static WritableByteChannel newChannel(OutputStream paramOutputStream)
  {
    checkNotNull(paramOutputStream, "out");
    if (((paramOutputStream instanceof FileOutputStream)) && (FileOutputStream.class.equals(paramOutputStream.getClass()))) {
      return ((FileOutputStream)paramOutputStream).getChannel();
    }
    return new WritableByteChannelImpl(paramOutputStream);
  }
  
  public static Reader newReader(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder, int paramInt)
  {
    checkNotNull(paramReadableByteChannel, "ch");
    return StreamDecoder.forDecoder(paramReadableByteChannel, paramCharsetDecoder.reset(), paramInt);
  }
  
  public static Reader newReader(ReadableByteChannel paramReadableByteChannel, String paramString)
  {
    checkNotNull(paramString, "csName");
    return newReader(paramReadableByteChannel, Charset.forName(paramString).newDecoder(), -1);
  }
  
  public static Writer newWriter(WritableByteChannel paramWritableByteChannel, CharsetEncoder paramCharsetEncoder, int paramInt)
  {
    checkNotNull(paramWritableByteChannel, "ch");
    return StreamEncoder.forEncoder(paramWritableByteChannel, paramCharsetEncoder.reset(), paramInt);
  }
  
  public static Writer newWriter(WritableByteChannel paramWritableByteChannel, String paramString)
  {
    checkNotNull(paramString, "csName");
    return newWriter(paramWritableByteChannel, Charset.forName(paramString).newEncoder(), -1);
  }
  
  private static class ReadableByteChannelImpl
    extends AbstractInterruptibleChannel
    implements ReadableByteChannel
  {
    InputStream in;
    private static final int TRANSFER_SIZE = 8192;
    private byte[] buf = new byte[0];
    private boolean open = true;
    private Object readLock = new Object();
    
    ReadableByteChannelImpl(InputStream paramInputStream)
    {
      in = paramInputStream;
    }
    
    public int read(ByteBuffer paramByteBuffer)
      throws IOException
    {
      int i = paramByteBuffer.remaining();
      int j = 0;
      int k = 0;
      synchronized (readLock)
      {
        while (j < i)
        {
          int m = Math.min(i - j, 8192);
          if (buf.length < m) {
            buf = new byte[m];
          }
          if ((j > 0) && (in.available() <= 0)) {
            break;
          }
          try
          {
            begin();
            k = in.read(buf, 0, m);
          }
          finally
          {
            end(k > 0);
          }
          if (k < 0) {
            break;
          }
          j += k;
          paramByteBuffer.put(buf, 0, k);
        }
        if ((k < 0) && (j == 0)) {
          return -1;
        }
        return j;
      }
    }
    
    protected void implCloseChannel()
      throws IOException
    {
      in.close();
      open = false;
    }
  }
  
  private static class WritableByteChannelImpl
    extends AbstractInterruptibleChannel
    implements WritableByteChannel
  {
    OutputStream out;
    private static final int TRANSFER_SIZE = 8192;
    private byte[] buf = new byte[0];
    private boolean open = true;
    private Object writeLock = new Object();
    
    WritableByteChannelImpl(OutputStream paramOutputStream)
    {
      out = paramOutputStream;
    }
    
    public int write(ByteBuffer paramByteBuffer)
      throws IOException
    {
      int i = paramByteBuffer.remaining();
      int j = 0;
      synchronized (writeLock)
      {
        while (j < i)
        {
          int k = Math.min(i - j, 8192);
          if (buf.length < k) {
            buf = new byte[k];
          }
          paramByteBuffer.get(buf, 0, k);
          try
          {
            begin();
            out.write(buf, 0, k);
          }
          finally
          {
            end(k > 0);
          }
          j += k;
        }
        return j;
      }
    }
    
    protected void implCloseChannel()
      throws IOException
    {
      out.close();
      open = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\Channels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */