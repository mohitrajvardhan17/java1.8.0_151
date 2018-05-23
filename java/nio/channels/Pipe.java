package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class Pipe
{
  protected Pipe() {}
  
  public abstract SourceChannel source();
  
  public abstract SinkChannel sink();
  
  public static Pipe open()
    throws IOException
  {
    return SelectorProvider.provider().openPipe();
  }
  
  public static abstract class SinkChannel
    extends AbstractSelectableChannel
    implements WritableByteChannel, GatheringByteChannel
  {
    protected SinkChannel(SelectorProvider paramSelectorProvider)
    {
      super();
    }
    
    public final int validOps()
    {
      return 4;
    }
  }
  
  public static abstract class SourceChannel
    extends AbstractSelectableChannel
    implements ReadableByteChannel, ScatteringByteChannel
  {
    protected SourceChannel(SelectorProvider paramSelectorProvider)
    {
      super();
    }
    
    public final int validOps()
    {
      return 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\Pipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */