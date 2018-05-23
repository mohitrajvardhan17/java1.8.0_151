package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameComponentHolder
  implements Streamable
{
  public NameComponent value = null;
  
  public NameComponentHolder() {}
  
  public NameComponentHolder(NameComponent paramNameComponent)
  {
    value = paramNameComponent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NameComponentHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NameComponentHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NameComponentHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NameComponentHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */