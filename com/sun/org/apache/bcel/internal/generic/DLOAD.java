package com.sun.org.apache.bcel.internal.generic;

public class DLOAD
  extends LoadInstruction
{
  DLOAD()
  {
    super((short)24, (short)38);
  }
  
  public DLOAD(int paramInt)
  {
    super((short)24, (short)38, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitDLOAD(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DLOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */