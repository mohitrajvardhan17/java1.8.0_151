package java.lang.invoke;

import java.io.FilePermission;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.PropertyPermission;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

final class InnerClassLambdaMetafactory
  extends AbstractValidatingLambdaMetafactory
{
  private static final Unsafe UNSAFE = ;
  private static final int CLASSFILE_VERSION = 52;
  private static final String METHOD_DESCRIPTOR_VOID = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]);
  private static final String JAVA_LANG_OBJECT = "java/lang/Object";
  private static final String NAME_CTOR = "<init>";
  private static final String NAME_FACTORY = "get$Lambda";
  private static final String NAME_SERIALIZED_LAMBDA = "java/lang/invoke/SerializedLambda";
  private static final String NAME_NOT_SERIALIZABLE_EXCEPTION = "java/io/NotSerializableException";
  private static final String DESCR_METHOD_WRITE_REPLACE = "()Ljava/lang/Object;";
  private static final String DESCR_METHOD_WRITE_OBJECT = "(Ljava/io/ObjectOutputStream;)V";
  private static final String DESCR_METHOD_READ_OBJECT = "(Ljava/io/ObjectInputStream;)V";
  private static final String NAME_METHOD_WRITE_REPLACE = "writeReplace";
  private static final String NAME_METHOD_READ_OBJECT = "readObject";
  private static final String NAME_METHOD_WRITE_OBJECT = "writeObject";
  private static final String DESCR_CTOR_SERIALIZED_LAMBDA = MethodType.methodType(Void.TYPE, Class.class, new Class[] { String.class, String.class, String.class, Integer.TYPE, String.class, String.class, String.class, String.class, Object[].class }).toMethodDescriptorString();
  private static final String DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION = MethodType.methodType(Void.TYPE, String.class).toMethodDescriptorString();
  private static final String[] SER_HOSTILE_EXCEPTIONS = { "java/io/NotSerializableException" };
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final AtomicInteger counter = new AtomicInteger(0);
  private static final ProxyClassesDumper dumper;
  private final String implMethodClassName = implDefiningClass.getName().replace('.', '/');
  private final String implMethodName = implInfo.getName();
  private final String implMethodDesc = implMethodType.toMethodDescriptorString();
  private final Class<?> implMethodReturnClass = implKind == 8 ? implDefiningClass : implMethodType.returnType();
  private final MethodType constructorType;
  private final ClassWriter cw;
  private final String[] argNames;
  private final String[] argDescs;
  private final String lambdaClassName;
  
  public InnerClassLambdaMetafactory(MethodHandles.Lookup paramLookup, MethodType paramMethodType1, String paramString, MethodType paramMethodType2, MethodHandle paramMethodHandle, MethodType paramMethodType3, boolean paramBoolean, Class<?>[] paramArrayOfClass, MethodType[] paramArrayOfMethodType)
    throws LambdaConversionException
  {
    super(paramLookup, paramMethodType1, paramString, paramMethodType2, paramMethodHandle, paramMethodType3, paramBoolean, paramArrayOfClass, paramArrayOfMethodType);
    constructorType = paramMethodType1.changeReturnType(Void.TYPE);
    lambdaClassName = (targetClass.getName().replace('.', '/') + "$$Lambda$" + counter.incrementAndGet());
    cw = new ClassWriter(1);
    int i = paramMethodType1.parameterCount();
    if (i > 0)
    {
      argNames = new String[i];
      argDescs = new String[i];
      for (int j = 0; j < i; j++)
      {
        argNames[j] = ("arg$" + (j + 1));
        argDescs[j] = BytecodeDescriptor.unparse(paramMethodType1.parameterType(j));
      }
    }
    else
    {
      argNames = (argDescs = EMPTY_STRING_ARRAY);
    }
  }
  
  CallSite buildCallSite()
    throws LambdaConversionException
  {
    final Class localClass = spinInnerClass();
    if (invokedType.parameterCount() == 0)
    {
      Constructor[] arrayOfConstructor = (Constructor[])AccessController.doPrivileged(new PrivilegedAction()
      {
        public Constructor<?>[] run()
        {
          Constructor[] arrayOfConstructor = localClass.getDeclaredConstructors();
          if (arrayOfConstructor.length == 1) {
            arrayOfConstructor[0].setAccessible(true);
          }
          return arrayOfConstructor;
        }
      });
      if (arrayOfConstructor.length != 1) {
        throw new LambdaConversionException("Expected one lambda constructor for " + localClass.getCanonicalName() + ", got " + arrayOfConstructor.length);
      }
      try
      {
        Object localObject = arrayOfConstructor[0].newInstance(new Object[0]);
        return new ConstantCallSite(MethodHandles.constant(samBase, localObject));
      }
      catch (ReflectiveOperationException localReflectiveOperationException2)
      {
        throw new LambdaConversionException("Exception instantiating lambda object", localReflectiveOperationException2);
      }
    }
    try
    {
      UNSAFE.ensureClassInitialized(localClass);
      return new ConstantCallSite(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(localClass, "get$Lambda", invokedType));
    }
    catch (ReflectiveOperationException localReflectiveOperationException1)
    {
      throw new LambdaConversionException("Exception finding constructor", localReflectiveOperationException1);
    }
  }
  
  private Class<?> spinInnerClass()
    throws LambdaConversionException
  {
    String str = samBase.getName().replace('.', '/');
    int i = (!isSerializable) && (Serializable.class.isAssignableFrom(samBase)) ? 1 : 0;
    String[] arrayOfString;
    Class localClass;
    if (markerInterfaces.length == 0)
    {
      arrayOfString = new String[] { str };
    }
    else
    {
      LinkedHashSet localLinkedHashSet = new LinkedHashSet(markerInterfaces.length + 1);
      localLinkedHashSet.add(str);
      for (localClass : markerInterfaces)
      {
        localLinkedHashSet.add(localClass.getName().replace('.', '/'));
        i |= ((!isSerializable) && (Serializable.class.isAssignableFrom(localClass)) ? 1 : 0);
      }
      arrayOfString = (String[])localLinkedHashSet.toArray(new String[localLinkedHashSet.size()]);
    }
    cw.visit(52, 4144, lambdaClassName, null, "java/lang/Object", arrayOfString);
    for (int j = 0; j < argDescs.length; j++)
    {
      ??? = cw.visitField(18, argNames[j], argDescs[j], null, null);
      ((FieldVisitor)???).visitEnd();
    }
    generateConstructor();
    if (invokedType.parameterCount() != 0) {
      generateFactory();
    }
    MethodVisitor localMethodVisitor = cw.visitMethod(1, samMethodName, samMethodType.toMethodDescriptorString(), null, null);
    localMethodVisitor.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
    new ForwardingMethodGenerator(localMethodVisitor).generate(samMethodType);
    if (additionalBridges != null) {
      for (localClass : additionalBridges)
      {
        localMethodVisitor = cw.visitMethod(65, samMethodName, localClass.toMethodDescriptorString(), null, null);
        localMethodVisitor.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        new ForwardingMethodGenerator(localMethodVisitor).generate(localClass);
      }
    }
    if (isSerializable) {
      generateSerializationFriendlyMethods();
    } else if (i != 0) {
      generateSerializationHostileMethods();
    }
    cw.visitEnd();
    ??? = cw.toByteArray();
    if (dumper != null) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          InnerClassLambdaMetafactory.dumper.dumpClass(lambdaClassName, localObject);
          return null;
        }
      }, null, new Permission[] { new FilePermission("<<ALL FILES>>", "read, write"), new PropertyPermission("user.dir", "read") });
    }
    return UNSAFE.defineAnonymousClass(targetClass, (byte[])???, null);
  }
  
  private void generateFactory()
  {
    MethodVisitor localMethodVisitor = cw.visitMethod(10, "get$Lambda", invokedType.toMethodDescriptorString(), null, null);
    localMethodVisitor.visitCode();
    localMethodVisitor.visitTypeInsn(187, lambdaClassName);
    localMethodVisitor.visitInsn(89);
    int i = invokedType.parameterCount();
    int j = 0;
    int k = 0;
    while (j < i)
    {
      Class localClass = invokedType.parameterType(j);
      localMethodVisitor.visitVarInsn(getLoadOpcode(localClass), k);
      k += getParameterSize(localClass);
      j++;
    }
    localMethodVisitor.visitMethodInsn(183, lambdaClassName, "<init>", constructorType.toMethodDescriptorString(), false);
    localMethodVisitor.visitInsn(176);
    localMethodVisitor.visitMaxs(-1, -1);
    localMethodVisitor.visitEnd();
  }
  
  private void generateConstructor()
  {
    MethodVisitor localMethodVisitor = cw.visitMethod(2, "<init>", constructorType.toMethodDescriptorString(), null, null);
    localMethodVisitor.visitCode();
    localMethodVisitor.visitVarInsn(25, 0);
    localMethodVisitor.visitMethodInsn(183, "java/lang/Object", "<init>", METHOD_DESCRIPTOR_VOID, false);
    int i = invokedType.parameterCount();
    int j = 0;
    int k = 0;
    while (j < i)
    {
      localMethodVisitor.visitVarInsn(25, 0);
      Class localClass = invokedType.parameterType(j);
      localMethodVisitor.visitVarInsn(getLoadOpcode(localClass), k + 1);
      k += getParameterSize(localClass);
      localMethodVisitor.visitFieldInsn(181, lambdaClassName, argNames[j], argDescs[j]);
      j++;
    }
    localMethodVisitor.visitInsn(177);
    localMethodVisitor.visitMaxs(-1, -1);
    localMethodVisitor.visitEnd();
  }
  
  private void generateSerializationFriendlyMethods()
  {
    TypeConvertingMethodAdapter localTypeConvertingMethodAdapter = new TypeConvertingMethodAdapter(cw.visitMethod(18, "writeReplace", "()Ljava/lang/Object;", null, null));
    localTypeConvertingMethodAdapter.visitCode();
    localTypeConvertingMethodAdapter.visitTypeInsn(187, "java/lang/invoke/SerializedLambda");
    localTypeConvertingMethodAdapter.visitInsn(89);
    localTypeConvertingMethodAdapter.visitLdcInsn(Type.getType(targetClass));
    localTypeConvertingMethodAdapter.visitLdcInsn(invokedType.returnType().getName().replace('.', '/'));
    localTypeConvertingMethodAdapter.visitLdcInsn(samMethodName);
    localTypeConvertingMethodAdapter.visitLdcInsn(samMethodType.toMethodDescriptorString());
    localTypeConvertingMethodAdapter.visitLdcInsn(Integer.valueOf(implInfo.getReferenceKind()));
    localTypeConvertingMethodAdapter.visitLdcInsn(implInfo.getDeclaringClass().getName().replace('.', '/'));
    localTypeConvertingMethodAdapter.visitLdcInsn(implInfo.getName());
    localTypeConvertingMethodAdapter.visitLdcInsn(implInfo.getMethodType().toMethodDescriptorString());
    localTypeConvertingMethodAdapter.visitLdcInsn(instantiatedMethodType.toMethodDescriptorString());
    localTypeConvertingMethodAdapter.iconst(argDescs.length);
    localTypeConvertingMethodAdapter.visitTypeInsn(189, "java/lang/Object");
    for (int i = 0; i < argDescs.length; i++)
    {
      localTypeConvertingMethodAdapter.visitInsn(89);
      localTypeConvertingMethodAdapter.iconst(i);
      localTypeConvertingMethodAdapter.visitVarInsn(25, 0);
      localTypeConvertingMethodAdapter.visitFieldInsn(180, lambdaClassName, argNames[i], argDescs[i]);
      localTypeConvertingMethodAdapter.boxIfTypePrimitive(Type.getType(argDescs[i]));
      localTypeConvertingMethodAdapter.visitInsn(83);
    }
    localTypeConvertingMethodAdapter.visitMethodInsn(183, "java/lang/invoke/SerializedLambda", "<init>", DESCR_CTOR_SERIALIZED_LAMBDA, false);
    localTypeConvertingMethodAdapter.visitInsn(176);
    localTypeConvertingMethodAdapter.visitMaxs(-1, -1);
    localTypeConvertingMethodAdapter.visitEnd();
  }
  
  private void generateSerializationHostileMethods()
  {
    MethodVisitor localMethodVisitor = cw.visitMethod(18, "writeObject", "(Ljava/io/ObjectOutputStream;)V", null, SER_HOSTILE_EXCEPTIONS);
    localMethodVisitor.visitCode();
    localMethodVisitor.visitTypeInsn(187, "java/io/NotSerializableException");
    localMethodVisitor.visitInsn(89);
    localMethodVisitor.visitLdcInsn("Non-serializable lambda");
    localMethodVisitor.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
    localMethodVisitor.visitInsn(191);
    localMethodVisitor.visitMaxs(-1, -1);
    localMethodVisitor.visitEnd();
    localMethodVisitor = cw.visitMethod(18, "readObject", "(Ljava/io/ObjectInputStream;)V", null, SER_HOSTILE_EXCEPTIONS);
    localMethodVisitor.visitCode();
    localMethodVisitor.visitTypeInsn(187, "java/io/NotSerializableException");
    localMethodVisitor.visitInsn(89);
    localMethodVisitor.visitLdcInsn("Non-serializable lambda");
    localMethodVisitor.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
    localMethodVisitor.visitInsn(191);
    localMethodVisitor.visitMaxs(-1, -1);
    localMethodVisitor.visitEnd();
  }
  
  static int getParameterSize(Class<?> paramClass)
  {
    if (paramClass == Void.TYPE) {
      return 0;
    }
    if ((paramClass == Long.TYPE) || (paramClass == Double.TYPE)) {
      return 2;
    }
    return 1;
  }
  
  static int getLoadOpcode(Class<?> paramClass)
  {
    if (paramClass == Void.TYPE) {
      throw new InternalError("Unexpected void type of load opcode");
    }
    return 21 + getOpcodeOffset(paramClass);
  }
  
  static int getReturnOpcode(Class<?> paramClass)
  {
    if (paramClass == Void.TYPE) {
      return 177;
    }
    return 172 + getOpcodeOffset(paramClass);
  }
  
  private static int getOpcodeOffset(Class<?> paramClass)
  {
    if (paramClass.isPrimitive())
    {
      if (paramClass == Long.TYPE) {
        return 1;
      }
      if (paramClass == Float.TYPE) {
        return 2;
      }
      if (paramClass == Double.TYPE) {
        return 3;
      }
      return 0;
    }
    return 4;
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.internal.lambda.dumpProxyClasses"), null, new Permission[] { new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read") });
    dumper = null == str ? null : ProxyClassesDumper.getInstance(str);
  }
  
  private class ForwardingMethodGenerator
    extends TypeConvertingMethodAdapter
  {
    ForwardingMethodGenerator(MethodVisitor paramMethodVisitor)
    {
      super();
    }
    
    void generate(MethodType paramMethodType)
    {
      visitCode();
      if (implKind == 8)
      {
        visitTypeInsn(187, implMethodClassName);
        visitInsn(89);
      }
      for (int i = 0; i < argNames.length; i++)
      {
        visitVarInsn(25, 0);
        visitFieldInsn(180, lambdaClassName, argNames[i], argDescs[i]);
      }
      convertArgumentTypes(paramMethodType);
      visitMethodInsn(invocationOpcode(), implMethodClassName, implMethodName, implMethodDesc, implDefiningClass.isInterface());
      Class localClass = paramMethodType.returnType();
      convertType(implMethodReturnClass, localClass, localClass);
      visitInsn(InnerClassLambdaMetafactory.getReturnOpcode(localClass));
      visitMaxs(-1, -1);
      visitEnd();
    }
    
    private void convertArgumentTypes(MethodType paramMethodType)
    {
      int i = 0;
      int j = (implIsInstanceMethod) && (invokedType.parameterCount() == 0) ? 1 : 0;
      int k = j != 0 ? 1 : 0;
      if (j != 0)
      {
        Class localClass1 = paramMethodType.parameterType(0);
        visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(localClass1), i + 1);
        i += InnerClassLambdaMetafactory.getParameterSize(localClass1);
        convertType(localClass1, implDefiningClass, instantiatedMethodType.parameterType(0));
      }
      int m = paramMethodType.parameterCount();
      int n = implMethodType.parameterCount() - m;
      for (int i1 = k; i1 < m; i1++)
      {
        Class localClass2 = paramMethodType.parameterType(i1);
        visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(localClass2), i + 1);
        i += InnerClassLambdaMetafactory.getParameterSize(localClass2);
        convertType(localClass2, implMethodType.parameterType(n + i1), instantiatedMethodType.parameterType(i1));
      }
    }
    
    private int invocationOpcode()
      throws InternalError
    {
      switch (implKind)
      {
      case 6: 
        return 184;
      case 8: 
        return 183;
      case 5: 
        return 182;
      case 9: 
        return 185;
      case 7: 
        return 183;
      }
      throw new InternalError("Unexpected invocation kind: " + implKind);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\InnerClassLambdaMetafactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */