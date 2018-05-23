package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextListHolder
  implements Streamable
{
  public ServiceContext[] value = null;
  
  public ServiceContextListHolder() {}
  
  public ServiceContextListHolder(ServiceContext[] paramArrayOfServiceContext)
  {
    value = paramArrayOfServiceContext;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServiceContextListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServiceContextListHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServiceContextListHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\ServiceContextListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */