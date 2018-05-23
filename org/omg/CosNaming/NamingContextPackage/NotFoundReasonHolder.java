package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundReasonHolder
  implements Streamable
{
  public NotFoundReason value = null;
  
  public NotFoundReasonHolder() {}
  
  public NotFoundReasonHolder(NotFoundReason paramNotFoundReason)
  {
    value = paramNotFoundReason;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NotFoundReasonHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NotFoundReasonHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NotFoundReasonHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundReasonHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */