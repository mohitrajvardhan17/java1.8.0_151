package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class Function3Args
  extends Function2Args
{
  static final long serialVersionUID = 7915240747161506646L;
  Expression m_arg2;
  
  public Function3Args() {}
  
  public Expression getArg2()
  {
    return m_arg2;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    if (null != m_arg2) {
      m_arg2.fixupVariables(paramVector, paramInt);
    }
  }
  
  public void setArg(Expression paramExpression, int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt < 2)
    {
      super.setArg(paramExpression, paramInt);
    }
    else if (2 == paramInt)
    {
      m_arg2 = paramExpression;
      paramExpression.exprSetParent(this);
    }
    else
    {
      reportWrongNumberArgs();
    }
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt != 3) {
      reportWrongNumberArgs();
    }
  }
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("three", null));
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    return super.canTraverseOutsideSubtree() ? true : m_arg2.canTraverseOutsideSubtree();
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor)
  {
    super.callArgVisitors(paramXPathVisitor);
    if (null != m_arg2) {
      m_arg2.callVisitors(new Arg2Owner(), paramXPathVisitor);
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    if (null != m_arg2)
    {
      if (null == m_arg2) {
        return false;
      }
      if (!m_arg2.deepEquals(m_arg2)) {
        return false;
      }
    }
    else if (null != m_arg2)
    {
      return false;
    }
    return true;
  }
  
  class Arg2Owner
    implements ExpressionOwner
  {
    Arg2Owner() {}
    
    public Expression getExpression()
    {
      return m_arg2;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(Function3Args.this);
      m_arg2 = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\Function3Args.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */