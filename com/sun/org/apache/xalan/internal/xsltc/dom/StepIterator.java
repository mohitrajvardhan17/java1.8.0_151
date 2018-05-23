package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class StepIterator
  extends DTMAxisIteratorBase
{
  protected DTMAxisIterator _source;
  protected DTMAxisIterator _iterator;
  private int _pos = -1;
  
  public StepIterator(DTMAxisIterator paramDTMAxisIterator1, DTMAxisIterator paramDTMAxisIterator2)
  {
    _source = paramDTMAxisIterator1;
    _iterator = paramDTMAxisIterator2;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
    _source.setRestartable(paramBoolean);
    _iterator.setRestartable(true);
  }
  
  public DTMAxisIterator cloneIterator()
  {
    _isRestartable = false;
    try
    {
      StepIterator localStepIterator = (StepIterator)super.clone();
      _source = _source.cloneIterator();
      _iterator = _iterator.cloneIterator();
      _iterator.setRestartable(true);
      _isRestartable = false;
      return localStepIterator.reset();
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
      _source.setStartNode(_startNode = paramInt);
      _iterator.setStartNode(_includeSelf ? _startNode : _source.next());
      return resetPosition();
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    _source.reset();
    _iterator.setStartNode(_includeSelf ? _startNode : _source.next());
    return resetPosition();
  }
  
  public int next()
  {
    for (;;)
    {
      int i;
      if ((i = _iterator.next()) != -1) {
        return returnNode(i);
      }
      if ((i = _source.next()) == -1) {
        return -1;
      }
      _iterator.setStartNode(i);
    }
  }
  
  public void setMark()
  {
    _source.setMark();
    _iterator.setMark();
  }
  
  public void gotoMark()
  {
    _source.gotoMark();
    _iterator.gotoMark();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\StepIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */