package com.sun.org.apache.bcel.internal.generic;

public class DCMPL
  extends Instruction
  implements TypedInstruction, StackProducer, StackConsumer
{
  public DCMPL()
  {
    super((short)151, (short)1);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.DOUBLE;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitDCMPL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DCMPL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */