package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;

abstract class StreamBasedMessage
{
  public final Packet properties;
  public final AttachmentSet attachments;
  
  protected StreamBasedMessage(Packet paramPacket)
  {
    properties = paramPacket;
    attachments = new AttachmentSetImpl();
  }
  
  protected StreamBasedMessage(Packet paramPacket, AttachmentSet paramAttachmentSet)
  {
    properties = paramPacket;
    attachments = paramAttachmentSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\stream\StreamBasedMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */