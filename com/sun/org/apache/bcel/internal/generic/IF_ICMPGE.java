package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPGE
  extends IfInstruction
{
  IF_ICMPGE() {}
  
  public IF_ICMPGE(InstructionHandle paramInstructionHandle)
  {
    super((short)162, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ICMPLT(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPGE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPGE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */