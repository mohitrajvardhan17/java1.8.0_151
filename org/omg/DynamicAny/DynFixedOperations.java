package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public abstract interface DynFixedOperations
  extends DynAnyOperations
{
  public abstract String get_value();
  
  public abstract boolean set_value(String paramString)
    throws TypeMismatch, InvalidValue;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynFixedOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */