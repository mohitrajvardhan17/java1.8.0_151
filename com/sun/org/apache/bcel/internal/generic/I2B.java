package com.sun.org.apache.bcel.internal.generic;

public class I2B
  extends ConversionInstruction
{
  public I2B()
  {
    super((short)145);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitI2B(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\I2B.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */