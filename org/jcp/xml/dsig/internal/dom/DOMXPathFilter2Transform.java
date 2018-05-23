package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XPathType.Filter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class DOMXPathFilter2Transform
  extends ApacheTransform
{
  public DOMXPathFilter2Transform() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec == null) {
      throw new InvalidAlgorithmParameterException("params are required");
    }
    if (!(paramTransformParameterSpec instanceof XPathFilter2ParameterSpec)) {
      throw new InvalidAlgorithmParameterException("params must be of type XPathFilter2ParameterSpec");
    }
    params = paramTransformParameterSpec;
  }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws InvalidAlgorithmParameterException
  {
    super.init(paramXMLStructure, paramXMLCryptoContext);
    try
    {
      unmarshalParams(DOMUtils.getFirstChildElement(transformElem));
    }
    catch (MarshalException localMarshalException)
    {
      throw new InvalidAlgorithmParameterException(localMarshalException);
    }
  }
  
  private void unmarshalParams(Element paramElement)
    throws MarshalException
  {
    ArrayList localArrayList = new ArrayList();
    while (paramElement != null)
    {
      String str1 = paramElement.getFirstChild().getNodeValue();
      String str2 = DOMUtils.getAttributeValue(paramElement, "Filter");
      if (str2 == null) {
        throw new MarshalException("filter cannot be null");
      }
      XPathType.Filter localFilter = null;
      if (str2.equals("intersect")) {
        localFilter = XPathType.Filter.INTERSECT;
      } else if (str2.equals("subtract")) {
        localFilter = XPathType.Filter.SUBTRACT;
      } else if (str2.equals("union")) {
        localFilter = XPathType.Filter.UNION;
      } else {
        throw new MarshalException("Unknown XPathType filter type" + str2);
      }
      NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
      if (localNamedNodeMap != null)
      {
        int i = localNamedNodeMap.getLength();
        HashMap localHashMap = new HashMap(i);
        for (int j = 0; j < i; j++)
        {
          Attr localAttr = (Attr)localNamedNodeMap.item(j);
          String str3 = localAttr.getPrefix();
          if ((str3 != null) && (str3.equals("xmlns"))) {
            localHashMap.put(localAttr.getLocalName(), localAttr.getValue());
          }
        }
        localArrayList.add(new XPathType(str1, localFilter, localHashMap));
      }
      else
      {
        localArrayList.add(new XPathType(str1, localFilter));
      }
      paramElement = DOMUtils.getNextSiblingElement(paramElement);
    }
    params = new XPathFilter2ParameterSpec(localArrayList);
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    super.marshalParams(paramXMLStructure, paramXMLCryptoContext);
    XPathFilter2ParameterSpec localXPathFilter2ParameterSpec = (XPathFilter2ParameterSpec)getParameterSpec();
    String str1 = DOMUtils.getNSPrefix(paramXMLCryptoContext, "http://www.w3.org/2002/06/xmldsig-filter2");
    String str2 = "xmlns:" + str1;
    List localList = localXPathFilter2ParameterSpec.getXPathList();
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      XPathType localXPathType = (XPathType)localIterator1.next();
      Element localElement = DOMUtils.createElement(ownerDoc, "XPath", "http://www.w3.org/2002/06/xmldsig-filter2", str1);
      localElement.appendChild(ownerDoc.createTextNode(localXPathType.getExpression()));
      DOMUtils.setAttribute(localElement, "Filter", localXPathType.getFilter().toString());
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str2, "http://www.w3.org/2002/06/xmldsig-filter2");
      Set localSet = localXPathType.getNamespaceMap().entrySet();
      Iterator localIterator2 = localSet.iterator();
      while (localIterator2.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator2.next();
        localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + (String)localEntry.getKey(), (String)localEntry.getValue());
      }
      transformElem.appendChild(localElement);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXPathFilter2Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */