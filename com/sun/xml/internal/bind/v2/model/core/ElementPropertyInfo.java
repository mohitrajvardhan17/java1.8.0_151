package com.sun.xml.internal.bind.v2.model.core;

import java.util.List;
import javax.xml.namespace.QName;

public abstract interface ElementPropertyInfo<T, C>
  extends PropertyInfo<T, C>
{
  public abstract List<? extends TypeRef<T, C>> getTypes();
  
  public abstract QName getXmlName();
  
  public abstract boolean isCollectionRequired();
  
  public abstract boolean isCollectionNillable();
  
  public abstract boolean isValueList();
  
  public abstract boolean isRequired();
  
  public abstract Adapter<T, C> getAdapter();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\ElementPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */