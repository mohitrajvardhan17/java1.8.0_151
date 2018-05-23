package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class SetOverrideType
  implements IDLEntity
{
  public static final int _SET_OVERRIDE = 0;
  public static final int _ADD_OVERRIDE = 1;
  public static final SetOverrideType SET_OVERRIDE = new SetOverrideType(0);
  public static final SetOverrideType ADD_OVERRIDE = new SetOverrideType(1);
  private int _value;
  
  public int value()
  {
    return _value;
  }
  
  public static SetOverrideType from_int(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return SET_OVERRIDE;
    case 1: 
      return ADD_OVERRIDE;
    }
    throw new BAD_PARAM();
  }
  
  protected SetOverrideType(int paramInt)
  {
    _value = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\SetOverrideType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */