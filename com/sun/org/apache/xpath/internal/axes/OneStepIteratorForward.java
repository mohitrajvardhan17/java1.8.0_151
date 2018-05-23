package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class OneStepIteratorForward
  extends ChildTestIterator
{
  static final long serialVersionUID = -1576936606178190566L;
  protected int m_axis = -1;
  
  OneStepIteratorForward(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2);
    int i = OpMap.getFirstChildPos(paramInt1);
    m_axis = WalkerFactory.getAxisFromStep(paramCompiler, i);
  }
  
  public OneStepIteratorForward(int paramInt)
  {
    super(null);
    m_axis = paramInt;
    int i = -1;
    initNodeTest(i);
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    m_traverser = m_cdtm.getAxisTraverser(m_axis);
  }
  
  protected int getNextNode()
  {
    m_lastFetched = (-1 == m_lastFetched ? m_traverser.first(m_context) : m_traverser.next(m_context, m_lastFetched));
    return m_lastFetched;
  }
  
  public int getAxis()
  {
    return m_axis;
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    return m_axis == m_axis;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\OneStepIteratorForward.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */