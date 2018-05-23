package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLE
  extends IfInstruction
{
  IF_ICMPLE() {}
  
  public IF_ICMPLE(InstructionHandle paramInstructionHandle)
  {
    super((short)164, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ICMPGT(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPLE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */