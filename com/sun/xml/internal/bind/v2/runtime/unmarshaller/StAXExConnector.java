package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class StAXExConnector
  extends StAXStreamConnector
{
  private final XMLStreamReaderEx in;
  
  public StAXExConnector(XMLStreamReaderEx paramXMLStreamReaderEx, XmlVisitor paramXmlVisitor)
  {
    super(paramXMLStreamReaderEx, paramXmlVisitor);
    in = paramXMLStreamReaderEx;
  }
  
  protected void handleCharacters()
    throws XMLStreamException, SAXException
  {
    if (predictor.expectText())
    {
      CharSequence localCharSequence = in.getPCDATA();
      if ((localCharSequence instanceof com.sun.xml.internal.org.jvnet.staxex.Base64Data))
      {
        com.sun.xml.internal.org.jvnet.staxex.Base64Data localBase64Data = (com.sun.xml.internal.org.jvnet.staxex.Base64Data)localCharSequence;
        Base64Data localBase64Data1 = new Base64Data();
        if (!localBase64Data.hasData()) {
          localBase64Data1.set(localBase64Data.getDataHandler());
        } else {
          localBase64Data1.set(localBase64Data.get(), localBase64Data.getDataLen(), localBase64Data.getMimeType());
        }
        visitor.text(localBase64Data1);
        textReported = true;
      }
      else
      {
        buffer.append(localCharSequence);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\StAXExConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */