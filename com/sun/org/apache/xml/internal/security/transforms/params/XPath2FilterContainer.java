package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPath2FilterContainer
  extends ElementProxy
  implements TransformParam
{
  private static final String _ATT_FILTER = "Filter";
  private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";
  private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";
  private static final String _ATT_FILTER_VALUE_UNION = "union";
  public static final String INTERSECT = "intersect";
  public static final String SUBTRACT = "subtract";
  public static final String UNION = "union";
  public static final String _TAG_XPATH2 = "XPath";
  public static final String XPathFilter2NS = "http://www.w3.org/2002/06/xmldsig-filter2";
  
  private XPath2FilterContainer() {}
  
  private XPath2FilterContainer(Document paramDocument, String paramString1, String paramString2)
  {
    super(paramDocument);
    constructionElement.setAttributeNS(null, "Filter", paramString2);
    constructionElement.appendChild(paramDocument.createTextNode(paramString1));
  }
  
  private XPath2FilterContainer(Element paramElement, String paramString)
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
  
  public static XPath2FilterContainer newInstanceIntersect(Document paramDocument, String paramString)
  {
    return new XPath2FilterContainer(paramDocument, paramString, "intersect");
  }
  
  public static XPath2FilterContainer newInstanceSubtract(Document paramDocument, String paramString)
  {
    return new XPath2FilterContainer(paramDocument, paramString, "subtract");
  }
  
  public static XPath2FilterContainer newInstanceUnion(Document paramDocument, String paramString)
  {
    return new XPath2FilterContainer(paramDocument, paramString, "union");
  }
  
  public static NodeList newInstances(Document paramDocument, String[][] paramArrayOfString)
  {
    HelperNodeList localHelperNodeList = new HelperNodeList();
    XMLUtils.addReturnToElement(paramDocument, localHelperNodeList);
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str1 = paramArrayOfString[i][0];
      String str2 = paramArrayOfString[i][1];
      if ((!str1.equals("intersect")) && (!str1.equals("subtract")) && (!str1.equals("union"))) {
        throw new IllegalArgumentException("The type(" + i + ")=\"" + str1 + "\" is illegal");
      }
      XPath2FilterContainer localXPath2FilterContainer = new XPath2FilterContainer(paramDocument, str2, str1);
      localHelperNodeList.appendChild(localXPath2FilterContainer.getElement());
      XMLUtils.addReturnToElement(paramDocument, localHelperNodeList);
    }
    return localHelperNodeList;
  }
  
  public static XPath2FilterContainer newInstance(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    return new XPath2FilterContainer(paramElement, paramString);
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
    return "http://www.w3.org/2002/06/xmldsig-filter2";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\XPath2FilterContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */