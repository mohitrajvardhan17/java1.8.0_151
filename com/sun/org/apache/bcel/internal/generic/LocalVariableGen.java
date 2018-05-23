package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Objects;

public class LocalVariableGen
  implements InstructionTargeter, NamedAndTyped, Cloneable, Serializable
{
  private final int index;
  private String name;
  private Type type;
  private InstructionHandle start;
  private InstructionHandle end;
  
  public LocalVariableGen(int paramInt, String paramString, Type paramType, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw new ClassGenException("Invalid index index: " + paramInt);
    }
    name = paramString;
    type = paramType;
    index = paramInt;
    setStart(paramInstructionHandle1);
    setEnd(paramInstructionHandle2);
  }
  
  public LocalVariable getLocalVariable(ConstantPoolGen paramConstantPoolGen)
  {
    int i = start.getPosition();
    int j = end.getPosition() - i;
    if (j > 0) {
      j += end.getInstruction().getLength();
    }
    int k = paramConstantPoolGen.addUtf8(name);
    int m = paramConstantPoolGen.addUtf8(type.getSignature());
    return new LocalVariable(i, j, k, m, index, paramConstantPoolGen.getConstantPool());
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setType(Type paramType)
  {
    type = paramType;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public InstructionHandle getStart()
  {
    return start;
  }
  
  public InstructionHandle getEnd()
  {
    return end;
  }
  
  void notifyTargetChanging()
  {
    BranchInstruction.notifyTargetChanging(start, this);
    if (end != start) {
      BranchInstruction.notifyTargetChanging(end, this);
    }
  }
  
  void notifyTargetChanged()
  {
    BranchInstruction.notifyTargetChanged(start, this);
    if (end != start) {
      BranchInstruction.notifyTargetChanged(end, this);
    }
  }
  
  public final void setStart(InstructionHandle paramInstructionHandle)
  {
    notifyTargetChanging();
    start = paramInstructionHandle;
    notifyTargetChanged();
  }
  
  public final void setEnd(InstructionHandle paramInstructionHandle)
  {
    notifyTargetChanging();
    end = paramInstructionHandle;
    notifyTargetChanged();
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    int i = 0;
    if (start == paramInstructionHandle1)
    {
      i = 1;
      setStart(paramInstructionHandle2);
    }
    if (end == paramInstructionHandle1)
    {
      i = 1;
      setEnd(paramInstructionHandle2);
    }
    if (i == 0) {
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but {" + start + ", " + end + "}");
    }
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle)
  {
    return (start == paramInstructionHandle) || (end == paramInstructionHandle);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof LocalVariableGen)) {
      return false;
    }
    LocalVariableGen localLocalVariableGen = (LocalVariableGen)paramObject;
    return (index == index) && (start == start) && (end == end);
  }
  
  public int hashCode()
  {
    int i = 7;
    i = 59 * i + index;
    i = 59 * i + Objects.hashCode(start);
    i = 59 * i + Objects.hashCode(end);
    return i;
  }
  
  public String toString()
  {
    return "LocalVariableGen(" + name + ", " + type + ", " + start + ", " + end + ")";
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LocalVariableGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */