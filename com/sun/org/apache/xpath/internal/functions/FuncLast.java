package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncLast
  extends Function
{
  static final long serialVersionUID = 9205812403085432943L;
  private boolean m_isTopLevel;
  
  public FuncLast() {}
  
  public void postCompileStep(Compiler paramCompiler)
  {
    m_isTopLevel = (paramCompiler.getLocationPathDepth() == -1);
  }
  
  public int getCountOfContextNodeList(XPathContext paramXPathContext)
    throws TransformerException
  {
    SubContextList localSubContextList = m_isTopLevel ? null : paramXPathContext.getSubContextList();
    if (null != localSubContextList) {
      return localSubContextList.getLastPos(paramXPathContext);
    }
    DTMIterator localDTMIterator = paramXPathContext.getContextNodeList();
    int i;
    if (null != localDTMIterator) {
      i = localDTMIterator.getLength();
    } else {
      i = 0;
    }
    return i;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XNumber localXNumber = new XNumber(getCountOfContextNodeList(paramXPathContext));
    return localXNumber;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncLast.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */