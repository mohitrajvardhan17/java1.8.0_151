package org.omg.DynamicAny;

import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public abstract interface DynUnionOperations
  extends DynAnyOperations
{
  public abstract DynAny get_discriminator();
  
  public abstract void set_discriminator(DynAny paramDynAny)
    throws TypeMismatch;
  
  public abstract void set_to_default_member()
    throws TypeMismatch;
  
  public abstract void set_to_no_active_member()
    throws TypeMismatch;
  
  public abstract boolean has_no_active_member();
  
  public abstract TCKind discriminator_kind();
  
  public abstract TCKind member_kind()
    throws InvalidValue;
  
  public abstract DynAny member()
    throws InvalidValue;
  
  public abstract String member_name()
    throws InvalidValue;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynUnionOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */