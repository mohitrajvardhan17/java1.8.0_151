package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncPosition
  extends Function
{
  static final long serialVersionUID = -9092846348197271582L;
  private boolean m_isTopLevel;
  
  public FuncPosition() {}
  
  public void postCompileStep(Compiler paramCompiler)
  {
    m_isTopLevel = (paramCompiler.getLocationPathDepth() == -1);
  }
  
  public int getPositionInContextNodeList(XPathContext paramXPathContext)
  {
    SubContextList localSubContextList = m_isTopLevel ? null : paramXPathContext.getSubContextList();
    if (null != localSubContextList)
    {
      int i = localSubContextList.getProximityPosition(paramXPathContext);
      return i;
    }
    DTMIterator localDTMIterator = paramXPathContext.getContextNodeList();
    if (null != localDTMIterator)
    {
      int j = localDTMIterator.getCurrentNode();
      if (j == -1)
      {
        if (localDTMIterator.getCurrentPos() == 0) {
          return 0;
        }
        try
        {
          localDTMIterator = localDTMIterator.cloneWithReset();
        }
        catch (CloneNotSupportedException localCloneNotSupportedException)
        {
          throw new WrappedRuntimeException(localCloneNotSupportedException);
        }
        int k = paramXPathContext.getContextNode();
        while (-1 != (j = localDTMIterator.nextNode())) {
          if (j == k) {
            break;
          }
        }
      }
      return localDTMIterator.getCurrentPos();
    }
    return -1;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    double d = getPositionInContextNodeList(paramXPathContext);
    return new XNumber(d);
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */