package sun.reflect;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class UnsafeStaticFieldAccessorImpl
  extends UnsafeFieldAccessorImpl
{
  protected final Object base;
  
  UnsafeStaticFieldAccessorImpl(Field paramField)
  {
    super(paramField);
    base = unsafe.staticFieldBase(paramField);
  }
  
  static
  {
    Reflection.registerFieldsToFilter(UnsafeStaticFieldAccessorImpl.class, new String[] { "base" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\UnsafeStaticFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */