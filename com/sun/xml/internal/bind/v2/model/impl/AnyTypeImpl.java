package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

class AnyTypeImpl<T, C>
  implements NonElement<T, C>
{
  private final T type;
  private final Navigator<T, C, ?, ?> nav;
  
  public AnyTypeImpl(Navigator<T, C, ?, ?> paramNavigator)
  {
    type = paramNavigator.ref(Object.class);
    nav = paramNavigator;
  }
  
  public QName getTypeName()
  {
    return ANYTYPE_NAME;
  }
  
  public T getType()
  {
    return (T)type;
  }
  
  public Locatable getUpstream()
  {
    return null;
  }
  
  public boolean isSimpleType()
  {
    return false;
  }
  
  public Location getLocation()
  {
    return nav.getClassLocation(nav.asDecl(Object.class));
  }
  
  /**
   * @deprecated
   */
  public final boolean canBeReferencedByIDREF()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\AnyTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */