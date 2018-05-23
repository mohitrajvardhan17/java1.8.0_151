package com.sun.org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

@Deprecated
public abstract interface ValueHelper
  extends BoxedValueHelper
{
  public abstract Class get_class();
  
  public abstract String[] get_truncatable_base_ids();
  
  public abstract TypeCode get_type();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\portable\ValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */