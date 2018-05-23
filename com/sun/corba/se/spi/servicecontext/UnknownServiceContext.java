package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class UnknownServiceContext
  extends ServiceContext
{
  private int id = -1;
  private byte[] data = null;
  
  public UnknownServiceContext(int paramInt, byte[] paramArrayOfByte)
  {
    id = paramInt;
    data = paramArrayOfByte;
  }
  
  public UnknownServiceContext(int paramInt, InputStream paramInputStream)
  {
    id = paramInt;
    int i = paramInputStream.read_long();
    data = new byte[i];
    paramInputStream.read_octet_array(data, 0, i);
  }
  
  public int getId()
  {
    return id;
  }
  
  public void writeData(OutputStream paramOutputStream)
    throws SystemException
  {}
  
  public void write(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
    throws SystemException
  {
    paramOutputStream.write_long(id);
    paramOutputStream.write_long(data.length);
    paramOutputStream.write_octet_array(data, 0, data.length);
  }
  
  public byte[] getData()
  {
    return data;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\UnknownServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */