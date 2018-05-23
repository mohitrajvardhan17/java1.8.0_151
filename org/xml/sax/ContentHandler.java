package org.xml.sax;

public abstract interface ContentHandler
{
  public abstract void setDocumentLocator(Locator paramLocator);
  
  public abstract void startDocument()
    throws SAXException;
  
  public abstract void endDocument()
    throws SAXException;
  
  public abstract void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException;
  
  public abstract void endPrefixMapping(String paramString)
    throws SAXException;
  
  public abstract void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException;
  
  public abstract void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException;
  
  public abstract void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void processingInstruction(String paramString1, String paramString2)
    throws SAXException;
  
  public abstract void skippedEntity(String paramString)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\ContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */