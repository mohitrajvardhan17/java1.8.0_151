package com.sun.org.apache.bcel.internal.generic;

public class CASTORE
  extends ArrayInstruction
  implements StackConsumer
{
  public CASTORE()
  {
    super((short)85);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitCASTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\CASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */