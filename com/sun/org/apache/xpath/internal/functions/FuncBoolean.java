package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncBoolean
  extends FunctionOneArg
{
  static final long serialVersionUID = 4328660760070034592L;
  
  public FuncBoolean() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return m_arg0.execute(paramXPathContext).bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */