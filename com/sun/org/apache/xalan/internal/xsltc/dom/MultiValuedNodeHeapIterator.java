package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public abstract class MultiValuedNodeHeapIterator
  extends DTMAxisIteratorBase
{
  private static final int InitSize = 8;
  private int _heapSize = 0;
  private int _size = 8;
  private HeapNode[] _heap = new HeapNode[8];
  private int _free = 0;
  private int _returnedLast;
  private int _cachedReturnedLast = -1;
  private int _cachedHeapSize;
  
  public MultiValuedNodeHeapIterator() {}
  
  public DTMAxisIterator cloneIterator()
  {
    _isRestartable = false;
    HeapNode[] arrayOfHeapNode = new HeapNode[_heap.length];
    try
    {
      MultiValuedNodeHeapIterator localMultiValuedNodeHeapIterator = (MultiValuedNodeHeapIterator)super.clone();
      for (int i = 0; i < _free; i++) {
        arrayOfHeapNode[i] = _heap[i].cloneHeapNode();
      }
      localMultiValuedNodeHeapIterator.setRestartable(false);
      _heap = arrayOfHeapNode;
      return localMultiValuedNodeHeapIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  protected void addHeapNode(HeapNode paramHeapNode)
  {
    if (_free == _size)
    {
      HeapNode[] arrayOfHeapNode = new HeapNode[_size *= 2];
      System.arraycopy(_heap, 0, arrayOfHeapNode, 0, _free);
      _heap = arrayOfHeapNode;
    }
    _heapSize += 1;
    _heap[(_free++)] = paramHeapNode;
  }
  
  public int next()
  {
    while (_heapSize > 0)
    {
      int i = _heap[0]._node;
      if (i == -1)
      {
        if (_heapSize > 1)
        {
          HeapNode localHeapNode = _heap[0];
          _heap[0] = _heap[(--_heapSize)];
          _heap[_heapSize] = localHeapNode;
        }
        else
        {
          return -1;
        }
      }
      else if (i == _returnedLast)
      {
        _heap[0].step();
      }
      else
      {
        _heap[0].step();
        heapify(0);
        return returnNode(_returnedLast = i);
      }
      heapify(0);
    }
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      _startNode = paramInt;
      for (int i = 0; i < _free; i++) {
        if (!_heap[i]._isStartSet)
        {
          _heap[i].setStartNode(paramInt);
          _heap[i].step();
          _heap[i]._isStartSet = true;
        }
      }
      for (i = (_heapSize = _free) / 2; i >= 0; i--) {
        heapify(i);
      }
      _returnedLast = -1;
      return resetPosition();
    }
    return this;
  }
  
  protected void init()
  {
    for (int i = 0; i < _free; i++) {
      _heap[i] = null;
    }
    _heapSize = 0;
    _free = 0;
  }
  
  private void heapify(int paramInt)
  {
    for (;;)
    {
      int i = paramInt + 1 << 1;
      int j = i - 1;
      int k = (j < _heapSize) && (_heap[j].isLessThan(_heap[paramInt])) ? j : paramInt;
      if ((i < _heapSize) && (_heap[i].isLessThan(_heap[k]))) {
        k = i;
      }
      if (k == paramInt) {
        break;
      }
      HeapNode localHeapNode = _heap[k];
      _heap[k] = _heap[paramInt];
      _heap[paramInt] = localHeapNode;
      paramInt = k;
    }
  }
  
  public void setMark()
  {
    for (int i = 0; i < _free; i++) {
      _heap[i].setMark();
    }
    _cachedReturnedLast = _returnedLast;
    _cachedHeapSize = _heapSize;
  }
  
  public void gotoMark()
  {
    for (int i = 0; i < _free; i++) {
      _heap[i].gotoMark();
    }
    for (i = (_heapSize = _cachedHeapSize) / 2; i >= 0; i--) {
      heapify(i);
    }
    _returnedLast = _cachedReturnedLast;
  }
  
  public DTMAxisIterator reset()
  {
    for (int i = 0; i < _free; i++)
    {
      _heap[i].reset();
      _heap[i].step();
    }
    for (i = (_heapSize = _free) / 2; i >= 0; i--) {
      heapify(i);
    }
    _returnedLast = -1;
    return resetPosition();
  }
  
  public abstract class HeapNode
    implements Cloneable
  {
    protected int _node;
    protected int _markedNode;
    protected boolean _isStartSet = false;
    
    public HeapNode() {}
    
    public abstract int step();
    
    public HeapNode cloneHeapNode()
    {
      HeapNode localHeapNode;
      try
      {
        localHeapNode = (HeapNode)super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
        return null;
      }
      _node = _node;
      _markedNode = _node;
      return localHeapNode;
    }
    
    public void setMark()
    {
      _markedNode = _node;
    }
    
    public void gotoMark()
    {
      _node = _markedNode;
    }
    
    public abstract boolean isLessThan(HeapNode paramHeapNode);
    
    public abstract HeapNode setStartNode(int paramInt);
    
    public abstract HeapNode reset();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MultiValuedNodeHeapIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */