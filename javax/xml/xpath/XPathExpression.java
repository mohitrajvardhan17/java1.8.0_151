package javax.xml.xpath;

import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

public abstract interface XPathExpression
{
  public abstract Object evaluate(Object paramObject, QName paramQName)
    throws XPathExpressionException;
  
  public abstract String evaluate(Object paramObject)
    throws XPathExpressionException;
  
  public abstract Object evaluate(InputSource paramInputSource, QName paramQName)
    throws XPathExpressionException;
  
  public abstract String evaluate(InputSource paramInputSource)
    throws XPathExpressionException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\xpath\XPathExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */