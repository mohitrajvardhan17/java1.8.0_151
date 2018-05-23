package com.sun.org.apache.bcel.internal.generic;

public abstract class StoreInstruction
  extends LocalVariableInstruction
  implements PopInstruction
{
  StoreInstruction(short paramShort1, short paramShort2)
  {
    super(paramShort1, paramShort2);
  }
  
  protected StoreInstruction(short paramShort1, short paramShort2, int paramInt)
  {
    super(paramShort1, paramShort2, paramInt);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitPopInstruction(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitLocalVariableInstruction(this);
    paramVisitor.visitStoreInstruction(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\StoreInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */