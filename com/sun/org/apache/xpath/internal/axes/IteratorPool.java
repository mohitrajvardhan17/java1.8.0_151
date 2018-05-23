package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.Serializable;
import java.util.ArrayList;

public final class IteratorPool
  implements Serializable
{
  static final long serialVersionUID = -460927331149566998L;
  private final DTMIterator m_orig;
  private final ArrayList m_freeStack;
  
  public IteratorPool(DTMIterator paramDTMIterator)
  {
    m_orig = paramDTMIterator;
    m_freeStack = new ArrayList();
  }
  
  public synchronized DTMIterator getInstanceOrThrow()
    throws CloneNotSupportedException
  {
    if (m_freeStack.isEmpty()) {
      return (DTMIterator)m_orig.clone();
    }
    DTMIterator localDTMIterator = (DTMIterator)m_freeStack.remove(m_freeStack.size() - 1);
    return localDTMIterator;
  }
  
  public synchronized DTMIterator getInstance()
  {
    if (m_freeStack.isEmpty()) {
      try
      {
        return (DTMIterator)m_orig.clone();
      }
      catch (Exception localException)
      {
        throw new WrappedRuntimeException(localException);
      }
    }
    DTMIterator localDTMIterator = (DTMIterator)m_freeStack.remove(m_freeStack.size() - 1);
    return localDTMIterator;
  }
  
  public synchronized void freeInstance(DTMIterator paramDTMIterator)
  {
    m_freeStack.add(paramDTMIterator);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\IteratorPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */