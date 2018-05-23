package com.sun.xml.internal.bind.v2.model.nav;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;

final class ReflectionNavigator
  implements Navigator<Type, Class, Field, Method>
{
  private static final ReflectionNavigator INSTANCE = new ReflectionNavigator();
  private static final TypeVisitor<Type, Class> baseClassFinder = new TypeVisitor()
  {
    public Type onClass(Class paramAnonymousClass1, Class paramAnonymousClass2)
    {
      if (paramAnonymousClass2 == paramAnonymousClass1) {
        return paramAnonymousClass2;
      }
      Type localType2 = paramAnonymousClass1.getGenericSuperclass();
      Type localType1;
      if (localType2 != null)
      {
        localType1 = (Type)visit(localType2, paramAnonymousClass2);
        if (localType1 != null) {
          return localType1;
        }
      }
      for (Type localType3 : paramAnonymousClass1.getGenericInterfaces())
      {
        localType1 = (Type)visit(localType3, paramAnonymousClass2);
        if (localType1 != null) {
          return localType1;
        }
      }
      return null;
    }
    
    public Type onParameterizdType(ParameterizedType paramAnonymousParameterizedType, Class paramAnonymousClass)
    {
      Class localClass = (Class)paramAnonymousParameterizedType.getRawType();
      if (localClass == paramAnonymousClass) {
        return paramAnonymousParameterizedType;
      }
      Type localType1 = localClass.getGenericSuperclass();
      if (localType1 != null) {
        localType1 = (Type)visit(bind(localType1, localClass, paramAnonymousParameterizedType), paramAnonymousClass);
      }
      if (localType1 != null) {
        return localType1;
      }
      for (Type localType2 : localClass.getGenericInterfaces())
      {
        localType1 = (Type)visit(bind(localType2, localClass, paramAnonymousParameterizedType), paramAnonymousClass);
        if (localType1 != null) {
          return localType1;
        }
      }
      return null;
    }
    
    public Type onGenericArray(GenericArrayType paramAnonymousGenericArrayType, Class paramAnonymousClass)
    {
      return null;
    }
    
    public Type onVariable(TypeVariable paramAnonymousTypeVariable, Class paramAnonymousClass)
    {
      return (Type)visit(paramAnonymousTypeVariable.getBounds()[0], paramAnonymousClass);
    }
    
    public Type onWildcard(WildcardType paramAnonymousWildcardType, Class paramAnonymousClass)
    {
      return null;
    }
    
    private Type bind(Type paramAnonymousType, GenericDeclaration paramAnonymousGenericDeclaration, ParameterizedType paramAnonymousParameterizedType)
    {
      return (Type)ReflectionNavigator.binder.visit(paramAnonymousType, new ReflectionNavigator.BinderArg(paramAnonymousGenericDeclaration, paramAnonymousParameterizedType.getActualTypeArguments()));
    }
  };
  private static final TypeVisitor<Type, BinderArg> binder = new TypeVisitor()
  {
    public Type onClass(Class paramAnonymousClass, ReflectionNavigator.BinderArg paramAnonymousBinderArg)
    {
      return paramAnonymousClass;
    }
    
    public Type onParameterizdType(ParameterizedType paramAnonymousParameterizedType, ReflectionNavigator.BinderArg paramAnonymousBinderArg)
    {
      Type[] arrayOfType = paramAnonymousParameterizedType.getActualTypeArguments();
      int i = 0;
      for (int j = 0; j < arrayOfType.length; j++)
      {
        Type localType2 = arrayOfType[j];
        arrayOfType[j] = ((Type)visit(localType2, paramAnonymousBinderArg));
        i |= (localType2 != arrayOfType[j] ? 1 : 0);
      }
      Type localType1 = paramAnonymousParameterizedType.getOwnerType();
      if (localType1 != null) {
        localType1 = (Type)visit(localType1, paramAnonymousBinderArg);
      }
      i |= (paramAnonymousParameterizedType.getOwnerType() != localType1 ? 1 : 0);
      if (i == 0) {
        return paramAnonymousParameterizedType;
      }
      return new ParameterizedTypeImpl((Class)paramAnonymousParameterizedType.getRawType(), arrayOfType, localType1);
    }
    
    public Type onGenericArray(GenericArrayType paramAnonymousGenericArrayType, ReflectionNavigator.BinderArg paramAnonymousBinderArg)
    {
      Type localType = (Type)visit(paramAnonymousGenericArrayType.getGenericComponentType(), paramAnonymousBinderArg);
      if (localType == paramAnonymousGenericArrayType.getGenericComponentType()) {
        return paramAnonymousGenericArrayType;
      }
      return new GenericArrayTypeImpl(localType);
    }
    
    public Type onVariable(TypeVariable paramAnonymousTypeVariable, ReflectionNavigator.BinderArg paramAnonymousBinderArg)
    {
      return paramAnonymousBinderArg.replace(paramAnonymousTypeVariable);
    }
    
    public Type onWildcard(WildcardType paramAnonymousWildcardType, ReflectionNavigator.BinderArg paramAnonymousBinderArg)
    {
      Type[] arrayOfType1 = paramAnonymousWildcardType.getLowerBounds();
      Type[] arrayOfType2 = paramAnonymousWildcardType.getUpperBounds();
      int i = 0;
      Type localType;
      for (int j = 0; j < arrayOfType1.length; j++)
      {
        localType = arrayOfType1[j];
        arrayOfType1[j] = ((Type)visit(localType, paramAnonymousBinderArg));
        i |= (localType != arrayOfType1[j] ? 1 : 0);
      }
      for (j = 0; j < arrayOfType2.length; j++)
      {
        localType = arrayOfType2[j];
        arrayOfType2[j] = ((Type)visit(localType, paramAnonymousBinderArg));
        i |= (localType != arrayOfType2[j] ? 1 : 0);
      }
      if (i == 0) {
        return paramAnonymousWildcardType;
      }
      return new WildcardTypeImpl(arrayOfType1, arrayOfType2);
    }
  };
  private static final TypeVisitor<Class, Void> eraser = new TypeVisitor()
  {
    public Class onClass(Class paramAnonymousClass, Void paramAnonymousVoid)
    {
      return paramAnonymousClass;
    }
    
    public Class onParameterizdType(ParameterizedType paramAnonymousParameterizedType, Void paramAnonymousVoid)
    {
      return (Class)visit(paramAnonymousParameterizedType.getRawType(), null);
    }
    
    public Class onGenericArray(GenericArrayType paramAnonymousGenericArrayType, Void paramAnonymousVoid)
    {
      return Array.newInstance((Class)visit(paramAnonymousGenericArrayType.getGenericComponentType(), null), 0).getClass();
    }
    
    public Class onVariable(TypeVariable paramAnonymousTypeVariable, Void paramAnonymousVoid)
    {
      return (Class)visit(paramAnonymousTypeVariable.getBounds()[0], null);
    }
    
    public Class onWildcard(WildcardType paramAnonymousWildcardType, Void paramAnonymousVoid)
    {
      return (Class)visit(paramAnonymousWildcardType.getUpperBounds()[0], null);
    }
  };
  
  static ReflectionNavigator getInstance()
  {
    return INSTANCE;
  }
  
  private ReflectionNavigator() {}
  
  public Class getSuperClass(Class paramClass)
  {
    if (paramClass == Object.class) {
      return null;
    }
    Class localClass = paramClass.getSuperclass();
    if (localClass == null) {
      localClass = Object.class;
    }
    return localClass;
  }
  
  public Type getBaseClass(Type paramType, Class paramClass)
  {
    return (Type)baseClassFinder.visit(paramType, paramClass);
  }
  
  public String getClassName(Class paramClass)
  {
    return paramClass.getName();
  }
  
  public String getTypeName(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      if (localClass.isArray()) {
        return getTypeName(localClass.getComponentType()) + "[]";
      }
      return localClass.getName();
    }
    return paramType.toString();
  }
  
  public String getClassShortName(Class paramClass)
  {
    return paramClass.getSimpleName();
  }
  
  public Collection<? extends Field> getDeclaredFields(Class paramClass)
  {
    return Arrays.asList(paramClass.getDeclaredFields());
  }
  
  public Field getDeclaredField(Class paramClass, String paramString)
  {
    try
    {
      return paramClass.getDeclaredField(paramString);
    }
    catch (NoSuchFieldException localNoSuchFieldException) {}
    return null;
  }
  
  public Collection<? extends Method> getDeclaredMethods(Class paramClass)
  {
    return Arrays.asList(paramClass.getDeclaredMethods());
  }
  
  public Class getDeclaringClassForField(Field paramField)
  {
    return paramField.getDeclaringClass();
  }
  
  public Class getDeclaringClassForMethod(Method paramMethod)
  {
    return paramMethod.getDeclaringClass();
  }
  
  public Type getFieldType(Field paramField)
  {
    if (paramField.getType().isArray())
    {
      Class localClass = paramField.getType().getComponentType();
      if (localClass.isPrimitive()) {
        return Array.newInstance(localClass, 0).getClass();
      }
    }
    return fix(paramField.getGenericType());
  }
  
  public String getFieldName(Field paramField)
  {
    return paramField.getName();
  }
  
  public String getMethodName(Method paramMethod)
  {
    return paramMethod.getName();
  }
  
  public Type getReturnType(Method paramMethod)
  {
    return fix(paramMethod.getGenericReturnType());
  }
  
  public Type[] getMethodParameters(Method paramMethod)
  {
    return paramMethod.getGenericParameterTypes();
  }
  
  public boolean isStaticMethod(Method paramMethod)
  {
    return Modifier.isStatic(paramMethod.getModifiers());
  }
  
  public boolean isFinalMethod(Method paramMethod)
  {
    return Modifier.isFinal(paramMethod.getModifiers());
  }
  
  public boolean isSubClassOf(Type paramType1, Type paramType2)
  {
    return erasure(paramType2).isAssignableFrom(erasure(paramType1));
  }
  
  public Class ref(Class paramClass)
  {
    return paramClass;
  }
  
  public Class use(Class paramClass)
  {
    return paramClass;
  }
  
  public Class asDecl(Type paramType)
  {
    return erasure(paramType);
  }
  
  public Class asDecl(Class paramClass)
  {
    return paramClass;
  }
  
  public <T> Class<T> erasure(Type paramType)
  {
    return (Class)eraser.visit(paramType, null);
  }
  
  public boolean isAbstract(Class paramClass)
  {
    return Modifier.isAbstract(paramClass.getModifiers());
  }
  
  public boolean isFinal(Class paramClass)
  {
    return Modifier.isFinal(paramClass.getModifiers());
  }
  
  public Type createParameterizedType(Class paramClass, Type... paramVarArgs)
  {
    return new ParameterizedTypeImpl(paramClass, paramVarArgs, null);
  }
  
  public boolean isArray(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      return localClass.isArray();
    }
    return (paramType instanceof GenericArrayType);
  }
  
  public boolean isArrayButNotByteArray(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      return (localClass.isArray()) && (localClass != byte[].class);
    }
    if ((paramType instanceof GenericArrayType))
    {
      paramType = ((GenericArrayType)paramType).getGenericComponentType();
      return paramType != Byte.TYPE;
    }
    return false;
  }
  
  public Type getComponentType(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      return localClass.getComponentType();
    }
    if ((paramType instanceof GenericArrayType)) {
      return ((GenericArrayType)paramType).getGenericComponentType();
    }
    throw new IllegalArgumentException();
  }
  
  public Type getTypeArgument(Type paramType, int paramInt)
  {
    if ((paramType instanceof ParameterizedType))
    {
      ParameterizedType localParameterizedType = (ParameterizedType)paramType;
      return fix(localParameterizedType.getActualTypeArguments()[paramInt]);
    }
    throw new IllegalArgumentException();
  }
  
  public boolean isParameterizedType(Type paramType)
  {
    return paramType instanceof ParameterizedType;
  }
  
  public boolean isPrimitive(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      return localClass.isPrimitive();
    }
    return false;
  }
  
  public Type getPrimitive(Class paramClass)
  {
    assert (paramClass.isPrimitive());
    return paramClass;
  }
  
  public Location getClassLocation(final Class paramClass)
  {
    new Location()
    {
      public String toString()
      {
        return paramClass.getName();
      }
    };
  }
  
  public Location getFieldLocation(final Field paramField)
  {
    new Location()
    {
      public String toString()
      {
        return paramField.toString();
      }
    };
  }
  
  public Location getMethodLocation(final Method paramMethod)
  {
    new Location()
    {
      public String toString()
      {
        return paramMethod.toString();
      }
    };
  }
  
  public boolean hasDefaultConstructor(Class paramClass)
  {
    try
    {
      paramClass.getDeclaredConstructor(new Class[0]);
      return true;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return false;
  }
  
  public boolean isStaticField(Field paramField)
  {
    return Modifier.isStatic(paramField.getModifiers());
  }
  
  public boolean isPublicMethod(Method paramMethod)
  {
    return Modifier.isPublic(paramMethod.getModifiers());
  }
  
  public boolean isPublicField(Field paramField)
  {
    return Modifier.isPublic(paramField.getModifiers());
  }
  
  public boolean isEnum(Class paramClass)
  {
    return Enum.class.isAssignableFrom(paramClass);
  }
  
  public Field[] getEnumConstants(Class paramClass)
  {
    try
    {
      Object[] arrayOfObject = paramClass.getEnumConstants();
      Field[] arrayOfField = new Field[arrayOfObject.length];
      for (int i = 0; i < arrayOfObject.length; i++) {
        arrayOfField[i] = paramClass.getField(((Enum)arrayOfObject[i]).name());
      }
      return arrayOfField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new NoSuchFieldError(localNoSuchFieldException.getMessage());
    }
  }
  
  public Type getVoidType()
  {
    return Void.class;
  }
  
  public String getPackageName(Class paramClass)
  {
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    if (i < 0) {
      return "";
    }
    return str.substring(0, i);
  }
  
  public Class loadObjectFactory(Class paramClass, String paramString)
  {
    ClassLoader localClassLoader = SecureLoader.getClassClassLoader(paramClass);
    if (localClassLoader == null) {
      localClassLoader = SecureLoader.getSystemClassLoader();
    }
    try
    {
      return localClassLoader.loadClass(paramString + ".ObjectFactory");
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return null;
  }
  
  public boolean isBridgeMethod(Method paramMethod)
  {
    return paramMethod.isBridge();
  }
  
  public boolean isOverriding(Method paramMethod, Class paramClass)
  {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    while (paramClass != null)
    {
      try
      {
        if (paramClass.getDeclaredMethod(str, arrayOfClass) != null) {
          return true;
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
      paramClass = paramClass.getSuperclass();
    }
    return false;
  }
  
  public boolean isInterface(Class paramClass)
  {
    return paramClass.isInterface();
  }
  
  public boolean isTransient(Field paramField)
  {
    return Modifier.isTransient(paramField.getModifiers());
  }
  
  public boolean isInnerClass(Class paramClass)
  {
    return (paramClass.getEnclosingClass() != null) && (!Modifier.isStatic(paramClass.getModifiers()));
  }
  
  public boolean isSameType(Type paramType1, Type paramType2)
  {
    return paramType1.equals(paramType2);
  }
  
  private Type fix(Type paramType)
  {
    if (!(paramType instanceof GenericArrayType)) {
      return paramType;
    }
    GenericArrayType localGenericArrayType = (GenericArrayType)paramType;
    if ((localGenericArrayType.getGenericComponentType() instanceof Class))
    {
      Class localClass = (Class)localGenericArrayType.getGenericComponentType();
      return Array.newInstance(localClass, 0).getClass();
    }
    return paramType;
  }
  
  private static class BinderArg
  {
    final TypeVariable[] params;
    final Type[] args;
    
    BinderArg(TypeVariable[] paramArrayOfTypeVariable, Type[] paramArrayOfType)
    {
      params = paramArrayOfTypeVariable;
      args = paramArrayOfType;
      assert (paramArrayOfTypeVariable.length == paramArrayOfType.length);
    }
    
    public BinderArg(GenericDeclaration paramGenericDeclaration, Type[] paramArrayOfType)
    {
      this(paramGenericDeclaration.getTypeParameters(), paramArrayOfType);
    }
    
    Type replace(TypeVariable paramTypeVariable)
    {
      for (int i = 0; i < params.length; i++) {
        if (params[i].equals(paramTypeVariable)) {
          return args[i];
        }
      }
      return paramTypeVariable;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\ReflectionNavigator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */