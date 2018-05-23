package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class AttributeDescription
  implements IDLEntity
{
  public String name = null;
  public String id = null;
  public String defined_in = null;
  public String version = null;
  public TypeCode type = null;
  public AttributeMode mode = null;
  
  public AttributeDescription() {}
  
  public AttributeDescription(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode, AttributeMode paramAttributeMode)
  {
    name = paramString1;
    id = paramString2;
    defined_in = paramString3;
    version = paramString4;
    type = paramTypeCode;
    mode = paramAttributeMode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\AttributeDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */