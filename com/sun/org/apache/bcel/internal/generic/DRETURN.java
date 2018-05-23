package com.sun.org.apache.bcel.internal.generic;

public class DRETURN
  extends ReturnInstruction
{
  public DRETURN()
  {
    super((short)175);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitDRETURN(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DRETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */