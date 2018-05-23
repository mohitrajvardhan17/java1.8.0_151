package com.sun.org.apache.bcel.internal.generic;

public class FLOAD
  extends LoadInstruction
{
  FLOAD()
  {
    super((short)23, (short)34);
  }
  
  public FLOAD(int paramInt)
  {
    super((short)23, (short)34, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitFLOAD(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FLOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */