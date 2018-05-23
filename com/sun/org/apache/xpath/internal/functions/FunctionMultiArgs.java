package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Vector;

public class FunctionMultiArgs
  extends Function3Args
{
  static final long serialVersionUID = 7117257746138417181L;
  Expression[] m_args;
  
  public FunctionMultiArgs() {}
  
  public Expression[] getArgs()
  {
    return m_args;
  }
  
  public void setArg(Expression paramExpression, int paramInt)
    throws WrongNumberArgsException
  {
    if (paramInt < 3)
    {
      super.setArg(paramExpression, paramInt);
    }
    else
    {
      if (null == m_args)
      {
        m_args = new Expression[1];
        m_args[0] = paramExpression;
      }
      else
      {
        Expression[] arrayOfExpression = new Expression[m_args.length + 1];
        System.arraycopy(m_args, 0, arrayOfExpression, 0, m_args.length);
        arrayOfExpression[m_args.length] = paramExpression;
        m_args = arrayOfExpression;
      }
      paramExpression.exprSetParent(this);
    }
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    if (null != m_args) {
      for (int i = 0; i < m_args.length; i++) {
        m_args[i].fixupVariables(paramVector, paramInt);
      }
    }
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {}
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });
    throw new RuntimeException(str);
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    if (super.canTraverseOutsideSubtree()) {
      return true;
    }
    int i = m_args.length;
    for (int j = 0; j < i; j++) {
      if (m_args[j].canTraverseOutsideSubtree()) {
        return true;
      }
    }
    return false;
  }
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor)
  {
    super.callArgVisitors(paramXPathVisitor);
    if (null != m_args)
    {
      int i = m_args.length;
      for (int j = 0; j < i; j++) {
        m_args[j].callVisitors(new ArgMultiOwner(j), paramXPathVisitor);
      }
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    FunctionMultiArgs localFunctionMultiArgs = (FunctionMultiArgs)paramExpression;
    if (null != m_args)
    {
      int i = m_args.length;
      if ((null == localFunctionMultiArgs) || (m_args.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!m_args[j].deepEquals(m_args[j])) {
          return false;
        }
      }
    }
    else if (null != m_args)
    {
      return false;
    }
    return true;
  }
  
  class ArgMultiOwner
    implements ExpressionOwner
  {
    int m_argIndex;
    
    ArgMultiOwner(int paramInt)
    {
      m_argIndex = paramInt;
    }
    
    public Expression getExpression()
    {
      return m_args[m_argIndex];
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(FunctionMultiArgs.this);
      m_args[m_argIndex] = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FunctionMultiArgs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */