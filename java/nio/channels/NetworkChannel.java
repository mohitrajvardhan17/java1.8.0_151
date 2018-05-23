package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.util.Set;

public abstract interface NetworkChannel
  extends Channel
{
  public abstract NetworkChannel bind(SocketAddress paramSocketAddress)
    throws IOException;
  
  public abstract SocketAddress getLocalAddress()
    throws IOException;
  
  public abstract <T> NetworkChannel setOption(SocketOption<T> paramSocketOption, T paramT)
    throws IOException;
  
  public abstract <T> T getOption(SocketOption<T> paramSocketOption)
    throws IOException;
  
  public abstract Set<SocketOption<?>> supportedOptions();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\NetworkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */