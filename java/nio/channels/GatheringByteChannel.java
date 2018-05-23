package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface GatheringByteChannel
  extends WritableByteChannel
{
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\GatheringByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */