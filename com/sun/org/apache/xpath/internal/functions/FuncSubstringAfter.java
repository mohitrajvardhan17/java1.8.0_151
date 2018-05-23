package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncSubstringAfter
  extends Function2Args
{
  static final long serialVersionUID = -8119731889862512194L;
  
  public FuncSubstringAfter() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XMLString localXMLString1 = m_arg0.execute(paramXPathContext).xstr();
    XMLString localXMLString2 = m_arg1.execute(paramXPathContext).xstr();
    int i = localXMLString1.indexOf(localXMLString2);
    return -1 == i ? XString.EMPTYSTRING : (XString)localXMLString1.substring(i + localXMLString2.length());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSubstringAfter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */