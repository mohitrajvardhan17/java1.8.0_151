package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class AssociationMap<XmlNode>
{
  private final Map<XmlNode, Entry<XmlNode>> byElement = new IdentityHashMap();
  private final Map<Object, Entry<XmlNode>> byPeer = new IdentityHashMap();
  private final Set<XmlNode> usedNodes = new HashSet();
  
  public AssociationMap() {}
  
  public void addInner(XmlNode paramXmlNode, Object paramObject)
  {
    Entry localEntry1 = (Entry)byElement.get(paramXmlNode);
    if (localEntry1 != null)
    {
      if (inner != null) {
        byPeer.remove(inner);
      }
      inner = paramObject;
    }
    else
    {
      localEntry1 = new Entry();
      element = paramXmlNode;
      inner = paramObject;
    }
    byElement.put(paramXmlNode, localEntry1);
    Entry localEntry2 = (Entry)byPeer.put(paramObject, localEntry1);
    if (localEntry2 != null)
    {
      if (outer != null) {
        byPeer.remove(outer);
      }
      if (element != null) {
        byElement.remove(element);
      }
    }
  }
  
  public void addOuter(XmlNode paramXmlNode, Object paramObject)
  {
    Entry localEntry1 = (Entry)byElement.get(paramXmlNode);
    if (localEntry1 != null)
    {
      if (outer != null) {
        byPeer.remove(outer);
      }
      outer = paramObject;
    }
    else
    {
      localEntry1 = new Entry();
      element = paramXmlNode;
      outer = paramObject;
    }
    byElement.put(paramXmlNode, localEntry1);
    Entry localEntry2 = (Entry)byPeer.put(paramObject, localEntry1);
    if (localEntry2 != null)
    {
      outer = null;
      if (inner == null) {
        byElement.remove(element);
      }
    }
  }
  
  public void addUsed(XmlNode paramXmlNode)
  {
    usedNodes.add(paramXmlNode);
  }
  
  public Entry<XmlNode> byElement(Object paramObject)
  {
    return (Entry)byElement.get(paramObject);
  }
  
  public Entry<XmlNode> byPeer(Object paramObject)
  {
    return (Entry)byPeer.get(paramObject);
  }
  
  public Object getInnerPeer(XmlNode paramXmlNode)
  {
    Entry localEntry = byElement(paramXmlNode);
    if (localEntry == null) {
      return null;
    }
    return inner;
  }
  
  public Object getOuterPeer(XmlNode paramXmlNode)
  {
    Entry localEntry = byElement(paramXmlNode);
    if (localEntry == null) {
      return null;
    }
    return outer;
  }
  
  static final class Entry<XmlNode>
  {
    private XmlNode element;
    private Object inner;
    private Object outer;
    
    Entry() {}
    
    public XmlNode element()
    {
      return (XmlNode)element;
    }
    
    public Object inner()
    {
      return inner;
    }
    
    public Object outer()
    {
      return outer;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\AssociationMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */