package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Ref
  extends Accessor
{
  public MethodAccessor_Ref()
  {
    super(Ref.class);
  }
  
  public Object get(Object paramObject)
  {
    return ((Bean)paramObject).get_ref();
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    ((Bean)paramObject1).set_ref((Ref)paramObject2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\MethodAccessor_Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */