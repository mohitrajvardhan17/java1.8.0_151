package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;

public class ServerSocketAdaptor
  extends ServerSocket
{
  private final ServerSocketChannelImpl ssc;
  private volatile int timeout = 0;
  
  public static ServerSocket create(ServerSocketChannelImpl paramServerSocketChannelImpl)
  {
    try
    {
      return new ServerSocketAdaptor(paramServerSocketChannelImpl);
    }
    catch (IOException localIOException)
    {
      throw new Error(localIOException);
    }
  }
  
  private ServerSocketAdaptor(ServerSocketChannelImpl paramServerSocketChannelImpl)
    throws IOException
  {
    ssc = paramServerSocketChannelImpl;
  }
  
  public void bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    bind(paramSocketAddress, 50);
  }
  
  public void bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    if (paramSocketAddress == null) {
      paramSocketAddress = new InetSocketAddress(0);
    }
    try
    {
      ssc.bind(paramSocketAddress, paramInt);
    }
    catch (Exception localException)
    {
      Net.translateException(localException);
    }
  }
  
  public InetAddress getInetAddress()
  {
    if (!ssc.isBound()) {
      return null;
    }
    return Net.getRevealedLocalAddress(ssc.localAddress()).getAddress();
  }
  
  public int getLocalPort()
  {
    if (!ssc.isBound()) {
      return -1;
    }
    return Net.asInetSocketAddress(ssc.localAddress()).getPort();
  }
  
  /* Error */
  public java.net.Socket accept()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   4: invokevirtual 229	sun/nio/ch/ServerSocketChannelImpl:blockingLock	()Ljava/lang/Object;
    //   7: dup
    //   8: astore_1
    //   9: monitorenter
    //   10: aload_0
    //   11: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   14: invokevirtual 226	sun/nio/ch/ServerSocketChannelImpl:isBound	()Z
    //   17: ifne +11 -> 28
    //   20: new 117	java/nio/channels/IllegalBlockingModeException
    //   23: dup
    //   24: invokespecial 213	java/nio/channels/IllegalBlockingModeException:<init>	()V
    //   27: athrow
    //   28: aload_0
    //   29: getfield 190	sun/nio/ch/ServerSocketAdaptor:timeout	I
    //   32: ifne +40 -> 72
    //   35: aload_0
    //   36: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   39: invokevirtual 232	sun/nio/ch/ServerSocketChannelImpl:accept	()Ljava/nio/channels/SocketChannel;
    //   42: astore_2
    //   43: aload_2
    //   44: ifnonnull +21 -> 65
    //   47: aload_0
    //   48: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   51: invokevirtual 225	sun/nio/ch/ServerSocketChannelImpl:isBlocking	()Z
    //   54: ifne +11 -> 65
    //   57: new 117	java/nio/channels/IllegalBlockingModeException
    //   60: dup
    //   61: invokespecial 213	java/nio/channels/IllegalBlockingModeException:<init>	()V
    //   64: athrow
    //   65: aload_2
    //   66: invokevirtual 214	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
    //   69: aload_1
    //   70: monitorexit
    //   71: areturn
    //   72: aload_0
    //   73: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   76: iconst_0
    //   77: invokevirtual 231	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   80: pop
    //   81: aload_0
    //   82: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   85: invokevirtual 232	sun/nio/ch/ServerSocketChannelImpl:accept	()Ljava/nio/channels/SocketChannel;
    //   88: dup
    //   89: astore_2
    //   90: ifnull +31 -> 121
    //   93: aload_2
    //   94: invokevirtual 214	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
    //   97: astore_3
    //   98: aload_0
    //   99: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   102: invokevirtual 227	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
    //   105: ifeq +12 -> 117
    //   108: aload_0
    //   109: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   112: iconst_1
    //   113: invokevirtual 231	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   116: pop
    //   117: aload_1
    //   118: monitorexit
    //   119: aload_3
    //   120: areturn
    //   121: aload_0
    //   122: getfield 190	sun/nio/ch/ServerSocketAdaptor:timeout	I
    //   125: i2l
    //   126: lstore_3
    //   127: aload_0
    //   128: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   131: invokevirtual 227	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
    //   134: ifne +11 -> 145
    //   137: new 116	java/nio/channels/ClosedChannelException
    //   140: dup
    //   141: invokespecial 212	java/nio/channels/ClosedChannelException:<init>	()V
    //   144: athrow
    //   145: invokestatic 206	java/lang/System:currentTimeMillis	()J
    //   148: lstore 5
    //   150: aload_0
    //   151: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   154: getstatic 189	sun/nio/ch/Net:POLLIN	S
    //   157: lload_3
    //   158: invokevirtual 228	sun/nio/ch/ServerSocketChannelImpl:poll	(IJ)I
    //   161: istore 7
    //   163: iload 7
    //   165: ifle +45 -> 210
    //   168: aload_0
    //   169: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   172: invokevirtual 232	sun/nio/ch/ServerSocketChannelImpl:accept	()Ljava/nio/channels/SocketChannel;
    //   175: dup
    //   176: astore_2
    //   177: ifnull +33 -> 210
    //   180: aload_2
    //   181: invokevirtual 214	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
    //   184: astore 8
    //   186: aload_0
    //   187: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   190: invokevirtual 227	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
    //   193: ifeq +12 -> 205
    //   196: aload_0
    //   197: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   200: iconst_1
    //   201: invokevirtual 231	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   204: pop
    //   205: aload_1
    //   206: monitorexit
    //   207: aload 8
    //   209: areturn
    //   210: lload_3
    //   211: invokestatic 206	java/lang/System:currentTimeMillis	()J
    //   214: lload 5
    //   216: lsub
    //   217: lsub
    //   218: lstore_3
    //   219: lload_3
    //   220: lconst_0
    //   221: lcmp
    //   222: ifgt +11 -> 233
    //   225: new 114	java/net/SocketTimeoutException
    //   228: dup
    //   229: invokespecial 211	java/net/SocketTimeoutException:<init>	()V
    //   232: athrow
    //   233: goto -106 -> 127
    //   236: astore 9
    //   238: aload_0
    //   239: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   242: invokevirtual 227	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
    //   245: ifeq +12 -> 257
    //   248: aload_0
    //   249: getfield 192	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
    //   252: iconst_1
    //   253: invokevirtual 231	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   256: pop
    //   257: aload 9
    //   259: athrow
    //   260: astore_2
    //   261: aload_2
    //   262: invokestatic 215	sun/nio/ch/Net:translateException	(Ljava/lang/Exception;)V
    //   265: getstatic 191	sun/nio/ch/ServerSocketAdaptor:$assertionsDisabled	Z
    //   268: ifne +11 -> 279
    //   271: new 99	java/lang/AssertionError
    //   274: dup
    //   275: invokespecial 193	java/lang/AssertionError:<init>	()V
    //   278: athrow
    //   279: aconst_null
    //   280: aload_1
    //   281: monitorexit
    //   282: areturn
    //   283: astore 10
    //   285: aload_1
    //   286: monitorexit
    //   287: aload 10
    //   289: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	290	0	this	ServerSocketAdaptor
    //   8	278	1	Ljava/lang/Object;	Object
    //   42	139	2	localSocketChannel	java.nio.channels.SocketChannel
    //   260	2	2	localException	Exception
    //   97	23	3	localSocket1	java.net.Socket
    //   126	94	3	l1	long
    //   148	67	5	l2	long
    //   161	3	7	i	int
    //   184	24	8	localSocket2	java.net.Socket
    //   236	22	9	localObject1	Object
    //   283	5	10	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   81	98	236	finally
    //   121	186	236	finally
    //   210	238	236	finally
    //   28	69	260	java/lang/Exception
    //   72	117	260	java/lang/Exception
    //   121	205	260	java/lang/Exception
    //   210	260	260	java/lang/Exception
    //   10	71	283	finally
    //   72	119	283	finally
    //   121	207	283	finally
    //   210	282	283	finally
    //   283	287	283	finally
  }
  
  public void close()
    throws IOException
  {
    ssc.close();
  }
  
  public ServerSocketChannel getChannel()
  {
    return ssc;
  }
  
  public boolean isBound()
  {
    return ssc.isBound();
  }
  
  public boolean isClosed()
  {
    return !ssc.isOpen();
  }
  
  public void setSoTimeout(int paramInt)
    throws SocketException
  {
    timeout = paramInt;
  }
  
  public int getSoTimeout()
    throws SocketException
  {
    return timeout;
  }
  
  public void setReuseAddress(boolean paramBoolean)
    throws SocketException
  {
    try
    {
      ssc.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf(paramBoolean));
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
  }
  
  public boolean getReuseAddress()
    throws SocketException
  {
    try
    {
      return ((Boolean)ssc.getOption(StandardSocketOptions.SO_REUSEADDR)).booleanValue();
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
    return false;
  }
  
  public String toString()
  {
    if (!isBound()) {
      return "ServerSocket[unbound]";
    }
    return "ServerSocket[addr=" + getInetAddress() + ",localport=" + getLocalPort() + "]";
  }
  
  public void setReceiveBufferSize(int paramInt)
    throws SocketException
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("size cannot be 0 or negative");
    }
    try
    {
      ssc.setOption(StandardSocketOptions.SO_RCVBUF, Integer.valueOf(paramInt));
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
  }
  
  public int getReceiveBufferSize()
    throws SocketException
  {
    try
    {
      return ((Integer)ssc.getOption(StandardSocketOptions.SO_RCVBUF)).intValue();
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\ServerSocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */