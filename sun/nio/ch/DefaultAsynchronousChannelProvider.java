package sun.nio.ch;

import java.nio.channels.spi.AsynchronousChannelProvider;

public class DefaultAsynchronousChannelProvider
{
  private DefaultAsynchronousChannelProvider() {}
  
  public static AsynchronousChannelProvider create()
  {
    return new WindowsAsynchronousChannelProvider();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\DefaultAsynchronousChannelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */