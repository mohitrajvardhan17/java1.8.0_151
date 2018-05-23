package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class DOMXPathTransform
  extends ApacheTransform
{
  public DOMXPathTransform() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec == null) {
      throw new InvalidAlgorithmParameterException("params are required");
    }
    if (!(paramTransformParameterSpec instanceof XPathFilterParameterSpec)) {
      throw new InvalidAlgorithmParameterException("params must be of type XPathFilterParameterSpec");
    }
    params = paramTransformParameterSpec;
  }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws InvalidAlgorithmParameterException
  {
    super.init(paramXMLStructure, paramXMLCryptoContext);
    unmarshalParams(DOMUtils.getFirstChildElement(transformElem));
  }
  
  private void unmarshalParams(Element paramElement)
  {
    String str1 = paramElement.getFirstChild().getNodeValue();
    NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
    if (localNamedNodeMap != null)
    {
      int i = localNamedNodeMap.getLength();
      HashMap localHashMap = new HashMap(i);
      for (int j = 0; j < i; j++)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(j);
        String str2 = localAttr.getPrefix();
        if ((str2 != null) && (str2.equals("xmlns"))) {
          localHashMap.put(localAttr.getLocalName(), localAttr.getValue());
        }
      }
      params = new XPathFilterParameterSpec(str1, localHashMap);
    }
    else
    {
      params = new XPathFilterParameterSpec(str1);
    }
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    super.marshalParams(paramXMLStructure, paramXMLCryptoContext);
    XPathFilterParameterSpec localXPathFilterParameterSpec = (XPathFilterParameterSpec)getParameterSpec();
    Element localElement = DOMUtils.createElement(ownerDoc, "XPath", "http://www.w3.org/2000/09/xmldsig#", DOMUtils.getSignaturePrefix(paramXMLCryptoContext));
    localElement.appendChild(ownerDoc.createTextNode(localXPathFilterParameterSpec.getXPath()));
    Set localSet = localXPathFilterParameterSpec.getNamespaceMap().entrySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + (String)localEntry.getKey(), (String)localEntry.getValue());
    }
    transformElem.appendChild(localElement);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXPathTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */