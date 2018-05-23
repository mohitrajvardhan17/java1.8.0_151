package com.sun.org.apache.bcel.internal.generic;

public class LUSHR
  extends ArithmeticInstruction
{
  public LUSHR()
  {
    super((short)125);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitLUSHR(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LUSHR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */