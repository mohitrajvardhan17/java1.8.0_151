package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

abstract class TypeInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  implements TypeInfo<TypeT, ClassDeclT>, Locatable
{
  private final Locatable upstream;
  protected final TypeInfoSetImpl<TypeT, ClassDeclT, FieldT, MethodT> owner;
  protected ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> builder;
  
  protected TypeInfoImpl(ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> paramModelBuilder, Locatable paramLocatable)
  {
    builder = paramModelBuilder;
    owner = typeInfoSet;
    upstream = paramLocatable;
  }
  
  public Locatable getUpstream()
  {
    return upstream;
  }
  
  void link()
  {
    builder = null;
  }
  
  protected final Navigator<TypeT, ClassDeclT, FieldT, MethodT> nav()
  {
    return owner.nav;
  }
  
  protected final AnnotationReader<TypeT, ClassDeclT, FieldT, MethodT> reader()
  {
    return owner.reader;
  }
  
  protected final QName parseElementName(ClassDeclT paramClassDeclT)
  {
    XmlRootElement localXmlRootElement = (XmlRootElement)reader().getClassAnnotation(XmlRootElement.class, paramClassDeclT, this);
    if (localXmlRootElement == null) {
      return null;
    }
    String str1 = localXmlRootElement.name();
    if (str1.equals("##default")) {
      str1 = NameConverter.standard.toVariableName(nav().getClassShortName(paramClassDeclT));
    }
    String str2 = localXmlRootElement.namespace();
    if (str2.equals("##default"))
    {
      XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, paramClassDeclT, this);
      if (localXmlSchema != null) {
        str2 = localXmlSchema.namespace();
      } else {
        str2 = builder.defaultNsUri;
      }
    }
    return new QName(str2.intern(), str1.intern());
  }
  
  protected final QName parseTypeName(ClassDeclT paramClassDeclT)
  {
    return parseTypeName(paramClassDeclT, (XmlType)reader().getClassAnnotation(XmlType.class, paramClassDeclT, this));
  }
  
  protected final QName parseTypeName(ClassDeclT paramClassDeclT, XmlType paramXmlType)
  {
    String str1 = "##default";
    String str2 = "##default";
    if (paramXmlType != null)
    {
      str1 = paramXmlType.namespace();
      str2 = paramXmlType.name();
    }
    if (str2.length() == 0) {
      return null;
    }
    if (str2.equals("##default")) {
      str2 = NameConverter.standard.toVariableName(nav().getClassShortName(paramClassDeclT));
    }
    if (str1.equals("##default"))
    {
      XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, paramClassDeclT, this);
      if (localXmlSchema != null) {
        str1 = localXmlSchema.namespace();
      } else {
        str1 = builder.defaultNsUri;
      }
    }
    return new QName(str1.intern(), str2.intern());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\TypeInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */