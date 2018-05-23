package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AlreadyBoundHolder
  implements Streamable
{
  public AlreadyBound value = null;
  
  public AlreadyBoundHolder() {}
  
  public AlreadyBoundHolder(AlreadyBound paramAlreadyBound)
  {
    value = paramAlreadyBound;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = AlreadyBoundHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    AlreadyBoundHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return AlreadyBoundHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\AlreadyBoundHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */