package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextHolder
  implements Streamable
{
  public NamingContext value = null;
  
  public NamingContextHolder() {}
  
  public NamingContextHolder(NamingContext paramNamingContext)
  {
    value = paramNamingContext;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NamingContextHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NamingContextHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NamingContextHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */