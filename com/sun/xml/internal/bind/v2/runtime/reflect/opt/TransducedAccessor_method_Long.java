package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Long
  extends DefaultTransducedAccessor
{
  public TransducedAccessor_method_Long() {}
  
  public String print(Object paramObject)
  {
    return DatatypeConverterImpl._printLong(((Bean)paramObject).get_long());
  }
  
  public void parse(Object paramObject, CharSequence paramCharSequence)
  {
    ((Bean)paramObject).set_long(DatatypeConverterImpl._parseLong(paramCharSequence));
  }
  
  public boolean hasValue(Object paramObject)
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\TransducedAccessor_method_Long.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */