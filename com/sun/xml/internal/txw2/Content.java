package com.sun.xml.internal.txw2;

abstract class Content
{
  private Content next;
  
  Content() {}
  
  final Content getNext()
  {
    return next;
  }
  
  final void setNext(Document paramDocument, Content paramContent)
  {
    assert (paramContent != null);
    assert (next == null) : ("next of " + this + " is already set to " + next);
    next = paramContent;
    paramDocument.run();
  }
  
  boolean isReadyToCommit()
  {
    return true;
  }
  
  abstract boolean concludesPendingStartTag();
  
  abstract void accept(ContentVisitor paramContentVisitor);
  
  public void written() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\Content.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */