package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncGenerateId
  extends FunctionDef1Arg
{
  static final long serialVersionUID = 973544842091724273L;
  
  public FuncGenerateId() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getArg0AsNode(paramXPathContext);
    if (-1 != i) {
      return new XString("N" + Integer.toHexString(i).toUpperCase());
    }
    return XString.EMPTYSTRING;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncGenerateId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */