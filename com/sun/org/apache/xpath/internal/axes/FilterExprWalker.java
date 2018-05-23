package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.operations.Variable;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FilterExprWalker
  extends AxesWalker
{
  static final long serialVersionUID = 5457182471424488375L;
  private Expression m_expr;
  private transient XNodeSet m_exprObj;
  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;
  
  public FilterExprWalker(WalkingIterator paramWalkingIterator)
  {
    super(paramWalkingIterator, 20);
  }
  
  public void init(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super.init(paramCompiler, paramInt1, paramInt2);
    switch (paramInt2)
    {
    case 24: 
    case 25: 
      m_mustHardReset = true;
    case 22: 
    case 23: 
      m_expr = paramCompiler.compile(paramInt1);
      m_expr.exprSetParent(this);
      if ((m_expr instanceof Variable)) {
        m_canDetachNodeset = false;
      }
      break;
    default: 
      m_expr = paramCompiler.compile(paramInt1 + 2);
      m_expr.exprSetParent(this);
    }
  }
  
  public void detach()
  {
    super.detach();
    if (m_canDetachNodeset) {
      m_exprObj.detach();
    }
    m_exprObj = null;
  }
  
  public void setRoot(int paramInt)
  {
    super.setRoot(paramInt);
    m_exprObj = FilterExprIteratorSimple.executeFilterExpr(paramInt, m_lpi.getXPathContext(), m_lpi.getPrefixResolver(), m_lpi.getIsTopLevel(), m_lpi.m_stackFrame, m_expr);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    FilterExprWalker localFilterExprWalker = (FilterExprWalker)super.clone();
    if (null != m_exprObj) {
      m_exprObj = ((XNodeSet)m_exprObj.clone());
    }
    return localFilterExprWalker;
  }
  
  public short acceptNode(int paramInt)
  {
    try
    {
      if (getPredicateCount() > 0)
      {
        countProximityPosition(0);
        if (!executePredicates(paramInt, m_lpi.getXPathContext())) {
          return 3;
        }
      }
      return 1;
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
  }
  
  public int getNextNode()
  {
    if (null != m_exprObj)
    {
      int i = m_exprObj.nextNode();
      return i;
    }
    return -1;
  }
  
  public int getLastPos(XPathContext paramXPathContext)
  {
    return m_exprObj.getLength();
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
  
  public int getAxis()
  {
    return m_exprObj.getAxis();
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
    FilterExprWalker localFilterExprWalker = (FilterExprWalker)paramExpression;
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
      paramExpression.exprSetParent(FilterExprWalker.this);
      m_expr = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\FilterExprWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */