package com.sun.org.apache.bcel.internal.generic;

public class IFGE
  extends IfInstruction
{
  IFGE() {}
  
  public IFGE(InstructionHandle paramInstructionHandle)
  {
    super((short)156, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFLT(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFGE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFGE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */