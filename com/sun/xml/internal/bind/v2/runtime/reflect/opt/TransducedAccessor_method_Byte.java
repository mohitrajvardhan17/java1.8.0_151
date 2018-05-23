package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Byte
  extends DefaultTransducedAccessor
{
  public TransducedAccessor_method_Byte() {}
  
  public String print(Object paramObject)
  {
    return DatatypeConverterImpl._printByte(((Bean)paramObject).get_byte());
  }
  
  public void parse(Object paramObject, CharSequence paramCharSequence)
  {
    ((Bean)paramObject).set_byte(DatatypeConverterImpl._parseByte(paramCharSequence));
  }
  
  public boolean hasValue(Object paramObject)
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\TransducedAccessor_method_Byte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */