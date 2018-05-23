package com.sun.xml.internal.stream.buffer.sax;

import com.sun.xml.internal.stream.buffer.AbstractCreator;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class SAXBufferCreator
  extends AbstractCreator
  implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler
{
  protected String[] _namespaceAttributes = new String[32];
  protected int _namespaceAttributesPtr;
  private int depth = 0;
  
  public SAXBufferCreator() {}
  
  public SAXBufferCreator(MutableXMLStreamBuffer paramMutableXMLStreamBuffer)
  {
    this();
    setBuffer(paramMutableXMLStreamBuffer);
  }
  
  public MutableXMLStreamBuffer create(XMLReader paramXMLReader, InputStream paramInputStream)
    throws IOException, SAXException
  {
    return create(paramXMLReader, paramInputStream, null);
  }
  
  public MutableXMLStreamBuffer create(XMLReader paramXMLReader, InputStream paramInputStream, String paramString)
    throws IOException, SAXException
  {
    if (_buffer == null) {
      createBuffer();
    }
    _buffer.setSystemId(paramString);
    paramXMLReader.setContentHandler(this);
    paramXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
    try
    {
      setHasInternedStrings(paramXMLReader.getFeature("http://xml.org/sax/features/string-interning"));
    }
    catch (SAXException localSAXException) {}
    if (paramString != null)
    {
      InputSource localInputSource = new InputSource(paramString);
      localInputSource.setByteStream(paramInputStream);
      paramXMLReader.parse(localInputSource);
    }
    else
    {
      paramXMLReader.parse(new InputSource(paramInputStream));
    }
    return getXMLStreamBuffer();
  }
  
  public void reset()
  {
    _buffer = null;
    _namespaceAttributesPtr = 0;
    depth = 0;
  }
  
  public void startDocument()
    throws SAXException
  {
    storeStructure(16);
  }
  
  public void endDocument()
    throws SAXException
  {
    storeStructure(144);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    cacheNamespaceAttribute(paramString1, paramString2);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    storeQualifiedName(32, paramString1, paramString2, paramString3);
    if (_namespaceAttributesPtr > 0) {
      storeNamespaceAttributes();
    }
    if (paramAttributes.getLength() > 0) {
      storeAttributes(paramAttributes);
    }
    depth += 1;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    storeStructure(144);
    if (--depth == 0) {
      increaseTreeCount();
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    storeContentCharacters(80, paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    storeStructure(112);
    storeStructureString(paramString1);
    storeStructureString(paramString2);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    storeContentCharacters(96, paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void cacheNamespaceAttribute(String paramString1, String paramString2)
  {
    _namespaceAttributes[(_namespaceAttributesPtr++)] = paramString1;
    _namespaceAttributes[(_namespaceAttributesPtr++)] = paramString2;
    if (_namespaceAttributesPtr == _namespaceAttributes.length)
    {
      String[] arrayOfString = new String[_namespaceAttributesPtr * 2];
      System.arraycopy(_namespaceAttributes, 0, arrayOfString, 0, _namespaceAttributesPtr);
      _namespaceAttributes = arrayOfString;
    }
  }
  
  private void storeNamespaceAttributes()
  {
    for (int i = 0; i < _namespaceAttributesPtr; i += 2)
    {
      int j = 64;
      if (_namespaceAttributes[i].length() > 0)
      {
        j |= 0x1;
        storeStructureString(_namespaceAttributes[i]);
      }
      if (_namespaceAttributes[(i + 1)].length() > 0)
      {
        j |= 0x2;
        storeStructureString(_namespaceAttributes[(i + 1)]);
      }
      storeStructure(j);
    }
    _namespaceAttributesPtr = 0;
  }
  
  private void storeAttributes(Attributes paramAttributes)
  {
    for (int i = 0; i < paramAttributes.getLength(); i++) {
      if (!paramAttributes.getQName(i).startsWith("xmlns"))
      {
        storeQualifiedName(48, paramAttributes.getURI(i), paramAttributes.getLocalName(i), paramAttributes.getQName(i));
        storeStructureString(paramAttributes.getType(i));
        storeContentString(paramAttributes.getValue(i));
      }
    }
  }
  
  private void storeQualifiedName(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (paramString1.length() > 0)
    {
      paramInt |= 0x2;
      storeStructureString(paramString1);
    }
    storeStructureString(paramString2);
    if (paramString3.indexOf(':') >= 0)
    {
      paramInt |= 0x4;
      storeStructureString(paramString3);
    }
    storeStructure(paramInt);
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
    throws IOException, SAXException
  {
    return null;
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {}
  
  public void endCDATA()
    throws SAXException
  {}
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\sax\SAXBufferCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */