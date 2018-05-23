package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Equals
  extends Operation
{
  static final long serialVersionUID = -2658315633903426134L;
  
  public Equals() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return paramXObject1.equals(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
  
  public boolean bool(XPathContext paramXPathContext)
    throws TransformerException
  {
    XObject localXObject1 = m_left.execute(paramXPathContext, true);
    XObject localXObject2 = m_right.execute(paramXPathContext, true);
    boolean bool = localXObject1.equals(localXObject2);
    localXObject1.detach();
    localXObject2.detach();
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Equals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */