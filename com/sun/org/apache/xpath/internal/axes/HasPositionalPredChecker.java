package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.functions.FuncLast;
import com.sun.org.apache.xpath.internal.functions.FuncPosition;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.operations.Div;
import com.sun.org.apache.xpath.internal.operations.Minus;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.sun.org.apache.xpath.internal.operations.Number;
import com.sun.org.apache.xpath.internal.operations.Plus;
import com.sun.org.apache.xpath.internal.operations.Quo;
import com.sun.org.apache.xpath.internal.operations.Variable;

public class HasPositionalPredChecker
  extends XPathVisitor
{
  private boolean m_hasPositionalPred = false;
  private int m_predDepth = 0;
  
  public HasPositionalPredChecker() {}
  
  public static boolean check(LocPathIterator paramLocPathIterator)
  {
    HasPositionalPredChecker localHasPositionalPredChecker = new HasPositionalPredChecker();
    paramLocPathIterator.callVisitors(null, localHasPositionalPredChecker);
    return m_hasPositionalPred;
  }
  
  public boolean visitFunction(ExpressionOwner paramExpressionOwner, Function paramFunction)
  {
    if (((paramFunction instanceof FuncPosition)) || ((paramFunction instanceof FuncLast))) {
      m_hasPositionalPred = true;
    }
    return true;
  }
  
  public boolean visitPredicate(ExpressionOwner paramExpressionOwner, Expression paramExpression)
  {
    m_predDepth += 1;
    if (m_predDepth == 1) {
      if (((paramExpression instanceof Variable)) || ((paramExpression instanceof XNumber)) || ((paramExpression instanceof Div)) || ((paramExpression instanceof Plus)) || ((paramExpression instanceof Minus)) || ((paramExpression instanceof Mod)) || ((paramExpression instanceof Quo)) || ((paramExpression instanceof Mult)) || ((paramExpression instanceof Number)) || ((paramExpression instanceof Function))) {
        m_hasPositionalPred = true;
      } else {
        paramExpression.callVisitors(paramExpressionOwner, this);
      }
    }
    m_predDepth -= 1;
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\HasPositionalPredChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */