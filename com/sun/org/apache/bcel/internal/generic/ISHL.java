package com.sun.org.apache.bcel.internal.generic;

public class ISHL
  extends ArithmeticInstruction
{
  public ISHL()
  {
    super((short)120);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitISHL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ISHL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */