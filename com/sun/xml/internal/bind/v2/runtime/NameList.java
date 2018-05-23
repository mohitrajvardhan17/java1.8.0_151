package com.sun.xml.internal.bind.v2.runtime;

public final class NameList
{
  public final String[] namespaceURIs;
  public final boolean[] nsUriCannotBeDefaulted;
  public final String[] localNames;
  public final int numberOfElementNames;
  public final int numberOfAttributeNames;
  
  public NameList(String[] paramArrayOfString1, boolean[] paramArrayOfBoolean, String[] paramArrayOfString2, int paramInt1, int paramInt2)
  {
    namespaceURIs = paramArrayOfString1;
    nsUriCannotBeDefaulted = paramArrayOfBoolean;
    localNames = paramArrayOfString2;
    numberOfElementNames = paramInt1;
    numberOfAttributeNames = paramInt2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\NameList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */