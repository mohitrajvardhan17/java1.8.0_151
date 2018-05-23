package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FunctionPattern
  extends StepPattern
{
  static final long serialVersionUID = -5426793413091209944L;
  Expression m_functionExpr;
  
  public FunctionPattern(Expression paramExpression, int paramInt1, int paramInt2)
  {
    super(0, null, null, paramInt1, paramInt2);
    m_functionExpr = paramExpression;
  }
  
  public final void calcScore()
  {
    m_score = SCORE_OTHER;
    if (null == m_targetString) {
      calcTargetString();
    }
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    m_functionExpr.fixupVariables(paramVector, paramInt);
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    DTMIterator localDTMIterator = m_functionExpr.asIterator(paramXPathContext, paramInt);
    XNumber localXNumber = SCORE_NONE;
    if (null != localDTMIterator)
    {
      int i;
      while (-1 != (i = localDTMIterator.nextNode()))
      {
        localXNumber = i == paramInt ? SCORE_OTHER : SCORE_NONE;
        if (localXNumber == SCORE_OTHER) {
          paramInt = i;
        }
      }
    }
    localDTMIterator.detach();
    return localXNumber;
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2)
    throws TransformerException
  {
    DTMIterator localDTMIterator = m_functionExpr.asIterator(paramXPathContext, paramInt1);
    XNumber localXNumber = SCORE_NONE;
    if (null != localDTMIterator)
    {
      int i;
      while (-1 != (i = localDTMIterator.nextNode()))
      {
        localXNumber = i == paramInt1 ? SCORE_OTHER : SCORE_NONE;
        if (localXNumber == SCORE_OTHER) {
          paramInt1 = i;
        }
      }
      localDTMIterator.detach();
    }
    return localXNumber;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = paramXPathContext.getCurrentNode();
    DTMIterator localDTMIterator = m_functionExpr.asIterator(paramXPathContext, i);
    XNumber localXNumber = SCORE_NONE;
    if (null != localDTMIterator)
    {
      int j;
      while (-1 != (j = localDTMIterator.nextNode()))
      {
        localXNumber = j == i ? SCORE_OTHER : SCORE_NONE;
        if (localXNumber == SCORE_OTHER) {
          i = j;
        }
      }
      localDTMIterator.detach();
    }
    return localXNumber;
  }
  
  protected void callSubtreeVisitors(XPathVisitor paramXPathVisitor)
  {
    m_functionExpr.callVisitors(new FunctionOwner(), paramXPathVisitor);
    super.callSubtreeVisitors(paramXPathVisitor);
  }
  
  class FunctionOwner
    implements ExpressionOwner
  {
    FunctionOwner() {}
    
    public Expression getExpression()
    {
      return m_functionExpr;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(FunctionPattern.this);
      m_functionExpr = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\patterns\FunctionPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */