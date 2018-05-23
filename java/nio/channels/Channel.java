package java.nio.channels;

import java.io.Closeable;
import java.io.IOException;

public abstract interface Channel
  extends Closeable
{
  public abstract boolean isOpen();
  
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */