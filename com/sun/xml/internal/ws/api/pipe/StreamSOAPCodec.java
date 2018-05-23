package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.stream.XMLStreamReader;

public abstract interface StreamSOAPCodec
  extends Codec
{
  @NotNull
  public abstract Message decode(@NotNull XMLStreamReader paramXMLStreamReader);
  
  @NotNull
  public abstract Message decode(@NotNull XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\StreamSOAPCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */