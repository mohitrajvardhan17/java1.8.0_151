package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.QName;

public final class Name
  implements Comparable<Name>
{
  public final String nsUri;
  public final String localName;
  public final short nsUriIndex;
  public final short localNameIndex;
  public final short qNameIndex;
  public final boolean isAttribute;
  
  Name(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2, boolean paramBoolean)
  {
    qNameIndex = ((short)paramInt1);
    nsUri = paramString1;
    localName = paramString2;
    nsUriIndex = ((short)paramInt2);
    localNameIndex = ((short)paramInt3);
    isAttribute = paramBoolean;
  }
  
  public String toString()
  {
    return '{' + nsUri + '}' + localName;
  }
  
  public QName toQName()
  {
    return new QName(nsUri, localName);
  }
  
  public boolean equals(String paramString1, String paramString2)
  {
    return (paramString2.equals(localName)) && (paramString1.equals(nsUri));
  }
  
  public int compareTo(Name paramName)
  {
    int i = nsUri.compareTo(nsUri);
    if (i != 0) {
      return i;
    }
    return localName.compareTo(localName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */