package com.sun.org.apache.bcel.internal.generic;

public class NOP
  extends Instruction
{
  public NOP()
  {
    super((short)0, (short)1);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitNOP(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\NOP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */