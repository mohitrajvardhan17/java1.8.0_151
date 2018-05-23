package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Float
  extends Accessor
{
  public MethodAccessor_Float()
  {
    super(Float.class);
  }
  
  public Object get(Object paramObject)
  {
    return Float.valueOf(((Bean)paramObject).get_float());
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    ((Bean)paramObject1).set_float(paramObject2 == null ? Const.default_value_float : ((Float)paramObject2).floatValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\MethodAccessor_Float.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */