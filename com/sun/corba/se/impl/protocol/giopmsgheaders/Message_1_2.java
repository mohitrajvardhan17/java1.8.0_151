package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_2
  extends Message_1_1
{
  protected int request_id = 0;
  
  Message_1_2() {}
  
  Message_1_2(int paramInt1, GIOPVersion paramGIOPVersion, byte paramByte1, byte paramByte2, int paramInt2)
  {
    super(paramInt1, paramGIOPVersion, paramByte1, paramByte2, paramInt2);
  }
  
  public void unmarshalRequestID(ByteBuffer paramByteBuffer)
  {
    int i;
    int j;
    int k;
    int m;
    if (!isLittleEndian())
    {
      i = paramByteBuffer.get(12) << 24 & 0xFF000000;
      j = paramByteBuffer.get(13) << 16 & 0xFF0000;
      k = paramByteBuffer.get(14) << 8 & 0xFF00;
      m = paramByteBuffer.get(15) << 0 & 0xFF;
    }
    else
    {
      i = paramByteBuffer.get(15) << 24 & 0xFF000000;
      j = paramByteBuffer.get(14) << 16 & 0xFF0000;
      k = paramByteBuffer.get(13) << 8 & 0xFF00;
      m = paramByteBuffer.get(12) << 0 & 0xFF;
    }
    request_id = (i | j | k | m);
  }
  
  public void write(OutputStream paramOutputStream)
  {
    if (encodingVersion == 0)
    {
      super.write(paramOutputStream);
      return;
    }
    GIOPVersion localGIOPVersion = GIOP_version;
    GIOP_version = GIOPVersion.getInstance((byte)13, encodingVersion);
    super.write(paramOutputStream);
    GIOP_version = localGIOPVersion;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\Message_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */