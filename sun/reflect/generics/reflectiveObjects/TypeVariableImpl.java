package sun.reflect.generics.reflectiveObjects;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.misc.ReflectUtil;

public class TypeVariableImpl<D extends GenericDeclaration>
  extends LazyReflectiveObjectGenerator
  implements TypeVariable<D>
{
  D genericDeclaration;
  private String name;
  private Type[] bounds;
  private FieldTypeSignature[] boundASTs;
  private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
  
  private TypeVariableImpl(D paramD, String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature, GenericsFactory paramGenericsFactory)
  {
    super(paramGenericsFactory);
    genericDeclaration = paramD;
    name = paramString;
    boundASTs = paramArrayOfFieldTypeSignature;
  }
  
  private FieldTypeSignature[] getBoundASTs()
  {
    assert (bounds == null);
    return boundASTs;
  }
  
  public static <T extends GenericDeclaration> TypeVariableImpl<T> make(T paramT, String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature, GenericsFactory paramGenericsFactory)
  {
    if ((!(paramT instanceof Class)) && (!(paramT instanceof Method)) && (!(paramT instanceof Constructor))) {
      throw new AssertionError("Unexpected kind of GenericDeclaration" + paramT.getClass().toString());
    }
    return new TypeVariableImpl(paramT, paramString, paramArrayOfFieldTypeSignature, paramGenericsFactory);
  }
  
  public Type[] getBounds()
  {
    if (bounds == null)
    {
      FieldTypeSignature[] arrayOfFieldTypeSignature = getBoundASTs();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (int i = 0; i < arrayOfFieldTypeSignature.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfFieldTypeSignature[i].accept(localReifier);
        arrayOfType[i] = localReifier.getResult();
      }
      bounds = arrayOfType;
    }
    return (Type[])bounds.clone();
  }
  
  public D getGenericDeclaration()
  {
    if ((genericDeclaration instanceof Class)) {
      ReflectUtil.checkPackageAccess((Class)genericDeclaration);
    } else if (((genericDeclaration instanceof Method)) || ((genericDeclaration instanceof Constructor))) {
      ReflectUtil.conservativeCheckMemberAccess((Member)genericDeclaration);
    } else {
      throw new AssertionError("Unexpected kind of GenericDeclaration");
    }
    return genericDeclaration;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return getName();
  }
  
  public boolean equals(Object paramObject)
  {
    if (((paramObject instanceof TypeVariable)) && (paramObject.getClass() == TypeVariableImpl.class))
    {
      TypeVariable localTypeVariable = (TypeVariable)paramObject;
      GenericDeclaration localGenericDeclaration = localTypeVariable.getGenericDeclaration();
      String str = localTypeVariable.getName();
      return (Objects.equals(genericDeclaration, localGenericDeclaration)) && (Objects.equals(name, str));
    }
    return false;
  }
  
  public int hashCode()
  {
    return genericDeclaration.hashCode() ^ name.hashCode();
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return (Annotation)mapAnnotations(getAnnotations()).get(paramClass);
  }
  
  public <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return getAnnotation(paramClass);
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return AnnotationSupport.getDirectlyAndIndirectlyPresent(mapAnnotations(getAnnotations()), paramClass);
  }
  
  public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return getAnnotationsByType(paramClass);
  }
  
  public Annotation[] getAnnotations()
  {
    int i = typeVarIndex();
    if (i < 0) {
      throw new AssertionError("Index must be non-negative.");
    }
    return TypeAnnotationParser.parseTypeVariableAnnotations(getGenericDeclaration(), i);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return getAnnotations();
  }
  
  public AnnotatedType[] getAnnotatedBounds()
  {
    return TypeAnnotationParser.parseAnnotatedBounds(getBounds(), getGenericDeclaration(), typeVarIndex());
  }
  
  private int typeVarIndex()
  {
    TypeVariable[] arrayOfTypeVariable1 = getGenericDeclaration().getTypeParameters();
    int i = -1;
    for (TypeVariable localTypeVariable : arrayOfTypeVariable1)
    {
      i++;
      if (equals(localTypeVariable)) {
        return i;
      }
    }
    return -1;
  }
  
  private static Map<Class<? extends Annotation>, Annotation> mapAnnotations(Annotation[] paramArrayOfAnnotation)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    for (Annotation localAnnotation : paramArrayOfAnnotation)
    {
      Class localClass = localAnnotation.annotationType();
      AnnotationType localAnnotationType = AnnotationType.getInstance(localClass);
      if ((localAnnotationType.retention() == RetentionPolicy.RUNTIME) && (localLinkedHashMap.put(localClass, localAnnotation) != null)) {
        throw new AnnotationFormatError("Duplicate annotation for class: " + localClass + ": " + localAnnotation);
      }
    }
    return localLinkedHashMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\reflectiveObjects\TypeVariableImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */