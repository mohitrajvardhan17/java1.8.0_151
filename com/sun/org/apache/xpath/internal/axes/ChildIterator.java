package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class ChildIterator
  extends LocPathIterator
{
  static final long serialVersionUID = -6935428015142993583L;
  
  ChildIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, false);
    initNodeTest(-1);
  }
  
  public int asNode(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = paramXPathContext.getCurrentNode();
    DTM localDTM = paramXPathContext.getDTM(i);
    return localDTM.getFirstChild(i);
  }
  
  public int nextNode()
  {
    if (m_foundLast) {
      return -1;
    }
    int i;
    m_lastFetched = (i = -1 == m_lastFetched ? m_cdtm.getFirstChild(m_context) : m_cdtm.getNextSibling(m_lastFetched));
    if (-1 != i)
    {
      m_pos += 1;
      return i;
    }
    m_foundLast = true;
    return -1;
  }
  
  public int getAxis()
  {
    return 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\ChildIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */