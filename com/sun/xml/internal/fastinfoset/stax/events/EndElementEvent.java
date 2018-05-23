package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

public class EndElementEvent
  extends EventBase
  implements EndElement
{
  List _namespaces = null;
  QName _qname;
  
  public void reset()
  {
    if (_namespaces != null) {
      _namespaces.clear();
    }
  }
  
  public EndElementEvent()
  {
    setEventType(2);
  }
  
  public EndElementEvent(String paramString1, String paramString2, String paramString3)
  {
    _qname = getQName(paramString2, paramString3, paramString1);
    setEventType(2);
  }
  
  public EndElementEvent(QName paramQName)
  {
    _qname = paramQName;
    setEventType(2);
  }
  
  public QName getName()
  {
    return _qname;
  }
  
  public void setName(QName paramQName)
  {
    _qname = paramQName;
  }
  
  public Iterator getNamespaces()
  {
    if (_namespaces != null) {
      return _namespaces.iterator();
    }
    return EmptyIterator.getInstance();
  }
  
  public void addNamespace(Namespace paramNamespace)
  {
    if (_namespaces == null) {
      _namespaces = new ArrayList();
    }
    _namespaces.add(paramNamespace);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("</").append(nameAsString());
    Iterator localIterator = getNamespaces();
    while (localIterator.hasNext()) {
      localStringBuffer.append(" ").append(localIterator.next().toString());
    }
    localStringBuffer.append(">");
    return localStringBuffer.toString();
  }
  
  private String nameAsString()
  {
    if ("".equals(_qname.getNamespaceURI())) {
      return _qname.getLocalPart();
    }
    if (_qname.getPrefix() != null) {
      return "['" + _qname.getNamespaceURI() + "']:" + _qname.getPrefix() + ":" + _qname.getLocalPart();
    }
    return "['" + _qname.getNamespaceURI() + "']:" + _qname.getLocalPart();
  }
  
  private QName getQName(String paramString1, String paramString2, String paramString3)
  {
    QName localQName = null;
    if ((paramString3 != null) && (paramString1 != null)) {
      localQName = new QName(paramString1, paramString2, paramString3);
    } else if ((paramString3 == null) && (paramString1 != null)) {
      localQName = new QName(paramString1, paramString2);
    } else if ((paramString3 == null) && (paramString1 == null)) {
      localQName = new QName(paramString2);
    }
    return localQName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EndElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */