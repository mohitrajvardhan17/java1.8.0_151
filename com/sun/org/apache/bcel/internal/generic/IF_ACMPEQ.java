package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPEQ
  extends IfInstruction
{
  IF_ACMPEQ() {}
  
  public IF_ACMPEQ(InstructionHandle paramInstructionHandle)
  {
    super((short)165, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ACMPNE(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ACMPEQ(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ACMPEQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */