package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public abstract class ServiceContext
{
  protected InputStream in = null;
  
  protected ServiceContext() {}
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint(this, paramString);
  }
  
  protected ServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
    throws SystemException
  {
    in = paramInputStream;
  }
  
  public abstract int getId();
  
  public void write(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
    throws SystemException
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramOutputStream.orb(), paramGIOPVersion);
    localEncapsOutputStream.putEndian();
    writeData(localEncapsOutputStream);
    byte[] arrayOfByte = localEncapsOutputStream.toByteArray();
    paramOutputStream.write_long(getId());
    paramOutputStream.write_long(arrayOfByte.length);
    paramOutputStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
  }
  
  protected abstract void writeData(OutputStream paramOutputStream);
  
  public String toString()
  {
    return "ServiceContext[ id=" + getId() + " ]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */