package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import javax.xml.transform.TransformerException;

public class MatchPatternIterator
  extends LocPathIterator
{
  static final long serialVersionUID = -5201153767396296474L;
  protected StepPattern m_pattern;
  protected int m_superAxis = -1;
  protected DTMAxisTraverser m_traverser;
  private static final boolean DEBUG = false;
  
  MatchPatternIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, false);
    int i = OpMap.getFirstChildPos(paramInt1);
    m_pattern = WalkerFactory.loadSteps(this, paramCompiler, i, 0);
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    if (0 != (paramInt2 & 0x28000000)) {
      j = 1;
    }
    if (0 != (paramInt2 & 0x5D86000)) {
      k = 1;
    }
    if (0 != (paramInt2 & 0x70000)) {
      m = 1;
    }
    if (0 != (paramInt2 & 0x208000)) {
      n = 1;
    }
    if ((j != 0) || (k != 0))
    {
      if (n != 0) {
        m_superAxis = 16;
      } else {
        m_superAxis = 17;
      }
    }
    else if (m != 0)
    {
      if (n != 0) {
        m_superAxis = 14;
      } else {
        m_superAxis = 5;
      }
    }
    else {
      m_superAxis = 16;
    }
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    super.setRoot(paramInt, paramObject);
    m_traverser = m_cdtm.getAxisTraverser(m_superAxis);
  }
  
  public void detach()
  {
    if (m_allowDetach)
    {
      m_traverser = null;
      super.detach();
    }
  }
  
  protected int getNextNode()
  {
    m_lastFetched = (-1 == m_lastFetched ? m_traverser.first(m_context) : m_traverser.next(m_context, m_lastFetched));
    return m_lastFetched;
  }
  
  public int nextNode()
  {
    if (m_foundLast) {
      return -1;
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
        i = getNextNode();
      } while ((-1 != i) && (1 != acceptNode(i, m_execContext)) && (i != -1));
      if (-1 != i)
      {
        incrementCurrentPos();
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
  
  public short acceptNode(int paramInt, XPathContext paramXPathContext)
  {
    try
    {
      paramXPathContext.pushCurrentNode(paramInt);
      paramXPathContext.pushIteratorRoot(m_context);
      XObject localXObject = m_pattern.execute(paramXPathContext);
      short s = localXObject == NodeTest.SCORE_NONE ? 3 : 1;
      return s;
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
    finally
    {
      paramXPathContext.popCurrentNode();
      paramXPathContext.popIteratorRoot();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\MatchPatternIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */