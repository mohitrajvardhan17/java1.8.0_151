package java.lang.invoke;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;

abstract class BoundMethodHandle
  extends MethodHandle
{
  private static final int FIELD_COUNT_THRESHOLD = 12;
  private static final int FORM_EXPRESSION_THRESHOLD = 24;
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.Lookup.IMPL_LOOKUP;
  static final SpeciesData SPECIES_DATA = SpeciesData.EMPTY;
  private static final SpeciesData[] SPECIES_DATA_CACHE = new SpeciesData[5];
  
  BoundMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    super(paramMethodType, paramLambdaForm);
    assert (speciesData() == speciesData(paramLambdaForm));
  }
  
  static BoundMethodHandle bindSingle(MethodType paramMethodType, LambdaForm paramLambdaForm, LambdaForm.BasicType paramBasicType, Object paramObject)
  {
    try
    {
      switch (paramBasicType)
      {
      case L_TYPE: 
        return bindSingle(paramMethodType, paramLambdaForm, paramObject);
      case I_TYPE: 
        return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ValueConversions.widenSubword(paramObject));
      case J_TYPE: 
        return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ((Long)paramObject).longValue());
      case F_TYPE: 
        return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ((Float)paramObject).floatValue());
      case D_TYPE: 
        return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ((Double)paramObject).doubleValue());
      }
      throw MethodHandleStatics.newInternalError("unexpected xtype: " + paramBasicType);
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.newInternalError(localThrowable);
    }
  }
  
  LambdaFormEditor editor()
  {
    return form.editor();
  }
  
  static BoundMethodHandle bindSingle(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject)
  {
    return Species_L.make(paramMethodType, paramLambdaForm, paramObject);
  }
  
  BoundMethodHandle bindArgumentL(int paramInt, Object paramObject)
  {
    return editor().bindArgumentL(this, paramInt, paramObject);
  }
  
  BoundMethodHandle bindArgumentI(int paramInt1, int paramInt2)
  {
    return editor().bindArgumentI(this, paramInt1, paramInt2);
  }
  
  BoundMethodHandle bindArgumentJ(int paramInt, long paramLong)
  {
    return editor().bindArgumentJ(this, paramInt, paramLong);
  }
  
  BoundMethodHandle bindArgumentF(int paramInt, float paramFloat)
  {
    return editor().bindArgumentF(this, paramInt, paramFloat);
  }
  
  BoundMethodHandle bindArgumentD(int paramInt, double paramDouble)
  {
    return editor().bindArgumentD(this, paramInt, paramDouble);
  }
  
  BoundMethodHandle rebind()
  {
    if (!tooComplex()) {
      return this;
    }
    return makeReinvoker(this);
  }
  
  private boolean tooComplex()
  {
    return (fieldCount() > 12) || (form.expressionCount() > 24);
  }
  
  static BoundMethodHandle makeReinvoker(MethodHandle paramMethodHandle)
  {
    LambdaForm localLambdaForm = DelegatingMethodHandle.makeReinvokerForm(paramMethodHandle, 7, Species_L.SPECIES_DATA, Species_L.SPECIES_DATA.getterFunction(0));
    return Species_L.make(paramMethodHandle.type(), localLambdaForm, paramMethodHandle);
  }
  
  abstract SpeciesData speciesData();
  
  static SpeciesData speciesData(LambdaForm paramLambdaForm)
  {
    Object localObject = names[0].constraint;
    if ((localObject instanceof SpeciesData)) {
      return (SpeciesData)localObject;
    }
    return SpeciesData.EMPTY;
  }
  
  abstract int fieldCount();
  
  Object internalProperties()
  {
    return "\n& BMH=" + internalValues();
  }
  
  final Object internalValues()
  {
    Object[] arrayOfObject = new Object[speciesData().fieldCount()];
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfObject[i] = arg(i);
    }
    return Arrays.asList(arrayOfObject);
  }
  
  final Object arg(int paramInt)
  {
    try
    {
      switch (speciesData().fieldType(paramInt))
      {
      case L_TYPE: 
        return speciesDatagetters[paramInt].invokeBasic(this);
      case I_TYPE: 
        return Integer.valueOf(speciesDatagetters[paramInt].invokeBasic(this));
      case J_TYPE: 
        return Long.valueOf(speciesDatagetters[paramInt].invokeBasic(this));
      case F_TYPE: 
        return Float.valueOf(speciesDatagetters[paramInt].invokeBasic(this));
      case D_TYPE: 
        return Double.valueOf(speciesDatagetters[paramInt].invokeBasic(this));
      }
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.newInternalError(localThrowable);
    }
    throw new InternalError("unexpected type: " + speciesDatatypeChars + "." + paramInt);
  }
  
  abstract BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm);
  
  abstract BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject);
  
  abstract BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt);
  
  abstract BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong);
  
  abstract BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat);
  
  abstract BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble);
  
  static SpeciesData getSpeciesData(String paramString)
  {
    return SpeciesData.get(paramString);
  }
  
  private static SpeciesData checkCache(int paramInt, String paramString)
  {
    int i = paramInt - 1;
    SpeciesData localSpeciesData = SPECIES_DATA_CACHE[i];
    if (localSpeciesData != null) {
      return localSpeciesData;
    }
    SPECIES_DATA_CACHE[i] = (localSpeciesData = getSpeciesData(paramString));
    return localSpeciesData;
  }
  
  static SpeciesData speciesData_L()
  {
    return checkCache(1, "L");
  }
  
  static SpeciesData speciesData_LL()
  {
    return checkCache(2, "LL");
  }
  
  static SpeciesData speciesData_LLL()
  {
    return checkCache(3, "LLL");
  }
  
  static SpeciesData speciesData_LLLL()
  {
    return checkCache(4, "LLLL");
  }
  
  static SpeciesData speciesData_LLLLL()
  {
    return checkCache(5, "LLLLL");
  }
  
  static class Factory
  {
    static final String JLO_SIG = "Ljava/lang/Object;";
    static final String JLS_SIG = "Ljava/lang/String;";
    static final String JLC_SIG = "Ljava/lang/Class;";
    static final String MH = "java/lang/invoke/MethodHandle";
    static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
    static final String BMH = "java/lang/invoke/BoundMethodHandle";
    static final String BMH_SIG = "Ljava/lang/invoke/BoundMethodHandle;";
    static final String SPECIES_DATA = "java/lang/invoke/BoundMethodHandle$SpeciesData";
    static final String SPECIES_DATA_SIG = "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    static final String STABLE_SIG = "Ljava/lang/invoke/Stable;";
    static final String SPECIES_PREFIX_NAME = "Species_";
    static final String SPECIES_PREFIX_PATH = "java/lang/invoke/BoundMethodHandle$Species_";
    static final String BMHSPECIES_DATA_EWI_SIG = "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    static final String BMHSPECIES_DATA_GFC_SIG = "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    static final String MYSPECIES_DATA_SIG = "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    static final String VOID_SIG = "()V";
    static final String INT_SIG = "()I";
    static final String SIG_INCIPIT = "(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;";
    static final String[] E_THROWABLE = { "java/lang/Throwable" };
    static final ConcurrentMap<String, Class<? extends BoundMethodHandle>> CLASS_CACHE = new ConcurrentHashMap();
    
    Factory() {}
    
    static Class<? extends BoundMethodHandle> getConcreteBMHClass(String paramString)
    {
      (Class)CLASS_CACHE.computeIfAbsent(paramString, new Function()
      {
        public Class<? extends BoundMethodHandle> apply(String paramAnonymousString)
        {
          return BoundMethodHandle.Factory.generateConcreteBMHClass(paramAnonymousString);
        }
      });
    }
    
    static Class<? extends BoundMethodHandle> generateConcreteBMHClass(String paramString)
    {
      ClassWriter localClassWriter = new ClassWriter(3);
      String str1 = LambdaForm.shortenSignature(paramString);
      String str2 = "java/lang/invoke/BoundMethodHandle$Species_" + str1;
      String str3 = "Species_" + str1;
      localClassWriter.visit(50, 48, str2, null, "java/lang/invoke/BoundMethodHandle", null);
      localClassWriter.visitSource(str3, null);
      FieldVisitor localFieldVisitor = localClassWriter.visitField(8, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", null, null);
      localFieldVisitor.visitAnnotation("Ljava/lang/invoke/Stable;", true);
      localFieldVisitor.visitEnd();
      for (int i = 0; i < paramString.length(); i++)
      {
        j = paramString.charAt(i);
        String str4 = makeFieldName(paramString, i);
        String str5 = j == 76 ? "Ljava/lang/Object;" : String.valueOf(j);
        localClassWriter.visitField(16, str4, str5, null, null).visitEnd();
      }
      MethodVisitor localMethodVisitor = localClassWriter.visitMethod(2, "<init>", makeSignature(paramString, true), null, null);
      localMethodVisitor.visitCode();
      localMethodVisitor.visitVarInsn(25, 0);
      localMethodVisitor.visitVarInsn(25, 1);
      localMethodVisitor.visitVarInsn(25, 2);
      localMethodVisitor.visitMethodInsn(183, "java/lang/invoke/BoundMethodHandle", "<init>", makeSignature("", true), false);
      int j = 0;
      for (int m = 0; j < paramString.length(); m++)
      {
        c1 = paramString.charAt(j);
        localMethodVisitor.visitVarInsn(25, 0);
        localMethodVisitor.visitVarInsn(typeLoadOp(c1), m + 3);
        localMethodVisitor.visitFieldInsn(181, str2, makeFieldName(paramString, j), typeSig(c1));
        if ((c1 == 'J') || (c1 == 'D')) {
          m++;
        }
        j++;
      }
      localMethodVisitor.visitInsn(177);
      localMethodVisitor.visitMaxs(0, 0);
      localMethodVisitor.visitEnd();
      localMethodVisitor = localClassWriter.visitMethod(16, "speciesData", "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", null, null);
      localMethodVisitor.visitCode();
      localMethodVisitor.visitFieldInsn(178, str2, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
      localMethodVisitor.visitInsn(176);
      localMethodVisitor.visitMaxs(0, 0);
      localMethodVisitor.visitEnd();
      localMethodVisitor = localClassWriter.visitMethod(16, "fieldCount", "()I", null, null);
      localMethodVisitor.visitCode();
      int k = paramString.length();
      if (k <= 5) {
        localMethodVisitor.visitInsn(3 + k);
      } else {
        localMethodVisitor.visitIntInsn(17, k);
      }
      localMethodVisitor.visitInsn(172);
      localMethodVisitor.visitMaxs(0, 0);
      localMethodVisitor.visitEnd();
      localMethodVisitor = localClassWriter.visitMethod(8, "make", makeSignature(paramString, false), null, null);
      localMethodVisitor.visitCode();
      localMethodVisitor.visitTypeInsn(187, str2);
      localMethodVisitor.visitInsn(89);
      localMethodVisitor.visitVarInsn(25, 0);
      localMethodVisitor.visitVarInsn(25, 1);
      m = 0;
      int i1;
      for (char c1 = '\000'; m < paramString.length(); c1++)
      {
        i1 = paramString.charAt(m);
        localMethodVisitor.visitVarInsn(typeLoadOp(i1), c1 + '\002');
        if ((i1 == 74) || (i1 == 68)) {
          c1++;
        }
        m++;
      }
      localMethodVisitor.visitMethodInsn(183, str2, "<init>", makeSignature(paramString, true), false);
      localMethodVisitor.visitInsn(176);
      localMethodVisitor.visitMaxs(0, 0);
      localMethodVisitor.visitEnd();
      localMethodVisitor = localClassWriter.visitMethod(16, "copyWith", makeSignature("", false), null, null);
      localMethodVisitor.visitCode();
      localMethodVisitor.visitTypeInsn(187, str2);
      localMethodVisitor.visitInsn(89);
      localMethodVisitor.visitVarInsn(25, 1);
      localMethodVisitor.visitVarInsn(25, 2);
      emitPushFields(paramString, str2, localMethodVisitor);
      localMethodVisitor.visitMethodInsn(183, str2, "<init>", makeSignature(paramString, true), false);
      localMethodVisitor.visitInsn(176);
      localMethodVisitor.visitMaxs(0, 0);
      localMethodVisitor.visitEnd();
      for (Object localObject2 : LambdaForm.BasicType.ARG_TYPES)
      {
        int i2 = ((LambdaForm.BasicType)localObject2).ordinal();
        char c2 = ((LambdaForm.BasicType)localObject2).basicTypeChar();
        localMethodVisitor = localClassWriter.visitMethod(16, "copyWithExtend" + c2, makeSignature(String.valueOf(c2), false), null, E_THROWABLE);
        localMethodVisitor.visitCode();
        localMethodVisitor.visitFieldInsn(178, str2, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
        int i3 = 3 + i2;
        assert (i3 <= 8);
        localMethodVisitor.visitInsn(i3);
        localMethodVisitor.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "extendWith", "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", false);
        localMethodVisitor.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "constructor", "()Ljava/lang/invoke/MethodHandle;", false);
        localMethodVisitor.visitVarInsn(25, 1);
        localMethodVisitor.visitVarInsn(25, 2);
        emitPushFields(paramString, str2, localMethodVisitor);
        localMethodVisitor.visitVarInsn(typeLoadOp(c2), 3);
        localMethodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", makeSignature(paramString + c2, false), false);
        localMethodVisitor.visitInsn(176);
        localMethodVisitor.visitMaxs(0, 0);
        localMethodVisitor.visitEnd();
      }
      localClassWriter.visitEnd();
      ??? = localClassWriter.toByteArray();
      InvokerBytecodeGenerator.maybeDump(str2, (byte[])???);
      Class localClass = MethodHandleStatics.UNSAFE.defineClass(str2, (byte[])???, 0, ???.length, BoundMethodHandle.class.getClassLoader(), null).asSubclass(BoundMethodHandle.class);
      return localClass;
    }
    
    private static int typeLoadOp(char paramChar)
    {
      switch (paramChar)
      {
      case 'L': 
        return 25;
      case 'I': 
        return 21;
      case 'J': 
        return 22;
      case 'F': 
        return 23;
      case 'D': 
        return 24;
      }
      throw MethodHandleStatics.newInternalError("unrecognized type " + paramChar);
    }
    
    private static void emitPushFields(String paramString1, String paramString2, MethodVisitor paramMethodVisitor)
    {
      for (int i = 0; i < paramString1.length(); i++)
      {
        char c = paramString1.charAt(i);
        paramMethodVisitor.visitVarInsn(25, 0);
        paramMethodVisitor.visitFieldInsn(180, paramString2, makeFieldName(paramString1, i), typeSig(c));
      }
    }
    
    static String typeSig(char paramChar)
    {
      return paramChar == 'L' ? "Ljava/lang/Object;" : String.valueOf(paramChar);
    }
    
    private static MethodHandle makeGetter(Class<?> paramClass, String paramString, int paramInt)
    {
      String str = makeFieldName(paramString, paramInt);
      Class localClass = Wrapper.forBasicType(paramString.charAt(paramInt)).primitiveType();
      try
      {
        return BoundMethodHandle.LOOKUP.findGetter(paramClass, str, localClass);
      }
      catch (NoSuchFieldException|IllegalAccessException localNoSuchFieldException)
      {
        throw MethodHandleStatics.newInternalError(localNoSuchFieldException);
      }
    }
    
    static MethodHandle[] makeGetters(Class<?> paramClass, String paramString, MethodHandle[] paramArrayOfMethodHandle)
    {
      if (paramArrayOfMethodHandle == null) {
        paramArrayOfMethodHandle = new MethodHandle[paramString.length()];
      }
      for (int i = 0; i < paramArrayOfMethodHandle.length; i++)
      {
        paramArrayOfMethodHandle[i] = makeGetter(paramClass, paramString, i);
        assert (paramArrayOfMethodHandle[i].internalMemberName().getDeclaringClass() == paramClass);
      }
      return paramArrayOfMethodHandle;
    }
    
    static MethodHandle[] makeCtors(Class<? extends BoundMethodHandle> paramClass, String paramString, MethodHandle[] paramArrayOfMethodHandle)
    {
      if (paramArrayOfMethodHandle == null) {
        paramArrayOfMethodHandle = new MethodHandle[1];
      }
      if (paramString.equals("")) {
        return paramArrayOfMethodHandle;
      }
      paramArrayOfMethodHandle[0] = makeCbmhCtor(paramClass, paramString);
      return paramArrayOfMethodHandle;
    }
    
    static LambdaForm.NamedFunction[] makeNominalGetters(String paramString, LambdaForm.NamedFunction[] paramArrayOfNamedFunction, MethodHandle[] paramArrayOfMethodHandle)
    {
      if (paramArrayOfNamedFunction == null) {
        paramArrayOfNamedFunction = new LambdaForm.NamedFunction[paramString.length()];
      }
      for (int i = 0; i < paramArrayOfNamedFunction.length; i++) {
        paramArrayOfNamedFunction[i] = new LambdaForm.NamedFunction(paramArrayOfMethodHandle[i]);
      }
      return paramArrayOfNamedFunction;
    }
    
    static BoundMethodHandle.SpeciesData getSpeciesDataFromConcreteBMHClass(Class<? extends BoundMethodHandle> paramClass)
    {
      try
      {
        Field localField = paramClass.getDeclaredField("SPECIES_DATA");
        return (BoundMethodHandle.SpeciesData)localField.get(null);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
      }
    }
    
    static void setSpeciesDataToConcreteBMHClass(Class<? extends BoundMethodHandle> paramClass, BoundMethodHandle.SpeciesData paramSpeciesData)
    {
      try
      {
        Field localField = paramClass.getDeclaredField("SPECIES_DATA");
        assert (localField.getDeclaredAnnotation(Stable.class) != null);
        localField.set(null, paramSpeciesData);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
      }
    }
    
    private static String makeFieldName(String paramString, int paramInt)
    {
      assert ((paramInt >= 0) && (paramInt < paramString.length()));
      return "arg" + paramString.charAt(paramInt) + paramInt;
    }
    
    private static String makeSignature(String paramString, boolean paramBoolean)
    {
      StringBuilder localStringBuilder = new StringBuilder("(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;");
      for (char c : paramString.toCharArray()) {
        localStringBuilder.append(typeSig(c));
      }
      return ')' + (paramBoolean ? "V" : "Ljava/lang/invoke/BoundMethodHandle;");
    }
    
    static MethodHandle makeCbmhCtor(Class<? extends BoundMethodHandle> paramClass, String paramString)
    {
      try
      {
        return BoundMethodHandle.LOOKUP.findStatic(paramClass, "make", MethodType.fromMethodDescriptorString(makeSignature(paramString, false), null));
      }
      catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|TypeNotPresentException localNoSuchMethodException)
      {
        throw MethodHandleStatics.newInternalError(localNoSuchMethodException);
      }
    }
  }
  
  static class SpeciesData
  {
    private final String typeChars;
    private final LambdaForm.BasicType[] typeCodes;
    private final Class<? extends BoundMethodHandle> clazz;
    @Stable
    private final MethodHandle[] constructor;
    @Stable
    private final MethodHandle[] getters;
    @Stable
    private final LambdaForm.NamedFunction[] nominalGetters;
    @Stable
    private final SpeciesData[] extensions;
    static final SpeciesData EMPTY;
    private static final ConcurrentMap<String, SpeciesData> CACHE;
    private static final boolean INIT_DONE = Boolean.TRUE.booleanValue();
    
    int fieldCount()
    {
      return typeCodes.length;
    }
    
    LambdaForm.BasicType fieldType(int paramInt)
    {
      return typeCodes[paramInt];
    }
    
    char fieldTypeChar(int paramInt)
    {
      return typeChars.charAt(paramInt);
    }
    
    Object fieldSignature()
    {
      return typeChars;
    }
    
    public Class<? extends BoundMethodHandle> fieldHolder()
    {
      return clazz;
    }
    
    public String toString()
    {
      return "SpeciesData<" + fieldSignature() + ">";
    }
    
    LambdaForm.NamedFunction getterFunction(int paramInt)
    {
      LambdaForm.NamedFunction localNamedFunction = nominalGetters[paramInt];
      assert (localNamedFunction.memberDeclaringClassOrNull() == fieldHolder());
      assert (localNamedFunction.returnType() == fieldType(paramInt));
      return localNamedFunction;
    }
    
    LambdaForm.NamedFunction[] getterFunctions()
    {
      return nominalGetters;
    }
    
    MethodHandle[] getterHandles()
    {
      return getters;
    }
    
    MethodHandle constructor()
    {
      return constructor[0];
    }
    
    SpeciesData(String paramString, Class<? extends BoundMethodHandle> paramClass)
    {
      typeChars = paramString;
      typeCodes = LambdaForm.BasicType.basicTypes(paramString);
      clazz = paramClass;
      if (!INIT_DONE)
      {
        constructor = new MethodHandle[1];
        getters = new MethodHandle[paramString.length()];
        nominalGetters = new LambdaForm.NamedFunction[paramString.length()];
      }
      else
      {
        constructor = BoundMethodHandle.Factory.makeCtors(paramClass, paramString, null);
        getters = BoundMethodHandle.Factory.makeGetters(paramClass, paramString, null);
        nominalGetters = BoundMethodHandle.Factory.makeNominalGetters(paramString, null, getters);
      }
      extensions = new SpeciesData[LambdaForm.BasicType.ARG_TYPE_LIMIT];
    }
    
    private void initForBootstrap()
    {
      assert (!INIT_DONE);
      if (constructor() == null)
      {
        String str = typeChars;
        CACHE.put(str, this);
        BoundMethodHandle.Factory.makeCtors(clazz, str, constructor);
        BoundMethodHandle.Factory.makeGetters(clazz, str, getters);
        BoundMethodHandle.Factory.makeNominalGetters(str, nominalGetters, getters);
      }
    }
    
    SpeciesData extendWith(byte paramByte)
    {
      return extendWith(LambdaForm.BasicType.basicType(paramByte));
    }
    
    SpeciesData extendWith(LambdaForm.BasicType paramBasicType)
    {
      int i = paramBasicType.ordinal();
      SpeciesData localSpeciesData = extensions[i];
      if (localSpeciesData != null) {
        return localSpeciesData;
      }
      extensions[i] = (localSpeciesData = get(typeChars + paramBasicType.basicTypeChar()));
      return localSpeciesData;
    }
    
    private static SpeciesData get(String paramString)
    {
      (SpeciesData)CACHE.computeIfAbsent(paramString, new Function()
      {
        public BoundMethodHandle.SpeciesData apply(String paramAnonymousString)
        {
          Class localClass = BoundMethodHandle.Factory.getConcreteBMHClass(paramAnonymousString);
          BoundMethodHandle.SpeciesData localSpeciesData = new BoundMethodHandle.SpeciesData(paramAnonymousString, localClass);
          BoundMethodHandle.Factory.setSpeciesDataToConcreteBMHClass(localClass, localSpeciesData);
          return localSpeciesData;
        }
      });
    }
    
    static boolean speciesDataCachePopulated()
    {
      Class localClass1 = BoundMethodHandle.class;
      try
      {
        for (Class localClass2 : localClass1.getDeclaredClasses()) {
          if (localClass1.isAssignableFrom(localClass2))
          {
            Class localClass3 = localClass2.asSubclass(BoundMethodHandle.class);
            SpeciesData localSpeciesData = BoundMethodHandle.Factory.getSpeciesDataFromConcreteBMHClass(localClass3);
            assert (localSpeciesData != null) : localClass3.getName();
            assert (clazz == localClass3);
            assert (CACHE.get(typeChars) == localSpeciesData);
          }
        }
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.newInternalError(localThrowable);
      }
      return true;
    }
    
    static
    {
      EMPTY = new SpeciesData("", BoundMethodHandle.class);
      CACHE = new ConcurrentHashMap();
      EMPTY.initForBootstrap();
      BoundMethodHandle.Species_L.SPECIES_DATA.initForBootstrap();
      assert (speciesDataCachePopulated());
    }
  }
  
  private static final class Species_L
    extends BoundMethodHandle
  {
    final Object argL0;
    static final BoundMethodHandle.SpeciesData SPECIES_DATA = new BoundMethodHandle.SpeciesData("L", Species_L.class);
    
    private Species_L(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject)
    {
      super(paramLambdaForm);
      argL0 = paramObject;
    }
    
    BoundMethodHandle.SpeciesData speciesData()
    {
      return SPECIES_DATA;
    }
    
    int fieldCount()
    {
      return 1;
    }
    
    static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject)
    {
      return new Species_L(paramMethodType, paramLambdaForm, paramObject);
    }
    
    final BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
    {
      return new Species_L(paramMethodType, paramLambdaForm, argL0);
    }
    
    final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject)
    {
      try
      {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.L_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, argL0, paramObject);
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.uncaughtException(localThrowable);
      }
    }
    
    final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt)
    {
      try
      {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, argL0, paramInt);
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.uncaughtException(localThrowable);
      }
    }
    
    final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong)
    {
      try
      {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, argL0, paramLong);
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.uncaughtException(localThrowable);
      }
    }
    
    final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat)
    {
      try
      {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, argL0, paramFloat);
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.uncaughtException(localThrowable);
      }
    }
    
    final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble)
    {
      try
      {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, argL0, paramDouble);
      }
      catch (Throwable localThrowable)
      {
        throw MethodHandleStatics.uncaughtException(localThrowable);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\BoundMethodHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */