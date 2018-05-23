package com.sun.org.apache.bcel.internal.generic;

public class DUP2
  extends StackInstruction
  implements PushInstruction
{
  public DUP2()
  {
    super((short)92);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitDUP2(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DUP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */