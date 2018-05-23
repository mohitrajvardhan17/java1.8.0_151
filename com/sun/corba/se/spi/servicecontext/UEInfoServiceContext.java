package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class UEInfoServiceContext
  extends ServiceContext
{
  public static final int SERVICE_CONTEXT_ID = 9;
  private Throwable unknown = null;
  
  public UEInfoServiceContext(Throwable paramThrowable)
  {
    unknown = paramThrowable;
  }
  
  public UEInfoServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
  {
    super(paramInputStream, paramGIOPVersion);
    try
    {
      unknown = ((Throwable)in.read_value());
    }
    catch (ThreadDeath localThreadDeath)
    {
      throw localThreadDeath;
    }
    catch (Throwable localThrowable)
    {
      unknown = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
    }
  }
  
  public int getId()
  {
    return 9;
  }
  
  public void writeData(OutputStream paramOutputStream)
    throws SystemException
  {
    paramOutputStream.write_value(unknown);
  }
  
  public Throwable getUE()
  {
    return unknown;
  }
  
  public String toString()
  {
    return "UEInfoServiceContext[ unknown=" + unknown.toString() + " ]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\UEInfoServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */