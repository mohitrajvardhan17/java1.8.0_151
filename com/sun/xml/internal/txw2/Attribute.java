package com.sun.xml.internal.txw2;

final class Attribute
{
  final String nsUri;
  final String localName;
  Attribute next;
  final StringBuilder value = new StringBuilder();
  
  Attribute(String paramString1, String paramString2)
  {
    assert ((paramString1 != null) && (paramString2 != null));
    nsUri = paramString1;
    localName = paramString2;
  }
  
  boolean hasName(String paramString1, String paramString2)
  {
    return (localName.equals(paramString2)) && (nsUri.equals(paramString1));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */