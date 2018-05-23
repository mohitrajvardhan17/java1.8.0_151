package com.sun.org.apache.bcel.internal.generic;

public class I2L
  extends ConversionInstruction
{
  public I2L()
  {
    super((short)133);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitI2L(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\I2L.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */