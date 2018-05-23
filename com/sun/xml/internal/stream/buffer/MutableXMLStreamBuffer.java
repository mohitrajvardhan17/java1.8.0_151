package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class MutableXMLStreamBuffer
  extends XMLStreamBuffer
{
  public static final int DEFAULT_ARRAY_SIZE = 512;
  
  public MutableXMLStreamBuffer()
  {
    this(512);
  }
  
  public void setSystemId(String paramString)
  {
    systemId = paramString;
  }
  
  public MutableXMLStreamBuffer(int paramInt)
  {
    _structure = new FragmentedArray(new byte[paramInt]);
    _structureStrings = new FragmentedArray(new String[paramInt]);
    _contentCharactersBuffer = new FragmentedArray(new char['က']);
    _contentObjects = new FragmentedArray(new Object[paramInt]);
    ((byte[])_structure.getArray())[0] = -112;
  }
  
  public void createFromXMLStreamReader(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    reset();
    StreamReaderBufferCreator localStreamReaderBufferCreator = new StreamReaderBufferCreator(this);
    localStreamReaderBufferCreator.create(paramXMLStreamReader);
  }
  
  public XMLStreamWriter createFromXMLStreamWriter()
  {
    reset();
    return new StreamWriterBufferCreator(this);
  }
  
  public SAXBufferCreator createFromSAXBufferCreator()
  {
    reset();
    SAXBufferCreator localSAXBufferCreator = new SAXBufferCreator();
    localSAXBufferCreator.setBuffer(this);
    return localSAXBufferCreator;
  }
  
  public void createFromXMLReader(XMLReader paramXMLReader, InputStream paramInputStream)
    throws SAXException, IOException
  {
    createFromXMLReader(paramXMLReader, paramInputStream, null);
  }
  
  public void createFromXMLReader(XMLReader paramXMLReader, InputStream paramInputStream, String paramString)
    throws SAXException, IOException
  {
    reset();
    SAXBufferCreator localSAXBufferCreator = new SAXBufferCreator(this);
    paramXMLReader.setContentHandler(localSAXBufferCreator);
    paramXMLReader.setDTDHandler(localSAXBufferCreator);
    paramXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", localSAXBufferCreator);
    localSAXBufferCreator.create(paramXMLReader, paramInputStream, paramString);
  }
  
  public void reset()
  {
    _structurePtr = (_structureStringsPtr = _contentCharactersBufferPtr = _contentObjectsPtr = 0);
    ((byte[])_structure.getArray())[0] = -112;
    _contentObjects.setNext(null);
    Object[] arrayOfObject = (Object[])_contentObjects.getArray();
    for (int i = 0; (i < arrayOfObject.length) && (arrayOfObject[i] != null); i++) {
      arrayOfObject[i] = null;
    }
    treeCount = 0;
  }
  
  protected void setHasInternedStrings(boolean paramBoolean)
  {
    _hasInternedStrings = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\MutableXMLStreamBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */