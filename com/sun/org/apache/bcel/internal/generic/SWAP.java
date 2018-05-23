package com.sun.org.apache.bcel.internal.generic;

public class SWAP
  extends StackInstruction
  implements StackConsumer, StackProducer
{
  public SWAP()
  {
    super((short)95);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitSWAP(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\SWAP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */