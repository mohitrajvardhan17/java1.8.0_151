package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Double
  extends Accessor
{
  public MethodAccessor_Double()
  {
    super(Double.class);
  }
  
  public Object get(Object paramObject)
  {
    return Double.valueOf(((Bean)paramObject).get_double());
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    ((Bean)paramObject1).set_double(paramObject2 == null ? Const.default_value_double : ((Double)paramObject2).doubleValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\MethodAccessor_Double.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */