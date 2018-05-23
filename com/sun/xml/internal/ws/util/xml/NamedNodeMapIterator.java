package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapIterator
  implements Iterator
{
  protected NamedNodeMap _map;
  protected int _index;
  
  public NamedNodeMapIterator(NamedNodeMap paramNamedNodeMap)
  {
    _map = paramNamedNodeMap;
    _index = 0;
  }
  
  public boolean hasNext()
  {
    if (_map == null) {
      return false;
    }
    return _index < _map.getLength();
  }
  
  public Object next()
  {
    Node localNode = _map.item(_index);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\NamedNodeMapIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */