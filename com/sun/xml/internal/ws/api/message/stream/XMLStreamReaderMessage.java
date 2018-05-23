package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderMessage
  extends StreamBasedMessage
{
  public final XMLStreamReader msg;
  
  public XMLStreamReaderMessage(Packet paramPacket, XMLStreamReader paramXMLStreamReader)
  {
    super(paramPacket);
    msg = paramXMLStreamReader;
  }
  
  public XMLStreamReaderMessage(Packet paramPacket, AttachmentSet paramAttachmentSet, XMLStreamReader paramXMLStreamReader)
  {
    super(paramPacket, paramAttachmentSet);
    msg = paramXMLStreamReader;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\stream\XMLStreamReaderMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */