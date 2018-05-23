package com.sun.org.apache.bcel.internal.generic;

public class I2C
  extends ConversionInstruction
{
  public I2C()
  {
    super((short)146);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitI2C(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\I2C.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */