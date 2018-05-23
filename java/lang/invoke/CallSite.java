package java.lang.invoke;

import sun.misc.Unsafe;

public abstract class CallSite
{
  MethodHandle target;
  private static final MethodHandle GET_TARGET;
  private static final MethodHandle THROW_UCS;
  private static final long TARGET_OFFSET;
  
  CallSite(MethodType paramMethodType)
  {
    target = makeUninitializedCallSite(paramMethodType);
  }
  
  CallSite(MethodHandle paramMethodHandle)
  {
    paramMethodHandle.type();
    target = paramMethodHandle;
  }
  
  CallSite(MethodType paramMethodType, MethodHandle paramMethodHandle)
    throws Throwable
  {
    this(paramMethodType);
    ConstantCallSite localConstantCallSite = (ConstantCallSite)this;
    MethodHandle localMethodHandle = (MethodHandle)paramMethodHandle.invokeWithArguments(new Object[] { localConstantCallSite });
    checkTargetChange(target, localMethodHandle);
    target = localMethodHandle;
  }
  
  public MethodType type()
  {
    return target.type();
  }
  
  public abstract MethodHandle getTarget();
  
  public abstract void setTarget(MethodHandle paramMethodHandle);
  
  void checkTargetChange(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    if (!localMethodType2.equals(localMethodType1)) {
      throw wrongTargetType(paramMethodHandle2, localMethodType1);
    }
  }
  
  private static WrongMethodTypeException wrongTargetType(MethodHandle paramMethodHandle, MethodType paramMethodType)
  {
    return new WrongMethodTypeException(String.valueOf(paramMethodHandle) + " should be of type " + paramMethodType);
  }
  
  public abstract MethodHandle dynamicInvoker();
  
  MethodHandle makeDynamicInvoker()
  {
    BoundMethodHandle localBoundMethodHandle = GET_TARGET.bindArgumentL(0, this);
    MethodHandle localMethodHandle = MethodHandles.exactInvoker(type());
    return MethodHandles.foldArguments(localMethodHandle, localBoundMethodHandle);
  }
  
  private static Object uninitializedCallSite(Object... paramVarArgs)
  {
    throw new IllegalStateException("uninitialized call site");
  }
  
  private MethodHandle makeUninitializedCallSite(MethodType paramMethodType)
  {
    MethodType localMethodType = paramMethodType.basicType();
    MethodHandle localMethodHandle = localMethodType.form().cachedMethodHandle(2);
    if (localMethodHandle == null)
    {
      localMethodHandle = THROW_UCS.asType(localMethodType);
      localMethodHandle = localMethodType.form().setCachedMethodHandle(2, localMethodHandle);
    }
    return localMethodHandle.viewAsType(paramMethodType, false);
  }
  
  void setTargetNormal(MethodHandle paramMethodHandle)
  {
    MethodHandleNatives.setCallSiteTargetNormal(this, paramMethodHandle);
  }
  
  MethodHandle getTargetVolatile()
  {
    return (MethodHandle)MethodHandleStatics.UNSAFE.getObjectVolatile(this, TARGET_OFFSET);
  }
  
  void setTargetVolatile(MethodHandle paramMethodHandle)
  {
    MethodHandleNatives.setCallSiteTargetVolatile(this, paramMethodHandle);
  }
  
  static CallSite makeSite(MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, Class<?> paramClass)
  {
    MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP.in(paramClass);
    CallSite localCallSite;
    try
    {
      paramObject = maybeReBox(paramObject);
      Object localObject1;
      if (paramObject == null)
      {
        localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType);
      }
      else if (!paramObject.getClass().isArray())
      {
        localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, paramObject);
      }
      else
      {
        localObject2 = (Object[])paramObject;
        maybeReBoxElements((Object[])localObject2);
        switch (localObject2.length)
        {
        case 0: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType);
          break;
        case 1: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, localObject2[0]);
          break;
        case 2: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, localObject2[0], localObject2[1]);
          break;
        case 3: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, localObject2[0], localObject2[1], localObject2[2]);
          break;
        case 4: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, localObject2[0], localObject2[1], localObject2[2], localObject2[3]);
          break;
        case 5: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, localObject2[0], localObject2[1], localObject2[2], localObject2[3], localObject2[4]);
          break;
        case 6: 
          localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, localObject2[0], localObject2[1], localObject2[2], localObject2[3], localObject2[4], localObject2[5]);
          break;
        default: 
          if (3 + localObject2.length > 254) {
            throw new BootstrapMethodError("too many bootstrap method arguments");
          }
          MethodType localMethodType1 = paramMethodHandle.type();
          MethodType localMethodType2 = MethodType.genericMethodType(3 + localObject2.length);
          MethodHandle localMethodHandle1 = paramMethodHandle.asType(localMethodType2);
          MethodHandle localMethodHandle2 = localMethodType2.invokers().spreadInvoker(3);
          localObject1 = localMethodHandle2.invokeExact(localMethodHandle1, localLookup, paramString, paramMethodType, (Object[])localObject2);
        }
      }
      if ((localObject1 instanceof CallSite)) {
        localCallSite = (CallSite)localObject1;
      } else {
        throw new ClassCastException("bootstrap method failed to produce a CallSite");
      }
      if (!localCallSite.getTarget().type().equals(paramMethodType)) {
        throw wrongTargetType(localCallSite.getTarget(), paramMethodType);
      }
    }
    catch (Throwable localThrowable)
    {
      Object localObject2;
      if ((localThrowable instanceof BootstrapMethodError)) {
        localObject2 = (BootstrapMethodError)localThrowable;
      } else {
        localObject2 = new BootstrapMethodError("call site initialization exception", localThrowable);
      }
      throw ((Throwable)localObject2);
    }
    return localCallSite;
  }
  
  private static Object maybeReBox(Object paramObject)
  {
    if ((paramObject instanceof Integer))
    {
      int i = ((Integer)paramObject).intValue();
      if (i == (byte)i) {
        paramObject = Integer.valueOf(i);
      }
    }
    return paramObject;
  }
  
  private static void maybeReBoxElements(Object[] paramArrayOfObject)
  {
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      paramArrayOfObject[i] = maybeReBox(paramArrayOfObject[i]);
    }
  }
  
  static
  {
    
    try
    {
      GET_TARGET = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(CallSite.class, "getTarget", MethodType.methodType(MethodHandle.class));
      THROW_UCS = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(CallSite.class, "uninitializedCallSite", MethodType.methodType(Object.class, Object[].class));
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
    }
    try
    {
      TARGET_OFFSET = MethodHandleStatics.UNSAFE.objectFieldOffset(CallSite.class.getDeclaredField("target"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\CallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */