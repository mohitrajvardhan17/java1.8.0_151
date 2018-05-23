package com.sun.xml.internal.ws.spi.db;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public final class TypeInfo
{
  public final QName tagName;
  public Type type;
  public final Annotation[] annotations;
  private Map<String, Object> properties = new HashMap();
  private boolean isGlobalElement = true;
  private TypeInfo parentCollectionType;
  private Type genericType;
  private boolean nillable = true;
  
  public TypeInfo(QName paramQName, Type paramType, Annotation... paramVarArgs)
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
      throw new IllegalArgumentException("Argument(s) \"" + str + "\" can''t be null.)");
    }
    tagName = new QName(paramQName.getNamespaceURI().intern(), paramQName.getLocalPart().intern(), paramQName.getPrefix());
    type = paramType;
    if (((paramType instanceof Class)) && (((Class)paramType).isPrimitive())) {
      nillable = false;
    }
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
  
  public TypeInfo toItemType()
  {
    Type localType1 = genericType != null ? genericType : type;
    Type localType2 = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(localType1, Collection.class);
    if (localType2 == null) {
      return this;
    }
    return new TypeInfo(tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(localType2, 0), new Annotation[0]);
  }
  
  public Map<String, Object> properties()
  {
    return properties;
  }
  
  public boolean isGlobalElement()
  {
    return isGlobalElement;
  }
  
  public void setGlobalElement(boolean paramBoolean)
  {
    isGlobalElement = paramBoolean;
  }
  
  public TypeInfo getParentCollectionType()
  {
    return parentCollectionType;
  }
  
  public void setParentCollectionType(TypeInfo paramTypeInfo)
  {
    parentCollectionType = paramTypeInfo;
  }
  
  public boolean isRepeatedElement()
  {
    return parentCollectionType != null;
  }
  
  public Type getGenericType()
  {
    return genericType;
  }
  
  public void setGenericType(Type paramType)
  {
    genericType = paramType;
  }
  
  public boolean isNillable()
  {
    return nillable;
  }
  
  public void setNillable(boolean paramBoolean)
  {
    nillable = paramBoolean;
  }
  
  public String toString()
  {
    return "TypeInfo: Type = " + type + ", tag = " + tagName;
  }
  
  public TypeInfo getItemType()
  {
    if (((type instanceof Class)) && (((Class)type).isArray()) && (!byte[].class.equals(type)))
    {
      localObject1 = ((Class)type).getComponentType();
      localType = null;
      if ((genericType != null) && ((genericType instanceof GenericArrayType)))
      {
        localObject2 = (GenericArrayType)type;
        localType = ((GenericArrayType)localObject2).getGenericComponentType();
        localObject1 = ((GenericArrayType)localObject2).getGenericComponentType();
      }
      Object localObject2 = new TypeInfo(tagName, (Type)localObject1, annotations);
      if (localType != null) {
        ((TypeInfo)localObject2).setGenericType(localType);
      }
      return (TypeInfo)localObject2;
    }
    Object localObject1 = genericType != null ? genericType : type;
    Type localType = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(localObject1, Collection.class);
    if (localType != null) {
      return new TypeInfo(tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument(localType, 0), annotations);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */