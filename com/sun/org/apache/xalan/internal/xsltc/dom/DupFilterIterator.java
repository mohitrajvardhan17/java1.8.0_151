package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class DupFilterIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private IntegerArray _nodes = new IntegerArray();
  private int _current = 0;
  private int _nodesSize = 0;
  private int _lastNext = -1;
  private int _markedLastNext = -1;
  
  public DupFilterIterator(DTMAxisIterator paramDTMAxisIterator)
  {
    _source = paramDTMAxisIterator;
    if ((paramDTMAxisIterator instanceof KeyIndex)) {
      setStartNode(0);
    }
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      boolean bool = _source instanceof KeyIndex;
      if ((bool) && (_startNode == 0)) {
        return this;
      }
      if (paramInt != _startNode)
      {
        _source.setStartNode(_startNode = paramInt);
        _nodes.clear();
        while ((paramInt = _source.next()) != -1) {
          _nodes.add(paramInt);
        }
        if (!bool) {
          _nodes.sort();
        }
        _nodesSize = _nodes.cardinality();
        _current = 0;
        _lastNext = -1;
        resetPosition();
      }
    }
    return this;
  }
  
  public int next()
  {
    while (_current < _nodesSize)
    {
      int i = _nodes.at(_current++);
      if (i != _lastNext) {
        return returnNode(_lastNext = i);
      }
    }
    return -1;
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      DupFilterIterator localDupFilterIterator = (DupFilterIterator)super.clone();
      _nodes = ((IntegerArray)_nodes.clone());
      _source = _source.cloneIterator();
      _isRestartable = false;
      return localDupFilterIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
    _source.setRestartable(paramBoolean);
  }
  
  public void setMark()
  {
    _markedNode = _current;
    _markedLastNext = _lastNext;
  }
  
  public void gotoMark()
  {
    _current = _markedNode;
    _lastNext = _markedLastNext;
  }
  
  public DTMAxisIterator reset()
  {
    _current = 0;
    _lastNext = -1;
    return resetPosition();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DupFilterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */