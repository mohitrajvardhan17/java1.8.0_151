package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

public class ArrayInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  extends TypeInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  implements ArrayInfo<TypeT, ClassDeclT>, Location
{
  private final NonElement<TypeT, ClassDeclT> itemType;
  private final QName typeName;
  private final TypeT arrayType;
  
  public ArrayInfoImpl(ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> paramModelBuilder, Locatable paramLocatable, TypeT paramTypeT)
  {
    super(paramModelBuilder, paramLocatable);
    arrayType = paramTypeT;
    Object localObject = nav().getComponentType(paramTypeT);
    itemType = paramModelBuilder.getTypeInfo(localObject, this);
    QName localQName = itemType.getTypeName();
    if (localQName == null)
    {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.ANONYMOUS_ARRAY_ITEM.format(new Object[] { nav().getTypeName(localObject) }), this));
      localQName = new QName("#dummy");
    }
    typeName = ArrayInfoUtil.calcArrayTypeName(localQName);
  }
  
  public NonElement<TypeT, ClassDeclT> getItemType()
  {
    return itemType;
  }
  
  public QName getTypeName()
  {
    return typeName;
  }
  
  public boolean isSimpleType()
  {
    return false;
  }
  
  public TypeT getType()
  {
    return (TypeT)arrayType;
  }
  
  /**
   * @deprecated
   */
  public final boolean canBeReferencedByIDREF()
  {
    return false;
  }
  
  public Location getLocation()
  {
    return this;
  }
  
  public String toString()
  {
    return nav().getTypeName(arrayType);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ArrayInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */