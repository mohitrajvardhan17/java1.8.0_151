package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

public class EndElementEvent
  extends DummyEvent
  implements EndElement
{
  List fNamespaces = null;
  QName fQName;
  
  public EndElementEvent()
  {
    init();
  }
  
  protected void init()
  {
    setEventType(2);
    fNamespaces = new ArrayList();
  }
  
  public EndElementEvent(String paramString1, String paramString2, String paramString3)
  {
    this(new QName(paramString2, paramString3, paramString1));
  }
  
  public EndElementEvent(QName paramQName)
  {
    fQName = paramQName;
    init();
  }
  
  public QName getName()
  {
    return fQName;
  }
  
  public void setName(QName paramQName)
  {
    fQName = paramQName;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("</");
    String str = fQName.getPrefix();
    if ((str != null) && (str.length() > 0))
    {
      paramWriter.write(str);
      paramWriter.write(58);
    }
    paramWriter.write(fQName.getLocalPart());
    paramWriter.write(62);
  }
  
  public Iterator getNamespaces()
  {
    if (fNamespaces != null) {
      fNamespaces.iterator();
    }
    return new ReadOnlyIterator();
  }
  
  void addNamespace(Namespace paramNamespace)
  {
    if (paramNamespace != null) {
      fNamespaces.add(paramNamespace);
    }
  }
  
  public String toString()
  {
    String str = "</" + nameAsString();
    str = str + ">";
    return str;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\EndElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */