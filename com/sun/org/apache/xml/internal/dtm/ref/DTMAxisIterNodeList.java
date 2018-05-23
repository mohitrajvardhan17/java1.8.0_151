package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.IntVector;
import org.w3c.dom.Node;

public class DTMAxisIterNodeList
  extends DTMNodeListBase
{
  private DTM m_dtm;
  private DTMAxisIterator m_iter;
  private IntVector m_cachedNodes;
  private int m_last = -1;
  
  private DTMAxisIterNodeList() {}
  
  public DTMAxisIterNodeList(DTM paramDTM, DTMAxisIterator paramDTMAxisIterator)
  {
    if (paramDTMAxisIterator == null)
    {
      m_last = 0;
    }
    else
    {
      m_cachedNodes = new IntVector();
      m_dtm = paramDTM;
    }
    m_iter = paramDTMAxisIterator;
  }
  
  public DTMAxisIterator getDTMAxisIterator()
  {
    return m_iter;
  }
  
  public Node item(int paramInt)
  {
    if (m_iter != null)
    {
      int i = 0;
      int j = m_cachedNodes.size();
      if (j > paramInt)
      {
        i = m_cachedNodes.elementAt(paramInt);
        return m_dtm.getNode(i);
      }
      if (m_last == -1)
      {
        while ((j <= paramInt) && ((i = m_iter.next()) != -1))
        {
          m_cachedNodes.addElement(i);
          j++;
        }
        if (i == -1) {
          m_last = j;
        } else {
          return m_dtm.getNode(i);
        }
      }
    }
    return null;
  }
  
  public int getLength()
  {
    if (m_last == -1)
    {
      int i;
      while ((i = m_iter.next()) != -1) {
        m_cachedNodes.addElement(i);
      }
      m_last = m_cachedNodes.size();
    }
    return m_last;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMAxisIterNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */