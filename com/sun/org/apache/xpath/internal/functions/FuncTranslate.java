package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncTranslate
  extends Function3Args
{
  static final long serialVersionUID = -1672834340026116482L;
  
  public FuncTranslate() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    String str1 = m_arg0.execute(paramXPathContext).str();
    String str2 = m_arg1.execute(paramXPathContext).str();
    String str3 = m_arg2.execute(paramXPathContext).str();
    int i = str1.length();
    int j = str3.length();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int k = 0; k < i; k++)
    {
      char c = str1.charAt(k);
      int m = str2.indexOf(c);
      if (m < 0) {
        localStringBuffer.append(c);
      } else if (m < j) {
        localStringBuffer.append(str3.charAt(m));
      }
    }
    return new XString(localStringBuffer.toString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncTranslate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */