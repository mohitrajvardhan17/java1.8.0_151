package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class ElementProxy
{
  protected static final Logger log = Logger.getLogger(ElementProxy.class.getName());
  protected Element constructionElement = null;
  protected String baseURI = null;
  protected Document doc = null;
  private static Map<String, String> prefixMappings = new ConcurrentHashMap();
  
  public ElementProxy() {}
  
  public ElementProxy(Document paramDocument)
  {
    if (paramDocument == null) {
      throw new RuntimeException("Document is null");
    }
    doc = paramDocument;
    constructionElement = createElementForFamilyLocal(doc, getBaseNamespace(), getBaseLocalName());
  }
  
  public ElementProxy(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    if (paramElement == null) {
      throw new XMLSecurityException("ElementProxy.nullElement");
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "setElement(\"" + paramElement.getTagName() + "\", \"" + paramString + "\")");
    }
    doc = paramElement.getOwnerDocument();
    constructionElement = paramElement;
    baseURI = paramString;
    guaranteeThatElementInCorrectSpace();
  }
  
  public abstract String getBaseNamespace();
  
  public abstract String getBaseLocalName();
  
  protected Element createElementForFamilyLocal(Document paramDocument, String paramString1, String paramString2)
  {
    Element localElement = null;
    if (paramString1 == null)
    {
      localElement = paramDocument.createElementNS(null, paramString2);
    }
    else
    {
      String str1 = getBaseNamespace();
      String str2 = getDefaultPrefix(str1);
      if ((str2 == null) || (str2.length() == 0))
      {
        localElement = paramDocument.createElementNS(paramString1, paramString2);
        localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", paramString1);
      }
      else
      {
        localElement = paramDocument.createElementNS(paramString1, str2 + ":" + paramString2);
        localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str2, paramString1);
      }
    }
    return localElement;
  }
  
  public static Element createElementForFamily(Document paramDocument, String paramString1, String paramString2)
  {
    Element localElement = null;
    String str = getDefaultPrefix(paramString1);
    if (paramString1 == null)
    {
      localElement = paramDocument.createElementNS(null, paramString2);
    }
    else if ((str == null) || (str.length() == 0))
    {
      localElement = paramDocument.createElementNS(paramString1, paramString2);
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", paramString1);
    }
    else
    {
      localElement = paramDocument.createElementNS(paramString1, str + ":" + paramString2);
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, paramString1);
    }
    return localElement;
  }
  
  public void setElement(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    if (paramElement == null) {
      throw new XMLSecurityException("ElementProxy.nullElement");
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "setElement(" + paramElement.getTagName() + ", \"" + paramString + "\"");
    }
    doc = paramElement.getOwnerDocument();
    constructionElement = paramElement;
    baseURI = paramString;
  }
  
  public final Element getElement()
  {
    return constructionElement;
  }
  
  public final NodeList getElementPlusReturns()
  {
    HelperNodeList localHelperNodeList = new HelperNodeList();
    localHelperNodeList.appendChild(doc.createTextNode("\n"));
    localHelperNodeList.appendChild(getElement());
    localHelperNodeList.appendChild(doc.createTextNode("\n"));
    return localHelperNodeList;
  }
  
  public Document getDocument()
  {
    return doc;
  }
  
  public String getBaseURI()
  {
    return baseURI;
  }
  
  void guaranteeThatElementInCorrectSpace()
    throws XMLSecurityException
  {
    String str1 = getBaseLocalName();
    String str2 = getBaseNamespace();
    String str3 = constructionElement.getLocalName();
    String str4 = constructionElement.getNamespaceURI();
    if ((!str2.equals(str4)) && (!str1.equals(str3)))
    {
      Object[] arrayOfObject = { str4 + ":" + str3, str2 + ":" + str1 };
      throw new XMLSecurityException("xml.WrongElement", arrayOfObject);
    }
  }
  
  public void addBigIntegerElement(BigInteger paramBigInteger, String paramString)
  {
    if (paramBigInteger != null)
    {
      Element localElement = XMLUtils.createElementInSignatureSpace(doc, paramString);
      Base64.fillElementWithBigInteger(localElement, paramBigInteger);
      constructionElement.appendChild(localElement);
      XMLUtils.addReturnToElement(constructionElement);
    }
  }
  
  public void addBase64Element(byte[] paramArrayOfByte, String paramString)
  {
    if (paramArrayOfByte != null)
    {
      Element localElement = Base64.encodeToElement(doc, paramString, paramArrayOfByte);
      constructionElement.appendChild(localElement);
      if (!XMLUtils.ignoreLineBreaks()) {
        constructionElement.appendChild(doc.createTextNode("\n"));
      }
    }
  }
  
  public void addTextElement(String paramString1, String paramString2)
  {
    Element localElement = XMLUtils.createElementInSignatureSpace(doc, paramString2);
    Text localText = doc.createTextNode(paramString1);
    localElement.appendChild(localText);
    constructionElement.appendChild(localElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void addBase64Text(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null)
    {
      Text localText = XMLUtils.ignoreLineBreaks() ? doc.createTextNode(Base64.encode(paramArrayOfByte)) : doc.createTextNode("\n" + Base64.encode(paramArrayOfByte) + "\n");
      constructionElement.appendChild(localText);
    }
  }
  
  public void addText(String paramString)
  {
    if (paramString != null)
    {
      Text localText = doc.createTextNode(paramString);
      constructionElement.appendChild(localText);
    }
  }
  
  public BigInteger getBigIntegerFromChildElement(String paramString1, String paramString2)
    throws Base64DecodingException
  {
    return Base64.decodeBigIntegerFromText(XMLUtils.selectNodeText(constructionElement.getFirstChild(), paramString2, paramString1, 0));
  }
  
  @Deprecated
  public byte[] getBytesFromChildElement(String paramString1, String paramString2)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectNode(constructionElement.getFirstChild(), paramString2, paramString1, 0);
    return Base64.decode(localElement);
  }
  
  public String getTextFromChildElement(String paramString1, String paramString2)
  {
    return XMLUtils.selectNode(constructionElement.getFirstChild(), paramString2, paramString1, 0).getTextContent();
  }
  
  public byte[] getBytesFromTextChild()
    throws XMLSecurityException
  {
    return Base64.decode(XMLUtils.getFullTextChildrenFromElement(constructionElement));
  }
  
  public String getTextFromTextChild()
  {
    return XMLUtils.getFullTextChildrenFromElement(constructionElement);
  }
  
  public int length(String paramString1, String paramString2)
  {
    int i = 0;
    for (Node localNode = constructionElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if ((paramString2.equals(localNode.getLocalName())) && (paramString1.equals(localNode.getNamespaceURI()))) {
        i++;
      }
    }
    return i;
  }
  
  public void setXPathNamespaceContext(String paramString1, String paramString2)
    throws XMLSecurityException
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
    }
    if (paramString1.equals("xmlns")) {
      throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
    }
    String str;
    if (paramString1.startsWith("xmlns:")) {
      str = paramString1;
    } else {
      str = "xmlns:" + paramString1;
    }
    Attr localAttr = constructionElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", str);
    if (localAttr != null)
    {
      if (!localAttr.getNodeValue().equals(paramString2))
      {
        Object[] arrayOfObject = { str, constructionElement.getAttributeNS(null, str) };
        throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", arrayOfObject);
      }
      return;
    }
    constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
  }
  
  public static void setDefaultPrefix(String paramString1, String paramString2)
    throws XMLSecurityException
  {
    
    if (prefixMappings.containsValue(paramString2))
    {
      String str = (String)prefixMappings.get(paramString1);
      if (!str.equals(paramString2))
      {
        Object[] arrayOfObject = { paramString2, paramString1, str };
        throw new XMLSecurityException("prefix.AlreadyAssigned", arrayOfObject);
      }
    }
    if ("http://www.w3.org/2000/09/xmldsig#".equals(paramString1)) {
      XMLUtils.setDsPrefix(paramString2);
    }
    if ("http://www.w3.org/2001/04/xmlenc#".equals(paramString1)) {
      XMLUtils.setXencPrefix(paramString2);
    }
    prefixMappings.put(paramString1, paramString2);
  }
  
  public static void registerDefaultPrefixes()
    throws XMLSecurityException
  {
    setDefaultPrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
    setDefaultPrefix("http://www.w3.org/2001/04/xmlenc#", "xenc");
    setDefaultPrefix("http://www.w3.org/2009/xmlenc11#", "xenc11");
    setDefaultPrefix("http://www.xmlsecurity.org/experimental#", "experimental");
    setDefaultPrefix("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
    setDefaultPrefix("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
    setDefaultPrefix("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
    setDefaultPrefix("http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx");
  }
  
  public static String getDefaultPrefix(String paramString)
  {
    return (String)prefixMappings.get(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\ElementProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */