package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncFloor
  extends FunctionOneArg
{
  static final long serialVersionUID = 2326752233236309265L;
  
  public FuncFloor() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return new XNumber(Math.floor(m_arg0.execute(paramXPathContext).num()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncFloor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */