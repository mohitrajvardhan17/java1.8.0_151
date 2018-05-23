package com.sun.xml.internal.ws.encoding.xml;

import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public final class XMLCodec
  implements Codec
{
  public static final String XML_APPLICATION_MIME_TYPE = "application/xml";
  public static final String XML_TEXT_MIME_TYPE = "text/xml";
  private static final ContentType contentType = new ContentTypeImpl("text/xml");
  private WSFeatureList features;
  
  public XMLCodec(WSFeatureList paramWSFeatureList)
  {
    features = paramWSFeatureList;
  }
  
  public String getMimeType()
  {
    return "application/xml";
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    return contentType;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
  {
    String str = (String)invocationProperties.get("com.sun.jaxws.rest.contenttype");
    XMLStreamWriter localXMLStreamWriter = null;
    if ((str != null) && (str.length() > 0)) {
      localXMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream, str);
    } else {
      localXMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream);
    }
    try
    {
      if (paramPacket.getMessage().hasPayload())
      {
        localXMLStreamWriter.writeStartDocument();
        paramPacket.getMessage().writePayloadTo(localXMLStreamWriter);
        localXMLStreamWriter.flush();
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
    return contentType;
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  public Codec copy()
  {
    return this;
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    Message localMessage = XMLMessage.create(paramString, paramInputStream, features);
    paramPacket.setMessage(localMessage);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket)
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\xml\XMLCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */