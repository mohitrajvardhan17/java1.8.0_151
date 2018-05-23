package org.omg.DynamicAny.DynAnyFactoryPackage;

import org.omg.CORBA.UserException;

public final class InconsistentTypeCode
  extends UserException
{
  public InconsistentTypeCode()
  {
    super(InconsistentTypeCodeHelper.id());
  }
  
  public InconsistentTypeCode(String paramString)
  {
    super(InconsistentTypeCodeHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynAnyFactoryPackage\InconsistentTypeCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */