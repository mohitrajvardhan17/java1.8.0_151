package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Boolean
  extends Accessor
{
  public FieldAccessor_Boolean()
  {
    super(Boolean.class);
  }
  
  public Object get(Object paramObject)
  {
    return Boolean.valueOf(f_boolean);
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    f_boolean = (paramObject2 == null ? Const.default_value_boolean : ((Boolean)paramObject2).booleanValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\FieldAccessor_Boolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */