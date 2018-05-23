package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class AnnotatedTypeFactory
{
  static final AnnotatedType EMPTY_ANNOTATED_TYPE = new AnnotatedTypeBaseImpl(null, TypeAnnotation.LocationInfo.BASE_LOCATION, new TypeAnnotation[0], new TypeAnnotation[0], null);
  static final AnnotatedType[] EMPTY_ANNOTATED_TYPE_ARRAY = new AnnotatedType[0];
  
  public AnnotatedTypeFactory() {}
  
  public static AnnotatedType buildAnnotatedType(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
  {
    if (paramType == null) {
      return EMPTY_ANNOTATED_TYPE;
    }
    if (isArray(paramType)) {
      return new AnnotatedArrayTypeImpl(paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    if ((paramType instanceof Class)) {
      return new AnnotatedTypeBaseImpl(paramType, addNesting(paramType, paramLocationInfo), paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    if ((paramType instanceof TypeVariable)) {
      return new AnnotatedTypeVariableImpl((TypeVariable)paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    if ((paramType instanceof ParameterizedType)) {
      return new AnnotatedParameterizedTypeImpl((ParameterizedType)paramType, addNesting(paramType, paramLocationInfo), paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    if ((paramType instanceof WildcardType)) {
      return new AnnotatedWildcardTypeImpl((WildcardType)paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    throw new AssertionError("Unknown instance of Type: " + paramType + "\nThis should not happen.");
  }
  
  private static TypeAnnotation.LocationInfo addNesting(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo)
  {
    if (isArray(paramType)) {
      return paramLocationInfo;
    }
    Object localObject;
    if ((paramType instanceof Class))
    {
      localObject = (Class)paramType;
      if (((Class)localObject).getEnclosingClass() == null) {
        return paramLocationInfo;
      }
      if (Modifier.isStatic(((Class)localObject).getModifiers())) {
        return addNesting(((Class)localObject).getEnclosingClass(), paramLocationInfo);
      }
      return addNesting(((Class)localObject).getEnclosingClass(), paramLocationInfo.pushInner());
    }
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      if (((ParameterizedType)localObject).getOwnerType() == null) {
        return paramLocationInfo;
      }
      return addNesting(((ParameterizedType)localObject).getOwnerType(), paramLocationInfo.pushInner());
    }
    return paramLocationInfo;
  }
  
  private static boolean isArray(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      if (localClass.isArray()) {
        return true;
      }
    }
    else if ((paramType instanceof GenericArrayType))
    {
      return true;
    }
    return false;
  }
  
  private static final class AnnotatedArrayTypeImpl
    extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl
    implements AnnotatedArrayType
  {
    AnnotatedArrayTypeImpl(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
    {
      super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    
    public AnnotatedType getAnnotatedGenericComponentType()
    {
      return AnnotatedTypeFactory.buildAnnotatedType(getComponentType(), getLocation().pushArray(), getTypeAnnotations(), getTypeAnnotations(), getDecl());
    }
    
    private Type getComponentType()
    {
      Type localType = getType();
      if ((localType instanceof Class))
      {
        Class localClass = (Class)localType;
        return localClass.getComponentType();
      }
      return ((GenericArrayType)localType).getGenericComponentType();
    }
  }
  
  private static final class AnnotatedParameterizedTypeImpl
    extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl
    implements AnnotatedParameterizedType
  {
    AnnotatedParameterizedTypeImpl(ParameterizedType paramParameterizedType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
    {
      super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    
    public AnnotatedType[] getAnnotatedActualTypeArguments()
    {
      Type[] arrayOfType = getParameterizedType().getActualTypeArguments();
      AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[arrayOfType.length];
      Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
      int i = getTypeAnnotations().length;
      for (int j = 0; j < arrayOfAnnotatedType.length; j++)
      {
        ArrayList localArrayList = new ArrayList(i);
        TypeAnnotation.LocationInfo localLocationInfo = getLocation().pushTypeArg((short)(byte)j);
        for (TypeAnnotation localTypeAnnotation : getTypeAnnotations()) {
          if (localTypeAnnotation.getLocationInfo().isSameLocationInfo(localLocationInfo)) {
            localArrayList.add(localTypeAnnotation);
          }
        }
        arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(arrayOfType[j], localLocationInfo, (TypeAnnotation[])localArrayList.toArray(new TypeAnnotation[0]), getTypeAnnotations(), getDecl());
      }
      return arrayOfAnnotatedType;
    }
    
    private ParameterizedType getParameterizedType()
    {
      return (ParameterizedType)getType();
    }
  }
  
  private static class AnnotatedTypeBaseImpl
    implements AnnotatedType
  {
    private final Type type;
    private final AnnotatedElement decl;
    private final TypeAnnotation.LocationInfo location;
    private final TypeAnnotation[] allOnSameTargetTypeAnnotations;
    private final Map<Class<? extends Annotation>, Annotation> annotations;
    
    AnnotatedTypeBaseImpl(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
    {
      type = paramType;
      decl = paramAnnotatedElement;
      location = paramLocationInfo;
      allOnSameTargetTypeAnnotations = paramArrayOfTypeAnnotation2;
      annotations = TypeAnnotationParser.mapTypeAnnotations(paramLocationInfo.filter(paramArrayOfTypeAnnotation1));
    }
    
    public final Annotation[] getAnnotations()
    {
      return getDeclaredAnnotations();
    }
    
    public final <T extends Annotation> T getAnnotation(Class<T> paramClass)
    {
      return getDeclaredAnnotation(paramClass);
    }
    
    public final <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
    {
      return getDeclaredAnnotationsByType(paramClass);
    }
    
    public final Annotation[] getDeclaredAnnotations()
    {
      return (Annotation[])annotations.values().toArray(new Annotation[0]);
    }
    
    public final <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass)
    {
      return (Annotation)annotations.get(paramClass);
    }
    
    public final <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass)
    {
      return AnnotationSupport.getDirectlyAndIndirectlyPresent(annotations, paramClass);
    }
    
    public final Type getType()
    {
      return type;
    }
    
    final TypeAnnotation.LocationInfo getLocation()
    {
      return location;
    }
    
    final TypeAnnotation[] getTypeAnnotations()
    {
      return allOnSameTargetTypeAnnotations;
    }
    
    final AnnotatedElement getDecl()
    {
      return decl;
    }
  }
  
  private static final class AnnotatedTypeVariableImpl
    extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl
    implements AnnotatedTypeVariable
  {
    AnnotatedTypeVariableImpl(TypeVariable<?> paramTypeVariable, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
    {
      super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
    }
    
    public AnnotatedType[] getAnnotatedBounds()
    {
      return getTypeVariable().getAnnotatedBounds();
    }
    
    private TypeVariable<?> getTypeVariable()
    {
      return (TypeVariable)getType();
    }
  }
  
  private static final class AnnotatedWildcardTypeImpl
    extends AnnotatedTypeFactory.AnnotatedTypeBaseImpl
    implements AnnotatedWildcardType
  {
    private final boolean hasUpperBounds;
    
    AnnotatedWildcardTypeImpl(WildcardType paramWildcardType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
    {
      super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
      hasUpperBounds = (paramWildcardType.getLowerBounds().length == 0);
    }
    
    public AnnotatedType[] getAnnotatedUpperBounds()
    {
      if (!hasUpperBounds()) {
        return new AnnotatedType[0];
      }
      return getAnnotatedBounds(getWildcardType().getUpperBounds());
    }
    
    public AnnotatedType[] getAnnotatedLowerBounds()
    {
      if (hasUpperBounds) {
        return new AnnotatedType[0];
      }
      return getAnnotatedBounds(getWildcardType().getLowerBounds());
    }
    
    private AnnotatedType[] getAnnotatedBounds(Type[] paramArrayOfType)
    {
      AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[paramArrayOfType.length];
      Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
      TypeAnnotation.LocationInfo localLocationInfo = getLocation().pushWildcard();
      int i = getTypeAnnotations().length;
      for (int j = 0; j < arrayOfAnnotatedType.length; j++)
      {
        ArrayList localArrayList = new ArrayList(i);
        for (TypeAnnotation localTypeAnnotation : getTypeAnnotations()) {
          if (localTypeAnnotation.getLocationInfo().isSameLocationInfo(localLocationInfo)) {
            localArrayList.add(localTypeAnnotation);
          }
        }
        arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[j], localLocationInfo, (TypeAnnotation[])localArrayList.toArray(new TypeAnnotation[0]), getTypeAnnotations(), getDecl());
      }
      return arrayOfAnnotatedType;
    }
    
    private WildcardType getWildcardType()
    {
      return (WildcardType)getType();
    }
    
    private boolean hasUpperBounds()
    {
      return hasUpperBounds;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\AnnotatedTypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */