package com.sun.xml.internal.txw2;

final class Pcdata
  extends Text
{
  Pcdata(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject)
  {
    super(paramDocument, paramNamespaceResolver, paramObject);
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onPcdata(buffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\Pcdata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */