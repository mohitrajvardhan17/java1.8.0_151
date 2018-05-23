package com.sun.org.apache.bcel.internal.generic;

public class IFNE
  extends IfInstruction
{
  IFNE() {}
  
  public IFNE(InstructionHandle paramInstructionHandle)
  {
    super((short)154, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFEQ(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFNE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFNE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */