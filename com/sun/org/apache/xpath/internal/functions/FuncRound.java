package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncRound
  extends FunctionOneArg
{
  static final long serialVersionUID = -7970583902573826611L;
  
  public FuncRound() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XObject localXObject = m_arg0.execute(paramXPathContext);
    double d = localXObject.num();
    if ((d >= -0.5D) && (d < 0.0D)) {
      return new XNumber(-0.0D);
    }
    if (d == 0.0D) {
      return new XNumber(d);
    }
    return new XNumber(Math.floor(d + 0.5D));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncRound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */