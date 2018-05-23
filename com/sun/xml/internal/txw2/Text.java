package com.sun.xml.internal.txw2;

abstract class Text
  extends Content
{
  protected final StringBuilder buffer = new StringBuilder();
  
  protected Text(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject)
  {
    paramDocument.writeValue(paramObject, paramNamespaceResolver, buffer);
  }
  
  boolean concludesPendingStartTag()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\Text.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */