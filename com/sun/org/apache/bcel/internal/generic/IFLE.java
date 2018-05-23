package com.sun.org.apache.bcel.internal.generic;

public class IFLE
  extends IfInstruction
{
  IFLE() {}
  
  public IFLE(InstructionHandle paramInstructionHandle)
  {
    super((short)158, paramInstructionHandle);
  }
  
  public IfInstruction negate()
  {
    return new IFGT(target);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitBranchInstruction(this);
    paramVisitor.visitIfInstruction(this);
    paramVisitor.visitIFLE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IFLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */