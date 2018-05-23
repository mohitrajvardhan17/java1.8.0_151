package sun.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ByteBuffered
{
  public abstract ByteBuffer getByteBuffer()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ByteBuffered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */