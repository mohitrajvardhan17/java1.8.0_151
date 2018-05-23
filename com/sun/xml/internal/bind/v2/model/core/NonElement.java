package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface NonElement<T, C>
  extends TypeInfo<T, C>
{
  public static final QName ANYTYPE_NAME = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
  
  public abstract QName getTypeName();
  
  public abstract boolean isSimpleType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\NonElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */