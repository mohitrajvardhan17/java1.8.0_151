package java.lang.invoke;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.Wrapper;

class LambdaFormEditor
{
  final LambdaForm lambdaForm;
  private static final int MIN_CACHE_ARRAY_SIZE = 4;
  private static final int MAX_CACHE_ARRAY_SIZE = 16;
  
  private LambdaFormEditor(LambdaForm paramLambdaForm)
  {
    lambdaForm = paramLambdaForm;
  }
  
  static LambdaFormEditor lambdaFormEditor(LambdaForm paramLambdaForm)
  {
    return new LambdaFormEditor(paramLambdaForm.uncustomize());
  }
  
  private LambdaForm getInCache(Transform paramTransform)
  {
    assert (paramTransform.get() == null);
    Object localObject1 = lambdaForm.transformCache;
    Object localObject2 = null;
    Object localObject3;
    if ((localObject1 instanceof ConcurrentHashMap))
    {
      localObject3 = (ConcurrentHashMap)localObject1;
      localObject2 = (Transform)((ConcurrentHashMap)localObject3).get(paramTransform);
    }
    else
    {
      if (localObject1 == null) {
        return null;
      }
      if ((localObject1 instanceof Transform))
      {
        localObject3 = (Transform)localObject1;
        if (((Transform)localObject3).equals(paramTransform)) {
          localObject2 = localObject3;
        }
      }
      else
      {
        localObject3 = (Transform[])localObject1;
        for (int i = 0; i < localObject3.length; i++)
        {
          Object localObject4 = localObject3[i];
          if (localObject4 == null) {
            break;
          }
          if (((Transform)localObject4).equals(paramTransform))
          {
            localObject2 = localObject4;
            break;
          }
        }
      }
    }
    assert ((localObject2 == null) || (paramTransform.equals((Transform)localObject2)));
    return localObject2 != null ? (LambdaForm)((Transform)localObject2).get() : null;
  }
  
  private LambdaForm putInCache(Transform paramTransform, LambdaForm paramLambdaForm)
  {
    paramTransform = paramTransform.withResult(paramLambdaForm);
    for (int i = 0;; i++)
    {
      Object localObject1 = lambdaForm.transformCache;
      Object localObject2;
      Object localObject3;
      if ((localObject1 instanceof ConcurrentHashMap))
      {
        ConcurrentHashMap localConcurrentHashMap1 = (ConcurrentHashMap)localObject1;
        localObject2 = (Transform)localConcurrentHashMap1.putIfAbsent(paramTransform, paramTransform);
        if (localObject2 == null) {
          return paramLambdaForm;
        }
        localObject3 = (LambdaForm)((Transform)localObject2).get();
        if (localObject3 != null) {
          return (LambdaForm)localObject3;
        }
        if (localConcurrentHashMap1.replace(paramTransform, localObject2, paramTransform)) {
          return paramLambdaForm;
        }
      }
      else
      {
        assert (i == 0);
        synchronized (lambdaForm)
        {
          localObject1 = lambdaForm.transformCache;
          if (!(localObject1 instanceof ConcurrentHashMap))
          {
            if (localObject1 == null)
            {
              lambdaForm.transformCache = paramTransform;
              return paramLambdaForm;
            }
            if ((localObject1 instanceof Transform))
            {
              localObject3 = (Transform)localObject1;
              if (((Transform)localObject3).equals(paramTransform))
              {
                LambdaForm localLambdaForm = (LambdaForm)((Transform)localObject3).get();
                if (localLambdaForm == null)
                {
                  lambdaForm.transformCache = paramTransform;
                  return paramLambdaForm;
                }
                return localLambdaForm;
              }
              if (((Transform)localObject3).get() == null)
              {
                lambdaForm.transformCache = paramTransform;
                return paramLambdaForm;
              }
              localObject2 = new Transform[4];
              localObject2[0] = localObject3;
              lambdaForm.transformCache = localObject2;
            }
            else
            {
              localObject2 = (Transform[])localObject1;
            }
            int j = localObject2.length;
            int k = -1;
            Object localObject4;
            for (ConcurrentHashMap localConcurrentHashMap2 = 0; localConcurrentHashMap2 < j; localConcurrentHashMap2++)
            {
              localConcurrentHashMap3 = localObject2[localConcurrentHashMap2];
              if (localConcurrentHashMap3 == null) {
                break;
              }
              if (localConcurrentHashMap3.equals(paramTransform))
              {
                localObject4 = (LambdaForm)localConcurrentHashMap3.get();
                if (localObject4 == null)
                {
                  localObject2[localConcurrentHashMap2] = paramTransform;
                  return paramLambdaForm;
                }
                return (LambdaForm)localObject4;
              }
              if ((k < 0) && (localConcurrentHashMap3.get() == null)) {
                k = localConcurrentHashMap2;
              }
            }
            if ((localConcurrentHashMap2 >= j) && (k < 0)) {
              if (j < 16)
              {
                j = Math.min(j * 2, 16);
                localObject2 = (Transform[])Arrays.copyOf((Object[])localObject2, j);
                lambdaForm.transformCache = localObject2;
              }
              else
              {
                localConcurrentHashMap3 = new ConcurrentHashMap(32);
                for (Object localObject5 : localObject2) {
                  localConcurrentHashMap3.put(localObject5, localObject5);
                }
                lambdaForm.transformCache = localConcurrentHashMap3;
                continue;
              }
            }
            ConcurrentHashMap localConcurrentHashMap3 = k >= 0 ? k : localConcurrentHashMap2;
            localObject2[localConcurrentHashMap3] = paramTransform;
            return paramLambdaForm;
          }
        }
      }
    }
  }
  
  private LambdaFormBuffer buffer()
  {
    return new LambdaFormBuffer(lambdaForm);
  }
  
  private BoundMethodHandle.SpeciesData oldSpeciesData()
  {
    return BoundMethodHandle.speciesData(lambdaForm);
  }
  
  private BoundMethodHandle.SpeciesData newSpeciesData(LambdaForm.BasicType paramBasicType)
  {
    return oldSpeciesData().extendWith(paramBasicType);
  }
  
  BoundMethodHandle bindArgumentL(BoundMethodHandle paramBoundMethodHandle, int paramInt, Object paramObject)
  {
    assert (paramBoundMethodHandle.speciesData() == oldSpeciesData());
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.L_TYPE;
    MethodType localMethodType = bindArgumentType(paramBoundMethodHandle, paramInt, localBasicType);
    LambdaForm localLambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendL(localMethodType, localLambdaForm, paramObject);
  }
  
  BoundMethodHandle bindArgumentI(BoundMethodHandle paramBoundMethodHandle, int paramInt1, int paramInt2)
  {
    assert (paramBoundMethodHandle.speciesData() == oldSpeciesData());
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.I_TYPE;
    MethodType localMethodType = bindArgumentType(paramBoundMethodHandle, paramInt1, localBasicType);
    LambdaForm localLambdaForm = bindArgumentForm(1 + paramInt1);
    return paramBoundMethodHandle.copyWithExtendI(localMethodType, localLambdaForm, paramInt2);
  }
  
  BoundMethodHandle bindArgumentJ(BoundMethodHandle paramBoundMethodHandle, int paramInt, long paramLong)
  {
    assert (paramBoundMethodHandle.speciesData() == oldSpeciesData());
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.J_TYPE;
    MethodType localMethodType = bindArgumentType(paramBoundMethodHandle, paramInt, localBasicType);
    LambdaForm localLambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendJ(localMethodType, localLambdaForm, paramLong);
  }
  
  BoundMethodHandle bindArgumentF(BoundMethodHandle paramBoundMethodHandle, int paramInt, float paramFloat)
  {
    assert (paramBoundMethodHandle.speciesData() == oldSpeciesData());
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.F_TYPE;
    MethodType localMethodType = bindArgumentType(paramBoundMethodHandle, paramInt, localBasicType);
    LambdaForm localLambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendF(localMethodType, localLambdaForm, paramFloat);
  }
  
  BoundMethodHandle bindArgumentD(BoundMethodHandle paramBoundMethodHandle, int paramInt, double paramDouble)
  {
    assert (paramBoundMethodHandle.speciesData() == oldSpeciesData());
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.D_TYPE;
    MethodType localMethodType = bindArgumentType(paramBoundMethodHandle, paramInt, localBasicType);
    LambdaForm localLambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendD(localMethodType, localLambdaForm, paramDouble);
  }
  
  private MethodType bindArgumentType(BoundMethodHandle paramBoundMethodHandle, int paramInt, LambdaForm.BasicType paramBasicType)
  {
    assert (form.uncustomize() == lambdaForm);
    assert (form.names[(1 + paramInt)].type == paramBasicType);
    assert (LambdaForm.BasicType.basicType(paramBoundMethodHandle.type().parameterType(paramInt)) == paramBasicType);
    return paramBoundMethodHandle.type().dropParameterTypes(paramInt, paramInt + 1);
  }
  
  LambdaForm bindArgumentForm(int paramInt)
  {
    Transform localTransform = Transform.of(LambdaFormEditor.Transform.Kind.BIND_ARG, paramInt);
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (localLambdaForm.parameterConstraint(0) == newSpeciesData(lambdaForm.parameterType(paramInt)));
      return localLambdaForm;
    }
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    BoundMethodHandle.SpeciesData localSpeciesData1 = oldSpeciesData();
    BoundMethodHandle.SpeciesData localSpeciesData2 = newSpeciesData(lambdaForm.parameterType(paramInt));
    LambdaForm.Name localName1 = lambdaForm.parameter(0);
    LambdaForm.NamedFunction localNamedFunction = localSpeciesData2.getterFunction(localSpeciesData1.fieldCount());
    LambdaForm.Name localName2;
    if (paramInt != 0)
    {
      localLambdaFormBuffer.replaceFunctions(localSpeciesData1.getterFunctions(), localSpeciesData2.getterFunctions(), new Object[] { localName1 });
      localName2 = localName1.withConstraint(localSpeciesData2);
      localLambdaFormBuffer.renameParameter(0, localName2);
      localLambdaFormBuffer.replaceParameterByNewExpression(paramInt, new LambdaForm.Name(localNamedFunction, new Object[] { localName2 }));
    }
    else
    {
      assert (localSpeciesData1 == BoundMethodHandle.SpeciesData.EMPTY);
      localName2 = new LambdaForm.Name(LambdaForm.BasicType.L_TYPE).withConstraint(localSpeciesData2);
      localLambdaFormBuffer.replaceParameterByNewExpression(0, new LambdaForm.Name(localNamedFunction, new Object[] { localName2 }));
      localLambdaFormBuffer.insertParameter(0, localName2);
    }
    localLambdaForm = localLambdaFormBuffer.endEdit();
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm addArgumentForm(int paramInt, LambdaForm.BasicType paramBasicType)
  {
    Transform localTransform = Transform.of(LambdaFormEditor.Transform.Kind.ADD_ARG, paramInt, paramBasicType.ordinal());
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == lambdaForm.arity + 1);
      assert (localLambdaForm.parameterType(paramInt) == paramBasicType);
      return localLambdaForm;
    }
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    localLambdaFormBuffer.insertParameter(paramInt, new LambdaForm.Name(paramBasicType));
    localLambdaForm = localLambdaFormBuffer.endEdit();
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm dupArgumentForm(int paramInt1, int paramInt2)
  {
    Transform localTransform = Transform.of(LambdaFormEditor.Transform.Kind.DUP_ARG, paramInt1, paramInt2);
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == lambdaForm.arity - 1);
      return localLambdaForm;
    }
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    assert (lambdaForm.parameter(paramInt1).constraint == null);
    assert (lambdaForm.parameter(paramInt2).constraint == null);
    localLambdaFormBuffer.replaceParameterByCopy(paramInt2, paramInt1);
    localLambdaForm = localLambdaFormBuffer.endEdit();
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm spreadArgumentsForm(int paramInt1, Class<?> paramClass, int paramInt2)
  {
    Class localClass = paramClass.getComponentType();
    Object localObject = paramClass;
    if (!localClass.isPrimitive()) {
      localObject = Object[].class;
    }
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.basicType(localClass);
    int i = localBasicType.ordinal();
    if ((localBasicType.basicTypeClass() != localClass) && (localClass.isPrimitive())) {
      i = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(localClass).ordinal();
    }
    Transform localTransform = Transform.of(LambdaFormEditor.Transform.Kind.SPREAD_ARGS, paramInt1, i, paramInt2);
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == lambdaForm.arity - paramInt2 + 1);
      return localLambdaForm;
    }
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    assert (paramInt1 <= 255);
    assert (paramInt1 + paramInt2 <= lambdaForm.arity);
    assert (paramInt1 > 0);
    LambdaForm.Name localName1 = new LambdaForm.Name(LambdaForm.BasicType.L_TYPE);
    LambdaForm.Name localName2 = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_checkSpreadArgument, new Object[] { localName1, Integer.valueOf(paramInt2) });
    int j = lambdaForm.arity();
    localLambdaFormBuffer.insertExpression(j++, localName2);
    MethodHandle localMethodHandle = MethodHandles.arrayElementGetter((Class)localObject);
    for (int k = 0; k < paramInt2; k++)
    {
      LambdaForm.Name localName3 = new LambdaForm.Name(localMethodHandle, new Object[] { localName1, Integer.valueOf(k) });
      localLambdaFormBuffer.insertExpression(j + k, localName3);
      localLambdaFormBuffer.replaceParameterByCopy(paramInt1 + k, j + k);
    }
    localLambdaFormBuffer.insertParameter(paramInt1, localName1);
    localLambdaForm = localLambdaFormBuffer.endEdit();
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm collectArgumentsForm(int paramInt, MethodType paramMethodType)
  {
    int i = paramMethodType.parameterCount();
    boolean bool = paramMethodType.returnType() == Void.TYPE;
    if ((i == 1) && (!bool)) {
      return filterArgumentForm(paramInt, LambdaForm.BasicType.basicType(paramMethodType.parameterType(0)));
    }
    LambdaForm.BasicType[] arrayOfBasicType = LambdaForm.BasicType.basicTypes(paramMethodType.parameterList());
    LambdaFormEditor.Transform.Kind localKind = bool ? LambdaFormEditor.Transform.Kind.COLLECT_ARGS_TO_VOID : LambdaFormEditor.Transform.Kind.COLLECT_ARGS;
    if ((bool) && (i == 0)) {
      paramInt = 1;
    }
    Transform localTransform = Transform.of(localKind, paramInt, i, LambdaForm.BasicType.basicTypesOrd(arrayOfBasicType));
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      if (!$assertionsDisabled) {
        if (arity != lambdaForm.arity - (bool ? 0 : 1) + i) {
          throw new AssertionError();
        }
      }
      return localLambdaForm;
    }
    localLambdaForm = makeArgumentCombinationForm(paramInt, paramMethodType, false, bool);
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm collectArgumentArrayForm(int paramInt, MethodHandle paramMethodHandle)
  {
    MethodType localMethodType = paramMethodHandle.type();
    int i = localMethodType.parameterCount();
    assert (paramMethodHandle.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY);
    Class localClass1 = localMethodType.returnType();
    Class localClass2 = localClass1.getComponentType();
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.basicType(localClass2);
    int j = localBasicType.ordinal();
    if (localBasicType.basicTypeClass() != localClass2)
    {
      if (!localClass2.isPrimitive()) {
        return null;
      }
      j = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(localClass2).ordinal();
    }
    assert (localMethodType.parameterList().equals(Collections.nCopies(i, localClass2)));
    LambdaFormEditor.Transform.Kind localKind = LambdaFormEditor.Transform.Kind.COLLECT_ARGS_TO_ARRAY;
    Transform localTransform = Transform.of(localKind, paramInt, i, j);
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == lambdaForm.arity - 1 + i);
      return localLambdaForm;
    }
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    assert (paramInt + 1 <= lambdaForm.arity);
    assert (paramInt > 0);
    LambdaForm.Name[] arrayOfName1 = new LambdaForm.Name[i];
    for (int k = 0; k < i; k++) {
      arrayOfName1[k] = new LambdaForm.Name(paramInt + k, localBasicType);
    }
    LambdaForm.Name localName1 = new LambdaForm.Name(paramMethodHandle, (Object[])arrayOfName1);
    int m = lambdaForm.arity();
    localLambdaFormBuffer.insertExpression(m, localName1);
    int n = paramInt + 1;
    for (LambdaForm.Name localName2 : arrayOfName1) {
      localLambdaFormBuffer.insertParameter(n++, localName2);
    }
    assert (localLambdaFormBuffer.lastIndexOf(localName1) == m + arrayOfName1.length);
    localLambdaFormBuffer.replaceParameterByCopy(paramInt, m + arrayOfName1.length);
    localLambdaForm = localLambdaFormBuffer.endEdit();
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm filterArgumentForm(int paramInt, LambdaForm.BasicType paramBasicType)
  {
    Transform localTransform = Transform.of(LambdaFormEditor.Transform.Kind.FILTER_ARG, paramInt, paramBasicType.ordinal());
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == lambdaForm.arity);
      assert (localLambdaForm.parameterType(paramInt) == paramBasicType);
      return localLambdaForm;
    }
    LambdaForm.BasicType localBasicType = lambdaForm.parameterType(paramInt);
    MethodType localMethodType = MethodType.methodType(localBasicType.basicTypeClass(), paramBasicType.basicTypeClass());
    localLambdaForm = makeArgumentCombinationForm(paramInt, localMethodType, false, false);
    return putInCache(localTransform, localLambdaForm);
  }
  
  private LambdaForm makeArgumentCombinationForm(int paramInt, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2)
  {
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    int i = paramMethodType.parameterCount();
    int j = paramBoolean2 ? 0 : 1;
    assert (paramInt <= 255);
    if (!$assertionsDisabled) {
      if (paramInt + j + (paramBoolean1 ? i : 0) > lambdaForm.arity) {
        throw new AssertionError();
      }
    }
    assert (paramInt > 0);
    assert (paramMethodType == paramMethodType.basicType());
    assert ((paramMethodType.returnType() != Void.TYPE) || (paramBoolean2));
    BoundMethodHandle.SpeciesData localSpeciesData1 = oldSpeciesData();
    BoundMethodHandle.SpeciesData localSpeciesData2 = newSpeciesData(LambdaForm.BasicType.L_TYPE);
    LambdaForm.Name localName1 = lambdaForm.parameter(0);
    localLambdaFormBuffer.replaceFunctions(localSpeciesData1.getterFunctions(), localSpeciesData2.getterFunctions(), new Object[] { localName1 });
    LambdaForm.Name localName2 = localName1.withConstraint(localSpeciesData2);
    localLambdaFormBuffer.renameParameter(0, localName2);
    LambdaForm.Name localName3 = new LambdaForm.Name(localSpeciesData2.getterFunction(localSpeciesData1.fieldCount()), new Object[] { localName2 });
    Object[] arrayOfObject = new Object[1 + i];
    arrayOfObject[0] = localName3;
    LambdaForm.Name[] arrayOfName1;
    if (paramBoolean1)
    {
      arrayOfName1 = new LambdaForm.Name[0];
      System.arraycopy(lambdaForm.names, paramInt + j, arrayOfObject, 1, i);
    }
    else
    {
      arrayOfName1 = new LambdaForm.Name[i];
      localObject = LambdaForm.BasicType.basicTypes(paramMethodType.parameterList());
      for (k = 0; k < localObject.length; k++) {
        arrayOfName1[k] = new LambdaForm.Name(paramInt + k, localObject[k]);
      }
      System.arraycopy(arrayOfName1, 0, arrayOfObject, 1, i);
    }
    Object localObject = new LambdaForm.Name(paramMethodType, arrayOfObject);
    int k = lambdaForm.arity();
    localLambdaFormBuffer.insertExpression(k + 0, localName3);
    localLambdaFormBuffer.insertExpression(k + 1, (LambdaForm.Name)localObject);
    int m = paramInt + j;
    for (LambdaForm.Name localName4 : arrayOfName1) {
      localLambdaFormBuffer.insertParameter(m++, localName4);
    }
    assert (localLambdaFormBuffer.lastIndexOf((LambdaForm.Name)localObject) == k + 1 + arrayOfName1.length);
    if (!paramBoolean2) {
      localLambdaFormBuffer.replaceParameterByCopy(paramInt, k + 1 + arrayOfName1.length);
    }
    return localLambdaFormBuffer.endEdit();
  }
  
  LambdaForm filterReturnForm(LambdaForm.BasicType paramBasicType, boolean paramBoolean)
  {
    LambdaFormEditor.Transform.Kind localKind = paramBoolean ? LambdaFormEditor.Transform.Kind.FILTER_RETURN_TO_ZERO : LambdaFormEditor.Transform.Kind.FILTER_RETURN;
    Transform localTransform = Transform.of(localKind, paramBasicType.ordinal());
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == lambdaForm.arity);
      assert (localLambdaForm.returnType() == paramBasicType);
      return localLambdaForm;
    }
    LambdaFormBuffer localLambdaFormBuffer = buffer();
    localLambdaFormBuffer.startEdit();
    int i = lambdaForm.names.length;
    LambdaForm.Name localName1;
    if (paramBoolean)
    {
      if (paramBasicType == LambdaForm.BasicType.V_TYPE) {
        localName1 = null;
      } else {
        localName1 = new LambdaForm.Name(LambdaForm.constantZero(paramBasicType), new Object[0]);
      }
    }
    else
    {
      BoundMethodHandle.SpeciesData localSpeciesData1 = oldSpeciesData();
      BoundMethodHandle.SpeciesData localSpeciesData2 = newSpeciesData(LambdaForm.BasicType.L_TYPE);
      LambdaForm.Name localName2 = lambdaForm.parameter(0);
      localLambdaFormBuffer.replaceFunctions(localSpeciesData1.getterFunctions(), localSpeciesData2.getterFunctions(), new Object[] { localName2 });
      LambdaForm.Name localName3 = localName2.withConstraint(localSpeciesData2);
      localLambdaFormBuffer.renameParameter(0, localName3);
      LambdaForm.Name localName4 = new LambdaForm.Name(localSpeciesData2.getterFunction(localSpeciesData1.fieldCount()), new Object[] { localName3 });
      localLambdaFormBuffer.insertExpression(i++, localName4);
      LambdaForm.BasicType localBasicType = lambdaForm.returnType();
      MethodType localMethodType;
      if (localBasicType == LambdaForm.BasicType.V_TYPE)
      {
        localMethodType = MethodType.methodType(paramBasicType.basicTypeClass());
        localName1 = new LambdaForm.Name(localMethodType, new Object[] { localName4 });
      }
      else
      {
        localMethodType = MethodType.methodType(paramBasicType.basicTypeClass(), localBasicType.basicTypeClass());
        localName1 = new LambdaForm.Name(localMethodType, new Object[] { localName4, lambdaForm.names[lambdaForm.result] });
      }
    }
    if (localName1 != null) {
      localLambdaFormBuffer.insertExpression(i++, localName1);
    }
    localLambdaFormBuffer.setResult(localName1);
    localLambdaForm = localLambdaFormBuffer.endEdit();
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm foldArgumentsForm(int paramInt, boolean paramBoolean, MethodType paramMethodType)
  {
    int i = paramMethodType.parameterCount();
    LambdaFormEditor.Transform.Kind localKind = paramBoolean ? LambdaFormEditor.Transform.Kind.FOLD_ARGS_TO_VOID : LambdaFormEditor.Transform.Kind.FOLD_ARGS;
    Transform localTransform = Transform.of(localKind, paramInt, i);
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      if (!$assertionsDisabled) {
        if (arity != lambdaForm.arity - (localKind == LambdaFormEditor.Transform.Kind.FOLD_ARGS ? 1 : 0)) {
          throw new AssertionError();
        }
      }
      return localLambdaForm;
    }
    localLambdaForm = makeArgumentCombinationForm(paramInt, paramMethodType, true, paramBoolean);
    return putInCache(localTransform, localLambdaForm);
  }
  
  LambdaForm permuteArgumentsForm(int paramInt, int[] paramArrayOfInt)
  {
    assert (paramInt == 1);
    int i = lambdaForm.names.length;
    int j = paramArrayOfInt.length;
    int k = 0;
    int m = 1;
    for (int n = 0; n < paramArrayOfInt.length; n++)
    {
      int i1 = paramArrayOfInt[n];
      if (i1 != n) {
        m = 0;
      }
      k = Math.max(k, i1 + 1);
    }
    assert (paramInt + paramArrayOfInt.length == lambdaForm.arity);
    if (m != 0) {
      return lambdaForm;
    }
    Transform localTransform = Transform.of(LambdaFormEditor.Transform.Kind.PERMUTE_ARGS, paramArrayOfInt);
    LambdaForm localLambdaForm = getInCache(localTransform);
    if (localLambdaForm != null)
    {
      assert (arity == paramInt + k) : localLambdaForm;
      return localLambdaForm;
    }
    LambdaForm.BasicType[] arrayOfBasicType = new LambdaForm.BasicType[k];
    for (int i2 = 0; i2 < j; i2++)
    {
      int i3 = paramArrayOfInt[i2];
      arrayOfBasicType[i3] = lambdaForm.names[(paramInt + i2)].type;
    }
    assert (paramInt + j == lambdaForm.arity);
    assert (permutedTypesMatch(paramArrayOfInt, arrayOfBasicType, lambdaForm.names, paramInt));
    for (i2 = 0; (i2 < j) && (paramArrayOfInt[i2] == i2); i2++) {}
    LambdaForm.Name[] arrayOfName = new LambdaForm.Name[i - j + k];
    System.arraycopy(lambdaForm.names, 0, arrayOfName, 0, paramInt + i2);
    int i4 = i - lambdaForm.arity;
    System.arraycopy(lambdaForm.names, paramInt + j, arrayOfName, paramInt + k, i4);
    int i5 = arrayOfName.length - i4;
    int i6 = lambdaForm.result;
    if (i6 >= paramInt) {
      if (i6 < paramInt + j) {
        i6 = paramArrayOfInt[(i6 - paramInt)] + paramInt;
      } else {
        i6 = i6 - j + k;
      }
    }
    LambdaForm.Name localName3;
    int i10;
    for (int i7 = i2; i7 < j; i7++)
    {
      LambdaForm.Name localName1 = lambdaForm.names[(paramInt + i7)];
      int i9 = paramArrayOfInt[i7];
      localName3 = arrayOfName[(paramInt + i9)];
      if (localName3 == null) {
        arrayOfName[(paramInt + i9)] = (localName3 = new LambdaForm.Name(arrayOfBasicType[i9]));
      } else {
        assert (type == arrayOfBasicType[i9]);
      }
      for (i10 = i5; i10 < arrayOfName.length; i10++) {
        arrayOfName[i10] = arrayOfName[i10].replaceName(localName1, localName3);
      }
    }
    for (i7 = paramInt + i2; i7 < i5; i7++) {
      if (arrayOfName[i7] == null) {
        arrayOfName[i7] = LambdaForm.argument(i7, arrayOfBasicType[(i7 - paramInt)]);
      }
    }
    for (i7 = lambdaForm.arity; i7 < lambdaForm.names.length; i7++)
    {
      int i8 = i7 - lambdaForm.arity + i5;
      LambdaForm.Name localName2 = lambdaForm.names[i7];
      localName3 = arrayOfName[i8];
      if (localName2 != localName3) {
        for (i10 = i8 + 1; i10 < arrayOfName.length; i10++) {
          arrayOfName[i10] = arrayOfName[i10].replaceName(localName2, localName3);
        }
      }
    }
    localLambdaForm = new LambdaForm(lambdaForm.debugName, i5, arrayOfName, i6);
    return putInCache(localTransform, localLambdaForm);
  }
  
  static boolean permutedTypesMatch(int[] paramArrayOfInt, LambdaForm.BasicType[] paramArrayOfBasicType, LambdaForm.Name[] paramArrayOfName, int paramInt)
  {
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      assert (paramArrayOfName[(paramInt + i)].isParam());
      assert (type == paramArrayOfBasicType[paramArrayOfInt[i]]);
    }
    return true;
  }
  
  private static final class Transform
    extends SoftReference<LambdaForm>
  {
    final long packedBytes;
    final byte[] fullBytes;
    private static final boolean STRESS_TEST = false;
    private static final int PACKED_BYTE_SIZE = 4;
    private static final int PACKED_BYTE_MASK = 15;
    private static final int PACKED_BYTE_MAX_LENGTH = 16;
    private static final byte[] NO_BYTES = new byte[0];
    
    private static long packedBytes(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length > 16) {
        return 0L;
      }
      long l = 0L;
      int i = 0;
      for (int j = 0; j < paramArrayOfByte.length; j++)
      {
        int k = paramArrayOfByte[j] & 0xFF;
        i |= k;
        l |= k << j * 4;
      }
      if (!inRange(i)) {
        return 0L;
      }
      return l;
    }
    
    private static long packedBytes(int paramInt1, int paramInt2)
    {
      assert (inRange(paramInt1 | paramInt2));
      return paramInt1 << 0 | paramInt2 << 4;
    }
    
    private static long packedBytes(int paramInt1, int paramInt2, int paramInt3)
    {
      assert (inRange(paramInt1 | paramInt2 | paramInt3));
      return paramInt1 << 0 | paramInt2 << 4 | paramInt3 << 8;
    }
    
    private static long packedBytes(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      assert (inRange(paramInt1 | paramInt2 | paramInt3 | paramInt4));
      return paramInt1 << 0 | paramInt2 << 4 | paramInt3 << 8 | paramInt4 << 12;
    }
    
    private static boolean inRange(int paramInt)
    {
      assert ((paramInt & 0xFF) == paramInt);
      return (paramInt & 0xFFFFFFF0) == 0;
    }
    
    private static byte[] fullBytes(int... paramVarArgs)
    {
      byte[] arrayOfByte = new byte[paramVarArgs.length];
      int i = 0;
      for (int m : paramVarArgs) {
        arrayOfByte[(i++)] = bval(m);
      }
      assert (packedBytes(arrayOfByte) == 0L);
      return arrayOfByte;
    }
    
    private byte byteAt(int paramInt)
    {
      long l = packedBytes;
      if (l == 0L)
      {
        if (paramInt >= fullBytes.length) {
          return 0;
        }
        return fullBytes[paramInt];
      }
      assert (fullBytes == null);
      if (paramInt > 16) {
        return 0;
      }
      int i = paramInt * 4;
      return (byte)(int)(l >>> i & 0xF);
    }
    
    Kind kind()
    {
      return Kind.values()[byteAt(0)];
    }
    
    private Transform(long paramLong, byte[] paramArrayOfByte, LambdaForm paramLambdaForm)
    {
      super();
      packedBytes = paramLong;
      fullBytes = paramArrayOfByte;
    }
    
    private Transform(long paramLong)
    {
      this(paramLong, null, null);
      assert (paramLong != 0L);
    }
    
    private Transform(byte[] paramArrayOfByte)
    {
      this(0L, paramArrayOfByte, null);
    }
    
    private static byte bval(int paramInt)
    {
      assert ((paramInt & 0xFF) == paramInt);
      return (byte)paramInt;
    }
    
    private static byte bval(Kind paramKind)
    {
      return bval(paramKind.ordinal());
    }
    
    static Transform of(Kind paramKind, int paramInt)
    {
      int i = bval(paramKind);
      if (inRange(i | paramInt)) {
        return new Transform(packedBytes(i, paramInt));
      }
      return new Transform(fullBytes(new int[] { i, paramInt }));
    }
    
    static Transform of(Kind paramKind, int paramInt1, int paramInt2)
    {
      int i = (byte)paramKind.ordinal();
      if (inRange(i | paramInt1 | paramInt2)) {
        return new Transform(packedBytes(i, paramInt1, paramInt2));
      }
      return new Transform(fullBytes(new int[] { i, paramInt1, paramInt2 }));
    }
    
    static Transform of(Kind paramKind, int paramInt1, int paramInt2, int paramInt3)
    {
      int i = (byte)paramKind.ordinal();
      if (inRange(i | paramInt1 | paramInt2 | paramInt3)) {
        return new Transform(packedBytes(i, paramInt1, paramInt2, paramInt3));
      }
      return new Transform(fullBytes(new int[] { i, paramInt1, paramInt2, paramInt3 }));
    }
    
    static Transform of(Kind paramKind, int... paramVarArgs)
    {
      return ofBothArrays(paramKind, paramVarArgs, NO_BYTES);
    }
    
    static Transform of(Kind paramKind, int paramInt, byte[] paramArrayOfByte)
    {
      return ofBothArrays(paramKind, new int[] { paramInt }, paramArrayOfByte);
    }
    
    static Transform of(Kind paramKind, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      return ofBothArrays(paramKind, new int[] { paramInt1, paramInt2 }, paramArrayOfByte);
    }
    
    private static Transform ofBothArrays(Kind paramKind, int[] paramArrayOfInt, byte[] paramArrayOfByte)
    {
      byte[] arrayOfByte = new byte[1 + paramArrayOfInt.length + paramArrayOfByte.length];
      int i = 0;
      arrayOfByte[(i++)] = bval(paramKind);
      int m;
      for (m : paramArrayOfInt) {
        arrayOfByte[(i++)] = bval(m);
      }
      for (m : paramArrayOfByte) {
        arrayOfByte[(i++)] = m;
      }
      long l = packedBytes(arrayOfByte);
      if (l != 0L) {
        return new Transform(l);
      }
      return new Transform(arrayOfByte);
    }
    
    Transform withResult(LambdaForm paramLambdaForm)
    {
      return new Transform(packedBytes, fullBytes, paramLambdaForm);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Transform)) && (equals((Transform)paramObject));
    }
    
    public boolean equals(Transform paramTransform)
    {
      return (packedBytes == packedBytes) && (Arrays.equals(fullBytes, fullBytes));
    }
    
    public int hashCode()
    {
      if (packedBytes != 0L)
      {
        assert (fullBytes == null);
        return Long.hashCode(packedBytes);
      }
      return Arrays.hashCode(fullBytes);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      long l = packedBytes;
      if (l != 0L)
      {
        localStringBuilder.append("(");
        while (l != 0L)
        {
          localStringBuilder.append(l & 0xF);
          l >>>= 4;
          if (l != 0L) {
            localStringBuilder.append(",");
          }
        }
        localStringBuilder.append(")");
      }
      if (fullBytes != null)
      {
        localStringBuilder.append("unpacked");
        localStringBuilder.append(Arrays.toString(fullBytes));
      }
      LambdaForm localLambdaForm = (LambdaForm)get();
      if (localLambdaForm != null)
      {
        localStringBuilder.append(" result=");
        localStringBuilder.append(localLambdaForm);
      }
      return localStringBuilder.toString();
    }
    
    private static enum Kind
    {
      NO_KIND,  BIND_ARG,  ADD_ARG,  DUP_ARG,  SPREAD_ARGS,  FILTER_ARG,  FILTER_RETURN,  FILTER_RETURN_TO_ZERO,  COLLECT_ARGS,  COLLECT_ARGS_TO_VOID,  COLLECT_ARGS_TO_ARRAY,  FOLD_ARGS,  FOLD_ARGS_TO_VOID,  PERMUTE_ARGS;
      
      private Kind() {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\LambdaFormEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */