package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract interface XPathExpression
{
  public abstract Object evaluate(Node paramNode, short paramShort, Object paramObject)
    throws XPathException, DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\xpath\XPathExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */