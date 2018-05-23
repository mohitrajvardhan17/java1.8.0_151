package com.sun.org.apache.bcel.internal.generic;

public class IRETURN
  extends ReturnInstruction
{
  public IRETURN()
  {
    super((short)172);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitIRETURN(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IRETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */