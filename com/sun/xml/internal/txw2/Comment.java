package com.sun.xml.internal.txw2;

final class Comment
  extends Content
{
  private final StringBuilder buffer = new StringBuilder();
  
  public Comment(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject)
  {
    paramDocument.writeValue(paramObject, paramNamespaceResolver, buffer);
  }
  
  boolean concludesPendingStartTag()
  {
    return false;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onComment(buffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\Comment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */