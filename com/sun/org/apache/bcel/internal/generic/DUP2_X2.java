package com.sun.org.apache.bcel.internal.generic;

public class DUP2_X2
  extends StackInstruction
{
  public DUP2_X2()
  {
    super((short)94);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitDUP2_X2(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DUP2_X2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */