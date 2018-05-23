package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncCount
  extends FunctionOneArg
{
  static final long serialVersionUID = -7116225100474153751L;
  
  public FuncCount() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    DTMIterator localDTMIterator = m_arg0.asIterator(paramXPathContext, paramXPathContext.getCurrentNode());
    int i = localDTMIterator.getLength();
    localDTMIterator.detach();
    return new XNumber(i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */