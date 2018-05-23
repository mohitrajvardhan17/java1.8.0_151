package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;

class EnumLeafInfoImpl<T, C, F, M>
  extends TypeInfoImpl<T, C, F, M>
  implements EnumLeafInfo<T, C>, Element<T, C>, Iterable<EnumConstantImpl<T, C, F, M>>
{
  final C clazz;
  NonElement<T, C> baseType;
  private final T type;
  private final QName typeName;
  private EnumConstantImpl<T, C, F, M> firstConstant;
  private QName elementName;
  protected boolean tokenStringType;
  
  public EnumLeafInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC, T paramT)
  {
    super(paramModelBuilder, paramLocatable);
    clazz = paramC;
    type = paramT;
    elementName = parseElementName(paramC);
    typeName = parseTypeName(paramC);
    XmlEnum localXmlEnum = (XmlEnum)reader.getClassAnnotation(XmlEnum.class, paramC, this);
    if (localXmlEnum != null)
    {
      Object localObject = reader.getClassValue(localXmlEnum, "value");
      baseType = paramModelBuilder.getTypeInfo(localObject, this);
    }
    else
    {
      baseType = paramModelBuilder.getTypeInfo(nav.ref(String.class), this);
    }
  }
  
  protected void calcConstants()
  {
    EnumConstantImpl localEnumConstantImpl = null;
    Collection localCollection = nav().getDeclaredFields(clazz);
    Object localObject1 = localCollection.iterator();
    XmlSchemaType localXmlSchemaType;
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = ((Iterator)localObject1).next();
      if (nav().isSameType(nav().getFieldType(localObject2), nav().ref(String.class)))
      {
        localXmlSchemaType = (XmlSchemaType)builder.reader.getFieldAnnotation(XmlSchemaType.class, localObject2, this);
        if ((localXmlSchemaType != null) && ("token".equals(localXmlSchemaType.name())))
        {
          tokenStringType = true;
          break;
        }
      }
    }
    localObject1 = nav().getEnumConstants(clazz);
    for (int i = localObject1.length - 1; i >= 0; i--)
    {
      localXmlSchemaType = localObject1[i];
      String str1 = nav().getFieldName(localXmlSchemaType);
      XmlEnumValue localXmlEnumValue = (XmlEnumValue)builder.reader.getFieldAnnotation(XmlEnumValue.class, localXmlSchemaType, this);
      String str2;
      if (localXmlEnumValue == null) {
        str2 = str1;
      } else {
        str2 = localXmlEnumValue.value();
      }
      localEnumConstantImpl = createEnumConstant(str1, str2, localXmlSchemaType, localEnumConstantImpl);
    }
    firstConstant = localEnumConstantImpl;
  }
  
  protected EnumConstantImpl<T, C, F, M> createEnumConstant(String paramString1, String paramString2, F paramF, EnumConstantImpl<T, C, F, M> paramEnumConstantImpl)
  {
    return new EnumConstantImpl(this, paramString1, paramString2, paramEnumConstantImpl);
  }
  
  public T getType()
  {
    return (T)type;
  }
  
  public boolean isToken()
  {
    return tokenStringType;
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
  
  public C getClazz()
  {
    return (C)clazz;
  }
  
  public NonElement<T, C> getBaseType()
  {
    return baseType;
  }
  
  public boolean isSimpleType()
  {
    return true;
  }
  
  public Location getLocation()
  {
    return nav().getClassLocation(clazz);
  }
  
  public Iterable<? extends EnumConstantImpl<T, C, F, M>> getConstants()
  {
    if (firstConstant == null) {
      calcConstants();
    }
    return this;
  }
  
  public void link()
  {
    getConstants();
    super.link();
  }
  
  /**
   * @deprecated
   */
  public Element<T, C> getSubstitutionHead()
  {
    return null;
  }
  
  public QName getElementName()
  {
    return elementName;
  }
  
  public boolean isElement()
  {
    return elementName != null;
  }
  
  public Element<T, C> asElement()
  {
    if (isElement()) {
      return this;
    }
    return null;
  }
  
  /**
   * @deprecated
   */
  public ClassInfo<T, C> getScope()
  {
    return null;
  }
  
  public Iterator<EnumConstantImpl<T, C, F, M>> iterator()
  {
    new Iterator()
    {
      private EnumConstantImpl<T, C, F, M> next = firstConstant;
      
      public boolean hasNext()
      {
        return next != null;
      }
      
      public EnumConstantImpl<T, C, F, M> next()
      {
        EnumConstantImpl localEnumConstantImpl = next;
        next = next.next;
        return localEnumConstantImpl;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\EnumLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */