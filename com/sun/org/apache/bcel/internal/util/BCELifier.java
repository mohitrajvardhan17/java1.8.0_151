package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.EmptyVisitor;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.OutputStream;
import java.io.PrintWriter;

public class BCELifier
  extends EmptyVisitor
{
  private JavaClass _clazz;
  private PrintWriter _out;
  private ConstantPoolGen _cp;
  
  public BCELifier(JavaClass paramJavaClass, OutputStream paramOutputStream)
  {
    _clazz = paramJavaClass;
    _out = new PrintWriter(paramOutputStream);
    _cp = new ConstantPoolGen(_clazz.getConstantPool());
  }
  
  public void start()
  {
    visitJavaClass(_clazz);
    _out.flush();
  }
  
  public void visitJavaClass(JavaClass paramJavaClass)
  {
    String str1 = paramJavaClass.getClassName();
    String str2 = paramJavaClass.getSuperclassName();
    String str3 = paramJavaClass.getPackageName();
    String str4 = Utility.printArray(paramJavaClass.getInterfaceNames(), false, true);
    if (!"".equals(str3))
    {
      str1 = str1.substring(str3.length() + 1);
      _out.println("package " + str3 + ";\n");
    }
    _out.println("import com.sun.org.apache.bcel.internal.generic.*;");
    _out.println("import com.sun.org.apache.bcel.internal.classfile.*;");
    _out.println("import com.sun.org.apache.bcel.internal.*;");
    _out.println("import java.io.*;\n");
    _out.println("public class " + str1 + "Creator implements Constants {");
    _out.println("  private InstructionFactory _factory;");
    _out.println("  private ConstantPoolGen    _cp;");
    _out.println("  private ClassGen           _cg;\n");
    _out.println("  public " + str1 + "Creator() {");
    _out.println("    _cg = new ClassGen(\"" + ("".equals(str3) ? str1 : new StringBuilder().append(str3).append(".").append(str1).toString()) + "\", \"" + str2 + "\", \"" + paramJavaClass.getSourceFileName() + "\", " + printFlags(paramJavaClass.getAccessFlags(), true) + ", new String[] { " + str4 + " });\n");
    _out.println("    _cp = _cg.getConstantPool();");
    _out.println("    _factory = new InstructionFactory(_cg, _cp);");
    _out.println("  }\n");
    printCreate();
    Field[] arrayOfField = paramJavaClass.getFields();
    if (arrayOfField.length > 0)
    {
      _out.println("  private void createFields() {");
      _out.println("    FieldGen field;");
      for (int i = 0; i < arrayOfField.length; i++) {
        arrayOfField[i].accept(this);
      }
      _out.println("  }\n");
    }
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    for (int j = 0; j < arrayOfMethod.length; j++)
    {
      _out.println("  private void createMethod_" + j + "() {");
      arrayOfMethod[j].accept(this);
      _out.println("  }\n");
    }
    printMain();
    _out.println("}");
  }
  
  private void printCreate()
  {
    _out.println("  public void create(OutputStream out) throws IOException {");
    Field[] arrayOfField = _clazz.getFields();
    if (arrayOfField.length > 0) {
      _out.println("    createFields();");
    }
    Method[] arrayOfMethod = _clazz.getMethods();
    for (int i = 0; i < arrayOfMethod.length; i++) {
      _out.println("    createMethod_" + i + "();");
    }
    _out.println("    _cg.getJavaClass().dump(out);");
    _out.println("  }\n");
  }
  
  private void printMain()
  {
    String str = _clazz.getClassName();
    _out.println("  public static void _main(String[] args) throws Exception {");
    _out.println("    " + str + "Creator creator = new " + str + "Creator();");
    _out.println("    creator.create(new FileOutputStream(\"" + str + ".class\"));");
    _out.println("  }");
  }
  
  public void visitField(Field paramField)
  {
    _out.println("\n    field = new FieldGen(" + printFlags(paramField.getAccessFlags()) + ", " + printType(paramField.getSignature()) + ", \"" + paramField.getName() + "\", _cp);");
    ConstantValue localConstantValue = paramField.getConstantValue();
    if (localConstantValue != null)
    {
      String str = localConstantValue.toString();
      _out.println("    field.setInitValue(" + str + ")");
    }
    _out.println("    _cg.addField(field.getField());");
  }
  
  public void visitMethod(Method paramMethod)
  {
    MethodGen localMethodGen = new MethodGen(paramMethod, _clazz.getClassName(), _cp);
    Type localType = localMethodGen.getReturnType();
    Type[] arrayOfType = localMethodGen.getArgumentTypes();
    _out.println("    InstructionList il = new InstructionList();");
    _out.println("    MethodGen method = new MethodGen(" + printFlags(paramMethod.getAccessFlags()) + ", " + printType(localType) + ", " + printArgumentTypes(arrayOfType) + ", new String[] { " + Utility.printArray(localMethodGen.getArgumentNames(), false, true) + " }, \"" + paramMethod.getName() + "\", \"" + _clazz.getClassName() + "\", il, _cp);\n");
    BCELFactory localBCELFactory = new BCELFactory(localMethodGen, _out);
    localBCELFactory.start();
    _out.println("    method.setMaxStack();");
    _out.println("    method.setMaxLocals();");
    _out.println("    _cg.addMethod(method.getMethod());");
    _out.println("    il.dispose();");
  }
  
  static String printFlags(int paramInt)
  {
    return printFlags(paramInt, false);
  }
  
  static String printFlags(int paramInt, boolean paramBoolean)
  {
    if (paramInt == 0) {
      return "0";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = 1;
    while (i <= 2048)
    {
      if ((paramInt & j) != 0) {
        if ((j == 32) && (paramBoolean)) {
          localStringBuffer.append("ACC_SUPER | ");
        } else {
          localStringBuffer.append("ACC_" + com.sun.org.apache.bcel.internal.Constants.ACCESS_NAMES[i].toUpperCase() + " | ");
        }
      }
      j <<= 1;
      i++;
    }
    String str = localStringBuffer.toString();
    return str.substring(0, str.length() - 3);
  }
  
  static String printArgumentTypes(Type[] paramArrayOfType)
  {
    if (paramArrayOfType.length == 0) {
      return "Type.NO_ARGS";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfType.length; i++)
    {
      localStringBuffer.append(printType(paramArrayOfType[i]));
      if (i < paramArrayOfType.length - 1) {
        localStringBuffer.append(", ");
      }
    }
    return "new Type[] { " + localStringBuffer.toString() + " }";
  }
  
  static String printType(Type paramType)
  {
    return printType(paramType.getSignature());
  }
  
  static String printType(String paramString)
  {
    Type localType = Type.getType(paramString);
    int i = localType.getType();
    if (i <= 12) {
      return "Type." + com.sun.org.apache.bcel.internal.Constants.TYPE_NAMES[i].toUpperCase();
    }
    if (localType.toString().equals("java.lang.String")) {
      return "Type.STRING";
    }
    if (localType.toString().equals("java.lang.Object")) {
      return "Type.OBJECT";
    }
    if (localType.toString().equals("java.lang.StringBuffer")) {
      return "Type.STRINGBUFFER";
    }
    if ((localType instanceof ArrayType))
    {
      ArrayType localArrayType = (ArrayType)localType;
      return "new ArrayType(" + printType(localArrayType.getBasicType()) + ", " + localArrayType.getDimensions() + ")";
    }
    return "new ObjectType(\"" + Utility.signatureToString(paramString, false) + "\")";
  }
  
  public static void _main(String[] paramArrayOfString)
    throws Exception
  {
    String str = paramArrayOfString[0];
    JavaClass localJavaClass;
    if ((localJavaClass = Repository.lookupClass(str)) == null) {
      localJavaClass = new ClassParser(str).parse();
    }
    BCELifier localBCELifier = new BCELifier(localJavaClass, System.out);
    localBCELifier.start();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\BCELifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */