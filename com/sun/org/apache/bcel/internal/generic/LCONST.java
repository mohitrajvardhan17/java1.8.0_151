package com.sun.org.apache.bcel.internal.generic;

public class LCONST
  extends Instruction
  implements ConstantPushInstruction, TypedInstruction
{
  private long value;
  
  LCONST() {}
  
  public LCONST(long paramLong)
  {
    super((short)9, (short)1);
    if (paramLong == 0L) {
      opcode = 9;
    } else if (paramLong == 1L) {
      opcode = 10;
    } else {
      throw new ClassGenException("LCONST can be used only for 0 and 1: " + paramLong);
    }
    value = paramLong;
  }
  
  public Number getValue()
  {
    return new Long(value);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.LONG;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitLCONST(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LCONST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */