package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncUnparsedEntityURI
  extends FunctionOneArg
{
  static final long serialVersionUID = 845309759097448178L;
  
  public FuncUnparsedEntityURI() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    String str1 = m_arg0.execute(paramXPathContext).str();
    int i = paramXPathContext.getCurrentNode();
    DTM localDTM = paramXPathContext.getDTM(i);
    int j = localDTM.getDocument();
    String str2 = localDTM.getUnparsedEntityURI(str1);
    return new XString(str2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncUnparsedEntityURI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */