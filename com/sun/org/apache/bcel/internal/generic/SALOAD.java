package com.sun.org.apache.bcel.internal.generic;

public class SALOAD
  extends ArrayInstruction
  implements StackProducer
{
  public SALOAD()
  {
    super((short)53);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitArrayInstruction(this);
    paramVisitor.visitSALOAD(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\SALOAD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */