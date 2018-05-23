package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class CurrentNodeListIterator
  extends DTMAxisIteratorBase
{
  private boolean _docOrder;
  private DTMAxisIterator _source;
  private final CurrentNodeListFilter _filter;
  private IntegerArray _nodes = new IntegerArray();
  private int _currentIndex;
  private final int _currentNode;
  private AbstractTranslet _translet;
  
  public CurrentNodeListIterator(DTMAxisIterator paramDTMAxisIterator, CurrentNodeListFilter paramCurrentNodeListFilter, int paramInt, AbstractTranslet paramAbstractTranslet)
  {
    this(paramDTMAxisIterator, !paramDTMAxisIterator.isReverse(), paramCurrentNodeListFilter, paramInt, paramAbstractTranslet);
  }
  
  public CurrentNodeListIterator(DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean, CurrentNodeListFilter paramCurrentNodeListFilter, int paramInt, AbstractTranslet paramAbstractTranslet)
  {
    _source = paramDTMAxisIterator;
    _filter = paramCurrentNodeListFilter;
    _translet = paramAbstractTranslet;
    _docOrder = paramBoolean;
    _currentNode = paramInt;
  }
  
  public DTMAxisIterator forceNaturalOrder()
  {
    _docOrder = true;
    return this;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
    _source.setRestartable(paramBoolean);
  }
  
  public boolean isReverse()
  {
    return !_docOrder;
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      CurrentNodeListIterator localCurrentNodeListIterator = (CurrentNodeListIterator)super.clone();
      _nodes = ((IntegerArray)_nodes.clone());
      _source = _source.cloneIterator();
      _isRestartable = false;
      return localCurrentNodeListIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public DTMAxisIterator reset()
  {
    _currentIndex = 0;
    return resetPosition();
  }
  
  public int next()
  {
    int i = _nodes.cardinality();
    int j = _currentNode;
    AbstractTranslet localAbstractTranslet = _translet;
    int k = _currentIndex;
    while (k < i)
    {
      int m = _docOrder ? k + 1 : i - k;
      int n = _nodes.at(k++);
      if (_filter.test(n, m, i, j, localAbstractTranslet, this))
      {
        _currentIndex = k;
        return returnNode(n);
      }
    }
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      _source.setStartNode(_startNode = paramInt);
      _nodes.clear();
      while ((paramInt = _source.next()) != -1) {
        _nodes.add(paramInt);
      }
      _currentIndex = 0;
      resetPosition();
    }
    return this;
  }
  
  public int getLast()
  {
    if (_last == -1) {
      _last = computePositionOfLast();
    }
    return _last;
  }
  
  public void setMark()
  {
    _markedNode = _currentIndex;
  }
  
  public void gotoMark()
  {
    _currentIndex = _markedNode;
  }
  
  private int computePositionOfLast()
  {
    int i = _nodes.cardinality();
    int j = _currentNode;
    AbstractTranslet localAbstractTranslet = _translet;
    int k = _position;
    int m = _currentIndex;
    while (m < i)
    {
      int n = _docOrder ? m + 1 : i - m;
      int i1 = _nodes.at(m++);
      if (_filter.test(i1, n, i, j, localAbstractTranslet, this)) {
        k++;
      }
    }
    return k;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CurrentNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */