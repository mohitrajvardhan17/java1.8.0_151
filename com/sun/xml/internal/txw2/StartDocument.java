package com.sun.xml.internal.txw2;

final class StartDocument
  extends Content
{
  StartDocument() {}
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onStartDocument();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\StartDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */