package com.sun.org.apache.bcel.internal.generic;

public class POP2
  extends StackInstruction
  implements PopInstruction
{
  public POP2()
  {
    super((short)88);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitPopInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitPOP2(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\POP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */