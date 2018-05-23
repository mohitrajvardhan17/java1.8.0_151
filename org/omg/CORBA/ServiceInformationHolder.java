package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceInformationHolder
  implements Streamable
{
  public ServiceInformation value;
  
  public ServiceInformationHolder()
  {
    this(null);
  }
  
  public ServiceInformationHolder(ServiceInformation paramServiceInformation)
  {
    value = paramServiceInformation;
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServiceInformationHelper.write(paramOutputStream, value);
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServiceInformationHelper.read(paramInputStream);
  }
  
  public TypeCode _type()
  {
    return ServiceInformationHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ServiceInformationHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */