package com.sun.org.apache.bcel.internal.generic;

public class DSTORE
  extends StoreInstruction
{
  DSTORE()
  {
    super((short)57, (short)71);
  }
  
  public DSTORE(int paramInt)
  {
    super((short)57, (short)71, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitDSTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DSTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */