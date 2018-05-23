package com.sun.org.apache.bcel.internal.generic;

public class DASTORE
  extends ArrayInstruction
  implements StackConsumer
{
  public DASTORE()
  {
    super((short)82);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitDASTORE(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\DASTORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */