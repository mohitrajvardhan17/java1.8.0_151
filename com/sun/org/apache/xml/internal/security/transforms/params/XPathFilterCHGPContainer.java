package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XPathFilterCHGPContainer
  extends ElementProxy
  implements TransformParam
{
  public static final String TRANSFORM_XPATHFILTERCHGP = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
  private static final String _TAG_INCLUDE_BUT_SEARCH = "IncludeButSearch";
  private static final String _TAG_EXCLUDE_BUT_SEARCH = "ExcludeButSearch";
  private static final String _TAG_EXCLUDE = "Exclude";
  public static final String _TAG_XPATHCHGP = "XPathAlternative";
  public static final String _ATT_INCLUDESLASH = "IncludeSlashPolicy";
  public static final boolean IncludeSlash = true;
  public static final boolean ExcludeSlash = false;
  
  private XPathFilterCHGPContainer() {}
  
  private XPathFilterCHGPContainer(Document paramDocument, boolean paramBoolean, String paramString1, String paramString2, String paramString3)
  {
    super(paramDocument);
    if (paramBoolean) {
      constructionElement.setAttributeNS(null, "IncludeSlashPolicy", "true");
    } else {
      constructionElement.setAttributeNS(null, "IncludeSlashPolicy", "false");
    }
    Element localElement;
    if ((paramString1 != null) && (paramString1.trim().length() > 0))
    {
      localElement = ElementProxy.createElementForFamily(paramDocument, getBaseNamespace(), "IncludeButSearch");
      localElement.appendChild(doc.createTextNode(indentXPathText(paramString1)));
      XMLUtils.addReturnToElement(constructionElement);
      constructionElement.appendChild(localElement);
    }
    if ((paramString2 != null) && (paramString2.trim().length() > 0))
    {
      localElement = ElementProxy.createElementForFamily(paramDocument, getBaseNamespace(), "ExcludeButSearch");
      localElement.appendChild(doc.createTextNode(indentXPathText(paramString2)));
      XMLUtils.addReturnToElement(constructionElement);
      constructionElement.appendChild(localElement);
    }
    if ((paramString3 != null) && (paramString3.trim().length() > 0))
    {
      localElement = ElementProxy.createElementForFamily(paramDocument, getBaseNamespace(), "Exclude");
      localElement.appendChild(doc.createTextNode(indentXPathText(paramString3)));
      XMLUtils.addReturnToElement(constructionElement);
      constructionElement.appendChild(localElement);
    }
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  static String indentXPathText(String paramString)
  {
    if ((paramString.length() > 2) && (!Character.isWhitespace(paramString.charAt(0)))) {
      return "\n" + paramString + "\n";
    }
    return paramString;
  }
  
  private XPathFilterCHGPContainer(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public static XPathFilterCHGPContainer getInstance(Document paramDocument, boolean paramBoolean, String paramString1, String paramString2, String paramString3)
  {
    return new XPathFilterCHGPContainer(paramDocument, paramBoolean, paramString1, paramString2, paramString3);
  }
  
  public static XPathFilterCHGPContainer getInstance(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    return new XPathFilterCHGPContainer(paramElement, paramString);
  }
  
  private String getXStr(String paramString)
  {
    if (length(getBaseNamespace(), paramString) != 1) {
      return "";
    }
    Element localElement = XMLUtils.selectNode(constructionElement.getFirstChild(), getBaseNamespace(), paramString, 0);
    return XMLUtils.getFullTextChildrenFromElement(localElement);
  }
  
  public String getIncludeButSearch()
  {
    return getXStr("IncludeButSearch");
  }
  
  public String getExcludeButSearch()
  {
    return getXStr("ExcludeButSearch");
  }
  
  public String getExclude()
  {
    return getXStr("Exclude");
  }
  
  public boolean getIncludeSlashPolicy()
  {
    return constructionElement.getAttributeNS(null, "IncludeSlashPolicy").equals("true");
  }
  
  private Node getHereContextNode(String paramString)
  {
    if (length(getBaseNamespace(), paramString) != 1) {
      return null;
    }
    return XMLUtils.selectNodeText(constructionElement.getFirstChild(), getBaseNamespace(), paramString, 0);
  }
  
  public Node getHereContextNodeIncludeButSearch()
  {
    return getHereContextNode("IncludeButSearch");
  }
  
  public Node getHereContextNodeExcludeButSearch()
  {
    return getHereContextNode("ExcludeButSearch");
  }
  
  public Node getHereContextNodeExclude()
  {
    return getHereContextNode("Exclude");
  }
  
  public final String getBaseLocalName()
  {
    return "XPathAlternative";
  }
  
  public final String getBaseNamespace()
  {
    return "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\XPathFilterCHGPContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */