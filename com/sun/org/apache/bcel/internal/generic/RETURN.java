package com.sun.org.apache.bcel.internal.generic;

public class RETURN
  extends ReturnInstruction
{
  public RETURN()
  {
    super((short)177);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitReturnInstruction(this);
    paramVisitor.visitRETURN(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\RETURN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */