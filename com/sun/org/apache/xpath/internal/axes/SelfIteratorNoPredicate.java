package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class SelfIteratorNoPredicate
  extends LocPathIterator
{
  static final long serialVersionUID = -4226887905279814201L;
  
  SelfIteratorNoPredicate(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, false);
  }
  
  public SelfIteratorNoPredicate()
    throws TransformerException
  {
    super(null);
  }
  
  public int nextNode()
  {
    if (m_foundLast) {
      return -1;
    }
    DTM localDTM = m_cdtm;
    int i;
    m_lastFetched = (i = -1 == m_lastFetched ? m_context : -1);
    if (-1 != i)
    {
      m_pos += 1;
      return i;
    }
    m_foundLast = true;
    return -1;
  }
  
  public int asNode(XPathContext paramXPathContext)
    throws TransformerException
  {
    return paramXPathContext.getCurrentNode();
  }
  
  public int getLastPos(XPathContext paramXPathContext)
  {
    return 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\SelfIteratorNoPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */