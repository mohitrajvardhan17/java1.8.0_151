package com.sun.org.apache.bcel.internal.generic;

public class ALOAD
  extends LoadInstruction
{
  ALOAD()
  {
    super((short)25, (short)42);
  }
  
  public ALOAD(int paramInt)
  {
    super((short)25, (short)42, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitALOAD(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ALOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */