package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class CancelRequestMessage_1_0
  extends Message_1_0
  implements CancelRequestMessage
{
  private int request_id = 0;
  
  CancelRequestMessage_1_0() {}
  
  CancelRequestMessage_1_0(int paramInt)
  {
    super(1195986768, false, (byte)2, 4);
    request_id = paramInt;
  }
  
  public int getRequestId()
  {
    return request_id;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\CancelRequestMessage_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */