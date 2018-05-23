package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SignatureElementProxy
  extends ElementProxy
{
  protected SignatureElementProxy() {}
  
  public SignatureElementProxy(Document paramDocument)
  {
    if (paramDocument == null) {
      throw new RuntimeException("Document is null");
    }
    doc = paramDocument;
    constructionElement = XMLUtils.createElementInSignatureSpace(doc, getBaseLocalName());
  }
  
  public SignatureElementProxy(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public String getBaseNamespace()
  {
    return "http://www.w3.org/2000/09/xmldsig#";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\SignatureElementProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */