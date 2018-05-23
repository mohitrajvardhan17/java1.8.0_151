package com.sun.org.apache.bcel.internal.generic;

public class IFNULL
  extends IfInstruction
{
  IFNULL() {}
  
  public IFNULL(InstructionHandle paramInstructionHandle)
  {
    super((short)198, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFNONNULL(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFNULL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFNULL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */