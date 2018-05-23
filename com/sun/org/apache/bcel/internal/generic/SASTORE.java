package com.sun.org.apache.bcel.internal.generic;

public class SASTORE
  extends ArrayInstruction
  implements StackConsumer
{
  public SASTORE()
  {
    super((short)86);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitSASTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\SASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */