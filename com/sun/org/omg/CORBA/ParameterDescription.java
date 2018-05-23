package com.sun.org.omg.CORBA;

import org.omg.CORBA.IDLType;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ParameterDescription
  implements IDLEntity
{
  public String name = null;
  public TypeCode type = null;
  public IDLType type_def = null;
  public ParameterMode mode = null;
  
  public ParameterDescription() {}
  
  public ParameterDescription(String paramString, TypeCode paramTypeCode, IDLType paramIDLType, ParameterMode paramParameterMode)
  {
    name = paramString;
    type = paramTypeCode;
    type_def = paramIDLType;
    mode = paramParameterMode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\ParameterDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */