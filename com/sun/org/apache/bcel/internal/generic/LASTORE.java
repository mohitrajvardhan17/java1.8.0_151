package com.sun.org.apache.bcel.internal.generic;

public class LASTORE
  extends ArrayInstruction
  implements StackConsumer
{
  public LASTORE()
  {
    super((short)80);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitLASTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */