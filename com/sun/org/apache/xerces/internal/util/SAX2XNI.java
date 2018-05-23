package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.jaxp.validation.WrappedSAXException;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAX2XNI
  implements ContentHandler, XMLDocumentSource
{
  private XMLDocumentHandler fCore;
  private final NamespaceSupport nsContext = new NamespaceSupport();
  private final SymbolTable symbolTable = new SymbolTable();
  private Locator locator;
  private final XMLAttributes xa = new XMLAttributesImpl();
  
  public SAX2XNI(XMLDocumentHandler paramXMLDocumentHandler)
  {
    fCore = paramXMLDocumentHandler;
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    fCore = paramXMLDocumentHandler;
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return fCore;
  }
  
  public void startDocument()
    throws SAXException
  {
    try
    {
      nsContext.reset();
      Object localObject;
      if (locator == null) {
        localObject = new SimpleLocator(null, null, -1, -1);
      } else {
        localObject = new LocatorWrapper(locator);
      }
      fCore.startDocument((XMLLocator)localObject, null, nsContext, null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      fCore.endDocument(null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      fCore.startElement(createQName(paramString1, paramString2, paramString3), createAttributes(paramAttributes), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      fCore.endElement(createQName(paramString1, paramString2, paramString3), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      fCore.characters(new XMLString(paramArrayOfChar, paramInt1, paramInt2), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      fCore.ignorableWhitespace(new XMLString(paramArrayOfChar, paramInt1, paramInt2), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    nsContext.pushContext();
    nsContext.declarePrefix(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
  {
    nsContext.popContext();
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      fCore.processingInstruction(symbolize(paramString1), createXMLString(paramString2), null);
    }
    catch (WrappedSAXException localWrappedSAXException)
    {
      throw exception;
    }
  }
  
  public void skippedEntity(String paramString) {}
  
  public void setDocumentLocator(Locator paramLocator)
  {
    locator = paramLocator;
  }
  
  private QName createQName(String paramString1, String paramString2, String paramString3)
  {
    int i = paramString3.indexOf(':');
    if (paramString2.length() == 0)
    {
      paramString1 = "";
      if (i < 0) {
        paramString2 = paramString3;
      } else {
        paramString2 = paramString3.substring(i + 1);
      }
    }
    String str;
    if (i < 0) {
      str = null;
    } else {
      str = paramString3.substring(0, i);
    }
    if ((paramString1 != null) && (paramString1.length() == 0)) {
      paramString1 = null;
    }
    return new QName(symbolize(str), symbolize(paramString2), symbolize(paramString3), symbolize(paramString1));
  }
  
  private String symbolize(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return symbolTable.addSymbol(paramString);
  }
  
  private XMLString createXMLString(String paramString)
  {
    return new XMLString(paramString.toCharArray(), 0, paramString.length());
  }
  
  private XMLAttributes createAttributes(Attributes paramAttributes)
  {
    xa.removeAllAttributes();
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++) {
      xa.addAttribute(createQName(paramAttributes.getURI(j), paramAttributes.getLocalName(j), paramAttributes.getQName(j)), paramAttributes.getType(j), paramAttributes.getValue(j));
    }
    return xa;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\SAX2XNI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */