package com.sun.org.apache.bcel.internal.generic;

public class BREAKPOINT
  extends Instruction
{
  public BREAKPOINT()
  {
    super((short)202, (short)1);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitBREAKPOINT(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\BREAKPOINT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */