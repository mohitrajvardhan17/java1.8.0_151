package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPath2FilterContainer04
  extends ElementProxy
  implements TransformParam
{
  private static final String _ATT_FILTER = "Filter";
  private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";
  private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";
  private static final String _ATT_FILTER_VALUE_UNION = "union";
  public static final String _TAG_XPATH2 = "XPath";
  public static final String XPathFilter2NS = "http://www.w3.org/2002/04/xmldsig-filter2";
  
  private XPath2FilterContainer04() {}
  
  private XPath2FilterContainer04(Document paramDocument, String paramString1, String paramString2)
  {
    super(paramDocument);
    constructionElement.setAttributeNS(null, "Filter", paramString2);
    if ((paramString1.length() > 2) && (!Character.isWhitespace(paramString1.charAt(0))))
    {
      XMLUtils.addReturnToElement(constructionElement);
      constructionElement.appendChild(paramDocument.createTextNode(paramString1));
      XMLUtils.addReturnToElement(constructionElement);
    }
    else
    {
      constructionElement.appendChild(paramDocument.createTextNode(paramString1));
    }
  }
  
  private XPath2FilterContainer04(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    String str = constructionElement.getAttributeNS(null, "Filter");
    if ((!str.equals("intersect")) && (!str.equals("subtract")) && (!str.equals("union")))
    {
      Object[] arrayOfObject = { "Filter", str, "intersect, subtract or union" };
      throw new XMLSecurityException("attributeValueIllegal", arrayOfObject);
    }
  }
  
  public static XPath2FilterContainer04 newInstanceIntersect(Document paramDocument, String paramString)
  {
    return new XPath2FilterContainer04(paramDocument, paramString, "intersect");
  }
  
  public static XPath2FilterContainer04 newInstanceSubtract(Document paramDocument, String paramString)
  {
    return new XPath2FilterContainer04(paramDocument, paramString, "subtract");
  }
  
  public static XPath2FilterContainer04 newInstanceUnion(Document paramDocument, String paramString)
  {
    return new XPath2FilterContainer04(paramDocument, paramString, "union");
  }
  
  public static XPath2FilterContainer04 newInstance(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    return new XPath2FilterContainer04(paramElement, paramString);
  }
  
  public boolean isIntersect()
  {
    return constructionElement.getAttributeNS(null, "Filter").equals("intersect");
  }
  
  public boolean isSubtract()
  {
    return constructionElement.getAttributeNS(null, "Filter").equals("subtract");
  }
  
  public boolean isUnion()
  {
    return constructionElement.getAttributeNS(null, "Filter").equals("union");
  }
  
  public String getXPathFilterStr()
  {
    return getTextFromTextChild();
  }
  
  public Node getXPathFilterTextNode()
  {
    NodeList localNodeList = constructionElement.getChildNodes();
    int i = localNodeList.getLength();
    for (int j = 0; j < i; j++) {
      if (localNodeList.item(j).getNodeType() == 3) {
        return localNodeList.item(j);
      }
    }
    return null;
  }
  
  public final String getBaseLocalName()
  {
    return "XPath";
  }
  
  public final String getBaseNamespace()
  {
    return "http://www.w3.org/2002/04/xmldsig-filter2";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\XPath2FilterContainer04.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */