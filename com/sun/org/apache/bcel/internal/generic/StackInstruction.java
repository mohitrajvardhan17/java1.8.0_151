package com.sun.org.apache.bcel.internal.generic;

public abstract class StackInstruction
  extends Instruction
{
  StackInstruction() {}
  
  protected StackInstruction(short paramShort)
  {
    super(paramShort, (short)1);
  }
  
  public Type getType(ConstantPoolGen paramConstantPoolGen)
  {
    return Type.UNKNOWN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\StackInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */