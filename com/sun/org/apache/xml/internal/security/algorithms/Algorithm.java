package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Algorithm
  extends SignatureElementProxy
{
  public Algorithm(Document paramDocument, String paramString)
  {
    super(paramDocument);
    setAlgorithmURI(paramString);
  }
  
  public Algorithm(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public String getAlgorithmURI()
  {
    return constructionElement.getAttributeNS(null, "Algorithm");
  }
  
  protected void setAlgorithmURI(String paramString)
  {
    if (paramString != null) {
      constructionElement.setAttributeNS(null, "Algorithm", paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\Algorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */