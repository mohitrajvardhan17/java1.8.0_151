package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTypeTable;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

public class MethodGen
  extends FieldGenOrMethodGen
{
  private String class_name;
  private Type[] arg_types;
  private String[] arg_names;
  private int max_locals;
  private int max_stack;
  private InstructionList il;
  private boolean strip_attributes;
  private ArrayList variable_vec = new ArrayList();
  private ArrayList type_vec = new ArrayList();
  private ArrayList line_number_vec = new ArrayList();
  private ArrayList exception_vec = new ArrayList();
  private ArrayList throws_vec = new ArrayList();
  private ArrayList code_attrs_vec = new ArrayList();
  private ArrayList observers;
  
  public MethodGen(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen)
  {
    setAccessFlags(paramInt);
    setType(paramType);
    setArgumentTypes(paramArrayOfType);
    setArgumentNames(paramArrayOfString);
    setName(paramString1);
    setClassName(paramString2);
    setInstructionList(paramInstructionList);
    setConstantPool(paramConstantPoolGen);
    int i = (isAbstract()) || (isNative()) ? 1 : 0;
    InstructionHandle localInstructionHandle1 = null;
    InstructionHandle localInstructionHandle2 = null;
    if (i == 0)
    {
      localInstructionHandle1 = paramInstructionList.getStart();
      localInstructionHandle2 = paramInstructionList.getEnd();
      if ((!isStatic()) && (paramString2 != null)) {
        addLocalVariable("this", new ObjectType(paramString2), localInstructionHandle1, localInstructionHandle2);
      }
    }
    if (paramArrayOfType != null)
    {
      int j = paramArrayOfType.length;
      for (int k = 0; k < j; k++) {
        if (Type.VOID == paramArrayOfType[k]) {
          throw new ClassGenException("'void' is an illegal argument type for a method");
        }
      }
      if (paramArrayOfString != null)
      {
        if (j != paramArrayOfString.length) {
          throw new ClassGenException("Mismatch in argument array lengths: " + j + " vs. " + paramArrayOfString.length);
        }
      }
      else
      {
        paramArrayOfString = new String[j];
        for (k = 0; k < j; k++) {
          paramArrayOfString[k] = ("arg" + k);
        }
        setArgumentNames(paramArrayOfString);
      }
      if (i == 0) {
        for (k = 0; k < j; k++) {
          addLocalVariable(paramArrayOfString[k], paramArrayOfType[k], localInstructionHandle1, localInstructionHandle2);
        }
      }
    }
  }
  
  public MethodGen(Method paramMethod, String paramString, ConstantPoolGen paramConstantPoolGen)
  {
    this(paramMethod.getAccessFlags(), Type.getReturnType(paramMethod.getSignature()), Type.getArgumentTypes(paramMethod.getSignature()), null, paramMethod.getName(), paramString, (paramMethod.getAccessFlags() & 0x500) == 0 ? new InstructionList(paramMethod.getCode().getCode()) : null, paramConstantPoolGen);
    Attribute[] arrayOfAttribute1 = paramMethod.getAttributes();
    for (int i = 0; i < arrayOfAttribute1.length; i++)
    {
      Attribute localAttribute = arrayOfAttribute1[i];
      Object localObject1;
      if ((localAttribute instanceof Code))
      {
        localObject1 = (Code)localAttribute;
        setMaxStack(((Code)localObject1).getMaxStack());
        setMaxLocals(((Code)localObject1).getMaxLocals());
        CodeException[] arrayOfCodeException = ((Code)localObject1).getExceptionTable();
        InstructionHandle localInstructionHandle2;
        if (arrayOfCodeException != null) {
          for (int k = 0; k < arrayOfCodeException.length; k++)
          {
            CodeException localCodeException = arrayOfCodeException[k];
            int n = localCodeException.getCatchType();
            ObjectType localObjectType = null;
            if (n > 0)
            {
              String str = paramMethod.getConstantPool().getConstantString(n, (byte)7);
              localObjectType = new ObjectType(str);
            }
            int i2 = localCodeException.getEndPC();
            int i3 = paramMethod.getCode().getCode().length;
            if (i3 == i2)
            {
              localInstructionHandle2 = il.getEnd();
            }
            else
            {
              localInstructionHandle2 = il.findHandle(i2);
              localInstructionHandle2 = localInstructionHandle2.getPrev();
            }
            addExceptionHandler(il.findHandle(localCodeException.getStartPC()), localInstructionHandle2, il.findHandle(localCodeException.getHandlerPC()), localObjectType);
          }
        }
        Attribute[] arrayOfAttribute2 = ((Code)localObject1).getAttributes();
        for (int m = 0; m < arrayOfAttribute2.length; m++)
        {
          localAttribute = arrayOfAttribute2[m];
          Object localObject2;
          int i1;
          Object localObject3;
          if ((localAttribute instanceof LineNumberTable))
          {
            localObject2 = ((LineNumberTable)localAttribute).getLineNumberTable();
            for (i1 = 0; i1 < localObject2.length; i1++)
            {
              localObject3 = localObject2[i1];
              addLineNumber(il.findHandle(((LineNumber)localObject3).getStartPC()), ((LineNumber)localObject3).getLineNumber());
            }
          }
          else
          {
            InstructionHandle localInstructionHandle1;
            if ((localAttribute instanceof LocalVariableTable))
            {
              localObject2 = ((LocalVariableTable)localAttribute).getLocalVariableTable();
              removeLocalVariables();
              for (i1 = 0; i1 < localObject2.length; i1++)
              {
                localObject3 = localObject2[i1];
                localInstructionHandle1 = il.findHandle(((LocalVariable)localObject3).getStartPC());
                localInstructionHandle2 = il.findHandle(((LocalVariable)localObject3).getStartPC() + ((LocalVariable)localObject3).getLength());
                if (null == localInstructionHandle1) {
                  localInstructionHandle1 = il.getStart();
                }
                if (null == localInstructionHandle2) {
                  localInstructionHandle2 = il.getEnd();
                }
                addLocalVariable(((LocalVariable)localObject3).getName(), Type.getType(((LocalVariable)localObject3).getSignature()), ((LocalVariable)localObject3).getIndex(), localInstructionHandle1, localInstructionHandle2);
              }
            }
            else if ((localAttribute instanceof LocalVariableTypeTable))
            {
              localObject2 = ((LocalVariableTypeTable)localAttribute).getLocalVariableTypeTable();
              removeLocalVariableTypes();
              for (i1 = 0; i1 < localObject2.length; i1++)
              {
                localObject3 = localObject2[i1];
                localInstructionHandle1 = il.findHandle(((LocalVariable)localObject3).getStartPC());
                localInstructionHandle2 = il.findHandle(((LocalVariable)localObject3).getStartPC() + ((LocalVariable)localObject3).getLength());
                if (null == localInstructionHandle1) {
                  localInstructionHandle1 = il.getStart();
                }
                if (null == localInstructionHandle2) {
                  localInstructionHandle2 = il.getEnd();
                }
                addLocalVariableType(((LocalVariable)localObject3).getName(), Type.getType(((LocalVariable)localObject3).getSignature()), ((LocalVariable)localObject3).getIndex(), localInstructionHandle1, localInstructionHandle2);
              }
            }
            else
            {
              addCodeAttribute(localAttribute);
            }
          }
        }
      }
      else if ((localAttribute instanceof ExceptionTable))
      {
        localObject1 = ((ExceptionTable)localAttribute).getExceptionNames();
        for (int j = 0; j < localObject1.length; j++) {
          addException(localObject1[j]);
        }
      }
      else
      {
        addAttribute(localAttribute);
      }
    }
  }
  
  public LocalVariableGen addLocalVariable(String paramString, Type paramType, int paramInt, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    int i = paramType.getType();
    if (i != 16)
    {
      int j = paramType.getSize();
      if (paramInt + j > max_locals) {
        max_locals = (paramInt + j);
      }
      LocalVariableGen localLocalVariableGen = new LocalVariableGen(paramInt, paramString, paramType, paramInstructionHandle1, paramInstructionHandle2);
      int k;
      if ((k = variable_vec.indexOf(localLocalVariableGen)) >= 0) {
        variable_vec.set(k, localLocalVariableGen);
      } else {
        variable_vec.add(localLocalVariableGen);
      }
      return localLocalVariableGen;
    }
    throw new IllegalArgumentException("Can not use " + paramType + " as type for local variable");
  }
  
  public LocalVariableGen addLocalVariable(String paramString, Type paramType, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    return addLocalVariable(paramString, paramType, max_locals, paramInstructionHandle1, paramInstructionHandle2);
  }
  
  public void removeLocalVariable(LocalVariableGen paramLocalVariableGen)
  {
    variable_vec.remove(paramLocalVariableGen);
  }
  
  public void removeLocalVariables()
  {
    variable_vec.clear();
  }
  
  private static final void sort(LocalVariableGen[] paramArrayOfLocalVariableGen, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = paramInt2;
    int k = paramArrayOfLocalVariableGen[((paramInt1 + paramInt2) / 2)].getIndex();
    do
    {
      while (paramArrayOfLocalVariableGen[i].getIndex() < k) {
        i++;
      }
      while (k < paramArrayOfLocalVariableGen[j].getIndex()) {
        j--;
      }
      if (i <= j)
      {
        LocalVariableGen localLocalVariableGen = paramArrayOfLocalVariableGen[i];
        paramArrayOfLocalVariableGen[i] = paramArrayOfLocalVariableGen[j];
        paramArrayOfLocalVariableGen[j] = localLocalVariableGen;
        i++;
        j--;
      }
    } while (i <= j);
    if (paramInt1 < j) {
      sort(paramArrayOfLocalVariableGen, paramInt1, j);
    }
    if (i < paramInt2) {
      sort(paramArrayOfLocalVariableGen, i, paramInt2);
    }
  }
  
  public LocalVariableGen[] getLocalVariables()
  {
    int i = variable_vec.size();
    LocalVariableGen[] arrayOfLocalVariableGen = new LocalVariableGen[i];
    variable_vec.toArray(arrayOfLocalVariableGen);
    for (int j = 0; j < i; j++)
    {
      if (arrayOfLocalVariableGen[j].getStart() == null) {
        arrayOfLocalVariableGen[j].setStart(il.getStart());
      }
      if (arrayOfLocalVariableGen[j].getEnd() == null) {
        arrayOfLocalVariableGen[j].setEnd(il.getEnd());
      }
    }
    if (i > 1) {
      sort(arrayOfLocalVariableGen, 0, i - 1);
    }
    return arrayOfLocalVariableGen;
  }
  
  private LocalVariableGen[] getLocalVariableTypes()
  {
    int i = type_vec.size();
    LocalVariableGen[] arrayOfLocalVariableGen = new LocalVariableGen[i];
    type_vec.toArray(arrayOfLocalVariableGen);
    for (int j = 0; j < i; j++)
    {
      if (arrayOfLocalVariableGen[j].getStart() == null) {
        arrayOfLocalVariableGen[j].setStart(il.getStart());
      }
      if (arrayOfLocalVariableGen[j].getEnd() == null) {
        arrayOfLocalVariableGen[j].setEnd(il.getEnd());
      }
    }
    if (i > 1) {
      sort(arrayOfLocalVariableGen, 0, i - 1);
    }
    return arrayOfLocalVariableGen;
  }
  
  public LocalVariableTable getLocalVariableTable(ConstantPoolGen paramConstantPoolGen)
  {
    LocalVariableGen[] arrayOfLocalVariableGen = getLocalVariables();
    int i = arrayOfLocalVariableGen.length;
    LocalVariable[] arrayOfLocalVariable = new LocalVariable[i];
    for (int j = 0; j < i; j++) {
      arrayOfLocalVariable[j] = arrayOfLocalVariableGen[j].getLocalVariable(paramConstantPoolGen);
    }
    return new LocalVariableTable(paramConstantPoolGen.addUtf8("LocalVariableTable"), 2 + arrayOfLocalVariable.length * 10, arrayOfLocalVariable, paramConstantPoolGen.getConstantPool());
  }
  
  public LocalVariableTypeTable getLocalVariableTypeTable(ConstantPoolGen paramConstantPoolGen)
  {
    LocalVariableGen[] arrayOfLocalVariableGen = getLocalVariableTypes();
    int i = arrayOfLocalVariableGen.length;
    LocalVariable[] arrayOfLocalVariable = new LocalVariable[i];
    for (int j = 0; j < i; j++) {
      arrayOfLocalVariable[j] = arrayOfLocalVariableGen[j].getLocalVariable(paramConstantPoolGen);
    }
    return new LocalVariableTypeTable(paramConstantPoolGen.addUtf8("LocalVariableTypeTable"), 2 + arrayOfLocalVariable.length * 10, arrayOfLocalVariable, paramConstantPoolGen.getConstantPool());
  }
  
  private LocalVariableGen addLocalVariableType(String paramString, Type paramType, int paramInt, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    int i = paramType.getType();
    if (i != 16)
    {
      int j = paramType.getSize();
      if (paramInt + j > max_locals) {
        max_locals = (paramInt + j);
      }
      LocalVariableGen localLocalVariableGen = new LocalVariableGen(paramInt, paramString, paramType, paramInstructionHandle1, paramInstructionHandle2);
      int k;
      if ((k = type_vec.indexOf(localLocalVariableGen)) >= 0) {
        type_vec.set(k, localLocalVariableGen);
      } else {
        type_vec.add(localLocalVariableGen);
      }
      return localLocalVariableGen;
    }
    throw new IllegalArgumentException("Can not use " + paramType + " as type for local variable");
  }
  
  private void removeLocalVariableTypes()
  {
    type_vec.clear();
  }
  
  public LineNumberGen addLineNumber(InstructionHandle paramInstructionHandle, int paramInt)
  {
    LineNumberGen localLineNumberGen = new LineNumberGen(paramInstructionHandle, paramInt);
    line_number_vec.add(localLineNumberGen);
    return localLineNumberGen;
  }
  
  public void removeLineNumber(LineNumberGen paramLineNumberGen)
  {
    line_number_vec.remove(paramLineNumberGen);
  }
  
  public void removeLineNumbers()
  {
    line_number_vec.clear();
  }
  
  public LineNumberGen[] getLineNumbers()
  {
    LineNumberGen[] arrayOfLineNumberGen = new LineNumberGen[line_number_vec.size()];
    line_number_vec.toArray(arrayOfLineNumberGen);
    return arrayOfLineNumberGen;
  }
  
  public LineNumberTable getLineNumberTable(ConstantPoolGen paramConstantPoolGen)
  {
    int i = line_number_vec.size();
    LineNumber[] arrayOfLineNumber = new LineNumber[i];
    try
    {
      for (int j = 0; j < i; j++) {
        arrayOfLineNumber[j] = ((LineNumberGen)line_number_vec.get(j)).getLineNumber();
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return new LineNumberTable(paramConstantPoolGen.addUtf8("LineNumberTable"), 2 + arrayOfLineNumber.length * 4, arrayOfLineNumber, paramConstantPoolGen.getConstantPool());
  }
  
  public CodeExceptionGen addExceptionHandler(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, InstructionHandle paramInstructionHandle3, ObjectType paramObjectType)
  {
    if ((paramInstructionHandle1 == null) || (paramInstructionHandle2 == null) || (paramInstructionHandle3 == null)) {
      throw new ClassGenException("Exception handler target is null instruction");
    }
    CodeExceptionGen localCodeExceptionGen = new CodeExceptionGen(paramInstructionHandle1, paramInstructionHandle2, paramInstructionHandle3, paramObjectType);
    exception_vec.add(localCodeExceptionGen);
    return localCodeExceptionGen;
  }
  
  public void removeExceptionHandler(CodeExceptionGen paramCodeExceptionGen)
  {
    exception_vec.remove(paramCodeExceptionGen);
  }
  
  public void removeExceptionHandlers()
  {
    exception_vec.clear();
  }
  
  public CodeExceptionGen[] getExceptionHandlers()
  {
    CodeExceptionGen[] arrayOfCodeExceptionGen = new CodeExceptionGen[exception_vec.size()];
    exception_vec.toArray(arrayOfCodeExceptionGen);
    return arrayOfCodeExceptionGen;
  }
  
  private CodeException[] getCodeExceptions()
  {
    int i = exception_vec.size();
    CodeException[] arrayOfCodeException = new CodeException[i];
    try
    {
      for (int j = 0; j < i; j++)
      {
        CodeExceptionGen localCodeExceptionGen = (CodeExceptionGen)exception_vec.get(j);
        arrayOfCodeException[j] = localCodeExceptionGen.getCodeException(cp);
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return arrayOfCodeException;
  }
  
  public void addException(String paramString)
  {
    throws_vec.add(paramString);
  }
  
  public void removeException(String paramString)
  {
    throws_vec.remove(paramString);
  }
  
  public void removeExceptions()
  {
    throws_vec.clear();
  }
  
  public String[] getExceptions()
  {
    String[] arrayOfString = new String[throws_vec.size()];
    throws_vec.toArray(arrayOfString);
    return arrayOfString;
  }
  
  private ExceptionTable getExceptionTable(ConstantPoolGen paramConstantPoolGen)
  {
    int i = throws_vec.size();
    int[] arrayOfInt = new int[i];
    try
    {
      for (int j = 0; j < i; j++) {
        arrayOfInt[j] = paramConstantPoolGen.addClass((String)throws_vec.get(j));
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return new ExceptionTable(paramConstantPoolGen.addUtf8("Exceptions"), 2 + 2 * i, arrayOfInt, paramConstantPoolGen.getConstantPool());
  }
  
  public void addCodeAttribute(Attribute paramAttribute)
  {
    code_attrs_vec.add(paramAttribute);
  }
  
  public void removeCodeAttribute(Attribute paramAttribute)
  {
    code_attrs_vec.remove(paramAttribute);
  }
  
  public void removeCodeAttributes()
  {
    code_attrs_vec.clear();
  }
  
  public Attribute[] getCodeAttributes()
  {
    Attribute[] arrayOfAttribute = new Attribute[code_attrs_vec.size()];
    code_attrs_vec.toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public Method getMethod()
  {
    String str = getSignature();
    int i = cp.addUtf8(name);
    int j = cp.addUtf8(str);
    byte[] arrayOfByte = null;
    if (il != null) {
      arrayOfByte = il.getByteCode();
    }
    LineNumberTable localLineNumberTable = null;
    LocalVariableTable localLocalVariableTable = null;
    LocalVariableTypeTable localLocalVariableTypeTable = null;
    if ((variable_vec.size() > 0) && (!strip_attributes)) {
      addCodeAttribute(localLocalVariableTable = getLocalVariableTable(cp));
    }
    if ((type_vec.size() > 0) && (!strip_attributes)) {
      addCodeAttribute(localLocalVariableTypeTable = getLocalVariableTypeTable(cp));
    }
    if ((line_number_vec.size() > 0) && (!strip_attributes)) {
      addCodeAttribute(localLineNumberTable = getLineNumberTable(cp));
    }
    Attribute[] arrayOfAttribute = getCodeAttributes();
    int k = 0;
    for (int m = 0; m < arrayOfAttribute.length; m++) {
      k += arrayOfAttribute[m].getLength() + 6;
    }
    CodeException[] arrayOfCodeException = getCodeExceptions();
    int n = arrayOfCodeException.length * 8;
    Code localCode = null;
    if ((il != null) && (!isAbstract()))
    {
      localObject = getAttributes();
      for (int i1 = 0; i1 < localObject.length; i1++)
      {
        Attribute localAttribute = localObject[i1];
        if ((localAttribute instanceof Code)) {
          removeAttribute(localAttribute);
        }
      }
      localCode = new Code(cp.addUtf8("Code"), 8 + arrayOfByte.length + 2 + n + 2 + k, max_stack, max_locals, arrayOfByte, arrayOfCodeException, arrayOfAttribute, cp.getConstantPool());
      addAttribute(localCode);
    }
    Object localObject = null;
    if (throws_vec.size() > 0) {
      addAttribute(localObject = getExceptionTable(cp));
    }
    Method localMethod = new Method(access_flags, i, j, getAttributes(), cp.getConstantPool());
    if (localLocalVariableTable != null) {
      removeCodeAttribute(localLocalVariableTable);
    }
    if (localLocalVariableTypeTable != null) {
      removeCodeAttribute(localLocalVariableTypeTable);
    }
    if (localLineNumberTable != null) {
      removeCodeAttribute(localLineNumberTable);
    }
    if (localCode != null) {
      removeAttribute(localCode);
    }
    if (localObject != null) {
      removeAttribute((Attribute)localObject);
    }
    return localMethod;
  }
  
  public void removeNOPs()
  {
    if (il != null)
    {
      InstructionHandle localInstructionHandle;
      for (Object localObject = il.getStart(); localObject != null; localObject = localInstructionHandle)
      {
        localInstructionHandle = next;
        if ((localInstructionHandle != null) && ((((InstructionHandle)localObject).getInstruction() instanceof NOP)))
        {
          InstructionHandle[] arrayOfInstructionHandle;
          int i;
          try
          {
            il.delete((InstructionHandle)localObject);
          }
          catch (TargetLostException localTargetLostException)
          {
            arrayOfInstructionHandle = localTargetLostException.getTargets();
            i = 0;
          }
          while (i < arrayOfInstructionHandle.length)
          {
            InstructionTargeter[] arrayOfInstructionTargeter = arrayOfInstructionHandle[i].getTargeters();
            for (int j = 0; j < arrayOfInstructionTargeter.length; j++) {
              arrayOfInstructionTargeter[j].updateTarget(arrayOfInstructionHandle[i], localInstructionHandle);
            }
            i++;
          }
        }
      }
    }
  }
  
  public void setMaxLocals(int paramInt)
  {
    max_locals = paramInt;
  }
  
  public int getMaxLocals()
  {
    return max_locals;
  }
  
  public void setMaxStack(int paramInt)
  {
    max_stack = paramInt;
  }
  
  public int getMaxStack()
  {
    return max_stack;
  }
  
  public String getClassName()
  {
    return class_name;
  }
  
  public void setClassName(String paramString)
  {
    class_name = paramString;
  }
  
  public void setReturnType(Type paramType)
  {
    setType(paramType);
  }
  
  public Type getReturnType()
  {
    return getType();
  }
  
  public void setArgumentTypes(Type[] paramArrayOfType)
  {
    arg_types = paramArrayOfType;
  }
  
  public Type[] getArgumentTypes()
  {
    return (Type[])arg_types.clone();
  }
  
  public void setArgumentType(int paramInt, Type paramType)
  {
    arg_types[paramInt] = paramType;
  }
  
  public Type getArgumentType(int paramInt)
  {
    return arg_types[paramInt];
  }
  
  public void setArgumentNames(String[] paramArrayOfString)
  {
    arg_names = paramArrayOfString;
  }
  
  public String[] getArgumentNames()
  {
    return (String[])arg_names.clone();
  }
  
  public void setArgumentName(int paramInt, String paramString)
  {
    arg_names[paramInt] = paramString;
  }
  
  public String getArgumentName(int paramInt)
  {
    return arg_names[paramInt];
  }
  
  public InstructionList getInstructionList()
  {
    return il;
  }
  
  public void setInstructionList(InstructionList paramInstructionList)
  {
    il = paramInstructionList;
  }
  
  public String getSignature()
  {
    return Type.getMethodSignature(type, arg_types);
  }
  
  public void setMaxStack()
  {
    if (il != null) {
      max_stack = getMaxStack(cp, il, getExceptionHandlers());
    } else {
      max_stack = 0;
    }
  }
  
  public void setMaxLocals()
  {
    if (il != null)
    {
      int i = isStatic() ? 0 : 1;
      if (arg_types != null) {
        for (int j = 0; j < arg_types.length; j++) {
          i += arg_types[j].getSize();
        }
      }
      for (InstructionHandle localInstructionHandle = il.getStart(); localInstructionHandle != null; localInstructionHandle = localInstructionHandle.getNext())
      {
        Instruction localInstruction = localInstructionHandle.getInstruction();
        if (((localInstruction instanceof LocalVariableInstruction)) || ((localInstruction instanceof RET)) || ((localInstruction instanceof IINC)))
        {
          int k = ((IndexedInstruction)localInstruction).getIndex() + ((TypedInstruction)localInstruction).getType(cp).getSize();
          if (k > i) {
            i = k;
          }
        }
      }
      max_locals = i;
    }
    else
    {
      max_locals = 0;
    }
  }
  
  public void stripAttributes(boolean paramBoolean)
  {
    strip_attributes = paramBoolean;
  }
  
  public static int getMaxStack(ConstantPoolGen paramConstantPoolGen, InstructionList paramInstructionList, CodeExceptionGen[] paramArrayOfCodeExceptionGen)
  {
    BranchStack localBranchStack = new BranchStack();
    for (int i = 0; i < paramArrayOfCodeExceptionGen.length; i++)
    {
      InstructionHandle localInstructionHandle1 = paramArrayOfCodeExceptionGen[i].getHandlerPC();
      if (localInstructionHandle1 != null) {
        localBranchStack.push(localInstructionHandle1, 1);
      }
    }
    i = 0;
    int j = 0;
    InstructionHandle localInstructionHandle2 = paramInstructionList.getStart();
    while (localInstructionHandle2 != null)
    {
      Instruction localInstruction = localInstructionHandle2.getInstruction();
      int k = localInstruction.getOpcode();
      int m = localInstruction.produceStack(paramConstantPoolGen) - localInstruction.consumeStack(paramConstantPoolGen);
      i += m;
      if (i > j) {
        j = i;
      }
      Object localObject;
      if ((localInstruction instanceof BranchInstruction))
      {
        localObject = (BranchInstruction)localInstruction;
        if ((localInstruction instanceof Select))
        {
          Select localSelect = (Select)localObject;
          InstructionHandle[] arrayOfInstructionHandle = localSelect.getTargets();
          for (int n = 0; n < arrayOfInstructionHandle.length; n++) {
            localBranchStack.push(arrayOfInstructionHandle[n], i);
          }
          localInstructionHandle2 = null;
        }
        else if (!(localObject instanceof IfInstruction))
        {
          if ((k == 168) || (k == 201)) {
            localBranchStack.push(localInstructionHandle2.getNext(), i - 1);
          }
          localInstructionHandle2 = null;
        }
        localBranchStack.push(((BranchInstruction)localObject).getTarget(), i);
      }
      else if ((k == 191) || (k == 169) || ((k >= 172) && (k <= 177)))
      {
        localInstructionHandle2 = null;
      }
      if (localInstructionHandle2 != null) {
        localInstructionHandle2 = localInstructionHandle2.getNext();
      }
      if (localInstructionHandle2 == null)
      {
        localObject = localBranchStack.pop();
        if (localObject != null)
        {
          localInstructionHandle2 = target;
          i = stackDepth;
        }
      }
    }
    return j;
  }
  
  public void addObserver(MethodObserver paramMethodObserver)
  {
    if (observers == null) {
      observers = new ArrayList();
    }
    observers.add(paramMethodObserver);
  }
  
  public void removeObserver(MethodObserver paramMethodObserver)
  {
    if (observers != null) {
      observers.remove(paramMethodObserver);
    }
  }
  
  public void update()
  {
    if (observers != null)
    {
      Iterator localIterator = observers.iterator();
      while (localIterator.hasNext()) {
        ((MethodObserver)localIterator.next()).notify(this);
      }
    }
  }
  
  public final String toString()
  {
    String str1 = Utility.accessToString(access_flags);
    String str2 = Type.getMethodSignature(type, arg_types);
    str2 = Utility.methodSignatureToString(str2, name, str1, true, getLocalVariableTable(cp));
    StringBuffer localStringBuffer = new StringBuffer(str2);
    if (throws_vec.size() > 0)
    {
      Iterator localIterator = throws_vec.iterator();
      while (localIterator.hasNext()) {
        localStringBuffer.append("\n\t\tthrows " + localIterator.next());
      }
    }
    return localStringBuffer.toString();
  }
  
  public MethodGen copy(String paramString, ConstantPoolGen paramConstantPoolGen)
  {
    Method localMethod = ((MethodGen)clone()).getMethod();
    MethodGen localMethodGen = new MethodGen(localMethod, paramString, cp);
    if (cp != paramConstantPoolGen)
    {
      localMethodGen.setConstantPool(paramConstantPoolGen);
      localMethodGen.getInstructionList().replaceConstantPool(cp, paramConstantPoolGen);
    }
    return localMethodGen;
  }
  
  static final class BranchStack
  {
    Stack branchTargets = new Stack();
    Hashtable visitedTargets = new Hashtable();
    
    BranchStack() {}
    
    public void push(InstructionHandle paramInstructionHandle, int paramInt)
    {
      if (visited(paramInstructionHandle)) {
        return;
      }
      branchTargets.push(visit(paramInstructionHandle, paramInt));
    }
    
    public MethodGen.BranchTarget pop()
    {
      if (!branchTargets.empty())
      {
        MethodGen.BranchTarget localBranchTarget = (MethodGen.BranchTarget)branchTargets.pop();
        return localBranchTarget;
      }
      return null;
    }
    
    private final MethodGen.BranchTarget visit(InstructionHandle paramInstructionHandle, int paramInt)
    {
      MethodGen.BranchTarget localBranchTarget = new MethodGen.BranchTarget(paramInstructionHandle, paramInt);
      visitedTargets.put(paramInstructionHandle, localBranchTarget);
      return localBranchTarget;
    }
    
    private final boolean visited(InstructionHandle paramInstructionHandle)
    {
      return visitedTargets.get(paramInstructionHandle) != null;
    }
  }
  
  static final class BranchTarget
  {
    InstructionHandle target;
    int stackDepth;
    
    BranchTarget(InstructionHandle paramInstructionHandle, int paramInt)
    {
      target = paramInstructionHandle;
      stackDepth = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\MethodGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */