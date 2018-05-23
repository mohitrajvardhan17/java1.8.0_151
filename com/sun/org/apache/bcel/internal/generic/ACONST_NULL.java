package com.sun.org.apache.bcel.internal.generic;

public class ACONST_NULL
  extends Instruction
  implements PushInstruction, TypedInstruction
{
  public ACONST_NULL()
  {
    super((short)1, (short)1);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.NULL;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitACONST_NULL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ACONST_NULL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */