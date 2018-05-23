package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_0
  extends MessageBase
{
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
  int magic = 0;
  GIOPVersion GIOP_version = null;
  boolean byte_order = false;
  byte message_type = 0;
  int message_size = 0;
  
  Message_1_0() {}
  
  Message_1_0(int paramInt1, boolean paramBoolean, byte paramByte, int paramInt2)
  {
    magic = paramInt1;
    GIOP_version = GIOPVersion.V1_0;
    byte_order = paramBoolean;
    message_type = paramByte;
    message_size = paramInt2;
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOP_version;
  }
  
  public int getType()
  {
    return message_type;
  }
  
  public int getSize()
  {
    return message_size;
  }
  
  public boolean isLittleEndian()
  {
    return byte_order;
  }
  
  public boolean moreFragmentsToFollow()
  {
    return false;
  }
  
  public void setSize(ByteBuffer paramByteBuffer, int paramInt)
  {
    message_size = paramInt;
    int i = paramInt - 12;
    if (!isLittleEndian())
    {
      paramByteBuffer.put(8, (byte)(i >>> 24 & 0xFF));
      paramByteBuffer.put(9, (byte)(i >>> 16 & 0xFF));
      paramByteBuffer.put(10, (byte)(i >>> 8 & 0xFF));
      paramByteBuffer.put(11, (byte)(i >>> 0 & 0xFF));
    }
    else
    {
      paramByteBuffer.put(8, (byte)(i >>> 0 & 0xFF));
      paramByteBuffer.put(9, (byte)(i >>> 8 & 0xFF));
      paramByteBuffer.put(10, (byte)(i >>> 16 & 0xFF));
      paramByteBuffer.put(11, (byte)(i >>> 24 & 0xFF));
    }
  }
  
  public FragmentMessage createFragmentMessage()
  {
    throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void read(InputStream paramInputStream) {}
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_long(magic);
    nullCheck(GIOP_version);
    GIOP_version.write(paramOutputStream);
    paramOutputStream.write_boolean(byte_order);
    paramOutputStream.write_octet(message_type);
    paramOutputStream.write_ulong(message_size);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\Message_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */