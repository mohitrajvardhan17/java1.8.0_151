package com.sun.xml.internal.bind.api;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.namespace.QName;

public final class TypeReference
{
  public final QName tagName;
  public final Type type;
  public final Annotation[] annotations;
  
  public TypeReference(QName paramQName, Type paramType, Annotation... paramVarArgs)
  {
    if ((paramQName == null) || (paramType == null) || (paramVarArgs == null))
    {
      String str = "";
      if (paramQName == null) {
        str = "tagName";
      }
      if (paramType == null) {
        str = str + (str.length() > 0 ? ", type" : "type");
      }
      if (paramVarArgs == null) {
        str = str + (str.length() > 0 ? ", annotations" : "annotations");
      }
      Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { str });
      throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { str }));
    }
    tagName = new QName(paramQName.getNamespaceURI().intern(), paramQName.getLocalPart().intern(), paramQName.getPrefix());
    type = paramType;
    annotations = paramVarArgs;
  }
  
  public <A extends Annotation> A get(Class<A> paramClass)
  {
    for (Annotation localAnnotation : annotations) {
      if (localAnnotation.annotationType() == paramClass) {
        return (Annotation)paramClass.cast(localAnnotation);
      }
    }
    return null;
  }
  
  public TypeReference toItemType()
  {
    Type localType = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(type, Collection.class);
    if (localType == null) {
      return this;
    }
    return new TypeReference(tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(localType, 0), new Annotation[0]);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    TypeReference localTypeReference = (TypeReference)paramObject;
    if (!Arrays.equals(annotations, annotations)) {
      return false;
    }
    if (!tagName.equals(tagName)) {
      return false;
    }
    return type.equals(type);
  }
  
  public int hashCode()
  {
    int i = tagName.hashCode();
    i = 31 * i + type.hashCode();
    i = 31 * i + Arrays.hashCode(annotations);
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\api\TypeReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */