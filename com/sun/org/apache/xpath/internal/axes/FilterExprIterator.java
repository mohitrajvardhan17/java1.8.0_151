package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import java.util.Vector;

public class FilterExprIterator
  extends BasicTestIterator
{
  static final long serialVersionUID = 2552176105165737614L;
  private Expression m_expr;
  private transient XNodeSet m_exprObj;
  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;
  
  public FilterExprIterator()
  {
    super(null);
  }
  
  public FilterExprIterator(Expression paramExpression)
  {
    super(null);
    m_expr = paramExpression;
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    m_exprObj = FilterExprIteratorSimple.executeFilterExpr(paramInt, m_execContext, getPrefixResolver(), getIsTopLevel(), m_stackFrame, m_expr);
  }
  
  protected int getNextNode()
  {
    if (null != m_exprObj) {
      m_lastFetched = m_exprObj.nextNode();
    } else {
      m_lastFetched = -1;
    }
    return m_lastFetched;
  }
  
  public void detach()
  {
    super.detach();
    m_exprObj.detach();
    m_exprObj = null;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    m_expr.fixupVariables(paramVector, paramInt);
  }
  
  public Expression getInnerExpression()
  {
    return m_expr;
  }
  
  public void setInnerExpression(Expression paramExpression)
  {
    paramExpression.exprSetParent(this);
    m_expr = paramExpression;
  }
  
  public int getAnalysisBits()
  {
    if ((null != m_expr) && ((m_expr instanceof PathComponent))) {
      return ((PathComponent)m_expr).getAnalysisBits();
    }
    return 67108864;
  }
  
  public boolean isDocOrdered()
  {
    return m_exprObj.isDocOrdered();
  }
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor)
  {
    m_expr.callVisitors(new filterExprOwner(), paramXPathVisitor);
    super.callPredicateVisitors(paramXPathVisitor);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    FilterExprIterator localFilterExprIterator = (FilterExprIterator)paramExpression;
    return m_expr.deepEquals(m_expr);
  }
  
  class filterExprOwner
    implements ExpressionOwner
  {
    filterExprOwner() {}
    
    public Expression getExpression()
    {
      return m_expr;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(FilterExprIterator.this);
      m_expr = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\FilterExprIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */