package com.sun.xml.internal.bind.v2.model.core;

public abstract interface EnumConstant<T, C>
{
  public abstract EnumLeafInfo<T, C> getEnclosingClass();
  
  public abstract String getLexicalValue();
  
  public abstract String getName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\EnumConstant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */