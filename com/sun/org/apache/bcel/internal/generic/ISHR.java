package com.sun.org.apache.bcel.internal.generic;

public class ISHR
  extends ArithmeticInstruction
{
  public ISHR()
  {
    super((short)122);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitISHR(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ISHR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */