package com.sun.rowset.internal;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class XmlResolver
  implements EntityResolver
{
  public XmlResolver() {}
  
  public InputSource resolveEntity(String paramString1, String paramString2)
  {
    String str = paramString2.substring(paramString2.lastIndexOf("/"));
    if (paramString2.startsWith("http://java.sun.com/xml/ns/jdbc")) {
      return new InputSource(getClass().getResourceAsStream(str));
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\XmlResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */