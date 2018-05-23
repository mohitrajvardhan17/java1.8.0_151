package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

/**
 * @deprecated
 */
public final class ForwardPositionIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  
  public ForwardPositionIterator(DTMAxisIterator paramDTMAxisIterator)
  {
    _source = paramDTMAxisIterator;
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      ForwardPositionIterator localForwardPositionIterator = (ForwardPositionIterator)super.clone();
      _source = _source.cloneIterator();
      _isRestartable = false;
      return localForwardPositionIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public int next()
  {
    return returnNode(_source.next());
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    _source.setStartNode(paramInt);
    return this;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ForwardPositionIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */