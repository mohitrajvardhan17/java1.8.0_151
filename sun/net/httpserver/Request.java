package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class Request
{
  static final int BUF_LEN = 2048;
  static final byte CR = 13;
  static final byte LF = 10;
  private String startLine;
  private SocketChannel chan;
  private InputStream is;
  private OutputStream os;
  char[] buf = new char['ࠀ'];
  int pos;
  StringBuffer lineBuf;
  Headers hdrs = null;
  
  Request(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    is = paramInputStream;
    os = paramOutputStream;
    do
    {
      startLine = readLine();
      if (startLine == null) {
        return;
      }
    } while ((startLine != null) && (startLine.equals("")));
  }
  
  public InputStream inputStream()
  {
    return is;
  }
  
  public OutputStream outputStream()
  {
    return os;
  }
  
  public String readLine()
    throws IOException
  {
    int i = 0;
    int j = 0;
    pos = 0;
    lineBuf = new StringBuffer();
    while (j == 0)
    {
      int k = is.read();
      if (k == -1) {
        return null;
      }
      if (i != 0)
      {
        if (k == 10)
        {
          j = 1;
        }
        else
        {
          i = 0;
          consume(13);
          consume(k);
        }
      }
      else if (k == 13) {
        i = 1;
      } else {
        consume(k);
      }
    }
    lineBuf.append(buf, 0, pos);
    return new String(lineBuf);
  }
  
  private void consume(int paramInt)
  {
    if (pos == 2048)
    {
      lineBuf.append(buf);
      pos = 0;
    }
    buf[(pos++)] = ((char)paramInt);
  }
  
  public String requestLine()
  {
    return startLine;
  }
  
  Headers headers()
    throws IOException
  {
    if (hdrs != null) {
      return hdrs;
    }
    hdrs = new Headers();
    Object localObject1 = new char[10];
    int i = 0;
    int j = is.read();
    int k;
    if ((j == 13) || (j == 10))
    {
      k = is.read();
      if ((k == 13) || (k == 10)) {
        return hdrs;
      }
      localObject1[0] = ((char)j);
      i = 1;
      j = k;
    }
    while ((j != 10) && (j != 13) && (j >= 0))
    {
      k = -1;
      int n = j > 32 ? 1 : 0;
      localObject1[(i++)] = ((char)j);
      int m;
      Object localObject2;
      while ((m = is.read()) >= 0)
      {
        switch (m)
        {
        case 58: 
          if ((n != 0) && (i > 0)) {
            k = i;
          }
          n = 0;
          break;
        case 9: 
          m = 32;
        case 32: 
          n = 0;
          break;
        case 10: 
        case 13: 
          j = is.read();
          if ((m == 13) && (j == 10))
          {
            j = is.read();
            if (j == 13) {
              j = is.read();
            }
          }
          if ((j == 10) || (j == 13) || (j > 32)) {
            break label328;
          }
          m = 32;
        }
        if (i >= localObject1.length)
        {
          localObject2 = new char[localObject1.length * 2];
          System.arraycopy(localObject1, 0, localObject2, 0, i);
          localObject1 = localObject2;
        }
        localObject1[(i++)] = ((char)m);
      }
      j = -1;
      label328:
      while ((i > 0) && (localObject1[(i - 1)] <= ' ')) {
        i--;
      }
      if (k <= 0)
      {
        localObject2 = null;
        k = 0;
      }
      else
      {
        localObject2 = String.copyValueOf((char[])localObject1, 0, k);
        if ((k < i) && (localObject1[k] == ':')) {
          k++;
        }
        while ((k < i) && (localObject1[k] <= ' ')) {
          k++;
        }
      }
      String str;
      if (k >= i) {
        str = new String();
      } else {
        str = String.copyValueOf((char[])localObject1, k, i - k);
      }
      if (hdrs.size() >= ServerConfig.getMaxReqHeaders()) {
        throw new IOException("Maximum number of request headers (sun.net.httpserver.maxReqHeaders) exceeded, " + ServerConfig.getMaxReqHeaders() + ".");
      }
      hdrs.add((String)localObject2, str);
      i = 0;
    }
    return hdrs;
  }
  
  static class ReadStream
    extends InputStream
  {
    SocketChannel channel;
    ByteBuffer chanbuf;
    byte[] one;
    private boolean closed = false;
    private boolean eof = false;
    ByteBuffer markBuf;
    boolean marked;
    boolean reset;
    int readlimit;
    static long readTimeout;
    ServerImpl server;
    static final int BUFSIZE = 8192;
    
    public ReadStream(ServerImpl paramServerImpl, SocketChannel paramSocketChannel)
      throws IOException
    {
      channel = paramSocketChannel;
      server = paramServerImpl;
      chanbuf = ByteBuffer.allocate(8192);
      chanbuf.clear();
      one = new byte[1];
      closed = (marked = reset = 0);
    }
    
    public synchronized int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public synchronized int read()
      throws IOException
    {
      int i = read(one, 0, 1);
      if (i == 1) {
        return one[0] & 0xFF;
      }
      return -1;
    }
    
    public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (closed) {
        throw new IOException("Stream closed");
      }
      if (eof) {
        return -1;
      }
      assert (channel.isBlocking());
      if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
        throw new IndexOutOfBoundsException();
      }
      int j;
      if (reset)
      {
        int i = markBuf.remaining();
        j = i > paramInt2 ? paramInt2 : i;
        markBuf.get(paramArrayOfByte, paramInt1, j);
        if (i == j) {
          reset = false;
        }
      }
      else
      {
        chanbuf.clear();
        if (paramInt2 < 8192) {
          chanbuf.limit(paramInt2);
        }
        do
        {
          j = channel.read(chanbuf);
        } while (j == 0);
        if (j == -1)
        {
          eof = true;
          return -1;
        }
        chanbuf.flip();
        chanbuf.get(paramArrayOfByte, paramInt1, j);
        if (marked) {
          try
          {
            markBuf.put(paramArrayOfByte, paramInt1, j);
          }
          catch (BufferOverflowException localBufferOverflowException)
          {
            marked = false;
          }
        }
      }
      return j;
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public synchronized int available()
      throws IOException
    {
      if (closed) {
        throw new IOException("Stream is closed");
      }
      if (eof) {
        return -1;
      }
      if (reset) {
        return markBuf.remaining();
      }
      return chanbuf.remaining();
    }
    
    public void close()
      throws IOException
    {
      if (closed) {
        return;
      }
      channel.close();
      closed = true;
    }
    
    public synchronized void mark(int paramInt)
    {
      if (closed) {
        return;
      }
      readlimit = paramInt;
      markBuf = ByteBuffer.allocate(paramInt);
      marked = true;
      reset = false;
    }
    
    public synchronized void reset()
      throws IOException
    {
      if (closed) {
        return;
      }
      if (!marked) {
        throw new IOException("Stream not marked");
      }
      marked = false;
      reset = true;
      markBuf.flip();
    }
  }
  
  static class WriteStream
    extends OutputStream
  {
    SocketChannel channel;
    ByteBuffer buf;
    SelectionKey key;
    boolean closed;
    byte[] one;
    ServerImpl server;
    
    public WriteStream(ServerImpl paramServerImpl, SocketChannel paramSocketChannel)
      throws IOException
    {
      channel = paramSocketChannel;
      server = paramServerImpl;
      assert (paramSocketChannel.isBlocking());
      closed = false;
      one = new byte[1];
      buf = ByteBuffer.allocate(4096);
    }
    
    public synchronized void write(int paramInt)
      throws IOException
    {
      one[0] = ((byte)paramInt);
      write(one, 0, 1);
    }
    
    public synchronized void write(byte[] paramArrayOfByte)
      throws IOException
    {
      write(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt2;
      if (closed) {
        throw new IOException("stream is closed");
      }
      int j = buf.capacity();
      int k;
      if (j < paramInt2)
      {
        k = paramInt2 - j;
        buf = ByteBuffer.allocate(2 * (j + k));
      }
      buf.clear();
      buf.put(paramArrayOfByte, paramInt1, paramInt2);
      buf.flip();
      while ((k = channel.write(buf)) < i)
      {
        i -= k;
        if (i == 0) {}
      }
    }
    
    public void close()
      throws IOException
    {
      if (closed) {
        return;
      }
      channel.close();
      closed = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */