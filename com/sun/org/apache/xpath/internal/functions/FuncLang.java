package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncLang
  extends FunctionOneArg
{
  static final long serialVersionUID = -7868705139354872185L;
  
  public FuncLang() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    String str1 = m_arg0.execute(paramXPathContext).str();
    int i = paramXPathContext.getCurrentNode();
    int j = 0;
    DTM localDTM = paramXPathContext.getDTM(i);
    while (-1 != i)
    {
      if (1 == localDTM.getNodeType(i))
      {
        int k = localDTM.getAttributeNode(i, "http://www.w3.org/XML/1998/namespace", "lang");
        if (-1 != k)
        {
          String str2 = localDTM.getNodeValue(k);
          if (!str2.toLowerCase().startsWith(str1.toLowerCase())) {
            break;
          }
          int m = str1.length();
          if ((str2.length() == m) || (str2.charAt(m) == '-')) {
            j = 1;
          }
          break;
        }
      }
      i = localDTM.getParent(i);
    }
    return j != 0 ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncLang.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */