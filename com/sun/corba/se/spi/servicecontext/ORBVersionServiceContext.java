package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBVersionServiceContext
  extends ServiceContext
{
  public static final int SERVICE_CONTEXT_ID = 1313165056;
  private ORBVersion version = ORBVersionFactory.getORBVersion();
  
  public ORBVersionServiceContext()
  {
    version = ORBVersionFactory.getORBVersion();
  }
  
  public ORBVersionServiceContext(ORBVersion paramORBVersion)
  {
    version = paramORBVersion;
  }
  
  public ORBVersionServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
  {
    super(paramInputStream, paramGIOPVersion);
    version = ORBVersionFactory.create(in);
  }
  
  public int getId()
  {
    return 1313165056;
  }
  
  public void writeData(OutputStream paramOutputStream)
    throws SystemException
  {
    version.write(paramOutputStream);
  }
  
  public ORBVersion getVersion()
  {
    return version;
  }
  
  public String toString()
  {
    return "ORBVersionServiceContext[ version=" + version + " ]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\ORBVersionServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */