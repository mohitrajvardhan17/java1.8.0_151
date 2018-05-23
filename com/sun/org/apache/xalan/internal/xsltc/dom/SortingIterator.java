package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class SortingIterator
  extends DTMAxisIteratorBase
{
  private static final int INIT_DATA_SIZE = 16;
  private DTMAxisIterator _source;
  private NodeSortRecordFactory _factory;
  private NodeSortRecord[] _data;
  private int _free = 0;
  private int _current;
  
  public SortingIterator(DTMAxisIterator paramDTMAxisIterator, NodeSortRecordFactory paramNodeSortRecordFactory)
  {
    _source = paramDTMAxisIterator;
    _factory = paramNodeSortRecordFactory;
  }
  
  public int next()
  {
    return _current < _free ? _data[(_current++)].getNode() : -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    try
    {
      _source.setStartNode(_startNode = paramInt);
      _data = new NodeSortRecord[16];
      _free = 0;
      while ((paramInt = _source.next()) != -1) {
        addRecord(_factory.makeNodeSortRecord(paramInt, _free));
      }
      quicksort(0, _free - 1);
      _current = 0;
      return this;
    }
    catch (Exception localException) {}
    return this;
  }
  
  public int getPosition()
  {
    return _current == 0 ? 1 : _current;
  }
  
  public int getLast()
  {
    return _free;
  }
  
  public void setMark()
  {
    _source.setMark();
    _markedNode = _current;
  }
  
  public void gotoMark()
  {
    _source.gotoMark();
    _current = _markedNode;
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      SortingIterator localSortingIterator = (SortingIterator)super.clone();
      _source = _source.cloneIterator();
      _factory = _factory;
      _data = _data;
      _free = _free;
      _current = _current;
      localSortingIterator.setRestartable(false);
      return localSortingIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  private void addRecord(NodeSortRecord paramNodeSortRecord)
  {
    if (_free == _data.length)
    {
      NodeSortRecord[] arrayOfNodeSortRecord = new NodeSortRecord[_data.length * 2];
      System.arraycopy(_data, 0, arrayOfNodeSortRecord, 0, _free);
      _data = arrayOfNodeSortRecord;
    }
    _data[(_free++)] = paramNodeSortRecord;
  }
  
  private void quicksort(int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      int i = partition(paramInt1, paramInt2);
      quicksort(paramInt1, i);
      paramInt1 = i + 1;
    }
  }
  
  private int partition(int paramInt1, int paramInt2)
  {
    NodeSortRecord localNodeSortRecord1 = _data[(paramInt1 + paramInt2 >>> 1)];
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    for (;;)
    {
      if (localNodeSortRecord1.compareTo(_data[(--j)]) >= 0)
      {
        while (localNodeSortRecord1.compareTo(_data[(++i)]) > 0) {}
        if (i >= j) {
          break;
        }
        NodeSortRecord localNodeSortRecord2 = _data[i];
        _data[i] = _data[j];
        _data[j] = localNodeSortRecord2;
      }
    }
    return j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SortingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */