package com.sun.org.apache.bcel.internal.generic;

public class IFEQ
  extends IfInstruction
{
  IFEQ() {}
  
  public IFEQ(InstructionHandle paramInstructionHandle)
  {
    super((short)153, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFNE(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFEQ(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFEQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */