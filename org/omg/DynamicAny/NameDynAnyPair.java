package org.omg.DynamicAny;

import org.omg.CORBA.portable.IDLEntity;

public final class NameDynAnyPair
  implements IDLEntity
{
  public String id = null;
  public DynAny value = null;
  
  public NameDynAnyPair() {}
  
  public NameDynAnyPair(String paramString, DynAny paramDynAny)
  {
    id = paramString;
    value = paramDynAny;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\NameDynAnyPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */