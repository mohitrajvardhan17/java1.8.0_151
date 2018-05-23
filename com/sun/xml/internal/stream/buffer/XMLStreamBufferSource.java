package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import java.io.ByteArrayInputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XMLStreamBufferSource
  extends SAXSource
{
  protected XMLStreamBuffer _buffer;
  protected SAXBufferProcessor _bufferProcessor;
  
  public XMLStreamBufferSource(XMLStreamBuffer paramXMLStreamBuffer)
  {
    super(new InputSource(new ByteArrayInputStream(new byte[0])));
    setXMLStreamBuffer(paramXMLStreamBuffer);
  }
  
  public XMLStreamBuffer getXMLStreamBuffer()
  {
    return _buffer;
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer)
  {
    if (paramXMLStreamBuffer == null) {
      throw new NullPointerException("buffer cannot be null");
    }
    _buffer = paramXMLStreamBuffer;
    if (_bufferProcessor != null) {
      _bufferProcessor.setBuffer(_buffer, false);
    }
  }
  
  public XMLReader getXMLReader()
  {
    if (_bufferProcessor == null)
    {
      _bufferProcessor = new SAXBufferProcessor(_buffer, false);
      setXMLReader(_bufferProcessor);
    }
    else if (super.getXMLReader() == null)
    {
      setXMLReader(_bufferProcessor);
    }
    return _bufferProcessor;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBufferSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */