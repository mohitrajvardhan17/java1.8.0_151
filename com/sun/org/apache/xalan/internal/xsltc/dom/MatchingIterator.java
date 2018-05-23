package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class MatchingIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private final int _match;
  
  public MatchingIterator(int paramInt, DTMAxisIterator paramDTMAxisIterator)
  {
    _source = paramDTMAxisIterator;
    _match = paramInt;
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
      MatchingIterator localMatchingIterator = (MatchingIterator)super.clone();
      _source = _source.cloneIterator();
      _isRestartable = false;
      return localMatchingIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      _source.setStartNode(paramInt);
      for (_position = 1; ((paramInt = _source.next()) != -1) && (paramInt != _match); _position += 1) {}
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    _source.reset();
    return resetPosition();
  }
  
  public int next()
  {
    return _source.next();
  }
  
  public int getLast()
  {
    if (_last == -1) {
      _last = _source.getLast();
    }
    return _last;
  }
  
  public int getPosition()
  {
    return _position;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MatchingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */