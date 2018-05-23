package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidAddressHolder
  implements Streamable
{
  public InvalidAddress value = null;
  
  public InvalidAddressHolder() {}
  
  public InvalidAddressHolder(InvalidAddress paramInvalidAddress)
  {
    value = paramInvalidAddress;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = InvalidAddressHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    InvalidAddressHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return InvalidAddressHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextExtPackage\InvalidAddressHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */