package com.sun.org.apache.bcel.internal.generic;

public class ISTORE
  extends StoreInstruction
{
  ISTORE()
  {
    super((short)54, (short)59);
  }
  
  public ISTORE(int paramInt)
  {
    super((short)54, (short)59, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    super.accept(paramVisitor);
    paramVisitor.visitISTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ISTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */