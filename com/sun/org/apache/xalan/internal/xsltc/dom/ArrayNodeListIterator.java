package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public class ArrayNodeListIterator
  implements DTMAxisIterator
{
  private int _pos = 0;
  private int _mark = 0;
  private int[] _nodes;
  private static final int[] EMPTY = new int[0];
  
  public ArrayNodeListIterator(int[] paramArrayOfInt)
  {
    _nodes = paramArrayOfInt;
  }
  
  public int next()
  {
    return _pos < _nodes.length ? _nodes[(_pos++)] : -1;
  }
  
  public DTMAxisIterator reset()
  {
    _pos = 0;
    return this;
  }
  
  public int getLast()
  {
    return _nodes.length;
  }
  
  public int getPosition()
  {
    return _pos;
  }
  
  public void setMark()
  {
    _mark = _pos;
  }
  
  public void gotoMark()
  {
    _pos = _mark;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (paramInt == -1) {
      _nodes = EMPTY;
    }
    return this;
  }
  
  public int getStartNode()
  {
    return -1;
  }
  
  public boolean isReverse()
  {
    return false;
  }
  
  public DTMAxisIterator cloneIterator()
  {
    return new ArrayNodeListIterator(_nodes);
  }
  
  public void setRestartable(boolean paramBoolean) {}
  
  public int getNodeByPosition(int paramInt)
  {
    return _nodes[(paramInt - 1)];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ArrayNodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */