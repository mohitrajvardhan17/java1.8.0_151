package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.params.XPath2FilterContainer;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TransformXPath2Filter
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/2002/06/xmldsig-filter2";
  
  public TransformXPath2Filter() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2002/06/xmldsig-filter2";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws TransformationException
  {
    try
    {
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      ArrayList localArrayList3 = new ArrayList();
      Element[] arrayOfElement = XMLUtils.selectNodes(paramTransform.getElement().getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath");
      if (arrayOfElement.length == 0)
      {
        localObject = new Object[] { "http://www.w3.org/2002/06/xmldsig-filter2", "XPath" };
        throw new TransformationException("xml.WrongContent", (Object[])localObject);
      }
      Object localObject = null;
      if (paramXMLSignatureInput.getSubNode() != null) {
        localObject = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
      } else {
        localObject = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
      }
      for (int i = 0; i < arrayOfElement.length; i++)
      {
        Element localElement = arrayOfElement[i];
        XPath2FilterContainer localXPath2FilterContainer = XPath2FilterContainer.newInstance(localElement, paramXMLSignatureInput.getSourceURI());
        String str = XMLUtils.getStrFromNode(localXPath2FilterContainer.getXPathFilterTextNode());
        XPathFactory localXPathFactory = XPathFactory.newInstance();
        XPathAPI localXPathAPI = localXPathFactory.newXPathAPI();
        NodeList localNodeList = localXPathAPI.selectNodeList((Node)localObject, localXPath2FilterContainer.getXPathFilterTextNode(), str, localXPath2FilterContainer.getElement());
        if (localXPath2FilterContainer.isIntersect()) {
          localArrayList3.add(localNodeList);
        } else if (localXPath2FilterContainer.isSubtract()) {
          localArrayList2.add(localNodeList);
        } else if (localXPath2FilterContainer.isUnion()) {
          localArrayList1.add(localNodeList);
        }
      }
      paramXMLSignatureInput.addNodeFilter(new XPath2NodeFilter(localArrayList1, localArrayList2, localArrayList3));
      paramXMLSignatureInput.setNodeSet(true);
      return paramXMLSignatureInput;
    }
    catch (TransformerException localTransformerException)
    {
      throw new TransformationException("empty", localTransformerException);
    }
    catch (DOMException localDOMException)
    {
      throw new TransformationException("empty", localDOMException);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new TransformationException("empty", localCanonicalizationException);
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new TransformationException("empty", localInvalidCanonicalizerException);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new TransformationException("empty", localXMLSecurityException);
    }
    catch (SAXException localSAXException)
    {
      throw new TransformationException("empty", localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new TransformationException("empty", localIOException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new TransformationException("empty", localParserConfigurationException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformXPath2Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */