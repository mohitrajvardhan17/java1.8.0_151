package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class FastInfosetConnector
  extends StAXConnector
{
  private final StAXDocumentParser fastInfosetStreamReader;
  private boolean textReported;
  private final Base64Data base64Data = new Base64Data();
  private final StringBuilder buffer = new StringBuilder();
  private final CharSequenceImpl charArray = new CharSequenceImpl();
  
  public FastInfosetConnector(StAXDocumentParser paramStAXDocumentParser, XmlVisitor paramXmlVisitor)
  {
    super(paramXmlVisitor);
    paramStAXDocumentParser.setStringInterning(true);
    fastInfosetStreamReader = paramStAXDocumentParser;
  }
  
  public void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      int j = fastInfosetStreamReader.getEventType();
      if (j == 7) {
        while (!fastInfosetStreamReader.isStartElement()) {
          j = fastInfosetStreamReader.next();
        }
      }
      if (j != 1) {
        throw new IllegalStateException("The current event is not START_ELEMENT\n but " + j);
      }
      handleStartDocument(fastInfosetStreamReader.getNamespaceContext());
      for (;;)
      {
        switch (j)
        {
        case 1: 
          handleStartElement();
          i++;
          break;
        case 2: 
          i--;
          handleEndElement();
          if (i != 0) {
            break;
          }
          break;
        case 4: 
        case 6: 
        case 12: 
          if (predictor.expectText())
          {
            j = fastInfosetStreamReader.peekNext();
            if (j == 2) {
              processNonIgnorableText();
            } else if (j == 1) {
              processIgnorableText();
            } else {
              handleFragmentedCharacters();
            }
          }
          break;
        }
        j = fastInfosetStreamReader.next();
      }
      fastInfosetStreamReader.next();
      handleEndDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  protected Location getCurrentLocation()
  {
    return fastInfosetStreamReader.getLocation();
  }
  
  protected String getCurrentQName()
  {
    return fastInfosetStreamReader.getNameString();
  }
  
  private void handleStartElement()
    throws SAXException
  {
    processUnreportedText();
    for (int i = 0; i < fastInfosetStreamReader.accessNamespaceCount(); i++) {
      visitor.startPrefixMapping(fastInfosetStreamReader.getNamespacePrefix(i), fastInfosetStreamReader.getNamespaceURI(i));
    }
    tagName.uri = fastInfosetStreamReader.accessNamespaceURI();
    tagName.local = fastInfosetStreamReader.accessLocalName();
    tagName.atts = fastInfosetStreamReader.getAttributesHolder();
    visitor.startElement(tagName);
  }
  
  private void handleFragmentedCharacters()
    throws XMLStreamException, SAXException
  {
    buffer.setLength(0);
    buffer.append(fastInfosetStreamReader.getTextCharacters(), fastInfosetStreamReader.getTextStart(), fastInfosetStreamReader.getTextLength());
    for (;;)
    {
      switch (fastInfosetStreamReader.peekNext())
      {
      case 1: 
        processBufferedText(true);
        return;
      case 2: 
        processBufferedText(false);
        return;
      case 4: 
      case 6: 
      case 12: 
        fastInfosetStreamReader.next();
        buffer.append(fastInfosetStreamReader.getTextCharacters(), fastInfosetStreamReader.getTextStart(), fastInfosetStreamReader.getTextLength());
        break;
      case 3: 
      case 5: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      default: 
        fastInfosetStreamReader.next();
      }
    }
  }
  
  private void handleEndElement()
    throws SAXException
  {
    processUnreportedText();
    tagName.uri = fastInfosetStreamReader.accessNamespaceURI();
    tagName.local = fastInfosetStreamReader.accessLocalName();
    visitor.endElement(tagName);
    for (int i = fastInfosetStreamReader.accessNamespaceCount() - 1; i >= 0; i--) {
      visitor.endPrefixMapping(fastInfosetStreamReader.getNamespacePrefix(i));
    }
  }
  
  private void processNonIgnorableText()
    throws SAXException
  {
    textReported = true;
    int i = fastInfosetStreamReader.getTextAlgorithmBytes() != null ? 1 : 0;
    if ((i != 0) && (fastInfosetStreamReader.getTextAlgorithmIndex() == 1))
    {
      base64Data.set(fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
      visitor.text(base64Data);
    }
    else
    {
      if (i != 0) {
        fastInfosetStreamReader.getText();
      }
      charArray.set();
      visitor.text(charArray);
    }
  }
  
  private void processIgnorableText()
    throws SAXException
  {
    int i = fastInfosetStreamReader.getTextAlgorithmBytes() != null ? 1 : 0;
    if ((i != 0) && (fastInfosetStreamReader.getTextAlgorithmIndex() == 1))
    {
      base64Data.set(fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
      visitor.text(base64Data);
      textReported = true;
    }
    else
    {
      if (i != 0) {
        fastInfosetStreamReader.getText();
      }
      charArray.set();
      if (!WhiteSpaceProcessor.isWhiteSpace(charArray))
      {
        visitor.text(charArray);
        textReported = true;
      }
    }
  }
  
  private void processBufferedText(boolean paramBoolean)
    throws SAXException
  {
    if ((!paramBoolean) || (!WhiteSpaceProcessor.isWhiteSpace(buffer)))
    {
      visitor.text(buffer);
      textReported = true;
    }
  }
  
  private void processUnreportedText()
    throws SAXException
  {
    if ((!textReported) && (predictor.expectText())) {
      visitor.text("");
    }
    textReported = false;
  }
  
  private final class CharSequenceImpl
    implements CharSequence
  {
    char[] ch;
    int start;
    int length;
    
    CharSequenceImpl() {}
    
    CharSequenceImpl(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      ch = paramArrayOfChar;
      start = paramInt1;
      length = paramInt2;
    }
    
    public void set()
    {
      ch = fastInfosetStreamReader.getTextCharacters();
      start = fastInfosetStreamReader.getTextStart();
      length = fastInfosetStreamReader.getTextLength();
    }
    
    public final int length()
    {
      return length;
    }
    
    public final char charAt(int paramInt)
    {
      return ch[(start + paramInt)];
    }
    
    public final CharSequence subSequence(int paramInt1, int paramInt2)
    {
      return new CharSequenceImpl(FastInfosetConnector.this, ch, start + paramInt1, paramInt2 - paramInt1);
    }
    
    public String toString()
    {
      return new String(ch, start, length);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\FastInfosetConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */