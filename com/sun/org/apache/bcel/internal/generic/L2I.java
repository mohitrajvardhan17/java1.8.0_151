package com.sun.org.apache.bcel.internal.generic;

public class L2I
  extends ConversionInstruction
{
  public L2I()
  {
    super((short)136);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitConversionInstruction(this);
    paramVisitor.visitL2I(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\L2I.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */