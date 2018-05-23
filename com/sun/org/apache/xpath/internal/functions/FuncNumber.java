package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncNumber
  extends FunctionDef1Arg
{
  static final long serialVersionUID = 7266745342264153076L;
  
  public FuncNumber() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return new XNumber(getArg0AsNumber(paramXPathContext));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */