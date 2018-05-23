package com.sun.org.apache.bcel.internal.generic;

public abstract class JsrInstruction
  extends BranchInstruction
  implements UnconditionalBranch, TypedInstruction, StackProducer
{
  JsrInstruction(short paramShort, InstructionHandle paramInstructionHandle)
  {
    super(paramShort, paramInstructionHandle);
  }
  
  JsrInstruction() {}
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return new ReturnaddressType(physicalSuccessor());
  }
  
  public InstructionHandle physicalSuccessor()
  {
    for (InstructionHandle localInstructionHandle1 = target; localInstructionHandle1.getPrev() != null; localInstructionHandle1 = localInstructionHandle1.getPrev()) {}
    while (localInstructionHandle1.getInstruction() != this) {
      localInstructionHandle1 = localInstructionHandle1.getNext();
    }
    InstructionHandle localInstructionHandle2 = localInstructionHandle1;
    while (localInstructionHandle1 != null)
    {
      localInstructionHandle1 = localInstructionHandle1.getNext();
      if ((localInstructionHandle1 != null) && (localInstructionHandle1.getInstruction() == this)) {
        throw new RuntimeException("physicalSuccessor() called on a shared JsrInstruction.");
      }
    }
    return localInstructionHandle2.getNext();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\JsrInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */