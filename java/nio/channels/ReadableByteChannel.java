package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ReadableByteChannel
  extends Channel
{
  public abstract int read(ByteBuffer paramByteBuffer)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\ReadableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */