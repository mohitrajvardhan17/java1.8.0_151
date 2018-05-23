package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Character
  extends Accessor
{
  public FieldAccessor_Character()
  {
    super(Character.class);
  }
  
  public Object get(Object paramObject)
  {
    return Character.valueOf(f_char);
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    f_char = (paramObject2 == null ? Const.default_value_char : ((Character)paramObject2).charValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\FieldAccessor_Character.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */