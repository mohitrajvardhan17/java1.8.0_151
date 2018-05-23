package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

abstract class ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  extends PropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
{
  private final QName xmlName;
  private final boolean wrapperNillable;
  private final boolean wrapperRequired;
  
  public ERPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlElementWrapper localXmlElementWrapper = (XmlElementWrapper)seed.readAnnotation(XmlElementWrapper.class);
    boolean bool1 = false;
    boolean bool2 = false;
    if (!isCollection())
    {
      xmlName = null;
      if (localXmlElementWrapper != null) {
        builder.reportError(new IllegalAnnotationException(Messages.XML_ELEMENT_WRAPPER_ON_NON_COLLECTION.format(new Object[] { nav().getClassName(parent.getClazz()) + '.' + seed.getName() }), localXmlElementWrapper));
      }
    }
    else if (localXmlElementWrapper != null)
    {
      xmlName = calcXmlName(localXmlElementWrapper);
      bool1 = localXmlElementWrapper.nillable();
      bool2 = localXmlElementWrapper.required();
    }
    else
    {
      xmlName = null;
    }
    wrapperNillable = bool1;
    wrapperRequired = bool2;
  }
  
  public final QName getXmlName()
  {
    return xmlName;
  }
  
  public final boolean isCollectionNillable()
  {
    return wrapperNillable;
  }
  
  public final boolean isCollectionRequired()
  {
    return wrapperRequired;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ERPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */