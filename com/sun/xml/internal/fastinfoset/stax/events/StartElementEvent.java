package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class StartElementEvent
  extends EventBase
  implements StartElement
{
  private Map _attributes;
  private List _namespaces;
  private NamespaceContext _context = null;
  private QName _qname;
  
  public void reset()
  {
    if (_attributes != null) {
      _attributes.clear();
    }
    if (_namespaces != null) {
      _namespaces.clear();
    }
    if (_context != null) {
      _context = null;
    }
  }
  
  public StartElementEvent()
  {
    init();
  }
  
  public StartElementEvent(String paramString1, String paramString2, String paramString3)
  {
    init();
    if (paramString2 == null) {
      paramString2 = "";
    }
    if (paramString1 == null) {
      paramString1 = "";
    }
    _qname = new QName(paramString2, paramString3, paramString1);
    setEventType(1);
  }
  
  public StartElementEvent(QName paramQName)
  {
    init();
    _qname = paramQName;
  }
  
  public StartElementEvent(StartElement paramStartElement)
  {
    this(paramStartElement.getName());
    addAttributes(paramStartElement.getAttributes());
    addNamespaces(paramStartElement.getNamespaces());
  }
  
  protected void init()
  {
    setEventType(1);
    _attributes = new HashMap();
    _namespaces = new ArrayList();
  }
  
  public QName getName()
  {
    return _qname;
  }
  
  public Iterator getAttributes()
  {
    if (_attributes != null)
    {
      Collection localCollection = _attributes.values();
      return new ReadIterator(localCollection.iterator());
    }
    return EmptyIterator.getInstance();
  }
  
  public Iterator getNamespaces()
  {
    if (_namespaces != null) {
      return new ReadIterator(_namespaces.iterator());
    }
    return EmptyIterator.getInstance();
  }
  
  public Attribute getAttributeByName(QName paramQName)
  {
    if (paramQName == null) {
      return null;
    }
    return (Attribute)_attributes.get(paramQName);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return _context;
  }
  
  public void setName(QName paramQName)
  {
    _qname = paramQName;
  }
  
  public String getNamespace()
  {
    return _qname.getNamespaceURI();
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (getNamespace() != null) {
      return getNamespace();
    }
    if (_context != null) {
      return _context.getNamespaceURI(paramString);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    localStringBuilder.append('<').append(nameAsString());
    Iterator localIterator;
    Object localObject;
    if (_attributes != null)
    {
      localIterator = getAttributes();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Attribute)localIterator.next();
        localStringBuilder.append(' ').append(localObject.toString());
      }
    }
    if (_namespaces != null)
    {
      localIterator = _namespaces.iterator();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Namespace)localIterator.next();
        localStringBuilder.append(' ').append(localObject.toString());
      }
    }
    localStringBuilder.append('>');
    return localStringBuilder.toString();
  }
  
  public String nameAsString()
  {
    if ("".equals(_qname.getNamespaceURI())) {
      return _qname.getLocalPart();
    }
    if (_qname.getPrefix() != null) {
      return "['" + _qname.getNamespaceURI() + "']:" + _qname.getPrefix() + ":" + _qname.getLocalPart();
    }
    return "['" + _qname.getNamespaceURI() + "']:" + _qname.getLocalPart();
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
  {
    _context = paramNamespaceContext;
  }
  
  public void addAttribute(Attribute paramAttribute)
  {
    _attributes.put(paramAttribute.getName(), paramAttribute);
  }
  
  public void addAttributes(Iterator paramIterator)
  {
    if (paramIterator != null) {
      while (paramIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)paramIterator.next();
        _attributes.put(localAttribute.getName(), localAttribute);
      }
    }
  }
  
  public void addNamespace(Namespace paramNamespace)
  {
    if (paramNamespace != null) {
      _namespaces.add(paramNamespace);
    }
  }
  
  public void addNamespaces(Iterator paramIterator)
  {
    if (paramIterator != null) {
      while (paramIterator.hasNext())
      {
        Namespace localNamespace = (Namespace)paramIterator.next();
        _namespaces.add(localNamespace);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StartElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */