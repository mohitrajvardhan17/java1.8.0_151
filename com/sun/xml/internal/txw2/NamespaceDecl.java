package com.sun.xml.internal.txw2;

final class NamespaceDecl
{
  final String uri;
  boolean requirePrefix;
  final String dummyPrefix;
  final char uniqueId;
  String prefix;
  boolean declared;
  NamespaceDecl next;
  
  NamespaceDecl(char paramChar, String paramString1, String paramString2, boolean paramBoolean)
  {
    dummyPrefix = (2 + '\000' + paramChar);
    uri = paramString1;
    prefix = paramString2;
    requirePrefix = paramBoolean;
    uniqueId = paramChar;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\NamespaceDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */