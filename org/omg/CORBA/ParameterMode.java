package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class ParameterMode
  implements IDLEntity
{
  private int __value;
  private static int __size = 3;
  private static ParameterMode[] __array = new ParameterMode[__size];
  public static final int _PARAM_IN = 0;
  public static final ParameterMode PARAM_IN = new ParameterMode(0);
  public static final int _PARAM_OUT = 1;
  public static final ParameterMode PARAM_OUT = new ParameterMode(1);
  public static final int _PARAM_INOUT = 2;
  public static final ParameterMode PARAM_INOUT = new ParameterMode(2);
  
  public int value()
  {
    return __value;
  }
  
  public static ParameterMode from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected ParameterMode(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ParameterMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */