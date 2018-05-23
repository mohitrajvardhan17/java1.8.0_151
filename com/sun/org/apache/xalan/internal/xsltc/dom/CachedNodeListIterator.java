package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class CachedNodeListIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private IntegerArray _nodes = new IntegerArray();
  private int _numCachedNodes = 0;
  private int _index = 0;
  private boolean _isEnded = false;
  
  public CachedNodeListIterator(DTMAxisIterator paramDTMAxisIterator)
  {
    _source = paramDTMAxisIterator;
  }
  
  public void setRestartable(boolean paramBoolean) {}
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      _startNode = paramInt;
      _source.setStartNode(paramInt);
      resetPosition();
      _isRestartable = false;
    }
    return this;
  }
  
  public int next()
  {
    return getNode(_index++);
  }
  
  public int getPosition()
  {
    return _index == 0 ? 1 : _index;
  }
  
  public int getNodeByPosition(int paramInt)
  {
    return getNode(paramInt);
  }
  
  public int getNode(int paramInt)
  {
    if (paramInt < _numCachedNodes) {
      return _nodes.at(paramInt);
    }
    if (!_isEnded)
    {
      int i = _source.next();
      if (i != -1)
      {
        _nodes.add(i);
        _numCachedNodes += 1;
      }
      else
      {
        _isEnded = true;
      }
      return i;
    }
    return -1;
  }
  
  public DTMAxisIterator cloneIterator()
  {
    ClonedNodeListIterator localClonedNodeListIterator = new ClonedNodeListIterator(this);
    return localClonedNodeListIterator;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CachedNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */