package com.sun.xml.internal.bind.v2.model.core;

public enum WildcardMode
{
  STRICT(false, true),  SKIP(true, false),  LAX(true, true);
  
  public final boolean allowDom;
  public final boolean allowTypedObject;
  
  private WildcardMode(boolean paramBoolean1, boolean paramBoolean2)
  {
    allowDom = paramBoolean1;
    allowTypedObject = paramBoolean2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\WildcardMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */