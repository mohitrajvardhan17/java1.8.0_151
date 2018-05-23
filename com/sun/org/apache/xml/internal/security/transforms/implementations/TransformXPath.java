package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import java.io.OutputStream;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TransformXPath
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xpath-19991116";
  
  public TransformXPath() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/TR/1999/REC-xpath-19991116";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws TransformationException
  {
    try
    {
      Element localElement = XMLUtils.selectDsNode(paramTransform.getElement().getFirstChild(), "XPath", 0);
      if (localElement == null)
      {
        localObject = new Object[] { "ds:XPath", "Transform" };
        throw new TransformationException("xml.WrongContent", (Object[])localObject);
      }
      Object localObject = localElement.getChildNodes().item(0);
      String str = XMLUtils.getStrFromNode((Node)localObject);
      paramXMLSignatureInput.setNeedsToBeExpanded(needsCircumvent(str));
      if (localObject == null) {
        throw new DOMException((short)3, "Text must be in ds:Xpath");
      }
      XPathFactory localXPathFactory = XPathFactory.newInstance();
      XPathAPI localXPathAPI = localXPathFactory.newXPathAPI();
      paramXMLSignatureInput.addNodeFilter(new XPathNodeFilter(localElement, (Node)localObject, str, localXPathAPI));
      paramXMLSignatureInput.setNodeSet(true);
      return paramXMLSignatureInput;
    }
    catch (DOMException localDOMException)
    {
      throw new TransformationException("empty", localDOMException);
    }
  }
  
  private boolean needsCircumvent(String paramString)
  {
    return (paramString.indexOf("namespace") != -1) || (paramString.indexOf("name()") != -1);
  }
  
  static class XPathNodeFilter
    implements NodeFilter
  {
    XPathAPI xPathAPI;
    Node xpathnode;
    Element xpathElement;
    String str;
    
    XPathNodeFilter(Element paramElement, Node paramNode, String paramString, XPathAPI paramXPathAPI)
    {
      xpathnode = paramNode;
      str = paramString;
      xpathElement = paramElement;
      xPathAPI = paramXPathAPI;
    }
    
    public int isNodeInclude(Node paramNode)
    {
      try
      {
        boolean bool = xPathAPI.evaluate(paramNode, xpathnode, str, xpathElement);
        if (bool) {
          return 1;
        }
        return 0;
      }
      catch (TransformerException localTransformerException)
      {
        arrayOfObject = new Object[] { paramNode };
        throw new XMLSecurityRuntimeException("signature.Transform.node", arrayOfObject, localTransformerException);
      }
      catch (Exception localException)
      {
        Object[] arrayOfObject = { paramNode, Short.valueOf(paramNode.getNodeType()) };
        throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType", arrayOfObject, localException);
      }
    }
    
    public int isNodeIncludeDO(Node paramNode, int paramInt)
    {
      return isNodeInclude(paramNode);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformXPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */