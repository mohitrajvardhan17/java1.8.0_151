package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;

public final class XPathEvaluatorImpl
  implements XPathEvaluator
{
  private final Document m_doc;
  
  public XPathEvaluatorImpl(Document paramDocument)
  {
    m_doc = paramDocument;
  }
  
  public XPathEvaluatorImpl()
  {
    m_doc = null;
  }
  
  public XPathExpression createExpression(String paramString, XPathNSResolver paramXPathNSResolver)
    throws XPathException, DOMException
  {
    try
    {
      XPath localXPath = new XPath(paramString, null, null == paramXPathNSResolver ? new DummyPrefixResolver() : (PrefixResolver)paramXPathNSResolver, 0);
      return new XPathExpressionImpl(localXPath, m_doc);
    }
    catch (TransformerException localTransformerException)
    {
      if ((localTransformerException instanceof XPathStylesheetDOM3Exception)) {
        throw new DOMException((short)14, localTransformerException.getMessageAndLocation());
      }
      throw new XPathException((short)1, localTransformerException.getMessageAndLocation());
    }
  }
  
  public XPathNSResolver createNSResolver(Node paramNode)
  {
    return new XPathNSResolverImpl(paramNode.getNodeType() == 9 ? ((Document)paramNode).getDocumentElement() : paramNode);
  }
  
  public Object evaluate(String paramString, Node paramNode, XPathNSResolver paramXPathNSResolver, short paramShort, Object paramObject)
    throws XPathException, DOMException
  {
    XPathExpression localXPathExpression = createExpression(paramString, paramXPathNSResolver);
    return localXPathExpression.evaluate(paramNode, paramShort, paramObject);
  }
  
  private class DummyPrefixResolver
    implements PrefixResolver
  {
    DummyPrefixResolver() {}
    
    public String getNamespaceForPrefix(String paramString, Node paramNode)
    {
      String str = XPATHMessages.createXPATHMessage("ER_NULL_RESOLVER", null);
      throw new DOMException((short)14, str);
    }
    
    public String getNamespaceForPrefix(String paramString)
    {
      return getNamespaceForPrefix(paramString, null);
    }
    
    public boolean handlesNullPrefixes()
    {
      return false;
    }
    
    public String getBaseIdentifier()
    {
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathEvaluatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */