package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionPathIterator
  extends LocPathIterator
  implements Cloneable, DTMIterator, Serializable, PathComponent
{
  static final long serialVersionUID = -3910351546843826781L;
  protected LocPathIterator[] m_exprs;
  protected DTMIterator[] m_iterators;
  
  public UnionPathIterator()
  {
    m_iterators = null;
    m_exprs = null;
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    try
    {
      if (null != m_exprs)
      {
        int i = m_exprs.length;
        DTMIterator[] arrayOfDTMIterator = new DTMIterator[i];
        for (int j = 0; j < i; j++)
        {
          DTMIterator localDTMIterator = m_exprs[j].asIterator(m_execContext, paramInt);
          arrayOfDTMIterator[j] = localDTMIterator;
          localDTMIterator.nextNode();
        }
        m_iterators = arrayOfDTMIterator;
      }
    }
    catch (Exception localException)
    {
      throw new WrappedRuntimeException(localException);
    }
  }
  
  public void addIterator(DTMIterator paramDTMIterator)
  {
    if (null == m_iterators)
    {
      m_iterators = new DTMIterator[1];
      m_iterators[0] = paramDTMIterator;
    }
    else
    {
      DTMIterator[] arrayOfDTMIterator = m_iterators;
      int i = m_iterators.length;
      m_iterators = new DTMIterator[i + 1];
      System.arraycopy(arrayOfDTMIterator, 0, m_iterators, 0, i);
      m_iterators[i] = paramDTMIterator;
    }
    paramDTMIterator.nextNode();
    if ((paramDTMIterator instanceof Expression)) {
      ((Expression)paramDTMIterator).exprSetParent(this);
    }
  }
  
  public void detach()
  {
    if ((m_allowDetach) && (null != m_iterators))
    {
      int i = m_iterators.length;
      for (int j = 0; j < i; j++) {
        m_iterators[j].detach();
      }
      m_iterators = null;
    }
  }
  
  public UnionPathIterator(Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    paramInt = OpMap.getFirstChildPos(paramInt);
    loadLocationPaths(paramCompiler, paramInt, 0);
  }
  
  public static LocPathIterator createUnionIterator(Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    UnionPathIterator localUnionPathIterator = new UnionPathIterator(paramCompiler, paramInt);
    int i = m_exprs.length;
    int j = 1;
    for (int k = 0; k < i; k++)
    {
      LocPathIterator localLocPathIterator1 = m_exprs[k];
      if (localLocPathIterator1.getAxis() != 3)
      {
        j = 0;
        break;
      }
      if (HasPositionalPredChecker.check(localLocPathIterator1))
      {
        j = 0;
        break;
      }
    }
    if (j != 0)
    {
      UnionChildIterator localUnionChildIterator = new UnionChildIterator();
      for (int m = 0; m < i; m++)
      {
        LocPathIterator localLocPathIterator2 = m_exprs[m];
        localUnionChildIterator.addNodeTest(localLocPathIterator2);
      }
      return localUnionChildIterator;
    }
    return localUnionPathIterator;
  }
  
  public int getAnalysisBits()
  {
    int i = 0;
    if (m_exprs != null)
    {
      int j = m_exprs.length;
      for (int k = 0; k < j; k++)
      {
        int m = m_exprs[k].getAnalysisBits();
        i |= m;
      }
    }
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, TransformerException
  {
    try
    {
      paramObjectInputStream.defaultReadObject();
      m_clones = new IteratorPool(this);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new TransformerException(localClassNotFoundException);
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    UnionPathIterator localUnionPathIterator = (UnionPathIterator)super.clone();
    if (m_iterators != null)
    {
      int i = m_iterators.length;
      m_iterators = new DTMIterator[i];
      for (int j = 0; j < i; j++) {
        m_iterators[j] = ((DTMIterator)m_iterators[j].clone());
      }
    }
    return localUnionPathIterator;
  }
  
  protected LocPathIterator createDTMIterator(Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    LocPathIterator localLocPathIterator = (LocPathIterator)WalkerFactory.newDTMIterator(paramCompiler, paramInt, paramCompiler.getLocationPathDepth() <= 0);
    return localLocPathIterator;
  }
  
  protected void loadLocationPaths(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    int i = paramCompiler.getOp(paramInt1);
    if (i == 28)
    {
      loadLocationPaths(paramCompiler, paramCompiler.getNextOpPos(paramInt1), paramInt2 + 1);
      m_exprs[paramInt2] = createDTMIterator(paramCompiler, paramInt1);
      m_exprs[paramInt2].exprSetParent(this);
    }
    else
    {
      switch (i)
      {
      case 22: 
      case 23: 
      case 24: 
      case 25: 
        loadLocationPaths(paramCompiler, paramCompiler.getNextOpPos(paramInt1), paramInt2 + 1);
        WalkingIterator localWalkingIterator = new WalkingIterator(paramCompiler.getNamespaceContext());
        localWalkingIterator.exprSetParent(this);
        if (paramCompiler.getLocationPathDepth() <= 0) {
          localWalkingIterator.setIsTopLevel(true);
        }
        m_firstWalker = new FilterExprWalker(localWalkingIterator);
        m_firstWalker.init(paramCompiler, paramInt1, i);
        m_exprs[paramInt2] = localWalkingIterator;
        break;
      default: 
        m_exprs = new LocPathIterator[paramInt2];
      }
    }
  }
  
  public int nextNode()
  {
    if (m_foundLast) {
      return -1;
    }
    int i = -1;
    if (null != m_iterators)
    {
      int j = m_iterators.length;
      int k = -1;
      for (int m = 0; m < j; m++)
      {
        int n = m_iterators[m].getCurrentNode();
        if (-1 != n) {
          if (-1 == i)
          {
            k = m;
            i = n;
          }
          else if (n == i)
          {
            m_iterators[m].nextNode();
          }
          else
          {
            DTM localDTM = getDTM(n);
            if (localDTM.isNodeAfter(n, i))
            {
              k = m;
              i = n;
            }
          }
        }
      }
      if (-1 != i)
      {
        m_iterators[k].nextNode();
        incrementCurrentPos();
      }
      else
      {
        m_foundLast = true;
      }
    }
    m_lastFetched = i;
    return i;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    for (int i = 0; i < m_exprs.length; i++) {
      m_exprs[i].fixupVariables(paramVector, paramInt);
    }
  }
  
  public int getAxis()
  {
    return -1;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    if ((paramXPathVisitor.visitUnionPath(paramExpressionOwner, this)) && (null != m_exprs))
    {
      int i = m_exprs.length;
      for (int j = 0; j < i; j++) {
        m_exprs[j].callVisitors(new iterOwner(j), paramXPathVisitor);
      }
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    UnionPathIterator localUnionPathIterator = (UnionPathIterator)paramExpression;
    if (null != m_exprs)
    {
      int i = m_exprs.length;
      if ((null == m_exprs) || (m_exprs.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!m_exprs[j].deepEquals(m_exprs[j])) {
          return false;
        }
      }
    }
    else if (null != m_exprs)
    {
      return false;
    }
    return true;
  }
  
  class iterOwner
    implements ExpressionOwner
  {
    int m_index;
    
    iterOwner(int paramInt)
    {
      m_index = paramInt;
    }
    
    public Expression getExpression()
    {
      return m_exprs[m_index];
    }
    
    public void setExpression(Expression paramExpression)
    {
      if (!(paramExpression instanceof LocPathIterator))
      {
        WalkingIterator localWalkingIterator = new WalkingIterator(getPrefixResolver());
        FilterExprWalker localFilterExprWalker = new FilterExprWalker(localWalkingIterator);
        localWalkingIterator.setFirstWalker(localFilterExprWalker);
        localFilterExprWalker.setInnerExpression(paramExpression);
        localWalkingIterator.exprSetParent(UnionPathIterator.this);
        localFilterExprWalker.exprSetParent(localWalkingIterator);
        paramExpression.exprSetParent(localFilterExprWalker);
        paramExpression = localWalkingIterator;
      }
      else
      {
        paramExpression.exprSetParent(UnionPathIterator.this);
      }
      m_exprs[m_index] = ((LocPathIterator)paramExpression);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\UnionPathIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */