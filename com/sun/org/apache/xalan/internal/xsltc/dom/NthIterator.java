package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class NthIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private final int _position;
  private boolean _ready;
  
  public NthIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt)
  {
    _source = paramDTMAxisIterator;
    _position = paramInt;
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
      NthIterator localNthIterator = (NthIterator)super.clone();
      _source = _source.cloneIterator();
      _isRestartable = false;
      return localNthIterator;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public int next()
  {
    if (_ready)
    {
      _ready = false;
      return _source.getNodeByPosition(_position);
    }
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isRestartable)
    {
      _source.setStartNode(paramInt);
      _ready = true;
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    _source.reset();
    _ready = true;
    return this;
  }
  
  public int getLast()
  {
    return 1;
  }
  
  public int getPosition()
  {
    return 1;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NthIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */