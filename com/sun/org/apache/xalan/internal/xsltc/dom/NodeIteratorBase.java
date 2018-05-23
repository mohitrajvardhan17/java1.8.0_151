package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.NodeIterator;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;

public abstract class NodeIteratorBase
  implements NodeIterator
{
  protected int _last = -1;
  protected int _position = 0;
  protected int _markedNode;
  protected int _startNode = -1;
  protected boolean _includeSelf = false;
  protected boolean _isRestartable = true;
  
  public NodeIteratorBase() {}
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
  }
  
  public abstract NodeIterator setStartNode(int paramInt);
  
  public NodeIterator reset()
  {
    boolean bool = _isRestartable;
    _isRestartable = true;
    setStartNode(_includeSelf ? _startNode + 1 : _startNode);
    _isRestartable = bool;
    return this;
  }
  
  public NodeIterator includeSelf()
  {
    _includeSelf = true;
    return this;
  }
  
  public int getLast()
  {
    if (_last == -1)
    {
      int i = _position;
      setMark();
      reset();
      do
      {
        _last += 1;
      } while (next() != -1);
      gotoMark();
      _position = i;
    }
    return _last;
  }
  
  public int getPosition()
  {
    return _position == 0 ? 1 : _position;
  }
  
  public boolean isReverse()
  {
    return false;
  }
  
  public NodeIterator cloneIterator()
  {
    try
    {
      NodeIteratorBase localNodeIteratorBase = (NodeIteratorBase)super.clone();
      _isRestartable = false;
      return localNodeIteratorBase.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  protected final int returnNode(int paramInt)
  {
    _position += 1;
    return paramInt;
  }
  
  protected final NodeIterator resetPosition()
  {
    _position = 0;
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeIteratorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */