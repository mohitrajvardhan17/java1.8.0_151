package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class And
  extends Operation
{
  static final long serialVersionUID = 392330077126534022L;
  
  public And() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XObject localXObject1 = m_left.execute(paramXPathContext);
    if (localXObject1.bool())
    {
      XObject localXObject2 = m_right.execute(paramXPathContext);
      return localXObject2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    return XBoolean.S_FALSE;
  }
  
  public boolean bool(XPathContext paramXPathContext)
    throws TransformerException
  {
    return (m_left.bool(paramXPathContext)) && (m_right.bool(paramXPathContext));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\And.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */