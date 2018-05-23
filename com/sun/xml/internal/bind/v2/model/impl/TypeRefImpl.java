package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import javax.xml.namespace.QName;

class TypeRefImpl<TypeT, ClassDeclT>
  implements TypeRef<TypeT, ClassDeclT>
{
  private final QName elementName;
  private final TypeT type;
  protected final ElementPropertyInfoImpl<TypeT, ClassDeclT, ?, ?> owner;
  private NonElement<TypeT, ClassDeclT> ref;
  private final boolean isNillable;
  private String defaultValue;
  
  public TypeRefImpl(ElementPropertyInfoImpl<TypeT, ClassDeclT, ?, ?> paramElementPropertyInfoImpl, QName paramQName, TypeT paramTypeT, boolean paramBoolean, String paramString)
  {
    owner = paramElementPropertyInfoImpl;
    elementName = paramQName;
    type = paramTypeT;
    isNillable = paramBoolean;
    defaultValue = paramString;
    assert (paramElementPropertyInfoImpl != null);
    assert (paramQName != null);
    assert (paramTypeT != null);
  }
  
  public NonElement<TypeT, ClassDeclT> getTarget()
  {
    if (ref == null) {
      calcRef();
    }
    return ref;
  }
  
  public QName getTagName()
  {
    return elementName;
  }
  
  public boolean isNillable()
  {
    return isNillable;
  }
  
  public String getDefaultValue()
  {
    return defaultValue;
  }
  
  protected void link()
  {
    calcRef();
  }
  
  private void calcRef()
  {
    ref = owner.parent.builder.getTypeInfo(type, owner);
    assert (ref != null);
  }
  
  public PropertyInfo<TypeT, ClassDeclT> getSource()
  {
    return owner;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\TypeRefImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */