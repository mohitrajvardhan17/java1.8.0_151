package com.sun.beans.decoder;

abstract class AccessorElementHandler
  extends ElementHandler
{
  private String name;
  private ValueObject value;
  
  AccessorElementHandler() {}
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("name")) {
      name = paramString2;
    } else {
      super.addAttribute(paramString1, paramString2);
    }
  }
  
  protected final void addArgument(Object paramObject)
  {
    if (value != null) {
      throw new IllegalStateException("Could not add argument to evaluated element");
    }
    setValue(name, paramObject);
    value = ValueObjectImpl.VOID;
  }
  
  protected final ValueObject getValueObject()
  {
    if (value == null) {
      value = ValueObjectImpl.create(getValue(name));
    }
    return value;
  }
  
  protected abstract Object getValue(String paramString);
  
  protected abstract void setValue(String paramString, Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\AccessorElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */