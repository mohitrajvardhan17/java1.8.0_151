package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedStaticFieldAccessorImpl
  extends UnsafeStaticFieldAccessorImpl
{
  protected final boolean isReadOnly;
  
  UnsafeQualifiedStaticFieldAccessorImpl(Field paramField, boolean paramBoolean)
  {
    super(paramField);
    isReadOnly = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\UnsafeQualifiedStaticFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */