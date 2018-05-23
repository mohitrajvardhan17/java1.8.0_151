package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ValueMember
  implements IDLEntity
{
  public String name;
  public String id;
  public String defined_in;
  public String version;
  public TypeCode type;
  public IDLType type_def;
  public short access;
  
  public ValueMember() {}
  
  public ValueMember(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode, IDLType paramIDLType, short paramShort)
  {
    name = paramString1;
    id = paramString2;
    defined_in = paramString3;
    version = paramString4;
    type = paramTypeCode;
    type_def = paramIDLType;
    access = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ValueMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */