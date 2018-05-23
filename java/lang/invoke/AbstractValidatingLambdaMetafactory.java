package java.lang.invoke;

import sun.invoke.util.Wrapper;

abstract class AbstractValidatingLambdaMetafactory
{
  final Class<?> targetClass;
  final MethodType invokedType;
  final Class<?> samBase;
  final String samMethodName;
  final MethodType samMethodType;
  final MethodHandle implMethod;
  final MethodHandleInfo implInfo;
  final int implKind;
  final boolean implIsInstanceMethod;
  final Class<?> implDefiningClass;
  final MethodType implMethodType;
  final MethodType instantiatedMethodType;
  final boolean isSerializable;
  final Class<?>[] markerInterfaces;
  final MethodType[] additionalBridges;
  
  AbstractValidatingLambdaMetafactory(MethodHandles.Lookup paramLookup, MethodType paramMethodType1, String paramString, MethodType paramMethodType2, MethodHandle paramMethodHandle, MethodType paramMethodType3, boolean paramBoolean, Class<?>[] paramArrayOfClass, MethodType[] paramArrayOfMethodType)
    throws LambdaConversionException
  {
    if ((paramLookup.lookupModes() & 0x2) == 0) {
      throw new LambdaConversionException(String.format("Invalid caller: %s", new Object[] { paramLookup.lookupClass().getName() }));
    }
    targetClass = paramLookup.lookupClass();
    invokedType = paramMethodType1;
    samBase = paramMethodType1.returnType();
    samMethodName = paramString;
    samMethodType = paramMethodType2;
    implMethod = paramMethodHandle;
    implInfo = paramLookup.revealDirect(paramMethodHandle);
    implKind = implInfo.getReferenceKind();
    implIsInstanceMethod = ((implKind == 5) || (implKind == 7) || (implKind == 9));
    implDefiningClass = implInfo.getDeclaringClass();
    implMethodType = implInfo.getMethodType();
    instantiatedMethodType = paramMethodType3;
    isSerializable = paramBoolean;
    markerInterfaces = paramArrayOfClass;
    additionalBridges = paramArrayOfMethodType;
    if (!samBase.isInterface()) {
      throw new LambdaConversionException(String.format("Functional interface %s is not an interface", new Object[] { samBase.getName() }));
    }
    for (Class<?> localClass : paramArrayOfClass) {
      if (!localClass.isInterface()) {
        throw new LambdaConversionException(String.format("Marker interface %s is not an interface", new Object[] { localClass.getName() }));
      }
    }
  }
  
  abstract CallSite buildCallSite()
    throws LambdaConversionException;
  
  void validateMetafactoryArgs()
    throws LambdaConversionException
  {
    switch (implKind)
    {
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
      break;
    default: 
      throw new LambdaConversionException(String.format("Unsupported MethodHandle kind: %s", new Object[] { implInfo }));
    }
    Class localClass1 = implMethodType.parameterCount();
    int i = implIsInstanceMethod ? 1 : 0;
    int j = invokedType.parameterCount();
    int k = samMethodType.parameterCount();
    int m = instantiatedMethodType.parameterCount();
    if (localClass1 + i != j + k) {
      throw new LambdaConversionException(String.format("Incorrect number of parameters for %s method %s; %d captured parameters, %d functional interface method parameters, %d implementation parameters", new Object[] { implIsInstanceMethod ? "instance" : "static", implInfo, Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(localClass1) }));
    }
    if (m != k) {
      throw new LambdaConversionException(String.format("Incorrect number of parameters for %s method %s; %d instantiated parameters, %d functional interface method parameters", new Object[] { implIsInstanceMethod ? "instance" : "static", implInfo, Integer.valueOf(m), Integer.valueOf(k) }));
    }
    Object localObject;
    for (localObject : additionalBridges) {
      if (((MethodType)localObject).parameterCount() != k) {
        throw new LambdaConversionException(String.format("Incorrect number of parameters for bridge signature %s; incompatible with %s", new Object[] { localObject, samMethodType }));
      }
    }
    int n;
    if (implIsInstanceMethod)
    {
      Class localClass2;
      if (j == 0)
      {
        n = 0;
        ??? = 1;
        localClass2 = instantiatedMethodType.parameterType(0);
      }
      else
      {
        n = 1;
        ??? = 0;
        localClass2 = invokedType.parameterType(0);
      }
      if (!implDefiningClass.isAssignableFrom(localClass2)) {
        throw new LambdaConversionException(String.format("Invalid receiver type %s; not a subtype of implementation type %s", new Object[] { localClass2, implDefiningClass }));
      }
      localObject = implMethod.type().parameterType(0);
      if ((localObject != implDefiningClass) && (!((Class)localObject).isAssignableFrom(localClass2))) {
        throw new LambdaConversionException(String.format("Invalid receiver type %s; not a subtype of implementation receiver type %s", new Object[] { localClass2, localObject }));
      }
    }
    else
    {
      n = 0;
      ??? = 0;
    }
    Class localClass3 = j - n;
    for (Class localClass4 = 0; localClass4 < localClass3; localClass4++)
    {
      localClass5 = implMethodType.parameterType(localClass4);
      localClass6 = invokedType.parameterType(localClass4 + n);
      if (!localClass6.equals(localClass5)) {
        throw new LambdaConversionException(String.format("Type mismatch in captured lambda parameter %d: expecting %s, found %s", new Object[] { Integer.valueOf(localClass4), localClass6, localClass5 }));
      }
    }
    localClass4 = ??? - localClass3;
    for (Class localClass5 = localClass3; localClass5 < localClass1; localClass5++)
    {
      localClass6 = implMethodType.parameterType(localClass5);
      localClass7 = instantiatedMethodType.parameterType(localClass5 + localClass4);
      if (!isAdaptableTo(localClass7, localClass6, true)) {
        throw new LambdaConversionException(String.format("Type mismatch for lambda argument %d: %s is not convertible to %s", new Object[] { Integer.valueOf(localClass5), localClass7, localClass6 }));
      }
    }
    localClass5 = instantiatedMethodType.returnType();
    Class localClass6 = implKind == 8 ? implDefiningClass : implMethodType.returnType();
    Class localClass7 = samMethodType.returnType();
    if (!isAdaptableToAsReturn(localClass6, localClass5)) {
      throw new LambdaConversionException(String.format("Type mismatch for lambda return: %s is not convertible to %s", new Object[] { localClass6, localClass5 }));
    }
    if (!isAdaptableToAsReturnStrict(localClass5, localClass7)) {
      throw new LambdaConversionException(String.format("Type mismatch for lambda expected return: %s is not convertible to %s", new Object[] { localClass5, localClass7 }));
    }
    for (MethodType localMethodType : additionalBridges) {
      if (!isAdaptableToAsReturnStrict(localClass5, localMethodType.returnType())) {
        throw new LambdaConversionException(String.format("Type mismatch for lambda expected return: %s is not convertible to %s", new Object[] { localClass5, localMethodType.returnType() }));
      }
    }
  }
  
  private boolean isAdaptableTo(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean)
  {
    if (paramClass1.equals(paramClass2)) {
      return true;
    }
    Wrapper localWrapper1;
    Wrapper localWrapper2;
    if (paramClass1.isPrimitive())
    {
      localWrapper1 = Wrapper.forPrimitiveType(paramClass1);
      if (paramClass2.isPrimitive())
      {
        localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
        return localWrapper2.isConvertibleFrom(localWrapper1);
      }
      return paramClass2.isAssignableFrom(localWrapper1.wrapperType());
    }
    if (paramClass2.isPrimitive())
    {
      if ((Wrapper.isWrapperType(paramClass1)) && ((localWrapper1 = Wrapper.forWrapperType(paramClass1)).primitiveType().isPrimitive()))
      {
        localWrapper2 = Wrapper.forPrimitiveType(paramClass2);
        return localWrapper2.isConvertibleFrom(localWrapper1);
      }
      return !paramBoolean;
    }
    return (!paramBoolean) || (paramClass2.isAssignableFrom(paramClass1));
  }
  
  private boolean isAdaptableToAsReturn(Class<?> paramClass1, Class<?> paramClass2)
  {
    return (paramClass2.equals(Void.TYPE)) || ((!paramClass1.equals(Void.TYPE)) && (isAdaptableTo(paramClass1, paramClass2, false)));
  }
  
  private boolean isAdaptableToAsReturnStrict(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass1.equals(Void.TYPE)) {
      return paramClass2.equals(Void.TYPE);
    }
    return isAdaptableTo(paramClass1, paramClass2, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\AbstractValidatingLambdaMetafactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */