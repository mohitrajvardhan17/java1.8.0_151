package com.sun.org.apache.bcel.internal.generic;

public class ILOAD
  extends LoadInstruction
{
  ILOAD()
  {
    super((short)21, (short)26);
  }
  
  public ILOAD(int paramInt)
  {
    super((short)21, (short)26, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitILOAD(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ILOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */