package com.sun.xml.internal.txw2;

final class EndTag
  extends Content
{
  EndTag() {}
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onEndTag();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\EndTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */