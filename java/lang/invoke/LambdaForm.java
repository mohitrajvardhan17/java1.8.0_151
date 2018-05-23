package java.lang.invoke;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import sun.invoke.util.Wrapper;

class LambdaForm
{
  final int arity;
  final int result;
  final boolean forceInline;
  final MethodHandle customized;
  @Stable
  final Name[] names;
  final String debugName;
  MemberName vmentry;
  private boolean isCompiled;
  volatile Object transformCache;
  public static final int VOID_RESULT = -1;
  public static final int LAST_RESULT = -2;
  private static final boolean USE_PREDEFINED_INTERPRET_METHODS = true;
  private static final int COMPILE_THRESHOLD;
  private int invocationCounter = 0;
  static final int INTERNED_ARGUMENT_LIMIT = 10;
  private static final Name[][] INTERNED_ARGUMENTS;
  private static final MemberName.Factory IMPL_NAMES;
  private static final LambdaForm[] LF_identityForm;
  private static final LambdaForm[] LF_zeroForm;
  private static final NamedFunction[] NF_identity;
  private static final NamedFunction[] NF_zero;
  private static final HashMap<String, Integer> DEBUG_NAME_COUNTERS;
  private static final boolean TRACE_INTERPRETER = MethodHandleStatics.TRACE_INTERPRETER;
  
  LambdaForm(String paramString, int paramInt1, Name[] paramArrayOfName, int paramInt2)
  {
    this(paramString, paramInt1, paramArrayOfName, paramInt2, true, null);
  }
  
  LambdaForm(String paramString, int paramInt1, Name[] paramArrayOfName, int paramInt2, boolean paramBoolean, MethodHandle paramMethodHandle)
  {
    assert (namesOK(paramInt1, paramArrayOfName));
    arity = paramInt1;
    result = fixResult(paramInt2, paramArrayOfName);
    names = ((Name[])paramArrayOfName.clone());
    debugName = fixDebugName(paramString);
    forceInline = paramBoolean;
    customized = paramMethodHandle;
    int i = normalize();
    if (i > 253)
    {
      assert (i <= 255);
      compileToBytecode();
    }
  }
  
  LambdaForm(String paramString, int paramInt, Name[] paramArrayOfName)
  {
    this(paramString, paramInt, paramArrayOfName, -2, true, null);
  }
  
  LambdaForm(String paramString, int paramInt, Name[] paramArrayOfName, boolean paramBoolean)
  {
    this(paramString, paramInt, paramArrayOfName, -2, paramBoolean, null);
  }
  
  LambdaForm(String paramString, Name[] paramArrayOfName1, Name[] paramArrayOfName2, Name paramName)
  {
    this(paramString, paramArrayOfName1.length, buildNames(paramArrayOfName1, paramArrayOfName2, paramName), -2, true, null);
  }
  
  LambdaForm(String paramString, Name[] paramArrayOfName1, Name[] paramArrayOfName2, Name paramName, boolean paramBoolean)
  {
    this(paramString, paramArrayOfName1.length, buildNames(paramArrayOfName1, paramArrayOfName2, paramName), -2, paramBoolean, null);
  }
  
  private static Name[] buildNames(Name[] paramArrayOfName1, Name[] paramArrayOfName2, Name paramName)
  {
    int i = paramArrayOfName1.length;
    int j = i + paramArrayOfName2.length + (paramName == null ? 0 : 1);
    Name[] arrayOfName = (Name[])Arrays.copyOf(paramArrayOfName1, j);
    System.arraycopy(paramArrayOfName2, 0, arrayOfName, i, paramArrayOfName2.length);
    if (paramName != null) {
      arrayOfName[(j - 1)] = paramName;
    }
    return arrayOfName;
  }
  
  private LambdaForm(String paramString)
  {
    assert (isValidSignature(paramString));
    arity = signatureArity(paramString);
    result = (signatureReturn(paramString) == BasicType.V_TYPE ? -1 : arity);
    names = buildEmptyNames(arity, paramString);
    debugName = "LF.zero";
    forceInline = true;
    customized = null;
    assert (nameRefsAreLegal());
    assert (isEmpty());
    assert (paramString.equals(basicTypeSignature())) : (paramString + " != " + basicTypeSignature());
  }
  
  private static Name[] buildEmptyNames(int paramInt, String paramString)
  {
    assert (isValidSignature(paramString));
    int i = paramInt + 1;
    if ((paramInt < 0) || (paramString.length() != i + 1)) {
      throw new IllegalArgumentException("bad arity for " + paramString);
    }
    int j = BasicType.basicType(paramString.charAt(i)) == BasicType.V_TYPE ? 0 : 1;
    Name[] arrayOfName = arguments(j, paramString.substring(0, paramInt));
    for (int k = 0; k < j; k++)
    {
      Name localName = new Name(constantZero(BasicType.basicType(paramString.charAt(i + k))), new Object[0]);
      arrayOfName[(paramInt + k)] = localName.newIndex(paramInt + k);
    }
    return arrayOfName;
  }
  
  private static int fixResult(int paramInt, Name[] paramArrayOfName)
  {
    if (paramInt == -2) {
      paramInt = paramArrayOfName.length - 1;
    }
    if ((paramInt >= 0) && (type == BasicType.V_TYPE)) {
      paramInt = -1;
    }
    return paramInt;
  }
  
  private static String fixDebugName(String paramString)
  {
    if (DEBUG_NAME_COUNTERS != null)
    {
      int i = paramString.indexOf('_');
      int j = paramString.length();
      if (i < 0) {
        i = j;
      }
      String str = paramString.substring(0, i);
      Integer localInteger;
      synchronized (DEBUG_NAME_COUNTERS)
      {
        localInteger = (Integer)DEBUG_NAME_COUNTERS.get(str);
        if (localInteger == null) {
          localInteger = Integer.valueOf(0);
        }
        DEBUG_NAME_COUNTERS.put(str, Integer.valueOf(localInteger.intValue() + 1));
      }
      ??? = new StringBuilder(str);
      ((StringBuilder)???).append('_');
      int k = ((StringBuilder)???).length();
      ((StringBuilder)???).append(localInteger.intValue());
      for (int m = ((StringBuilder)???).length() - k; m < 3; m++) {
        ((StringBuilder)???).insert(k, '0');
      }
      if (i < j)
      {
        i++;
        while ((i < j) && (Character.isDigit(paramString.charAt(i)))) {
          i++;
        }
        if ((i < j) && (paramString.charAt(i) == '_')) {
          i++;
        }
        if (i < j) {
          ((StringBuilder)???).append('_').append(paramString, i, j);
        }
      }
      return ((StringBuilder)???).toString();
    }
    return paramString;
  }
  
  private static boolean namesOK(int paramInt, Name[] paramArrayOfName)
  {
    for (int i = 0; i < paramArrayOfName.length; i++)
    {
      Name localName = paramArrayOfName[i];
      assert (localName != null) : "n is null";
      if (i < paramInt)
      {
        if ((!$assertionsDisabled) && (!localName.isParam())) {
          throw new AssertionError(localName + " is not param at " + i);
        }
      }
      else {
        assert (!localName.isParam()) : (localName + " is param at " + i);
      }
    }
    return true;
  }
  
  LambdaForm customize(MethodHandle paramMethodHandle)
  {
    LambdaForm localLambdaForm = new LambdaForm(debugName, arity, names, result, forceInline, paramMethodHandle);
    if ((COMPILE_THRESHOLD > 0) && (isCompiled)) {
      localLambdaForm.compileToBytecode();
    }
    transformCache = this;
    return localLambdaForm;
  }
  
  LambdaForm uncustomize()
  {
    if (customized == null) {
      return this;
    }
    assert (transformCache != null);
    LambdaForm localLambdaForm = (LambdaForm)transformCache;
    if ((COMPILE_THRESHOLD > 0) && (isCompiled)) {
      localLambdaForm.compileToBytecode();
    }
    return localLambdaForm;
  }
  
  private int normalize()
  {
    Name[] arrayOfName = null;
    int i = 0;
    int j = 0;
    Name localName2;
    for (Name localName1 = 0; localName1 < names.length; localName1++)
    {
      localName2 = names[localName1];
      if (!localName2.initIndex(localName1))
      {
        if (arrayOfName == null)
        {
          arrayOfName = (Name[])names.clone();
          j = localName1;
        }
        names[localName1] = localName2.cloneWithIndex(localName1);
      }
      if ((arguments != null) && (i < arguments.length)) {
        i = arguments.length;
      }
    }
    if (arrayOfName != null)
    {
      localName1 = arity;
      if (localName1 <= j) {
        localName1 = j + 1;
      }
      for (localName2 = localName1; localName2 < names.length; localName2++)
      {
        Name localName3 = names[localName2].replaceNames(arrayOfName, names, j, localName2);
        names[localName2] = localName3.newIndex(localName2);
      }
    }
    assert (nameRefsAreLegal());
    localName1 = Math.min(arity, 10);
    int k = 0;
    for (int m = 0; m < localName1; m++)
    {
      Name localName4 = names[m];
      Name localName5 = internArgument(localName4);
      if (localName4 != localName5)
      {
        names[m] = localName5;
        k = 1;
      }
    }
    if (k != 0) {
      for (m = arity; m < names.length; m++) {
        names[m].internArguments();
      }
    }
    assert (nameRefsAreLegal());
    return i;
  }
  
  boolean nameRefsAreLegal()
  {
    assert ((arity >= 0) && (arity <= names.length));
    assert ((result >= -1) && (result < names.length));
    Name localName1;
    for (int i = 0; i < arity; i++)
    {
      localName1 = names[i];
      if ((!$assertionsDisabled) && (localName1.index() != i)) {
        throw new AssertionError(Arrays.asList(new Integer[] { Integer.valueOf(localName1.index()), Integer.valueOf(i) }));
      }
      assert (localName1.isParam());
    }
    for (i = arity; i < names.length; i++)
    {
      localName1 = names[i];
      assert (localName1.index() == i);
      for (Object localObject : arguments) {
        if ((localObject instanceof Name))
        {
          Name localName2 = (Name)localObject;
          int m = index;
          assert ((0 <= m) && (m < names.length)) : (localName1.debugString() + ": 0 <= i2 && i2 < names.length: 0 <= " + m + " < " + names.length);
          if ((!$assertionsDisabled) && (names[m] != localName2)) {
            throw new AssertionError(Arrays.asList(new Object[] { "-1-", Integer.valueOf(i), "-2-", localName1.debugString(), "-3-", Integer.valueOf(m), "-4-", localName2.debugString(), "-5-", names[m].debugString(), "-6-", this }));
          }
          assert (m < i);
        }
      }
    }
    return true;
  }
  
  BasicType returnType()
  {
    if (result < 0) {
      return BasicType.V_TYPE;
    }
    Name localName = names[result];
    return type;
  }
  
  BasicType parameterType(int paramInt)
  {
    return parametertype;
  }
  
  Name parameter(int paramInt)
  {
    assert (paramInt < arity);
    Name localName = names[paramInt];
    assert (localName.isParam());
    return localName;
  }
  
  Object parameterConstraint(int paramInt)
  {
    return parameterconstraint;
  }
  
  int arity()
  {
    return arity;
  }
  
  int expressionCount()
  {
    return names.length - arity;
  }
  
  MethodType methodType()
  {
    return signatureType(basicTypeSignature());
  }
  
  final String basicTypeSignature()
  {
    StringBuilder localStringBuilder = new StringBuilder(arity() + 3);
    int i = 0;
    int j = arity();
    while (i < j)
    {
      localStringBuilder.append(parameterType(i).basicTypeChar());
      i++;
    }
    return '_' + returnType().basicTypeChar();
  }
  
  static int signatureArity(String paramString)
  {
    assert (isValidSignature(paramString));
    return paramString.indexOf('_');
  }
  
  static BasicType signatureReturn(String paramString)
  {
    return BasicType.basicType(paramString.charAt(signatureArity(paramString) + 1));
  }
  
  static boolean isValidSignature(String paramString)
  {
    int i = paramString.indexOf('_');
    if (i < 0) {
      return false;
    }
    int j = paramString.length();
    if (j != i + 2) {
      return false;
    }
    for (int k = 0; k < j; k++) {
      if (k != i)
      {
        char c = paramString.charAt(k);
        if (c == 'V') {
          return (k == j - 1) && (i == j - 2);
        }
        if (!BasicType.isArgBasicTypeChar(c)) {
          return false;
        }
      }
    }
    return true;
  }
  
  static MethodType signatureType(String paramString)
  {
    Class[] arrayOfClass = new Class[signatureArity(paramString)];
    for (int i = 0; i < arrayOfClass.length; i++) {
      arrayOfClass[i] = basicTypecharAtbtClass;
    }
    Class localClass = signatureReturnbtClass;
    return MethodType.methodType(localClass, arrayOfClass);
  }
  
  public void prepare()
  {
    if ((COMPILE_THRESHOLD == 0) && (!isCompiled)) {
      compileToBytecode();
    }
    if (vmentry != null) {
      return;
    }
    LambdaForm localLambdaForm = getPreparedForm(basicTypeSignature());
    vmentry = vmentry;
  }
  
  MemberName compileToBytecode()
  {
    if ((vmentry != null) && (isCompiled)) {
      return vmentry;
    }
    MethodType localMethodType = methodType();
    assert ((vmentry == null) || (vmentry.getMethodType().basicType().equals(localMethodType)));
    try
    {
      vmentry = InvokerBytecodeGenerator.generateCustomizedCode(this, localMethodType);
      if (TRACE_INTERPRETER) {
        traceInterpreter("compileToBytecode", this);
      }
      isCompiled = true;
      return vmentry;
    }
    catch (Error|Exception localError)
    {
      throw MethodHandleStatics.newInternalError(toString(), localError);
    }
  }
  
  private static void computeInitialPreparedForms()
  {
    Iterator localIterator = MemberName.getFactory().getMethods(LambdaForm.class, false, null, null, null).iterator();
    while (localIterator.hasNext())
    {
      MemberName localMemberName = (MemberName)localIterator.next();
      if ((localMemberName.isStatic()) && (localMemberName.isPackage()))
      {
        MethodType localMethodType = localMemberName.getMethodType();
        if ((localMethodType.parameterCount() > 0) && (localMethodType.parameterType(0) == MethodHandle.class) && (localMemberName.getName().startsWith("interpret_")))
        {
          String str = basicTypeSignature(localMethodType);
          assert (localMemberName.getName().equals("interpret" + str.substring(str.indexOf('_'))));
          LambdaForm localLambdaForm = new LambdaForm(str);
          vmentry = localMemberName;
          localLambdaForm = localMethodType.form().setCachedLambdaForm(6, localLambdaForm);
        }
      }
    }
  }
  
  static Object interpret_L(MethodHandle paramMethodHandle)
    throws Throwable
  {
    Object[] arrayOfObject = { paramMethodHandle };
    String str = null;
    assert (argumentTypesMatch(str = "L_L", arrayOfObject));
    Object localObject = form.interpretWithArguments(arrayOfObject);
    assert (returnTypesMatch(str, arrayOfObject, localObject));
    return localObject;
  }
  
  static Object interpret_L(MethodHandle paramMethodHandle, Object paramObject)
    throws Throwable
  {
    Object[] arrayOfObject = { paramMethodHandle, paramObject };
    String str = null;
    assert (argumentTypesMatch(str = "LL_L", arrayOfObject));
    Object localObject = form.interpretWithArguments(arrayOfObject);
    assert (returnTypesMatch(str, arrayOfObject, localObject));
    return localObject;
  }
  
  static Object interpret_L(MethodHandle paramMethodHandle, Object paramObject1, Object paramObject2)
    throws Throwable
  {
    Object[] arrayOfObject = { paramMethodHandle, paramObject1, paramObject2 };
    String str = null;
    assert (argumentTypesMatch(str = "LLL_L", arrayOfObject));
    Object localObject = form.interpretWithArguments(arrayOfObject);
    assert (returnTypesMatch(str, arrayOfObject, localObject));
    return localObject;
  }
  
  private static LambdaForm getPreparedForm(String paramString)
  {
    MethodType localMethodType = signatureType(paramString);
    LambdaForm localLambdaForm = localMethodType.form().cachedLambdaForm(6);
    if (localLambdaForm != null) {
      return localLambdaForm;
    }
    assert (isValidSignature(paramString));
    localLambdaForm = new LambdaForm(paramString);
    vmentry = InvokerBytecodeGenerator.generateLambdaFormInterpreterEntryPoint(paramString);
    return localMethodType.form().setCachedLambdaForm(6, localLambdaForm);
  }
  
  private static boolean argumentTypesMatch(String paramString, Object[] paramArrayOfObject)
  {
    int i = signatureArity(paramString);
    assert (paramArrayOfObject.length == i) : ("av.length == arity: av.length=" + paramArrayOfObject.length + ", arity=" + i);
    assert ((paramArrayOfObject[0] instanceof MethodHandle)) : ("av[0] not instace of MethodHandle: " + paramArrayOfObject[0]);
    MethodHandle localMethodHandle = (MethodHandle)paramArrayOfObject[0];
    MethodType localMethodType = localMethodHandle.type();
    assert (localMethodType.parameterCount() == i - 1);
    for (int j = 0; j < paramArrayOfObject.length; j++)
    {
      Class localClass = j == 0 ? MethodHandle.class : localMethodType.parameterType(j - 1);
      assert (valueMatches(BasicType.basicType(paramString.charAt(j)), localClass, paramArrayOfObject[j]));
    }
    return true;
  }
  
  private static boolean valueMatches(BasicType paramBasicType, Class<?> paramClass, Object paramObject)
  {
    if (paramClass == Void.TYPE) {
      paramBasicType = BasicType.V_TYPE;
    }
    assert (paramBasicType == BasicType.basicType(paramClass)) : (paramBasicType + " == basicType(" + paramClass + ")=" + BasicType.basicType(paramClass));
    switch (paramBasicType)
    {
    case I_TYPE: 
      if ((!$assertionsDisabled) && (!checkInt(paramClass, paramObject))) {
        throw new AssertionError("checkInt(" + paramClass + "," + paramObject + ")");
      }
      break;
    case J_TYPE: 
      if ((!$assertionsDisabled) && (!(paramObject instanceof Long))) {
        throw new AssertionError("instanceof Long: " + paramObject);
      }
      break;
    case F_TYPE: 
      if ((!$assertionsDisabled) && (!(paramObject instanceof Float))) {
        throw new AssertionError("instanceof Float: " + paramObject);
      }
      break;
    case D_TYPE: 
      if ((!$assertionsDisabled) && (!(paramObject instanceof Double))) {
        throw new AssertionError("instanceof Double: " + paramObject);
      }
      break;
    case L_TYPE: 
      if ((!$assertionsDisabled) && (!checkRef(paramClass, paramObject))) {
        throw new AssertionError("checkRef(" + paramClass + "," + paramObject + ")");
      }
      break;
    case V_TYPE: 
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    return true;
  }
  
  private static boolean returnTypesMatch(String paramString, Object[] paramArrayOfObject, Object paramObject)
  {
    MethodHandle localMethodHandle = (MethodHandle)paramArrayOfObject[0];
    return valueMatches(signatureReturn(paramString), localMethodHandle.type().returnType(), paramObject);
  }
  
  private static boolean checkInt(Class<?> paramClass, Object paramObject)
  {
    assert ((paramObject instanceof Integer));
    if (paramClass == Integer.TYPE) {
      return true;
    }
    Wrapper localWrapper = Wrapper.forBasicType(paramClass);
    assert (localWrapper.isSubwordOrInt());
    Object localObject = Wrapper.INT.wrap(localWrapper.wrap(paramObject));
    return paramObject.equals(localObject);
  }
  
  private static boolean checkRef(Class<?> paramClass, Object paramObject)
  {
    assert (!paramClass.isPrimitive());
    if (paramObject == null) {
      return true;
    }
    if (paramClass.isInterface()) {
      return true;
    }
    return paramClass.isInstance(paramObject);
  }
  
  @Hidden
  @DontInline
  Object interpretWithArguments(Object... paramVarArgs)
    throws Throwable
  {
    if (TRACE_INTERPRETER) {
      return interpretWithArgumentsTracing(paramVarArgs);
    }
    checkInvocationCounter();
    assert (arityCheck(paramVarArgs));
    Object[] arrayOfObject = Arrays.copyOf(paramVarArgs, names.length);
    for (int i = paramVarArgs.length; i < arrayOfObject.length; i++) {
      arrayOfObject[i] = interpretName(names[i], arrayOfObject);
    }
    Object localObject = result < 0 ? null : arrayOfObject[result];
    assert (resultCheck(paramVarArgs, localObject));
    return localObject;
  }
  
  @Hidden
  @DontInline
  Object interpretName(Name paramName, Object[] paramArrayOfObject)
    throws Throwable
  {
    if (TRACE_INTERPRETER) {
      traceInterpreter("| interpretName", paramName.debugString(), (Object[])null);
    }
    Object[] arrayOfObject = Arrays.copyOf(arguments, arguments.length, Object[].class);
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      Object localObject = arrayOfObject[i];
      if ((localObject instanceof Name))
      {
        int j = ((Name)localObject).index();
        assert (names[j] == localObject);
        localObject = paramArrayOfObject[j];
        arrayOfObject[i] = localObject;
      }
    }
    return function.invokeWithArguments(arrayOfObject);
  }
  
  private void checkInvocationCounter()
  {
    if ((COMPILE_THRESHOLD != 0) && (invocationCounter < COMPILE_THRESHOLD))
    {
      invocationCounter += 1;
      if (invocationCounter >= COMPILE_THRESHOLD) {
        compileToBytecode();
      }
    }
  }
  
  Object interpretWithArgumentsTracing(Object... paramVarArgs)
    throws Throwable
  {
    traceInterpreter("[ interpretWithArguments", this, paramVarArgs);
    if (invocationCounter < COMPILE_THRESHOLD)
    {
      int i = invocationCounter++;
      traceInterpreter("| invocationCounter", Integer.valueOf(i));
      if (invocationCounter >= COMPILE_THRESHOLD) {
        compileToBytecode();
      }
    }
    Object localObject;
    try
    {
      assert (arityCheck(paramVarArgs));
      Object[] arrayOfObject = Arrays.copyOf(paramVarArgs, names.length);
      for (int j = paramVarArgs.length; j < arrayOfObject.length; j++) {
        arrayOfObject[j] = interpretName(names[j], arrayOfObject);
      }
      localObject = result < 0 ? null : arrayOfObject[result];
    }
    catch (Throwable localThrowable)
    {
      traceInterpreter("] throw =>", localThrowable);
      throw localThrowable;
    }
    traceInterpreter("] return =>", localObject);
    return localObject;
  }
  
  static void traceInterpreter(String paramString, Object paramObject, Object... paramVarArgs)
  {
    if (TRACE_INTERPRETER) {
      System.out.println("LFI: " + paramString + " " + (paramObject != null ? paramObject : "") + ((paramVarArgs != null) && (paramVarArgs.length != 0) ? Arrays.asList(paramVarArgs) : ""));
    }
  }
  
  static void traceInterpreter(String paramString, Object paramObject)
  {
    traceInterpreter(paramString, paramObject, (Object[])null);
  }
  
  private boolean arityCheck(Object[] paramArrayOfObject)
  {
    assert (paramArrayOfObject.length == arity) : (arity + "!=" + Arrays.asList(paramArrayOfObject) + ".length");
    assert ((paramArrayOfObject[0] instanceof MethodHandle)) : ("not MH: " + paramArrayOfObject[0]);
    MethodHandle localMethodHandle = (MethodHandle)paramArrayOfObject[0];
    assert (localMethodHandle.internalForm() == this);
    argumentTypesMatch(basicTypeSignature(), paramArrayOfObject);
    return true;
  }
  
  private boolean resultCheck(Object[] paramArrayOfObject, Object paramObject)
  {
    MethodHandle localMethodHandle = (MethodHandle)paramArrayOfObject[0];
    MethodType localMethodType = localMethodHandle.type();
    assert (valueMatches(returnType(), localMethodType.returnType(), paramObject));
    return true;
  }
  
  private boolean isEmpty()
  {
    if (result < 0) {
      return names.length == arity;
    }
    if ((result == arity) && (names.length == arity + 1)) {
      return names[arity].isConstantZero();
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(debugName + "=Lambda(");
    for (int i = 0; i < names.length; i++)
    {
      if (i == arity) {
        localStringBuilder.append(")=>{");
      }
      Name localName = names[i];
      if (i >= arity) {
        localStringBuilder.append("\n    ");
      }
      localStringBuilder.append(localName.paramString());
      if (i < arity)
      {
        if (i + 1 < arity) {
          localStringBuilder.append(",");
        }
      }
      else
      {
        localStringBuilder.append("=").append(localName.exprString());
        localStringBuilder.append(";");
      }
    }
    if (arity == names.length) {
      localStringBuilder.append(")=>{");
    }
    localStringBuilder.append(result < 0 ? "void" : names[result]).append("}");
    if (TRACE_INTERPRETER)
    {
      localStringBuilder.append(":").append(basicTypeSignature());
      localStringBuilder.append("/").append(vmentry);
    }
    return localStringBuilder.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof LambdaForm)) && (equals((LambdaForm)paramObject));
  }
  
  public boolean equals(LambdaForm paramLambdaForm)
  {
    if (result != result) {
      return false;
    }
    return Arrays.equals(names, names);
  }
  
  public int hashCode()
  {
    return result + 31 * Arrays.hashCode(names);
  }
  
  LambdaFormEditor editor()
  {
    return LambdaFormEditor.lambdaFormEditor(this);
  }
  
  boolean contains(Name paramName)
  {
    int i = paramName.index();
    if (i >= 0) {
      return (i < names.length) && (paramName.equals(names[i]));
    }
    for (int j = arity; j < names.length; j++) {
      if (paramName.equals(names[j])) {
        return true;
      }
    }
    return false;
  }
  
  LambdaForm addArguments(int paramInt, BasicType... paramVarArgs)
  {
    int i = paramInt + 1;
    assert (i <= arity);
    int j = names.length;
    int k = paramVarArgs.length;
    Name[] arrayOfName = (Name[])Arrays.copyOf(names, j + k);
    int m = arity + k;
    int n = result;
    if (n >= i) {
      n += k;
    }
    System.arraycopy(names, i, arrayOfName, i + k, j - i);
    for (int i1 = 0; i1 < k; i1++) {
      arrayOfName[(i + i1)] = new Name(paramVarArgs[i1]);
    }
    return new LambdaForm(debugName, m, arrayOfName, n);
  }
  
  LambdaForm addArguments(int paramInt, List<Class<?>> paramList)
  {
    return addArguments(paramInt, BasicType.basicTypes(paramList));
  }
  
  LambdaForm permuteArguments(int paramInt, int[] paramArrayOfInt, BasicType[] paramArrayOfBasicType)
  {
    int i = names.length;
    int j = paramArrayOfBasicType.length;
    int k = paramArrayOfInt.length;
    assert (paramInt + k == arity);
    assert (permutedTypesMatch(paramArrayOfInt, paramArrayOfBasicType, names, paramInt));
    for (int m = 0; (m < k) && (paramArrayOfInt[m] == m); m++) {}
    Name[] arrayOfName = new Name[i - k + j];
    System.arraycopy(names, 0, arrayOfName, 0, paramInt + m);
    int n = i - arity;
    System.arraycopy(names, paramInt + k, arrayOfName, paramInt + j, n);
    int i1 = arrayOfName.length - n;
    int i2 = result;
    if (i2 >= 0) {
      if (i2 < paramInt + k) {
        i2 = paramArrayOfInt[(i2 - paramInt)];
      } else {
        i2 = i2 - k + j;
      }
    }
    Name localName3;
    int i6;
    for (int i3 = m; i3 < k; i3++)
    {
      Name localName1 = names[(paramInt + i3)];
      int i5 = paramArrayOfInt[i3];
      localName3 = arrayOfName[(paramInt + i5)];
      if (localName3 == null) {
        arrayOfName[(paramInt + i5)] = (localName3 = new Name(paramArrayOfBasicType[i5]));
      } else {
        assert (type == paramArrayOfBasicType[i5]);
      }
      for (i6 = i1; i6 < arrayOfName.length; i6++) {
        arrayOfName[i6] = arrayOfName[i6].replaceName(localName1, localName3);
      }
    }
    for (i3 = paramInt + m; i3 < i1; i3++) {
      if (arrayOfName[i3] == null) {
        arrayOfName[i3] = argument(i3, paramArrayOfBasicType[(i3 - paramInt)]);
      }
    }
    for (i3 = arity; i3 < names.length; i3++)
    {
      int i4 = i3 - arity + i1;
      Name localName2 = names[i3];
      localName3 = arrayOfName[i4];
      if (localName2 != localName3) {
        for (i6 = i4 + 1; i6 < arrayOfName.length; i6++) {
          arrayOfName[i6] = arrayOfName[i6].replaceName(localName2, localName3);
        }
      }
    }
    return new LambdaForm(debugName, i1, arrayOfName, i2);
  }
  
  static boolean permutedTypesMatch(int[] paramArrayOfInt, BasicType[] paramArrayOfBasicType, Name[] paramArrayOfName, int paramInt)
  {
    int i = paramArrayOfBasicType.length;
    int j = paramArrayOfInt.length;
    for (int k = 0; k < j; k++)
    {
      assert (paramArrayOfName[(paramInt + k)].isParam());
      assert (type == paramArrayOfBasicType[paramArrayOfInt[k]]);
    }
    return true;
  }
  
  public static String basicTypeSignature(MethodType paramMethodType)
  {
    char[] arrayOfChar = new char[paramMethodType.parameterCount() + 2];
    int i = 0;
    Iterator localIterator = paramMethodType.parameterList().iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      arrayOfChar[(i++)] = BasicType.basicTypeChar(localClass);
    }
    arrayOfChar[(i++)] = '_';
    arrayOfChar[(i++)] = BasicType.basicTypeChar(paramMethodType.returnType());
    assert (i == arrayOfChar.length);
    return String.valueOf(arrayOfChar);
  }
  
  public static String shortenSignature(String paramString)
  {
    int j = -1;
    int k = 0;
    StringBuilder localStringBuilder = null;
    int m = paramString.length();
    if (m < 3) {
      return paramString;
    }
    for (int n = 0; n <= m; n++)
    {
      int i = j;
      j = n == m ? '￿' : paramString.charAt(n);
      if (j == i)
      {
        k++;
      }
      else
      {
        int i1 = k;
        k = 1;
        if (i1 < 3)
        {
          if (localStringBuilder != null) {
            for (;;)
            {
              i1--;
              if (i1 < 0) {
                break;
              }
              localStringBuilder.append((char)i);
            }
          }
        }
        else
        {
          if (localStringBuilder == null) {
            localStringBuilder = new StringBuilder().append(paramString, 0, n - i1);
          }
          localStringBuilder.append((char)i).append(i1);
        }
      }
    }
    return localStringBuilder == null ? paramString : localStringBuilder.toString();
  }
  
  int lastUseIndex(Name paramName)
  {
    int i = index;
    int j = names.length;
    assert (names[i] == paramName);
    if (result == i) {
      return j;
    }
    int k = j;
    do
    {
      k--;
      if (k <= i) {
        break;
      }
    } while (names[k].lastUseIndex(paramName) < 0);
    return k;
    return -1;
  }
  
  int useCount(Name paramName)
  {
    int i = index;
    int j = names.length;
    int k = lastUseIndex(paramName);
    if (k < 0) {
      return 0;
    }
    int m = 0;
    if (k == j)
    {
      m++;
      k--;
    }
    int n = paramName.index() + 1;
    if (n < arity) {
      n = arity;
    }
    for (int i1 = n; i1 <= k; i1++) {
      m += names[i1].useCount(paramName);
    }
    return m;
  }
  
  static Name argument(int paramInt, char paramChar)
  {
    return argument(paramInt, BasicType.basicType(paramChar));
  }
  
  static Name argument(int paramInt, BasicType paramBasicType)
  {
    if (paramInt >= 10) {
      return new Name(paramInt, paramBasicType);
    }
    return INTERNED_ARGUMENTS[paramBasicType.ordinal()][paramInt];
  }
  
  static Name internArgument(Name paramName)
  {
    assert (paramName.isParam()) : ("not param: " + paramName);
    assert (index < 10);
    if (constraint != null) {
      return paramName;
    }
    return argument(index, type);
  }
  
  static Name[] arguments(int paramInt, String paramString)
  {
    int i = paramString.length();
    Name[] arrayOfName = new Name[i + paramInt];
    for (int j = 0; j < i; j++) {
      arrayOfName[j] = argument(j, paramString.charAt(j));
    }
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, char... paramVarArgs)
  {
    int i = paramVarArgs.length;
    Name[] arrayOfName = new Name[i + paramInt];
    for (int j = 0; j < i; j++) {
      arrayOfName[j] = argument(j, paramVarArgs[j]);
    }
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, List<Class<?>> paramList)
  {
    int i = paramList.size();
    Name[] arrayOfName = new Name[i + paramInt];
    for (int j = 0; j < i; j++) {
      arrayOfName[j] = argument(j, BasicType.basicType((Class)paramList.get(j)));
    }
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, Class<?>... paramVarArgs)
  {
    int i = paramVarArgs.length;
    Name[] arrayOfName = new Name[i + paramInt];
    for (int j = 0; j < i; j++) {
      arrayOfName[j] = argument(j, BasicType.basicType(paramVarArgs[j]));
    }
    return arrayOfName;
  }
  
  static Name[] arguments(int paramInt, MethodType paramMethodType)
  {
    int i = paramMethodType.parameterCount();
    Name[] arrayOfName = new Name[i + paramInt];
    for (int j = 0; j < i; j++) {
      arrayOfName[j] = argument(j, BasicType.basicType(paramMethodType.parameterType(j)));
    }
    return arrayOfName;
  }
  
  static LambdaForm identityForm(BasicType paramBasicType)
  {
    return LF_identityForm[paramBasicType.ordinal()];
  }
  
  static LambdaForm zeroForm(BasicType paramBasicType)
  {
    return LF_zeroForm[paramBasicType.ordinal()];
  }
  
  static NamedFunction identity(BasicType paramBasicType)
  {
    return NF_identity[paramBasicType.ordinal()];
  }
  
  static NamedFunction constantZero(BasicType paramBasicType)
  {
    return NF_zero[paramBasicType.ordinal()];
  }
  
  private static void createIdentityForms()
  {
    BasicType localBasicType;
    int k;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    MemberName localMemberName1;
    for (localBasicType : BasicType.ALL_TYPES)
    {
      k = localBasicType.ordinal();
      char c = localBasicType.basicTypeChar();
      int m = localBasicType == BasicType.V_TYPE ? 1 : 0;
      localObject1 = btClass;
      localObject2 = MethodType.methodType((Class)localObject1);
      localObject3 = m != 0 ? localObject2 : ((MethodType)localObject2).appendParameterTypes(new Class[] { localObject1 });
      localMemberName1 = new MemberName(LambdaForm.class, "identity_" + c, (MethodType)localObject3, (byte)6);
      MemberName localMemberName2 = new MemberName(LambdaForm.class, "zero_" + c, (MethodType)localObject2, (byte)6);
      try
      {
        localMemberName2 = IMPL_NAMES.resolveOrFail((byte)6, localMemberName2, null, NoSuchMethodException.class);
        localMemberName1 = IMPL_NAMES.resolveOrFail((byte)6, localMemberName1, null, NoSuchMethodException.class);
      }
      catch (IllegalAccessException|NoSuchMethodException localIllegalAccessException)
      {
        throw MethodHandleStatics.newInternalError(localIllegalAccessException);
      }
      NamedFunction localNamedFunction2 = new NamedFunction(localMemberName1);
      LambdaForm localLambdaForm2;
      if (m != 0)
      {
        localObject4 = new Name[] { argument(0, BasicType.L_TYPE) };
        localLambdaForm2 = new LambdaForm(localMemberName1.getName(), 1, (Name[])localObject4, -1);
      }
      else
      {
        localObject4 = new Name[] { argument(0, BasicType.L_TYPE), argument(1, localBasicType) };
        localLambdaForm2 = new LambdaForm(localMemberName1.getName(), 2, (Name[])localObject4, 1);
      }
      LF_identityForm[k] = localLambdaForm2;
      NF_identity[k] = localNamedFunction2;
      Object localObject4 = new NamedFunction(localMemberName2);
      LambdaForm localLambdaForm3;
      if (m != 0)
      {
        localLambdaForm3 = localLambdaForm2;
      }
      else
      {
        Object localObject5 = Wrapper.forBasicType(c).zero();
        Name[] arrayOfName = { argument(0, BasicType.L_TYPE), new Name(localNamedFunction2, new Object[] { localObject5 }) };
        localLambdaForm3 = new LambdaForm(localMemberName2.getName(), 1, arrayOfName, 1);
      }
      LF_zeroForm[k] = localLambdaForm3;
      NF_zero[k] = localObject4;
      assert (localNamedFunction2.isIdentity());
      assert (((NamedFunction)localObject4).isConstantZero());
      assert (new Name((NamedFunction)localObject4, new Object[0]).isConstantZero());
    }
    for (localBasicType : BasicType.ALL_TYPES)
    {
      k = localBasicType.ordinal();
      NamedFunction localNamedFunction1 = NF_identity[k];
      LambdaForm localLambdaForm1 = LF_identityForm[k];
      localObject1 = member;
      resolvedHandle = SimpleMethodHandle.make(((MemberName)localObject1).getInvocationType(), localLambdaForm1);
      localObject2 = NF_zero[k];
      localObject3 = LF_zeroForm[k];
      localMemberName1 = member;
      resolvedHandle = SimpleMethodHandle.make(localMemberName1.getInvocationType(), (LambdaForm)localObject3);
      assert (localNamedFunction1.isIdentity());
      assert (((NamedFunction)localObject2).isConstantZero());
      assert (new Name((NamedFunction)localObject2, new Object[0]).isConstantZero());
    }
  }
  
  private static int identity_I(int paramInt)
  {
    return paramInt;
  }
  
  private static long identity_J(long paramLong)
  {
    return paramLong;
  }
  
  private static float identity_F(float paramFloat)
  {
    return paramFloat;
  }
  
  private static double identity_D(double paramDouble)
  {
    return paramDouble;
  }
  
  private static Object identity_L(Object paramObject)
  {
    return paramObject;
  }
  
  private static void identity_V() {}
  
  private static int zero_I()
  {
    return 0;
  }
  
  private static long zero_J()
  {
    return 0L;
  }
  
  private static float zero_F()
  {
    return 0.0F;
  }
  
  private static double zero_D()
  {
    return 0.0D;
  }
  
  private static Object zero_L()
  {
    return null;
  }
  
  private static void zero_V() {}
  
  static
  {
    COMPILE_THRESHOLD = Math.max(-1, MethodHandleStatics.COMPILE_THRESHOLD);
    INTERNED_ARGUMENTS = new Name[BasicType.ARG_TYPE_LIMIT][10];
    for (BasicType localBasicType : BasicType.ARG_TYPES)
    {
      int k = localBasicType.ordinal();
      for (int m = 0; m < INTERNED_ARGUMENTS[k].length; m++) {
        INTERNED_ARGUMENTS[k][m] = new Name(m, localBasicType);
      }
    }
    IMPL_NAMES = MemberName.getFactory();
    LF_identityForm = new LambdaForm[BasicType.TYPE_LIMIT];
    LF_zeroForm = new LambdaForm[BasicType.TYPE_LIMIT];
    NF_identity = new NamedFunction[BasicType.TYPE_LIMIT];
    NF_zero = new NamedFunction[BasicType.TYPE_LIMIT];
    if (MethodHandleStatics.debugEnabled()) {
      DEBUG_NAME_COUNTERS = new HashMap();
    } else {
      DEBUG_NAME_COUNTERS = null;
    }
    createIdentityForms();
    computeInitialPreparedForms();
    NamedFunction.initializeInvokers();
  }
  
  static enum BasicType
  {
    static final BasicType[] ALL_TYPES;
    static final BasicType[] ARG_TYPES;
    static final int ARG_TYPE_LIMIT;
    static final int TYPE_LIMIT;
    private final char btChar;
    private final Class<?> btClass;
    private final Wrapper btWrapper;
    
    private BasicType(char paramChar, Class<?> paramClass, Wrapper paramWrapper)
    {
      btChar = paramChar;
      btClass = paramClass;
      btWrapper = paramWrapper;
    }
    
    char basicTypeChar()
    {
      return btChar;
    }
    
    Class<?> basicTypeClass()
    {
      return btClass;
    }
    
    Wrapper basicTypeWrapper()
    {
      return btWrapper;
    }
    
    int basicTypeSlots()
    {
      return btWrapper.stackSlots();
    }
    
    static BasicType basicType(byte paramByte)
    {
      return ALL_TYPES[paramByte];
    }
    
    static BasicType basicType(char paramChar)
    {
      switch (paramChar)
      {
      case 'L': 
        return L_TYPE;
      case 'I': 
        return I_TYPE;
      case 'J': 
        return J_TYPE;
      case 'F': 
        return F_TYPE;
      case 'D': 
        return D_TYPE;
      case 'V': 
        return V_TYPE;
      case 'B': 
      case 'C': 
      case 'S': 
      case 'Z': 
        return I_TYPE;
      }
      throw MethodHandleStatics.newInternalError("Unknown type char: '" + paramChar + "'");
    }
    
    static BasicType basicType(Wrapper paramWrapper)
    {
      char c = paramWrapper.basicTypeChar();
      return basicType(c);
    }
    
    static BasicType basicType(Class<?> paramClass)
    {
      if (!paramClass.isPrimitive()) {
        return L_TYPE;
      }
      return basicType(Wrapper.forPrimitiveType(paramClass));
    }
    
    static char basicTypeChar(Class<?> paramClass)
    {
      return basicTypebtChar;
    }
    
    static BasicType[] basicTypes(List<Class<?>> paramList)
    {
      BasicType[] arrayOfBasicType = new BasicType[paramList.size()];
      for (int i = 0; i < arrayOfBasicType.length; i++) {
        arrayOfBasicType[i] = basicType((Class)paramList.get(i));
      }
      return arrayOfBasicType;
    }
    
    static BasicType[] basicTypes(String paramString)
    {
      BasicType[] arrayOfBasicType = new BasicType[paramString.length()];
      for (int i = 0; i < arrayOfBasicType.length; i++) {
        arrayOfBasicType[i] = basicType(paramString.charAt(i));
      }
      return arrayOfBasicType;
    }
    
    static byte[] basicTypesOrd(BasicType[] paramArrayOfBasicType)
    {
      byte[] arrayOfByte = new byte[paramArrayOfBasicType.length];
      for (int i = 0; i < paramArrayOfBasicType.length; i++) {
        arrayOfByte[i] = ((byte)paramArrayOfBasicType[i].ordinal());
      }
      return arrayOfByte;
    }
    
    static boolean isBasicTypeChar(char paramChar)
    {
      return "LIJFDV".indexOf(paramChar) >= 0;
    }
    
    static boolean isArgBasicTypeChar(char paramChar)
    {
      return "LIJFD".indexOf(paramChar) >= 0;
    }
    
    private static boolean checkBasicType()
    {
      for (int i = 0; i < ARG_TYPE_LIMIT; i++)
      {
        assert (ARG_TYPES[i].ordinal() == i);
        assert (ARG_TYPES[i] == ALL_TYPES[i]);
      }
      for (i = 0; i < TYPE_LIMIT; i++) {
        assert (ALL_TYPES[i].ordinal() == i);
      }
      assert (ALL_TYPES[(TYPE_LIMIT - 1)] == V_TYPE);
      assert (!Arrays.asList(ARG_TYPES).contains(V_TYPE));
      return true;
    }
    
    static
    {
      L_TYPE = new BasicType("L_TYPE", 0, 'L', Object.class, Wrapper.OBJECT);
      I_TYPE = new BasicType("I_TYPE", 1, 'I', Integer.TYPE, Wrapper.INT);
      J_TYPE = new BasicType("J_TYPE", 2, 'J', Long.TYPE, Wrapper.LONG);
      F_TYPE = new BasicType("F_TYPE", 3, 'F', Float.TYPE, Wrapper.FLOAT);
      D_TYPE = new BasicType("D_TYPE", 4, 'D', Double.TYPE, Wrapper.DOUBLE);
      V_TYPE = new BasicType("V_TYPE", 5, 'V', Void.TYPE, Wrapper.VOID);
      $VALUES = new BasicType[] { L_TYPE, I_TYPE, J_TYPE, F_TYPE, D_TYPE, V_TYPE };
      ALL_TYPES = values();
      ARG_TYPES = (BasicType[])Arrays.copyOf(ALL_TYPES, ALL_TYPES.length - 1);
      ARG_TYPE_LIMIT = ARG_TYPES.length;
      TYPE_LIMIT = ALL_TYPES.length;
      assert (checkBasicType());
    }
  }
  
  @Target({java.lang.annotation.ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface Compiled {}
  
  @Target({java.lang.annotation.ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface Hidden {}
  
  static final class Name
  {
    final LambdaForm.BasicType type;
    private short index;
    final LambdaForm.NamedFunction function;
    final Object constraint;
    @Stable
    final Object[] arguments;
    
    private Name(int paramInt, LambdaForm.BasicType paramBasicType, LambdaForm.NamedFunction paramNamedFunction, Object[] paramArrayOfObject)
    {
      index = ((short)paramInt);
      type = paramBasicType;
      function = paramNamedFunction;
      arguments = paramArrayOfObject;
      constraint = null;
      assert (index == paramInt);
    }
    
    private Name(Name paramName, Object paramObject)
    {
      index = index;
      type = type;
      function = function;
      arguments = arguments;
      constraint = paramObject;
      assert ((paramObject == null) || (isParam()));
      assert ((paramObject == null) || ((paramObject instanceof BoundMethodHandle.SpeciesData)) || ((paramObject instanceof Class)));
    }
    
    Name(MethodHandle paramMethodHandle, Object... paramVarArgs)
    {
      this(new LambdaForm.NamedFunction(paramMethodHandle), paramVarArgs);
    }
    
    Name(MethodType paramMethodType, Object... paramVarArgs)
    {
      this(new LambdaForm.NamedFunction(paramMethodType), paramVarArgs);
      assert (((paramVarArgs[0] instanceof Name)) && (0type == LambdaForm.BasicType.L_TYPE));
    }
    
    Name(MemberName paramMemberName, Object... paramVarArgs)
    {
      this(new LambdaForm.NamedFunction(paramMemberName), paramVarArgs);
    }
    
    Name(LambdaForm.NamedFunction paramNamedFunction, Object... paramVarArgs)
    {
      this(-1, paramNamedFunction.returnType(), paramNamedFunction, paramVarArgs = Arrays.copyOf(paramVarArgs, paramVarArgs.length, Object[].class));
      assert (paramVarArgs.length == paramNamedFunction.arity()) : ("arity mismatch: arguments.length=" + paramVarArgs.length + " == function.arity()=" + paramNamedFunction.arity() + " in " + debugString());
      for (int i = 0; i < paramVarArgs.length; i++) {
        assert (typesMatch(paramNamedFunction.parameterType(i), paramVarArgs[i])) : ("types don't match: function.parameterType(" + i + ")=" + paramNamedFunction.parameterType(i) + ", arguments[" + i + "]=" + paramVarArgs[i] + " in " + debugString());
      }
    }
    
    Name(int paramInt, LambdaForm.BasicType paramBasicType)
    {
      this(paramInt, paramBasicType, null, null);
    }
    
    Name(LambdaForm.BasicType paramBasicType)
    {
      this(-1, paramBasicType);
    }
    
    LambdaForm.BasicType type()
    {
      return type;
    }
    
    int index()
    {
      return index;
    }
    
    boolean initIndex(int paramInt)
    {
      if (index != paramInt)
      {
        if (index != -1) {
          return false;
        }
        index = ((short)paramInt);
      }
      return true;
    }
    
    char typeChar()
    {
      return type.btChar;
    }
    
    void resolve()
    {
      if (function != null) {
        function.resolve();
      }
    }
    
    Name newIndex(int paramInt)
    {
      if (initIndex(paramInt)) {
        return this;
      }
      return cloneWithIndex(paramInt);
    }
    
    Name cloneWithIndex(int paramInt)
    {
      Object[] arrayOfObject = arguments == null ? null : (Object[])arguments.clone();
      return new Name(paramInt, type, function, arrayOfObject).withConstraint(constraint);
    }
    
    Name withConstraint(Object paramObject)
    {
      if (paramObject == constraint) {
        return this;
      }
      return new Name(this, paramObject);
    }
    
    Name replaceName(Name paramName1, Name paramName2)
    {
      if (paramName1 == paramName2) {
        return this;
      }
      Object[] arrayOfObject = arguments;
      if (arrayOfObject == null) {
        return this;
      }
      int i = 0;
      for (int j = 0; j < arrayOfObject.length; j++) {
        if (arrayOfObject[j] == paramName1)
        {
          if (i == 0)
          {
            i = 1;
            arrayOfObject = (Object[])arrayOfObject.clone();
          }
          arrayOfObject[j] = paramName2;
        }
      }
      if (i == 0) {
        return this;
      }
      return new Name(function, arrayOfObject);
    }
    
    Name replaceNames(Name[] paramArrayOfName1, Name[] paramArrayOfName2, int paramInt1, int paramInt2)
    {
      if (paramInt1 >= paramInt2) {
        return this;
      }
      Object[] arrayOfObject = arguments;
      int i = 0;
      for (int j = 0; j < arrayOfObject.length; j++) {
        if ((arrayOfObject[j] instanceof Name))
        {
          Name localName = (Name)arrayOfObject[j];
          int k = index;
          if ((k < 0) || (k >= paramArrayOfName2.length) || (localName != paramArrayOfName2[k])) {
            for (int m = paramInt1; m < paramInt2; m++) {
              if (localName == paramArrayOfName1[m])
              {
                if (localName == paramArrayOfName2[m]) {
                  break;
                }
                if (i == 0)
                {
                  i = 1;
                  arrayOfObject = (Object[])arrayOfObject.clone();
                }
                arrayOfObject[j] = paramArrayOfName2[m];
                break;
              }
            }
          }
        }
      }
      if (i == 0) {
        return this;
      }
      return new Name(function, arrayOfObject);
    }
    
    void internArguments()
    {
      Object[] arrayOfObject = arguments;
      for (int i = 0; i < arrayOfObject.length; i++) {
        if ((arrayOfObject[i] instanceof Name))
        {
          Name localName = (Name)arrayOfObject[i];
          if ((localName.isParam()) && (index < 10)) {
            arrayOfObject[i] = LambdaForm.internArgument(localName);
          }
        }
      }
    }
    
    boolean isParam()
    {
      return function == null;
    }
    
    boolean isConstantZero()
    {
      return (!isParam()) && (arguments.length == 0) && (function.isConstantZero());
    }
    
    public String toString()
    {
      return (isParam() ? "a" : "t") + (index >= 0 ? index : System.identityHashCode(this)) + ":" + typeChar();
    }
    
    public String debugString()
    {
      String str = paramString();
      return str + "=" + exprString();
    }
    
    public String paramString()
    {
      String str = toString();
      Object localObject = constraint;
      if (localObject == null) {
        return str;
      }
      if ((localObject instanceof Class)) {
        localObject = ((Class)localObject).getSimpleName();
      }
      return str + "/" + localObject;
    }
    
    public String exprString()
    {
      if (function == null) {
        return toString();
      }
      StringBuilder localStringBuilder = new StringBuilder(function.toString());
      localStringBuilder.append("(");
      String str = "";
      for (Object localObject : arguments)
      {
        localStringBuilder.append(str);
        str = ",";
        if (((localObject instanceof Name)) || ((localObject instanceof Integer))) {
          localStringBuilder.append(localObject);
        } else {
          localStringBuilder.append("(").append(localObject).append(")");
        }
      }
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
    
    static boolean typesMatch(LambdaForm.BasicType paramBasicType, Object paramObject)
    {
      if ((paramObject instanceof Name)) {
        return type == paramBasicType;
      }
      switch (LambdaForm.1.$SwitchMap$java$lang$invoke$LambdaForm$BasicType[paramBasicType.ordinal()])
      {
      case 1: 
        return paramObject instanceof Integer;
      case 2: 
        return paramObject instanceof Long;
      case 3: 
        return paramObject instanceof Float;
      case 4: 
        return paramObject instanceof Double;
      }
      assert (paramBasicType == LambdaForm.BasicType.L_TYPE);
      return true;
    }
    
    int lastUseIndex(Name paramName)
    {
      if (arguments == null) {
        return -1;
      }
      int i = arguments.length;
      do
      {
        i--;
        if (i < 0) {
          break;
        }
      } while (arguments[i] != paramName);
      return i;
      return -1;
    }
    
    int useCount(Name paramName)
    {
      if (arguments == null) {
        return 0;
      }
      int i = 0;
      int j = arguments.length;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        if (arguments[j] == paramName) {
          i++;
        }
      }
      return i;
    }
    
    boolean contains(Name paramName)
    {
      return (this == paramName) || (lastUseIndex(paramName) >= 0);
    }
    
    public boolean equals(Name paramName)
    {
      if (this == paramName) {
        return true;
      }
      if (isParam()) {
        return false;
      }
      return (type == type) && (function.equals(function)) && (Arrays.equals(arguments, arguments));
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Name)) && (equals((Name)paramObject));
    }
    
    public int hashCode()
    {
      if (isParam()) {
        return index | type.ordinal() << 8;
      }
      return function.hashCode() ^ Arrays.hashCode(arguments);
    }
  }
  
  static class NamedFunction
  {
    final MemberName member;
    @Stable
    MethodHandle resolvedHandle;
    @Stable
    MethodHandle invoker;
    static final MethodType INVOKER_METHOD_TYPE = MethodType.methodType(Object.class, MethodHandle.class, new Class[] { Object[].class });
    
    NamedFunction(MethodHandle paramMethodHandle)
    {
      this(paramMethodHandle.internalMemberName(), paramMethodHandle);
    }
    
    NamedFunction(MemberName paramMemberName, MethodHandle paramMethodHandle)
    {
      member = paramMemberName;
      resolvedHandle = paramMethodHandle;
    }
    
    NamedFunction(MethodType paramMethodType)
    {
      assert (paramMethodType == paramMethodType.basicType()) : paramMethodType;
      if (paramMethodType.parameterSlotCount() < 253)
      {
        resolvedHandle = paramMethodType.invokers().basicInvoker();
        member = resolvedHandle.internalMemberName();
      }
      else
      {
        member = Invokers.invokeBasicMethod(paramMethodType);
      }
      assert (isInvokeBasic(member));
    }
    
    private static boolean isInvokeBasic(MemberName paramMemberName)
    {
      return (paramMemberName != null) && (paramMemberName.getDeclaringClass() == MethodHandle.class) && ("invokeBasic".equals(paramMemberName.getName()));
    }
    
    NamedFunction(Method paramMethod)
    {
      this(new MemberName(paramMethod));
    }
    
    NamedFunction(Field paramField)
    {
      this(new MemberName(paramField));
    }
    
    NamedFunction(MemberName paramMemberName)
    {
      member = paramMemberName;
      resolvedHandle = null;
    }
    
    MethodHandle resolvedHandle()
    {
      if (resolvedHandle == null) {
        resolve();
      }
      return resolvedHandle;
    }
    
    void resolve()
    {
      resolvedHandle = DirectMethodHandle.make(member);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (!(paramObject instanceof NamedFunction)) {
        return false;
      }
      NamedFunction localNamedFunction = (NamedFunction)paramObject;
      return (member != null) && (member.equals(member));
    }
    
    public int hashCode()
    {
      if (member != null) {
        return member.hashCode();
      }
      return super.hashCode();
    }
    
    static void initializeInvokers()
    {
      Iterator localIterator = MemberName.getFactory().getMethods(NamedFunction.class, false, null, null, null).iterator();
      while (localIterator.hasNext())
      {
        MemberName localMemberName = (MemberName)localIterator.next();
        if ((localMemberName.isStatic()) && (localMemberName.isPackage()))
        {
          MethodType localMethodType1 = localMemberName.getMethodType();
          if ((localMethodType1.equals(INVOKER_METHOD_TYPE)) && (localMemberName.getName().startsWith("invoke_")))
          {
            String str = localMemberName.getName().substring("invoke_".length());
            int i = LambdaForm.signatureArity(str);
            MethodType localMethodType2 = MethodType.genericMethodType(i);
            if (LambdaForm.signatureReturn(str) == LambdaForm.BasicType.V_TYPE) {
              localMethodType2 = localMethodType2.changeReturnType(Void.TYPE);
            }
            MethodTypeForm localMethodTypeForm = localMethodType2.form();
            localMethodTypeForm.setCachedMethodHandle(1, DirectMethodHandle.make(localMemberName));
          }
        }
      }
    }
    
    @LambdaForm.Hidden
    static Object invoke__V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(0, Void.TYPE, paramMethodHandle, paramArrayOfObject));
      paramMethodHandle.invokeBasic();
      return null;
    }
    
    @LambdaForm.Hidden
    static Object invoke_L_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(1, Void.TYPE, paramMethodHandle, paramArrayOfObject));
      paramMethodHandle.invokeBasic(paramArrayOfObject[0]);
      return null;
    }
    
    @LambdaForm.Hidden
    static Object invoke_LL_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(2, Void.TYPE, paramMethodHandle, paramArrayOfObject));
      paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1]);
      return null;
    }
    
    @LambdaForm.Hidden
    static Object invoke_LLL_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(3, Void.TYPE, paramMethodHandle, paramArrayOfObject));
      paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2]);
      return null;
    }
    
    @LambdaForm.Hidden
    static Object invoke_LLLL_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(4, Void.TYPE, paramMethodHandle, paramArrayOfObject));
      paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3]);
      return null;
    }
    
    @LambdaForm.Hidden
    static Object invoke_LLLLL_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(5, Void.TYPE, paramMethodHandle, paramArrayOfObject));
      paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4]);
      return null;
    }
    
    @LambdaForm.Hidden
    static Object invoke__L(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(0, paramMethodHandle, paramArrayOfObject));
      return paramMethodHandle.invokeBasic();
    }
    
    @LambdaForm.Hidden
    static Object invoke_L_L(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(1, paramMethodHandle, paramArrayOfObject));
      return paramMethodHandle.invokeBasic(paramArrayOfObject[0]);
    }
    
    @LambdaForm.Hidden
    static Object invoke_LL_L(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(2, paramMethodHandle, paramArrayOfObject));
      return paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1]);
    }
    
    @LambdaForm.Hidden
    static Object invoke_LLL_L(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(3, paramMethodHandle, paramArrayOfObject));
      return paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2]);
    }
    
    @LambdaForm.Hidden
    static Object invoke_LLLL_L(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(4, paramMethodHandle, paramArrayOfObject));
      return paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3]);
    }
    
    @LambdaForm.Hidden
    static Object invoke_LLLLL_L(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
      throws Throwable
    {
      assert (arityCheck(5, paramMethodHandle, paramArrayOfObject));
      return paramMethodHandle.invokeBasic(paramArrayOfObject[0], paramArrayOfObject[1], paramArrayOfObject[2], paramArrayOfObject[3], paramArrayOfObject[4]);
    }
    
    private static boolean arityCheck(int paramInt, MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
    {
      return arityCheck(paramInt, Object.class, paramMethodHandle, paramArrayOfObject);
    }
    
    private static boolean arityCheck(int paramInt, Class<?> paramClass, MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
    {
      if ((!$assertionsDisabled) && (paramArrayOfObject.length != paramInt)) {
        throw new AssertionError(Arrays.asList(new Integer[] { Integer.valueOf(paramArrayOfObject.length), Integer.valueOf(paramInt) }));
      }
      if ((!$assertionsDisabled) && (paramMethodHandle.type().basicType() != MethodType.genericMethodType(paramInt).changeReturnType(paramClass))) {
        throw new AssertionError(Arrays.asList(new Object[] { paramMethodHandle, paramClass, Integer.valueOf(paramInt) }));
      }
      MemberName localMemberName = paramMethodHandle.internalMemberName();
      if (isInvokeBasic(localMemberName))
      {
        assert (paramInt > 0);
        assert ((paramArrayOfObject[0] instanceof MethodHandle));
        MethodHandle localMethodHandle = (MethodHandle)paramArrayOfObject[0];
        if ((!$assertionsDisabled) && (localMethodHandle.type().basicType() != MethodType.genericMethodType(paramInt - 1).changeReturnType(paramClass))) {
          throw new AssertionError(Arrays.asList(new Object[] { localMemberName, localMethodHandle, paramClass, Integer.valueOf(paramInt) }));
        }
      }
      return true;
    }
    
    private static MethodHandle computeInvoker(MethodTypeForm paramMethodTypeForm)
    {
      paramMethodTypeForm = paramMethodTypeForm.basicType().form();
      Object localObject = paramMethodTypeForm.cachedMethodHandle(1);
      if (localObject != null) {
        return (MethodHandle)localObject;
      }
      MemberName localMemberName = InvokerBytecodeGenerator.generateNamedFunctionInvoker(paramMethodTypeForm);
      localObject = DirectMethodHandle.make(localMemberName);
      MethodHandle localMethodHandle = paramMethodTypeForm.cachedMethodHandle(1);
      if (localMethodHandle != null) {
        return localMethodHandle;
      }
      if (!((MethodHandle)localObject).type().equals(INVOKER_METHOD_TYPE)) {
        throw MethodHandleStatics.newInternalError(((MethodHandle)localObject).debugString());
      }
      return paramMethodTypeForm.setCachedMethodHandle(1, (MethodHandle)localObject);
    }
    
    @LambdaForm.Hidden
    Object invokeWithArguments(Object... paramVarArgs)
      throws Throwable
    {
      if (LambdaForm.TRACE_INTERPRETER) {
        return invokeWithArgumentsTracing(paramVarArgs);
      }
      assert (checkArgumentTypes(paramVarArgs, methodType()));
      return invoker().invokeBasic(resolvedHandle(), paramVarArgs);
    }
    
    @LambdaForm.Hidden
    Object invokeWithArgumentsTracing(Object[] paramArrayOfObject)
      throws Throwable
    {
      Object localObject;
      try
      {
        LambdaForm.traceInterpreter("[ call", this, paramArrayOfObject);
        if (invoker == null)
        {
          LambdaForm.traceInterpreter("| getInvoker", this);
          invoker();
        }
        if (resolvedHandle == null)
        {
          LambdaForm.traceInterpreter("| resolve", this);
          resolvedHandle();
        }
        assert (checkArgumentTypes(paramArrayOfObject, methodType()));
        localObject = invoker().invokeBasic(resolvedHandle(), paramArrayOfObject);
      }
      catch (Throwable localThrowable)
      {
        LambdaForm.traceInterpreter("] throw =>", localThrowable);
        throw localThrowable;
      }
      LambdaForm.traceInterpreter("] return =>", localObject);
      return localObject;
    }
    
    private MethodHandle invoker()
    {
      if (invoker != null) {
        return invoker;
      }
      return invoker = computeInvoker(methodType().form());
    }
    
    private static boolean checkArgumentTypes(Object[] paramArrayOfObject, MethodType paramMethodType)
    {
      return true;
    }
    
    MethodType methodType()
    {
      if (resolvedHandle != null) {
        return resolvedHandle.type();
      }
      return member.getInvocationType();
    }
    
    MemberName member()
    {
      assert (assertMemberIsConsistent());
      return member;
    }
    
    private boolean assertMemberIsConsistent()
    {
      if ((resolvedHandle instanceof DirectMethodHandle))
      {
        MemberName localMemberName = resolvedHandle.internalMemberName();
        assert (localMemberName.equals(member));
      }
      return true;
    }
    
    Class<?> memberDeclaringClassOrNull()
    {
      return member == null ? null : member.getDeclaringClass();
    }
    
    LambdaForm.BasicType returnType()
    {
      return LambdaForm.BasicType.basicType(methodType().returnType());
    }
    
    LambdaForm.BasicType parameterType(int paramInt)
    {
      return LambdaForm.BasicType.basicType(methodType().parameterType(paramInt));
    }
    
    int arity()
    {
      return methodType().parameterCount();
    }
    
    public String toString()
    {
      if (member == null) {
        return String.valueOf(resolvedHandle);
      }
      return member.getDeclaringClass().getSimpleName() + "." + member.getName();
    }
    
    public boolean isIdentity()
    {
      return equals(LambdaForm.identity(returnType()));
    }
    
    public boolean isConstantZero()
    {
      return equals(LambdaForm.constantZero(returnType()));
    }
    
    public MethodHandleImpl.Intrinsic intrinsicName()
    {
      return resolvedHandle == null ? MethodHandleImpl.Intrinsic.NONE : resolvedHandle.intrinsicName();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\LambdaForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */