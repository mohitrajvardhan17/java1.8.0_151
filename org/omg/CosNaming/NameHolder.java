package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameHolder
  implements Streamable
{
  public NameComponent[] value = null;
  
  public NameHolder() {}
  
  public NameHolder(NameComponent[] paramArrayOfNameComponent)
  {
    value = paramArrayOfNameComponent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NameHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NameHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NameHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NameHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */