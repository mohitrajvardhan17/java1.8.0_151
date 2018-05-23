package com.sun.xml.internal.bind.v2.model.core;

public abstract interface EnumLeafInfo<T, C>
  extends LeafInfo<T, C>
{
  public abstract C getClazz();
  
  public abstract NonElement<T, C> getBaseType();
  
  public abstract Iterable<? extends EnumConstant> getConstants();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\EnumLeafInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */