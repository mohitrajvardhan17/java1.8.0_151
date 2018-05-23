package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Bool
  extends UnaryOperation
{
  static final long serialVersionUID = 44705375321914635L;
  
  public Bool() {}
  
  public XObject operate(XObject paramXObject)
    throws TransformerException
  {
    if (1 == paramXObject.getType()) {
      return paramXObject;
    }
    return paramXObject.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
  
  public boolean bool(XPathContext paramXPathContext)
    throws TransformerException
  {
    return m_right.bool(paramXPathContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Bool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */