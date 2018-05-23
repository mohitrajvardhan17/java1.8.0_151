package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.Node;

public class DTMChildIterNodeList
  extends DTMNodeListBase
{
  private int m_firstChild;
  private DTM m_parentDTM;
  
  private DTMChildIterNodeList() {}
  
  public DTMChildIterNodeList(DTM paramDTM, int paramInt)
  {
    m_parentDTM = paramDTM;
    m_firstChild = paramDTM.getFirstChild(paramInt);
  }
  
  public Node item(int paramInt)
  {
    for (int i = m_firstChild;; i = m_parentDTM.getNextSibling(i))
    {
      paramInt--;
      if ((paramInt < 0) || (i == -1)) {
        break;
      }
    }
    if (i == -1) {
      return null;
    }
    return m_parentDTM.getNode(i);
  }
  
  public int getLength()
  {
    int i = 0;
    for (int j = m_firstChild; j != -1; j = m_parentDTM.getNextSibling(j)) {
      i++;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMChildIterNodeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */