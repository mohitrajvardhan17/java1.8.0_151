package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Byte
  extends Accessor
{
  public MethodAccessor_Byte()
  {
    super(Byte.class);
  }
  
  public Object get(Object paramObject)
  {
    return Byte.valueOf(((Bean)paramObject).get_byte());
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    ((Bean)paramObject1).set_byte(paramObject2 == null ? Const.default_value_byte : ((Byte)paramObject2).byteValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\MethodAccessor_Byte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */