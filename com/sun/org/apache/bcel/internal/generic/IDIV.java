package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class IDIV
  extends ArithmeticInstruction
  implements ExceptionThrower
{
  public IDIV()
  {
    super((short)108);
  }
  
  public Class[] getExceptions()
  {
    return new Class[] { ExceptionConstants.ARITHMETIC_EXCEPTION };
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitArithmeticInstruction(this);
    paramVisitor.visitIDIV(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\IDIV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */