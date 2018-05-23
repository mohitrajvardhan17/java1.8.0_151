package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;

class FieldPropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
  implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
{
  protected final FieldT field;
  private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;
  
  FieldPropertySeed(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, FieldT paramFieldT)
  {
    parent = paramClassInfoImpl;
    field = paramFieldT;
  }
  
  public <A extends Annotation> A readAnnotation(Class<A> paramClass)
  {
    return parent.reader().getFieldAnnotation(paramClass, field, this);
  }
  
  public boolean hasAnnotation(Class<? extends Annotation> paramClass)
  {
    return parent.reader().hasFieldAnnotation(paramClass, field);
  }
  
  public String getName()
  {
    return parent.nav().getFieldName(field);
  }
  
  public TypeT getRawType()
  {
    return (TypeT)parent.nav().getFieldType(field);
  }
  
  public Locatable getUpstream()
  {
    return parent;
  }
  
  public Location getLocation()
  {
    return parent.nav().getFieldLocation(field);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\FieldPropertySeed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */