package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Mod
  extends Operation
{
  static final long serialVersionUID = 5009471154238918201L;
  
  public Mod() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return new XNumber(paramXObject1.num() % paramXObject2.num());
  }
  
  public double num(XPathContext paramXPathContext)
    throws TransformerException
  {
    return m_left.num(paramXPathContext) % m_right.num(paramXPathContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Mod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */