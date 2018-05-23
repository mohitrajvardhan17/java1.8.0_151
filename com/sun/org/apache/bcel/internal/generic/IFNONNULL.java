package com.sun.org.apache.bcel.internal.generic;

public class IFNONNULL
  extends IfInstruction
{
  IFNONNULL() {}
  
  public IFNONNULL(InstructionHandle paramInstructionHandle)
  {
    super((short)199, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFNULL(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFNONNULL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFNONNULL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */