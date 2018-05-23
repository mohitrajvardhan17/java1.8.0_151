package com.sun.org.apache.bcel.internal.generic;

public class LLOAD
  extends LoadInstruction
{
  LLOAD()
  {
    super((short)22, (short)30);
  }
  
  public LLOAD(int paramInt)
  {
    super((short)22, (short)30, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitLLOAD(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\LLOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */