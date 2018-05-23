package org.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMSignatureProperties
  extends DOMStructure
  implements SignatureProperties
{
  private final String id;
  private final List<SignatureProperty> properties;
  
  public DOMSignatureProperties(List<? extends SignatureProperty> paramList, String paramString)
  {
    if (paramList == null) {
      throw new NullPointerException("properties cannot be null");
    }
    if (paramList.isEmpty()) {
      throw new IllegalArgumentException("properties cannot be empty");
    }
    properties = Collections.unmodifiableList(new ArrayList(paramList));
    int i = 0;
    int j = properties.size();
    while (i < j)
    {
      if (!(properties.get(i) instanceof SignatureProperty)) {
        throw new ClassCastException("properties[" + i + "] is not a valid type");
      }
      i++;
    }
    id = paramString;
  }
  
  public DOMSignatureProperties(Element paramElement, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    Attr localAttr = paramElement.getAttributeNodeNS(null, "Id");
    if (localAttr != null)
    {
      id = localAttr.getValue();
      paramElement.setIdAttributeNode(localAttr, true);
    }
    else
    {
      id = null;
    }
    NodeList localNodeList = paramElement.getChildNodes();
    int i = localNodeList.getLength();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Node localNode = localNodeList.item(j);
      if (localNode.getNodeType() == 1)
      {
        String str = localNode.getLocalName();
        if (!str.equals("SignatureProperty")) {
          throw new MarshalException("Invalid element name: " + str + ", expected SignatureProperty");
        }
        localArrayList.add(new DOMSignatureProperty((Element)localNode, paramXMLCryptoContext));
      }
    }
    if (localArrayList.isEmpty()) {
      throw new MarshalException("properties cannot be empty");
    }
    properties = Collections.unmodifiableList(localArrayList);
  }
  
  public List getProperties()
  {
    return properties;
  }
  
  public String getId()
  {
    return id;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "SignatureProperties", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(localElement, "Id", id);
    Iterator localIterator = properties.iterator();
    while (localIterator.hasNext())
    {
      SignatureProperty localSignatureProperty = (SignatureProperty)localIterator.next();
      ((DOMSignatureProperty)localSignatureProperty).marshal(localElement, paramString, paramDOMCryptoContext);
    }
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SignatureProperties)) {
      return false;
    }
    SignatureProperties localSignatureProperties = (SignatureProperties)paramObject;
    boolean bool = id == null ? false : localSignatureProperties.getId() == null ? true : id.equals(localSignatureProperties.getId());
    return (properties.equals(localSignatureProperties.getProperties())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    i = 31 * i + properties.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSignatureProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */