package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract interface ExceptionHandler
{
  public abstract boolean isDeclaredException(Class paramClass);
  
  public abstract void writeException(OutputStream paramOutputStream, Exception paramException);
  
  public abstract Exception readException(ApplicationException paramApplicationException);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\ExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */