package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public abstract interface DynSequenceOperations
  extends DynAnyOperations
{
  public abstract int get_length();
  
  public abstract void set_length(int paramInt)
    throws InvalidValue;
  
  public abstract Any[] get_elements();
  
  public abstract void set_elements(Any[] paramArrayOfAny)
    throws TypeMismatch, InvalidValue;
  
  public abstract DynAny[] get_elements_as_dyn_any();
  
  public abstract void set_elements_as_dyn_any(DynAny[] paramArrayOfDynAny)
    throws TypeMismatch, InvalidValue;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynSequenceOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */