package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class Operation
  extends Expression
  implements ExpressionOwner
{
  static final long serialVersionUID = -3037139537171050430L;
  protected Expression m_left;
  protected Expression m_right;
  
  public Operation() {}
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    m_left.fixupVariables(paramVector, paramInt);
    m_right.fixupVariables(paramVector, paramInt);
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    if ((null != m_left) && (m_left.canTraverseOutsideSubtree())) {
      return true;
    }
    return (null != m_right) && (m_right.canTraverseOutsideSubtree());
  }
  
  public void setLeftRight(Expression paramExpression1, Expression paramExpression2)
  {
    m_left = paramExpression1;
    m_right = paramExpression2;
    paramExpression1.exprSetParent(this);
    paramExpression2.exprSetParent(this);
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XObject localXObject1 = m_left.execute(paramXPathContext, true);
    XObject localXObject2 = m_right.execute(paramXPathContext, true);
    XObject localXObject3 = operate(localXObject1, localXObject2);
    localXObject1.detach();
    localXObject2.detach();
    return localXObject3;
  }
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return null;
  }
  
  public Expression getLeftOperand()
  {
    return m_left;
  }
  
  public Expression getRightOperand()
  {
    return m_right;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if (paramXPathVisitor.visitBinaryOperation(paramExpressionOwner, this))
    {
      m_left.callVisitors(new LeftExprOwner(), paramXPathVisitor);
      m_right.callVisitors(this, paramXPathVisitor);
    }
  }
  
  public Expression getExpression()
  {
    return m_right;
  }
  
  public void setExpression(Expression paramExpression)
  {
    paramExpression.exprSetParent(this);
    m_right = paramExpression;
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!isSameClass(paramExpression)) {
      return false;
    }
    if (!m_left.deepEquals(m_left)) {
      return false;
    }
    return m_right.deepEquals(m_right);
  }
  
  class LeftExprOwner
    implements ExpressionOwner
  {
    LeftExprOwner() {}
    
    public Expression getExpression()
    {
      return m_left;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(Operation.this);
      m_left = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Operation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */