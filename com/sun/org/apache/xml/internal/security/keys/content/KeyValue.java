package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyValue
  extends SignatureElementProxy
  implements KeyInfoContent
{
  public KeyValue(Document paramDocument, DSAKeyValue paramDSAKeyValue)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    constructionElement.appendChild(paramDSAKeyValue.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public KeyValue(Document paramDocument, RSAKeyValue paramRSAKeyValue)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    constructionElement.appendChild(paramRSAKeyValue.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public KeyValue(Document paramDocument, Element paramElement)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    constructionElement.appendChild(paramElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public KeyValue(Document paramDocument, PublicKey paramPublicKey)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    Object localObject;
    if ((paramPublicKey instanceof DSAPublicKey))
    {
      localObject = new DSAKeyValue(doc, paramPublicKey);
      constructionElement.appendChild(((DSAKeyValue)localObject).getElement());
      XMLUtils.addReturnToElement(constructionElement);
    }
    else if ((paramPublicKey instanceof RSAPublicKey))
    {
      localObject = new RSAKeyValue(doc, paramPublicKey);
      constructionElement.appendChild(((RSAKeyValue)localObject).getElement());
      XMLUtils.addReturnToElement(constructionElement);
    }
  }
  
  public KeyValue(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public PublicKey getPublicKey()
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "RSAKeyValue", 0);
    if (localElement != null)
    {
      localObject = new RSAKeyValue(localElement, baseURI);
      return ((RSAKeyValue)localObject).getPublicKey();
    }
    Object localObject = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "DSAKeyValue", 0);
    if (localObject != null)
    {
      DSAKeyValue localDSAKeyValue = new DSAKeyValue((Element)localObject, baseURI);
      return localDSAKeyValue.getPublicKey();
    }
    return null;
  }
  
  public String getBaseLocalName()
  {
    return "KeyValue";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\KeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */