package com.sun.org.apache.bcel.internal.generic;

public class ASTORE
  extends StoreInstruction
{
  ASTORE()
  {
    super((short)58, (short)75);
  }
  
  public ASTORE(int paramInt)
  {
    super((short)58, (short)75, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitASTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */