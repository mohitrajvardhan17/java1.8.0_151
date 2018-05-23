package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface WritableByteChannel
  extends Channel
{
  public abstract int write(ByteBuffer paramByteBuffer)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\WritableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */