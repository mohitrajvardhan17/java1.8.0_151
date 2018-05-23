package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public abstract class PredicatedNodeTest
  extends NodeTest
  implements SubContextList
{
  static final long serialVersionUID = -6193530757296377351L;
  protected int m_predCount = -1;
  protected transient boolean m_foundLast = false;
  protected LocPathIterator m_lpi;
  transient int m_predicateIndex = -1;
  private Expression[] m_predicates;
  protected transient int[] m_proximityPositions;
  static final boolean DEBUG_PREDICATECOUNTING = false;
  
  PredicatedNodeTest(LocPathIterator paramLocPathIterator)
  {
    m_lpi = paramLocPathIterator;
  }
  
  PredicatedNodeTest() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, TransformerException
  {
    try
    {
      paramObjectInputStream.defaultReadObject();
      m_predicateIndex = -1;
      m_predCount = -1;
      resetProximityPositions();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new TransformerException(localClassNotFoundException);
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    PredicatedNodeTest localPredicatedNodeTest = (PredicatedNodeTest)super.clone();
    if ((null != m_proximityPositions) && (m_proximityPositions == m_proximityPositions))
    {
      m_proximityPositions = new int[m_proximityPositions.length];
      System.arraycopy(m_proximityPositions, 0, m_proximityPositions, 0, m_proximityPositions.length);
    }
    if (m_lpi == this) {
      m_lpi = ((LocPathIterator)localPredicatedNodeTest);
    }
    return localPredicatedNodeTest;
  }
  
  public int getPredicateCount()
  {
    if (-1 == m_predCount) {
      return null == m_predicates ? 0 : m_predicates.length;
    }
    return m_predCount;
  }
  
  public void setPredicateCount(int paramInt)
  {
    if (paramInt > 0)
    {
      Expression[] arrayOfExpression = new Expression[paramInt];
      for (int i = 0; i < paramInt; i++) {
        arrayOfExpression[i] = m_predicates[i];
      }
      m_predicates = arrayOfExpression;
    }
    else
    {
      m_predicates = null;
    }
  }
  
  protected void initPredicateInfo(Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    int i = paramCompiler.getFirstPredicateOpPos(paramInt);
    if (i > 0)
    {
      m_predicates = paramCompiler.getCompiledPredicates(i);
      if (null != m_predicates) {
        for (int j = 0; j < m_predicates.length; j++) {
          m_predicates[j].exprSetParent(this);
        }
      }
    }
  }
  
  public Expression getPredicate(int paramInt)
  {
    return m_predicates[paramInt];
  }
  
  public int getProximityPosition()
  {
    return getProximityPosition(m_predicateIndex);
  }
  
  public int getProximityPosition(XPathContext paramXPathContext)
  {
    return getProximityPosition();
  }
  
  public abstract int getLastPos(XPathContext paramXPathContext);
  
  protected int getProximityPosition(int paramInt)
  {
    return paramInt >= 0 ? m_proximityPositions[paramInt] : 0;
  }
  
  public void resetProximityPositions()
  {
    int i = getPredicateCount();
    if (i > 0)
    {
      if (null == m_proximityPositions) {
        m_proximityPositions = new int[i];
      }
      for (int j = 0; j < i; j++) {
        try
        {
          initProximityPosition(j);
        }
        catch (Exception localException)
        {
          throw new WrappedRuntimeException(localException);
        }
      }
    }
  }
  
  public void initProximityPosition(int paramInt)
    throws TransformerException
  {
    m_proximityPositions[paramInt] = 0;
  }
  
  protected void countProximityPosition(int paramInt)
  {
    int[] arrayOfInt = m_proximityPositions;
    if ((null != arrayOfInt) && (paramInt < arrayOfInt.length)) {
      arrayOfInt[paramInt] += 1;
    }
  }
  
  public boolean isReverseAxes()
  {
    return false;
  }
  
  public int getPredicateIndex()
  {
    return m_predicateIndex;
  }
  
  boolean executePredicates(int paramInt, XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getPredicateCount();
    if (i == 0) {
      return true;
    }
    PrefixResolver localPrefixResolver = paramXPathContext.getNamespaceContext();
    try
    {
      m_predicateIndex = 0;
      paramXPathContext.pushSubContextList(this);
      paramXPathContext.pushNamespaceContext(m_lpi.getPrefixResolver());
      paramXPathContext.pushCurrentNode(paramInt);
      for (int j = 0; j < i; j++)
      {
        XObject localXObject = m_predicates[j].execute(paramXPathContext);
        int k;
        if (2 == localXObject.getType())
        {
          k = getProximityPosition(m_predicateIndex);
          int m = (int)localXObject.num();
          if (k != m)
          {
            boolean bool = false;
            return bool;
          }
          if ((m_predicates[j].isStableNumber()) && (j == i - 1)) {
            m_foundLast = true;
          }
        }
        else if (!localXObject.bool())
        {
          k = 0;
          return k;
        }
        countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popNamespaceContext();
      paramXPathContext.popSubContextList();
      m_predicateIndex = -1;
    }
    return true;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    int i = getPredicateCount();
    for (int j = 0; j < i; j++) {
      m_predicates[j].fixupVariables(paramVector, paramInt);
    }
  }
  
  protected String nodeToString(int paramInt)
  {
    if (-1 != paramInt)
    {
      DTM localDTM = m_lpi.getXPathContext().getDTM(paramInt);
      return localDTM.getNodeName(paramInt) + "{" + (paramInt + 1) + "}";
    }
    return "null";
  }
  
  public short acceptNode(int paramInt)
  {
    XPathContext localXPathContext = m_lpi.getXPathContext();
    try
    {
      localXPathContext.pushCurrentNode(paramInt);
      XObject localXObject = execute(localXPathContext, paramInt);
      if (localXObject != NodeTest.SCORE_NONE)
      {
        if (getPredicateCount() > 0)
        {
          countProximityPosition(0);
          if (!executePredicates(paramInt, localXPathContext))
          {
            s = 3;
            return s;
          }
        }
        short s = 1;
        return s;
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
    finally
    {
      localXPathContext.popCurrentNode();
    }
    return 3;
  }
  
  public LocPathIterator getLocPathIterator()
  {
    return m_lpi;
  }
  
  public void setLocPathIterator(LocPathIterator paramLocPathIterator)
  {
    m_lpi = paramLocPathIterator;
    if (this != paramLocPathIterator) {
      paramLocPathIterator.exprSetParent(this);
    }
  }
  
  public boolean canTraverseOutsideSubtree()
  {
    int i = getPredicateCount();
    for (int j = 0; j < i; j++) {
      if (getPredicate(j).canTraverseOutsideSubtree()) {
        return true;
      }
    }
    return false;
  }
  
  public void callPredicateVisitors(XPathVisitor paramXPathVisitor)
  {
    if (null != m_predicates)
    {
      int i = m_predicates.length;
      for (int j = 0; j < i; j++)
      {
        PredOwner localPredOwner = new PredOwner(j);
        if (paramXPathVisitor.visitPredicate(localPredOwner, m_predicates[j])) {
          m_predicates[j].callVisitors(localPredOwner, paramXPathVisitor);
        }
      }
    }
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    PredicatedNodeTest localPredicatedNodeTest = (PredicatedNodeTest)paramExpression;
    if (null != m_predicates)
    {
      int i = m_predicates.length;
      if ((null == m_predicates) || (m_predicates.length != i)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!m_predicates[j].deepEquals(m_predicates[j])) {
          return false;
        }
      }
    }
    else if (null != m_predicates)
    {
      return false;
    }
    return true;
  }
  
  class PredOwner
    implements ExpressionOwner
  {
    int m_index;
    
    PredOwner(int paramInt)
    {
      m_index = paramInt;
    }
    
    public Expression getExpression()
    {
      return m_predicates[m_index];
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(PredicatedNodeTest.this);
      m_predicates[m_index] = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\PredicatedNodeTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */