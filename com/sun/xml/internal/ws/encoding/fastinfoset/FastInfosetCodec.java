package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public class FastInfosetCodec
  implements Codec
{
  private static final int DEFAULT_INDEXED_STRING_SIZE_LIMIT = 32;
  private static final int DEFAULT_INDEXED_STRING_MEMORY_LIMIT = 4194304;
  private StAXDocumentParser _parser;
  private StAXDocumentSerializer _serializer;
  private final boolean _retainState;
  private final ContentType _contentType;
  
  FastInfosetCodec(boolean paramBoolean)
  {
    _retainState = paramBoolean;
    _contentType = (paramBoolean ? new ContentTypeImpl("application/vnd.sun.stateful.fastinfoset") : new ContentTypeImpl("application/fastinfoset"));
  }
  
  public String getMimeType()
  {
    return _contentType.getContentType();
  }
  
  public Codec copy()
  {
    return new FastInfosetCodec(_retainState);
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    return _contentType;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
  {
    Message localMessage = paramPacket.getMessage();
    if ((localMessage != null) && (localMessage.hasPayload()))
    {
      XMLStreamWriter localXMLStreamWriter = getXMLStreamWriter(paramOutputStream);
      try
      {
        localXMLStreamWriter.writeStartDocument();
        paramPacket.getMessage().writePayloadTo(localXMLStreamWriter);
        localXMLStreamWriter.writeEndDocument();
        localXMLStreamWriter.flush();
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
    }
    return _contentType;
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    paramInputStream = hasSomeData(paramInputStream);
    Message localMessage;
    if (paramInputStream != null) {
      localMessage = Messages.createUsingPayload(new FastInfosetSource(paramInputStream), SOAPVersion.SOAP_11);
    } else {
      localMessage = Messages.createEmpty(SOAPVersion.SOAP_11);
    }
    paramPacket.setMessage(localMessage);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket)
  {
    throw new UnsupportedOperationException();
  }
  
  private XMLStreamWriter getXMLStreamWriter(OutputStream paramOutputStream)
  {
    if (_serializer != null)
    {
      _serializer.setOutputStream(paramOutputStream);
      return _serializer;
    }
    return _serializer = createNewStreamWriter(paramOutputStream, _retainState);
  }
  
  public static FastInfosetCodec create()
  {
    return create(false);
  }
  
  public static FastInfosetCodec create(boolean paramBoolean)
  {
    return new FastInfosetCodec(paramBoolean);
  }
  
  static StAXDocumentSerializer createNewStreamWriter(OutputStream paramOutputStream, boolean paramBoolean)
  {
    return createNewStreamWriter(paramOutputStream, paramBoolean, 32, 4194304);
  }
  
  static StAXDocumentSerializer createNewStreamWriter(OutputStream paramOutputStream, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    StAXDocumentSerializer localStAXDocumentSerializer = new StAXDocumentSerializer(paramOutputStream);
    if (paramBoolean)
    {
      SerializerVocabulary localSerializerVocabulary = new SerializerVocabulary();
      localStAXDocumentSerializer.setVocabulary(localSerializerVocabulary);
      localStAXDocumentSerializer.setMinAttributeValueSize(0);
      localStAXDocumentSerializer.setMaxAttributeValueSize(paramInt1);
      localStAXDocumentSerializer.setMinCharacterContentChunkSize(0);
      localStAXDocumentSerializer.setMaxCharacterContentChunkSize(paramInt1);
      localStAXDocumentSerializer.setAttributeValueMapMemoryLimit(paramInt2);
      localStAXDocumentSerializer.setCharacterContentChunkMapMemoryLimit(paramInt2);
    }
    return localStAXDocumentSerializer;
  }
  
  static StAXDocumentParser createNewStreamReader(InputStream paramInputStream, boolean paramBoolean)
  {
    StAXDocumentParser localStAXDocumentParser = new StAXDocumentParser(paramInputStream);
    localStAXDocumentParser.setStringInterning(true);
    if (paramBoolean)
    {
      ParserVocabulary localParserVocabulary = new ParserVocabulary();
      localStAXDocumentParser.setVocabulary(localParserVocabulary);
    }
    return localStAXDocumentParser;
  }
  
  static StAXDocumentParser createNewStreamReaderRecyclable(InputStream paramInputStream, boolean paramBoolean)
  {
    FastInfosetStreamReaderRecyclable localFastInfosetStreamReaderRecyclable = new FastInfosetStreamReaderRecyclable(paramInputStream);
    localFastInfosetStreamReaderRecyclable.setStringInterning(true);
    localFastInfosetStreamReaderRecyclable.setForceStreamClose(true);
    if (paramBoolean)
    {
      ParserVocabulary localParserVocabulary = new ParserVocabulary();
      localFastInfosetStreamReaderRecyclable.setVocabulary(localParserVocabulary);
    }
    return localFastInfosetStreamReaderRecyclable;
  }
  
  private static InputStream hasSomeData(InputStream paramInputStream)
    throws IOException
  {
    if ((paramInputStream != null) && (paramInputStream.available() < 1))
    {
      if (!paramInputStream.markSupported()) {
        paramInputStream = new BufferedInputStream(paramInputStream);
      }
      paramInputStream.mark(1);
      if (paramInputStream.read() != -1) {
        paramInputStream.reset();
      } else {
        paramInputStream = null;
      }
    }
    return paramInputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */