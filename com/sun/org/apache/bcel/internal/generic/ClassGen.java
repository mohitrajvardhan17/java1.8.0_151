package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.AccessFlags;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.SourceFile;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ClassGen
  extends AccessFlags
  implements Cloneable
{
  private String class_name;
  private String super_class_name;
  private String file_name;
  private int class_name_index = -1;
  private int superclass_name_index = -1;
  private int major = 45;
  private int minor = 3;
  private ConstantPoolGen cp;
  private ArrayList field_vec = new ArrayList();
  private ArrayList method_vec = new ArrayList();
  private ArrayList attribute_vec = new ArrayList();
  private ArrayList interface_vec = new ArrayList();
  private ArrayList observers;
  
  public ClassGen(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, ConstantPoolGen paramConstantPoolGen)
  {
    class_name = paramString1;
    super_class_name = paramString2;
    file_name = paramString3;
    access_flags = paramInt;
    cp = paramConstantPoolGen;
    if (paramString3 != null) {
      addAttribute(new SourceFile(paramConstantPoolGen.addUtf8("SourceFile"), 2, paramConstantPoolGen.addUtf8(paramString3), paramConstantPoolGen.getConstantPool()));
    }
    class_name_index = paramConstantPoolGen.addClass(paramString1);
    superclass_name_index = paramConstantPoolGen.addClass(paramString2);
    if (paramArrayOfString != null) {
      for (int i = 0; i < paramArrayOfString.length; i++) {
        addInterface(paramArrayOfString[i]);
      }
    }
  }
  
  public ClassGen(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString)
  {
    this(paramString1, paramString2, paramString3, paramInt, paramArrayOfString, new ConstantPoolGen());
  }
  
  public ClassGen(JavaClass paramJavaClass)
  {
    class_name_index = paramJavaClass.getClassNameIndex();
    superclass_name_index = paramJavaClass.getSuperclassNameIndex();
    class_name = paramJavaClass.getClassName();
    super_class_name = paramJavaClass.getSuperclassName();
    file_name = paramJavaClass.getSourceFileName();
    access_flags = paramJavaClass.getAccessFlags();
    cp = new ConstantPoolGen(paramJavaClass.getConstantPool());
    major = paramJavaClass.getMajor();
    minor = paramJavaClass.getMinor();
    Attribute[] arrayOfAttribute = paramJavaClass.getAttributes();
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    Field[] arrayOfField = paramJavaClass.getFields();
    String[] arrayOfString = paramJavaClass.getInterfaceNames();
    for (int i = 0; i < arrayOfString.length; i++) {
      addInterface(arrayOfString[i]);
    }
    for (i = 0; i < arrayOfAttribute.length; i++) {
      addAttribute(arrayOfAttribute[i]);
    }
    for (i = 0; i < arrayOfMethod.length; i++) {
      addMethod(arrayOfMethod[i]);
    }
    for (i = 0; i < arrayOfField.length; i++) {
      addField(arrayOfField[i]);
    }
  }
  
  public JavaClass getJavaClass()
  {
    int[] arrayOfInt = getInterfaces();
    Field[] arrayOfField = getFields();
    Method[] arrayOfMethod = getMethods();
    Attribute[] arrayOfAttribute = getAttributes();
    ConstantPool localConstantPool = cp.getFinalConstantPool();
    return new JavaClass(class_name_index, superclass_name_index, file_name, major, minor, access_flags, localConstantPool, arrayOfInt, arrayOfField, arrayOfMethod, arrayOfAttribute);
  }
  
  public void addInterface(String paramString)
  {
    interface_vec.add(paramString);
  }
  
  public void removeInterface(String paramString)
  {
    interface_vec.remove(paramString);
  }
  
  public int getMajor()
  {
    return major;
  }
  
  public void setMajor(int paramInt)
  {
    major = paramInt;
  }
  
  public void setMinor(int paramInt)
  {
    minor = paramInt;
  }
  
  public int getMinor()
  {
    return minor;
  }
  
  public void addAttribute(Attribute paramAttribute)
  {
    attribute_vec.add(paramAttribute);
  }
  
  public void addMethod(Method paramMethod)
  {
    method_vec.add(paramMethod);
  }
  
  public void addEmptyConstructor(int paramInt)
  {
    InstructionList localInstructionList = new InstructionList();
    localInstructionList.append(InstructionConstants.THIS);
    localInstructionList.append(new INVOKESPECIAL(cp.addMethodref(super_class_name, "<init>", "()V")));
    localInstructionList.append(InstructionConstants.RETURN);
    MethodGen localMethodGen = new MethodGen(paramInt, Type.VOID, Type.NO_ARGS, null, "<init>", class_name, localInstructionList, cp);
    localMethodGen.setMaxStack(1);
    addMethod(localMethodGen.getMethod());
  }
  
  public void addField(Field paramField)
  {
    field_vec.add(paramField);
  }
  
  public boolean containsField(Field paramField)
  {
    return field_vec.contains(paramField);
  }
  
  public Field containsField(String paramString)
  {
    Iterator localIterator = field_vec.iterator();
    while (localIterator.hasNext())
    {
      Field localField = (Field)localIterator.next();
      if (localField.getName().equals(paramString)) {
        return localField;
      }
    }
    return null;
  }
  
  public Method containsMethod(String paramString1, String paramString2)
  {
    Iterator localIterator = method_vec.iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      if ((localMethod.getName().equals(paramString1)) && (localMethod.getSignature().equals(paramString2))) {
        return localMethod;
      }
    }
    return null;
  }
  
  public void removeAttribute(Attribute paramAttribute)
  {
    attribute_vec.remove(paramAttribute);
  }
  
  public void removeMethod(Method paramMethod)
  {
    method_vec.remove(paramMethod);
  }
  
  public void replaceMethod(Method paramMethod1, Method paramMethod2)
  {
    if (paramMethod2 == null) {
      throw new ClassGenException("Replacement method must not be null");
    }
    int i = method_vec.indexOf(paramMethod1);
    if (i < 0) {
      method_vec.add(paramMethod2);
    } else {
      method_vec.set(i, paramMethod2);
    }
  }
  
  public void replaceField(Field paramField1, Field paramField2)
  {
    if (paramField2 == null) {
      throw new ClassGenException("Replacement method must not be null");
    }
    int i = field_vec.indexOf(paramField1);
    if (i < 0) {
      field_vec.add(paramField2);
    } else {
      field_vec.set(i, paramField2);
    }
  }
  
  public void removeField(Field paramField)
  {
    field_vec.remove(paramField);
  }
  
  public String getClassName()
  {
    return class_name;
  }
  
  public String getSuperclassName()
  {
    return super_class_name;
  }
  
  public String getFileName()
  {
    return file_name;
  }
  
  public void setClassName(String paramString)
  {
    class_name = paramString.replace('/', '.');
    class_name_index = cp.addClass(paramString);
  }
  
  public void setSuperclassName(String paramString)
  {
    super_class_name = paramString.replace('/', '.');
    superclass_name_index = cp.addClass(paramString);
  }
  
  public Method[] getMethods()
  {
    Method[] arrayOfMethod = new Method[method_vec.size()];
    method_vec.toArray(arrayOfMethod);
    return arrayOfMethod;
  }
  
  public void setMethods(Method[] paramArrayOfMethod)
  {
    method_vec.clear();
    for (int i = 0; i < paramArrayOfMethod.length; i++) {
      addMethod(paramArrayOfMethod[i]);
    }
  }
  
  public void setMethodAt(Method paramMethod, int paramInt)
  {
    method_vec.set(paramInt, paramMethod);
  }
  
  public Method getMethodAt(int paramInt)
  {
    return (Method)method_vec.get(paramInt);
  }
  
  public String[] getInterfaceNames()
  {
    int i = interface_vec.size();
    String[] arrayOfString = new String[i];
    interface_vec.toArray(arrayOfString);
    return arrayOfString;
  }
  
  public int[] getInterfaces()
  {
    int i = interface_vec.size();
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++) {
      arrayOfInt[j] = cp.addClass((String)interface_vec.get(j));
    }
    return arrayOfInt;
  }
  
  public Field[] getFields()
  {
    Field[] arrayOfField = new Field[field_vec.size()];
    field_vec.toArray(arrayOfField);
    return arrayOfField;
  }
  
  public Attribute[] getAttributes()
  {
    Attribute[] arrayOfAttribute = new Attribute[attribute_vec.size()];
    attribute_vec.toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public ConstantPoolGen getConstantPool()
  {
    return cp;
  }
  
  public void setConstantPool(ConstantPoolGen paramConstantPoolGen)
  {
    cp = paramConstantPoolGen;
  }
  
  public void setClassNameIndex(int paramInt)
  {
    class_name_index = paramInt;
    class_name = cp.getConstantPool().getConstantString(paramInt, (byte)7).replace('/', '.');
  }
  
  public void setSuperclassNameIndex(int paramInt)
  {
    superclass_name_index = paramInt;
    super_class_name = cp.getConstantPool().getConstantString(paramInt, (byte)7).replace('/', '.');
  }
  
  public int getSuperclassNameIndex()
  {
    return superclass_name_index;
  }
  
  public int getClassNameIndex()
  {
    return class_name_index;
  }
  
  public void addObserver(ClassObserver paramClassObserver)
  {
    if (observers == null) {
      observers = new ArrayList();
    }
    observers.add(paramClassObserver);
  }
  
  public void removeObserver(ClassObserver paramClassObserver)
  {
    if (observers != null) {
      observers.remove(paramClassObserver);
    }
  }
  
  public void update()
  {
    if (observers != null)
    {
      Iterator localIterator = observers.iterator();
      while (localIterator.hasNext()) {
        ((ClassObserver)localIterator.next()).notify(this);
      }
    }
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      System.err.println(localCloneNotSupportedException);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ClassGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */