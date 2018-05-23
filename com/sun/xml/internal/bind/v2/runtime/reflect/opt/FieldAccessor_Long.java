package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Long
  extends Accessor
{
  public FieldAccessor_Long()
  {
    super(Long.class);
  }
  
  public Object get(Object paramObject)
  {
    return Long.valueOf(f_long);
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    f_long = (paramObject2 == null ? Const.default_value_long : ((Long)paramObject2).longValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\FieldAccessor_Long.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */