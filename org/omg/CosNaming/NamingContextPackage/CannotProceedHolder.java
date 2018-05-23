package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CannotProceedHolder
  implements Streamable
{
  public CannotProceed value = null;
  
  public CannotProceedHolder() {}
  
  public CannotProceedHolder(CannotProceed paramCannotProceed)
  {
    value = paramCannotProceed;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = CannotProceedHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    CannotProceedHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return CannotProceedHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\CannotProceedHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */