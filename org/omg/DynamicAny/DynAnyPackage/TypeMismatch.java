package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class TypeMismatch
  extends UserException
{
  public TypeMismatch()
  {
    super(TypeMismatchHelper.id());
  }
  
  public TypeMismatch(String paramString)
  {
    super(TypeMismatchHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynAnyPackage\TypeMismatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */