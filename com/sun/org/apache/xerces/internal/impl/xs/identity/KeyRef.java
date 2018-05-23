package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.XSIDCDefinition;

public class KeyRef
  extends IdentityConstraint
{
  protected UniqueOrKey fKey;
  
  public KeyRef(String paramString1, String paramString2, String paramString3, UniqueOrKey paramUniqueOrKey)
  {
    super(paramString1, paramString2, paramString3);
    fKey = paramUniqueOrKey;
    type = 2;
  }
  
  public UniqueOrKey getKey()
  {
    return fKey;
  }
  
  public XSIDCDefinition getRefKey()
  {
    return fKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\KeyRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */