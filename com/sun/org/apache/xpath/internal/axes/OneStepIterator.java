package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class OneStepIterator
  extends ChildTestIterator
{
  static final long serialVersionUID = 4623710779664998283L;
  protected int m_axis = -1;
  protected DTMAxisIterator m_iterator;
  
  OneStepIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2);
    int i = OpMap.getFirstChildPos(paramInt1);
    m_axis = WalkerFactory.getAxisFromStep(paramCompiler, i);
  }
  
  public OneStepIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt)
    throws TransformerException
  {
    super(null);
    m_iterator = paramDTMAxisIterator;
    m_axis = paramInt;
    int i = -1;
    initNodeTest(i);
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    if (m_axis > -1) {
      m_iterator = m_cdtm.getAxisIterator(m_axis);
    }
    m_iterator.setStartNode(m_context);
  }
  
  public void detach()
  {
    if (m_allowDetach)
    {
      if (m_axis > -1) {
        m_iterator = null;
      }
      super.detach();
    }
  }
  
  protected int getNextNode()
  {
    return m_lastFetched = m_iterator.next();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    OneStepIterator localOneStepIterator = (OneStepIterator)super.clone();
    if (m_iterator != null) {
      m_iterator = m_iterator.cloneIterator();
    }
    return localOneStepIterator;
  }
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    OneStepIterator localOneStepIterator = (OneStepIterator)super.cloneWithReset();
    m_iterator = m_iterator;
    return localOneStepIterator;
  }
  
  public boolean isReverseAxes()
  {
    return m_iterator.isReverse();
  }
  
  protected int getProximityPosition(int paramInt)
  {
    if (!isReverseAxes()) {
      return super.getProximityPosition(paramInt);
    }
    if (paramInt < 0) {
      return -1;
    }
    if (m_proximityPositions[paramInt] <= 0)
    {
      XPathContext localXPathContext = getXPathContext();
      try
      {
        OneStepIterator localOneStepIterator = (OneStepIterator)clone();
        int i = getRoot();
        localXPathContext.pushCurrentNode(i);
        localOneStepIterator.setRoot(i, localXPathContext);
        m_predCount = paramInt;
        int k;
        for (int j = 1; -1 != (k = localOneStepIterator.nextNode()); j++) {}
        m_proximityPositions[paramInt] += j;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}finally
      {
        localXPathContext.popCurrentNode();
      }
    }
    return m_proximityPositions[paramInt];
  }
  
  public int getLength()
  {
    if (!isReverseAxes()) {
      return super.getLength();
    }
    int i = this == m_execContext.getSubContextList() ? 1 : 0;
    int j = getPredicateCount();
    if ((-1 != m_length) && (i != 0) && (m_predicateIndex < 1)) {
      return m_length;
    }
    int k = 0;
    XPathContext localXPathContext = getXPathContext();
    try
    {
      OneStepIterator localOneStepIterator = (OneStepIterator)cloneWithReset();
      int m = getRoot();
      localXPathContext.pushCurrentNode(m);
      localOneStepIterator.setRoot(m, localXPathContext);
      m_predCount = m_predicateIndex;
      int n;
      while (-1 != (n = localOneStepIterator.nextNode())) {
        k++;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}finally
    {
      localXPathContext.popCurrentNode();
    }
    if ((i != 0) && (m_predicateIndex < 1)) {
      m_length = k;
    }
    return k;
  }
  
  protected void countProximityPosition(int paramInt)
  {
    if (!isReverseAxes()) {
      super.countProximityPosition(paramInt);
    } else if (paramInt < m_proximityPositions.length) {
      m_proximityPositions[paramInt] -= 1;
    }
  }
  
  public void reset()
  {
    super.reset();
    if (null != m_iterator) {
      m_iterator.reset();
    }
  }
  
  public int getAxis()
  {
    return m_axis;
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!super.deepEquals(paramExpression)) {
      return false;
    }
    return m_axis == m_axis;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\OneStepIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */