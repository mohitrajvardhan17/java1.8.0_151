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
import javax.xml.crypto.dsig.XMLObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMXMLObject
  extends DOMStructure
  implements XMLObject
{
  private final String id;
  private final String mimeType;
  private final String encoding;
  private final List<XMLStructure> content;
  private Element objectElem;
  
  public DOMXMLObject(List<? extends XMLStructure> paramList, String paramString1, String paramString2, String paramString3)
  {
    if ((paramList == null) || (paramList.isEmpty()))
    {
      content = Collections.emptyList();
    }
    else
    {
      content = Collections.unmodifiableList(new ArrayList(paramList));
      int i = 0;
      int j = content.size();
      while (i < j)
      {
        if (!(content.get(i) instanceof XMLStructure)) {
          throw new ClassCastException("content[" + i + "] is not a valid type");
        }
        i++;
      }
    }
    id = paramString1;
    mimeType = paramString2;
    encoding = paramString3;
  }
  
  public DOMXMLObject(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    encoding = DOMUtils.getAttributeValue(paramElement, "Encoding");
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
    mimeType = DOMUtils.getAttributeValue(paramElement, "MimeType");
    NodeList localNodeList = paramElement.getChildNodes();
    int i = localNodeList.getLength();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Node localNode = localNodeList.item(j);
      if (localNode.getNodeType() == 1)
      {
        Element localElement = (Element)localNode;
        String str = localElement.getLocalName();
        if (str.equals("Manifest"))
        {
          localArrayList.add(new DOMManifest(localElement, paramXMLCryptoContext, paramProvider));
          continue;
        }
        if (str.equals("SignatureProperties"))
        {
          localArrayList.add(new DOMSignatureProperties(localElement, paramXMLCryptoContext));
          continue;
        }
        if (str.equals("X509Data"))
        {
          localArrayList.add(new DOMX509Data(localElement));
          continue;
        }
      }
      localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localNode));
    }
    if (localArrayList.isEmpty()) {
      content = Collections.emptyList();
    } else {
      content = Collections.unmodifiableList(localArrayList);
    }
    objectElem = paramElement;
  }
  
  public List getContent()
  {
    return content;
  }
  
  public String getId()
  {
    return id;
  }
  
  public String getMimeType()
  {
    return mimeType;
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = objectElem != null ? objectElem : null;
    if (localElement == null)
    {
      localElement = DOMUtils.createElement(localDocument, "Object", "http://www.w3.org/2000/09/xmldsig#", paramString);
      DOMUtils.setAttributeID(localElement, "Id", id);
      DOMUtils.setAttribute(localElement, "MimeType", mimeType);
      DOMUtils.setAttribute(localElement, "Encoding", encoding);
      Iterator localIterator = content.iterator();
      while (localIterator.hasNext())
      {
        XMLStructure localXMLStructure = (XMLStructure)localIterator.next();
        if ((localXMLStructure instanceof DOMStructure))
        {
          ((DOMStructure)localXMLStructure).marshal(localElement, paramString, paramDOMCryptoContext);
        }
        else
        {
          javax.xml.crypto.dom.DOMStructure localDOMStructure = (javax.xml.crypto.dom.DOMStructure)localXMLStructure;
          DOMUtils.appendChild(localElement, localDOMStructure.getNode());
        }
      }
    }
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof XMLObject)) {
      return false;
    }
    XMLObject localXMLObject = (XMLObject)paramObject;
    boolean bool1 = id == null ? false : localXMLObject.getId() == null ? true : id.equals(localXMLObject.getId());
    boolean bool2 = encoding == null ? false : localXMLObject.getEncoding() == null ? true : encoding.equals(localXMLObject.getEncoding());
    boolean bool3 = mimeType == null ? false : localXMLObject.getMimeType() == null ? true : mimeType.equals(localXMLObject.getMimeType());
    List localList = localXMLObject.getContent();
    return (bool1) && (bool2) && (bool3) && (equalsContent(localList));
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    if (encoding != null) {
      i = 31 * i + encoding.hashCode();
    }
    if (mimeType != null) {
      i = 31 * i + mimeType.hashCode();
    }
    i = 31 * i + content.hashCode();
    return i;
  }
  
  private boolean equalsContent(List<XMLStructure> paramList)
  {
    if (content.size() != paramList.size()) {
      return false;
    }
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      XMLStructure localXMLStructure1 = (XMLStructure)paramList.get(i);
      XMLStructure localXMLStructure2 = (XMLStructure)content.get(i);
      if ((localXMLStructure1 instanceof javax.xml.crypto.dom.DOMStructure))
      {
        if (!(localXMLStructure2 instanceof javax.xml.crypto.dom.DOMStructure)) {
          return false;
        }
        Node localNode1 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure1).getNode();
        Node localNode2 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure2).getNode();
        if (!DOMUtils.nodesEqual(localNode2, localNode1)) {
          return false;
        }
      }
      else if (!localXMLStructure2.equals(localXMLStructure1))
      {
        return false;
      }
      i++;
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXMLObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */