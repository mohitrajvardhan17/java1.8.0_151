package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncConcat
  extends FunctionMultiArgs
{
  static final long serialVersionUID = 1737228885202314413L;
  
  public FuncConcat() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(m_arg0.execute(paramXPathContext).str());
    localStringBuffer.append(m_arg1.execute(paramXPathContext).str());
    if (null != m_arg2) {
      localStringBuffer.append(m_arg2.execute(paramXPathContext).str());
    }
    if (null != m_args) {
      for (int i = 0; i < m_args.length; i++) {
        localStringBuffer.append(m_args[i].execute(paramXPathContext).str());
      }
    }
    return new XString(localStringBuffer.toString());
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
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("gtone", null));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncConcat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */