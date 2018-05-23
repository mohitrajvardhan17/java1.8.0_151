package com.sun.org.apache.bcel.internal.generic;

public class POP
  extends StackInstruction
  implements PopInstruction
{
  public POP()
  {
    super((short)87);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitPopInstruction(this);
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitPOP(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\POP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */