package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class MaxStreamFormatVersionServiceContext
  extends ServiceContext
{
  private byte maxStreamFormatVersion;
  public static final MaxStreamFormatVersionServiceContext singleton = new MaxStreamFormatVersionServiceContext();
  public static final int SERVICE_CONTEXT_ID = 17;
  
  public MaxStreamFormatVersionServiceContext()
  {
    maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
  }
  
  public MaxStreamFormatVersionServiceContext(byte paramByte)
  {
    maxStreamFormatVersion = paramByte;
  }
  
  public MaxStreamFormatVersionServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
  {
    super(paramInputStream, paramGIOPVersion);
    maxStreamFormatVersion = paramInputStream.read_octet();
  }
  
  public int getId()
  {
    return 17;
  }
  
  public void writeData(OutputStream paramOutputStream)
    throws SystemException
  {
    paramOutputStream.write_octet(maxStreamFormatVersion);
  }
  
  public byte getMaximumStreamFormatVersion()
  {
    return maxStreamFormatVersion;
  }
  
  public String toString()
  {
    return "MaxStreamFormatVersionServiceContext[" + maxStreamFormatVersion + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\MaxStreamFormatVersionServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */