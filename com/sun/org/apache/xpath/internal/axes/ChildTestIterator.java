package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class ChildTestIterator
  extends BasicTestIterator
{
  static final long serialVersionUID = -7936835957960705722L;
  protected transient DTMAxisTraverser m_traverser;
  
  ChildTestIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2);
  }
  
  public ChildTestIterator(DTMAxisTraverser paramDTMAxisTraverser)
  {
    super(null);
    m_traverser = paramDTMAxisTraverser;
  }
  
  protected int getNextNode()
  {
    m_lastFetched = (-1 == m_lastFetched ? m_traverser.first(m_context) : m_traverser.next(m_context, m_lastFetched));
    return m_lastFetched;
  }
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    ChildTestIterator localChildTestIterator = (ChildTestIterator)super.cloneWithReset();
    m_traverser = m_traverser;
    return localChildTestIterator;
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    m_traverser = m_cdtm.getAxisTraverser(3);
  }
  
  public int getAxis()
  {
    return 3;
  }
  
  public void detach()
  {
    if (m_allowDetach)
    {
      m_traverser = null;
      super.detach();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\ChildTestIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */