package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class ClonedNodeListIterator
  extends DTMAxisIteratorBase
{
  private CachedNodeListIterator _source;
  private int _index = 0;
  
  public ClonedNodeListIterator(CachedNodeListIterator paramCachedNodeListIterator)
  {
    _source = paramCachedNodeListIterator;
  }
  
  public void setRestartable(boolean paramBoolean) {}
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    return this;
  }
  
  public int next()
  {
    return _source.getNode(_index++);
  }
  
  public int getPosition()
  {
    return _index == 0 ? 1 : _index;
  }
  
  public int getNodeByPosition(int paramInt)
  {
    return _source.getNode(paramInt);
  }
  
  public DTMAxisIterator cloneIterator()
  {
    return _source.cloneIterator();
  }
  
  public DTMAxisIterator reset()
  {
    _index = 0;
    return this;
  }
  
  public void setMark()
  {
    _source.setMark();
  }
  
  public void gotoMark()
  {
    _source.gotoMark();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ClonedNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */