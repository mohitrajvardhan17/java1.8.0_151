package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMManifest
  extends DOMStructure
  implements Manifest
{
  private final List<Reference> references;
  private final String id;
  
  public DOMManifest(List<? extends Reference> paramList, String paramString)
  {
    if (paramList == null) {
      throw new NullPointerException("references cannot be null");
    }
    references = Collections.unmodifiableList(new ArrayList(paramList));
    if (references.isEmpty()) {
      throw new IllegalArgumentException("list of references must contain at least one entry");
    }
    int i = 0;
    int j = references.size();
    while (i < j)
    {
      if (!(references.get(i) instanceof Reference)) {
        throw new ClassCastException("references[" + i + "] is not a valid type");
      }
      i++;
    }
    id = paramString;
  }
  
  public DOMManifest(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
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
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Element localElement = DOMUtils.getFirstChildElement(paramElement, "Reference");
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new DOMReference(localElement, paramXMLCryptoContext, paramProvider));
    for (localElement = DOMUtils.getNextSiblingElement(localElement); localElement != null; localElement = DOMUtils.getNextSiblingElement(localElement))
    {
      String str1 = localElement.getLocalName();
      if (!str1.equals("Reference")) {
        throw new MarshalException("Invalid element name: " + str1 + ", expected Reference");
      }
      localArrayList.add(new DOMReference(localElement, paramXMLCryptoContext, paramProvider));
      if ((bool) && (Policy.restrictNumReferences(localArrayList.size())))
      {
        String str2 = "A maximum of " + Policy.maxReferences() + " references per Manifest are allowed when secure validation is enabled";
        throw new MarshalException(str2);
      }
    }
    references = Collections.unmodifiableList(localArrayList);
  }
  
  public String getId()
  {
    return id;
  }
  
  public List getReferences()
  {
    return references;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "Manifest", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(localElement, "Id", id);
    Iterator localIterator = references.iterator();
    while (localIterator.hasNext())
    {
      Reference localReference = (Reference)localIterator.next();
      ((DOMReference)localReference).marshal(localElement, paramString, paramDOMCryptoContext);
    }
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Manifest)) {
      return false;
    }
    Manifest localManifest = (Manifest)paramObject;
    boolean bool = id == null ? false : localManifest.getId() == null ? true : id.equals(localManifest.getId());
    return (bool) && (references.equals(localManifest.getReferences()));
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    i = 31 * i + references.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMManifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */