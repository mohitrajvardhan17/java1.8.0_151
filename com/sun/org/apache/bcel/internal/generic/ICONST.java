package com.sun.org.apache.bcel.internal.generic;

public class ICONST
  extends Instruction
  implements ConstantPushInstruction, TypedInstruction
{
  private int value;
  
  ICONST() {}
  
  public ICONST(int paramInt)
  {
    super((short)3, (short)1);
    if ((paramInt >= -1) && (paramInt <= 5)) {
      opcode = ((short)(3 + paramInt));
    } else {
      throw new ClassGenException("ICONST can be used only for value between -1 and 5: " + paramInt);
    }
    value = paramInt;
  }
  
  public Number getValue()
  {
    return new Integer(value);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.INT;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitPushInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitConstantPushInstruction(this);
    paramVisitor.visitICONST(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ICONST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */