package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.ClassQueue;
import com.sun.org.apache.bcel.internal.util.ClassVector;
import com.sun.org.apache.bcel.internal.util.Repository;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class JavaClass
  extends AccessFlags
  implements Cloneable, Node
{
  private String file_name;
  private String package_name;
  private String source_file_name = "<Unknown>";
  private int class_name_index;
  private int superclass_name_index;
  private String class_name;
  private String superclass_name;
  private int major;
  private int minor;
  private ConstantPool constant_pool;
  private int[] interfaces;
  private String[] interface_names;
  private Field[] fields;
  private Method[] methods;
  private Attribute[] attributes;
  private byte source = 1;
  public static final byte HEAP = 1;
  public static final byte FILE = 2;
  public static final byte ZIP = 3;
  static boolean debug = false;
  static char sep = '/';
  private transient Repository repository = SyntheticRepository.getInstance();
  
  public JavaClass(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool, int[] paramArrayOfInt, Field[] paramArrayOfField, Method[] paramArrayOfMethod, Attribute[] paramArrayOfAttribute, byte paramByte)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[0];
    }
    if (paramArrayOfAttribute == null) {
      attributes = new Attribute[0];
    }
    if (paramArrayOfField == null) {
      paramArrayOfField = new Field[0];
    }
    if (paramArrayOfMethod == null) {
      paramArrayOfMethod = new Method[0];
    }
    class_name_index = paramInt1;
    superclass_name_index = paramInt2;
    file_name = paramString;
    major = paramInt3;
    minor = paramInt4;
    access_flags = paramInt5;
    constant_pool = paramConstantPool;
    interfaces = paramArrayOfInt;
    fields = paramArrayOfField;
    methods = paramArrayOfMethod;
    attributes = paramArrayOfAttribute;
    source = paramByte;
    for (int i = 0; i < paramArrayOfAttribute.length; i++) {
      if ((paramArrayOfAttribute[i] instanceof SourceFile))
      {
        source_file_name = ((SourceFile)paramArrayOfAttribute[i]).getSourceFileName();
        break;
      }
    }
    class_name = paramConstantPool.getConstantString(paramInt1, (byte)7);
    class_name = Utility.compactClassName(class_name, false);
    i = class_name.lastIndexOf('.');
    if (i < 0) {
      package_name = "";
    } else {
      package_name = class_name.substring(0, i);
    }
    if (paramInt2 > 0)
    {
      superclass_name = paramConstantPool.getConstantString(paramInt2, (byte)7);
      superclass_name = Utility.compactClassName(superclass_name, false);
    }
    else
    {
      superclass_name = "java.lang.Object";
    }
    interface_names = new String[paramArrayOfInt.length];
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      String str = paramConstantPool.getConstantString(paramArrayOfInt[j], (byte)7);
      interface_names[j] = Utility.compactClassName(str, false);
    }
  }
  
  public JavaClass(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool, int[] paramArrayOfInt, Field[] paramArrayOfField, Method[] paramArrayOfMethod, Attribute[] paramArrayOfAttribute)
  {
    this(paramInt1, paramInt2, paramString, paramInt3, paramInt4, paramInt5, paramConstantPool, paramArrayOfInt, paramArrayOfField, paramArrayOfMethod, paramArrayOfAttribute, (byte)1);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitJavaClass(this);
  }
  
  static final void Debug(String paramString)
  {
    if (debug) {
      System.out.println(paramString);
    }
  }
  
  public void dump(File paramFile)
    throws IOException
  {
    String str = paramFile.getParent();
    if (str != null)
    {
      File localFile = new File(str);
      if (localFile != null) {
        localFile.mkdirs();
      }
    }
    dump(new DataOutputStream(new FileOutputStream(paramFile)));
  }
  
  public void dump(String paramString)
    throws IOException
  {
    dump(new File(paramString));
  }
  
  public byte[] getBytes()
  {
    localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      dump(localDataOutputStream);
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException2)
    {
      localIOException2.printStackTrace();
    }
    finally
    {
      try
      {
        localDataOutputStream.close();
      }
      catch (IOException localIOException4)
      {
        localIOException4.printStackTrace();
      }
    }
  }
  
  public void dump(OutputStream paramOutputStream)
    throws IOException
  {
    dump(new DataOutputStream(paramOutputStream));
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeInt(-889275714);
    paramDataOutputStream.writeShort(minor);
    paramDataOutputStream.writeShort(major);
    constant_pool.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(access_flags);
    paramDataOutputStream.writeShort(class_name_index);
    paramDataOutputStream.writeShort(superclass_name_index);
    paramDataOutputStream.writeShort(interfaces.length);
    for (int i = 0; i < interfaces.length; i++) {
      paramDataOutputStream.writeShort(interfaces[i]);
    }
    paramDataOutputStream.writeShort(fields.length);
    for (i = 0; i < fields.length; i++) {
      fields[i].dump(paramDataOutputStream);
    }
    paramDataOutputStream.writeShort(methods.length);
    for (i = 0; i < methods.length; i++) {
      methods[i].dump(paramDataOutputStream);
    }
    if (attributes != null)
    {
      paramDataOutputStream.writeShort(attributes.length);
      for (i = 0; i < attributes.length; i++) {
        attributes[i].dump(paramDataOutputStream);
      }
    }
    else
    {
      paramDataOutputStream.writeShort(0);
    }
    paramDataOutputStream.close();
  }
  
  public Attribute[] getAttributes()
  {
    return attributes;
  }
  
  public String getClassName()
  {
    return class_name;
  }
  
  public String getPackageName()
  {
    return package_name;
  }
  
  public int getClassNameIndex()
  {
    return class_name_index;
  }
  
  public ConstantPool getConstantPool()
  {
    return constant_pool;
  }
  
  public Field[] getFields()
  {
    return fields;
  }
  
  public String getFileName()
  {
    return file_name;
  }
  
  public String[] getInterfaceNames()
  {
    return interface_names;
  }
  
  public int[] getInterfaceIndices()
  {
    return interfaces;
  }
  
  public int getMajor()
  {
    return major;
  }
  
  public Method[] getMethods()
  {
    return methods;
  }
  
  public Method getMethod(java.lang.reflect.Method paramMethod)
  {
    for (int i = 0; i < methods.length; i++)
    {
      Method localMethod = methods[i];
      if ((paramMethod.getName().equals(localMethod.getName())) && (paramMethod.getModifiers() == localMethod.getModifiers()) && (Type.getSignature(paramMethod).equals(localMethod.getSignature()))) {
        return localMethod;
      }
    }
    return null;
  }
  
  public int getMinor()
  {
    return minor;
  }
  
  public String getSourceFileName()
  {
    return source_file_name;
  }
  
  public String getSuperclassName()
  {
    return superclass_name;
  }
  
  public int getSuperclassNameIndex()
  {
    return superclass_name_index;
  }
  
  public void setAttributes(Attribute[] paramArrayOfAttribute)
  {
    attributes = paramArrayOfAttribute;
  }
  
  public void setClassName(String paramString)
  {
    class_name = paramString;
  }
  
  public void setClassNameIndex(int paramInt)
  {
    class_name_index = paramInt;
  }
  
  public void setConstantPool(ConstantPool paramConstantPool)
  {
    constant_pool = paramConstantPool;
  }
  
  public void setFields(Field[] paramArrayOfField)
  {
    fields = paramArrayOfField;
  }
  
  public void setFileName(String paramString)
  {
    file_name = paramString;
  }
  
  public void setInterfaceNames(String[] paramArrayOfString)
  {
    interface_names = paramArrayOfString;
  }
  
  public void setInterfaces(int[] paramArrayOfInt)
  {
    interfaces = paramArrayOfInt;
  }
  
  public void setMajor(int paramInt)
  {
    major = paramInt;
  }
  
  public void setMethods(Method[] paramArrayOfMethod)
  {
    methods = paramArrayOfMethod;
  }
  
  public void setMinor(int paramInt)
  {
    minor = paramInt;
  }
  
  public void setSourceFileName(String paramString)
  {
    source_file_name = paramString;
  }
  
  public void setSuperclassName(String paramString)
  {
    superclass_name = paramString;
  }
  
  public void setSuperclassNameIndex(int paramInt)
  {
    superclass_name_index = paramInt;
  }
  
  public String toString()
  {
    String str = Utility.accessToString(access_flags, true);
    str = str + " ";
    StringBuffer localStringBuffer = new StringBuffer(str + Utility.classOrInterface(access_flags) + " " + class_name + " extends " + Utility.compactClassName(superclass_name, false) + '\n');
    int i = interfaces.length;
    int j;
    if (i > 0)
    {
      localStringBuffer.append("implements\t\t");
      for (j = 0; j < i; j++)
      {
        localStringBuffer.append(interface_names[j]);
        if (j < i - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append('\n');
    }
    localStringBuffer.append("filename\t\t" + file_name + '\n');
    localStringBuffer.append("compiled from\t\t" + source_file_name + '\n');
    localStringBuffer.append("compiler version\t" + major + "." + minor + '\n');
    localStringBuffer.append("access flags\t\t" + access_flags + '\n');
    localStringBuffer.append("constant pool\t\t" + constant_pool.getLength() + " entries\n");
    localStringBuffer.append("ACC_SUPER flag\t\t" + isSuper() + "\n");
    if (attributes.length > 0)
    {
      localStringBuffer.append("\nAttribute(s):\n");
      for (j = 0; j < attributes.length; j++) {
        localStringBuffer.append(indent(attributes[j]));
      }
    }
    if (fields.length > 0)
    {
      localStringBuffer.append("\n" + fields.length + " fields:\n");
      for (j = 0; j < fields.length; j++) {
        localStringBuffer.append("\t" + fields[j] + '\n');
      }
    }
    if (methods.length > 0)
    {
      localStringBuffer.append("\n" + methods.length + " methods:\n");
      for (j = 0; j < methods.length; j++) {
        localStringBuffer.append("\t" + methods[j] + '\n');
      }
    }
    return localStringBuffer.toString();
  }
  
  private static final String indent(Object paramObject)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramObject.toString(), "\n");
    StringBuffer localStringBuffer = new StringBuffer();
    while (localStringTokenizer.hasMoreTokens()) {
      localStringBuffer.append("\t" + localStringTokenizer.nextToken() + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public JavaClass copy()
  {
    JavaClass localJavaClass = null;
    try
    {
      localJavaClass = (JavaClass)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    constant_pool = constant_pool.copy();
    interfaces = ((int[])interfaces.clone());
    interface_names = ((String[])interface_names.clone());
    fields = new Field[fields.length];
    for (int i = 0; i < fields.length; i++) {
      fields[i] = fields[i].copy(constant_pool);
    }
    methods = new Method[methods.length];
    for (i = 0; i < methods.length; i++) {
      methods[i] = methods[i].copy(constant_pool);
    }
    attributes = new Attribute[attributes.length];
    for (i = 0; i < attributes.length; i++) {
      attributes[i] = attributes[i].copy(constant_pool);
    }
    return localJavaClass;
  }
  
  public final boolean isSuper()
  {
    return (access_flags & 0x20) != 0;
  }
  
  public final boolean isClass()
  {
    return (access_flags & 0x200) == 0;
  }
  
  public final byte getSource()
  {
    return source;
  }
  
  public Repository getRepository()
  {
    return repository;
  }
  
  public void setRepository(Repository paramRepository)
  {
    repository = paramRepository;
  }
  
  public final boolean instanceOf(JavaClass paramJavaClass)
  {
    if (equals(paramJavaClass)) {
      return true;
    }
    JavaClass[] arrayOfJavaClass = getSuperClasses();
    for (int i = 0; i < arrayOfJavaClass.length; i++) {
      if (arrayOfJavaClass[i].equals(paramJavaClass)) {
        return true;
      }
    }
    if (paramJavaClass.isInterface()) {
      return implementationOf(paramJavaClass);
    }
    return false;
  }
  
  public boolean implementationOf(JavaClass paramJavaClass)
  {
    if (!paramJavaClass.isInterface()) {
      throw new IllegalArgumentException(paramJavaClass.getClassName() + " is no interface");
    }
    if (equals(paramJavaClass)) {
      return true;
    }
    JavaClass[] arrayOfJavaClass = getAllInterfaces();
    for (int i = 0; i < arrayOfJavaClass.length; i++) {
      if (arrayOfJavaClass[i].equals(paramJavaClass)) {
        return true;
      }
    }
    return false;
  }
  
  public JavaClass getSuperClass()
  {
    if ("java.lang.Object".equals(getClassName())) {
      return null;
    }
    try
    {
      return repository.loadClass(getSuperclassName());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println(localClassNotFoundException);
    }
    return null;
  }
  
  public JavaClass[] getSuperClasses()
  {
    JavaClass localJavaClass = this;
    ClassVector localClassVector = new ClassVector();
    for (localJavaClass = localJavaClass.getSuperClass(); localJavaClass != null; localJavaClass = localJavaClass.getSuperClass()) {
      localClassVector.addElement(localJavaClass);
    }
    return localClassVector.toArray();
  }
  
  public JavaClass[] getInterfaces()
  {
    String[] arrayOfString = getInterfaceNames();
    JavaClass[] arrayOfJavaClass = new JavaClass[arrayOfString.length];
    try
    {
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfJavaClass[i] = repository.loadClass(arrayOfString[i]);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println(localClassNotFoundException);
      return null;
    }
    return arrayOfJavaClass;
  }
  
  public JavaClass[] getAllInterfaces()
  {
    ClassQueue localClassQueue = new ClassQueue();
    ClassVector localClassVector = new ClassVector();
    localClassQueue.enqueue(this);
    while (!localClassQueue.empty())
    {
      JavaClass localJavaClass1 = localClassQueue.dequeue();
      JavaClass localJavaClass2 = localJavaClass1.getSuperClass();
      JavaClass[] arrayOfJavaClass = localJavaClass1.getInterfaces();
      if (localJavaClass1.isInterface()) {
        localClassVector.addElement(localJavaClass1);
      } else if (localJavaClass2 != null) {
        localClassQueue.enqueue(localJavaClass2);
      }
      for (int i = 0; i < arrayOfJavaClass.length; i++) {
        localClassQueue.enqueue(arrayOfJavaClass[i]);
      }
    }
    return localClassVector.toArray();
  }
  
  static
  {
    String str1 = null;
    String str2 = null;
    try
    {
      str1 = SecuritySupport.getSystemProperty("JavaClass.debug");
      str2 = SecuritySupport.getSystemProperty("file.separator");
    }
    catch (SecurityException localSecurityException) {}
    if (str1 != null) {
      debug = new Boolean(str1).booleanValue();
    }
    if (str2 != null) {
      try
      {
        sep = str2.charAt(0);
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\JavaClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */