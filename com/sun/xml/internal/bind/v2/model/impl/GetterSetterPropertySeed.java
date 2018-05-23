package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.beans.Introspector;
import java.lang.annotation.Annotation;

class GetterSetterPropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
  implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
{
  protected final MethodT getter;
  protected final MethodT setter;
  private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;
  
  GetterSetterPropertySeed(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, MethodT paramMethodT1, MethodT paramMethodT2)
  {
    parent = paramClassInfoImpl;
    getter = paramMethodT1;
    setter = paramMethodT2;
    if ((paramMethodT1 == null) && (paramMethodT2 == null)) {
      throw new IllegalArgumentException();
    }
  }
  
  public TypeT getRawType()
  {
    if (getter != null) {
      return (TypeT)parent.nav().getReturnType(getter);
    }
    return (TypeT)parent.nav().getMethodParameters(setter)[0];
  }
  
  public <A extends Annotation> A readAnnotation(Class<A> paramClass)
  {
    return parent.reader().getMethodAnnotation(paramClass, getter, setter, this);
  }
  
  public boolean hasAnnotation(Class<? extends Annotation> paramClass)
  {
    return parent.reader().hasMethodAnnotation(paramClass, getName(), getter, setter, this);
  }
  
  public String getName()
  {
    if (getter != null) {
      return getName(getter);
    }
    return getName(setter);
  }
  
  private String getName(MethodT paramMethodT)
  {
    String str1 = parent.nav().getMethodName(paramMethodT);
    String str2 = str1.toLowerCase();
    if ((str2.startsWith("get")) || (str2.startsWith("set"))) {
      return camelize(str1.substring(3));
    }
    if (str2.startsWith("is")) {
      return camelize(str1.substring(2));
    }
    return str1;
  }
  
  private static String camelize(String paramString)
  {
    return Introspector.decapitalize(paramString);
  }
  
  public Locatable getUpstream()
  {
    return parent;
  }
  
  public Location getLocation()
  {
    if (getter != null) {
      return parent.nav().getMethodLocation(getter);
    }
    return parent.nav().getMethodLocation(setter);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\GetterSetterPropertySeed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */