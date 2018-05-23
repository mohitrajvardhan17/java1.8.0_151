package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathContainer
  extends SignatureElementProxy
  implements TransformParam
{
  public XPathContainer(Document paramDocument)
  {
    super(paramDocument);
  }
  
  public void setXPath(String paramString)
  {
    if (constructionElement.getChildNodes() != null)
    {
      localObject = constructionElement.getChildNodes();
      for (int i = 0; i < ((NodeList)localObject).getLength(); i++) {
        constructionElement.removeChild(((NodeList)localObject).item(i));
      }
    }
    Object localObject = doc.createTextNode(paramString);
    constructionElement.appendChild((Node)localObject);
  }
  
  public String getXPath()
  {
    return getTextFromTextChild();
  }
  
  public String getBaseLocalName()
  {
    return "XPath";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\XPathContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */