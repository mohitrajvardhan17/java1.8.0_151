package com.sun.org.apache.bcel.internal.generic;

public class DUP_X2
  extends StackInstruction
{
  public DUP_X2()
  {
    super((short)91);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackInstruction(this);
    paramVisitor.visitDUP_X2(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DUP_X2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */