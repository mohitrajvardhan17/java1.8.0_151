package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPNE
  extends IfInstruction
{
  IF_ACMPNE() {}
  
  public IF_ACMPNE(InstructionHandle paramInstructionHandle)
  {
    super((short)166, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ACMPEQ(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ACMPNE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ACMPNE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */