package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class StructMember
  implements IDLEntity
{
  public String name;
  public TypeCode type;
  public IDLType type_def;
  
  public StructMember() {}
  
  public StructMember(String paramString, TypeCode paramTypeCode, IDLType paramIDLType)
  {
    name = paramString;
    type = paramTypeCode;
    type_def = paramIDLType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\StructMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */