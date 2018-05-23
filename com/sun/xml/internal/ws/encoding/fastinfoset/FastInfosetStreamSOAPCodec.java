package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class FastInfosetStreamSOAPCodec
  implements Codec
{
  private static final FastInfosetStreamReaderFactory READER_FACTORY = ;
  private StAXDocumentParser _statefulParser;
  private StAXDocumentSerializer _serializer;
  private final StreamSOAPCodec _soapCodec;
  private final boolean _retainState;
  protected final ContentType _defaultContentType;
  
  FastInfosetStreamSOAPCodec(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion, boolean paramBoolean, String paramString)
  {
    _soapCodec = paramStreamSOAPCodec;
    _retainState = paramBoolean;
    _defaultContentType = new ContentTypeImpl(paramString);
  }
  
  FastInfosetStreamSOAPCodec(FastInfosetStreamSOAPCodec paramFastInfosetStreamSOAPCodec)
  {
    _soapCodec = ((StreamSOAPCodec)_soapCodec.copy());
    _retainState = _retainState;
    _defaultContentType = _defaultContentType;
  }
  
  public String getMimeType()
  {
    return _defaultContentType.getContentType();
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    return getContentType(soapAction);
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
  {
    if (paramPacket.getMessage() != null)
    {
      XMLStreamWriter localXMLStreamWriter = getXMLStreamWriter(paramOutputStream);
      try
      {
        paramPacket.getMessage().writeTo(localXMLStreamWriter);
        localXMLStreamWriter.flush();
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
    }
    return getContentType(soapAction);
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    paramPacket.setMessage(_soapCodec.decode(getXMLStreamReader(paramInputStream)));
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket)
  {
    throw new UnsupportedOperationException();
  }
  
  protected abstract StreamHeader createHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer);
  
  protected abstract ContentType getContentType(String paramString);
  
  private XMLStreamWriter getXMLStreamWriter(OutputStream paramOutputStream)
  {
    if (_serializer != null)
    {
      _serializer.setOutputStream(paramOutputStream);
      return _serializer;
    }
    return _serializer = FastInfosetCodec.createNewStreamWriter(paramOutputStream, _retainState);
  }
  
  private XMLStreamReader getXMLStreamReader(InputStream paramInputStream)
  {
    if (_retainState)
    {
      if (_statefulParser != null)
      {
        _statefulParser.setInputStream(paramInputStream);
        return _statefulParser;
      }
      return _statefulParser = FastInfosetCodec.createNewStreamReader(paramInputStream, _retainState);
    }
    return READER_FACTORY.doCreate(null, paramInputStream, false);
  }
  
  public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion)
  {
    return create(paramStreamSOAPCodec, paramSOAPVersion, false);
  }
  
  public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion, boolean paramBoolean)
  {
    if (paramSOAPVersion == null) {
      throw new IllegalArgumentException();
    }
    switch (paramSOAPVersion)
    {
    case SOAP_11: 
      return new FastInfosetStreamSOAP11Codec(paramStreamSOAPCodec, paramBoolean);
    case SOAP_12: 
      return new FastInfosetStreamSOAP12Codec(paramStreamSOAPCodec, paramBoolean);
    }
    throw new AssertionError();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamSOAPCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */