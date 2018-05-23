package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import sun.security.action.GetBooleanAction;

public class ProxyGenerator
{
  private static final int CLASSFILE_MAJOR_VERSION = 49;
  private static final int CLASSFILE_MINOR_VERSION = 0;
  private static final int CONSTANT_UTF8 = 1;
  private static final int CONSTANT_UNICODE = 2;
  private static final int CONSTANT_INTEGER = 3;
  private static final int CONSTANT_FLOAT = 4;
  private static final int CONSTANT_LONG = 5;
  private static final int CONSTANT_DOUBLE = 6;
  private static final int CONSTANT_CLASS = 7;
  private static final int CONSTANT_STRING = 8;
  private static final int CONSTANT_FIELD = 9;
  private static final int CONSTANT_METHOD = 10;
  private static final int CONSTANT_INTERFACEMETHOD = 11;
  private static final int CONSTANT_NAMEANDTYPE = 12;
  private static final int ACC_PUBLIC = 1;
  private static final int ACC_PRIVATE = 2;
  private static final int ACC_STATIC = 8;
  private static final int ACC_FINAL = 16;
  private static final int ACC_SUPER = 32;
  private static final int opc_aconst_null = 1;
  private static final int opc_iconst_0 = 3;
  private static final int opc_bipush = 16;
  private static final int opc_sipush = 17;
  private static final int opc_ldc = 18;
  private static final int opc_ldc_w = 19;
  private static final int opc_iload = 21;
  private static final int opc_lload = 22;
  private static final int opc_fload = 23;
  private static final int opc_dload = 24;
  private static final int opc_aload = 25;
  private static final int opc_iload_0 = 26;
  private static final int opc_lload_0 = 30;
  private static final int opc_fload_0 = 34;
  private static final int opc_dload_0 = 38;
  private static final int opc_aload_0 = 42;
  private static final int opc_astore = 58;
  private static final int opc_astore_0 = 75;
  private static final int opc_aastore = 83;
  private static final int opc_pop = 87;
  private static final int opc_dup = 89;
  private static final int opc_ireturn = 172;
  private static final int opc_lreturn = 173;
  private static final int opc_freturn = 174;
  private static final int opc_dreturn = 175;
  private static final int opc_areturn = 176;
  private static final int opc_return = 177;
  private static final int opc_getstatic = 178;
  private static final int opc_putstatic = 179;
  private static final int opc_getfield = 180;
  private static final int opc_invokevirtual = 182;
  private static final int opc_invokespecial = 183;
  private static final int opc_invokestatic = 184;
  private static final int opc_invokeinterface = 185;
  private static final int opc_new = 187;
  private static final int opc_anewarray = 189;
  private static final int opc_athrow = 191;
  private static final int opc_checkcast = 192;
  private static final int opc_wide = 196;
  private static final String superclassName = "java/lang/reflect/Proxy";
  private static final String handlerFieldName = "h";
  private static final boolean saveGeneratedFiles;
  private static Method hashCodeMethod;
  private static Method equalsMethod;
  private static Method toStringMethod;
  private String className;
  private Class<?>[] interfaces;
  private int accessFlags;
  private ConstantPool cp = new ConstantPool(null);
  private List<FieldInfo> fields = new ArrayList();
  private List<MethodInfo> methods = new ArrayList();
  private Map<String, List<ProxyMethod>> proxyMethods = new HashMap();
  private int proxyMethodCount = 0;
  
  public static byte[] generateProxyClass(String paramString, Class<?>[] paramArrayOfClass)
  {
    return generateProxyClass(paramString, paramArrayOfClass, 49);
  }
  
  public static byte[] generateProxyClass(String paramString, Class<?>[] paramArrayOfClass, int paramInt)
  {
    ProxyGenerator localProxyGenerator = new ProxyGenerator(paramString, paramArrayOfClass, paramInt);
    final byte[] arrayOfByte = localProxyGenerator.generateClassFile();
    if (saveGeneratedFiles) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          try
          {
            int i = val$name.lastIndexOf('.');
            Path localPath1;
            if (i > 0)
            {
              Path localPath2 = Paths.get(val$name.substring(0, i).replace('.', File.separatorChar), new String[0]);
              Files.createDirectories(localPath2, new FileAttribute[0]);
              localPath1 = localPath2.resolve(val$name.substring(i + 1, val$name.length()) + ".class");
            }
            else
            {
              localPath1 = Paths.get(val$name + ".class", new String[0]);
            }
            Files.write(localPath1, arrayOfByte, new OpenOption[0]);
            return null;
          }
          catch (IOException localIOException)
          {
            throw new InternalError("I/O exception saving generated file: " + localIOException);
          }
        }
      });
    }
    return arrayOfByte;
  }
  
  private ProxyGenerator(String paramString, Class<?>[] paramArrayOfClass, int paramInt)
  {
    className = paramString;
    interfaces = paramArrayOfClass;
    accessFlags = paramInt;
  }
  
  private byte[] generateClassFile()
  {
    addProxyMethod(hashCodeMethod, Object.class);
    addProxyMethod(equalsMethod, Object.class);
    addProxyMethod(toStringMethod, Object.class);
    Object localObject4;
    for (localObject4 : interfaces) {
      for (Method localMethod : ((Class)localObject4).getMethods()) {
        addProxyMethod(localMethod, (Class)localObject4);
      }
    }
    ??? = proxyMethods.values().iterator();
    List localList;
    while (((Iterator)???).hasNext())
    {
      localList = (List)((Iterator)???).next();
      checkReturnTypes(localList);
    }
    try
    {
      methods.add(generateConstructor());
      ??? = proxyMethods.values().iterator();
      while (((Iterator)???).hasNext())
      {
        localList = (List)((Iterator)???).next();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          localObject4 = (ProxyMethod)localIterator.next();
          fields.add(new FieldInfo(methodFieldName, "Ljava/lang/reflect/Method;", 10));
          methods.add(((ProxyMethod)localObject4).generateMethod());
        }
      }
      methods.add(generateStaticInitializer());
    }
    catch (IOException localIOException1)
    {
      throw new InternalError("unexpected I/O Exception", localIOException1);
    }
    if (methods.size() > 65535) {
      throw new IllegalArgumentException("method limit exceeded");
    }
    if (fields.size() > 65535) {
      throw new IllegalArgumentException("field limit exceeded");
    }
    cp.getClass(dotToSlash(className));
    cp.getClass("java/lang/reflect/Proxy");
    for (localObject4 : interfaces) {
      cp.getClass(dotToSlash(((Class)localObject4).getName()));
    }
    cp.setReadOnly();
    ??? = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)???);
    try
    {
      localDataOutputStream.writeInt(-889275714);
      localDataOutputStream.writeShort(0);
      localDataOutputStream.writeShort(49);
      cp.write(localDataOutputStream);
      localDataOutputStream.writeShort(accessFlags);
      localDataOutputStream.writeShort(cp.getClass(dotToSlash(className)));
      localDataOutputStream.writeShort(cp.getClass("java/lang/reflect/Proxy"));
      localDataOutputStream.writeShort(interfaces.length);
      for (Object localObject6 : interfaces) {
        localDataOutputStream.writeShort(cp.getClass(dotToSlash(((Class)localObject6).getName())));
      }
      localDataOutputStream.writeShort(fields.size());
      ??? = fields.iterator();
      Object localObject5;
      while (((Iterator)???).hasNext())
      {
        localObject5 = (FieldInfo)((Iterator)???).next();
        ((FieldInfo)localObject5).write(localDataOutputStream);
      }
      localDataOutputStream.writeShort(methods.size());
      ??? = methods.iterator();
      while (((Iterator)???).hasNext())
      {
        localObject5 = (MethodInfo)((Iterator)???).next();
        ((MethodInfo)localObject5).write(localDataOutputStream);
      }
      localDataOutputStream.writeShort(0);
    }
    catch (IOException localIOException2)
    {
      throw new InternalError("unexpected I/O Exception", localIOException2);
    }
    return ((ByteArrayOutputStream)???).toByteArray();
  }
  
  private void addProxyMethod(Method paramMethod, Class<?> paramClass)
  {
    String str1 = paramMethod.getName();
    Class[] arrayOfClass1 = paramMethod.getParameterTypes();
    Class localClass = paramMethod.getReturnType();
    Class[] arrayOfClass2 = paramMethod.getExceptionTypes();
    String str2 = str1 + getParameterDescriptors(arrayOfClass1);
    Object localObject = (List)proxyMethods.get(str2);
    if (localObject != null)
    {
      Iterator localIterator = ((List)localObject).iterator();
      while (localIterator.hasNext())
      {
        ProxyMethod localProxyMethod = (ProxyMethod)localIterator.next();
        if (localClass == returnType)
        {
          ArrayList localArrayList = new ArrayList();
          collectCompatibleTypes(arrayOfClass2, exceptionTypes, localArrayList);
          collectCompatibleTypes(exceptionTypes, arrayOfClass2, localArrayList);
          exceptionTypes = new Class[localArrayList.size()];
          exceptionTypes = ((Class[])localArrayList.toArray(exceptionTypes));
          return;
        }
      }
    }
    else
    {
      localObject = new ArrayList(3);
      proxyMethods.put(str2, localObject);
    }
    ((List)localObject).add(new ProxyMethod(str1, arrayOfClass1, localClass, arrayOfClass2, paramClass, null));
  }
  
  private static void checkReturnTypes(List<ProxyMethod> paramList)
  {
    if (paramList.size() < 2) {
      return;
    }
    LinkedList localLinkedList = new LinkedList();
    Object localObject = paramList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ProxyMethod localProxyMethod = (ProxyMethod)((Iterator)localObject).next();
      Class localClass1 = returnType;
      if (localClass1.isPrimitive()) {
        throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(methodName, parameterTypes) + " but incompatible return types: " + localClass1.getName() + " and others");
      }
      int i = 0;
      ListIterator localListIterator = localLinkedList.listIterator();
      for (;;)
      {
        if (!localListIterator.hasNext()) {
          break label214;
        }
        Class localClass2 = (Class)localListIterator.next();
        if (localClass1.isAssignableFrom(localClass2))
        {
          if (($assertionsDisabled) || (i == 0)) {
            break;
          }
          throw new AssertionError();
        }
        if (localClass2.isAssignableFrom(localClass1)) {
          if (i == 0)
          {
            localListIterator.set(localClass1);
            i = 1;
          }
          else
          {
            localListIterator.remove();
          }
        }
      }
      label214:
      if (i == 0) {
        localLinkedList.add(localClass1);
      }
    }
    if (localLinkedList.size() > 1)
    {
      localObject = (ProxyMethod)paramList.get(0);
      throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(methodName, parameterTypes) + " but incompatible return types: " + localLinkedList);
    }
  }
  
  private MethodInfo generateConstructor()
    throws IOException
  {
    MethodInfo localMethodInfo = new MethodInfo("<init>", "(Ljava/lang/reflect/InvocationHandler;)V", 1);
    DataOutputStream localDataOutputStream = new DataOutputStream(code);
    code_aload(0, localDataOutputStream);
    code_aload(1, localDataOutputStream);
    localDataOutputStream.writeByte(183);
    localDataOutputStream.writeShort(cp.getMethodRef("java/lang/reflect/Proxy", "<init>", "(Ljava/lang/reflect/InvocationHandler;)V"));
    localDataOutputStream.writeByte(177);
    maxStack = 10;
    maxLocals = 2;
    declaredExceptions = new short[0];
    return localMethodInfo;
  }
  
  private MethodInfo generateStaticInitializer()
    throws IOException
  {
    MethodInfo localMethodInfo = new MethodInfo("<clinit>", "()V", 8);
    int i = 1;
    short s2 = 0;
    DataOutputStream localDataOutputStream = new DataOutputStream(code);
    Iterator localIterator1 = proxyMethods.values().iterator();
    while (localIterator1.hasNext())
    {
      List localList = (List)localIterator1.next();
      Iterator localIterator2 = localList.iterator();
      while (localIterator2.hasNext())
      {
        ProxyMethod localProxyMethod = (ProxyMethod)localIterator2.next();
        localProxyMethod.codeFieldInitialization(localDataOutputStream);
      }
    }
    localDataOutputStream.writeByte(177);
    short s3 = s1 = (short)code.size();
    exceptionTable.add(new ExceptionTableEntry(s2, s3, s1, cp.getClass("java/lang/NoSuchMethodException")));
    code_astore(i, localDataOutputStream);
    localDataOutputStream.writeByte(187);
    localDataOutputStream.writeShort(cp.getClass("java/lang/NoSuchMethodError"));
    localDataOutputStream.writeByte(89);
    code_aload(i, localDataOutputStream);
    localDataOutputStream.writeByte(182);
    localDataOutputStream.writeShort(cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
    localDataOutputStream.writeByte(183);
    localDataOutputStream.writeShort(cp.getMethodRef("java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V"));
    localDataOutputStream.writeByte(191);
    short s1 = (short)code.size();
    exceptionTable.add(new ExceptionTableEntry(s2, s3, s1, cp.getClass("java/lang/ClassNotFoundException")));
    code_astore(i, localDataOutputStream);
    localDataOutputStream.writeByte(187);
    localDataOutputStream.writeShort(cp.getClass("java/lang/NoClassDefFoundError"));
    localDataOutputStream.writeByte(89);
    code_aload(i, localDataOutputStream);
    localDataOutputStream.writeByte(182);
    localDataOutputStream.writeShort(cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
    localDataOutputStream.writeByte(183);
    localDataOutputStream.writeShort(cp.getMethodRef("java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V"));
    localDataOutputStream.writeByte(191);
    if (code.size() > 65535) {
      throw new IllegalArgumentException("code size limit exceeded");
    }
    maxStack = 10;
    maxLocals = ((short)(i + 1));
    declaredExceptions = new short[0];
    return localMethodInfo;
  }
  
  private void code_iload(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    codeLocalLoadStore(paramInt, 21, 26, paramDataOutputStream);
  }
  
  private void code_lload(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    codeLocalLoadStore(paramInt, 22, 30, paramDataOutputStream);
  }
  
  private void code_fload(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    codeLocalLoadStore(paramInt, 23, 34, paramDataOutputStream);
  }
  
  private void code_dload(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    codeLocalLoadStore(paramInt, 24, 38, paramDataOutputStream);
  }
  
  private void code_aload(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    codeLocalLoadStore(paramInt, 25, 42, paramDataOutputStream);
  }
  
  private void code_astore(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    codeLocalLoadStore(paramInt, 58, 75, paramDataOutputStream);
  }
  
  private void codeLocalLoadStore(int paramInt1, int paramInt2, int paramInt3, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    assert ((paramInt1 >= 0) && (paramInt1 <= 65535));
    if (paramInt1 <= 3)
    {
      paramDataOutputStream.writeByte(paramInt3 + paramInt1);
    }
    else if (paramInt1 <= 255)
    {
      paramDataOutputStream.writeByte(paramInt2);
      paramDataOutputStream.writeByte(paramInt1 & 0xFF);
    }
    else
    {
      paramDataOutputStream.writeByte(196);
      paramDataOutputStream.writeByte(paramInt2);
      paramDataOutputStream.writeShort(paramInt1 & 0xFFFF);
    }
  }
  
  private void code_ldc(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    assert ((paramInt >= 0) && (paramInt <= 65535));
    if (paramInt <= 255)
    {
      paramDataOutputStream.writeByte(18);
      paramDataOutputStream.writeByte(paramInt & 0xFF);
    }
    else
    {
      paramDataOutputStream.writeByte(19);
      paramDataOutputStream.writeShort(paramInt & 0xFFFF);
    }
  }
  
  private void code_ipush(int paramInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if ((paramInt >= -1) && (paramInt <= 5))
    {
      paramDataOutputStream.writeByte(3 + paramInt);
    }
    else if ((paramInt >= -128) && (paramInt <= 127))
    {
      paramDataOutputStream.writeByte(16);
      paramDataOutputStream.writeByte(paramInt & 0xFF);
    }
    else if ((paramInt >= 32768) && (paramInt <= 32767))
    {
      paramDataOutputStream.writeByte(17);
      paramDataOutputStream.writeShort(paramInt & 0xFFFF);
    }
    else
    {
      throw new AssertionError();
    }
  }
  
  private void codeClassForName(Class<?> paramClass, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    code_ldc(cp.getString(paramClass.getName()), paramDataOutputStream);
    paramDataOutputStream.writeByte(184);
    paramDataOutputStream.writeShort(cp.getMethodRef("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;"));
  }
  
  private static String dotToSlash(String paramString)
  {
    return paramString.replace('.', '/');
  }
  
  private static String getMethodDescriptor(Class<?>[] paramArrayOfClass, Class<?> paramClass)
  {
    return getParameterDescriptors(paramArrayOfClass) + (paramClass == Void.TYPE ? "V" : getFieldType(paramClass));
  }
  
  private static String getParameterDescriptors(Class<?>[] paramArrayOfClass)
  {
    StringBuilder localStringBuilder = new StringBuilder("(");
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      localStringBuilder.append(getFieldType(paramArrayOfClass[i]));
    }
    localStringBuilder.append(')');
    return localStringBuilder.toString();
  }
  
  private static String getFieldType(Class<?> paramClass)
  {
    if (paramClass.isPrimitive()) {
      return getbaseTypeString;
    }
    if (paramClass.isArray()) {
      return paramClass.getName().replace('.', '/');
    }
    return "L" + dotToSlash(paramClass.getName()) + ";";
  }
  
  private static String getFriendlyMethodSignature(String paramString, Class<?>[] paramArrayOfClass)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    localStringBuilder.append('(');
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      if (i > 0) {
        localStringBuilder.append(',');
      }
      Object localObject = paramArrayOfClass[i];
      for (int j = 0; ((Class)localObject).isArray(); j++) {
        localObject = ((Class)localObject).getComponentType();
      }
      localStringBuilder.append(((Class)localObject).getName());
      while (j-- > 0) {
        localStringBuilder.append("[]");
      }
    }
    localStringBuilder.append(')');
    return localStringBuilder.toString();
  }
  
  private static int getWordsPerType(Class<?> paramClass)
  {
    if ((paramClass == Long.TYPE) || (paramClass == Double.TYPE)) {
      return 2;
    }
    return 1;
  }
  
  private static void collectCompatibleTypes(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, List<Class<?>> paramList)
  {
    for (Class<?> localClass1 : paramArrayOfClass1) {
      if (!paramList.contains(localClass1)) {
        for (Class<?> localClass2 : paramArrayOfClass2) {
          if (localClass2.isAssignableFrom(localClass1))
          {
            paramList.add(localClass1);
            break;
          }
        }
      }
    }
  }
  
  private static List<Class<?>> computeUniqueCatchList(Class<?>[] paramArrayOfClass)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Error.class);
    localArrayList.add(RuntimeException.class);
    label155:
    for (Class<?> localClass : paramArrayOfClass)
    {
      if (localClass.isAssignableFrom(Throwable.class))
      {
        localArrayList.clear();
        break;
      }
      if (Throwable.class.isAssignableFrom(localClass))
      {
        int k = 0;
        while (k < localArrayList.size())
        {
          Class localClass1 = (Class)localArrayList.get(k);
          if (localClass1.isAssignableFrom(localClass)) {
            break label155;
          }
          if (localClass.isAssignableFrom(localClass1)) {
            localArrayList.remove(k);
          } else {
            k++;
          }
        }
        localArrayList.add(localClass);
      }
    }
    return localArrayList;
  }
  
  static
  {
    saveGeneratedFiles = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.misc.ProxyGenerator.saveGeneratedFiles"))).booleanValue();
    try
    {
      hashCodeMethod = Object.class.getMethod("hashCode", new Class[0]);
      equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
      toStringMethod = Object.class.getMethod("toString", new Class[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
  }
  
  private static class ConstantPool
  {
    private List<Entry> pool = new ArrayList(32);
    private Map<Object, Short> map = new HashMap(16);
    private boolean readOnly = false;
    
    private ConstantPool() {}
    
    public short getUtf8(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      return getValue(paramString);
    }
    
    public short getInteger(int paramInt)
    {
      return getValue(new Integer(paramInt));
    }
    
    public short getFloat(float paramFloat)
    {
      return getValue(new Float(paramFloat));
    }
    
    public short getClass(String paramString)
    {
      short s = getUtf8(paramString);
      return getIndirect(new IndirectEntry(7, s));
    }
    
    public short getString(String paramString)
    {
      short s = getUtf8(paramString);
      return getIndirect(new IndirectEntry(8, s));
    }
    
    public short getFieldRef(String paramString1, String paramString2, String paramString3)
    {
      short s1 = getClass(paramString1);
      short s2 = getNameAndType(paramString2, paramString3);
      return getIndirect(new IndirectEntry(9, s1, s2));
    }
    
    public short getMethodRef(String paramString1, String paramString2, String paramString3)
    {
      short s1 = getClass(paramString1);
      short s2 = getNameAndType(paramString2, paramString3);
      return getIndirect(new IndirectEntry(10, s1, s2));
    }
    
    public short getInterfaceMethodRef(String paramString1, String paramString2, String paramString3)
    {
      short s1 = getClass(paramString1);
      short s2 = getNameAndType(paramString2, paramString3);
      return getIndirect(new IndirectEntry(11, s1, s2));
    }
    
    public short getNameAndType(String paramString1, String paramString2)
    {
      short s1 = getUtf8(paramString1);
      short s2 = getUtf8(paramString2);
      return getIndirect(new IndirectEntry(12, s1, s2));
    }
    
    public void setReadOnly()
    {
      readOnly = true;
    }
    
    public void write(OutputStream paramOutputStream)
      throws IOException
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(paramOutputStream);
      localDataOutputStream.writeShort(pool.size() + 1);
      Iterator localIterator = pool.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        localEntry.write(localDataOutputStream);
      }
    }
    
    private short addEntry(Entry paramEntry)
    {
      pool.add(paramEntry);
      if (pool.size() >= 65535) {
        throw new IllegalArgumentException("constant pool size limit exceeded");
      }
      return (short)pool.size();
    }
    
    private short getValue(Object paramObject)
    {
      Short localShort = (Short)map.get(paramObject);
      if (localShort != null) {
        return localShort.shortValue();
      }
      if (readOnly) {
        throw new InternalError("late constant pool addition: " + paramObject);
      }
      short s = addEntry(new ValueEntry(paramObject));
      map.put(paramObject, new Short(s));
      return s;
    }
    
    private short getIndirect(IndirectEntry paramIndirectEntry)
    {
      Short localShort = (Short)map.get(paramIndirectEntry);
      if (localShort != null) {
        return localShort.shortValue();
      }
      if (readOnly) {
        throw new InternalError("late constant pool addition");
      }
      short s = addEntry(paramIndirectEntry);
      map.put(paramIndirectEntry, new Short(s));
      return s;
    }
    
    private static abstract class Entry
    {
      private Entry() {}
      
      public abstract void write(DataOutputStream paramDataOutputStream)
        throws IOException;
    }
    
    private static class IndirectEntry
      extends ProxyGenerator.ConstantPool.Entry
    {
      private int tag;
      private short index0;
      private short index1;
      
      public IndirectEntry(int paramInt, short paramShort)
      {
        super();
        tag = paramInt;
        index0 = paramShort;
        index1 = 0;
      }
      
      public IndirectEntry(int paramInt, short paramShort1, short paramShort2)
      {
        super();
        tag = paramInt;
        index0 = paramShort1;
        index1 = paramShort2;
      }
      
      public void write(DataOutputStream paramDataOutputStream)
        throws IOException
      {
        paramDataOutputStream.writeByte(tag);
        paramDataOutputStream.writeShort(index0);
        if ((tag == 9) || (tag == 10) || (tag == 11) || (tag == 12)) {
          paramDataOutputStream.writeShort(index1);
        }
      }
      
      public int hashCode()
      {
        return tag + index0 + index1;
      }
      
      public boolean equals(Object paramObject)
      {
        if ((paramObject instanceof IndirectEntry))
        {
          IndirectEntry localIndirectEntry = (IndirectEntry)paramObject;
          if ((tag == tag) && (index0 == index0) && (index1 == index1)) {
            return true;
          }
        }
        return false;
      }
    }
    
    private static class ValueEntry
      extends ProxyGenerator.ConstantPool.Entry
    {
      private Object value;
      
      public ValueEntry(Object paramObject)
      {
        super();
        value = paramObject;
      }
      
      public void write(DataOutputStream paramDataOutputStream)
        throws IOException
      {
        if ((value instanceof String))
        {
          paramDataOutputStream.writeByte(1);
          paramDataOutputStream.writeUTF((String)value);
        }
        else if ((value instanceof Integer))
        {
          paramDataOutputStream.writeByte(3);
          paramDataOutputStream.writeInt(((Integer)value).intValue());
        }
        else if ((value instanceof Float))
        {
          paramDataOutputStream.writeByte(4);
          paramDataOutputStream.writeFloat(((Float)value).floatValue());
        }
        else if ((value instanceof Long))
        {
          paramDataOutputStream.writeByte(5);
          paramDataOutputStream.writeLong(((Long)value).longValue());
        }
        else if ((value instanceof Double))
        {
          paramDataOutputStream.writeDouble(6.0D);
          paramDataOutputStream.writeDouble(((Double)value).doubleValue());
        }
        else
        {
          throw new InternalError("bogus value entry: " + value);
        }
      }
    }
  }
  
  private static class ExceptionTableEntry
  {
    public short startPc;
    public short endPc;
    public short handlerPc;
    public short catchType;
    
    public ExceptionTableEntry(short paramShort1, short paramShort2, short paramShort3, short paramShort4)
    {
      startPc = paramShort1;
      endPc = paramShort2;
      handlerPc = paramShort3;
      catchType = paramShort4;
    }
  }
  
  private class FieldInfo
  {
    public int accessFlags;
    public String name;
    public String descriptor;
    
    public FieldInfo(String paramString1, String paramString2, int paramInt)
    {
      name = paramString1;
      descriptor = paramString2;
      accessFlags = paramInt;
      cp.getUtf8(paramString1);
      cp.getUtf8(paramString2);
    }
    
    public void write(DataOutputStream paramDataOutputStream)
      throws IOException
    {
      paramDataOutputStream.writeShort(accessFlags);
      paramDataOutputStream.writeShort(cp.getUtf8(name));
      paramDataOutputStream.writeShort(cp.getUtf8(descriptor));
      paramDataOutputStream.writeShort(0);
    }
  }
  
  private class MethodInfo
  {
    public int accessFlags;
    public String name;
    public String descriptor;
    public short maxStack;
    public short maxLocals;
    public ByteArrayOutputStream code = new ByteArrayOutputStream();
    public List<ProxyGenerator.ExceptionTableEntry> exceptionTable = new ArrayList();
    public short[] declaredExceptions;
    
    public MethodInfo(String paramString1, String paramString2, int paramInt)
    {
      name = paramString1;
      descriptor = paramString2;
      accessFlags = paramInt;
      cp.getUtf8(paramString1);
      cp.getUtf8(paramString2);
      cp.getUtf8("Code");
      cp.getUtf8("Exceptions");
    }
    
    public void write(DataOutputStream paramDataOutputStream)
      throws IOException
    {
      paramDataOutputStream.writeShort(accessFlags);
      paramDataOutputStream.writeShort(cp.getUtf8(name));
      paramDataOutputStream.writeShort(cp.getUtf8(descriptor));
      paramDataOutputStream.writeShort(2);
      paramDataOutputStream.writeShort(cp.getUtf8("Code"));
      paramDataOutputStream.writeInt(12 + code.size() + 8 * exceptionTable.size());
      paramDataOutputStream.writeShort(maxStack);
      paramDataOutputStream.writeShort(maxLocals);
      paramDataOutputStream.writeInt(code.size());
      code.writeTo(paramDataOutputStream);
      paramDataOutputStream.writeShort(exceptionTable.size());
      Object localObject = exceptionTable.iterator();
      while (((Iterator)localObject).hasNext())
      {
        ProxyGenerator.ExceptionTableEntry localExceptionTableEntry = (ProxyGenerator.ExceptionTableEntry)((Iterator)localObject).next();
        paramDataOutputStream.writeShort(startPc);
        paramDataOutputStream.writeShort(endPc);
        paramDataOutputStream.writeShort(handlerPc);
        paramDataOutputStream.writeShort(catchType);
      }
      paramDataOutputStream.writeShort(0);
      paramDataOutputStream.writeShort(cp.getUtf8("Exceptions"));
      paramDataOutputStream.writeInt(2 + 2 * declaredExceptions.length);
      paramDataOutputStream.writeShort(declaredExceptions.length);
      for (int k : declaredExceptions) {
        paramDataOutputStream.writeShort(k);
      }
    }
  }
  
  private static class PrimitiveTypeInfo
  {
    public String baseTypeString;
    public String wrapperClassName;
    public String wrapperValueOfDesc;
    public String unwrapMethodName;
    public String unwrapMethodDesc;
    private static Map<Class<?>, PrimitiveTypeInfo> table;
    
    private static void add(Class<?> paramClass1, Class<?> paramClass2)
    {
      table.put(paramClass1, new PrimitiveTypeInfo(paramClass1, paramClass2));
    }
    
    private PrimitiveTypeInfo(Class<?> paramClass1, Class<?> paramClass2)
    {
      assert (paramClass1.isPrimitive());
      baseTypeString = Array.newInstance(paramClass1, 0).getClass().getName().substring(1);
      wrapperClassName = ProxyGenerator.dotToSlash(paramClass2.getName());
      wrapperValueOfDesc = ("(" + baseTypeString + ")L" + wrapperClassName + ";");
      unwrapMethodName = (paramClass1.getName() + "Value");
      unwrapMethodDesc = ("()" + baseTypeString);
    }
    
    public static PrimitiveTypeInfo get(Class<?> paramClass)
    {
      return (PrimitiveTypeInfo)table.get(paramClass);
    }
    
    static
    {
      table = new HashMap();
      add(Byte.TYPE, Byte.class);
      add(Character.TYPE, Character.class);
      add(Double.TYPE, Double.class);
      add(Float.TYPE, Float.class);
      add(Integer.TYPE, Integer.class);
      add(Long.TYPE, Long.class);
      add(Short.TYPE, Short.class);
      add(Boolean.TYPE, Boolean.class);
    }
  }
  
  private class ProxyMethod
  {
    public String methodName;
    public Class<?>[] parameterTypes;
    public Class<?> returnType;
    public Class<?>[] exceptionTypes;
    public Class<?> fromClass;
    public String methodFieldName;
    
    private ProxyMethod(Class<?>[] paramArrayOfClass1, Class<?> paramClass1, Class<?>[] paramArrayOfClass2, Class<?> paramClass2)
    {
      methodName = paramArrayOfClass1;
      parameterTypes = paramClass1;
      returnType = paramArrayOfClass2;
      exceptionTypes = paramClass2;
      Class localClass;
      fromClass = localClass;
      methodFieldName = ("m" + ProxyGenerator.access$408(ProxyGenerator.this));
    }
    
    private ProxyGenerator.MethodInfo generateMethod()
      throws IOException
    {
      String str = ProxyGenerator.getMethodDescriptor(parameterTypes, returnType);
      ProxyGenerator.MethodInfo localMethodInfo = new ProxyGenerator.MethodInfo(ProxyGenerator.this, methodName, str, 17);
      int[] arrayOfInt = new int[parameterTypes.length];
      int i = 1;
      for (int j = 0; j < arrayOfInt.length; j++)
      {
        arrayOfInt[j] = i;
        i += ProxyGenerator.getWordsPerType(parameterTypes[j]);
      }
      j = i;
      short s2 = 0;
      DataOutputStream localDataOutputStream = new DataOutputStream(code);
      ProxyGenerator.this.code_aload(0, localDataOutputStream);
      localDataOutputStream.writeByte(180);
      localDataOutputStream.writeShort(cp.getFieldRef("java/lang/reflect/Proxy", "h", "Ljava/lang/reflect/InvocationHandler;"));
      ProxyGenerator.this.code_aload(0, localDataOutputStream);
      localDataOutputStream.writeByte(178);
      localDataOutputStream.writeShort(cp.getFieldRef(ProxyGenerator.dotToSlash(className), methodFieldName, "Ljava/lang/reflect/Method;"));
      if (parameterTypes.length > 0)
      {
        ProxyGenerator.this.code_ipush(parameterTypes.length, localDataOutputStream);
        localDataOutputStream.writeByte(189);
        localDataOutputStream.writeShort(cp.getClass("java/lang/Object"));
        for (int k = 0; k < parameterTypes.length; k++)
        {
          localDataOutputStream.writeByte(89);
          ProxyGenerator.this.code_ipush(k, localDataOutputStream);
          codeWrapArgument(parameterTypes[k], arrayOfInt[k], localDataOutputStream);
          localDataOutputStream.writeByte(83);
        }
      }
      else
      {
        localDataOutputStream.writeByte(1);
      }
      localDataOutputStream.writeByte(185);
      localDataOutputStream.writeShort(cp.getInterfaceMethodRef("java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
      localDataOutputStream.writeByte(4);
      localDataOutputStream.writeByte(0);
      if (returnType == Void.TYPE)
      {
        localDataOutputStream.writeByte(87);
        localDataOutputStream.writeByte(177);
      }
      else
      {
        codeUnwrapReturnValue(returnType, localDataOutputStream);
      }
      short s1;
      short s3 = s1 = (short)code.size();
      List localList = ProxyGenerator.computeUniqueCatchList(exceptionTypes);
      if (localList.size() > 0)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Class localClass = (Class)localIterator.next();
          exceptionTable.add(new ProxyGenerator.ExceptionTableEntry(s2, s3, s1, cp.getClass(ProxyGenerator.dotToSlash(localClass.getName()))));
        }
        localDataOutputStream.writeByte(191);
        s1 = (short)code.size();
        exceptionTable.add(new ProxyGenerator.ExceptionTableEntry(s2, s3, s1, cp.getClass("java/lang/Throwable")));
        ProxyGenerator.this.code_astore(j, localDataOutputStream);
        localDataOutputStream.writeByte(187);
        localDataOutputStream.writeShort(cp.getClass("java/lang/reflect/UndeclaredThrowableException"));
        localDataOutputStream.writeByte(89);
        ProxyGenerator.this.code_aload(j, localDataOutputStream);
        localDataOutputStream.writeByte(183);
        localDataOutputStream.writeShort(cp.getMethodRef("java/lang/reflect/UndeclaredThrowableException", "<init>", "(Ljava/lang/Throwable;)V"));
        localDataOutputStream.writeByte(191);
      }
      if (code.size() > 65535) {
        throw new IllegalArgumentException("code size limit exceeded");
      }
      maxStack = 10;
      maxLocals = ((short)(j + 1));
      declaredExceptions = new short[exceptionTypes.length];
      for (int m = 0; m < exceptionTypes.length; m++) {
        declaredExceptions[m] = cp.getClass(ProxyGenerator.dotToSlash(exceptionTypes[m].getName()));
      }
      return localMethodInfo;
    }
    
    private void codeWrapArgument(Class<?> paramClass, int paramInt, DataOutputStream paramDataOutputStream)
      throws IOException
    {
      if (paramClass.isPrimitive())
      {
        ProxyGenerator.PrimitiveTypeInfo localPrimitiveTypeInfo = ProxyGenerator.PrimitiveTypeInfo.get(paramClass);
        if ((paramClass == Integer.TYPE) || (paramClass == Boolean.TYPE) || (paramClass == Byte.TYPE) || (paramClass == Character.TYPE) || (paramClass == Short.TYPE)) {
          ProxyGenerator.this.code_iload(paramInt, paramDataOutputStream);
        } else if (paramClass == Long.TYPE) {
          ProxyGenerator.this.code_lload(paramInt, paramDataOutputStream);
        } else if (paramClass == Float.TYPE) {
          ProxyGenerator.this.code_fload(paramInt, paramDataOutputStream);
        } else if (paramClass == Double.TYPE) {
          ProxyGenerator.this.code_dload(paramInt, paramDataOutputStream);
        } else {
          throw new AssertionError();
        }
        paramDataOutputStream.writeByte(184);
        paramDataOutputStream.writeShort(cp.getMethodRef(wrapperClassName, "valueOf", wrapperValueOfDesc));
      }
      else
      {
        ProxyGenerator.this.code_aload(paramInt, paramDataOutputStream);
      }
    }
    
    private void codeUnwrapReturnValue(Class<?> paramClass, DataOutputStream paramDataOutputStream)
      throws IOException
    {
      if (paramClass.isPrimitive())
      {
        ProxyGenerator.PrimitiveTypeInfo localPrimitiveTypeInfo = ProxyGenerator.PrimitiveTypeInfo.get(paramClass);
        paramDataOutputStream.writeByte(192);
        paramDataOutputStream.writeShort(cp.getClass(wrapperClassName));
        paramDataOutputStream.writeByte(182);
        paramDataOutputStream.writeShort(cp.getMethodRef(wrapperClassName, unwrapMethodName, unwrapMethodDesc));
        if ((paramClass == Integer.TYPE) || (paramClass == Boolean.TYPE) || (paramClass == Byte.TYPE) || (paramClass == Character.TYPE) || (paramClass == Short.TYPE)) {
          paramDataOutputStream.writeByte(172);
        } else if (paramClass == Long.TYPE) {
          paramDataOutputStream.writeByte(173);
        } else if (paramClass == Float.TYPE) {
          paramDataOutputStream.writeByte(174);
        } else if (paramClass == Double.TYPE) {
          paramDataOutputStream.writeByte(175);
        } else {
          throw new AssertionError();
        }
      }
      else
      {
        paramDataOutputStream.writeByte(192);
        paramDataOutputStream.writeShort(cp.getClass(ProxyGenerator.dotToSlash(paramClass.getName())));
        paramDataOutputStream.writeByte(176);
      }
    }
    
    private void codeFieldInitialization(DataOutputStream paramDataOutputStream)
      throws IOException
    {
      ProxyGenerator.this.codeClassForName(fromClass, paramDataOutputStream);
      ProxyGenerator.this.code_ldc(cp.getString(methodName), paramDataOutputStream);
      ProxyGenerator.this.code_ipush(parameterTypes.length, paramDataOutputStream);
      paramDataOutputStream.writeByte(189);
      paramDataOutputStream.writeShort(cp.getClass("java/lang/Class"));
      for (int i = 0; i < parameterTypes.length; i++)
      {
        paramDataOutputStream.writeByte(89);
        ProxyGenerator.this.code_ipush(i, paramDataOutputStream);
        if (parameterTypes[i].isPrimitive())
        {
          ProxyGenerator.PrimitiveTypeInfo localPrimitiveTypeInfo = ProxyGenerator.PrimitiveTypeInfo.get(parameterTypes[i]);
          paramDataOutputStream.writeByte(178);
          paramDataOutputStream.writeShort(cp.getFieldRef(wrapperClassName, "TYPE", "Ljava/lang/Class;"));
        }
        else
        {
          ProxyGenerator.this.codeClassForName(parameterTypes[i], paramDataOutputStream);
        }
        paramDataOutputStream.writeByte(83);
      }
      paramDataOutputStream.writeByte(182);
      paramDataOutputStream.writeShort(cp.getMethodRef("java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"));
      paramDataOutputStream.writeByte(179);
      paramDataOutputStream.writeShort(cp.getFieldRef(ProxyGenerator.dotToSlash(className), methodFieldName, "Ljava/lang/reflect/Method;"));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\ProxyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */