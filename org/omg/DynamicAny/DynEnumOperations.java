package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public abstract interface DynEnumOperations
  extends DynAnyOperations
{
  public abstract String get_as_string();
  
  public abstract void set_as_string(String paramString)
    throws InvalidValue;
  
  public abstract int get_as_ulong();
  
  public abstract void set_as_ulong(int paramInt)
    throws InvalidValue;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynEnumOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */