package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FragmentMessage_1_1
  extends Message_1_1
  implements FragmentMessage
{
  FragmentMessage_1_1() {}
  
  FragmentMessage_1_1(Message_1_1 paramMessage_1_1)
  {
    magic = magic;
    GIOP_version = GIOP_version;
    flags = flags;
    message_type = 7;
    message_size = 0;
  }
  
  public int getRequestId()
  {
    return -1;
  }
  
  public int getHeaderLength()
  {
    return 12;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\FragmentMessage_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */