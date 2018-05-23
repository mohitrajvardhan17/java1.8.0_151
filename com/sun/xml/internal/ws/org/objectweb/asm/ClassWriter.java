package com.sun.xml.internal.ws.org.objectweb.asm;

public class ClassWriter
  implements ClassVisitor
{
  public static final int COMPUTE_MAXS = 1;
  public static final int COMPUTE_FRAMES = 2;
  static final int NOARG_INSN = 0;
  static final int SBYTE_INSN = 1;
  static final int SHORT_INSN = 2;
  static final int VAR_INSN = 3;
  static final int IMPLVAR_INSN = 4;
  static final int TYPE_INSN = 5;
  static final int FIELDORMETH_INSN = 6;
  static final int ITFMETH_INSN = 7;
  static final int LABEL_INSN = 8;
  static final int LABELW_INSN = 9;
  static final int LDC_INSN = 10;
  static final int LDCW_INSN = 11;
  static final int IINC_INSN = 12;
  static final int TABL_INSN = 13;
  static final int LOOK_INSN = 14;
  static final int MANA_INSN = 15;
  static final int WIDE_INSN = 16;
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
  static final int TYPE_NORMAL = 13;
  static final int TYPE_UNINIT = 14;
  static final int TYPE_MERGED = 15;
  ClassReader cr;
  int version;
  int index = 1;
  final ByteVector pool = new ByteVector();
  Item[] items = new Item['Ā'];
  int threshold = (int)(0.75D * items.length);
  final Item key = new Item();
  final Item key2 = new Item();
  final Item key3 = new Item();
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
  private Attribute attrs;
  private int innerClassesCount;
  private ByteVector innerClasses;
  FieldWriter firstField;
  FieldWriter lastField;
  MethodWriter firstMethod;
  MethodWriter lastMethod;
  private final boolean computeMaxs;
  private final boolean computeFrames;
  boolean invalidFrames;
  
  public ClassWriter(int paramInt)
  {
    computeMaxs = ((paramInt & 0x1) != 0);
    computeFrames = ((paramInt & 0x2) != 0);
  }
  
  public ClassWriter(ClassReader paramClassReader, int paramInt)
  {
    this(paramInt);
    paramClassReader.copyPool(this);
    cr = paramClassReader;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
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
  
  public void visitSource(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      sourceFile = newUTF8(paramString1);
    }
    if (paramString2 != null) {
      sourceDebug = new ByteVector().putUTF8(paramString2);
    }
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    enclosingMethodOwner = newClass(paramString1);
    if ((paramString2 != null) && (paramString3 != null)) {
      enclosingMethod = newNameType(paramString2, paramString3);
    }
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
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
  
  public void visitAttribute(Attribute paramAttribute)
  {
    next = attrs;
    attrs = paramAttribute;
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (innerClasses == null) {
      innerClasses = new ByteVector();
    }
    innerClassesCount += 1;
    innerClasses.putShort(paramString1 == null ? 0 : newClass(paramString1));
    innerClasses.putShort(paramString2 == null ? 0 : newClass(paramString2));
    innerClasses.putShort(paramString3 == null ? 0 : newUTF8(paramString3));
    innerClasses.putShort(paramInt);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    return new FieldWriter(this, paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    return new MethodWriter(this, paramInt, paramString1, paramString2, paramString3, paramArrayOfString, computeMaxs, computeFrames);
  }
  
  public void visitEnd() {}
  
  public byte[] toByteArray()
  {
    int i = 24 + 2 * interfaceCount;
    int j = 0;
    for (FieldWriter localFieldWriter = firstField; localFieldWriter != null; localFieldWriter = next)
    {
      j++;
      i += localFieldWriter.getSize();
    }
    int k = 0;
    for (MethodWriter localMethodWriter = firstMethod; localMethodWriter != null; localMethodWriter = next)
    {
      k++;
      i += localMethodWriter.getSize();
    }
    int m = 0;
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
      i += sourceDebug.length + 4;
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
    if (((access & 0x1000) != 0) && ((version & 0xFFFF) < 49))
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
    if (attrs != null)
    {
      m += attrs.getCount();
      i += attrs.getSize(this, null, 0, -1, -1);
    }
    i += pool.length;
    ByteVector localByteVector = new ByteVector(i);
    localByteVector.putInt(-889275714).putInt(version);
    localByteVector.putShort(index).putByteArray(pool.data, 0, pool.length);
    localByteVector.putShort(access).putShort(name).putShort(superName);
    localByteVector.putShort(interfaceCount);
    for (int n = 0; n < interfaceCount; n++) {
      localByteVector.putShort(interfaces[n]);
    }
    localByteVector.putShort(j);
    for (localFieldWriter = firstField; localFieldWriter != null; localFieldWriter = next) {
      localFieldWriter.put(localByteVector);
    }
    localByteVector.putShort(k);
    for (localMethodWriter = firstMethod; localMethodWriter != null; localMethodWriter = next) {
      localMethodWriter.put(localByteVector);
    }
    localByteVector.putShort(m);
    if (signature != 0) {
      localByteVector.putShort(newUTF8("Signature")).putInt(2).putShort(signature);
    }
    if (sourceFile != 0) {
      localByteVector.putShort(newUTF8("SourceFile")).putInt(2).putShort(sourceFile);
    }
    if (sourceDebug != null)
    {
      n = sourceDebug.length - 2;
      localByteVector.putShort(newUTF8("SourceDebugExtension")).putInt(n);
      localByteVector.putByteArray(sourceDebug.data, 2, n);
    }
    if (enclosingMethodOwner != 0)
    {
      localByteVector.putShort(newUTF8("EnclosingMethod")).putInt(4);
      localByteVector.putShort(enclosingMethodOwner).putShort(enclosingMethod);
    }
    if ((access & 0x20000) != 0) {
      localByteVector.putShort(newUTF8("Deprecated")).putInt(0);
    }
    if (((access & 0x1000) != 0) && ((version & 0xFFFF) < 49)) {
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
    if (attrs != null) {
      attrs.put(this, null, 0, -1, -1, localByteVector);
    }
    if (invalidFrames)
    {
      ClassWriter localClassWriter = new ClassWriter(2);
      new ClassReader(data).accept(localClassWriter, 4);
      return localClassWriter.toByteArray();
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
    if ((paramObject instanceof Type))
    {
      Type localType = (Type)paramObject;
      return newClassItem(localType.getSort() == 10 ? localType.getInternalName() : localType.getDescriptor());
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
      put(localItem);
      index += 2;
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
      put(localItem);
      index += 2;
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
    key2.set(12, paramString1, paramString2, null);
    Item localItem = get(key2);
    if (localItem == null)
    {
      put122(12, newUTF8(paramString1), newUTF8(paramString2));
      localItem = new Item(index++, key2);
      put(localItem);
    }
    return index;
  }
  
  int addType(String paramString)
  {
    key.set(13, paramString, null, null);
    Item localItem = get(key);
    if (localItem == null) {
      localItem = addType(key);
    }
    return index;
  }
  
  int addUninitializedType(String paramString, int paramInt)
  {
    key.type = 14;
    key.intVal = paramInt;
    key.strVal1 = paramString;
    key.hashCode = (0x7FFFFFFF & 14 + paramString.hashCode() + paramInt);
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
    key2.type = 15;
    key2.longVal = (paramInt1 | paramInt2 << 32);
    key2.hashCode = (0x7FFFFFFF & 15 + paramInt1 + paramInt2);
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
    Class localClass1;
    Class localClass2;
    try
    {
      localClass1 = Class.forName(paramString1.replace('/', '.'));
      localClass2 = Class.forName(paramString2.replace('/', '.'));
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
    for (Item localItem = items[(hashCode % items.length)]; (localItem != null) && (!paramItem.isEqualTo(localItem)); localItem = next) {}
    return localItem;
  }
  
  private void put(Item paramItem)
  {
    if (index > threshold)
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
  
  static
  {
    byte[] arrayOfByte = new byte['Ü'];
    String str = "AAAAAAAAAAAAAAAABCKLLDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIDNOAAAAAAGGGGGGGHAFBFAAFFAAQPIIJJIIIIIIIIIIIIIIIIII";
    for (int i = 0; i < arrayOfByte.length; i++) {
      arrayOfByte[i] = ((byte)(str.charAt(i) - 'A'));
    }
    TYPE = arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ClassWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */