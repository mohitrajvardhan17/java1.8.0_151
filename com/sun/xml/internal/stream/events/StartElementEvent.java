package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.IOException;
import java.io.Writer;
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
  extends DummyEvent
  implements StartElement
{
  private Map fAttributes;
  private List fNamespaces;
  private NamespaceContext fNamespaceContext = null;
  private QName fQName;
  
  public StartElementEvent(String paramString1, String paramString2, String paramString3)
  {
    this(new QName(paramString2, paramString3, paramString1));
  }
  
  public StartElementEvent(QName paramQName)
  {
    fQName = paramQName;
    init();
  }
  
  public StartElementEvent(StartElement paramStartElement)
  {
    this(paramStartElement.getName());
    addAttributes(paramStartElement.getAttributes());
    addNamespaceAttributes(paramStartElement.getNamespaces());
  }
  
  protected void init()
  {
    setEventType(1);
    fAttributes = new HashMap();
    fNamespaces = new ArrayList();
  }
  
  public QName getName()
  {
    return fQName;
  }
  
  public void setName(QName paramQName)
  {
    fQName = paramQName;
  }
  
  public Iterator getAttributes()
  {
    if (fAttributes != null)
    {
      Collection localCollection = fAttributes.values();
      return new ReadOnlyIterator(localCollection.iterator());
    }
    return new ReadOnlyIterator();
  }
  
  public Iterator getNamespaces()
  {
    if (fNamespaces != null) {
      return new ReadOnlyIterator(fNamespaces.iterator());
    }
    return new ReadOnlyIterator();
  }
  
  public Attribute getAttributeByName(QName paramQName)
  {
    if (paramQName == null) {
      return null;
    }
    return (Attribute)fAttributes.get(paramQName);
  }
  
  public String getNamespace()
  {
    return fQName.getNamespaceURI();
  }
  
  public String getNamespaceURI(String paramString)
  {
    if ((getNamespace() != null) && (fQName.getPrefix().equals(paramString))) {
      return getNamespace();
    }
    if (fNamespaceContext != null) {
      return fNamespaceContext.getNamespaceURI(paramString);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("<");
    localStringBuffer.append(nameAsString());
    Iterator localIterator;
    Object localObject;
    if (fAttributes != null)
    {
      localIterator = getAttributes();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Attribute)localIterator.next();
        localStringBuffer.append(" ");
        localStringBuffer.append(localObject.toString());
      }
    }
    if (fNamespaces != null)
    {
      localIterator = fNamespaces.iterator();
      localObject = null;
      while (localIterator.hasNext())
      {
        localObject = (Namespace)localIterator.next();
        localStringBuffer.append(" ");
        localStringBuffer.append(localObject.toString());
      }
    }
    localStringBuffer.append(">");
    return localStringBuffer.toString();
  }
  
  public String nameAsString()
  {
    if ("".equals(fQName.getNamespaceURI())) {
      return fQName.getLocalPart();
    }
    if (fQName.getPrefix() != null) {
      return "['" + fQName.getNamespaceURI() + "']:" + fQName.getPrefix() + ":" + fQName.getLocalPart();
    }
    return "['" + fQName.getNamespaceURI() + "']:" + fQName.getLocalPart();
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return fNamespaceContext;
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
  {
    fNamespaceContext = paramNamespaceContext;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(toString());
  }
  
  void addAttribute(Attribute paramAttribute)
  {
    if (paramAttribute.isNamespace()) {
      fNamespaces.add(paramAttribute);
    } else {
      fAttributes.put(paramAttribute.getName(), paramAttribute);
    }
  }
  
  void addAttributes(Iterator paramIterator)
  {
    if (paramIterator == null) {
      return;
    }
    while (paramIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)paramIterator.next();
      fAttributes.put(localAttribute.getName(), localAttribute);
    }
  }
  
  void addNamespaceAttribute(Namespace paramNamespace)
  {
    if (paramNamespace == null) {
      return;
    }
    fNamespaces.add(paramNamespace);
  }
  
  void addNamespaceAttributes(Iterator paramIterator)
  {
    if (paramIterator == null) {
      return;
    }
    while (paramIterator.hasNext())
    {
      Namespace localNamespace = (Namespace)paramIterator.next();
      fNamespaces.add(localNamespace);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\StartElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */