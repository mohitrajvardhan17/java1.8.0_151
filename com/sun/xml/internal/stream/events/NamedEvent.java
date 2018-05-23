package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;

public class NamedEvent
  extends DummyEvent
{
  private QName name;
  
  public NamedEvent() {}
  
  public NamedEvent(QName paramQName)
  {
    name = paramQName;
  }
  
  public NamedEvent(String paramString1, String paramString2, String paramString3)
  {
    name = new QName(paramString2, paramString3, paramString1);
  }
  
  public String getPrefix()
  {
    return name.getPrefix();
  }
  
  public QName getName()
  {
    return name;
  }
  
  public void setName(QName paramQName)
  {
    name = paramQName;
  }
  
  public String nameAsString()
  {
    if ("".equals(name.getNamespaceURI())) {
      return name.getLocalPart();
    }
    if (name.getPrefix() != null) {
      return "['" + name.getNamespaceURI() + "']:" + getPrefix() + ":" + name.getLocalPart();
    }
    return "['" + name.getNamespaceURI() + "']:" + name.getLocalPart();
  }
  
  public String getNamespace()
  {
    return name.getNamespaceURI();
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(nameAsString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\NamedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */