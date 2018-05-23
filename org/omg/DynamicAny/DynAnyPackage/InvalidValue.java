package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class InvalidValue
  extends UserException
{
  public InvalidValue()
  {
    super(InvalidValueHelper.id());
  }
  
  public InvalidValue(String paramString)
  {
    super(InvalidValueHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynAnyPackage\InvalidValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */