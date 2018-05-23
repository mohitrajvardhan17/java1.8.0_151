package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import javax.xml.transform.TransformerException;

public abstract class BasicTestIterator
  extends LocPathIterator
{
  static final long serialVersionUID = 3505378079378096623L;
  
  protected BasicTestIterator() {}
  
  protected BasicTestIterator(PrefixResolver paramPrefixResolver)
  {
    super(paramPrefixResolver);
  }
  
  protected BasicTestIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, false);
    int i = OpMap.getFirstChildPos(paramInt1);
    int j = paramCompiler.getWhatToShow(i);
    if ((0 == (j & 0x1043)) || (j == -1)) {
      initNodeTest(j);
    } else {
      initNodeTest(j, paramCompiler.getStepNS(i), paramCompiler.getStepLocalName(i));
    }
    initPredicateInfo(paramCompiler, i);
  }
  
  protected BasicTestIterator(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, paramBoolean);
  }
  
  protected abstract int getNextNode();
  
  public int nextNode()
  {
    if (m_foundLast)
    {
      m_lastFetched = -1;
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
        i = getNextNode();
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
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    ChildTestIterator localChildTestIterator = (ChildTestIterator)super.cloneWithReset();
    localChildTestIterator.resetProximityPositions();
    return localChildTestIterator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\BasicTestIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */