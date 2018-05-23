package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Short
  extends Accessor
{
  public FieldAccessor_Short()
  {
    super(Short.class);
  }
  
  public Object get(Object paramObject)
  {
    return Short.valueOf(f_short);
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    f_short = (paramObject2 == null ? Const.default_value_short : ((Short)paramObject2).shortValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\FieldAccessor_Short.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */