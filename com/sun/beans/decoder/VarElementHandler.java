package com.sun.beans.decoder;

final class VarElementHandler
  extends ElementHandler
{
  private ValueObject value;
  
  VarElementHandler() {}
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("idref")) {
      value = ValueObjectImpl.create(getVariable(paramString2));
    } else {
      super.addAttribute(paramString1, paramString2);
    }
  }
  
  protected ValueObject getValueObject()
  {
    if (value == null) {
      throw new IllegalArgumentException("Variable name is not set");
    }
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\VarElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */