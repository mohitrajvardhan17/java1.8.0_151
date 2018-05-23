package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import org.w3c.dom.Node;

public class DTMNodeList
  extends DTMNodeListBase
{
  private DTMIterator m_iter;
  
  private DTMNodeList() {}
  
  public DTMNodeList(DTMIterator paramDTMIterator)
  {
    if (paramDTMIterator != null)
    {
      int i = paramDTMIterator.getCurrentPos();
      try
      {
        m_iter = paramDTMIterator.cloneWithReset();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        m_iter = paramDTMIterator;
      }
      m_iter.setShouldCacheNodes(true);
      m_iter.runTo(-1);
      m_iter.setCurrentPos(i);
    }
  }
  
  public DTMIterator getDTMIterator()
  {
    return m_iter;
  }
  
  public Node item(int paramInt)
  {
    if (m_iter != null)
    {
      int i = m_iter.item(paramInt);
      if (i == -1) {
        return null;
      }
      return m_iter.getDTM(i).getNode(i);
    }
    return null;
  }
  
  public int getLength()
  {
    return m_iter != null ? m_iter.getLength() : 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */