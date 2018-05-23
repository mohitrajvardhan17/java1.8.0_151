package java.lang.invoke;

import java.io.Serializable;
import java.util.Arrays;

public class LambdaMetafactory
{
  public static final int FLAG_SERIALIZABLE = 1;
  public static final int FLAG_MARKERS = 2;
  public static final int FLAG_BRIDGES = 4;
  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
  private static final MethodType[] EMPTY_MT_ARRAY = new MethodType[0];
  
  public LambdaMetafactory() {}
  
  public static CallSite metafactory(MethodHandles.Lookup paramLookup, String paramString, MethodType paramMethodType1, MethodType paramMethodType2, MethodHandle paramMethodHandle, MethodType paramMethodType3)
    throws LambdaConversionException
  {
    InnerClassLambdaMetafactory localInnerClassLambdaMetafactory = new InnerClassLambdaMetafactory(paramLookup, paramMethodType1, paramString, paramMethodType2, paramMethodHandle, paramMethodType3, false, EMPTY_CLASS_ARRAY, EMPTY_MT_ARRAY);
    localInnerClassLambdaMetafactory.validateMetafactoryArgs();
    return localInnerClassLambdaMetafactory.buildCallSite();
  }
  
  public static CallSite altMetafactory(MethodHandles.Lookup paramLookup, String paramString, MethodType paramMethodType, Object... paramVarArgs)
    throws LambdaConversionException
  {
    MethodType localMethodType1 = (MethodType)paramVarArgs[0];
    MethodHandle localMethodHandle = (MethodHandle)paramVarArgs[1];
    MethodType localMethodType2 = (MethodType)paramVarArgs[2];
    int i = ((Integer)paramVarArgs[3]).intValue();
    int j = 4;
    Class[] arrayOfClass1;
    if ((i & 0x2) != 0)
    {
      k = ((Integer)paramVarArgs[(j++)]).intValue();
      arrayOfClass1 = new Class[k];
      System.arraycopy(paramVarArgs, j, arrayOfClass1, 0, k);
      j += k;
    }
    else
    {
      arrayOfClass1 = EMPTY_CLASS_ARRAY;
    }
    MethodType[] arrayOfMethodType;
    if ((i & 0x4) != 0)
    {
      k = ((Integer)paramVarArgs[(j++)]).intValue();
      arrayOfMethodType = new MethodType[k];
      System.arraycopy(paramVarArgs, j, arrayOfMethodType, 0, k);
      j += k;
    }
    else
    {
      arrayOfMethodType = EMPTY_MT_ARRAY;
    }
    int k = (i & 0x1) != 0 ? 1 : 0;
    if (k != 0)
    {
      boolean bool = Serializable.class.isAssignableFrom(paramMethodType.returnType());
      for (Class localClass : arrayOfClass1) {
        bool |= Serializable.class.isAssignableFrom(localClass);
      }
      if (!bool)
      {
        arrayOfClass1 = (Class[])Arrays.copyOf(arrayOfClass1, arrayOfClass1.length + 1);
        arrayOfClass1[(arrayOfClass1.length - 1)] = Serializable.class;
      }
    }
    InnerClassLambdaMetafactory localInnerClassLambdaMetafactory = new InnerClassLambdaMetafactory(paramLookup, paramMethodType, paramString, localMethodType1, localMethodHandle, localMethodType2, k, arrayOfClass1, arrayOfMethodType);
    localInnerClassLambdaMetafactory.validateMetafactoryArgs();
    return localInnerClassLambdaMetafactory.buildCallSite();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\LambdaMetafactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */