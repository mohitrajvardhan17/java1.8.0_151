package com.sun.beans.decoder;

class NullElementHandler
  extends ElementHandler
  implements ValueObject
{
  NullElementHandler() {}
  
  protected final ValueObject getValueObject()
  {
    return this;
  }
  
  public Object getValue()
  {
    return null;
  }
  
  public final boolean isVoid()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\NullElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */