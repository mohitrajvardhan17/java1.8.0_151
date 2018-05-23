package com.sun.org.apache.bcel.internal.generic;

public class IMPDEP1
  extends Instruction
{
  public IMPDEP1()
  {
    super((short)254, (short)1);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitIMPDEP1(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IMPDEP1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */