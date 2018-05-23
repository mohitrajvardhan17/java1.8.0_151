package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMRetrievalMethod
  extends DOMStructure
  implements RetrievalMethod, DOMURIReference
{
  private final List<Transform> transforms;
  private String uri;
  private String type;
  private Attr here;
  
  public DOMRetrievalMethod(String paramString1, String paramString2, List<? extends Transform> paramList)
  {
    if (paramString1 == null) {
      throw new NullPointerException("uri cannot be null");
    }
    if ((paramList == null) || (paramList.isEmpty()))
    {
      transforms = Collections.emptyList();
    }
    else
    {
      transforms = Collections.unmodifiableList(new ArrayList(paramList));
      int i = 0;
      int j = transforms.size();
      while (i < j)
      {
        if (!(transforms.get(i) instanceof Transform)) {
          throw new ClassCastException("transforms[" + i + "] is not a valid type");
        }
        i++;
      }
    }
    uri = paramString1;
    if (!paramString1.equals("")) {
      try
      {
        new URI(paramString1);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new IllegalArgumentException(localURISyntaxException.getMessage());
      }
    }
    type = paramString2;
  }
  
  public DOMRetrievalMethod(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    uri = DOMUtils.getAttributeValue(paramElement, "URI");
    type = DOMUtils.getAttributeValue(paramElement, "Type");
    here = paramElement.getAttributeNodeNS(null, "URI");
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    ArrayList localArrayList = new ArrayList();
    Element localElement1 = DOMUtils.getFirstChildElement(paramElement);
    if (localElement1 != null)
    {
      String str1 = localElement1.getLocalName();
      if (!str1.equals("Transforms")) {
        throw new MarshalException("Invalid element name: " + str1 + ", expected Transforms");
      }
      Element localElement2 = DOMUtils.getFirstChildElement(localElement1, "Transform");
      localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
      for (localElement2 = DOMUtils.getNextSiblingElement(localElement2); localElement2 != null; localElement2 = DOMUtils.getNextSiblingElement(localElement2))
      {
        String str2 = localElement2.getLocalName();
        if (!str2.equals("Transform")) {
          throw new MarshalException("Invalid element name: " + str2 + ", expected Transform");
        }
        localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
        if ((bool) && (Policy.restrictNumTransforms(localArrayList.size())))
        {
          String str3 = "A maximum of " + Policy.maxTransforms() + " transforms per Reference are allowed when secure validation is enabled";
          throw new MarshalException(str3);
        }
      }
    }
    if (localArrayList.isEmpty()) {
      transforms = Collections.emptyList();
    } else {
      transforms = Collections.unmodifiableList(localArrayList);
    }
  }
  
  public String getURI()
  {
    return uri;
  }
  
  public String getType()
  {
    return type;
  }
  
  public List getTransforms()
  {
    return transforms;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement1 = DOMUtils.createElement(localDocument, "RetrievalMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttribute(localElement1, "URI", uri);
    DOMUtils.setAttribute(localElement1, "Type", type);
    if (!transforms.isEmpty())
    {
      Element localElement2 = DOMUtils.createElement(localDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
      localElement1.appendChild(localElement2);
      Iterator localIterator = transforms.iterator();
      while (localIterator.hasNext())
      {
        Transform localTransform = (Transform)localIterator.next();
        ((DOMTransform)localTransform).marshal(localElement2, paramString, paramDOMCryptoContext);
      }
    }
    paramNode.appendChild(localElement1);
    here = localElement1.getAttributeNodeNS(null, "URI");
  }
  
  public Node getHere()
  {
    return here;
  }
  
  public Data dereference(XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException
  {
    if (paramXMLCryptoContext == null) {
      throw new NullPointerException("context cannot be null");
    }
    URIDereferencer localURIDereferencer = paramXMLCryptoContext.getURIDereferencer();
    if (localURIDereferencer == null) {
      localURIDereferencer = DOMURIDereferencer.INSTANCE;
    }
    Data localData = localURIDereferencer.dereference(this, paramXMLCryptoContext);
    Object localObject;
    try
    {
      Iterator localIterator = transforms.iterator();
      while (localIterator.hasNext())
      {
        localObject = (Transform)localIterator.next();
        localData = ((DOMTransform)localObject).transform(localData, paramXMLCryptoContext);
      }
    }
    catch (Exception localException)
    {
      throw new URIReferenceException(localException);
    }
    if (((localData instanceof NodeSetData)) && (Utils.secureValidation(paramXMLCryptoContext)) && (Policy.restrictRetrievalMethodLoops()))
    {
      NodeSetData localNodeSetData = (NodeSetData)localData;
      localObject = localNodeSetData.iterator();
      if (((Iterator)localObject).hasNext())
      {
        Node localNode = (Node)((Iterator)localObject).next();
        if ("RetrievalMethod".equals(localNode.getLocalName())) {
          throw new URIReferenceException("It is forbidden to have one RetrievalMethod point to another when secure validation is enabled");
        }
      }
    }
    return localData;
  }
  
  public XMLStructure dereferenceAsXMLStructure(XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException
  {
    try
    {
      ApacheData localApacheData = (ApacheData)dereference(paramXMLCryptoContext);
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(localApacheData.getXMLSignatureInput().getBytes()));
      Element localElement = localDocument.getDocumentElement();
      if (localElement.getLocalName().equals("X509Data")) {
        return new DOMX509Data(localElement);
      }
      return null;
    }
    catch (Exception localException)
    {
      throw new URIReferenceException(localException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof RetrievalMethod)) {
      return false;
    }
    RetrievalMethod localRetrievalMethod = (RetrievalMethod)paramObject;
    boolean bool = type == null ? false : localRetrievalMethod.getType() == null ? true : type.equals(localRetrievalMethod.getType());
    return (uri.equals(localRetrievalMethod.getURI())) && (transforms.equals(localRetrievalMethod.getTransforms())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (type != null) {
      i = 31 * i + type.hashCode();
    }
    i = 31 * i + uri.hashCode();
    i = 31 * i + transforms.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMRetrievalMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */