package com.sun.org.apache.bcel.internal.generic;

public class D2I
  extends ConversionInstruction
{
  public D2I()
  {
    super((short)142);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitD2I(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\D2I.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */