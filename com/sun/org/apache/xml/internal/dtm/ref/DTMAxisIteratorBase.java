package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;

public abstract class DTMAxisIteratorBase
  implements DTMAxisIterator
{
  protected int _last = -1;
  protected int _position = 0;
  protected int _markedNode;
  protected int _startNode = -1;
  protected boolean _includeSelf = false;
  protected boolean _isRestartable = true;
  
  public DTMAxisIteratorBase() {}
  
  public int getStartNode()
  {
    return _startNode;
  }
  
  public DTMAxisIterator reset()
  {
    boolean bool = _isRestartable;
    _isRestartable = true;
    setStartNode(_startNode);
    _isRestartable = bool;
    return this;
  }
  
  public DTMAxisIterator includeSelf()
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
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      DTMAxisIteratorBase localDTMAxisIteratorBase = (DTMAxisIteratorBase)super.clone();
      _isRestartable = false;
      return localDTMAxisIteratorBase;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new WrappedRuntimeException(localCloneNotSupportedException);
    }
  }
  
  protected final int returnNode(int paramInt)
  {
    _position += 1;
    return paramInt;
  }
  
  protected final DTMAxisIterator resetPosition()
  {
    _position = 0;
    return this;
  }
  
  public boolean isDocOrdered()
  {
    return true;
  }
  
  public int getAxis()
  {
    return -1;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    _isRestartable = paramBoolean;
  }
  
  public int getNodeByPosition(int paramInt)
  {
    if (paramInt > 0)
    {
      int i = isReverse() ? getLast() - paramInt + 1 : paramInt;
      int j;
      while ((j = next()) != -1) {
        if (i == getPosition()) {
          return j;
        }
      }
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMAxisIteratorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */