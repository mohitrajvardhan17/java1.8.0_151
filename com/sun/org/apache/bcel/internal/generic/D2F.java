package com.sun.org.apache.bcel.internal.generic;

public class D2F
  extends ConversionInstruction
{
  public D2F()
  {
    super((short)144);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitD2F(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\D2F.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */