package com.sun.xml.internal.bind.v2.model.core;

public enum PropertyKind
{
  VALUE(true, false, Integer.MAX_VALUE),  ATTRIBUTE(false, false, Integer.MAX_VALUE),  ELEMENT(true, true, 0),  REFERENCE(false, true, 1),  MAP(false, true, 2);
  
  public final boolean canHaveXmlMimeType;
  public final boolean isOrdered;
  public final int propertyIndex;
  
  private PropertyKind(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    canHaveXmlMimeType = paramBoolean1;
    isOrdered = paramBoolean2;
    propertyIndex = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\PropertyKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */