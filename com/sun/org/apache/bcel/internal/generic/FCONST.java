package com.sun.org.apache.bcel.internal.generic;

public class FCONST
  extends Instruction
  implements ConstantPushInstruction, TypedInstruction
{
  private float value;
  
  FCONST() {}
  
  public FCONST(float paramFloat)
  {
    super((short)11, (short)1);
    if (paramFloat == 0.0D) {
      opcode = 11;
    } else if (paramFloat == 1.0D) {
      opcode = 12;
    } else if (paramFloat == 2.0D) {
      opcode = 13;
    } else {
      throw new ClassGenException("FCONST can be used only for 0.0, 1.0 and 2.0: " + paramFloat);
    }
    value = paramFloat;
  }
  
  public Number getValue()
  {
    return new Float(value);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.FLOAT;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitFCONST(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FCONST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */