package org.omg.DynamicAny;

import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public abstract interface DynValueOperations
  extends DynValueCommonOperations
{
  public abstract String current_member_name()
    throws TypeMismatch, InvalidValue;
  
  public abstract TCKind current_member_kind()
    throws TypeMismatch, InvalidValue;
  
  public abstract NameValuePair[] get_members()
    throws InvalidValue;
  
  public abstract void set_members(NameValuePair[] paramArrayOfNameValuePair)
    throws TypeMismatch, InvalidValue;
  
  public abstract NameDynAnyPair[] get_members_as_dyn_any()
    throws InvalidValue;
  
  public abstract void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair)
    throws TypeMismatch, InvalidValue;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynValueOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */