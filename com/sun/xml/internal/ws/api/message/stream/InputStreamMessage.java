package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.InputStream;

public class InputStreamMessage
  extends StreamBasedMessage
{
  public final String contentType;
  public final InputStream msg;
  
  public InputStreamMessage(Packet paramPacket, String paramString, InputStream paramInputStream)
  {
    super(paramPacket);
    contentType = paramString;
    msg = paramInputStream;
  }
  
  public InputStreamMessage(Packet paramPacket, AttachmentSet paramAttachmentSet, String paramString, InputStream paramInputStream)
  {
    super(paramPacket, paramAttachmentSet);
    contentType = paramString;
    msg = paramInputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\stream\InputStreamMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */