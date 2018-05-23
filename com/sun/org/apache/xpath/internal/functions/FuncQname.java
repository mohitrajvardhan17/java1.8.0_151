package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncQname
  extends FunctionDef1Arg
{
  static final long serialVersionUID = -1532307875532617380L;
  
  public FuncQname() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getArg0AsNode(paramXPathContext);
    XString localXString;
    if (-1 != i)
    {
      DTM localDTM = paramXPathContext.getDTM(i);
      String str = localDTM.getNodeNameX(i);
      localXString = null == str ? XString.EMPTYSTRING : new XString(str);
    }
    else
    {
      localXString = XString.EMPTYSTRING;
    }
    return localXString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncQname.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */