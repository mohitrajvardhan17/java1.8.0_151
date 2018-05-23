package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class MONITORENTER
  extends Instruction
  implements ExceptionThrower, StackConsumer
{
  public MONITORENTER()
  {
    super((short)194, (short)1);
  }
  
  public Class[] getExceptions()
  {
    return new Class[] { ExceptionConstants.NULL_POINTER_EXCEPTION };
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitMONITORENTER(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\MONITORENTER.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */