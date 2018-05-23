package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.Random;

class PipeImpl
  extends Pipe
{
  private static final int NUM_SECRET_BYTES = 16;
  private static final Random RANDOM_NUMBER_GENERATOR = new SecureRandom();
  private Pipe.SourceChannel source;
  private Pipe.SinkChannel sink;
  
  PipeImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new Initializer(paramSelectorProvider, null));
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getCause());
    }
  }
  
  public Pipe.SourceChannel source()
  {
    return source;
  }
  
  public Pipe.SinkChannel sink()
  {
    return sink;
  }
  
  private class Initializer
    implements PrivilegedExceptionAction<Void>
  {
    private final SelectorProvider sp;
    private IOException ioe = null;
    
    private Initializer(SelectorProvider paramSelectorProvider)
    {
      sp = paramSelectorProvider;
    }
    
    public Void run()
      throws IOException
    {
      LoopbackConnector localLoopbackConnector = new LoopbackConnector(null);
      localLoopbackConnector.run();
      if ((ioe instanceof ClosedByInterruptException))
      {
        ioe = null;
        Thread local1 = new Thread(localLoopbackConnector)
        {
          public void interrupt() {}
        };
        local1.start();
        for (;;)
        {
          try
          {
            local1.join();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        Thread.currentThread().interrupt();
      }
      if (ioe != null) {
        throw new IOException("Unable to establish loopback connection", ioe);
      }
      return null;
    }
    
    private class LoopbackConnector
      implements Runnable
    {
      private LoopbackConnector() {}
      
      public void run()
      {
        ServerSocketChannel localServerSocketChannel = null;
        SocketChannel localSocketChannel1 = null;
        SocketChannel localSocketChannel2 = null;
        try
        {
          ByteBuffer localByteBuffer1 = ByteBuffer.allocate(16);
          ByteBuffer localByteBuffer2 = ByteBuffer.allocate(16);
          InetAddress localInetAddress = InetAddress.getByName("127.0.0.1");
          assert (localInetAddress.isLoopbackAddress());
          InetSocketAddress localInetSocketAddress = null;
          for (;;)
          {
            if ((localServerSocketChannel == null) || (!localServerSocketChannel.isOpen()))
            {
              localServerSocketChannel = ServerSocketChannel.open();
              localServerSocketChannel.socket().bind(new InetSocketAddress(localInetAddress, 0));
              localInetSocketAddress = new InetSocketAddress(localInetAddress, localServerSocketChannel.socket().getLocalPort());
            }
            localSocketChannel1 = SocketChannel.open(localInetSocketAddress);
            PipeImpl.RANDOM_NUMBER_GENERATOR.nextBytes(localByteBuffer1.array());
            do
            {
              localSocketChannel1.write(localByteBuffer1);
            } while (localByteBuffer1.hasRemaining());
            localByteBuffer1.rewind();
            localSocketChannel2 = localServerSocketChannel.accept();
            do
            {
              localSocketChannel2.read(localByteBuffer2);
            } while (localByteBuffer2.hasRemaining());
            localByteBuffer2.rewind();
            if (localByteBuffer2.equals(localByteBuffer1)) {
              break;
            }
            localSocketChannel2.close();
            localSocketChannel1.close();
          }
          source = new SourceChannelImpl(sp, localSocketChannel1);
          sink = new SinkChannelImpl(sp, localSocketChannel2);
          return;
        }
        catch (IOException localIOException2)
        {
          try
          {
            if (localSocketChannel1 != null) {
              localSocketChannel1.close();
            }
            if (localSocketChannel2 != null) {
              localSocketChannel2.close();
            }
          }
          catch (IOException localIOException4) {}
          ioe = localIOException2;
        }
        finally
        {
          try
          {
            if (localServerSocketChannel != null) {
              localServerSocketChannel.close();
            }
          }
          catch (IOException localIOException5) {}
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\PipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */