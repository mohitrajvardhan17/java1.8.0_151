package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Neg
  extends UnaryOperation
{
  static final long serialVersionUID = -6280607702375702291L;
  
  public Neg() {}
  
  public XObject operate(XObject paramXObject)
    throws TransformerException
  {
    return new XNumber(-paramXObject.num());
  }
  
  public double num(XPathContext paramXPathContext)
    throws TransformerException
  {
    return -m_right.num(paramXPathContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Neg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */