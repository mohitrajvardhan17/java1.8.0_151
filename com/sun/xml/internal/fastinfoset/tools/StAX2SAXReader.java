package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class StAX2SAXReader
{
  ContentHandler _handler;
  LexicalHandler _lexicalHandler;
  XMLStreamReader _reader;
  
  public StAX2SAXReader(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler)
  {
    _handler = paramContentHandler;
    _reader = paramXMLStreamReader;
  }
  
  public StAX2SAXReader(XMLStreamReader paramXMLStreamReader)
  {
    _reader = paramXMLStreamReader;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    _handler = paramContentHandler;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    _lexicalHandler = paramLexicalHandler;
  }
  
  public void adapt()
    throws XMLStreamException, SAXException
  {
    AttributesImpl localAttributesImpl = new AttributesImpl();
    _handler.startDocument();
    try
    {
      while (_reader.hasNext())
      {
        int k = _reader.next();
        int i;
        int m;
        QName localQName1;
        String str1;
        String str2;
        switch (k)
        {
        case 1: 
          i = _reader.getNamespaceCount();
          for (m = 0; m < i; m++) {
            _handler.startPrefixMapping(_reader.getNamespacePrefix(m), _reader.getNamespaceURI(m));
          }
          localAttributesImpl.clear();
          int j = _reader.getAttributeCount();
          for (m = 0; m < j; m++)
          {
            QName localQName2 = _reader.getAttributeName(m);
            String str3 = _reader.getAttributePrefix(m);
            if ((str3 == null) || (str3 == "")) {
              str3 = localQName2.getLocalPart();
            } else {
              str3 = str3 + ":" + localQName2.getLocalPart();
            }
            localAttributesImpl.addAttribute(_reader.getAttributeNamespace(m), localQName2.getLocalPart(), str3, _reader.getAttributeType(m), _reader.getAttributeValue(m));
          }
          localQName1 = _reader.getName();
          str1 = localQName1.getPrefix();
          str2 = localQName1.getLocalPart();
          _handler.startElement(_reader.getNamespaceURI(), str2, str1.length() > 0 ? str1 + ":" + str2 : str2, localAttributesImpl);
          break;
        case 2: 
          localQName1 = _reader.getName();
          str1 = localQName1.getPrefix();
          str2 = localQName1.getLocalPart();
          _handler.endElement(_reader.getNamespaceURI(), str2, str1.length() > 0 ? str1 + ":" + str2 : str2);
          i = _reader.getNamespaceCount();
          for (m = 0; m < i; m++) {
            _handler.endPrefixMapping(_reader.getNamespacePrefix(m));
          }
          break;
        case 4: 
          _handler.characters(_reader.getTextCharacters(), _reader.getTextStart(), _reader.getTextLength());
          break;
        case 5: 
          _lexicalHandler.comment(_reader.getTextCharacters(), _reader.getTextStart(), _reader.getTextLength());
          break;
        case 3: 
          _handler.processingInstruction(_reader.getPITarget(), _reader.getPIData());
          break;
        case 8: 
          break;
        case 6: 
        case 7: 
        default: 
          throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[] { Integer.valueOf(k) }));
        }
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      _handler.endDocument();
      throw localXMLStreamException;
    }
    _handler.endDocument();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\tools\StAX2SAXReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */