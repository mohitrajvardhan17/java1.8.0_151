package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public class DescendantIterator
  extends LocPathIterator
{
  static final long serialVersionUID = -1190338607743976938L;
  protected transient DTMAxisTraverser m_traverser;
  protected int m_axis;
  protected int m_extendedTypeID;
  
  DescendantIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, false);
    int i = OpMap.getFirstChildPos(paramInt1);
    int j = paramCompiler.getOp(i);
    int k = 42 == j ? 1 : 0;
    int m = 0;
    if (48 == j)
    {
      k = 1;
    }
    else if (50 == j)
    {
      m = 1;
      n = paramCompiler.getNextStepPos(i);
      if (paramCompiler.getOp(n) == 42) {
        k = 1;
      }
    }
    int n = i;
    for (;;)
    {
      n = paramCompiler.getNextStepPos(n);
      if (n <= 0) {
        break;
      }
      i1 = paramCompiler.getOp(n);
      if (-1 == i1) {
        break;
      }
      i = n;
    }
    if ((paramInt2 & 0x10000) != 0) {
      k = 0;
    }
    if (m != 0)
    {
      if (k != 0) {
        m_axis = 18;
      } else {
        m_axis = 17;
      }
    }
    else if (k != 0) {
      m_axis = 5;
    } else {
      m_axis = 4;
    }
    int i1 = paramCompiler.getWhatToShow(i);
    if ((0 == (i1 & 0x43)) || (i1 == -1)) {
      initNodeTest(i1);
    } else {
      initNodeTest(i1, paramCompiler.getStepNS(i), paramCompiler.getStepLocalName(i));
    }
    initPredicateInfo(paramCompiler, i);
  }
  
  public DescendantIterator()
  {
    super(null);
    m_axis = 18;
    int i = -1;
    initNodeTest(i);
  }
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    DescendantIterator localDescendantIterator = (DescendantIterator)super.cloneWithReset();
    m_traverser = m_traverser;
    localDescendantIterator.resetProximityPositions();
    return localDescendantIterator;
  }
  
  public int nextNode()
  {
    if (m_foundLast) {
      return -1;
    }
    if (-1 == m_lastFetched) {
      resetProximityPositions();
    }
    VariableStack localVariableStack;
    int j;
    if (-1 != m_stackFrame)
    {
      localVariableStack = m_execContext.getVarStack();
      j = localVariableStack.getStackFrame();
      localVariableStack.setStackFrame(m_stackFrame);
    }
    else
    {
      localVariableStack = null;
      j = 0;
    }
    try
    {
      int i;
      do
      {
        if (0 == m_extendedTypeID) {
          i = m_lastFetched = -1 == m_lastFetched ? m_traverser.first(m_context) : m_traverser.next(m_context, m_lastFetched);
        } else {
          i = m_lastFetched = -1 == m_lastFetched ? m_traverser.first(m_context, m_extendedTypeID) : m_traverser.next(m_context, m_lastFetched, m_extendedTypeID);
        }
      } while ((-1 != i) && (1 != acceptNode(i)) && (i != -1));
      if (-1 != i)
      {
        m_pos += 1;
        k = i;
        return k;
      }
      m_foundLast = true;
      int k = -1;
      return k;
    }
    finally
    {
      if (-1 != m_stackFrame) {
        localVariableStack.setStackFrame(j);
      }
    }
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    m_traverser = m_cdtm.getAxisTraverser(m_axis);
    String str1 = getLocalName();
    String str2 = getNamespace();
    int i = m_whatToShow;
    if ((-1 == i) || ("*".equals(str1)) || ("*".equals(str2)))
    {
      m_extendedTypeID = 0;
    }
    else
    {
      int j = getNodeTypeTest(i);
      m_extendedTypeID = m_cdtm.getExpandedTypeID(str2, str1, j);
    }
  }
  
  public int asNode(XPathContext paramXPathContext)
    throws TransformerException
  {
    if (getPredicateCount() > 0) {
      return super.asNode(paramXPathContext);
    }
    int i = paramXPathContext.getCurrentNode();
    DTM localDTM = paramXPathContext.getDTM(i);
    DTMAxisTraverser localDTMAxisTraverser = localDTM.getAxisTraverser(m_axis);
    String str1 = getLocalName();
    String str2 = getNamespace();
    int j = m_whatToShow;
    if ((-1 == j) || (str1 == "*") || (str2 == "*")) {
      return localDTMAxisTraverser.first(i);
    }
    int k = getNodeTypeTest(j);
    int m = localDTM.getExpandedTypeID(str2, str1, k);
    return localDTMAxisTraverser.first(i, m);
  }
  
  public void detach()
  {
    if (m_allowDetach)
    {
      m_traverser = null;
      m_extendedTypeID = 0;
      super.detach();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\DescendantIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */