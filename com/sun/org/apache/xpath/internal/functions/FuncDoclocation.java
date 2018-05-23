package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncDoclocation
  extends FunctionDef1Arg
{
  static final long serialVersionUID = 7469213946343568769L;
  
  public FuncDoclocation() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getArg0AsNode(paramXPathContext);
    String str = null;
    if (-1 != i)
    {
      DTM localDTM = paramXPathContext.getDTM(i);
      if (11 == localDTM.getNodeType(i)) {
        i = localDTM.getFirstChild(i);
      }
      if (-1 != i) {
        str = localDTM.getDocumentBaseURI();
      }
    }
    return new XString(null != str ? str : "");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncDoclocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */