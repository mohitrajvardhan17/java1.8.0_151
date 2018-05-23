package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class FilterIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private final DTMFilter _filter;
  private final boolean _isReverse;
  
  public FilterIterator(DTMAxisIterator paramDTMAxisIterator, DTMFilter paramDTMFilter)
  {
    _source = paramDTMAxisIterator;
    _filter = paramDTMFilter;
    _isReverse = paramDTMAxisIterator.isReverse();
  }
  
  public boolean isReverse()
  {
    return _isReverse;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
    _source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      FilterIterator localFilterIterator = (FilterIterator)super.clone();
      _source = _source.cloneIterator();
      _isRestartable = false;
      return localFilterIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public DTMAxisIterator reset()
  {
    _source.reset();
    return resetPosition();
  }
  
  public int next()
  {
    int i;
    while ((i = _source.next()) != -1) {
      if (_filter.acceptNode(i, -1) == 1) {
        return returnNode(i);
      }
    }
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      _source.setStartNode(_startNode = paramInt);
      return resetPosition();
    }
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\FilterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */