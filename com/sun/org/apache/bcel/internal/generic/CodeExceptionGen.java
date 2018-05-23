package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.CodeException;
import java.io.PrintStream;
import java.io.Serializable;

public final class CodeExceptionGen
  implements InstructionTargeter, Cloneable, Serializable
{
  private InstructionHandle start_pc;
  private InstructionHandle end_pc;
  private InstructionHandle handler_pc;
  private ObjectType catch_type;
  
  public CodeExceptionGen(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, InstructionHandle paramInstructionHandle3, ObjectType paramObjectType)
  {
    setStartPC(paramInstructionHandle1);
    setEndPC(paramInstructionHandle2);
    setHandlerPC(paramInstructionHandle3);
    catch_type = paramObjectType;
  }
  
  public CodeException getCodeException(ConstantPoolGen paramConstantPoolGen)
  {
    return new CodeException(start_pc.getPosition(), end_pc.getPosition() + end_pc.getInstruction().getLength(), handler_pc.getPosition(), catch_type == null ? 0 : paramConstantPoolGen.addClass(catch_type));
  }
  
  public final void setStartPC(InstructionHandle paramInstructionHandle)
  {
    BranchInstruction.notifyTargetChanging(start_pc, this);
    start_pc = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(start_pc, this);
  }
  
  public final void setEndPC(InstructionHandle paramInstructionHandle)
  {
    BranchInstruction.notifyTargetChanging(end_pc, this);
    end_pc = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(end_pc, this);
  }
  
  public final void setHandlerPC(InstructionHandle paramInstructionHandle)
  {
    BranchInstruction.notifyTargetChanging(handler_pc, this);
    handler_pc = paramInstructionHandle;
    BranchInstruction.notifyTargetChanged(handler_pc, this);
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    int i = 0;
    if (start_pc == paramInstructionHandle1)
    {
      i = 1;
      setStartPC(paramInstructionHandle2);
    }
    if (end_pc == paramInstructionHandle1)
    {
      i = 1;
      setEndPC(paramInstructionHandle2);
    }
    if (handler_pc == paramInstructionHandle1)
    {
      i = 1;
      setHandlerPC(paramInstructionHandle2);
    }
    if (i == 0) {
      throw new ClassGenException("Not targeting " + paramInstructionHandle1 + ", but {" + start_pc + ", " + end_pc + ", " + handler_pc + "}");
    }
  }
  
  public boolean containsTarget(InstructionHandle paramInstructionHandle)
  {
    return (start_pc == paramInstructionHandle) || (end_pc == paramInstructionHandle) || (handler_pc == paramInstructionHandle);
  }
  
  public void setCatchType(ObjectType paramObjectType)
  {
    catch_type = paramObjectType;
  }
  
  public ObjectType getCatchType()
  {
    return catch_type;
  }
  
  public InstructionHandle getStartPC()
  {
    return start_pc;
  }
  
  public InstructionHandle getEndPC()
  {
    return end_pc;
  }
  
  public InstructionHandle getHandlerPC()
  {
    return handler_pc;
  }
  
  public String toString()
  {
    return "CodeExceptionGen(" + start_pc + ", " + end_pc + ", " + handler_pc + ")";
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\CodeExceptionGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */