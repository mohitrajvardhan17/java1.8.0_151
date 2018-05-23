package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundHolder
  implements Streamable
{
  public NotFound value = null;
  
  public NotFoundHolder() {}
  
  public NotFoundHolder(NotFound paramNotFound)
  {
    value = paramNotFound;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NotFoundHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NotFoundHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NotFoundHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */