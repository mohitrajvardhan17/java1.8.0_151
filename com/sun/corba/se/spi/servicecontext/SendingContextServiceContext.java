package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class SendingContextServiceContext
  extends ServiceContext
{
  public static final int SERVICE_CONTEXT_ID = 6;
  private IOR ior = null;
  
  public SendingContextServiceContext(IOR paramIOR)
  {
    ior = paramIOR;
  }
  
  public SendingContextServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
  {
    super(paramInputStream, paramGIOPVersion);
    ior = new IORImpl(in);
  }
  
  public int getId()
  {
    return 6;
  }
  
  public void writeData(OutputStream paramOutputStream)
    throws SystemException
  {
    ior.write(paramOutputStream);
  }
  
  public IOR getIOR()
  {
    return ior;
  }
  
  public String toString()
  {
    return "SendingContexServiceContext[ ior=" + ior + " ]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\SendingContextServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */