package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidNameHolder
  implements Streamable
{
  public InvalidName value = null;
  
  public InvalidNameHolder() {}
  
  public InvalidNameHolder(InvalidName paramInvalidName)
  {
    value = paramInvalidName;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = InvalidNameHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    InvalidNameHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return InvalidNameHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\InvalidNameHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */