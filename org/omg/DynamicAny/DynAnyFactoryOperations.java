package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public abstract interface DynAnyFactoryOperations
{
  public abstract DynAny create_dyn_any(Any paramAny)
    throws InconsistentTypeCode;
  
  public abstract DynAny create_dyn_any_from_type_code(TypeCode paramTypeCode)
    throws InconsistentTypeCode;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynAnyFactoryOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */