package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncExtFunctionAvailable
  extends FunctionOneArg
{
  static final long serialVersionUID = 5118814314918592241L;
  private transient FunctionTable m_functionTable = null;
  
  public FuncExtFunctionAvailable() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    String str4 = m_arg0.execute(paramXPathContext).str();
    int i = str4.indexOf(':');
    String str1;
    String str2;
    String str3;
    if (i < 0)
    {
      str1 = "";
      str2 = "http://www.w3.org/1999/XSL/Transform";
      str3 = str4;
    }
    else
    {
      str1 = str4.substring(0, i);
      str2 = paramXPathContext.getNamespaceContext().getNamespaceForPrefix(str1);
      if (null == str2) {
        return XBoolean.S_FALSE;
      }
      str3 = str4.substring(i + 1);
    }
    if (str2.equals("http://www.w3.org/1999/XSL/Transform")) {
      try
      {
        if (null == m_functionTable) {
          m_functionTable = new FunctionTable();
        }
        return m_functionTable.functionAvailable(str3) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
      catch (Exception localException)
      {
        return XBoolean.S_FALSE;
      }
    }
    ExtensionsProvider localExtensionsProvider = (ExtensionsProvider)paramXPathContext.getOwnerObject();
    return localExtensionsProvider.functionAvailable(str2, str3) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
  
  public void setFunctionTable(FunctionTable paramFunctionTable)
  {
    m_functionTable = paramFunctionTable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncExtFunctionAvailable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */