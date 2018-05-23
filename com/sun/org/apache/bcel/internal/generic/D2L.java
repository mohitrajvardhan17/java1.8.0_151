package com.sun.org.apache.bcel.internal.generic;

public class D2L
  extends ConversionInstruction
{
  public D2L()
  {
    super((short)143);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitD2L(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\D2L.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */