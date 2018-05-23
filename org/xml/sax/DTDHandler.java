package org.xml.sax;

public abstract interface DTDHandler
{
  public abstract void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException;
  
  public abstract void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\DTDHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */