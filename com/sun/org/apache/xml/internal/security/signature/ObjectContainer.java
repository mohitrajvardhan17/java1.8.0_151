package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ObjectContainer
  extends SignatureElementProxy
{
  public ObjectContainer(Document paramDocument)
  {
    super(paramDocument);
  }
  
  public ObjectContainer(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public void setId(String paramString)
  {
    if (paramString != null)
    {
      constructionElement.setAttributeNS(null, "Id", paramString);
      constructionElement.setIdAttributeNS(null, "Id", true);
    }
  }
  
  public String getId()
  {
    return constructionElement.getAttributeNS(null, "Id");
  }
  
  public void setMimeType(String paramString)
  {
    if (paramString != null) {
      constructionElement.setAttributeNS(null, "MimeType", paramString);
    }
  }
  
  public String getMimeType()
  {
    return constructionElement.getAttributeNS(null, "MimeType");
  }
  
  public void setEncoding(String paramString)
  {
    if (paramString != null) {
      constructionElement.setAttributeNS(null, "Encoding", paramString);
    }
  }
  
  public String getEncoding()
  {
    return constructionElement.getAttributeNS(null, "Encoding");
  }
  
  public Node appendChild(Node paramNode)
  {
    return constructionElement.appendChild(paramNode);
  }
  
  public String getBaseLocalName()
  {
    return "Object";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\ObjectContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */