package org.omg.CORBA.portable;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class UnknownException
  extends SystemException
{
  public Throwable originalEx;
  
  public UnknownException(Throwable paramThrowable)
  {
    super("", 0, CompletionStatus.COMPLETED_MAYBE);
    originalEx = paramThrowable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\UnknownException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */