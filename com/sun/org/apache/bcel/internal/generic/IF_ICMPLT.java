package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLT
  extends IfInstruction
{
  IF_ICMPLT() {}
  
  public IF_ICMPLT(InstructionHandle paramInstructionHandle)
  {
    super((short)161, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IF_ICMPGE(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIF_ICMPLT(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IF_ICMPLT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */