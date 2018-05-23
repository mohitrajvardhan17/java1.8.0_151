package jdk.internal.org.objectweb.asm;

public class ClassWriter
  extends ClassVisitor
{
  public static final int COMPUTE_MAXS = 1;
  public static final int COMPUTE_FRAMES = 2;
  static final int ACC_SYNTHETIC_ATTRIBUTE = 262144;
  static final int TO_ACC_SYNTHETIC = 64;
  static final int NOARG_INSN = 0;
  static final int SBYTE_INSN = 1;
  static final int SHORT_INSN = 2;
  static final int VAR_INSN = 3;
  static final int IMPLVAR_INSN = 4;
  static final int TYPE_INSN = 5;
  static final int FIELDORMETH_INSN = 6;
  static final int ITFMETH_INSN = 7;
  static final int INDYMETH_INSN = 8;
  static final int LABEL_INSN = 9;
  static final int LABELW_INSN = 10;
  static final int LDC_INSN = 11;
  static final int LDCW_INSN = 12;
  static final int IINC_INSN = 13;
  static final int TABL_INSN = 14;
  static final int LOOK_INSN = 15;
  static final int MANA_INSN = 16;
  static final int WIDE_INSN = 17;
  static final byte[] TYPE;
  static final int CLASS = 7;
  static final int FIELD = 9;
  static final int METH = 10;
  static final int IMETH = 11;
  static final int STR = 8;
  static final int INT = 3;
  static final int FLOAT = 4;
  static final int LONG = 5;
  static final int DOUBLE = 6;
  static final int NAME_TYPE = 12;
  static final int UTF8 = 1;
  static final int MTYPE = 16;
  static final int HANDLE = 15;
  static final int INDY = 18;
  static final int HANDLE_BASE = 20;
  static final int TYPE_NORMAL = 30;
  static final int TYPE_UNINIT = 31;
  static final int TYPE_MERGED = 32;
  static final int BSM = 33;
  ClassReader cr;
  int version;
  int index = 1;
  final ByteVector pool = new ByteVector();
  Item[] items = new Item['Ā'];
  int threshold = (int)(0.75D * items.length);
  final Item key = new Item();
  final Item key2 = new Item();
  final Item key3 = new Item();
  final Item key4 = new Item();
  Item[] typeTable;
  private short typeCount;
  private int access;
  private int name;
  String thisName;
  private int signature;
  private int superName;
  private int interfaceCount;
  private int[] interfaces;
  private int sourceFile;
  private ByteVector sourceDebug;
  private int enclosingMethodOwner;
  private int enclosingMethod;
  private AnnotationWriter anns;
  private AnnotationWriter ianns;
  private AnnotationWriter tanns;
  private AnnotationWriter itanns;
  private Attribute attrs;
  private int innerClassesCount;
  private ByteVector innerClasses;
  int bootstrapMethodsCount;
  ByteVector bootstrapMethods;
  FieldWriter firstField;
  FieldWriter lastField;
  MethodWriter firstMethod;
  MethodWriter lastMethod;
  private boolean computeMaxs;
  private boolean computeFrames;
  boolean invalidFrames;
  
  public ClassWriter(int paramInt)
  {
    super(327680);
    computeMaxs = ((paramInt & 0x1) != 0);
    computeFrames = ((paramInt & 0x2) != 0);
  }
  
  public ClassWriter(ClassReader paramClassReader, int paramInt)
  {
    this(paramInt);
    paramClassReader.copyPool(this);
    cr = paramClassReader;
  }
  
  public final void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    version = paramInt1;
    access = paramInt2;
    name = newClass(paramString1);
    thisName = paramString1;
    if (paramString2 != null) {
      signature = newUTF8(paramString2);
    }
    superName = (paramString3 == null ? 0 : newClass(paramString3));
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      interfaceCount = paramArrayOfString.length;
      interfaces = new int[interfaceCount];
      for (int i = 0; i < interfaceCount; i++) {
        interfaces[i] = newClass(paramArrayOfString[i]);
      }
    }
  }
  
  public final void visitSource(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      sourceFile = newUTF8(paramString1);
    }
    if (paramString2 != null) {
      sourceDebug = new ByteVector().encodeUTF8(paramString2, 0, Integer.MAX_VALUE);
    }
  }
  
  public final void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    enclosingMethodOwner = newClass(paramString1);
    if ((paramString2 != null) && (paramString3 != null)) {
      enclosingMethod = newNameType(paramString2, paramString3);
    }
  }
  
  public final AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      next = anns;
      anns = localAnnotationWriter;
    }
    else
    {
      next = ianns;
      ianns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public final AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this, true, localByteVector, localByteVector, length - 2);
    if (paramBoolean)
    {
      next = tanns;
      tanns = localAnnotationWriter;
    }
    else
    {
      next = itanns;
      itanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public final void visitAttribute(Attribute paramAttribute)
  {
    next = attrs;
    attrs = paramAttribute;
  }
  
  public final void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (innerClasses == null) {
      innerClasses = new ByteVector();
    }
    Item localItem = newClassItem(paramString1);
    if (intVal == 0)
    {
      innerClassesCount += 1;
      innerClasses.putShort(index);
      innerClasses.putShort(paramString2 == null ? 0 : newClass(paramString2));
      innerClasses.putShort(paramString3 == null ? 0 : newUTF8(paramString3));
      innerClasses.putShort(paramInt);
      intVal = innerClassesCount;
    }
  }
  
  public final FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    return new FieldWriter(this, paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public final MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    return new MethodWriter(this, paramInt, paramString1, paramString2, paramString3, paramArrayOfString, computeMaxs, computeFrames);
  }
  
  public final void visitEnd() {}
  
  public byte[] toByteArray()
  {
    if (index > 65535) {
      throw new RuntimeException("Class file too large!");
    }
    int i = 24 + 2 * interfaceCount;
    int j = 0;
    for (FieldWriter localFieldWriter = firstField; localFieldWriter != null; localFieldWriter = (FieldWriter)fv)
    {
      j++;
      i += localFieldWriter.getSize();
    }
    int k = 0;
    for (MethodWriter localMethodWriter = firstMethod; localMethodWriter != null; localMethodWriter = (MethodWriter)mv)
    {
      k++;
      i += localMethodWriter.getSize();
    }
    int m = 0;
    if (bootstrapMethods != null)
    {
      m++;
      i += 8 + bootstrapMethods.length;
      newUTF8("BootstrapMethods");
    }
    if (signature != 0)
    {
      m++;
      i += 8;
      newUTF8("Signature");
    }
    if (sourceFile != 0)
    {
      m++;
      i += 8;
      newUTF8("SourceFile");
    }
    if (sourceDebug != null)
    {
      m++;
      i += sourceDebug.length + 6;
      newUTF8("SourceDebugExtension");
    }
    if (enclosingMethodOwner != 0)
    {
      m++;
      i += 10;
      newUTF8("EnclosingMethod");
    }
    if ((access & 0x20000) != 0)
    {
      m++;
      i += 6;
      newUTF8("Deprecated");
    }
    if (((access & 0x1000) != 0) && (((version & 0xFFFF) < 49) || ((access & 0x40000) != 0)))
    {
      m++;
      i += 6;
      newUTF8("Synthetic");
    }
    if (innerClasses != null)
    {
      m++;
      i += 8 + innerClasses.length;
      newUTF8("InnerClasses");
    }
    if (anns != null)
    {
      m++;
      i += 8 + anns.getSize();
      newUTF8("RuntimeVisibleAnnotations");
    }
    if (ianns != null)
    {
      m++;
      i += 8 + ianns.getSize();
      newUTF8("RuntimeInvisibleAnnotations");
    }
    if (tanns != null)
    {
      m++;
      i += 8 + tanns.getSize();
      newUTF8("RuntimeVisibleTypeAnnotations");
    }
    if (itanns != null)
    {
      m++;
      i += 8 + itanns.getSize();
      newUTF8("RuntimeInvisibleTypeAnnotations");
    }
    if (attrs != null)
    {
      m += attrs.getCount();
      i += attrs.getSize(this, null, 0, -1, -1);
    }
    i += pool.length;
    ByteVector localByteVector = new ByteVector(i);
    localByteVector.putInt(-889275714).putInt(version);
    localByteVector.putShort(index).putByteArray(pool.data, 0, pool.length);
    int n = 0x60000 | (access & 0x40000) / 64;
    localByteVector.putShort(access & (n ^ 0xFFFFFFFF)).putShort(name).putShort(superName);
    localByteVector.putShort(interfaceCount);
    for (int i1 = 0; i1 < interfaceCount; i1++) {
      localByteVector.putShort(interfaces[i1]);
    }
    localByteVector.putShort(j);
    for (localFieldWriter = firstField; localFieldWriter != null; localFieldWriter = (FieldWriter)fv) {
      localFieldWriter.put(localByteVector);
    }
    localByteVector.putShort(k);
    for (localMethodWriter = firstMethod; localMethodWriter != null; localMethodWriter = (MethodWriter)mv) {
      localMethodWriter.put(localByteVector);
    }
    localByteVector.putShort(m);
    if (bootstrapMethods != null)
    {
      localByteVector.putShort(newUTF8("BootstrapMethods"));
      localByteVector.putInt(bootstrapMethods.length + 2).putShort(bootstrapMethodsCount);
      localByteVector.putByteArray(bootstrapMethods.data, 0, bootstrapMethods.length);
    }
    if (signature != 0) {
      localByteVector.putShort(newUTF8("Signature")).putInt(2).putShort(signature);
    }
    if (sourceFile != 0) {
      localByteVector.putShort(newUTF8("SourceFile")).putInt(2).putShort(sourceFile);
    }
    if (sourceDebug != null)
    {
      i1 = sourceDebug.length;
      localByteVector.putShort(newUTF8("SourceDebugExtension")).putInt(i1);
      localByteVector.putByteArray(sourceDebug.data, 0, i1);
    }
    if (enclosingMethodOwner != 0)
    {
      localByteVector.putShort(newUTF8("EnclosingMethod")).putInt(4);
      localByteVector.putShort(enclosingMethodOwner).putShort(enclosingMethod);
    }
    if ((access & 0x20000) != 0) {
      localByteVector.putShort(newUTF8("Deprecated")).putInt(0);
    }
    if (((access & 0x1000) != 0) && (((version & 0xFFFF) < 49) || ((access & 0x40000) != 0))) {
      localByteVector.putShort(newUTF8("Synthetic")).putInt(0);
    }
    if (innerClasses != null)
    {
      localByteVector.putShort(newUTF8("InnerClasses"));
      localByteVector.putInt(innerClasses.length + 2).putShort(innerClassesCount);
      localByteVector.putByteArray(innerClasses.data, 0, innerClasses.length);
    }
    if (anns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeVisibleAnnotations"));
      anns.put(localByteVector);
    }
    if (ianns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeInvisibleAnnotations"));
      ianns.put(localByteVector);
    }
    if (tanns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeVisibleTypeAnnotations"));
      tanns.put(localByteVector);
    }
    if (itanns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"));
      itanns.put(localByteVector);
    }
    if (attrs != null) {
      attrs.put(this, null, 0, -1, -1, localByteVector);
    }
    if (invalidFrames)
    {
      anns = null;
      ianns = null;
      attrs = null;
      innerClassesCount = 0;
      innerClasses = null;
      bootstrapMethodsCount = 0;
      bootstrapMethods = null;
      firstField = null;
      lastField = null;
      firstMethod = null;
      lastMethod = null;
      computeMaxs = false;
      computeFrames = true;
      invalidFrames = false;
      new ClassReader(data).accept(this, 4);
      return toByteArray();
    }
    return data;
  }
  
  Item newConstItem(Object paramObject)
  {
    int i;
    if ((paramObject instanceof Integer))
    {
      i = ((Integer)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Byte))
    {
      i = ((Byte)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Character))
    {
      i = ((Character)paramObject).charValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Short))
    {
      i = ((Short)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Boolean))
    {
      i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      return newInteger(i);
    }
    if ((paramObject instanceof Float))
    {
      float f = ((Float)paramObject).floatValue();
      return newFloat(f);
    }
    if ((paramObject instanceof Long))
    {
      long l = ((Long)paramObject).longValue();
      return newLong(l);
    }
    if ((paramObject instanceof Double))
    {
      double d = ((Double)paramObject).doubleValue();
      return newDouble(d);
    }
    if ((paramObject instanceof String)) {
      return newString((String)paramObject);
    }
    Object localObject;
    if ((paramObject instanceof Type))
    {
      localObject = (Type)paramObject;
      int j = ((Type)localObject).getSort();
      if (j == 10) {
        return newClassItem(((Type)localObject).getInternalName());
      }
      if (j == 11) {
        return newMethodTypeItem(((Type)localObject).getDescriptor());
      }
      return newClassItem(((Type)localObject).getDescriptor());
    }
    if ((paramObject instanceof Handle))
    {
      localObject = (Handle)paramObject;
      return newHandleItem(tag, owner, name, desc);
    }
    throw new IllegalArgumentException("value " + paramObject);
  }
  
  public int newConst(Object paramObject)
  {
    return newConstItemindex;
  }
  
  public int newUTF8(String paramString)
  {
    key.set(1, paramString, null, null);
    Item localItem = get(key);
    if (localItem == null)
    {
      pool.putByte(1).putUTF8(paramString);
      localItem = new Item(index++, key);
      put(localItem);
    }
    return index;
  }
  
  Item newClassItem(String paramString)
  {
    key2.set(7, paramString, null, null);
    Item localItem = get(key2);
    if (localItem == null)
    {
      pool.put12(7, newUTF8(paramString));
      localItem = new Item(index++, key2);
      put(localItem);
    }
    return localItem;
  }
  
  public int newClass(String paramString)
  {
    return newClassItemindex;
  }
  
  Item newMethodTypeItem(String paramString)
  {
    key2.set(16, paramString, null, null);
    Item localItem = get(key2);
    if (localItem == null)
    {
      pool.put12(16, newUTF8(paramString));
      localItem = new Item(index++, key2);
      put(localItem);
    }
    return localItem;
  }
  
  public int newMethodType(String paramString)
  {
    return newMethodTypeItemindex;
  }
  
  Item newHandleItem(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    key4.set(20 + paramInt, paramString1, paramString2, paramString3);
    Item localItem = get(key4);
    if (localItem == null)
    {
      if (paramInt <= 4) {
        put112(15, paramInt, newField(paramString1, paramString2, paramString3));
      } else {
        put112(15, paramInt, newMethod(paramString1, paramString2, paramString3, paramInt == 9));
      }
      localItem = new Item(index++, key4);
      put(localItem);
    }
    return localItem;
  }
  
  public int newHandle(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    return newHandleItemindex;
  }
  
  Item newInvokeDynamicItem(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    ByteVector localByteVector = bootstrapMethods;
    if (localByteVector == null) {
      localByteVector = bootstrapMethods = new ByteVector();
    }
    int i = length;
    int j = paramHandle.hashCode();
    localByteVector.putShort(newHandle(tag, owner, name, desc));
    int k = paramVarArgs.length;
    localByteVector.putShort(k);
    for (int m = 0; m < k; m++)
    {
      Object localObject = paramVarArgs[m];
      j ^= localObject.hashCode();
      localByteVector.putShort(newConst(localObject));
    }
    byte[] arrayOfByte = data;
    int n = 2 + k << 1;
    j &= 0x7FFFFFFF;
    Item localItem = items[(j % items.length)];
    int i1;
    label246:
    while (localItem != null) {
      if ((type != 33) || (hashCode != j))
      {
        localItem = next;
      }
      else
      {
        i1 = intVal;
        for (int i2 = 0;; i2++)
        {
          if (i2 >= n) {
            break label246;
          }
          if (arrayOfByte[(i + i2)] != arrayOfByte[(i1 + i2)])
          {
            localItem = next;
            break;
          }
        }
      }
    }
    if (localItem != null)
    {
      i1 = index;
      length = i;
    }
    else
    {
      i1 = bootstrapMethodsCount++;
      localItem = new Item(i1);
      localItem.set(i, j);
      put(localItem);
    }
    key3.set(paramString1, paramString2, i1);
    localItem = get(key3);
    if (localItem == null)
    {
      put122(18, i1, newNameType(paramString1, paramString2));
      localItem = new Item(index++, key3);
      put(localItem);
    }
    return localItem;
  }
  
  public int newInvokeDynamic(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    return newInvokeDynamicItemindex;
  }
  
  Item newFieldItem(String paramString1, String paramString2, String paramString3)
  {
    key3.set(9, paramString1, paramString2, paramString3);
    Item localItem = get(key3);
    if (localItem == null)
    {
      put122(9, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new Item(index++, key3);
      put(localItem);
    }
    return localItem;
  }
  
  public int newField(String paramString1, String paramString2, String paramString3)
  {
    return newFieldItemindex;
  }
  
  Item newMethodItem(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    int i = paramBoolean ? 11 : 10;
    key3.set(i, paramString1, paramString2, paramString3);
    Item localItem = get(key3);
    if (localItem == null)
    {
      put122(i, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new Item(index++, key3);
      put(localItem);
    }
    return localItem;
  }
  
  public int newMethod(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    return newMethodItemindex;
  }
  
  Item newInteger(int paramInt)
  {
    key.set(paramInt);
    Item localItem = get(key);
    if (localItem == null)
    {
      pool.putByte(3).putInt(paramInt);
      localItem = new Item(index++, key);
      put(localItem);
    }
    return localItem;
  }
  
  Item newFloat(float paramFloat)
  {
    key.set(paramFloat);
    Item localItem = get(key);
    if (localItem == null)
    {
      pool.putByte(4).putInt(key.intVal);
      localItem = new Item(index++, key);
      put(localItem);
    }
    return localItem;
  }
  
  Item newLong(long paramLong)
  {
    key.set(paramLong);
    Item localItem = get(key);
    if (localItem == null)
    {
      pool.putByte(5).putLong(paramLong);
      localItem = new Item(index, key);
      index += 2;
      put(localItem);
    }
    return localItem;
  }
  
  Item newDouble(double paramDouble)
  {
    key.set(paramDouble);
    Item localItem = get(key);
    if (localItem == null)
    {
      pool.putByte(6).putLong(key.longVal);
      localItem = new Item(index, key);
      index += 2;
      put(localItem);
    }
    return localItem;
  }
  
  private Item newString(String paramString)
  {
    key2.set(8, paramString, null, null);
    Item localItem = get(key2);
    if (localItem == null)
    {
      pool.put12(8, newUTF8(paramString));
      localItem = new Item(index++, key2);
      put(localItem);
    }
    return localItem;
  }
  
  public int newNameType(String paramString1, String paramString2)
  {
    return newNameTypeItemindex;
  }
  
  Item newNameTypeItem(String paramString1, String paramString2)
  {
    key2.set(12, paramString1, paramString2, null);
    Item localItem = get(key2);
    if (localItem == null)
    {
      put122(12, newUTF8(paramString1), newUTF8(paramString2));
      localItem = new Item(index++, key2);
      put(localItem);
    }
    return localItem;
  }
  
  int addType(String paramString)
  {
    key.set(30, paramString, null, null);
    Item localItem = get(key);
    if (localItem == null) {
      localItem = addType(key);
    }
    return index;
  }
  
  int addUninitializedType(String paramString, int paramInt)
  {
    key.type = 31;
    key.intVal = paramInt;
    key.strVal1 = paramString;
    key.hashCode = (0x7FFFFFFF & 31 + paramString.hashCode() + paramInt);
    Item localItem = get(key);
    if (localItem == null) {
      localItem = addType(key);
    }
    return index;
  }
  
  private Item addType(Item paramItem)
  {
    typeCount = ((short)(typeCount + 1));
    Item localItem = new Item(typeCount, key);
    put(localItem);
    if (typeTable == null) {
      typeTable = new Item[16];
    }
    if (typeCount == typeTable.length)
    {
      Item[] arrayOfItem = new Item[2 * typeTable.length];
      System.arraycopy(typeTable, 0, arrayOfItem, 0, typeTable.length);
      typeTable = arrayOfItem;
    }
    typeTable[typeCount] = localItem;
    return localItem;
  }
  
  int getMergedType(int paramInt1, int paramInt2)
  {
    key2.type = 32;
    key2.longVal = (paramInt1 | paramInt2 << 32);
    key2.hashCode = (0x7FFFFFFF & 32 + paramInt1 + paramInt2);
    Item localItem = get(key2);
    if (localItem == null)
    {
      String str1 = typeTable[paramInt1].strVal1;
      String str2 = typeTable[paramInt2].strVal1;
      key2.intVal = addType(getCommonSuperClass(str1, str2));
      localItem = new Item(0, key2);
      put(localItem);
    }
    return intVal;
  }
  
  protected String getCommonSuperClass(String paramString1, String paramString2)
  {
    ClassLoader localClassLoader = getClass().getClassLoader();
    Class localClass1;
    Class localClass2;
    try
    {
      localClass1 = Class.forName(paramString1.replace('/', '.'), false, localClassLoader);
      localClass2 = Class.forName(paramString2.replace('/', '.'), false, localClassLoader);
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
    if (localClass1.isAssignableFrom(localClass2)) {
      return paramString1;
    }
    if (localClass2.isAssignableFrom(localClass1)) {
      return paramString2;
    }
    if ((localClass1.isInterface()) || (localClass2.isInterface())) {
      return "java/lang/Object";
    }
    do
    {
      localClass1 = localClass1.getSuperclass();
    } while (!localClass1.isAssignableFrom(localClass2));
    return localClass1.getName().replace('.', '/');
  }
  
  private Item get(Item paramItem)
  {
    for (Item localItem = items[(hashCode % items.length)]; (localItem != null) && ((type != type) || (!paramItem.isEqualTo(localItem))); localItem = next) {}
    return localItem;
  }
  
  private void put(Item paramItem)
  {
    if (index + typeCount > threshold)
    {
      i = items.length;
      int j = i * 2 + 1;
      Item[] arrayOfItem = new Item[j];
      for (int k = i - 1; k >= 0; k--)
      {
        Item localItem;
        for (Object localObject = items[k]; localObject != null; localObject = localItem)
        {
          int m = hashCode % arrayOfItem.length;
          localItem = next;
          next = arrayOfItem[m];
          arrayOfItem[m] = localObject;
        }
      }
      items = arrayOfItem;
      threshold = ((int)(j * 0.75D));
    }
    int i = hashCode % items.length;
    next = items[i];
    items[i] = paramItem;
  }
  
  private void put122(int paramInt1, int paramInt2, int paramInt3)
  {
    pool.put12(paramInt1, paramInt2).putShort(paramInt3);
  }
  
  private void put112(int paramInt1, int paramInt2, int paramInt3)
  {
    pool.put11(paramInt1, paramInt2).putShort(paramInt3);
  }
  
  static
  {
    byte[] arrayOfByte = new byte['Ü'];
    String str = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
    for (int i = 0; i < arrayOfByte.length; i++) {
      arrayOfByte[i] = ((byte)(str.charAt(i) - 'A'));
    }
    TYPE = arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\ClassWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */