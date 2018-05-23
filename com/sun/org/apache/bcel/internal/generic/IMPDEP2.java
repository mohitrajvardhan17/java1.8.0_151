package com.sun.org.apache.bcel.internal.generic;

public class IMPDEP2
  extends Instruction
{
  public IMPDEP2()
  {
    super((short)255, (short)1);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitIMPDEP2(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IMPDEP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */