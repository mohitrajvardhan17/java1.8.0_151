package com.sun.xml.internal.stream.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceImpl
  extends AttributeImpl
  implements Namespace
{
  public NamespaceImpl()
  {
    init();
  }
  
  public NamespaceImpl(String paramString)
  {
    super("xmlns", "http://www.w3.org/2000/xmlns/", "", paramString, null);
    init();
  }
  
  public NamespaceImpl(String paramString1, String paramString2)
  {
    super("xmlns", "http://www.w3.org/2000/xmlns/", paramString1, paramString2, null);
    init();
  }
  
  public boolean isDefaultNamespaceDeclaration()
  {
    QName localQName = getName();
    return (localQName != null) && (localQName.getLocalPart().equals(""));
  }
  
  void setPrefix(String paramString)
  {
    if (paramString == null) {
      setName(new QName("http://www.w3.org/2000/xmlns/", "", "xmlns"));
    } else {
      setName(new QName("http://www.w3.org/2000/xmlns/", paramString, "xmlns"));
    }
  }
  
  public String getPrefix()
  {
    QName localQName = getName();
    if (localQName != null) {
      return localQName.getLocalPart();
    }
    return null;
  }
  
  public String getNamespaceURI()
  {
    return getValue();
  }
  
  void setNamespaceURI(String paramString)
  {
    setValue(paramString);
  }
  
  protected void init()
  {
    setEventType(13);
  }
  
  public int getEventType()
  {
    return 13;
  }
  
  public boolean isNamespace()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\NamespaceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */