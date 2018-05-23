package com.sun.org.apache.bcel.internal.generic;

public class DUP
  extends StackInstruction
  implements PushInstruction
{
  public DUP()
  {
    super((short)89);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitDUP(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DUP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */