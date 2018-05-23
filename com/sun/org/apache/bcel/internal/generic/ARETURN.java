package com.sun.org.apache.bcel.internal.generic;

public class ARETURN
  extends ReturnInstruction
{
  public ARETURN()
  {
    super((short)176);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitARETURN(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ARETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */