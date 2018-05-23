package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

abstract class LeafInfoImpl<TypeT, ClassDeclT>
  implements LeafInfo<TypeT, ClassDeclT>, Location
{
  private final TypeT type;
  private final QName typeName;
  
  protected LeafInfoImpl(TypeT paramTypeT, QName paramQName)
  {
    assert (paramTypeT != null);
    type = paramTypeT;
    typeName = paramQName;
  }
  
  public TypeT getType()
  {
    return (TypeT)type;
  }
  
  /**
   * @deprecated
   */
  public final boolean canBeReferencedByIDREF()
  {
    return false;
  }
  
  public QName getTypeName()
  {
    return typeName;
  }
  
  public Locatable getUpstream()
  {
    return null;
  }
  
  public Location getLocation()
  {
    return this;
  }
  
  public boolean isSimpleType()
  {
    return true;
  }
  
  public String toString()
  {
    return type.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\LeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */