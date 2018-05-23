package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class SingletonIterator
  extends DTMAxisIteratorBase
{
  private int _node;
  private final boolean _isConstant;
  
  public SingletonIterator()
  {
    this(Integer.MIN_VALUE, false);
  }
  
  public SingletonIterator(int paramInt)
  {
    this(paramInt, false);
  }
  
  public SingletonIterator(int paramInt, boolean paramBoolean)
  {
    _node = (_startNode = paramInt);
    _isConstant = paramBoolean;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (_isConstant)
    {
      _node = _startNode;
      return resetPosition();
    }
    if (_isRestartable)
    {
      if (_node <= 0) {
        _node = (_startNode = paramInt);
      }
      return resetPosition();
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    if (_isConstant)
    {
      _node = _startNode;
      return resetPosition();
    }
    boolean bool = _isRestartable;
    _isRestartable = true;
    setStartNode(_startNode);
    _isRestartable = bool;
    return this;
  }
  
  public int next()
  {
    int i = _node;
    _node = -1;
    return returnNode(i);
  }
  
  public void setMark()
  {
    _markedNode = _node;
  }
  
  public void gotoMark()
  {
    _node = _markedNode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SingletonIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */