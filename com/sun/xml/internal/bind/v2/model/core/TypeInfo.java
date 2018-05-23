package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

public abstract interface TypeInfo<T, C>
  extends Locatable
{
  public abstract T getType();
  
  public abstract boolean canBeReferencedByIDREF();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */