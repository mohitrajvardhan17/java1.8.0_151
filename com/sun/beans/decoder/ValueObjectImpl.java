package com.sun.beans.decoder;

final class ValueObjectImpl
  implements ValueObject
{
  static final ValueObject NULL = new ValueObjectImpl(null);
  static final ValueObject VOID = new ValueObjectImpl();
  private Object value;
  private boolean isVoid;
  
  static ValueObject create(Object paramObject)
  {
    return paramObject != null ? new ValueObjectImpl(paramObject) : NULL;
  }
  
  private ValueObjectImpl()
  {
    isVoid = true;
  }
  
  private ValueObjectImpl(Object paramObject)
  {
    value = paramObject;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public boolean isVoid()
  {
    return isVoid;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\ValueObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */