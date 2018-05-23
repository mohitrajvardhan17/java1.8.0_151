package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface BuiltinLeafInfo<T, C>
  extends LeafInfo<T, C>
{
  public abstract QName getTypeName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\BuiltinLeafInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */