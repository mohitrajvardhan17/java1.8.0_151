package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPNE
  extends IfInstruction
{
  IF_ICMPNE() {}
  
  public IF_ICMPNE(InstructionHandle paramInstructionHandle)
  {
    super((short)160, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ICMPEQ(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPNE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPNE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */