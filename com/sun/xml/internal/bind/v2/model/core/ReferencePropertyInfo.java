package com.sun.xml.internal.bind.v2.model.core;

import java.util.Collection;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract interface ReferencePropertyInfo<T, C>
  extends PropertyInfo<T, C>
{
  public abstract Set<? extends Element<T, C>> getElements();
  
  public abstract Collection<? extends TypeInfo<T, C>> ref();
  
  public abstract QName getXmlName();
  
  public abstract boolean isCollectionNillable();
  
  public abstract boolean isCollectionRequired();
  
  public abstract boolean isMixed();
  
  public abstract WildcardMode getWildcard();
  
  public abstract C getDOMHandler();
  
  public abstract boolean isRequired();
  
  public abstract Adapter<T, C> getAdapter();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\ReferencePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */