package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncSum
  extends FunctionOneArg
{
  static final long serialVersionUID = -2719049259574677519L;
  
  public FuncSum() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    DTMIterator localDTMIterator = m_arg0.asIterator(paramXPathContext, paramXPathContext.getCurrentNode());
    double d = 0.0D;
    int i;
    while (-1 != (i = localDTMIterator.nextNode()))
    {
      DTM localDTM = localDTMIterator.getDTM(i);
      XMLString localXMLString = localDTM.getStringValue(i);
      if (null != localXMLString) {
        d += localXMLString.toDouble();
      }
    }
    localDTMIterator.detach();
    return new XNumber(d);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */