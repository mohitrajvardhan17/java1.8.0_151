package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformEnvelopedSignature
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
  
  public TransformEnvelopedSignature() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws TransformationException
  {
    Object localObject = paramTransform.getElement();
    localObject = searchSignatureElement((Node)localObject);
    paramXMLSignatureInput.setExcludeNode((Node)localObject);
    paramXMLSignatureInput.addNodeFilter(new EnvelopedNodeFilter((Node)localObject));
    return paramXMLSignatureInput;
  }
  
  private static Node searchSignatureElement(Node paramNode)
    throws TransformationException
  {
    int i = 0;
    while ((paramNode != null) && (paramNode.getNodeType() != 9))
    {
      Element localElement = (Element)paramNode;
      if ((localElement.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) && (localElement.getLocalName().equals("Signature")))
      {
        i = 1;
        break;
      }
      paramNode = paramNode.getParentNode();
    }
    if (i == 0) {
      throw new TransformationException("transform.envelopedSignatureTransformNotInSignatureElement");
    }
    return paramNode;
  }
  
  static class EnvelopedNodeFilter
    implements NodeFilter
  {
    Node exclude;
    
    EnvelopedNodeFilter(Node paramNode)
    {
      exclude = paramNode;
    }
    
    public int isNodeIncludeDO(Node paramNode, int paramInt)
    {
      if (paramNode == exclude) {
        return -1;
      }
      return 1;
    }
    
    public int isNodeInclude(Node paramNode)
    {
      if ((paramNode == exclude) || (XMLUtils.isDescendantOrSelf(exclude, paramNode))) {
        return -1;
      }
      return 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformEnvelopedSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */