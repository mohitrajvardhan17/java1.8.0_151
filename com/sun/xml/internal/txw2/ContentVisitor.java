package com.sun.xml.internal.txw2;

abstract interface ContentVisitor
{
  public abstract void onStartDocument();
  
  public abstract void onEndDocument();
  
  public abstract void onEndTag();
  
  public abstract void onPcdata(StringBuilder paramStringBuilder);
  
  public abstract void onCdata(StringBuilder paramStringBuilder);
  
  public abstract void onStartTag(String paramString1, String paramString2, Attribute paramAttribute, NamespaceDecl paramNamespaceDecl);
  
  public abstract void onComment(StringBuilder paramStringBuilder);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\ContentVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */