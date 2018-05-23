package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xpath.internal.XPathContext;

public class ReverseAxesWalker
  extends AxesWalker
{
  static final long serialVersionUID = 2847007647832768941L;
  protected DTMAxisIterator m_iterator;
  
  ReverseAxesWalker(LocPathIterator paramLocPathIterator, int paramInt)
  {
    super(paramLocPathIterator, paramInt);
  }
  
  public void setRoot(int paramInt)
  {
    super.setRoot(paramInt);
    m_iterator = getDTM(paramInt).getAxisIterator(m_axis);
    m_iterator.setStartNode(paramInt);
  }
  
  public void detach()
  {
    m_iterator = null;
    super.detach();
  }
  
  protected int getNextNode()
  {
    if (m_foundLast) {
      return -1;
    }
    int i = m_iterator.next();
    if (m_isFresh) {
      m_isFresh = false;
    }
    if (-1 == i) {
      m_foundLast = true;
    }
    return i;
  }
  
  public boolean isReverseAxes()
  {
    return true;
  }
  
  protected int getProximityPosition(int paramInt)
  {
    if (paramInt < 0) {
      return -1;
    }
    int i = m_proximityPositions[paramInt];
    if (i <= 0)
    {
      AxesWalker localAxesWalker = wi().getLastUsedWalker();
      try
      {
        ReverseAxesWalker localReverseAxesWalker = (ReverseAxesWalker)clone();
        localReverseAxesWalker.setRoot(getRoot());
        localReverseAxesWalker.setPredicateCount(paramInt);
        localReverseAxesWalker.setPrevWalker(null);
        localReverseAxesWalker.setNextWalker(null);
        wi().setLastUsedWalker(localReverseAxesWalker);
        i++;
        int j;
        while (-1 != (j = localReverseAxesWalker.nextNode())) {
          i++;
        }
        m_proximityPositions[paramInt] = i;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}finally
      {
        wi().setLastUsedWalker(localAxesWalker);
      }
    }
    return i;
  }
  
  protected void countProximityPosition(int paramInt)
  {
    if (paramInt < m_proximityPositions.length) {
      m_proximityPositions[paramInt] -= 1;
    }
  }
  
  public int getLastPos(XPathContext paramXPathContext)
  {
    int i = 0;
    AxesWalker localAxesWalker = wi().getLastUsedWalker();
    try
    {
      ReverseAxesWalker localReverseAxesWalker = (ReverseAxesWalker)clone();
      localReverseAxesWalker.setRoot(getRoot());
      localReverseAxesWalker.setPredicateCount(getPredicateCount() - 1);
      localReverseAxesWalker.setPrevWalker(null);
      localReverseAxesWalker.setNextWalker(null);
      wi().setLastUsedWalker(localReverseAxesWalker);
      int j;
      while (-1 != (j = localReverseAxesWalker.nextNode())) {
        i++;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}finally
    {
      wi().setLastUsedWalker(localAxesWalker);
    }
    return i;
  }
  
  public boolean isDocOrdered()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\ReverseAxesWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */