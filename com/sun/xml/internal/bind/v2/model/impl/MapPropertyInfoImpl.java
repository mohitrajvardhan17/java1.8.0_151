package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

class MapPropertyInfoImpl<T, C, F, M>
  extends PropertyInfoImpl<T, C, F, M>
  implements MapPropertyInfo<T, C>
{
  private final QName xmlName;
  private boolean nil;
  private final T keyType;
  private final T valueType;
  private NonElement<T, C> keyTypeInfo;
  private NonElement<T, C> valueTypeInfo;
  
  public MapPropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlElementWrapper localXmlElementWrapper = (XmlElementWrapper)paramPropertySeed.readAnnotation(XmlElementWrapper.class);
    xmlName = calcXmlName(localXmlElementWrapper);
    nil = ((localXmlElementWrapper != null) && (localXmlElementWrapper.nillable()));
    Object localObject1 = getRawType();
    Object localObject2 = nav().getBaseClass(localObject1, nav().asDecl(Map.class));
    assert (localObject2 != null);
    if (nav().isParameterizedType(localObject2))
    {
      keyType = nav().getTypeArgument(localObject2, 0);
      valueType = nav().getTypeArgument(localObject2, 1);
    }
    else
    {
      keyType = (valueType = nav().ref(Object.class));
    }
  }
  
  public Collection<? extends TypeInfo<T, C>> ref()
  {
    return Arrays.asList(new NonElement[] { getKeyType(), getValueType() });
  }
  
  public final PropertyKind kind()
  {
    return PropertyKind.MAP;
  }
  
  public QName getXmlName()
  {
    return xmlName;
  }
  
  public boolean isCollectionNillable()
  {
    return nil;
  }
  
  public NonElement<T, C> getKeyType()
  {
    if (keyTypeInfo == null) {
      keyTypeInfo = getTarget(keyType);
    }
    return keyTypeInfo;
  }
  
  public NonElement<T, C> getValueType()
  {
    if (valueTypeInfo == null) {
      valueTypeInfo = getTarget(valueType);
    }
    return valueTypeInfo;
  }
  
  public NonElement<T, C> getTarget(T paramT)
  {
    assert (parent.builder != null) : "this method must be called during the build stage";
    return parent.builder.getTypeInfo(paramT, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\MapPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */