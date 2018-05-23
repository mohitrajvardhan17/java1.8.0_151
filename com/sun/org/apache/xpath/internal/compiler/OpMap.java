package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.ObjectVector;
import javax.xml.transform.TransformerException;

public class OpMap
{
  protected String m_currentPattern;
  static final int MAXTOKENQUEUESIZE = 500;
  static final int BLOCKTOKENQUEUESIZE = 500;
  ObjectVector m_tokenQueue = new ObjectVector(500, 500);
  OpMapVector m_opMap = null;
  public static final int MAPINDEX_LENGTH = 1;
  
  public OpMap() {}
  
  public String toString()
  {
    return m_currentPattern;
  }
  
  public String getPatternString()
  {
    return m_currentPattern;
  }
  
  public ObjectVector getTokenQueue()
  {
    return m_tokenQueue;
  }
  
  public Object getToken(int paramInt)
  {
    return m_tokenQueue.elementAt(paramInt);
  }
  
  public int getTokenQueueSize()
  {
    return m_tokenQueue.size();
  }
  
  public OpMapVector getOpMap()
  {
    return m_opMap;
  }
  
  void shrink()
  {
    int i = m_opMap.elementAt(1);
    m_opMap.setToSize(i + 4);
    m_opMap.setElementAt(0, i);
    m_opMap.setElementAt(0, i + 1);
    m_opMap.setElementAt(0, i + 2);
    i = m_tokenQueue.size();
    m_tokenQueue.setToSize(i + 4);
    m_tokenQueue.setElementAt(null, i);
    m_tokenQueue.setElementAt(null, i + 1);
    m_tokenQueue.setElementAt(null, i + 2);
  }
  
  public int getOp(int paramInt)
  {
    return m_opMap.elementAt(paramInt);
  }
  
  public void setOp(int paramInt1, int paramInt2)
  {
    m_opMap.setElementAt(paramInt2, paramInt1);
  }
  
  public int getNextOpPos(int paramInt)
  {
    return paramInt + m_opMap.elementAt(paramInt + 1);
  }
  
  public int getNextStepPos(int paramInt)
  {
    int i = getOp(paramInt);
    if ((i >= 37) && (i <= 53)) {
      return getNextOpPos(paramInt);
    }
    if ((i >= 22) && (i <= 25))
    {
      for (int j = getNextOpPos(paramInt); 29 == getOp(j); j = getNextOpPos(j)) {}
      i = getOp(j);
      if ((i < 37) || (i > 53)) {
        return -1;
      }
      return j;
    }
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_UNKNOWN_STEP", new Object[] { String.valueOf(i) }));
  }
  
  public static int getNextOpPos(int[] paramArrayOfInt, int paramInt)
  {
    return paramInt + paramArrayOfInt[(paramInt + 1)];
  }
  
  public int getFirstPredicateOpPos(int paramInt)
    throws TransformerException
  {
    int i = m_opMap.elementAt(paramInt);
    if ((i >= 37) && (i <= 53)) {
      return paramInt + m_opMap.elementAt(paramInt + 2);
    }
    if ((i >= 22) && (i <= 25)) {
      return paramInt + m_opMap.elementAt(paramInt + 1);
    }
    if (-2 == i) {
      return -2;
    }
    error("ER_UNKNOWN_OPCODE", new Object[] { String.valueOf(i) });
    return -1;
  }
  
  public void error(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    throw new TransformerException(str);
  }
  
  public static int getFirstChildPos(int paramInt)
  {
    return paramInt + 2;
  }
  
  public int getArgLength(int paramInt)
  {
    return m_opMap.elementAt(paramInt + 1);
  }
  
  public int getArgLengthOfStep(int paramInt)
  {
    return m_opMap.elementAt(paramInt + 1 + 1) - 3;
  }
  
  public static int getFirstChildPosOfStep(int paramInt)
  {
    return paramInt + 3;
  }
  
  public int getStepTestType(int paramInt)
  {
    return m_opMap.elementAt(paramInt + 3);
  }
  
  public String getStepNS(int paramInt)
  {
    int i = getArgLengthOfStep(paramInt);
    if (i == 3)
    {
      int j = m_opMap.elementAt(paramInt + 4);
      if (j >= 0) {
        return (String)m_tokenQueue.elementAt(j);
      }
      if (-3 == j) {
        return "*";
      }
      return null;
    }
    return null;
  }
  
  public String getStepLocalName(int paramInt)
  {
    int i = getArgLengthOfStep(paramInt);
    int j;
    switch (i)
    {
    case 0: 
      j = -2;
      break;
    case 1: 
      j = -3;
      break;
    case 2: 
      j = m_opMap.elementAt(paramInt + 4);
      break;
    case 3: 
      j = m_opMap.elementAt(paramInt + 5);
      break;
    default: 
      j = -2;
    }
    if (j >= 0) {
      return m_tokenQueue.elementAt(j).toString();
    }
    if (-3 == j) {
      return "*";
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\compiler\OpMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */