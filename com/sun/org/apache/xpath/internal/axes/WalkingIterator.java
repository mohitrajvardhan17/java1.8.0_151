package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class WalkingIterator
  extends LocPathIterator
  implements ExpressionOwner
{
  static final long serialVersionUID = 9110225941815665906L;
  protected AxesWalker m_lastUsedWalker;
  protected AxesWalker m_firstWalker;
  
  WalkingIterator(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, paramBoolean);
    int i = OpMap.getFirstChildPos(paramInt1);
    if (paramBoolean)
    {
      m_firstWalker = WalkerFactory.loadWalkers(this, paramCompiler, i, 0);
      m_lastUsedWalker = m_firstWalker;
    }
  }
  
  public WalkingIterator(PrefixResolver paramPrefixResolver)
  {
    super(paramPrefixResolver);
  }
  
  public int getAnalysisBits()
  {
    int i = 0;
    if (null != m_firstWalker) {
      for (AxesWalker localAxesWalker = m_firstWalker; null != localAxesWalker; localAxesWalker = localAxesWalker.getNextWalker())
      {
        int j = localAxesWalker.getAnalysisBits();
        i |= j;
      }
    }
    return i;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    WalkingIterator localWalkingIterator = (WalkingIterator)super.clone();
    if (null != m_firstWalker) {
      m_firstWalker = m_firstWalker.cloneDeep(localWalkingIterator, null);
    }
    return localWalkingIterator;
  }
  
  public void reset()
  {
    super.reset();
    if (null != m_firstWalker)
    {
      m_lastUsedWalker = m_firstWalker;
      m_firstWalker.setRoot(m_context);
    }
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    if (null != m_firstWalker)
    {
      m_firstWalker.setRoot(paramInt);
      m_lastUsedWalker = m_firstWalker;
    }
  }
  
  public int nextNode()
  {
    if (m_foundLast) {
      return -1;
    }
    if (-1 == m_stackFrame) {
      return returnNextNode(m_firstWalker.nextNode());
    }
    VariableStack localVariableStack = m_execContext.getVarStack();
    int i = localVariableStack.getStackFrame();
    localVariableStack.setStackFrame(m_stackFrame);
    int j = returnNextNode(m_firstWalker.nextNode());
    localVariableStack.setStackFrame(i);
    return j;
  }
  
  public final AxesWalker getFirstWalker()
  {
    return m_firstWalker;
  }
  
  public final void setFirstWalker(AxesWalker paramAxesWalker)
  {
    m_firstWalker = paramAxesWalker;
  }
  
  public final void setLastUsedWalker(AxesWalker paramAxesWalker)
  {
    m_lastUsedWalker = paramAxesWalker;
  }
  
  public final AxesWalker getLastUsedWalker()
  {
    return m_lastUsedWalker;
  }
  
  public void detach()
  {
    if (m_allowDetach)
    {
      for (AxesWalker localAxesWalker = m_firstWalker; null != localAxesWalker; localAxesWalker = localAxesWalker.getNextWalker()) {
        localAxesWalker.detach();
      }
      m_lastUsedWalker = null;
      super.detach();
    }
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    m_predicateIndex = -1;
    for (AxesWalker localAxesWalker = m_firstWalker; null != localAxesWalker; localAxesWalker = localAxesWalker.getNextWalker()) {
      localAxesWalker.fixupVariables(paramVector, paramInt);
    }
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if ((paramXPathVisitor.visitLocationPath(paramExpressionOwner, this)) && (null != m_firstWalker)) {
      m_firstWalker.callVisitors(this, paramXPathVisitor);
    }
  }
  
  public Expression getExpression()
  {
    return m_firstWalker;
  }
  
  public void setExpression(Expression paramExpression)
  {
    paramExpression.exprSetParent(this);
    m_firstWalker = ((AxesWalker)paramExpression);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    AxesWalker localAxesWalker1 = m_firstWalker;
    for (AxesWalker localAxesWalker2 = m_firstWalker; (null != localAxesWalker1) && (null != localAxesWalker2); localAxesWalker2 = localAxesWalker2.getNextWalker())
    {
      if (!localAxesWalker1.deepEquals(localAxesWalker2)) {
        return false;
      }
      localAxesWalker1 = localAxesWalker1.getNextWalker();
    }
    return (null == localAxesWalker1) && (null == localAxesWalker2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\WalkingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */