package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionPattern
  extends Expression
{
  static final long serialVersionUID = -6670449967116905820L;
  private StepPattern[] m_patterns;
  
  public UnionPattern() {}
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    for (int i = 0; i < m_patterns.length; i++) {
      m_patterns[i].fixupVariables(paramVector, paramInt);
    }
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    if (null != m_patterns)
    {
      int i = m_patterns.length;
      for (int j = 0; j < i; j++) {
        if (m_patterns[j].canTraverseOutsideSubtree()) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void setPatterns(StepPattern[] paramArrayOfStepPattern)
  {
    m_patterns = paramArrayOfStepPattern;
    if (null != paramArrayOfStepPattern) {
      for (int i = 0; i < paramArrayOfStepPattern.length; i++) {
        paramArrayOfStepPattern[i].exprSetParent(this);
      }
    }
  }
  
  public StepPattern[] getPatterns()
  {
    return m_patterns;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    Object localObject = null;
    int i = m_patterns.length;
    for (int j = 0; j < i; j++)
    {
      XObject localXObject = m_patterns[j].execute(paramXPathContext);
      if (localXObject != NodeTest.SCORE_NONE) {
        if (null == localObject) {
          localObject = localXObject;
        } else if (localXObject.num() > ((XObject)localObject).num()) {
          localObject = localXObject;
        }
      }
    }
    if (null == localObject) {
      localObject = NodeTest.SCORE_NONE;
    }
    return (XObject)localObject;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    paramXPathVisitor.visitUnionPattern(paramExpressionOwner, this);
    if (null != m_patterns)
    {
      int i = m_patterns.length;
      for (int j = 0; j < i; j++) {
        m_patterns[j].callVisitors(new UnionPathPartOwner(j), paramXPathVisitor);
      }
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!isSameClass(paramExpression)) {
      return false;
    }
    UnionPattern localUnionPattern = (UnionPattern)paramExpression;
    if (null != m_patterns)
    {
      int i = m_patterns.length;
      if ((null == m_patterns) || (m_patterns.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!m_patterns[j].deepEquals(m_patterns[j])) {
          return false;
        }
      }
    }
    else if (m_patterns != null)
    {
      return false;
    }
    return true;
  }
  
  class UnionPathPartOwner
    implements ExpressionOwner
  {
    int m_index;
    
    UnionPathPartOwner(int paramInt)
    {
      m_index = paramInt;
    }
    
    public Expression getExpression()
    {
      return m_patterns[m_index];
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(UnionPattern.this);
      m_patterns[m_index] = ((StepPattern)paramExpression);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\patterns\UnionPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */