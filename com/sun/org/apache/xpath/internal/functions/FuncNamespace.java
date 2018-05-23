package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncNamespace
  extends FunctionDef1Arg
{
  static final long serialVersionUID = -4695674566722321237L;
  
  public FuncNamespace() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getArg0AsNode(paramXPathContext);
    String str;
    if (i != -1)
    {
      DTM localDTM = paramXPathContext.getDTM(i);
      int j = localDTM.getNodeType(i);
      if (j == 1)
      {
        str = localDTM.getNamespaceURI(i);
      }
      else if (j == 2)
      {
        str = localDTM.getNodeName(i);
        if ((str.startsWith("xmlns:")) || (str.equals("xmlns"))) {
          return XString.EMPTYSTRING;
        }
        str = localDTM.getNamespaceURI(i);
      }
      else
      {
        return XString.EMPTYSTRING;
      }
    }
    else
    {
      return XString.EMPTYSTRING;
    }
    return null == str ? XString.EMPTYSTRING : new XString(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncNamespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */