package com.sun.xml.internal.ws.api.server;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract interface SDDocumentFilter
{
  public abstract XMLStreamWriter filter(SDDocument paramSDDocument, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\SDDocumentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */