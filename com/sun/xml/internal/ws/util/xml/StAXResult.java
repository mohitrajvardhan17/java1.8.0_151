package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

public class StAXResult
  extends SAXResult
{
  public StAXResult(XMLStreamWriter paramXMLStreamWriter)
  {
    if (paramXMLStreamWriter == null) {
      throw new IllegalArgumentException();
    }
    super.setHandler(new ContentHandlerToXMLStreamWriter(paramXMLStreamWriter));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\StAXResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */