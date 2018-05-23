package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstring
  extends Function3Args
{
  static final long serialVersionUID = -5996676095024715502L;
  
  public FuncSubstring() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XMLString localXMLString1 = m_arg0.execute(paramXPathContext).xstr();
    double d1 = m_arg1.execute(paramXPathContext).num();
    int i = localXMLString1.length();
    if (i <= 0) {
      return XString.EMPTYSTRING;
    }
    int j;
    if (Double.isNaN(d1))
    {
      d1 = -1000000.0D;
      j = 0;
    }
    else
    {
      d1 = Math.round(d1);
      j = d1 > 0.0D ? (int)d1 - 1 : 0;
    }
    XMLString localXMLString2;
    if (null != m_arg2)
    {
      double d2 = m_arg2.num(paramXPathContext);
      int k = (int)(Math.round(d2) + d1) - 1;
      if (k < 0) {
        k = 0;
      } else if (k > i) {
        k = i;
      }
      if (j > i) {
        j = i;
      }
      localXMLString2 = localXMLString1.substring(j, k);
    }
    else
    {
      if (j > i) {
        j = i;
      }
      localXMLString2 = localXMLString1.substring(j);
    }
    return (XString)localXMLString2;
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt < 2) {
      reportWrongNumberArgs();
    }
  }
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("ER_TWO_OR_THREE", null));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSubstring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */