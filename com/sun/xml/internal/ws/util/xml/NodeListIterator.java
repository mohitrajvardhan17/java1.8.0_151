package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator
  implements Iterator
{
  protected NodeList _list;
  protected int _index;
  
  public NodeListIterator(NodeList paramNodeList)
  {
    _list = paramNodeList;
    _index = 0;
  }
  
  public boolean hasNext()
  {
    if (_list == null) {
      return false;
    }
    return _index < _list.getLength();
  }
  
  public Object next()
  {
    Node localNode = _list.item(_index);
    if (localNode != null) {
      _index += 1;
    }
    return localNode;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\NodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */