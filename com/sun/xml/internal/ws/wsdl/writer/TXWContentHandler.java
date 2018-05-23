package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TXWContentHandler
  implements ContentHandler
{
  Stack<TypedXmlWriter> stack = new Stack();
  
  public TXWContentHandler(TypedXmlWriter paramTypedXmlWriter)
  {
    stack.push(paramTypedXmlWriter);
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    TypedXmlWriter localTypedXmlWriter = ((TypedXmlWriter)stack.peek())._element(paramString1, paramString2, TypedXmlWriter.class);
    stack.push(localTypedXmlWriter);
    if (paramAttributes != null) {
      for (int i = 0; i < paramAttributes.getLength(); i++)
      {
        String str = paramAttributes.getURI(i);
        if ("http://www.w3.org/2000/xmlns/".equals(str))
        {
          if ("xmlns".equals(paramAttributes.getLocalName(i))) {
            localTypedXmlWriter._namespace(paramAttributes.getValue(i), "");
          } else {
            localTypedXmlWriter._namespace(paramAttributes.getValue(i), paramAttributes.getLocalName(i));
          }
        }
        else if ((!"schemaLocation".equals(paramAttributes.getLocalName(i))) || (!"".equals(paramAttributes.getValue(i)))) {
          localTypedXmlWriter._attribute(str, paramAttributes.getLocalName(i), paramAttributes.getValue(i));
        }
      }
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    stack.pop();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\TXWContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */