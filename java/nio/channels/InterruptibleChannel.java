package java.nio.channels;

import java.io.IOException;

public abstract interface InterruptibleChannel
  extends Channel
{
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\InterruptibleChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */