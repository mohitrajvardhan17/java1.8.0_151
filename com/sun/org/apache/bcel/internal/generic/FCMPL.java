package com.sun.org.apache.bcel.internal.generic;

public class FCMPL
  extends Instruction
  implements TypedInstruction, StackProducer, StackConsumer
{
  public FCMPL()
  {
    super((short)149, (short)1);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.FLOAT;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitFCMPL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FCMPL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */