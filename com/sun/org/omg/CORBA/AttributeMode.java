package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class AttributeMode
  implements IDLEntity
{
  private int __value;
  private static int __size = 2;
  private static AttributeMode[] __array = new AttributeMode[__size];
  public static final int _ATTR_NORMAL = 0;
  public static final AttributeMode ATTR_NORMAL = new AttributeMode(0);
  public static final int _ATTR_READONLY = 1;
  public static final AttributeMode ATTR_READONLY = new AttributeMode(1);
  
  public int value()
  {
    return __value;
  }
  
  public static AttributeMode from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected AttributeMode(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\AttributeMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */