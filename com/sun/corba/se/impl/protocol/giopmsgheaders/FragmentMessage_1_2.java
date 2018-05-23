package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FragmentMessage_1_2
  extends Message_1_2
  implements FragmentMessage
{
  FragmentMessage_1_2() {}
  
  FragmentMessage_1_2(int paramInt)
  {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)7, 0);
    message_type = 7;
    request_id = paramInt;
  }
  
  FragmentMessage_1_2(Message_1_1 paramMessage_1_1)
  {
    magic = magic;
    GIOP_version = GIOP_version;
    flags = flags;
    message_type = 7;
    message_size = 0;
    switch (message_type)
    {
    case 0: 
      request_id = ((RequestMessage)paramMessage_1_1).getRequestId();
      break;
    case 1: 
      request_id = ((ReplyMessage)paramMessage_1_1).getRequestId();
      break;
    case 3: 
      request_id = ((LocateRequestMessage)paramMessage_1_1).getRequestId();
      break;
    case 4: 
      request_id = ((LocateReplyMessage)paramMessage_1_1).getRequestId();
      break;
    case 7: 
      request_id = ((FragmentMessage)paramMessage_1_1).getRequestId();
    }
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public int getHeaderLength()
  {
    return 16;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\FragmentMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */