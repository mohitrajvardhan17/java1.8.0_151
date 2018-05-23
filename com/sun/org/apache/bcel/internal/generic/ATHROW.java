package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class ATHROW
  extends Instruction
  implements UnconditionalBranch, ExceptionThrower
{
  public ATHROW()
  {
    super((short)191, (short)1);
  }
  
  public Class[] getExceptions()
  {
    return new Class[] { ExceptionConstants.THROWABLE };
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitUnconditionalBranch(this);
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitATHROW(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ATHROW.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */