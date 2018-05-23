package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class CachedXPathAPI
{
  protected XPathContext xpathSupport;
  
  public CachedXPathAPI()
  {
    xpathSupport = new XPathContext();
  }
  
  public CachedXPathAPI(CachedXPathAPI paramCachedXPathAPI)
  {
    xpathSupport = xpathSupport;
  }
  
  public XPathContext getXPathContext()
  {
    return xpathSupport;
  }
  
  public Node selectSingleNode(Node paramNode, String paramString)
    throws TransformerException
  {
    return selectSingleNode(paramNode, paramString, paramNode);
  }
  
  public Node selectSingleNode(Node paramNode1, String paramString, Node paramNode2)
    throws TransformerException
  {
    NodeIterator localNodeIterator = selectNodeIterator(paramNode1, paramString, paramNode2);
    return localNodeIterator.nextNode();
  }
  
  public NodeIterator selectNodeIterator(Node paramNode, String paramString)
    throws TransformerException
  {
    return selectNodeIterator(paramNode, paramString, paramNode);
  }
  
  public NodeIterator selectNodeIterator(Node paramNode1, String paramString, Node paramNode2)
    throws TransformerException
  {
    XObject localXObject = eval(paramNode1, paramString, paramNode2);
    return localXObject.nodeset();
  }
  
  public NodeList selectNodeList(Node paramNode, String paramString)
    throws TransformerException
  {
    return selectNodeList(paramNode, paramString, paramNode);
  }
  
  public NodeList selectNodeList(Node paramNode1, String paramString, Node paramNode2)
    throws TransformerException
  {
    XObject localXObject = eval(paramNode1, paramString, paramNode2);
    return localXObject.nodelist();
  }
  
  public XObject eval(Node paramNode, String paramString)
    throws TransformerException
  {
    return eval(paramNode, paramString, paramNode);
  }
  
  public XObject eval(Node paramNode1, String paramString, Node paramNode2)
    throws TransformerException
  {
    PrefixResolverDefault localPrefixResolverDefault = new PrefixResolverDefault(paramNode2.getNodeType() == 9 ? ((Document)paramNode2).getDocumentElement() : paramNode2);
    XPath localXPath = new XPath(paramString, null, localPrefixResolverDefault, 0, null);
    int i = xpathSupport.getDTMHandleFromNode(paramNode1);
    return localXPath.execute(xpathSupport, i, localPrefixResolverDefault);
  }
  
  public XObject eval(Node paramNode, String paramString, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    XPath localXPath = new XPath(paramString, null, paramPrefixResolver, 0, null);
    XPathContext localXPathContext = new XPathContext();
    int i = localXPathContext.getDTMHandleFromNode(paramNode);
    return localXPath.execute(localXPathContext, i, paramPrefixResolver);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\CachedXPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */