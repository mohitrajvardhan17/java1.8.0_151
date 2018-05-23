package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.util.BitSet;

public class CoroutineManager
{
  BitSet m_activeIDs = new BitSet();
  static final int m_unreasonableId = 1024;
  Object m_yield = null;
  static final int NOBODY = -1;
  static final int ANYBODY = -1;
  int m_nextCoroutine = -1;
  
  public CoroutineManager() {}
  
  public synchronized int co_joinCoroutineSet(int paramInt)
  {
    if (paramInt >= 0)
    {
      if ((paramInt >= 1024) || (m_activeIDs.get(paramInt))) {
        return -1;
      }
    }
    else
    {
      for (paramInt = 0; (paramInt < 1024) && (m_activeIDs.get(paramInt)); paramInt++) {}
      if (paramInt >= 1024) {
        return -1;
      }
    }
    m_activeIDs.set(paramInt);
    return paramInt;
  }
  
  public synchronized Object co_entry_pause(int paramInt)
    throws NoSuchMethodException
  {
    if (!m_activeIDs.get(paramInt)) {
      throw new NoSuchMethodException();
    }
    while (m_nextCoroutine != paramInt) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    return m_yield;
  }
  
  public synchronized Object co_resume(Object paramObject, int paramInt1, int paramInt2)
    throws NoSuchMethodException
  {
    if (!m_activeIDs.get(paramInt2)) {
      throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_NOT_AVAIL", new Object[] { Integer.toString(paramInt2) }));
    }
    m_yield = paramObject;
    m_nextCoroutine = paramInt2;
    notify();
    while ((m_nextCoroutine != paramInt1) || (m_nextCoroutine == -1) || (m_nextCoroutine == -1)) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    if (m_nextCoroutine == -1)
    {
      co_exit(paramInt1);
      throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_CO_EXIT", null));
    }
    return m_yield;
  }
  
  public synchronized void co_exit(int paramInt)
  {
    m_activeIDs.clear(paramInt);
    m_nextCoroutine = -1;
    notify();
  }
  
  public synchronized void co_exit_to(Object paramObject, int paramInt1, int paramInt2)
    throws NoSuchMethodException
  {
    if (!m_activeIDs.get(paramInt2)) {
      throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_NOT_AVAIL", new Object[] { Integer.toString(paramInt2) }));
    }
    m_yield = paramObject;
    m_nextCoroutine = paramInt2;
    m_activeIDs.clear(paramInt1);
    notify();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\CoroutineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */