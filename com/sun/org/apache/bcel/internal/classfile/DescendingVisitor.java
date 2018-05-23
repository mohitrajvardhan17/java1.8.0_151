package com.sun.org.apache.bcel.internal.classfile;

import java.util.Stack;

public class DescendingVisitor
  implements Visitor
{
  private JavaClass clazz;
  private Visitor visitor;
  private Stack stack = new Stack();
  
  public Object predecessor()
  {
    return predecessor(0);
  }
  
  public Object predecessor(int paramInt)
  {
    int i = stack.size();
    if ((i < 2) || (paramInt < 0)) {
      return null;
    }
    return stack.elementAt(i - (paramInt + 2));
  }
  
  public Object current()
  {
    return stack.peek();
  }
  
  public DescendingVisitor(JavaClass paramJavaClass, Visitor paramVisitor)
  {
    clazz = paramJavaClass;
    visitor = paramVisitor;
  }
  
  public void visit()
  {
    clazz.accept(this);
  }
  
  public void visitJavaClass(JavaClass paramJavaClass)
  {
    stack.push(paramJavaClass);
    paramJavaClass.accept(visitor);
    Field[] arrayOfField = paramJavaClass.getFields();
    for (int i = 0; i < arrayOfField.length; i++) {
      arrayOfField[i].accept(this);
    }
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    for (int j = 0; j < arrayOfMethod.length; j++) {
      arrayOfMethod[j].accept(this);
    }
    Attribute[] arrayOfAttribute = paramJavaClass.getAttributes();
    for (int k = 0; k < arrayOfAttribute.length; k++) {
      arrayOfAttribute[k].accept(this);
    }
    paramJavaClass.getConstantPool().accept(this);
    stack.pop();
  }
  
  public void visitField(Field paramField)
  {
    stack.push(paramField);
    paramField.accept(visitor);
    Attribute[] arrayOfAttribute = paramField.getAttributes();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      arrayOfAttribute[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitConstantValue(ConstantValue paramConstantValue)
  {
    stack.push(paramConstantValue);
    paramConstantValue.accept(visitor);
    stack.pop();
  }
  
  public void visitMethod(Method paramMethod)
  {
    stack.push(paramMethod);
    paramMethod.accept(visitor);
    Attribute[] arrayOfAttribute = paramMethod.getAttributes();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      arrayOfAttribute[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitExceptionTable(ExceptionTable paramExceptionTable)
  {
    stack.push(paramExceptionTable);
    paramExceptionTable.accept(visitor);
    stack.pop();
  }
  
  public void visitCode(Code paramCode)
  {
    stack.push(paramCode);
    paramCode.accept(visitor);
    CodeException[] arrayOfCodeException = paramCode.getExceptionTable();
    for (int i = 0; i < arrayOfCodeException.length; i++) {
      arrayOfCodeException[i].accept(this);
    }
    Attribute[] arrayOfAttribute = paramCode.getAttributes();
    for (int j = 0; j < arrayOfAttribute.length; j++) {
      arrayOfAttribute[j].accept(this);
    }
    stack.pop();
  }
  
  public void visitCodeException(CodeException paramCodeException)
  {
    stack.push(paramCodeException);
    paramCodeException.accept(visitor);
    stack.pop();
  }
  
  public void visitLineNumberTable(LineNumberTable paramLineNumberTable)
  {
    stack.push(paramLineNumberTable);
    paramLineNumberTable.accept(visitor);
    LineNumber[] arrayOfLineNumber = paramLineNumberTable.getLineNumberTable();
    for (int i = 0; i < arrayOfLineNumber.length; i++) {
      arrayOfLineNumber[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitLineNumber(LineNumber paramLineNumber)
  {
    stack.push(paramLineNumber);
    paramLineNumber.accept(visitor);
    stack.pop();
  }
  
  public void visitLocalVariableTable(LocalVariableTable paramLocalVariableTable)
  {
    stack.push(paramLocalVariableTable);
    paramLocalVariableTable.accept(visitor);
    LocalVariable[] arrayOfLocalVariable = paramLocalVariableTable.getLocalVariableTable();
    for (int i = 0; i < arrayOfLocalVariable.length; i++) {
      arrayOfLocalVariable[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitLocalVariableTypeTable(LocalVariableTypeTable paramLocalVariableTypeTable)
  {
    stack.push(paramLocalVariableTypeTable);
    paramLocalVariableTypeTable.accept(visitor);
    LocalVariable[] arrayOfLocalVariable = paramLocalVariableTypeTable.getLocalVariableTypeTable();
    for (int i = 0; i < arrayOfLocalVariable.length; i++) {
      arrayOfLocalVariable[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitStackMap(StackMap paramStackMap)
  {
    stack.push(paramStackMap);
    paramStackMap.accept(visitor);
    StackMapEntry[] arrayOfStackMapEntry = paramStackMap.getStackMap();
    for (int i = 0; i < arrayOfStackMapEntry.length; i++) {
      arrayOfStackMapEntry[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitStackMapEntry(StackMapEntry paramStackMapEntry)
  {
    stack.push(paramStackMapEntry);
    paramStackMapEntry.accept(visitor);
    stack.pop();
  }
  
  public void visitLocalVariable(LocalVariable paramLocalVariable)
  {
    stack.push(paramLocalVariable);
    paramLocalVariable.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantPool(ConstantPool paramConstantPool)
  {
    stack.push(paramConstantPool);
    paramConstantPool.accept(visitor);
    Constant[] arrayOfConstant = paramConstantPool.getConstantPool();
    for (int i = 1; i < arrayOfConstant.length; i++) {
      if (arrayOfConstant[i] != null) {
        arrayOfConstant[i].accept(this);
      }
    }
    stack.pop();
  }
  
  public void visitConstantClass(ConstantClass paramConstantClass)
  {
    stack.push(paramConstantClass);
    paramConstantClass.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantDouble(ConstantDouble paramConstantDouble)
  {
    stack.push(paramConstantDouble);
    paramConstantDouble.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantFieldref(ConstantFieldref paramConstantFieldref)
  {
    stack.push(paramConstantFieldref);
    paramConstantFieldref.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantFloat(ConstantFloat paramConstantFloat)
  {
    stack.push(paramConstantFloat);
    paramConstantFloat.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantInteger(ConstantInteger paramConstantInteger)
  {
    stack.push(paramConstantInteger);
    paramConstantInteger.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref paramConstantInterfaceMethodref)
  {
    stack.push(paramConstantInterfaceMethodref);
    paramConstantInterfaceMethodref.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantLong(ConstantLong paramConstantLong)
  {
    stack.push(paramConstantLong);
    paramConstantLong.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantMethodref(ConstantMethodref paramConstantMethodref)
  {
    stack.push(paramConstantMethodref);
    paramConstantMethodref.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantNameAndType(ConstantNameAndType paramConstantNameAndType)
  {
    stack.push(paramConstantNameAndType);
    paramConstantNameAndType.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantString(ConstantString paramConstantString)
  {
    stack.push(paramConstantString);
    paramConstantString.accept(visitor);
    stack.pop();
  }
  
  public void visitConstantUtf8(ConstantUtf8 paramConstantUtf8)
  {
    stack.push(paramConstantUtf8);
    paramConstantUtf8.accept(visitor);
    stack.pop();
  }
  
  public void visitInnerClasses(InnerClasses paramInnerClasses)
  {
    stack.push(paramInnerClasses);
    paramInnerClasses.accept(visitor);
    InnerClass[] arrayOfInnerClass = paramInnerClasses.getInnerClasses();
    for (int i = 0; i < arrayOfInnerClass.length; i++) {
      arrayOfInnerClass[i].accept(this);
    }
    stack.pop();
  }
  
  public void visitInnerClass(InnerClass paramInnerClass)
  {
    stack.push(paramInnerClass);
    paramInnerClass.accept(visitor);
    stack.pop();
  }
  
  public void visitDeprecated(Deprecated paramDeprecated)
  {
    stack.push(paramDeprecated);
    paramDeprecated.accept(visitor);
    stack.pop();
  }
  
  public void visitSignature(Signature paramSignature)
  {
    stack.push(paramSignature);
    paramSignature.accept(visitor);
    stack.pop();
  }
  
  public void visitSourceFile(SourceFile paramSourceFile)
  {
    stack.push(paramSourceFile);
    paramSourceFile.accept(visitor);
    stack.pop();
  }
  
  public void visitSynthetic(Synthetic paramSynthetic)
  {
    stack.push(paramSynthetic);
    paramSynthetic.accept(visitor);
    stack.pop();
  }
  
  public void visitUnknown(Unknown paramUnknown)
  {
    stack.push(paramUnknown);
    paramUnknown.accept(visitor);
    stack.pop();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\DescendingVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */