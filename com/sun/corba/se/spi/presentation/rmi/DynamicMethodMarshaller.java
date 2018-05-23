package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract interface DynamicMethodMarshaller
{
  public abstract Method getMethod();
  
  public abstract Object[] copyArguments(Object[] paramArrayOfObject, ORB paramORB)
    throws RemoteException;
  
  public abstract Object[] readArguments(InputStream paramInputStream);
  
  public abstract void writeArguments(OutputStream paramOutputStream, Object[] paramArrayOfObject);
  
  public abstract Object copyResult(Object paramObject, ORB paramORB)
    throws RemoteException;
  
  public abstract Object readResult(InputStream paramInputStream);
  
  public abstract void writeResult(OutputStream paramOutputStream, Object paramObject);
  
  public abstract boolean isDeclaredException(Throwable paramThrowable);
  
  public abstract void writeException(OutputStream paramOutputStream, Exception paramException);
  
  public abstract Exception readException(ApplicationException paramApplicationException);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\presentation\rmi\DynamicMethodMarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */