package com.sun.xml.internal.txw2;

final class EndDocument
  extends Content
{
  EndDocument() {}
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onEndDocument();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\EndDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */