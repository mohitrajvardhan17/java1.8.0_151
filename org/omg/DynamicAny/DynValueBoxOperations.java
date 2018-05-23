package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public abstract interface DynValueBoxOperations
  extends DynValueCommonOperations
{
  public abstract Any get_boxed_value()
    throws InvalidValue;
  
  public abstract void set_boxed_value(Any paramAny)
    throws TypeMismatch;
  
  public abstract DynAny get_boxed_value_as_dyn_any()
    throws InvalidValue;
  
  public abstract void set_boxed_value_as_dyn_any(DynAny paramDynAny)
    throws TypeMismatch;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynValueBoxOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */