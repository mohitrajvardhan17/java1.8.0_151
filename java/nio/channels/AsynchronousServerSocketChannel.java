package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.Future;

public abstract class AsynchronousServerSocketChannel
  implements AsynchronousChannel, NetworkChannel
{
  private final AsynchronousChannelProvider provider;
  
  protected AsynchronousServerSocketChannel(AsynchronousChannelProvider paramAsynchronousChannelProvider)
  {
    provider = paramAsynchronousChannelProvider;
  }
  
  public final AsynchronousChannelProvider provider()
  {
    return provider;
  }
  
  public static AsynchronousServerSocketChannel open(AsynchronousChannelGroup paramAsynchronousChannelGroup)
    throws IOException
  {
    AsynchronousChannelProvider localAsynchronousChannelProvider = paramAsynchronousChannelGroup == null ? AsynchronousChannelProvider.provider() : paramAsynchronousChannelGroup.provider();
    return localAsynchronousChannelProvider.openAsynchronousServerSocketChannel(paramAsynchronousChannelGroup);
  }
  
  public static AsynchronousServerSocketChannel open()
    throws IOException
  {
    return open(null);
  }
  
  public final AsynchronousServerSocketChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    return bind(paramSocketAddress, 0);
  }
  
  public abstract AsynchronousServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException;
  
  public abstract <T> AsynchronousServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
    throws IOException;
  
  public abstract <A> void accept(A paramA, CompletionHandler<AsynchronousSocketChannel, ? super A> paramCompletionHandler);
  
  public abstract Future<AsynchronousSocketChannel> accept();
  
  public abstract SocketAddress getLocalAddress()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\AsynchronousServerSocketChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */