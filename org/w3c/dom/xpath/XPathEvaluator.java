package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract interface XPathEvaluator
{
  public abstract XPathExpression createExpression(String paramString, XPathNSResolver paramXPathNSResolver)
    throws XPathException, DOMException;
  
  public abstract XPathNSResolver createNSResolver(Node paramNode);
  
  public abstract Object evaluate(String paramString, Node paramNode, XPathNSResolver paramXPathNSResolver, short paramShort, Object paramObject)
    throws XPathException, DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\xpath\XPathEvaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */