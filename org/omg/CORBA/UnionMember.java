package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class UnionMember
  implements IDLEntity
{
  public String name;
  public Any label;
  public TypeCode type;
  public IDLType type_def;
  
  public UnionMember() {}
  
  public UnionMember(String paramString, Any paramAny, TypeCode paramTypeCode, IDLType paramIDLType)
  {
    name = paramString;
    label = paramAny;
    type = paramTypeCode;
    type_def = paramIDLType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UnionMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */