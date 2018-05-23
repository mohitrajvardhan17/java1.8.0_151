package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

public abstract class TagName
{
  public String uri;
  public String local;
  public Attributes atts;
  
  public TagName() {}
  
  public final boolean matches(String paramString1, String paramString2)
  {
    return (uri == paramString1) && (local == paramString2);
  }
  
  public final boolean matches(Name paramName)
  {
    return (local == localName) && (uri == nsUri);
  }
  
  public String toString()
  {
    return '{' + uri + '}' + local;
  }
  
  public abstract String getQname();
  
  public String getPrefix()
  {
    String str = getQname();
    int i = str.indexOf(':');
    if (i < 0) {
      return "";
    }
    return str.substring(0, i);
  }
  
  public QName createQName()
  {
    return new QName(uri, local, getPrefix());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\TagName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */