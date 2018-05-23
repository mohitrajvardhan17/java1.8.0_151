package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotEmptyHolder
  implements Streamable
{
  public NotEmpty value = null;
  
  public NotEmptyHolder() {}
  
  public NotEmptyHolder(NotEmpty paramNotEmpty)
  {
    value = paramNotEmpty;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NotEmptyHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NotEmptyHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NotEmptyHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotEmptyHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */