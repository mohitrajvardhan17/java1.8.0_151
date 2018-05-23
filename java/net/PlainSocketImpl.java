package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class PlainSocketImpl
  extends AbstractPlainSocketImpl
{
  private AbstractPlainSocketImpl impl;
  private static float version;
  private static boolean preferIPv4Stack = false;
  private static boolean useDualStackImpl = false;
  private static String exclBindProp;
  private static boolean exclusiveBind = true;
  
  PlainSocketImpl()
  {
    if (useDualStackImpl) {
      impl = new DualStackPlainSocketImpl(exclusiveBind);
    } else {
      impl = new TwoStacksPlainSocketImpl(exclusiveBind);
    }
  }
  
  PlainSocketImpl(FileDescriptor paramFileDescriptor)
  {
    if (useDualStackImpl) {
      impl = new DualStackPlainSocketImpl(paramFileDescriptor, exclusiveBind);
    } else {
      impl = new TwoStacksPlainSocketImpl(paramFileDescriptor, exclusiveBind);
    }
  }
  
  protected FileDescriptor getFileDescriptor()
  {
    return impl.getFileDescriptor();
  }
  
  protected InetAddress getInetAddress()
  {
    return impl.getInetAddress();
  }
  
  protected int getPort()
  {
    return impl.getPort();
  }
  
  protected int getLocalPort()
  {
    return impl.getLocalPort();
  }
  
  void setSocket(Socket paramSocket)
  {
    impl.setSocket(paramSocket);
  }
  
  Socket getSocket()
  {
    return impl.getSocket();
  }
  
  void setServerSocket(ServerSocket paramServerSocket)
  {
    impl.setServerSocket(paramServerSocket);
  }
  
  ServerSocket getServerSocket()
  {
    return impl.getServerSocket();
  }
  
  public String toString()
  {
    return impl.toString();
  }
  
  protected synchronized void create(boolean paramBoolean)
    throws IOException
  {
    impl.create(paramBoolean);
    fd = impl.fd;
  }
  
  protected void connect(String paramString, int paramInt)
    throws UnknownHostException, IOException
  {
    impl.connect(paramString, paramInt);
  }
  
  protected void connect(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    impl.connect(paramInetAddress, paramInt);
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    impl.connect(paramSocketAddress, paramInt);
  }
  
  public void setOption(int paramInt, Object paramObject)
    throws SocketException
  {
    impl.setOption(paramInt, paramObject);
  }
  
  public Object getOption(int paramInt)
    throws SocketException
  {
    return impl.getOption(paramInt);
  }
  
  synchronized void doConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException
  {
    impl.doConnect(paramInetAddress, paramInt1, paramInt2);
  }
  
  protected synchronized void bind(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    impl.bind(paramInetAddress, paramInt);
  }
  
  protected synchronized void accept(SocketImpl paramSocketImpl)
    throws IOException
  {
    if ((paramSocketImpl instanceof PlainSocketImpl))
    {
      AbstractPlainSocketImpl localAbstractPlainSocketImpl = impl;
      address = new InetAddress();
      fd = new FileDescriptor();
      impl.accept(localAbstractPlainSocketImpl);
      fd = fd;
    }
    else
    {
      impl.accept(paramSocketImpl);
    }
  }
  
  void setFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    impl.setFileDescriptor(paramFileDescriptor);
  }
  
  void setAddress(InetAddress paramInetAddress)
  {
    impl.setAddress(paramInetAddress);
  }
  
  void setPort(int paramInt)
  {
    impl.setPort(paramInt);
  }
  
  void setLocalPort(int paramInt)
  {
    impl.setLocalPort(paramInt);
  }
  
  protected synchronized InputStream getInputStream()
    throws IOException
  {
    return impl.getInputStream();
  }
  
  void setInputStream(SocketInputStream paramSocketInputStream)
  {
    impl.setInputStream(paramSocketInputStream);
  }
  
  protected synchronized OutputStream getOutputStream()
    throws IOException
  {
    return impl.getOutputStream();
  }
  
  /* Error */
  protected void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 208	java/net/PlainSocketImpl:impl	Ljava/net/AbstractPlainSocketImpl;
    //   4: invokevirtual 219	java/net/AbstractPlainSocketImpl:close	()V
    //   7: aload_0
    //   8: aconst_null
    //   9: putfield 206	java/net/PlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   12: goto +11 -> 23
    //   15: astore_1
    //   16: aload_0
    //   17: aconst_null
    //   18: putfield 206	java/net/PlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   21: aload_1
    //   22: athrow
    //   23: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	PlainSocketImpl
    //   15	7	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	7	15	finally
  }
  
  /* Error */
  void reset()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 208	java/net/PlainSocketImpl:impl	Ljava/net/AbstractPlainSocketImpl;
    //   4: invokevirtual 221	java/net/AbstractPlainSocketImpl:reset	()V
    //   7: aload_0
    //   8: aconst_null
    //   9: putfield 206	java/net/PlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   12: goto +11 -> 23
    //   15: astore_1
    //   16: aload_0
    //   17: aconst_null
    //   18: putfield 206	java/net/PlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   21: aload_1
    //   22: athrow
    //   23: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	PlainSocketImpl
    //   15	7	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	7	15	finally
  }
  
  protected void shutdownInput()
    throws IOException
  {
    impl.shutdownInput();
  }
  
  protected void shutdownOutput()
    throws IOException
  {
    impl.shutdownOutput();
  }
  
  protected void sendUrgentData(int paramInt)
    throws IOException
  {
    impl.sendUrgentData(paramInt);
  }
  
  FileDescriptor acquireFD()
  {
    return impl.acquireFD();
  }
  
  void releaseFD()
  {
    impl.releaseFD();
  }
  
  public boolean isConnectionReset()
  {
    return impl.isConnectionReset();
  }
  
  public boolean isConnectionResetPending()
  {
    return impl.isConnectionResetPending();
  }
  
  public void setConnectionReset()
  {
    impl.setConnectionReset();
  }
  
  public void setConnectionResetPending()
  {
    impl.setConnectionResetPending();
  }
  
  public boolean isClosedOrPending()
  {
    return impl.isClosedOrPending();
  }
  
  public int getTimeout()
  {
    return impl.getTimeout();
  }
  
  void socketCreate(boolean paramBoolean)
    throws IOException
  {
    impl.socketCreate(paramBoolean);
  }
  
  void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException
  {
    impl.socketConnect(paramInetAddress, paramInt1, paramInt2);
  }
  
  void socketBind(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    impl.socketBind(paramInetAddress, paramInt);
  }
  
  void socketListen(int paramInt)
    throws IOException
  {
    impl.socketListen(paramInt);
  }
  
  void socketAccept(SocketImpl paramSocketImpl)
    throws IOException
  {
    impl.socketAccept(paramSocketImpl);
  }
  
  int socketAvailable()
    throws IOException
  {
    return impl.socketAvailable();
  }
  
  void socketClose0(boolean paramBoolean)
    throws IOException
  {
    impl.socketClose0(paramBoolean);
  }
  
  void socketShutdown(int paramInt)
    throws IOException
  {
    impl.socketShutdown(paramInt);
  }
  
  void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject)
    throws SocketException
  {
    impl.socketSetOption(paramInt, paramBoolean, paramObject);
  }
  
  int socketGetOption(int paramInt, Object paramObject)
    throws SocketException
  {
    return impl.socketGetOption(paramInt, paramObject);
  }
  
  void socketSendUrgentData(int paramInt)
    throws IOException
  {
    impl.socketSendUrgentData(paramInt);
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        PlainSocketImpl.access$002(0.0F);
        try
        {
          PlainSocketImpl.access$002(Float.parseFloat(System.getProperties().getProperty("os.version")));
          PlainSocketImpl.access$102(Boolean.parseBoolean(System.getProperties().getProperty("java.net.preferIPv4Stack")));
          PlainSocketImpl.access$202(System.getProperty("sun.net.useExclusiveBind"));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError(localNumberFormatException);
          }
        }
        return null;
      }
    });
    if ((version >= 6.0D) && (!preferIPv4Stack)) {
      useDualStackImpl = true;
    }
    if (exclBindProp != null) {
      exclusiveBind = exclBindProp.length() == 0 ? true : Boolean.parseBoolean(exclBindProp);
    } else if (version < 6.0D) {
      exclusiveBind = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\PlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */