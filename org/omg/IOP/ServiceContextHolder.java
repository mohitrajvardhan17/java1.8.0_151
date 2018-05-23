package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextHolder
  implements Streamable
{
  public ServiceContext value = null;
  
  public ServiceContextHolder() {}
  
  public ServiceContextHolder(ServiceContext paramServiceContext)
  {
    value = paramServiceContext;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServiceContextHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServiceContextHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServiceContextHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\ServiceContextHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */