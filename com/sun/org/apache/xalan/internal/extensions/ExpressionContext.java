package com.sun.org.apache.xalan.internal.extensions;

import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public abstract interface ExpressionContext
{
  public abstract Node getContextNode();
  
  public abstract NodeIterator getContextNodes();
  
  public abstract ErrorListener getErrorListener();
  
  public abstract double toNumber(Node paramNode);
  
  public abstract String toString(Node paramNode);
  
  public abstract XObject getVariableOrParam(QName paramQName)
    throws TransformerException;
  
  public abstract XPathContext getXPathContext()
    throws TransformerException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\extensions\ExpressionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */