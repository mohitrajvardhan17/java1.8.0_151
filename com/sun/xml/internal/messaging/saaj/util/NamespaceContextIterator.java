package com.sun.xml.internal.messaging.saaj.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamespaceContextIterator
  implements Iterator
{
  Node context;
  NamedNodeMap attributes = null;
  int attributesLength;
  int attributeIndex;
  Attr next = null;
  Attr last = null;
  boolean traverseStack = true;
  
  public NamespaceContextIterator(Node paramNode)
  {
    context = paramNode;
    findContextAttributes();
  }
  
  public NamespaceContextIterator(Node paramNode, boolean paramBoolean)
  {
    this(paramNode);
    traverseStack = paramBoolean;
  }
  
  protected void findContextAttributes()
  {
    while (context != null)
    {
      int i = context.getNodeType();
      if (i == 1)
      {
        attributes = context.getAttributes();
        attributesLength = attributes.getLength();
        attributeIndex = 0;
        return;
      }
      context = null;
    }
  }
  
  protected void findNext()
  {
    while ((next == null) && (context != null))
    {
      while (attributeIndex < attributesLength)
      {
        Node localNode = attributes.item(attributeIndex);
        String str = localNode.getNodeName();
        if ((str.startsWith("xmlns")) && ((str.length() == 5) || (str.charAt(5) == ':')))
        {
          next = ((Attr)localNode);
          attributeIndex += 1;
          return;
        }
        attributeIndex += 1;
      }
      if (traverseStack)
      {
        context = context.getParentNode();
        findContextAttributes();
      }
      else
      {
        context = null;
      }
    }
  }
  
  public boolean hasNext()
  {
    findNext();
    return next != null;
  }
  
  public Object next()
  {
    return getNext();
  }
  
  public Attr nextNamespaceAttr()
  {
    return getNext();
  }
  
  protected Attr getNext()
  {
    findNext();
    if (next == null) {
      throw new NoSuchElementException();
    }
    last = next;
    next = null;
    return last;
  }
  
  public void remove()
  {
    if (last == null) {
      throw new IllegalStateException();
    }
    ((Element)context).removeAttributeNode(last);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\NamespaceContextIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */