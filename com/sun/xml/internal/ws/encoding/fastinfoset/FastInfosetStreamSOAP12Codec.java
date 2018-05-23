package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;
import javax.xml.stream.XMLStreamReader;

final class FastInfosetStreamSOAP12Codec
  extends FastInfosetStreamSOAPCodec
{
  FastInfosetStreamSOAP12Codec(StreamSOAPCodec paramStreamSOAPCodec, boolean paramBoolean)
  {
    super(paramStreamSOAPCodec, SOAPVersion.SOAP_12, paramBoolean, paramBoolean ? "application/vnd.sun.stateful.soap+fastinfoset" : "application/soap+fastinfoset");
  }
  
  private FastInfosetStreamSOAP12Codec(FastInfosetStreamSOAPCodec paramFastInfosetStreamSOAPCodec)
  {
    super(paramFastInfosetStreamSOAPCodec);
  }
  
  public Codec copy()
  {
    return new FastInfosetStreamSOAP12Codec(this);
  }
  
  protected final StreamHeader createHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer)
  {
    return new StreamHeader12(paramXMLStreamReader, paramXMLStreamBuffer);
  }
  
  protected ContentType getContentType(String paramString)
  {
    if (paramString == null) {
      return _defaultContentType;
    }
    return new ContentTypeImpl(_defaultContentType.getContentType() + ";action=\"" + paramString + "\"");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamSOAP12Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */