package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class AbsoluteIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  
  public AbsoluteIterator(DTMAxisIterator paramDTMAxisIterator)
  {
    _source = paramDTMAxisIterator;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
    _source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    _startNode = 0;
    if (_isRestartable)
    {
      _source.setStartNode(_startNode);
      resetPosition();
    }
    return this;
  }
  
  public int next()
  {
    return returnNode(_source.next());
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      AbsoluteIterator localAbsoluteIterator = (AbsoluteIterator)super.clone();
      _source = _source.cloneIterator();
      localAbsoluteIterator.resetPosition();
      _isRestartable = false;
      return localAbsoluteIterator;
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
  
  public void setMark()
  {
    _source.setMark();
  }
  
  public void gotoMark()
  {
    _source.gotoMark();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\AbsoluteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */