package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Div
  extends Operation
{
  static final long serialVersionUID = 6220756595959798135L;
  
  public Div() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return new XNumber(paramXObject1.num() / paramXObject2.num());
  }
  
  public double num(XPathContext paramXPathContext)
    throws TransformerException
  {
    return m_left.num(paramXPathContext) / m_right.num(paramXPathContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Div.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */