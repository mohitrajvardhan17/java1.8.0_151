package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMKeyInfo
  extends DOMStructure
  implements KeyInfo
{
  private final String id;
  private final List<XMLStructure> keyInfoTypes;
  
  public DOMKeyInfo(List<? extends XMLStructure> paramList, String paramString)
  {
    if (paramList == null) {
      throw new NullPointerException("content cannot be null");
    }
    keyInfoTypes = Collections.unmodifiableList(new ArrayList(paramList));
    if (keyInfoTypes.isEmpty()) {
      throw new IllegalArgumentException("content cannot be empty");
    }
    int i = 0;
    int j = keyInfoTypes.size();
    while (i < j)
    {
      if (!(keyInfoTypes.get(i) instanceof XMLStructure)) {
        throw new ClassCastException("content[" + i + "] is not a valid KeyInfo type");
      }
      i++;
    }
    id = paramString;
  }
  
  public DOMKeyInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
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
    if (i < 1) {
      throw new MarshalException("KeyInfo must contain at least one type");
    }
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Node localNode = localNodeList.item(j);
      if (localNode.getNodeType() == 1)
      {
        Element localElement = (Element)localNode;
        String str = localElement.getLocalName();
        if (str.equals("X509Data")) {
          localArrayList.add(new DOMX509Data(localElement));
        } else if (str.equals("KeyName")) {
          localArrayList.add(new DOMKeyName(localElement));
        } else if (str.equals("KeyValue")) {
          localArrayList.add(DOMKeyValue.unmarshal(localElement));
        } else if (str.equals("RetrievalMethod")) {
          localArrayList.add(new DOMRetrievalMethod(localElement, paramXMLCryptoContext, paramProvider));
        } else if (str.equals("PGPData")) {
          localArrayList.add(new DOMPGPData(localElement));
        } else {
          localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localElement));
        }
      }
    }
    keyInfoTypes = Collections.unmodifiableList(localArrayList);
  }
  
  public String getId()
  {
    return id;
  }
  
  public List getContent()
  {
    return keyInfoTypes;
  }
  
  public void marshal(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    if (paramXMLStructure == null) {
      throw new NullPointerException("parent is null");
    }
    if (!(paramXMLStructure instanceof javax.xml.crypto.dom.DOMStructure)) {
      throw new ClassCastException("parent must be of type DOMStructure");
    }
    Node localNode = ((javax.xml.crypto.dom.DOMStructure)paramXMLStructure).getNode();
    String str = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
    Element localElement = DOMUtils.createElement(DOMUtils.getOwnerDocument(localNode), "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", str);
    if ((str == null) || (str.length() == 0)) {
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
    }
    marshal(localNode, localElement, null, str, (DOMCryptoContext)paramXMLCryptoContext);
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    marshal(paramNode, null, paramString, paramDOMCryptoContext);
  }
  
  public void marshal(Node paramNode1, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode1);
    Element localElement = DOMUtils.createElement(localDocument, "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
    marshal(paramNode1, localElement, paramNode2, paramString, paramDOMCryptoContext);
  }
  
  private void marshal(Node paramNode1, Element paramElement, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Iterator localIterator = keyInfoTypes.iterator();
    while (localIterator.hasNext())
    {
      XMLStructure localXMLStructure = (XMLStructure)localIterator.next();
      if ((localXMLStructure instanceof DOMStructure)) {
        ((DOMStructure)localXMLStructure).marshal(paramElement, paramString, paramDOMCryptoContext);
      } else {
        DOMUtils.appendChild(paramElement, ((javax.xml.crypto.dom.DOMStructure)localXMLStructure).getNode());
      }
    }
    DOMUtils.setAttributeID(paramElement, "Id", id);
    paramNode1.insertBefore(paramElement, paramNode2);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KeyInfo)) {
      return false;
    }
    KeyInfo localKeyInfo = (KeyInfo)paramObject;
    boolean bool = id == null ? false : localKeyInfo.getId() == null ? true : id.equals(localKeyInfo.getId());
    return (keyInfoTypes.equals(localKeyInfo.getContent())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    i = 31 * i + keyInfoTypes.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMKeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */