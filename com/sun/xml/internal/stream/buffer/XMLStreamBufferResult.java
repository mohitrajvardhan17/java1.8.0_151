package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class XMLStreamBufferResult
  extends SAXResult
{
  protected MutableXMLStreamBuffer _buffer;
  protected SAXBufferCreator _bufferCreator;
  
  public XMLStreamBufferResult()
  {
    setXMLStreamBuffer(new MutableXMLStreamBuffer());
  }
  
  public XMLStreamBufferResult(MutableXMLStreamBuffer paramMutableXMLStreamBuffer)
  {
    setXMLStreamBuffer(paramMutableXMLStreamBuffer);
  }
  
  public MutableXMLStreamBuffer getXMLStreamBuffer()
  {
    return _buffer;
  }
  
  public void setXMLStreamBuffer(MutableXMLStreamBuffer paramMutableXMLStreamBuffer)
  {
    if (paramMutableXMLStreamBuffer == null) {
      throw new NullPointerException("buffer cannot be null");
    }
    _buffer = paramMutableXMLStreamBuffer;
    setSystemId(_buffer.getSystemId());
    if (_bufferCreator != null) {
      _bufferCreator.setXMLStreamBuffer(_buffer);
    }
  }
  
  public ContentHandler getHandler()
  {
    if (_bufferCreator == null)
    {
      _bufferCreator = new SAXBufferCreator(_buffer);
      setHandler(_bufferCreator);
    }
    else if (super.getHandler() == null)
    {
      setHandler(_bufferCreator);
    }
    return _bufferCreator;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return (LexicalHandler)getHandler();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBufferResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */