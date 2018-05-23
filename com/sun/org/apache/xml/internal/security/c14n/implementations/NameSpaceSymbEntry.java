package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.w3c.dom.Attr;

class NameSpaceSymbEntry
  implements Cloneable
{
  String prefix;
  String uri;
  String lastrendered = null;
  boolean rendered = false;
  Attr n;
  
  NameSpaceSymbEntry(String paramString1, Attr paramAttr, boolean paramBoolean, String paramString2)
  {
    uri = paramString1;
    rendered = paramBoolean;
    n = paramAttr;
    prefix = paramString2;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\NameSpaceSymbEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */