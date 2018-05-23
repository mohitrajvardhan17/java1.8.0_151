package java.lang.invoke;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;
import sun.reflect.misc.ReflectUtil;

class InvokerBytecodeGenerator
{
  private static final String MH = "java/lang/invoke/MethodHandle";
  private static final String MHI = "java/lang/invoke/MethodHandleImpl";
  private static final String LF = "java/lang/invoke/LambdaForm";
  private static final String LFN = "java/lang/invoke/LambdaForm$Name";
  private static final String CLS = "java/lang/Class";
  private static final String OBJ = "java/lang/Object";
  private static final String OBJARY = "[Ljava/lang/Object;";
  private static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
  private static final String LF_SIG = "Ljava/lang/invoke/LambdaForm;";
  private static final String LFN_SIG = "Ljava/lang/invoke/LambdaForm$Name;";
  private static final String LL_SIG = "(Ljava/lang/Object;)Ljava/lang/Object;";
  private static final String LLV_SIG = "(Ljava/lang/Object;Ljava/lang/Object;)V";
  private static final String CLL_SIG = "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;";
  private static final String superName = "java/lang/Object";
  private final String className;
  private final String sourceFile;
  private final LambdaForm lambdaForm;
  private final String invokerName;
  private final MethodType invokerType;
  private final int[] localsMap;
  private final LambdaForm.BasicType[] localTypes;
  private final Class<?>[] localClasses;
  private ClassWriter cw;
  private MethodVisitor mv;
  private static final MemberName.Factory MEMBERNAME_FACTORY;
  private static final Class<?> HOST_CLASS;
  private static final HashMap<String, Integer> DUMP_CLASS_FILES_COUNTERS;
  private static final File DUMP_CLASS_FILES_DIR;
  Map<Object, CpPatch> cpPatches = new HashMap();
  int cph = 0;
  private static Class<?>[] STATICALLY_INVOCABLE_PACKAGES = { Object.class, Arrays.class, Unsafe.class };
  
  private InvokerBytecodeGenerator(LambdaForm paramLambdaForm, int paramInt, String paramString1, String paramString2, MethodType paramMethodType)
  {
    if (paramString2.contains("."))
    {
      int i = paramString2.indexOf(".");
      paramString1 = paramString2.substring(0, i);
      paramString2 = paramString2.substring(i + 1);
    }
    if (MethodHandleStatics.DUMP_CLASS_FILES) {
      paramString1 = makeDumpableClassName(paramString1);
    }
    className = ("java/lang/invoke/LambdaForm$" + paramString1);
    sourceFile = ("LambdaForm$" + paramString1);
    lambdaForm = paramLambdaForm;
    invokerName = paramString2;
    invokerType = paramMethodType;
    localsMap = new int[paramInt + 1];
    localTypes = new LambdaForm.BasicType[paramInt + 1];
    localClasses = new Class[paramInt + 1];
  }
  
  private InvokerBytecodeGenerator(String paramString1, String paramString2, MethodType paramMethodType)
  {
    this(null, paramMethodType.parameterCount(), paramString1, paramString2, paramMethodType);
    localTypes[(localTypes.length - 1)] = LambdaForm.BasicType.V_TYPE;
    for (int i = 0; i < localsMap.length; i++)
    {
      localsMap[i] = (paramMethodType.parameterSlotCount() - paramMethodType.parameterSlotDepth(i));
      if (i < paramMethodType.parameterCount()) {
        localTypes[i] = LambdaForm.BasicType.basicType(paramMethodType.parameterType(i));
      }
    }
  }
  
  private InvokerBytecodeGenerator(String paramString, LambdaForm paramLambdaForm, MethodType paramMethodType)
  {
    this(paramLambdaForm, names.length, paramString, debugName, paramMethodType);
    LambdaForm.Name[] arrayOfName = names;
    int i = 0;
    int j = 0;
    while (i < localsMap.length)
    {
      localsMap[i] = j;
      if (i < arrayOfName.length)
      {
        LambdaForm.BasicType localBasicType = arrayOfName[i].type();
        j += localBasicType.basicTypeSlots();
        localTypes[i] = localBasicType;
      }
      i++;
    }
  }
  
  static void maybeDump(String paramString, final byte[] paramArrayOfByte)
  {
    if (MethodHandleStatics.DUMP_CLASS_FILES) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          try
          {
            String str = val$className;
            File localFile = new File(InvokerBytecodeGenerator.DUMP_CLASS_FILES_DIR, str + ".class");
            System.out.println("dump: " + localFile);
            localFile.getParentFile().mkdirs();
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            localFileOutputStream.write(paramArrayOfByte);
            localFileOutputStream.close();
            return null;
          }
          catch (IOException localIOException)
          {
            throw MethodHandleStatics.newInternalError(localIOException);
          }
        }
      });
    }
  }
  
  private static String makeDumpableClassName(String paramString)
  {
    Integer localInteger;
    synchronized (DUMP_CLASS_FILES_COUNTERS)
    {
      localInteger = (Integer)DUMP_CLASS_FILES_COUNTERS.get(paramString);
      if (localInteger == null) {
        localInteger = Integer.valueOf(0);
      }
      DUMP_CLASS_FILES_COUNTERS.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
    }
    for (??? = localInteger.toString(); ((String)???).length() < 3; ??? = "0" + (String)???) {}
    paramString = paramString + (String)???;
    return paramString;
  }
  
  String constantPlaceholder(Object paramObject)
  {
    String str = "CONSTANT_PLACEHOLDER_" + cph++;
    if (MethodHandleStatics.DUMP_CLASS_FILES) {
      str = str + " <<" + debugString(paramObject) + ">>";
    }
    if (cpPatches.containsKey(str)) {
      throw new InternalError("observed CP placeholder twice: " + str);
    }
    int i = cw.newConst(str);
    cpPatches.put(str, new CpPatch(i, str, paramObject));
    return str;
  }
  
  Object[] cpPatches(byte[] paramArrayOfByte)
  {
    int i = getConstantPoolSize(paramArrayOfByte);
    Object[] arrayOfObject = new Object[i];
    Iterator localIterator = cpPatches.values().iterator();
    while (localIterator.hasNext())
    {
      CpPatch localCpPatch = (CpPatch)localIterator.next();
      if (index >= i) {
        throw new InternalError("in cpool[" + i + "]: " + localCpPatch + "\n" + Arrays.toString(Arrays.copyOf(paramArrayOfByte, 20)));
      }
      arrayOfObject[index] = value;
    }
    return arrayOfObject;
  }
  
  private static String debugString(Object paramObject)
  {
    if ((paramObject instanceof MethodHandle))
    {
      MethodHandle localMethodHandle = (MethodHandle)paramObject;
      MemberName localMemberName = localMethodHandle.internalMemberName();
      if (localMemberName != null) {
        return localMemberName.toString();
      }
      return localMethodHandle.debugString();
    }
    return paramObject.toString();
  }
  
  private static int getConstantPoolSize(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[8] & 0xFF) << 8 | paramArrayOfByte[9] & 0xFF;
  }
  
  private MemberName loadMethod(byte[] paramArrayOfByte)
  {
    Class localClass = loadAndInitializeInvokerClass(paramArrayOfByte, cpPatches(paramArrayOfByte));
    return resolveInvokerMember(localClass, invokerName, invokerType);
  }
  
  private static Class<?> loadAndInitializeInvokerClass(byte[] paramArrayOfByte, Object[] paramArrayOfObject)
  {
    Class localClass = MethodHandleStatics.UNSAFE.defineAnonymousClass(HOST_CLASS, paramArrayOfByte, paramArrayOfObject);
    MethodHandleStatics.UNSAFE.ensureClassInitialized(localClass);
    return localClass;
  }
  
  private static MemberName resolveInvokerMember(Class<?> paramClass, String paramString, MethodType paramMethodType)
  {
    MemberName localMemberName = new MemberName(paramClass, paramString, paramMethodType, (byte)6);
    try
    {
      localMemberName = MEMBERNAME_FACTORY.resolveOrFail((byte)6, localMemberName, HOST_CLASS, ReflectiveOperationException.class);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
    }
    return localMemberName;
  }
  
  private void classFilePrologue()
  {
    cw = new ClassWriter(3);
    cw.visit(52, 48, className, null, "java/lang/Object", null);
    cw.visitSource(sourceFile, null);
    String str = invokerType.toMethodDescriptorString();
    mv = cw.visitMethod(8, invokerName, str, null, null);
  }
  
  private void classFileEpilogue()
  {
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }
  
  private void emitConst(Object paramObject)
  {
    if (paramObject == null)
    {
      mv.visitInsn(1);
      return;
    }
    if ((paramObject instanceof Integer))
    {
      emitIconstInsn(((Integer)paramObject).intValue());
      return;
    }
    if ((paramObject instanceof Long))
    {
      long l = ((Long)paramObject).longValue();
      if (l == (short)(int)l)
      {
        emitIconstInsn((int)l);
        mv.visitInsn(133);
        return;
      }
    }
    if ((paramObject instanceof Float))
    {
      float f = ((Float)paramObject).floatValue();
      if (f == (short)(int)f)
      {
        emitIconstInsn((int)f);
        mv.visitInsn(134);
        return;
      }
    }
    if ((paramObject instanceof Double))
    {
      double d = ((Double)paramObject).doubleValue();
      if (d == (short)(int)d)
      {
        emitIconstInsn((int)d);
        mv.visitInsn(135);
        return;
      }
    }
    if ((paramObject instanceof Boolean))
    {
      emitIconstInsn(((Boolean)paramObject).booleanValue() ? 1 : 0);
      return;
    }
    mv.visitLdcInsn(paramObject);
  }
  
  private void emitIconstInsn(int paramInt)
  {
    int i;
    switch (paramInt)
    {
    case 0: 
      i = 3;
      break;
    case 1: 
      i = 4;
      break;
    case 2: 
      i = 5;
      break;
    case 3: 
      i = 6;
      break;
    case 4: 
      i = 7;
      break;
    case 5: 
      i = 8;
      break;
    default: 
      if (paramInt == (byte)paramInt) {
        mv.visitIntInsn(16, paramInt & 0xFF);
      } else if (paramInt == (short)paramInt) {
        mv.visitIntInsn(17, (char)paramInt);
      } else {
        mv.visitLdcInsn(Integer.valueOf(paramInt));
      }
      return;
    }
    mv.visitInsn(i);
  }
  
  private void emitLoadInsn(LambdaForm.BasicType paramBasicType, int paramInt)
  {
    int i = loadInsnOpcode(paramBasicType);
    mv.visitVarInsn(i, localsMap[paramInt]);
  }
  
  private int loadInsnOpcode(LambdaForm.BasicType paramBasicType)
    throws InternalError
  {
    switch (paramBasicType)
    {
    case I_TYPE: 
      return 21;
    case J_TYPE: 
      return 22;
    case F_TYPE: 
      return 23;
    case D_TYPE: 
      return 24;
    case L_TYPE: 
      return 25;
    }
    throw new InternalError("unknown type: " + paramBasicType);
  }
  
  private void emitAloadInsn(int paramInt)
  {
    emitLoadInsn(LambdaForm.BasicType.L_TYPE, paramInt);
  }
  
  private void emitStoreInsn(LambdaForm.BasicType paramBasicType, int paramInt)
  {
    int i = storeInsnOpcode(paramBasicType);
    mv.visitVarInsn(i, localsMap[paramInt]);
  }
  
  private int storeInsnOpcode(LambdaForm.BasicType paramBasicType)
    throws InternalError
  {
    switch (paramBasicType)
    {
    case I_TYPE: 
      return 54;
    case J_TYPE: 
      return 55;
    case F_TYPE: 
      return 56;
    case D_TYPE: 
      return 57;
    case L_TYPE: 
      return 58;
    }
    throw new InternalError("unknown type: " + paramBasicType);
  }
  
  private void emitAstoreInsn(int paramInt)
  {
    emitStoreInsn(LambdaForm.BasicType.L_TYPE, paramInt);
  }
  
  private byte arrayTypeCode(Wrapper paramWrapper)
  {
    switch (paramWrapper)
    {
    case BOOLEAN: 
      return 4;
    case BYTE: 
      return 8;
    case CHAR: 
      return 5;
    case SHORT: 
      return 9;
    case INT: 
      return 10;
    case LONG: 
      return 11;
    case FLOAT: 
      return 6;
    case DOUBLE: 
      return 7;
    case OBJECT: 
      return 0;
    }
    throw new InternalError();
  }
  
  private int arrayInsnOpcode(byte paramByte, int paramInt)
    throws InternalError
  {
    assert ((paramInt == 83) || (paramInt == 50));
    int i;
    switch (paramByte)
    {
    case 4: 
      i = 84;
      break;
    case 8: 
      i = 84;
      break;
    case 5: 
      i = 85;
      break;
    case 9: 
      i = 86;
      break;
    case 10: 
      i = 79;
      break;
    case 11: 
      i = 80;
      break;
    case 6: 
      i = 81;
      break;
    case 7: 
      i = 82;
      break;
    case 0: 
      i = 83;
      break;
    case 1: 
    case 2: 
    case 3: 
    default: 
      throw new InternalError();
    }
    return i - 83 + paramInt;
  }
  
  private void freeFrameLocal(int paramInt)
  {
    int i = indexForFrameLocal(paramInt);
    if (i < 0) {
      return;
    }
    LambdaForm.BasicType localBasicType = localTypes[i];
    int j = makeLocalTemp(localBasicType);
    mv.visitVarInsn(loadInsnOpcode(localBasicType), paramInt);
    mv.visitVarInsn(storeInsnOpcode(localBasicType), j);
    assert (localsMap[i] == paramInt);
    localsMap[i] = j;
    assert (indexForFrameLocal(paramInt) < 0);
  }
  
  private int indexForFrameLocal(int paramInt)
  {
    for (int i = 0; i < localsMap.length; i++) {
      if ((localsMap[i] == paramInt) && (localTypes[i] != LambdaForm.BasicType.V_TYPE)) {
        return i;
      }
    }
    return -1;
  }
  
  private int makeLocalTemp(LambdaForm.BasicType paramBasicType)
  {
    int i = localsMap[(localsMap.length - 1)];
    localsMap[(localsMap.length - 1)] = (i + paramBasicType.basicTypeSlots());
    return i;
  }
  
  private void emitBoxing(Wrapper paramWrapper)
  {
    String str1 = "java/lang/" + paramWrapper.wrapperType().getSimpleName();
    String str2 = "valueOf";
    String str3 = "(" + paramWrapper.basicTypeChar() + ")L" + str1 + ";";
    mv.visitMethodInsn(184, str1, str2, str3, false);
  }
  
  private void emitUnboxing(Wrapper paramWrapper)
  {
    String str1 = "java/lang/" + paramWrapper.wrapperType().getSimpleName();
    String str2 = paramWrapper.primitiveSimpleName() + "Value";
    String str3 = "()" + paramWrapper.basicTypeChar();
    emitReferenceCast(paramWrapper.wrapperType(), null);
    mv.visitMethodInsn(182, str1, str2, str3, false);
  }
  
  private void emitImplicitConversion(LambdaForm.BasicType paramBasicType, Class<?> paramClass, Object paramObject)
  {
    assert (LambdaForm.BasicType.basicType(paramClass) == paramBasicType);
    if ((paramClass == paramBasicType.basicTypeClass()) && (paramBasicType != LambdaForm.BasicType.L_TYPE)) {
      return;
    }
    switch (paramBasicType)
    {
    case L_TYPE: 
      if (VerifyType.isNullConversion(Object.class, paramClass, false))
      {
        if (MethodHandleStatics.PROFILE_LEVEL > 0) {
          emitReferenceCast(Object.class, paramObject);
        }
        return;
      }
      emitReferenceCast(paramClass, paramObject);
      return;
    case I_TYPE: 
      if (!VerifyType.isNullConversion(Integer.TYPE, paramClass, false)) {
        emitPrimCast(paramBasicType.basicTypeWrapper(), Wrapper.forPrimitiveType(paramClass));
      }
      return;
    }
    throw MethodHandleStatics.newInternalError("bad implicit conversion: tc=" + paramBasicType + ": " + paramClass);
  }
  
  private boolean assertStaticType(Class<?> paramClass, LambdaForm.Name paramName)
  {
    int i = paramName.index();
    Class localClass = localClasses[i];
    if ((localClass != null) && ((localClass == paramClass) || (paramClass.isAssignableFrom(localClass)))) {
      return true;
    }
    if ((localClass == null) || (localClass.isAssignableFrom(paramClass))) {
      localClasses[i] = paramClass;
    }
    return false;
  }
  
  private void emitReferenceCast(Class<?> paramClass, Object paramObject)
  {
    Object localObject1 = null;
    Object localObject2;
    if ((paramObject instanceof LambdaForm.Name))
    {
      localObject2 = (LambdaForm.Name)paramObject;
      if (assertStaticType(paramClass, (LambdaForm.Name)localObject2)) {
        return;
      }
      if (lambdaForm.useCount((LambdaForm.Name)localObject2) > 1) {
        localObject1 = localObject2;
      }
    }
    if (isStaticallyNameable(paramClass))
    {
      localObject2 = getInternalName(paramClass);
      mv.visitTypeInsn(192, (String)localObject2);
    }
    else
    {
      mv.visitLdcInsn(constantPlaceholder(paramClass));
      mv.visitTypeInsn(192, "java/lang/Class");
      mv.visitInsn(95);
      mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "castReference", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;", false);
      if (Object[].class.isAssignableFrom(paramClass)) {
        mv.visitTypeInsn(192, "[Ljava/lang/Object;");
      } else if (MethodHandleStatics.PROFILE_LEVEL > 0) {
        mv.visitTypeInsn(192, "java/lang/Object");
      }
    }
    if (localObject1 != null)
    {
      mv.visitInsn(89);
      emitAstoreInsn(((LambdaForm.Name)localObject1).index());
    }
  }
  
  private void emitReturnInsn(LambdaForm.BasicType paramBasicType)
  {
    int i;
    switch (paramBasicType)
    {
    case I_TYPE: 
      i = 172;
      break;
    case J_TYPE: 
      i = 173;
      break;
    case F_TYPE: 
      i = 174;
      break;
    case D_TYPE: 
      i = 175;
      break;
    case L_TYPE: 
      i = 176;
      break;
    case V_TYPE: 
      i = 177;
      break;
    default: 
      throw new InternalError("unknown return type: " + paramBasicType);
    }
    mv.visitInsn(i);
  }
  
  private static String getInternalName(Class<?> paramClass)
  {
    if (paramClass == Object.class) {
      return "java/lang/Object";
    }
    if (paramClass == Object[].class) {
      return "[Ljava/lang/Object;";
    }
    if (paramClass == Class.class) {
      return "java/lang/Class";
    }
    if (paramClass == MethodHandle.class) {
      return "java/lang/invoke/MethodHandle";
    }
    assert (VerifyAccess.isTypeVisible(paramClass, Object.class)) : paramClass.getName();
    return paramClass.getName().replace('.', '/');
  }
  
  static MemberName generateCustomizedCode(LambdaForm paramLambdaForm, MethodType paramMethodType)
  {
    InvokerBytecodeGenerator localInvokerBytecodeGenerator = new InvokerBytecodeGenerator("MH", paramLambdaForm, paramMethodType);
    return localInvokerBytecodeGenerator.loadMethod(localInvokerBytecodeGenerator.generateCustomizedCodeBytes());
  }
  
  private boolean checkActualReceiver()
  {
    mv.visitInsn(89);
    mv.visitVarInsn(25, localsMap[0]);
    mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "assertSame", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
    return true;
  }
  
  private byte[] generateCustomizedCodeBytes()
  {
    classFilePrologue();
    mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Compiled;", true);
    if (lambdaForm.forceInline) {
      mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
    } else {
      mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
    }
    if (lambdaForm.customized != null)
    {
      mv.visitLdcInsn(constantPlaceholder(lambdaForm.customized));
      mv.visitTypeInsn(192, "java/lang/invoke/MethodHandle");
      assert (checkActualReceiver());
      mv.visitVarInsn(58, localsMap[0]);
    }
    Object localObject1 = null;
    for (int i = lambdaForm.arity; i < lambdaForm.names.length; i++)
    {
      LambdaForm.Name localName = lambdaForm.names[i];
      emitStoreResult((LambdaForm.Name)localObject1);
      localObject1 = localName;
      MethodHandleImpl.Intrinsic localIntrinsic = function.intrinsicName();
      switch (localIntrinsic)
      {
      case SELECT_ALTERNATIVE: 
        assert (isSelectAlternative(i));
        if (MethodHandleStatics.PROFILE_GWT)
        {
          assert (((arguments[0] instanceof LambdaForm.Name)) && (nameRefersTo((LambdaForm.Name)arguments[0], MethodHandleImpl.class, "profileBoolean")));
          mv.visitAnnotation("Ljava/lang/invoke/InjectedProfile;", true);
        }
        localObject1 = emitSelectAlternative(localName, lambdaForm.names[(i + 1)]);
        i++;
        break;
      case GUARD_WITH_CATCH: 
        assert (isGuardWithCatch(i));
        localObject1 = emitGuardWithCatch(i);
        i += 2;
        break;
      case NEW_ARRAY: 
        localObject2 = function.methodType().returnType();
        if (isStaticallyNameable((Class)localObject2)) {
          emitNewArray(localName);
        }
        break;
      case ARRAY_LOAD: 
        emitArrayLoad(localName);
        break;
      case ARRAY_STORE: 
        emitArrayStore(localName);
        break;
      case IDENTITY: 
        assert (arguments.length == 1);
        emitPushArguments(localName);
        break;
      case ZERO: 
        assert (arguments.length == 0);
        emitConst(type.basicTypeWrapper().zero());
        break;
      case NONE: 
        break;
      default: 
        throw MethodHandleStatics.newInternalError("Unknown intrinsic: " + localIntrinsic);
      }
      Object localObject2 = function.member();
      if (isStaticallyInvocable((MemberName)localObject2)) {
        emitStaticInvoke((MemberName)localObject2, localName);
      } else {
        emitInvoke(localName);
      }
    }
    emitReturn((LambdaForm.Name)localObject1);
    classFileEpilogue();
    bogusMethod(new Object[] { lambdaForm });
    byte[] arrayOfByte = cw.toByteArray();
    maybeDump(className, arrayOfByte);
    return arrayOfByte;
  }
  
  void emitArrayLoad(LambdaForm.Name paramName)
  {
    emitArrayOp(paramName, 50);
  }
  
  void emitArrayStore(LambdaForm.Name paramName)
  {
    emitArrayOp(paramName, 83);
  }
  
  void emitArrayOp(LambdaForm.Name paramName, int paramInt)
  {
    assert ((paramInt == 50) || (paramInt == 83));
    Class localClass = function.methodType().parameterType(0).getComponentType();
    assert (localClass != null);
    emitPushArguments(paramName);
    if (localClass.isPrimitive())
    {
      Wrapper localWrapper = Wrapper.forPrimitiveType(localClass);
      paramInt = arrayInsnOpcode(arrayTypeCode(localWrapper), paramInt);
    }
    mv.visitInsn(paramInt);
  }
  
  void emitInvoke(LambdaForm.Name paramName)
  {
    assert (!isLinkerMethodInvoke(paramName));
    Object localObject = function.resolvedHandle;
    assert (localObject != null) : paramName.exprString();
    mv.visitLdcInsn(constantPlaceholder(localObject));
    emitReferenceCast(MethodHandle.class, localObject);
    emitPushArguments(paramName);
    localObject = function.methodType();
    mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", ((MethodType)localObject).basicType().toMethodDescriptorString(), false);
  }
  
  static boolean isStaticallyInvocable(LambdaForm.Name paramName)
  {
    return isStaticallyInvocable(function.member());
  }
  
  static boolean isStaticallyInvocable(MemberName paramMemberName)
  {
    if (paramMemberName == null) {
      return false;
    }
    if (paramMemberName.isConstructor()) {
      return false;
    }
    Class localClass1 = paramMemberName.getDeclaringClass();
    if ((localClass1.isArray()) || (localClass1.isPrimitive())) {
      return false;
    }
    if ((localClass1.isAnonymousClass()) || (localClass1.isLocalClass())) {
      return false;
    }
    if (localClass1.getClassLoader() != MethodHandle.class.getClassLoader()) {
      return false;
    }
    if (ReflectUtil.isVMAnonymousClass(localClass1)) {
      return false;
    }
    MethodType localMethodType = paramMemberName.getMethodOrFieldType();
    if (!isStaticallyNameable(localMethodType.returnType())) {
      return false;
    }
    for (Class localClass2 : localMethodType.parameterArray()) {
      if (!isStaticallyNameable(localClass2)) {
        return false;
      }
    }
    if ((!paramMemberName.isPrivate()) && (VerifyAccess.isSamePackage(MethodHandle.class, localClass1))) {
      return true;
    }
    return (paramMemberName.isPublic()) && (isStaticallyNameable(localClass1));
  }
  
  static boolean isStaticallyNameable(Class<?> paramClass)
  {
    if (paramClass == Object.class) {
      return true;
    }
    while (paramClass.isArray()) {
      paramClass = paramClass.getComponentType();
    }
    if (paramClass.isPrimitive()) {
      return true;
    }
    if (ReflectUtil.isVMAnonymousClass(paramClass)) {
      return false;
    }
    if (paramClass.getClassLoader() != Object.class.getClassLoader()) {
      return false;
    }
    if (VerifyAccess.isSamePackage(MethodHandle.class, paramClass)) {
      return true;
    }
    if (!Modifier.isPublic(paramClass.getModifiers())) {
      return false;
    }
    for (Class localClass : STATICALLY_INVOCABLE_PACKAGES) {
      if (VerifyAccess.isSamePackage(localClass, paramClass)) {
        return true;
      }
    }
    return false;
  }
  
  void emitStaticInvoke(LambdaForm.Name paramName)
  {
    emitStaticInvoke(function.member(), paramName);
  }
  
  void emitStaticInvoke(MemberName paramMemberName, LambdaForm.Name paramName)
  {
    assert (paramMemberName.equals(function.member()));
    Class localClass1 = paramMemberName.getDeclaringClass();
    String str1 = getInternalName(localClass1);
    String str2 = paramMemberName.getName();
    byte b = paramMemberName.getReferenceKind();
    if (b == 7)
    {
      assert (paramMemberName.canBeStaticallyBound()) : paramMemberName;
      b = 5;
    }
    if ((paramMemberName.getDeclaringClass().isInterface()) && (b == 5)) {
      b = 9;
    }
    emitPushArguments(paramName);
    String str3;
    if (paramMemberName.isMethod())
    {
      str3 = paramMemberName.getMethodType().toMethodDescriptorString();
      mv.visitMethodInsn(refKindOpcode(b), str1, str2, str3, paramMemberName.getDeclaringClass().isInterface());
    }
    else
    {
      str3 = MethodType.toFieldDescriptorString(paramMemberName.getFieldType());
      mv.visitFieldInsn(refKindOpcode(b), str1, str2, str3);
    }
    if (type == LambdaForm.BasicType.L_TYPE)
    {
      Class localClass2 = paramMemberName.getInvocationType().returnType();
      assert (!localClass2.isPrimitive());
      if ((localClass2 != Object.class) && (!localClass2.isInterface())) {
        assertStaticType(localClass2, paramName);
      }
    }
  }
  
  void emitNewArray(LambdaForm.Name paramName)
    throws InternalError
  {
    Class localClass = function.methodType().returnType();
    if (arguments.length == 0)
    {
      try
      {
        localObject = function.resolvedHandle.invoke();
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.newInternalError(localThrowable);
      }
      assert (Array.getLength(localObject) == 0);
      assert (localObject.getClass() == localClass);
      mv.visitLdcInsn(constantPlaceholder(localObject));
      emitReferenceCast(localClass, localObject);
      return;
    }
    Object localObject = localClass.getComponentType();
    assert (localObject != null);
    emitIconstInsn(arguments.length);
    int i = 83;
    if (!((Class)localObject).isPrimitive())
    {
      mv.visitTypeInsn(189, getInternalName((Class)localObject));
    }
    else
    {
      j = arrayTypeCode(Wrapper.forPrimitiveType((Class)localObject));
      i = arrayInsnOpcode(j, i);
      mv.visitIntInsn(188, j);
    }
    for (int j = 0; j < arguments.length; j++)
    {
      mv.visitInsn(89);
      emitIconstInsn(j);
      emitPushArgument(paramName, j);
      mv.visitInsn(i);
    }
    assertStaticType(localClass, paramName);
  }
  
  int refKindOpcode(byte paramByte)
  {
    switch (paramByte)
    {
    case 5: 
      return 182;
    case 6: 
      return 184;
    case 7: 
      return 183;
    case 9: 
      return 185;
    case 1: 
      return 180;
    case 3: 
      return 181;
    case 2: 
      return 178;
    case 4: 
      return 179;
    }
    throw new InternalError("refKind=" + paramByte);
  }
  
  private boolean memberRefersTo(MemberName paramMemberName, Class<?> paramClass, String paramString)
  {
    return (paramMemberName != null) && (paramMemberName.getDeclaringClass() == paramClass) && (paramMemberName.getName().equals(paramString));
  }
  
  private boolean nameRefersTo(LambdaForm.Name paramName, Class<?> paramClass, String paramString)
  {
    return (function != null) && (memberRefersTo(function.member(), paramClass, paramString));
  }
  
  private boolean isInvokeBasic(LambdaForm.Name paramName)
  {
    if (function == null) {
      return false;
    }
    if (arguments.length < 1) {
      return false;
    }
    MemberName localMemberName = function.member();
    return (memberRefersTo(localMemberName, MethodHandle.class, "invokeBasic")) && (!localMemberName.isPublic()) && (!localMemberName.isStatic());
  }
  
  private boolean isLinkerMethodInvoke(LambdaForm.Name paramName)
  {
    if (function == null) {
      return false;
    }
    if (arguments.length < 1) {
      return false;
    }
    MemberName localMemberName = function.member();
    return (localMemberName != null) && (localMemberName.getDeclaringClass() == MethodHandle.class) && (!localMemberName.isPublic()) && (localMemberName.isStatic()) && (localMemberName.getName().startsWith("linkTo"));
  }
  
  private boolean isSelectAlternative(int paramInt)
  {
    if (paramInt + 1 >= lambdaForm.names.length) {
      return false;
    }
    LambdaForm.Name localName1 = lambdaForm.names[paramInt];
    LambdaForm.Name localName2 = lambdaForm.names[(paramInt + 1)];
    return (nameRefersTo(localName1, MethodHandleImpl.class, "selectAlternative")) && (isInvokeBasic(localName2)) && (localName2.lastUseIndex(localName1) == 0) && (lambdaForm.lastUseIndex(localName1) == paramInt + 1);
  }
  
  private boolean isGuardWithCatch(int paramInt)
  {
    if (paramInt + 2 >= lambdaForm.names.length) {
      return false;
    }
    LambdaForm.Name localName1 = lambdaForm.names[paramInt];
    LambdaForm.Name localName2 = lambdaForm.names[(paramInt + 1)];
    LambdaForm.Name localName3 = lambdaForm.names[(paramInt + 2)];
    return (nameRefersTo(localName2, MethodHandleImpl.class, "guardWithCatch")) && (isInvokeBasic(localName1)) && (isInvokeBasic(localName3)) && (localName2.lastUseIndex(localName1) == 3) && (lambdaForm.lastUseIndex(localName1) == paramInt + 1) && (localName3.lastUseIndex(localName2) == 1) && (lambdaForm.lastUseIndex(localName2) == paramInt + 2);
  }
  
  private LambdaForm.Name emitSelectAlternative(LambdaForm.Name paramName1, LambdaForm.Name paramName2)
  {
    assert (isStaticallyInvocable(paramName2));
    LambdaForm.Name localName = (LambdaForm.Name)arguments[0];
    Label localLabel1 = new Label();
    Label localLabel2 = new Label();
    emitPushArgument(paramName1, 0);
    mv.visitJumpInsn(153, localLabel1);
    Class[] arrayOfClass = (Class[])localClasses.clone();
    emitPushArgument(paramName1, 1);
    emitAstoreInsn(localName.index());
    emitStaticInvoke(paramName2);
    mv.visitJumpInsn(167, localLabel2);
    mv.visitLabel(localLabel1);
    System.arraycopy(arrayOfClass, 0, localClasses, 0, arrayOfClass.length);
    emitPushArgument(paramName1, 2);
    emitAstoreInsn(localName.index());
    emitStaticInvoke(paramName2);
    mv.visitLabel(localLabel2);
    System.arraycopy(arrayOfClass, 0, localClasses, 0, arrayOfClass.length);
    return paramName2;
  }
  
  private LambdaForm.Name emitGuardWithCatch(int paramInt)
  {
    LambdaForm.Name localName1 = lambdaForm.names[paramInt];
    LambdaForm.Name localName2 = lambdaForm.names[(paramInt + 1)];
    LambdaForm.Name localName3 = lambdaForm.names[(paramInt + 2)];
    Label localLabel1 = new Label();
    Label localLabel2 = new Label();
    Label localLabel3 = new Label();
    Label localLabel4 = new Label();
    Class localClass = function.resolvedHandle.type().returnType();
    MethodType localMethodType1 = function.resolvedHandle.type().dropParameterTypes(0, 1).changeReturnType(localClass);
    mv.visitTryCatchBlock(localLabel1, localLabel2, localLabel3, "java/lang/Throwable");
    mv.visitLabel(localLabel1);
    emitPushArgument(localName2, 0);
    emitPushArguments(localName1, 1);
    mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", localMethodType1.basicType().toMethodDescriptorString(), false);
    mv.visitLabel(localLabel2);
    mv.visitJumpInsn(167, localLabel4);
    mv.visitLabel(localLabel3);
    mv.visitInsn(89);
    emitPushArgument(localName2, 1);
    mv.visitInsn(95);
    mv.visitMethodInsn(182, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
    Label localLabel5 = new Label();
    mv.visitJumpInsn(153, localLabel5);
    emitPushArgument(localName2, 2);
    mv.visitInsn(95);
    emitPushArguments(localName1, 1);
    MethodType localMethodType2 = localMethodType1.insertParameterTypes(0, new Class[] { Throwable.class });
    mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", localMethodType2.basicType().toMethodDescriptorString(), false);
    mv.visitJumpInsn(167, localLabel4);
    mv.visitLabel(localLabel5);
    mv.visitInsn(191);
    mv.visitLabel(localLabel4);
    return localName3;
  }
  
  private void emitPushArguments(LambdaForm.Name paramName)
  {
    emitPushArguments(paramName, 0);
  }
  
  private void emitPushArguments(LambdaForm.Name paramName, int paramInt)
  {
    for (int i = paramInt; i < arguments.length; i++) {
      emitPushArgument(paramName, i);
    }
  }
  
  private void emitPushArgument(LambdaForm.Name paramName, int paramInt)
  {
    Object localObject = arguments[paramInt];
    Class localClass = function.methodType().parameterType(paramInt);
    emitPushArgument(localClass, localObject);
  }
  
  private void emitPushArgument(Class<?> paramClass, Object paramObject)
  {
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.basicType(paramClass);
    if ((paramObject instanceof LambdaForm.Name))
    {
      LambdaForm.Name localName = (LambdaForm.Name)paramObject;
      emitLoadInsn(type, localName.index());
      emitImplicitConversion(type, paramClass, localName);
    }
    else if (((paramObject == null) || ((paramObject instanceof String))) && (localBasicType == LambdaForm.BasicType.L_TYPE))
    {
      emitConst(paramObject);
    }
    else if ((Wrapper.isWrapperType(paramObject.getClass())) && (localBasicType != LambdaForm.BasicType.L_TYPE))
    {
      emitConst(paramObject);
    }
    else
    {
      mv.visitLdcInsn(constantPlaceholder(paramObject));
      emitImplicitConversion(LambdaForm.BasicType.L_TYPE, paramClass, paramObject);
    }
  }
  
  private void emitStoreResult(LambdaForm.Name paramName)
  {
    if ((paramName != null) && (type != LambdaForm.BasicType.V_TYPE)) {
      emitStoreInsn(type, paramName.index());
    }
  }
  
  private void emitReturn(LambdaForm.Name paramName)
  {
    Class localClass = invokerType.returnType();
    LambdaForm.BasicType localBasicType = lambdaForm.returnType();
    assert (localBasicType == LambdaForm.BasicType.basicType(localClass));
    if (localBasicType == LambdaForm.BasicType.V_TYPE)
    {
      mv.visitInsn(177);
    }
    else
    {
      LambdaForm.Name localName = lambdaForm.names[lambdaForm.result];
      if (localName != paramName) {
        emitLoadInsn(localBasicType, lambdaForm.result);
      }
      emitImplicitConversion(localBasicType, localClass, localName);
      emitReturnInsn(localBasicType);
    }
  }
  
  private void emitPrimCast(Wrapper paramWrapper1, Wrapper paramWrapper2)
  {
    if (paramWrapper1 == paramWrapper2) {
      return;
    }
    if (paramWrapper1.isSubwordOrInt())
    {
      emitI2X(paramWrapper2);
    }
    else if (paramWrapper2.isSubwordOrInt())
    {
      emitX2I(paramWrapper1);
      if (paramWrapper2.bitWidth() < 32) {
        emitI2X(paramWrapper2);
      }
    }
    else
    {
      int i = 0;
      switch (paramWrapper1)
      {
      case LONG: 
        switch (paramWrapper2)
        {
        case FLOAT: 
          mv.visitInsn(137);
          break;
        case DOUBLE: 
          mv.visitInsn(138);
          break;
        default: 
          i = 1;
        }
        break;
      case FLOAT: 
        switch (paramWrapper2)
        {
        case LONG: 
          mv.visitInsn(140);
          break;
        case DOUBLE: 
          mv.visitInsn(141);
          break;
        default: 
          i = 1;
        }
        break;
      case DOUBLE: 
        switch (paramWrapper2)
        {
        case LONG: 
          mv.visitInsn(143);
          break;
        case FLOAT: 
          mv.visitInsn(144);
          break;
        default: 
          i = 1;
        }
        break;
      default: 
        i = 1;
      }
      if (i != 0) {
        throw new IllegalStateException("unhandled prim cast: " + paramWrapper1 + "2" + paramWrapper2);
      }
    }
  }
  
  private void emitI2X(Wrapper paramWrapper)
  {
    switch (paramWrapper)
    {
    case BYTE: 
      mv.visitInsn(145);
      break;
    case SHORT: 
      mv.visitInsn(147);
      break;
    case CHAR: 
      mv.visitInsn(146);
      break;
    case INT: 
      break;
    case LONG: 
      mv.visitInsn(133);
      break;
    case FLOAT: 
      mv.visitInsn(134);
      break;
    case DOUBLE: 
      mv.visitInsn(135);
      break;
    case BOOLEAN: 
      mv.visitInsn(4);
      mv.visitInsn(126);
      break;
    default: 
      throw new InternalError("unknown type: " + paramWrapper);
    }
  }
  
  private void emitX2I(Wrapper paramWrapper)
  {
    switch (paramWrapper)
    {
    case LONG: 
      mv.visitInsn(136);
      break;
    case FLOAT: 
      mv.visitInsn(139);
      break;
    case DOUBLE: 
      mv.visitInsn(142);
      break;
    default: 
      throw new InternalError("unknown type: " + paramWrapper);
    }
  }
  
  static MemberName generateLambdaFormInterpreterEntryPoint(String paramString)
  {
    assert (LambdaForm.isValidSignature(paramString));
    String str = "interpret_" + LambdaForm.signatureReturn(paramString).basicTypeChar();
    MethodType localMethodType = LambdaForm.signatureType(paramString);
    localMethodType = localMethodType.changeParameterType(0, MethodHandle.class);
    InvokerBytecodeGenerator localInvokerBytecodeGenerator = new InvokerBytecodeGenerator("LFI", str, localMethodType);
    return localInvokerBytecodeGenerator.loadMethod(localInvokerBytecodeGenerator.generateLambdaFormInterpreterEntryPointBytes());
  }
  
  private byte[] generateLambdaFormInterpreterEntryPointBytes()
  {
    classFilePrologue();
    mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
    emitIconstInsn(invokerType.parameterCount());
    mv.visitTypeInsn(189, "java/lang/Object");
    for (int i = 0; i < invokerType.parameterCount(); i++)
    {
      localObject = invokerType.parameterType(i);
      mv.visitInsn(89);
      emitIconstInsn(i);
      emitLoadInsn(LambdaForm.BasicType.basicType((Class)localObject), i);
      if (((Class)localObject).isPrimitive()) {
        emitBoxing(Wrapper.forPrimitiveType((Class)localObject));
      }
      mv.visitInsn(83);
    }
    emitAloadInsn(0);
    mv.visitFieldInsn(180, "java/lang/invoke/MethodHandle", "form", "Ljava/lang/invoke/LambdaForm;");
    mv.visitInsn(95);
    mv.visitMethodInsn(182, "java/lang/invoke/LambdaForm", "interpretWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
    Class localClass = invokerType.returnType();
    if ((localClass.isPrimitive()) && (localClass != Void.TYPE)) {
      emitUnboxing(Wrapper.forPrimitiveType(localClass));
    }
    emitReturnInsn(LambdaForm.BasicType.basicType(localClass));
    classFileEpilogue();
    bogusMethod(new Object[] { invokerType });
    Object localObject = cw.toByteArray();
    maybeDump(className, (byte[])localObject);
    return (byte[])localObject;
  }
  
  static MemberName generateNamedFunctionInvoker(MethodTypeForm paramMethodTypeForm)
  {
    MethodType localMethodType = LambdaForm.NamedFunction.INVOKER_METHOD_TYPE;
    String str = "invoke_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(paramMethodTypeForm.erasedType()));
    InvokerBytecodeGenerator localInvokerBytecodeGenerator = new InvokerBytecodeGenerator("NFI", str, localMethodType);
    return localInvokerBytecodeGenerator.loadMethod(localInvokerBytecodeGenerator.generateNamedFunctionInvokerImpl(paramMethodTypeForm));
  }
  
  private byte[] generateNamedFunctionInvokerImpl(MethodTypeForm paramMethodTypeForm)
  {
    MethodType localMethodType = paramMethodTypeForm.erasedType();
    classFilePrologue();
    mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
    emitAloadInsn(0);
    Object localObject2;
    for (int i = 0; i < localMethodType.parameterCount(); i++)
    {
      emitAloadInsn(1);
      emitIconstInsn(i);
      mv.visitInsn(50);
      localClass = localMethodType.parameterType(i);
      if (localClass.isPrimitive())
      {
        localObject1 = localMethodType.basicType().wrap().parameterType(i);
        localObject2 = Wrapper.forBasicType(localClass);
        Object localObject3 = ((Wrapper)localObject2).isSubwordOrInt() ? Wrapper.INT : localObject2;
        emitUnboxing((Wrapper)localObject3);
        emitPrimCast((Wrapper)localObject3, (Wrapper)localObject2);
      }
    }
    String str = localMethodType.basicType().toMethodDescriptorString();
    mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", str, false);
    Class localClass = localMethodType.returnType();
    if ((localClass != Void.TYPE) && (localClass.isPrimitive()))
    {
      localObject1 = Wrapper.forBasicType(localClass);
      localObject2 = ((Wrapper)localObject1).isSubwordOrInt() ? Wrapper.INT : localObject1;
      emitPrimCast((Wrapper)localObject1, (Wrapper)localObject2);
      emitBoxing((Wrapper)localObject2);
    }
    if (localClass == Void.TYPE) {
      mv.visitInsn(1);
    }
    emitReturnInsn(LambdaForm.BasicType.L_TYPE);
    classFileEpilogue();
    bogusMethod(new Object[] { localMethodType });
    Object localObject1 = cw.toByteArray();
    maybeDump(className, (byte[])localObject1);
    return (byte[])localObject1;
  }
  
  private void bogusMethod(Object... paramVarArgs)
  {
    if (MethodHandleStatics.DUMP_CLASS_FILES)
    {
      mv = cw.visitMethod(8, "dummy", "()V", null, null);
      for (Object localObject : paramVarArgs)
      {
        mv.visitLdcInsn(localObject.toString());
        mv.visitInsn(87);
      }
      mv.visitInsn(177);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
    }
  }
  
  static
  {
    MEMBERNAME_FACTORY = MemberName.getFactory();
    HOST_CLASS = LambdaForm.class;
    if (MethodHandleStatics.DUMP_CLASS_FILES)
    {
      DUMP_CLASS_FILES_COUNTERS = new HashMap();
      try
      {
        File localFile = new File("DUMP_CLASS_FILES");
        if (!localFile.exists()) {
          localFile.mkdirs();
        }
        DUMP_CLASS_FILES_DIR = localFile;
        System.out.println("Dumping class files to " + DUMP_CLASS_FILES_DIR + "/...");
      }
      catch (Exception localException)
      {
        throw MethodHandleStatics.newInternalError(localException);
      }
    }
    else
    {
      DUMP_CLASS_FILES_COUNTERS = null;
      DUMP_CLASS_FILES_DIR = null;
    }
  }
  
  class CpPatch
  {
    final int index;
    final String placeholder;
    final Object value;
    
    CpPatch(int paramInt, String paramString, Object paramObject)
    {
      index = paramInt;
      placeholder = paramString;
      value = paramObject;
    }
    
    public String toString()
    {
      return "CpPatch/index=" + index + ",placeholder=" + placeholder + ",value=" + value;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\InvokerBytecodeGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */