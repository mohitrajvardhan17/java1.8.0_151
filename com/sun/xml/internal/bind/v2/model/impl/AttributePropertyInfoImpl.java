package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

class AttributePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  extends SingleTypePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  implements AttributePropertyInfo<TypeT, ClassDeclT>
{
  private final QName xmlName;
  private final boolean isRequired;
  
  AttributePropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlAttribute localXmlAttribute = (XmlAttribute)paramPropertySeed.readAnnotation(XmlAttribute.class);
    assert (localXmlAttribute != null);
    if (localXmlAttribute.required()) {
      isRequired = true;
    } else {
      isRequired = nav().isPrimitive(getIndividualType());
    }
    xmlName = calcXmlName(localXmlAttribute);
  }
  
  private QName calcXmlName(XmlAttribute paramXmlAttribute)
  {
    String str1 = paramXmlAttribute.namespace();
    String str2 = paramXmlAttribute.name();
    if (str2.equals("##default")) {
      str2 = NameConverter.standard.toVariableName(getName());
    }
    if (str1.equals("##default"))
    {
      XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, parent.getClazz(), this);
      if (localXmlSchema != null) {
        switch (localXmlSchema.attributeFormDefault())
        {
        case QUALIFIED: 
          str1 = parent.getTypeName().getNamespaceURI();
          if (str1.length() == 0) {
            str1 = parent.builder.defaultNsUri;
          }
          break;
        case UNQUALIFIED: 
        case UNSET: 
          str1 = "";
        }
      } else {
        str1 = "";
      }
    }
    return new QName(str1.intern(), str2.intern());
  }
  
  public boolean isRequired()
  {
    return isRequired;
  }
  
  public final QName getXmlName()
  {
    return xmlName;
  }
  
  public final PropertyKind kind()
  {
    return PropertyKind.ATTRIBUTE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\AttributePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */