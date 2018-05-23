package sun.net.httpserver;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

class SSLStreams
{
  SSLContext sslctx;
  SocketChannel chan;
  TimeSource time;
  ServerImpl server;
  SSLEngine engine;
  EngineWrapper wrapper;
  OutputStream os;
  InputStream is;
  Lock handshaking = new ReentrantLock();
  int app_buf_size;
  int packet_buf_size;
  
  SSLStreams(ServerImpl paramServerImpl, SSLContext paramSSLContext, SocketChannel paramSocketChannel)
    throws IOException
  {
    server = paramServerImpl;
    time = paramServerImpl;
    sslctx = paramSSLContext;
    chan = paramSocketChannel;
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketChannel.socket().getRemoteSocketAddress();
    engine = paramSSLContext.createSSLEngine(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
    engine.setUseClientMode(false);
    HttpsConfigurator localHttpsConfigurator = paramServerImpl.getHttpsConfigurator();
    configureEngine(localHttpsConfigurator, localInetSocketAddress);
    wrapper = new EngineWrapper(paramSocketChannel, engine);
  }
  
  private void configureEngine(HttpsConfigurator paramHttpsConfigurator, InetSocketAddress paramInetSocketAddress)
  {
    if (paramHttpsConfigurator != null)
    {
      Parameters localParameters = new Parameters(paramHttpsConfigurator, paramInetSocketAddress);
      paramHttpsConfigurator.configure(localParameters);
      SSLParameters localSSLParameters = localParameters.getSSLParameters();
      if (localSSLParameters != null)
      {
        engine.setSSLParameters(localSSLParameters);
      }
      else
      {
        if (localParameters.getCipherSuites() != null) {
          try
          {
            engine.setEnabledCipherSuites(localParameters.getCipherSuites());
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
        engine.setNeedClientAuth(localParameters.getNeedClientAuth());
        engine.setWantClientAuth(localParameters.getWantClientAuth());
        if (localParameters.getProtocols() != null) {
          try
          {
            engine.setEnabledProtocols(localParameters.getProtocols());
          }
          catch (IllegalArgumentException localIllegalArgumentException2) {}
        }
      }
    }
  }
  
  void close()
    throws IOException
  {
    wrapper.close();
  }
  
  InputStream getInputStream()
    throws IOException
  {
    if (is == null) {
      is = new InputStream();
    }
    return is;
  }
  
  OutputStream getOutputStream()
    throws IOException
  {
    if (os == null) {
      os = new OutputStream();
    }
    return os;
  }
  
  SSLEngine getSSLEngine()
  {
    return engine;
  }
  
  void beginHandshake()
    throws SSLException
  {
    engine.beginHandshake();
  }
  
  private ByteBuffer allocate(BufType paramBufType)
  {
    return allocate(paramBufType, -1);
  }
  
  private ByteBuffer allocate(BufType paramBufType, int paramInt)
  {
    assert (engine != null);
    synchronized (this)
    {
      SSLSession localSSLSession;
      int i;
      if (paramBufType == BufType.PACKET)
      {
        if (packet_buf_size == 0)
        {
          localSSLSession = engine.getSession();
          packet_buf_size = localSSLSession.getPacketBufferSize();
        }
        if (paramInt > packet_buf_size) {
          packet_buf_size = paramInt;
        }
        i = packet_buf_size;
      }
      else
      {
        if (app_buf_size == 0)
        {
          localSSLSession = engine.getSession();
          app_buf_size = localSSLSession.getApplicationBufferSize();
        }
        if (paramInt > app_buf_size) {
          app_buf_size = paramInt;
        }
        i = app_buf_size;
      }
      return ByteBuffer.allocate(i);
    }
  }
  
  private ByteBuffer realloc(ByteBuffer paramByteBuffer, boolean paramBoolean, BufType paramBufType)
  {
    synchronized (this)
    {
      int i = 2 * paramByteBuffer.capacity();
      ByteBuffer localByteBuffer = allocate(paramBufType, i);
      if (paramBoolean) {
        paramByteBuffer.flip();
      }
      localByteBuffer.put(paramByteBuffer);
      paramByteBuffer = localByteBuffer;
    }
    return paramByteBuffer;
  }
  
  public WrapperResult sendData(ByteBuffer paramByteBuffer)
    throws IOException
  {
    WrapperResult localWrapperResult = null;
    while (paramByteBuffer.remaining() > 0)
    {
      localWrapperResult = wrapper.wrapAndSend(paramByteBuffer);
      SSLEngineResult.Status localStatus = result.getStatus();
      if (localStatus == SSLEngineResult.Status.CLOSED)
      {
        doClosure();
        return localWrapperResult;
      }
      SSLEngineResult.HandshakeStatus localHandshakeStatus = result.getHandshakeStatus();
      if ((localHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) && (localHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)) {
        doHandshake(localHandshakeStatus);
      }
    }
    return localWrapperResult;
  }
  
  public WrapperResult recvData(ByteBuffer paramByteBuffer)
    throws IOException
  {
    WrapperResult localWrapperResult = null;
    assert (paramByteBuffer.position() == 0);
    while (paramByteBuffer.position() == 0)
    {
      localWrapperResult = wrapper.recvAndUnwrap(paramByteBuffer);
      paramByteBuffer = buf != paramByteBuffer ? buf : paramByteBuffer;
      SSLEngineResult.Status localStatus = result.getStatus();
      if (localStatus == SSLEngineResult.Status.CLOSED)
      {
        doClosure();
        return localWrapperResult;
      }
      SSLEngineResult.HandshakeStatus localHandshakeStatus = result.getHandshakeStatus();
      if ((localHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) && (localHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)) {
        doHandshake(localHandshakeStatus);
      }
    }
    paramByteBuffer.flip();
    return localWrapperResult;
  }
  
  /* Error */
  void doClosure()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 274	sun/net/httpserver/SSLStreams:handshaking	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 337 1 0
    //   9: aload_0
    //   10: getstatic 283	sun/net/httpserver/SSLStreams$BufType:APPLICATION	Lsun/net/httpserver/SSLStreams$BufType;
    //   13: invokespecial 319	sun/net/httpserver/SSLStreams:allocate	(Lsun/net/httpserver/SSLStreams$BufType;)Ljava/nio/ByteBuffer;
    //   16: astore_1
    //   17: aload_1
    //   18: invokevirtual 297	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   21: pop
    //   22: aload_1
    //   23: invokevirtual 298	java/nio/ByteBuffer:flip	()Ljava/nio/Buffer;
    //   26: pop
    //   27: aload_0
    //   28: getfield 277	sun/net/httpserver/SSLStreams:wrapper	Lsun/net/httpserver/SSLStreams$EngineWrapper;
    //   31: aload_1
    //   32: iconst_1
    //   33: invokevirtual 325	sun/net/httpserver/SSLStreams$EngineWrapper:wrapAndSendX	(Ljava/nio/ByteBuffer;Z)Lsun/net/httpserver/SSLStreams$WrapperResult;
    //   36: astore_2
    //   37: aload_2
    //   38: getfield 286	sun/net/httpserver/SSLStreams$WrapperResult:result	Ljavax/net/ssl/SSLEngineResult;
    //   41: invokevirtual 314	javax/net/ssl/SSLEngineResult:getStatus	()Ljavax/net/ssl/SSLEngineResult$Status;
    //   44: getstatic 269	javax/net/ssl/SSLEngineResult$Status:CLOSED	Ljavax/net/ssl/SSLEngineResult$Status;
    //   47: if_acmpne -30 -> 17
    //   50: aload_0
    //   51: getfield 274	sun/net/httpserver/SSLStreams:handshaking	Ljava/util/concurrent/locks/Lock;
    //   54: invokeinterface 338 1 0
    //   59: goto +15 -> 74
    //   62: astore_3
    //   63: aload_0
    //   64: getfield 274	sun/net/httpserver/SSLStreams:handshaking	Ljava/util/concurrent/locks/Lock;
    //   67: invokeinterface 338 1 0
    //   72: aload_3
    //   73: athrow
    //   74: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	this	SSLStreams
    //   16	16	1	localByteBuffer	ByteBuffer
    //   36	2	2	localWrapperResult	WrapperResult
    //   62	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	50	62	finally
  }
  
  void doHandshake(SSLEngineResult.HandshakeStatus paramHandshakeStatus)
    throws IOException
  {
    try
    {
      handshaking.lock();
      ByteBuffer localByteBuffer = allocate(BufType.APPLICATION);
      while ((paramHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) && (paramHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING))
      {
        WrapperResult localWrapperResult = null;
        switch (paramHandshakeStatus)
        {
        case NEED_TASK: 
          Runnable localRunnable;
          while ((localRunnable = engine.getDelegatedTask()) != null) {
            localRunnable.run();
          }
        case NEED_WRAP: 
          localByteBuffer.clear();
          localByteBuffer.flip();
          localWrapperResult = wrapper.wrapAndSend(localByteBuffer);
          break;
        case NEED_UNWRAP: 
          localByteBuffer.clear();
          localWrapperResult = wrapper.recvAndUnwrap(localByteBuffer);
          if (buf != localByteBuffer) {
            localByteBuffer = buf;
          }
          assert (localByteBuffer.position() == 0);
        }
        paramHandshakeStatus = result.getHandshakeStatus();
      }
    }
    finally
    {
      handshaking.unlock();
    }
  }
  
  static enum BufType
  {
    PACKET,  APPLICATION;
    
    private BufType() {}
  }
  
  class EngineWrapper
  {
    SocketChannel chan;
    SSLEngine engine;
    Object wrapLock;
    Object unwrapLock;
    ByteBuffer unwrap_src;
    ByteBuffer wrap_dst;
    boolean closed = false;
    int u_remaining;
    
    EngineWrapper(SocketChannel paramSocketChannel, SSLEngine paramSSLEngine)
      throws IOException
    {
      chan = paramSocketChannel;
      engine = paramSSLEngine;
      wrapLock = new Object();
      unwrapLock = new Object();
      unwrap_src = SSLStreams.this.allocate(SSLStreams.BufType.PACKET);
      wrap_dst = SSLStreams.this.allocate(SSLStreams.BufType.PACKET);
    }
    
    void close()
      throws IOException
    {}
    
    SSLStreams.WrapperResult wrapAndSend(ByteBuffer paramByteBuffer)
      throws IOException
    {
      return wrapAndSendX(paramByteBuffer, false);
    }
    
    SSLStreams.WrapperResult wrapAndSendX(ByteBuffer paramByteBuffer, boolean paramBoolean)
      throws IOException
    {
      if ((closed) && (!paramBoolean)) {
        throw new IOException("Engine is closed");
      }
      SSLStreams.WrapperResult localWrapperResult = new SSLStreams.WrapperResult(SSLStreams.this);
      synchronized (wrapLock)
      {
        wrap_dst.clear();
        SSLEngineResult.Status localStatus;
        do
        {
          result = engine.wrap(paramByteBuffer, wrap_dst);
          localStatus = result.getStatus();
          if (localStatus == SSLEngineResult.Status.BUFFER_OVERFLOW) {
            wrap_dst = SSLStreams.this.realloc(wrap_dst, true, SSLStreams.BufType.PACKET);
          }
        } while (localStatus == SSLEngineResult.Status.BUFFER_OVERFLOW);
        if ((localStatus == SSLEngineResult.Status.CLOSED) && (!paramBoolean))
        {
          closed = true;
          return localWrapperResult;
        }
        if (result.bytesProduced() > 0)
        {
          wrap_dst.flip();
          int i = wrap_dst.remaining();
          assert (i == result.bytesProduced());
          while (i > 0) {
            i -= chan.write(wrap_dst);
          }
        }
      }
      return localWrapperResult;
    }
    
    SSLStreams.WrapperResult recvAndUnwrap(ByteBuffer paramByteBuffer)
      throws IOException
    {
      SSLEngineResult.Status localStatus = SSLEngineResult.Status.OK;
      SSLStreams.WrapperResult localWrapperResult = new SSLStreams.WrapperResult(SSLStreams.this);
      buf = paramByteBuffer;
      if (closed) {
        throw new IOException("Engine is closed");
      }
      int i;
      if (u_remaining > 0)
      {
        unwrap_src.compact();
        unwrap_src.flip();
        i = 0;
      }
      else
      {
        unwrap_src.clear();
        i = 1;
      }
      synchronized (unwrapLock)
      {
        do
        {
          if (i != 0)
          {
            int j;
            do
            {
              j = chan.read(unwrap_src);
            } while (j == 0);
            if (j == -1) {
              throw new IOException("connection closed for reading");
            }
            unwrap_src.flip();
          }
          result = engine.unwrap(unwrap_src, buf);
          localStatus = result.getStatus();
          if (localStatus == SSLEngineResult.Status.BUFFER_UNDERFLOW)
          {
            if (unwrap_src.limit() == unwrap_src.capacity())
            {
              unwrap_src = SSLStreams.this.realloc(unwrap_src, false, SSLStreams.BufType.PACKET);
            }
            else
            {
              unwrap_src.position(unwrap_src.limit());
              unwrap_src.limit(unwrap_src.capacity());
            }
            i = 1;
          }
          else if (localStatus == SSLEngineResult.Status.BUFFER_OVERFLOW)
          {
            buf = SSLStreams.this.realloc(buf, true, SSLStreams.BufType.APPLICATION);
            i = 0;
          }
          else if (localStatus == SSLEngineResult.Status.CLOSED)
          {
            closed = true;
            buf.flip();
            return localWrapperResult;
          }
        } while (localStatus != SSLEngineResult.Status.OK);
      }
      u_remaining = unwrap_src.remaining();
      return localWrapperResult;
    }
  }
  
  class InputStream
    extends InputStream
  {
    ByteBuffer bbuf = SSLStreams.this.allocate(SSLStreams.BufType.APPLICATION);
    boolean closed = false;
    boolean eof = false;
    boolean needData = true;
    byte[] single = new byte[1];
    
    InputStream() {}
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (closed) {
        throw new IOException("SSL stream is closed");
      }
      if (eof) {
        return 0;
      }
      int i = 0;
      if (!needData)
      {
        i = bbuf.remaining();
        needData = (i == 0);
      }
      if (needData)
      {
        bbuf.clear();
        SSLStreams.WrapperResult localWrapperResult = recvData(bbuf);
        bbuf = (buf == bbuf ? bbuf : buf);
        if ((i = bbuf.remaining()) == 0)
        {
          eof = true;
          return 0;
        }
        needData = false;
      }
      if (paramInt2 > i) {
        paramInt2 = i;
      }
      bbuf.get(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt2;
    }
    
    public int available()
      throws IOException
    {
      return bbuf.remaining();
    }
    
    public boolean markSupported()
    {
      return false;
    }
    
    public void reset()
      throws IOException
    {
      throw new IOException("mark/reset not supported");
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      int i = (int)paramLong;
      if (closed) {
        throw new IOException("SSL stream is closed");
      }
      if (eof) {
        return 0L;
      }
      int j = i;
      while (i > 0)
      {
        if (bbuf.remaining() >= i)
        {
          bbuf.position(bbuf.position() + i);
          return j;
        }
        i -= bbuf.remaining();
        bbuf.clear();
        SSLStreams.WrapperResult localWrapperResult = recvData(bbuf);
        bbuf = (buf == bbuf ? bbuf : buf);
      }
      return j;
    }
    
    public void close()
      throws IOException
    {
      eof = true;
      engine.closeInbound();
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read()
      throws IOException
    {
      int i = read(single, 0, 1);
      if (i == 0) {
        return -1;
      }
      return single[0] & 0xFF;
    }
  }
  
  class OutputStream
    extends OutputStream
  {
    ByteBuffer buf = SSLStreams.this.allocate(SSLStreams.BufType.APPLICATION);
    boolean closed = false;
    byte[] single = new byte[1];
    
    OutputStream() {}
    
    public void write(int paramInt)
      throws IOException
    {
      single[0] = ((byte)paramInt);
      write(single, 0, 1);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      write(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (closed) {
        throw new IOException("output stream is closed");
      }
      while (paramInt2 > 0)
      {
        int i = paramInt2 > buf.capacity() ? buf.capacity() : paramInt2;
        buf.clear();
        buf.put(paramArrayOfByte, paramInt1, i);
        paramInt2 -= i;
        paramInt1 += i;
        buf.flip();
        SSLStreams.WrapperResult localWrapperResult = sendData(buf);
        if (result.getStatus() == SSLEngineResult.Status.CLOSED)
        {
          closed = true;
          if (paramInt2 > 0) {
            throw new IOException("output stream is closed");
          }
        }
      }
    }
    
    public void flush()
      throws IOException
    {}
    
    public void close()
      throws IOException
    {
      SSLStreams.WrapperResult localWrapperResult = null;
      engine.closeOutbound();
      closed = true;
      SSLEngineResult.HandshakeStatus localHandshakeStatus = SSLEngineResult.HandshakeStatus.NEED_WRAP;
      buf.clear();
      while (localHandshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP)
      {
        localWrapperResult = wrapper.wrapAndSend(buf);
        localHandshakeStatus = result.getHandshakeStatus();
      }
      assert (result.getStatus() == SSLEngineResult.Status.CLOSED);
    }
  }
  
  class Parameters
    extends HttpsParameters
  {
    InetSocketAddress addr;
    HttpsConfigurator cfg;
    SSLParameters params;
    
    Parameters(HttpsConfigurator paramHttpsConfigurator, InetSocketAddress paramInetSocketAddress)
    {
      addr = paramInetSocketAddress;
      cfg = paramHttpsConfigurator;
    }
    
    public InetSocketAddress getClientAddress()
    {
      return addr;
    }
    
    public HttpsConfigurator getHttpsConfigurator()
    {
      return cfg;
    }
    
    public void setSSLParameters(SSLParameters paramSSLParameters)
    {
      params = paramSSLParameters;
    }
    
    SSLParameters getSSLParameters()
    {
      return params;
    }
  }
  
  class WrapperResult
  {
    SSLEngineResult result;
    ByteBuffer buf;
    
    WrapperResult() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\SSLStreams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */