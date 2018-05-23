package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract class UnaryOperation
  extends Expression
  implements ExpressionOwner
{
  static final long serialVersionUID = 6536083808424286166L;
  protected Expression m_right;
  
  public UnaryOperation() {}
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    m_right.fixupVariables(paramVector, paramInt);
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    return (null != m_right) && (m_right.canTraverseOutsideSubtree());
  }
  
  public void setRight(Expression paramExpression)
  {
    m_right = paramExpression;
    paramExpression.exprSetParent(this);
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return operate(m_right.execute(paramXPathContext));
  }
  
  public abstract XObject operate(XObject paramXObject)
    throws TransformerException;
  
  public Expression getOperand()
  {
    return m_right;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if (paramXPathVisitor.visitUnaryOperation(paramExpressionOwner, this)) {
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
    return m_right.deepEquals(m_right);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\UnaryOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */