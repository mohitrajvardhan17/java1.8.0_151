package com.sun.org.apache.xml.internal.security.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Deprecated
public class IdResolver
{
  private IdResolver() {}
  
  public static void registerElementById(Element paramElement, Attr paramAttr)
  {
    paramElement.setIdAttributeNode(paramAttr, true);
  }
  
  public static Element getElementById(Document paramDocument, String paramString)
  {
    return paramDocument.getElementById(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\IdResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */