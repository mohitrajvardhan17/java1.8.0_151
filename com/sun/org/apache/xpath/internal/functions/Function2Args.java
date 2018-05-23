package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class Function2Args
  extends FunctionOneArg
{
  static final long serialVersionUID = 5574294996842710641L;
  Expression m_arg1;
  
  public Function2Args() {}
  
  public Expression getArg1()
  {
    return m_arg1;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    if (null != m_arg1) {
      m_arg1.fixupVariables(paramVector, paramInt);
    }
  }
  
  public void setArg(Expression paramExpression, int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt == 0)
    {
      super.setArg(paramExpression, paramInt);
    }
    else if (1 == paramInt)
    {
      m_arg1 = paramExpression;
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
    if (paramInt != 2) {
      reportWrongNumberArgs();
    }
  }
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("two", null));
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    return super.canTraverseOutsideSubtree() ? true : m_arg1.canTraverseOutsideSubtree();
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor)
  {
    super.callArgVisitors(paramXPathVisitor);
    if (null != m_arg1) {
      m_arg1.callVisitors(new Arg1Owner(), paramXPathVisitor);
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    if (null != m_arg1)
    {
      if (null == m_arg1) {
        return false;
      }
      if (!m_arg1.deepEquals(m_arg1)) {
        return false;
      }
    }
    else if (null != m_arg1)
    {
      return false;
    }
    return true;
  }
  
  class Arg1Owner
    implements ExpressionOwner
  {
    Arg1Owner() {}
    
    public Expression getExpression()
    {
      return m_arg1;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(Function2Args.this);
      m_arg1 = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\Function2Args.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */